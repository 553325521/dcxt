package cn.wifiedu.ssm.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
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

}
