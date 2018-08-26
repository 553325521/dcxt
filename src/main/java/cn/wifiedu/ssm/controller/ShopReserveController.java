package cn.wifiedu.ssm.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * @author kqs
 * @time 2018年8月26日 - 上午10:29:48
 * @description:饭店预订支付设置
 */
@Controller
@Scope("prototype")
public class ShopReserveController extends BaseController {

	private static Logger logger = Logger.getLogger(ShopReserveController.class);

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
	 * 
	 * @author kqs
	 * @return void
	 * @date 2018年8月26日 - 上午11:46:54
	 * @description:更新/插入店铺对应的预订支付设置
	 */
	@RequestMapping("/Shop_update_updateShopReserve")
	public void updateShopReserve() {
		try {
			Map<String, Object> map = getParameterMap();

			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSONObject.parseObject(userJson);
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			if (map.containsKey("SHOP_RESERVE_PK")) {
				map.put("UPDATE_TIME", StringDeal.getStringDate());
				map.put("sqlMapId", "updateShopReserve");
				if (openService.update(map)) {
					output("0000", "操作成功!");
					return;
				}
			} else {
				map.put("INSERT_TIME", StringDeal.getStringDate());
				map.put("sqlMapId", "insertShopReserve");
				String res = openService.insert(map);
				if (res != null) {
					output("0000", "操作成功!");
					return;
				}
			}
			output("9999", "操作失败!");
			return;
		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	/**
	 * 
	 * @author kqs
	 * @return void
	 * @date 2018年8月26日 - 上午11:52:59 
	 * @description:查询商铺对应的预支付设置
	 */
	@RequestMapping("/Shop_queryForObject_loadShopReserveByShopId")
	public void loadShopReserveByShopId() {
		try {
			Map<String, Object> map = getParameterMap();

			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSONObject.parseObject(userJson);
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "loadShopReserveByShopId");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			if (reMap != null) {
				output("0000", reMap);
				return;
			}
			output("9999", "操作失败!");
			return;
		} catch (Exception e) {
			logger.error("error", e);
		}
	}
}
