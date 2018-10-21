package cn.wifiedu.ssm.controller;

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

	/**
	 * @author kqs
	 * @return void
	 * @date 2018年10月14日 - 下午8:50:47
	 * @description:购物车添加
	 */
	@RequestMapping(value = "/ShoppingCart_insert_insertCart", method = RequestMethod.POST)
	public void insertCart() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("FK_USER").toString());
			if (StringUtils.isNotBlank(userJson)) {
				// 判断是否已加入购物车
				// 若加入就加数量
				if (!map.containsKey("FK_SHOP") || StringUtils.isBlank(map.get("FK_SHOP").toString())) {
					output("9999", "商铺参数无效");
					return;
				}
				if (!map.containsKey("FK_USER") || StringUtils.isBlank(map.get("FK_USER").toString())) {
					output("9999", "USER参数无效");
					return;
				}
				if ((map.get("GOODS_TASTE").toString()).equals("[]")
						|| StringUtils.isBlank(map.get("GOODS_TASTE").toString())) {
					map.put("GOODS_TASTE", null);
				}
				if ((map.get("GOODS_RECIPE").toString()).equals("[]")
						|| StringUtils.isBlank(map.get("GOODS_RECIPE").toString())) {
					map.put("GOODS_RECIPE", null);
				}
				if ((map.get("GOODS_SPECIFICATION").toString()).equals("[]")
						|| StringUtils.isBlank(map.get("GOODS_SPECIFICATION").toString())) {
					map.put("GOODS_SPECIFICATION", null);
				}
				if (!checkIsExist(map)) {
					map.put("INSERT_BY", map.get("FK_USER").toString());
					map.put("sqlMapId", "insertShoppingCart");
					String result = openService.insert(map);
					if (StringUtils.isNotBlank(result)) {
						output("0000", "ok");
						return;
					} else {
						output("9999", "token无效");
						return;
					}
				} else {
					output("0000", "ok");
					return;
				}
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
		output("9999", "token无效");
		return;
	}

	/**
	 * @author kqs
	 * @param map
	 * @return
	 * @return boolean
	 * @date 2018年10月14日 - 下午8:50:47
	 * @description:查询是否已存在购物车
	 */
	private boolean checkIsExist(Map<String, Object> map) {
		try {
			map.put("sqlMapId", "checkIsExistShopCart");
			Map<String, Object> resMap = (Map<String, Object>) openService.queryForObject(map);
			if (resMap != null && resMap.containsKey("GOODS_COUNT")) {
				if (Integer.valueOf(resMap.get("GOODS_COUNT").toString()) > 0) {
					// 已存在 数量增加
					map.put("GOODS_NUMBER", Integer.valueOf(resMap.get("GOODS_NUMBER").toString()) + 1);
					map.put("sqlMapId", "updateGoodsNum");
					if (openService.update(map)) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			logger.error("error", e);
		}
		return false;
	}

	/**
	 * 
	 * @author kqs
	 * @return void
	 * @date 2018年10月14日 - 下午9:40:40
	 * @description:购物车减少数量
	 */
	@RequestMapping(value = "/ShoppingCart_update_removeCart", method = RequestMethod.POST)
	public void removeCart() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "checkIsExistShopCart");
			Map<String, Object> resMap = (Map<String, Object>) openService.queryForObject(map);
			if (resMap != null && resMap.containsKey("GOODS_COUNT")) {
				if (Integer.valueOf(resMap.get("GOODS_COUNT").toString()) > 0) {
					// 已存在 数量减少
					map.put("GOODS_NUMBER", Integer.valueOf(resMap.get("GOODS_NUMBER").toString()) - 1);
					if ((Integer.valueOf(resMap.get("GOODS_NUMBER").toString()) - 1) == 0) {
						map.put("sqlMapId", "deleteGoodsForCart");
						if (openService.delete(map)) {
							output("0000", "ok");
							return;
						}
					} else {
						map.put("sqlMapId", "updateGoodsNum");
						if (openService.update(map)) {
							output("0000", "ok");
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
		output("9999", "操作失败");
		return;
	}

	/**
	 * 
	 * @author kqs
	 * @return void
	 * @date 2018年10月20日 - 下午11:18:25
	 * @description:清空购物车
	 */
	@RequestMapping(value = "/ShoppingCart_update_removeAllCart", method = RequestMethod.POST)
	public void removeAllCart() {
		try {
			Map<String, Object> map = getParameterMap();
			if (StringUtils.isBlank(map.get("shopid").toString())) {
				output("9999", "商铺参数无效");
				return;
			}
			if (StringUtils.isBlank(map.get("openid").toString())) {
				output("9999", "USER参数无效");
				return;
			}
			map.put("sqlMapId", "removeAllCart");
			if (openService.delete(map)) {
				output("0000", "操作成功~");
				return;
			}
			output("9999", "操作失败~");
			return;
		} catch (Exception e) {
			logger.error("error", e);
		}
		output("9999", "操作失败");
		return;
	}
	
	@RequestMapping(value = "/ShoppingCart_load_loadCartDataByUser", method = RequestMethod.POST)
	public void loadCartDataByUser() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("FK_USER").toString());
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
				map.put("sqlMapId", "selectCartDataByUser");
				List<Map<String, Object>> cartDataList = openService.queryForList(map);
				output("0000", cartDataList);
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
	}

}
