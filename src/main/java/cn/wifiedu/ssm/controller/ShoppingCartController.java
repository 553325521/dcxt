package cn.wifiedu.ssm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

@Controller
@Scope("prototype")
public class ShoppingCartController extends BaseController {

	private static Logger logger = Logger.getLogger(GoodsController.class);
	
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
	
	@RequestMapping(value = "/ShoppingCart_insert_insertCart", method = RequestMethod.POST)
	public void loadGoodsTypeByShopId() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("openid").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("FK_SHOP") || StringUtils.isBlank(map.get("FK_SHOP").toString())) {
					output("9999", "商铺参数无效");
					return;
				}
				if (!map.containsKey("FK_USER") || StringUtils.isBlank(map.get("FK_USER").toString())) {
					output("9999", "USER参数无效");
					return;
				}
				map.put("sqlMapId","insertShoppingCart");
				openService.insert(map);
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/ShoppingCart_load_loadCartDataByUser", method = RequestMethod.POST)
	public void loadCartDataByUser() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("openid").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("FK_SHOP") || StringUtils.isBlank(map.get("FK_SHOP").toString())) {
					output("9999", "商铺参数无效");
					return;
				}
				if (!map.containsKey("FK_USER") || StringUtils.isBlank(map.get("FK_USER").toString())) {
					output("9999", "USER参数无效");
					return;
				}
				if (!map.containsKey("CART_STATE") || StringUtils.isBlank(map.get("CART_STATE").toString())) {
					output("9999", "状态参数无效");
					return;
				}
				map.put("sqlMapId","selectCartDataByUser");
				List<Map<String,Object>> cartDataList = openService.queryForList(map);
				output("0000", cartDataList);
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
