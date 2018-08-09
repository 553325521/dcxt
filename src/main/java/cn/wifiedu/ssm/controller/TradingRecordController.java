package cn.wifiedu.ssm.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.WxUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;


/**
 * 交易记录与数据库交互
 * @author wangjinglong
 *
 */

@Controller
@Scope("prototype")
public class TradingRecordController extends BaseController {

	@Resource
	OpenService openService;
	
	@Resource
	private JedisClient jedisClient;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	/**
	 * 显示当前登录代理商下的交易记录
	 * @author wangjinglong
	 */
	@RequestMapping(value="/TradingRecord_select_fingTradingRecord",method = RequestMethod.POST)
	public void fingTradingRecord() {
		try {
			Map<String, Object> map = getParameterMap();
			
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSONObject.parseObject(userJson);
			
			map.put("USER_WX", userObj.get("USER_PK")); 
			map.put("sqlMapId", "fingTradingRecord");
			List<Map<String, Object>> reMap  = openService.queryForList(map);
			output("0000",reMap);
		} catch (Exception e) {
			e.printStackTrace();
			output("9999","查询失败");
		}
	}
	
	
	/**
	 * 
	 * @date 2018年7月30日 下午9:09:58 
	 * @author lps
	 * 
	 * @Description: 根据代理商id查询提成记录
	 * @param request
	 * @param seesion 
	 * @return void 
	 *
	 */
	@RequestMapping("/TradingRecord_query_findCommissionRecordList")
	public void findCommissionRecordList(HttpServletRequest request,HttpSession seesion){
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSONObject.parseObject(userJson);
			
			map.put("USER_ID", userObj.get("USER_PK"));
			map.put("sqlMapId", "findCommissionRecordList");
			List<Map<String, Object>> reMap  = openService.queryForList(map);
			output("0000",reMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			output("9999","查询失败");
		}
		
	}
}
