package cn.wifiedu.ssm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.chainsaw.Main;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * 提现与数据库交互
 * @author wangjinglong
 *
 */
@Controller
@Scope("prototype")
public class CashController extends BaseController {

	@Resource
	OpenService openService;

	
	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}
	
	@Resource
	PlatformTransactionManager transactionManager;
	
	@Resource
	private JedisClient jedisClient;
	
	/**
	 * 根据user_pk 查询用户信息
	 * @author wangjinglong
	 */
	@RequestMapping(value="/Cash__select_agentInfo",method = RequestMethod.POST)
	public void showAgentShopInfo() {
		String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
		String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
		JSONObject userObj = JSONObject.parseObject(userJson);
		try {
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("USER_PK",userObj.get("USER_PK"));
			map.put("sqlMapId", "selectUserByPrimaryKey");
			Map<String, Object> reMap  = (Map<String, Object>)openService.queryForObject(map);
			output("0000",reMap);
		} catch (Exception e) {
			e.printStackTrace();
			output("9999","查询失败");
		}
	}
	/**
	 * 代理商提现申请实现
	 * @author wangjinglong
	 */
	@RequestMapping(value="/Cash_update_updateCash",method = RequestMethod.POST)
	public void Cash_update_updateCash() {
		DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
	    defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	    TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);
		String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
		String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
		JSONObject userObj = JSONObject.parseObject(userJson);
	    try {
			Map<String, Object> map = getParameterMap();
			map.put("USER_FK", userObj.get("USER_PK"));
			map.put("CASH_WAY", "微信提现");
			map.put("CASH_STATE", "0");
			map.put("CASH_YEARMONTH", StringDeal.getNowYearMonthStr());
			map.put("CASH_TIME", StringDeal.getStringDate());
			map.put("sqlMapId", "insertCashRecord");
			String insertResult = openService.insert(map);
			if(insertResult == null || insertResult.equals("")){
				throw new Exception();
			}else{
				map.put("USER_PK", userObj.get("USER_PK"));
				map.put("sqlMapId", "selectUserByPrimaryKey");
				Map<String, Object> reMap  = (Map<String, Object>)openService.queryForObject(map);
				double nowMoney = Double.parseDouble(reMap.get("USER_BALANCE").toString());
				double cashMoney = Double.parseDouble(map.get("CASH_MONEY").toString());
				map.put("USER_BALANCE", nowMoney-cashMoney);
				map.put("sqlMapId", "updateUserBalance");
				boolean updateResult = openService.update(map);
				if(updateResult){
					transactionManager.commit(status);
					output("0000","提现申请成功");
				}else{
					throw new Exception();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			transactionManager.rollback(status);
			e.printStackTrace();
			output("9999","提现申请失败");
		}
	}
	/**
	 * 根据user_pk 查询用户提现记录
	 * @author wangjinglong
	 */
	@RequestMapping(value="/Cash__select_cashRecord",method = RequestMethod.POST)
	public void Cash__select_cashRecord() {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSONObject.parseObject(userJson);
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("USER_PK", userObj.get("USER_PK"));
			map.put("sqlMapId", "selectCashMoneyByUserPK");
			List<Map<String, Object>> finalList = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> reList = openService.queryForList(map);
			for(int i = 0;i< reList.size();i++){
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("yearMonth", reList.get(i).get("CASH_YEARMONTH"));
				map1.put("monthMoney", reList.get(i).get("MONTHMONEY"));
				map.put("CASH_YEARMONTH", reList.get(i).get("CASH_YEARMONTH"));
				map.put("sqlMapId", "selectCashRecordByUserPKAndTime");
				List<Map<String, Object>> recordList = openService.queryForList(map);
				for(int j = 0;j < recordList.size(); j++){
					String recordTime = recordList.get(j).get("CASH_TIME").toString();
					recordList.get(j).put("time", dateConvert(recordTime));
					recordList.get(j).put("monthDay", dateConvert1(recordTime));
				}
				map1.put("recordList", recordList);
				finalList.add(map1);
			}
			output("0000",finalList);
		} catch (Exception e) {
			e.printStackTrace();
			output("9999","查询失败");
		}
	}
	public  String dateConvert(String dateStr){
		return dateStr.substring(11,16);
	}
	public static String dateConvert1(String dateStr){
		return dateStr.substring(5,10);
	}
	
	
}
