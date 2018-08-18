package cn.wifiedu.ssm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

@Controller
@Scope("prototype")
public class ShopInfoController extends BaseController {

	private static Logger logger = Logger.getLogger(FunctionController.class);

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
	 * 获取用户信息
	 * @throws Exception
	 */
	@RequestMapping("/ShopInfo_getUserInfo_data")
	public void getUserInfo() throws Exception {
		Map<String, Object> map = getParameterMap();
		
		//String token = "o40NVwcZRjgFCE5GSb9JKb6luzb4";
		String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
		String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
		JSONObject userObj = JSONObject.parseObject(userJson);
		
		map.put("userId", userObj.get("USER_PK"));
		map.put("shopId", userObj.get("FK_SHOP"));
		
		output(map);
	}
	
	
	
	@RequestMapping("/ShopInfo_editYouhuimaidan_data")
	public void editYouhuimaidan() throws Exception {
		Map<String, Object> map = getParameterMap();
		
		String rulePk = map.get("rulePk").toString();
		
		//String token = "o40NVwcZRjgFCE5GSb9JKb6luzb4";
		String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
		String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
		JSONObject userObj = JSONObject.parseObject(userJson);
		
		map.put("SHOP_ID", userObj.get("FK_SHOP"));
		map.put("USER_ID", userObj.get("USER_PK")); 
		map.put("ROLE_ID", userObj.get("FK_ROLE")); 
		
		if(map.get("rule_model").toString().equals("1")) {
			map.put("rule_model_first", map.get("yh_zkxf").toString());
			map.put("rule_model_second", map.get("yh_zkyh").toString());
		}else if(map.get("rule_model").toString().equals("2")) {
			map.put("rule_model_first", map.get("yh_gdxf").toString());
			map.put("rule_model_second", map.get("yh_gdj").toString());
		}else if(map.get("rule_model").toString().equals("3")) {
			map.put("rule_model_first", map.get("yh_sjxf").toString());
			map.put("rule_model_second", map.get("yh_sjj").toString());
		}
		
		map.put("sqlMapId", "editYouhuimaidan");
		openService.update(map);
		
		map.put("fk_preferential_rule", rulePk);
		
		map.put("sqlMapId", "deleteAllRuleGood");
		if(openService.update(map)) {
			List<Map<String, Object>> list = toListMap(map.get("goodType").toString());
			
			for(int i=0; i<list.size(); i++) {
				map.put("sqlMapId", "saveYouhuimaidanGood");
				map.put("fk_goodtype", list.get(i).get("GTYPE_PK").toString());
				openService.insert(map);
			}
		}
		output("success");
	}
	
	
	/**
	 * 优惠买单设置
	 * @throws Exception 
	 */
	@RequestMapping("/ShopInfo_saveYouhuimaidan_data")
	public void saveYouhuimaidan() throws Exception {
		Map<String, Object> map = getParameterMap();
		
		//String token = "o40NVwcZRjgFCE5GSb9JKb6luzb4";
		String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
		String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
		JSONObject userObj = JSONObject.parseObject(userJson);
		
		map.put("SHOP_ID", userObj.get("FK_SHOP"));
		map.put("USER_ID", userObj.get("USER_PK")); 
		map.put("ROLE_ID", userObj.get("FK_ROLE")); 
		
		if(map.get("rule_model").toString().equals("1")) {
			map.put("rule_model_first", map.get("yh_zkxf").toString());
			map.put("rule_model_second", map.get("yh_zkyh").toString());
		}else if(map.get("rule_model").toString().equals("2")) {
			map.put("rule_model_first", map.get("yh_gdxf").toString());
			map.put("rule_model_second", map.get("yh_gdj").toString());
		}else if(map.get("rule_model").toString().equals("3")) {
			map.put("rule_model_first", map.get("yh_sjxf").toString());
			map.put("rule_model_second", map.get("yh_sjj").toString());
		}
		
		map.put("sqlMapId", "saveYouhuimaidan");
		String rulePk = openService.insert(map);
		
		map.put("fk_preferential_rule", rulePk);
		
		List<Map<String, Object>> list = toListMap(map.get("goodType").toString());
		
		for(int i=0; i<list.size(); i++) {
			map.put("sqlMapId", "saveYouhuimaidanGood");
			map.put("fk_goodtype", list.get(i).get("GTYPE_PK").toString());
			openService.insert(map);
		}
		
		output("success");
	}
	
	
	/**
	 * 获取当前用户商铺信息
	 * @param request
	 * @param session
	 */
	@RequestMapping("/ShopInfo_getUserShopInfo_data")
	public void getUserShopInfo(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();

			//String token = "o40NVwcZRjgFCE5GSb9JKb6luzb4";
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSONObject.parseObject(userJson);
			
			map.put("SHOP_ID", userObj.get("FK_SHOP"));
			map.put("USER_ID", userObj.get("USER_PK")); 
			map.put("ROLE_ID", userObj.get("FK_ROLE")); 
			
			map.put("sqlMapId", "getShopInfo");
			List<Map<String, Object>> shopInfoList = openService.queryForList(map);
			shopInfoList.get(0).put("userPk", userObj.get("USER_PK"));
			
			output(shopInfoList);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	
	public static List<Map<String, Object>> toListMap(String json){
    	List<Object> list =JSON.parseArray(json);
    	
    	List< Map<String,Object>> listw = new ArrayList<Map<String,Object>>();
    	for (Object object : list){
    	Map<String,Object> ageMap = new HashMap<String,Object>();
    	Map <String,Object> ret = (Map<String, Object>) object;//取出list里面的值转为map
    	
    	listw.add(ret);
    	}
		return listw;
    	
    }
}
