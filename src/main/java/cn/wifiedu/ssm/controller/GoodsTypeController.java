package cn.wifiedu.ssm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.Arith;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * @author wangjinglong 商品类别与数据库交互
 */
@Controller
@Scope("prototype")
public class GoodsTypeController extends BaseController {

	@Resource
	OpenService openService;

	@Resource
	PlatformTransactionManager transactionManager;

	@Resource
	private JedisClient jedisClient;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	/**
	 * wjl 获取商品类别的序号
	 */
	@RequestMapping(value = "/GoodsType_select_loadGoodsTypeOrder", method = RequestMethod.POST)
	public void GoodsType_select_loadGoodsTypeOrder() {
		// 如果未认证，跳转完善信息界面
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			map.put("SHOP_FK", userObj.get("FK_SHOP"));
			map.put("sqlMapId", "loadGoodsTypeOrder");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			output("0000", reMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * wjl 添加商品类别
	 */
	@RequestMapping(value = "/GoodsType_insert_insertGoodsType", method = RequestMethod.POST)
	public void GoodsType_insert_insertGoodsType() {
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			map.put("SHOP_FK", userObj.get("FK_SHOP"));
			map.put("sqlMapId", "loadGoodsTypeOrder");
			int maxOrder = 0;
			Map<String, Object> reMapOrder = (Map<String, Object>) openService.queryForObject(map);
			if (reMapOrder != null) {
				maxOrder = Integer.parseInt(reMapOrder.get("GTYPE_ORDER").toString());
			}
			int userSROrder = Integer.parseInt(map.get("GTYPE_ORDER").toString());
			if (userSROrder <= 0 || userSROrder > maxOrder + 1) {
				output("5555", "输入的序号不合法");
			} else {
				if (userSROrder < maxOrder + 1) {
					map.put("PID", map.get("GTYPE_PID"));
					map.put("GTYPE_OLD_ORDER", maxOrder + 1);
					map.put("sqlMapId", "updateGoodsTypeOrderChangeSmall");
					openService.update(map);
				}
				map.put("GTYPE_ATTACH", 0);
				map.put("CREATE_BY", userObj.get("USER_NAME"));
				map.put("sqlMapId", "insertGoodsType");
				String resultStr = openService.insert(map);
				if (resultStr != null) {
					if (map.get("GTYPE_PID").toString().equals("0")) {
						map.put("GTYPE_PATH", 0 + "/" + resultStr);
					} else {
						map.put("GTYPE_PID", map.get("GTYPE_PID"));
						map.put("sqlMapId", "selectGoodsTypePNameByPID");
						Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
						map.put("GTYPE_PATH", reMap.get("GTYPE_PATH") + "/" + resultStr);
					}
					map.put("GTYPE_PK", resultStr);
					map.put("sqlMapId", "updateGoodsTypePath");
					boolean updateResult = openService.update(map);
					if (updateResult) {
						output("0000", "保存成功");
					} else {
						output("9999", "保存失败");
					}
				} else {
					output("9999", "保存失败");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * wjl 显示商品类别
	 */
	@RequestMapping(value = "/GoodsType_select_loadGoodsTypeListByPID", method = RequestMethod.POST)
	public void loadGoodsTypeListByPID() {
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			map.put("SHOP_FK", userObj.get("FK_SHOP"));
			map.put("sqlMapId", "loadGoodsTypeListByPID");
			List<Map<String, Object>> reList = openService.queryForList(map);
			for (int i = 0; i < reList.size(); i++) {
				String goodsTypeArea_JsonStr = reList.get(i).get("GTYPE_AREA").toString();
				JSONArray goodsTypeArea_JsonArray = JSON.parseArray(goodsTypeArea_JsonStr);
				String goodsTypeArea = "";
				for (int j = 0; j < goodsTypeArea_JsonArray.size(); j++) {
					JSONObject jsonObject = goodsTypeArea_JsonArray.getJSONObject(j);
					if (jsonObject.getBooleanValue("checked")) {
						goodsTypeArea += jsonObject.getString("name") + "/";
					}
				}
				if (goodsTypeArea.lastIndexOf("/") == goodsTypeArea.length() - 1) {
					goodsTypeArea = goodsTypeArea.substring(0, goodsTypeArea.length() - 1);
				}
				reList.get(i).put("GTYPE_AREA", goodsTypeArea);
			}
			if (reList != null && reList.size() != 0) {
				output("0000", reList);
			} else {
				output("9999", "查询失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * wjl 根据PID查询下一级商品类别的数量和商品的数量
	 */
	@RequestMapping(value = "/GoodsType_select_selectLastRecordCountByPID", method = RequestMethod.POST)
	public void selectLastRecordCountByPID() {
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			map.put("SHOP_FK", userObj.get("FK_SHOP"));
			map.put("sqlMapId", "loadGoodsTypeListByPID");
			List<Map<String, Object>> reList = openService.queryForList(map);
			map.put("sqlMapId", "selectGoodsByGoodsType");
			List<Map<String, Object>> goodsList = openService.queryForList(map);
			if (reList.size() == 0 && goodsList.size() == 0) {
				output("0000", "00");
			} else if (reList.size() == 0 && goodsList.size() != 0) {
				output("0000", "01");
			} else if (reList.size() != 0 && goodsList.size() == 0) {
				output("0000", "10");
			} else {
				output("0000", "11");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * wjl 根据商品分类ID查询商品分类名称
	 */
	@RequestMapping(value = "/GoodsType_select_selectGoodsTypePNameByPID", method = RequestMethod.POST)
	public void selectGoodsTypePNameByPID() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectGoodsTypePNameByPID");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			output("0000", reMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * wjl 查询商品分类编辑时候的父分类(查询跟父分类同级且没有商品的分类)
	 */
	@RequestMapping(value = "/GoodsType_select_selectGTypeNameButNoGoodsByPID", method = RequestMethod.POST)
	public void selectGTypeNameButNoGoodsByPID() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectGTypeNameButNoGoodsByPID");
			List<Map<String, Object>> reList = openService.queryForList(map);
			output("0000", reList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * wjl 查询商品分类编辑时候的父分类(查询跟父分类同级且没有商品的分类)
	 */
	@RequestMapping(value = "/GoodsType_select_selectGTypeOrderByPID", method = RequestMethod.POST)
	public void selectGTypeOrderByPID() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectGTypeOrderByPID");
			List<Map<String, Object>> reList = openService.queryForList(map);
			output("0000", reList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * wjl 添加商品类别
	 */
	@RequestMapping(value = "/GoodsType_update_updateGoodsType", method = RequestMethod.POST)
	public void updateGoodsType() {
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			map.put("SHOP_FK", userObj.get("FK_SHOP"));
			boolean updateOrderResult = false;
			int oldOrder = Integer.parseInt(map.get("GTYPE_OLD_ORDER").toString());
			int userSROrder = Integer.parseInt(map.get("GTYPE_ORDER").toString());
			map.put("sqlMapId", "loadGoodsTypeOrder");
			Map<String, Object> reMapOrder = (Map<String, Object>) openService.queryForObject(map);
			int maxOrder = Integer.parseInt(reMapOrder.get("GTYPE_ORDER").toString());
			if (userSROrder <= 0 || userSROrder > maxOrder) {
				output("5555", "输入的序号不合法");
			} else {
				if (Integer.parseInt(map.get("GTYPE_OLD_ORDER").toString()) < Integer
						.parseInt(map.get("GTYPE_ORDER").toString())) {
					map.put("sqlMapId", "updateGoodsTypeOrderChangeLarge");
					updateOrderResult = openService.update(map);
				} else {
					map.put("sqlMapId", "updateGoodsTypeOrderChangeSmall");
					updateOrderResult = openService.update(map);
				}
				map.put("UPDATE_BY", userObj.get("USER_NAME"));
				map.put("sqlMapId", "updateGoodsType");
				boolean result = openService.update(map);
				if (result) {
					output("0000", "修改成功");
				} else {
					output("9999", "修改失败");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * wjl 删除商品分类以及它下面的子分类
	 */
	@RequestMapping(value = "/GoodsType_delete_deleteGoodsTypeByID", method = RequestMethod.POST)
	public void deleteGoodsTypeByID() {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			Map<String, Object> map = getParameterMap();
			map.put("SHOP_FK", userObj.get("FK_SHOP"));
			map.put("GTYPE_PID", map.get("GTYPE_PK"));
			map.put("sqlMapId", "selectGoodsTypePNameByPID");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			int olderOrder = Integer.parseInt(reMap.get("GTYPE_ORDER").toString());
			map.put("GTYPE_PID", reMap.get("GTYPE_PID"));
			map.put("sqlMapId", "loadGoodsTypeOrder");
			Map<String, Object> reMapOrder = (Map<String, Object>) openService.queryForObject(map);
			int maxOrder = Integer.parseInt(reMapOrder.get("GTYPE_ORDER").toString());
			if (olderOrder < maxOrder) {
				map.put("GTYPE_ORDER", maxOrder);
				map.put("GTYPE_OLD_ORDER", olderOrder);
				map.put("PID", reMap.get("GTYPE_PID"));
				map.put("sqlMapId", "updateGoodsTypeOrderChangeLarge");
				openService.update(map);
			}
			/* 删除商品 */
			map.put("sqlMapId", "deleteGoodsByGTypePK");
			openService.delete(map);
			map.put("sqlMapId", "deleteGoodsTypeByID");
			boolean deleteResult = openService.delete(map);
			if (deleteResult) {
				output("0000", "删除成功");
			} else {
				output("0000", "删除失败");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @date 2018年8月17日 上午2:22:22
	 * @author lps
	 * 
	 * @Description: 根据类别id查询类别的名字
	 * @return void
	 *
	 */
	@RequestMapping(value = "/GoodsType_select_findGoodsNameById")
	public void findGoodsNameById() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectGtypeNameById");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			output("0000", reMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author kqs
	 * @return void
	 * @date 2018年9月8日 - 下午4:32:43
	 * @description:小程序根据商铺id获取商品类型
	 */
	@RequestMapping(value = "/GoodsType_select_loadGoodsTypeByShopId", method = RequestMethod.POST)
	public void loadGoodsTypeByShopId() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("openid").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("shopid") || StringUtils.isBlank(map.get("shopid").toString())) {
					output("9999", "参数无效");
					return;
				}
				map.put("FK_SHOP", map.get("shopid").toString());
				map.put("sqlMapId", "loadGoodTypeByShopId");
				List<Map<String, Object>> reMap = openService.queryForList(map);
				List<Map<String, Object>> typeList = new ArrayList<>();
				List<Map<String, Object>> goodsList = new ArrayList<>();
				if (reMap != null && reMap.size() > 0) {
					Map<String, String> checkMap = new HashMap<>();
					for (Map<String, Object> typeMap : reMap) {
						if (!checkMap.containsKey(typeMap.get("GTYPE_PK").toString())) {
							Map<String, Object> type = new HashMap<>();
							type.put("GTYPE_PK", typeMap.get("GTYPE_PK").toString());
							type.put("GTYPE_NAME", typeMap.get("GTYPE_NAME").toString());
							typeList.add(type);
							checkMap.put(typeMap.get("GTYPE_PK").toString(), "");
						}
					}
					map.put("FK_USER", map.get("openid").toString());
					map.put("CART_STATE", "zancun");
					map.put("sqlMapId", "selectCartDataByUser");
					// 购物车列表
					List<Map<String, Object>> cartDataList = openService.queryForList(map);
					
					for (Map<String, Object> type : typeList) {
						Map<String, Object> good = new HashMap<>();
						good.put("GTYPE_PK", type.get("GTYPE_PK").toString());
						good.put("GTYPE_NAME", type.get("GTYPE_NAME").toString());
						List<Map<String, Object>> goods = new ArrayList<>();
						double typeCount = 0;
						for (Map<String, Object> goodInfo : reMap) {
							if ((type.get("GTYPE_PK").toString()).equals(goodInfo.get("GTYPE_PK").toString())) {
								double goodCount = 0;
								if (cartDataList != null && !cartDataList.isEmpty()) {
									for (Map<String, Object> cartMap : cartDataList) {
										if ((cartMap.get("FK_GOODS").toString()).equals(goodInfo.get("GOODS_PK").toString())) {
											typeCount = Arith.add(typeCount, Double.valueOf(cartMap.get("qity").toString()));
											goodCount = Arith.add(goodCount, Double.valueOf(cartMap.get("qity").toString()));
										}
									}
								}
								goodInfo.put("qity", goodCount);
								goods.add(goodInfo);
							}
						}
						good.put("infos", goods);
						good.put("GTYPE_QITY", typeCount);
						goodsList.add(good);
					}
				}
				map.clear();
				map.put("greensList", goodsList);
				map.put("navList", typeList);
				output("0000", map);
				return;
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
