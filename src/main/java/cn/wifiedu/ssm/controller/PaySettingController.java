package cn.wifiedu.ssm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;


/**
* <p>Title: PaySettingController</p>
* <p>Description:支付设置 </p>
* @author    wangjinglong
* @date       2018年8月21日
*/
@Controller
@Scope("prototype")
public class PaySettingController extends BaseController {

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	@Resource
	private JedisClient jedisClient;
	
	
	/**
	* <p>Title: selectByShopFK</p>
	* <p>Description: 根据shopID查询支付设置信息</p>
	* @param requestn
	*/
	@RequestMapping("/PaySetting_select_selectByShopFK")
	public void selectByShopFK(HttpServletRequest requestn){
		String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
		String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
		JSONObject userObj = JSONObject.parseObject(userJson);
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("SHOP_FK", userObj.get("FK_SHOP"));
			map.put("sqlMapId", "selectByShopFK");
			List<Map<String, Object>> reMap = openService.queryForList(map);
			if(reMap.size() != 0){
				output("0000", reMap);
			}else{
				output("0000", 0);
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
			return;
		}
	}
	
	/**
	* <p>Title: insertRecord</p>
	* <p>Description: 插入支付设置</p>
	* @param request
	*/
	@RequestMapping("/PaySetting_insert_insertRecord")
	public void insertRecord(HttpServletRequest request){
		String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
		String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
		JSONObject userObj = JSONObject.parseObject(userJson);
		try {
			Map<String, Object> map = getParameterMap();
			map.put("SHOP_FK", userObj.get("FK_SHOP"));
			map.put("CREATE_BY", userObj.get("USER_NAME"));
			map.put("sqlMapId", "insertRecord");
			String reStr = openService.insert(map);
			if(reStr!=null){
				output("0000", "保存成功");
			}else{
				output("9999", "保存失败");
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
			return;
		}
	}
	
	@RequestMapping("/PaySetting_update_updateRecord")
	public void updateRecord(HttpServletRequest request){
		String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
		String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
		JSONObject userObj = JSONObject.parseObject(userJson);
		try {
			Map<String, Object> map = getParameterMap();
			map.put("UPDATE_BY", userObj.get("USER_NAME"));
			map.put("sqlMapId", "updateRecord");
			boolean result = openService.update(map);
			if(result){
				output("0000", "保存成功");
			}else{
				output("9999", "保存失败");
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
			return;
		}
	}
	
}
