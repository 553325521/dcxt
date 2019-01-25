package cn.wifiedu.ssm.controller;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.Arith;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.print.PrintTemplate58MM;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * @author kqs
 * @time 2018年7月24日 - 下午10:34:43
 * @description:打印机模块
 */
@Controller
@Scope("prototype")
public class PrinterController extends BaseController {

	private static Logger logger = Logger.getLogger(PrinterController.class);

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

	@Autowired
	private TransactionManagerController txManagerController;

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年7月24日 - 下午10:37:23
	 * @description:打印机设置
	 */
	@RequestMapping("/Print_insert_addPinterInfo")
	public void findPlatformTypeList(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("INSERT_TIME", StringDeal.getStringDate());
			map.put("sqlMapId", "addPinterInfo");
			txManagerController.createTxManager();
			String res = openService.insert(map);
			if (res != null) {
				map.put("sqlMapId", "updatePrinterToUse");
				if (openService.update(map)) {
					txManagerController.commit();
					output("0000", "保存成功!");
					return;
				}
			}
		} catch (Exception e) {
			txManagerController.rollback();
			output("9999", " Exception ", e);
		}
	}

	/**
	 * @author kqs
	 * @param string
	 * @return
	 * @return Object
	 * @date 2018年12月30日 - 下午10:19:18
	 * @description:创建该商铺的打印设备id
	 */
	private String createPrintNum(String shopId) {
		try {
			Map<String, Object> res = (Map<String, Object>) openService.queryForObject(new HashMap<String, Object>() {
				{
					put("sqlMapId", "loadPrintCount");
				}
			});
			String printCount = "";
			int PRINT_COUNT = Integer.valueOf(res.get("PRINT_COUNT").toString()) + 1;
			for (int i = 0; i < 12 - (String.valueOf(PRINT_COUNT)).length(); i++) {
				printCount += "0";
			}
			return printCount + PRINT_COUNT;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月27日 - 下午2:42:35
	 * @description:查询已购打印机
	 */
	@RequestMapping("/Print_queryForList_loadPrintList")
	public void loadPrintList(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "loadPrintList");
			List<Map<String, Object>> res = openService.queryForList(map);
			if (res != null) {
				output("0000", res);
				return;
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
		return;
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月14日 - 下午11:08:32
	 * @description:查询打印机相关信息
	 */
	@RequestMapping("/Print_queryForList_loadPrintRelevantList")
	public void loadPrintRelevantList(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadPrintRelevantList");
			List<Map<String, Object>> res = openService.queryForList(map);
			output("0000", res);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月14日 - 下午11:08:47
	 * @description:查询打印机价格
	 */
	@RequestMapping("/Print_queryForList_findPriceURL")
	public void findPriceURL(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "findPriceURL");
			Map<String, Object> res = (Map<String, Object>) openService.queryForObject(map);
			output("0000", res);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月17日 - 上午12:19:50
	 * @description:打印机购买
	 */
	@RequestMapping("/Print_queryForList_addPirntBug")
	public void addPirntBug(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("INSERT_TIME", StringDeal.getStringDate());
			map.put("CREATER", userObj.getString("USER_WX"));
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			int count = Integer.valueOf(map.get("PRINT_BUG_NUM").toString());
			txManagerController.createTxManager();
			String PRINT_PRICE = map.get("PRINT_PRICE").toString();
			for (int i = 0; i < count; i++) {
				map.put("PRINT_BUG_PK", this.createPrintNum(userObj.getString("FK_SHOP")));
				map.put("PRINT_BUG_NUM", (i + 1));
				map.put("PRINT_PRICE", Arith.div(Double.valueOf(PRINT_PRICE), count));
				map.put("sqlMapId", "addPirntBug");
				String res = openService.insert(map);
				if (res == null) {
					throw new RuntimeException();
				}
			}
			txManagerController.commit();
			output("0000", "购买成功！");
			return;
		} catch (Exception e) {
			txManagerController.rollback();
			output("9999", " Exception ", e);
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月27日 - 下午2:42:35
	 * @description:查询已添加打印机
	 */
	@RequestMapping("/Print_queryForList_loadInUsePrintList")
	public void loadInUsePrintList(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "loadInUsePrintList");
			List<Map<String, Object>> res = openService.queryForList(map);
			if (res != null) {
				output("0000", res);
				return;
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
		return;
	}

	public void doPrintJSByOrderId(String orderId) {
		Map map = new HashMap<String, Object>();
		// 根据订单id查询出来商铺id
		map.put("sqlMapId", "slectShopIdByOrderId");
		map.put("ORDER_PK", orderId);
		try {
			map = (Map) openService.queryForObject(map);
		} catch (Exception e) {
			logger.error(e);
			return;
		}
		String shopId = (String) map.get("FK_SHOP");
		String ORDER_DIVISION = (String) map.get("ORDER_DIVISION");

		String type = null;
		if ("1".equals(ORDER_DIVISION)) {
			type = "wmjs";
		} else {
			type = "tdjs";
		}

		doPrintJS(shopId, orderId, type);
	}

	/**
	 * 
	 * @author kqs
	 * @param shopId
	 * @param orderId
	 * @param type
	 *            打印类型
	 * @return void
	 * @date 2019年1月21日 - 上午12:01:48
	 * @description: tdjs:堂点结算 wmjs:外卖结算
	 */
	@RequestMapping("/Print_insert_doPrintJS")
	public void doPrintJS(String shopId, String orderId, String type) {
		try {

			// shopId = "f11099f4816f4a6c99e511c4a7aa82d0";
			// orderId = "0151f0c7738f4957b62e20ec3287c107";
			// type = "wmjs";

			// Map<String, Object> map = getParameterMap();
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("FK_SHOP", shopId);
			// 根据shopid 去查对应的打印机
			map.put("sqlMapId", "loadInUsePrintList");
			// 先获取到所有的打印机
			List<Map<String, Object>> res = openService.queryForList(map);
			if (res != null && !res.isEmpty()) {
				map.put("ORDER_PK", orderId);
				map.put("sqlMapId", "loadOrderInfoById");
				// 根据orderId 获取对应订单头
				Map<String, Object> order = (Map<String, Object>) openService.queryForObject(map);

				map.put("sqlMapId", "loadOrderDetailInfoById");
				// 根据orderId 获取对应订单详情 总的 给其他打印联用
				List<Map<String, Object>> orderInfo = openService.queryForList(map);
				order.put("orderGoodsList", orderInfo);

				map.put("sqlMapId", "getShopInfo");
				map.put("SHOP_ID", map.get("FK_SHOP"));
				// 根据shopid 获取对应订单头
				Map<String, Object> shop = (Map<String, Object>) openService.queryForObject(map);

				map.put("sqlMapId", "selectJFCZByOrderId");
				// 根据orderId 获取会员卡信息
				Map<String, Object> hys = (Map<String, Object>) openService.queryForObject(map);
				if (hys != null) {
					// 会员卡号
					if (hys.containsKey("VCARD_NUMBER")) {
						order.put("VCARD_NUMBER", hys.get("VCARD_NUMBER"));
					}
					if (hys.containsKey("USER_VCARD_JF")) {
						// 积分
						order.put("JIFEN", hys.get("USER_VCARD_JF"));
					}
					if (hys.containsKey("USER_VCARD_CZ")) {
						// 储值
						order.put("CHUZHI", hys.get("USER_VCARD_CZ"));
					}
					// 卡券总数
					if (hys.containsKey("CARD_NUM")) {
						order.put("KAQUAN", "-" + hys.get("CARD_NUM").toString());
					}
				}
				// 遍历打印机 找对应联的打印机
				for (Map<String, Object> p : res) {
					if ("58mm".equals(p.get("PRINTER_PAGE_WIDTH").toString())
							&& (p.get("PRINTER_LEVEL").toString()).indexOf("3") >= 0) {
						if ("tdjs".equals(type)) {
							// 堂点对账58mm
							// 发送内容
							// shopId!!!deviceId!!!orderId!!!content#
							StringBuilder printStr = new StringBuilder();
							// shopId
							printStr.append(UUID.randomUUID().toString().replace("-", "")).append("!!!")
									// deviceId
									.append(res.get(0).get("PRINTER_KEY")).append("!!!")
									// orderId
									.append(order.get("ORDER_CODE")).append("!!!")
									// content
									.append("&!*4" + order.get("ORDER_CODE") + "*"
											+ new PrintTemplate58MM(order, shop).getInStoreJSTemplate());
							contPrintService(printStr);
						} else {
							// 外卖对账 58mm
							// 发送内容
							// shopId!!!deviceId!!!orderId!!!content#
							StringBuilder printStr = new StringBuilder();
							// shopId
							printStr.append(UUID.randomUUID().toString().replace("-", "")).append("!!!")
									// deviceId
									.append(res.get(0).get("PRINTER_KEY")).append("!!!")
									// orderId
									.append(order.get("ORDER_CODE")).append("!!!")
									// content
									.append("&!*4" + order.get("ORDER_CODE") + "*"
											+ new PrintTemplate58MM(order, shop).getOutStoreJSTemplate());
							contPrintService(printStr);
						}
					} else if ("80mm".equals(p.get("PRINTER_PAGE_WIDTH").toString())
							&& (p.get("PRINTER_LEVEL").toString()).indexOf("3") >= 0) {
						// TODO 80mm 备物联样式
					}
				}
				return;
			}
			return;
		} catch (Exception e) {
			logger.error(" Exception ", e);
		}
	}

	public void doPrintDZByOrderId(String orderId) {
		Map map = new HashMap<String, Object>();
		// 根据订单id查询出来商铺id
		map.put("sqlMapId", "slectShopIdByOrderId");
		map.put("ORDER_PK", orderId);
		try {
			map = (Map) openService.queryForObject(map);
		} catch (Exception e) {
			logger.error(e);
			return;
		}
		String shopId = (String) map.get("FK_SHOP");
		String ORDER_DIVISION = (String) map.get("ORDER_DIVISION");

		String type = null;
		if ("1".equals(ORDER_DIVISION)) {
			type = "wmdz";
		} else {
			type = "tddz";
		}

		doPrintDZ(shopId, orderId, type);
	}

	/**
	 * 
	 * @author kqs
	 * @param shopId
	 * @param orderId
	 * @param type
	 *            打印类型
	 * @return void
	 * @date 2019年1月21日 - 上午12:01:48
	 * @description: tddz:堂点对账 wmdz:外卖对账
	 */
	@RequestMapping("/Print_insert_doPrintDZ")
	public void doPrintDZ(String shopId, String orderId, String type) {
		try {

			// shopId = "f11099f4816f4a6c99e511c4a7aa82d0";
			// orderId = "0151f0c7738f4957b62e20ec3287c107";
			// type = "wmdz";

			// Map<String, Object> map = getParameterMap();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("FK_SHOP", shopId);
			// 根据shopid 去查对应的打印机
			map.put("sqlMapId", "loadInUsePrintList");
			// 先获取到所有的打印机
			List<Map<String, Object>> res = openService.queryForList(map);
			if (res != null && !res.isEmpty()) {
				map.put("ORDER_PK", orderId);
				map.put("sqlMapId", "loadOrderInfoById");
				// 根据orderId 获取对应订单头
				Map<String, Object> order = (Map<String, Object>) openService.queryForObject(map);

				map.put("sqlMapId", "loadOrderDetailInfoById");
				// 根据orderId 获取对应订单详情 总的 给其他打印联用
				List<Map<String, Object>> orderInfo = openService.queryForList(map);
				order.put("orderGoodsList", orderInfo);

				map.put("sqlMapId", "getShopInfo");
				map.put("SHOP_ID", map.get("FK_SHOP"));
				// 根据shopid 获取对应订单头
				Map<String, Object> shop = (Map<String, Object>) openService.queryForObject(map);

				map.put("sqlMapId", "selectPrintData1ByOrderId");
				// 根据orderId 获取会员卡信息
				Map<String, Object> hys = (Map<String, Object>) openService.queryForObject(map);
				if (hys != null) {
					if (hys.containsKey("vard_record")) {
						List<Map<String, Object>> hyInfo = (List<Map<String, Object>>) hys.get("vard_record");
						if (hyInfo != null && !hyInfo.isEmpty()) {
							// 会员卡号
							if (hys.containsKey("VCARD_NUMBER")) {
								order.put("VCARD_NUMBER", hys.get("VCARD_NUMBER"));
								// 消耗类型
								for (Map<String, Object> hy : hyInfo) {
									if (hy.containsKey("VRECORD_TYPE")) {
										String codeTypes[] = hy.get("VRECORD_TYPE").toString().split("");
										String str = "";
										if (codeTypes[0].equals("1")) {
											str = "+";
										} else {
											str = "-";
										}
										if (codeTypes[1].equals("1")) {
											str = str + hy.get("VRECORD_NUM").toString();
											// 积分
											order.put("JIFEN", str);
										} else {
											String chuzhi = String.valueOf(Arith
													.div(Double.valueOf(hy.get("VRECORD_NUM").toString()), 100, 2));
											str = str + chuzhi;
											// 储值
											order.put("CHUZHI", str);
										}
									}
								}
							}
							// 卡券总数
							if (hys.containsKey("CARD_NUM")) {
								order.put("KAQUAN", "-" + hys.get("CARD_NUM").toString());
							}
							// 支付时间
							order.put("PAY_TIME", hys.get("PAY_TIME").toString().substring(11, 16));
						}
					}
				}
				// 遍历打印机 找对应联的打印机
				for (Map<String, Object> p : res) {
					if ("58mm".equals(p.get("PRINTER_PAGE_WIDTH").toString())
							&& (p.get("PRINTER_LEVEL").toString()).indexOf("2") >= 0) {
						if ("tddz".equals(type)) {
							// 堂点对账58mm
							// 发送内容
							// shopId!!!deviceId!!!orderId!!!content#
							StringBuilder printStr = new StringBuilder();
							// shopId
							printStr.append(UUID.randomUUID().toString().replace("-", "")).append("!!!")
									// deviceId
									.append(res.get(0).get("PRINTER_KEY")).append("!!!")
									// orderId
									.append(order.get("ORDER_CODE")).append("!!!")
									// content
									.append("&!*4" + order.get("ORDER_CODE") + "*"
											+ new PrintTemplate58MM(order, shop).getInStoreDZTemplate());
							contPrintService(printStr);
						} else {
							// 外卖对账 58mm
							// 发送内容
							// shopId!!!deviceId!!!orderId!!!content#
							StringBuilder printStr = new StringBuilder();
							// shopId
							printStr.append(UUID.randomUUID().toString().replace("-", "")).append("!!!")
									// deviceId
									.append(res.get(0).get("PRINTER_KEY")).append("!!!")
									// orderId
									.append(order.get("ORDER_CODE")).append("!!!")
									// content
									.append("&!*4" + order.get("ORDER_CODE") + "*"
											+ new PrintTemplate58MM(order, shop).getOutStoreBWTemplate());
							contPrintService(printStr);
						}
					} else if ("80mm".equals(p.get("PRINTER_PAGE_WIDTH").toString())
							&& (p.get("PRINTER_LEVEL").toString()).indexOf("2") >= 0) {
						// TODO 80mm 备物联样式
					}
				}
				logger.info(" 打印成功! ");
				return;
			}
			logger.info(" 暂无可用打印机! ");
			return;
		} catch (Exception e) {
			logger.error(" Exception ", e);
		}
	}

	public void doPrintByOrderId(String orderId) {
		Map map = new HashMap<String, Object>();
		// 根据订单id查询出来商铺id
		map.put("sqlMapId", "slectShopIdByOrderId");
		map.put("ORDER_PK", orderId);

		try {
			map = (Map) openService.queryForObject(map);
		} catch (Exception e) {
			logger.error(e);
			return;
		}
		String shopId = (String) map.get("FK_SHOP");
		String ORDER_DIVISION = (String) map.get("ORDER_DIVISION");

		String type = null;
		if ("1".equals(ORDER_DIVISION)) {
			type = "wmbw";
		} else {
			type = "tdbw";
		}

		doPrint(shopId, orderId, type);
	}

	/**
	 * 
	 * @author lps
	 * @date Jan 25, 2019 10:17:08 PM
	 * 
	 * @description: 补单
	 * @return void
	 */
	@RequestMapping("/Print_insert_BD")
	public void BD() {
		try {
			Map<String, Object> map = getParameterMap();
			String orderId = (String) map.get("ORDER_PK");
			map.put("sqlMapId", "slectShopIdByOrderId");
			Map orderMap = (Map) openService.queryForObject(map);
			if (orderMap.get("ORDER_PAY_STATE") == null || "0".equals(orderMap.get("ORDER_PAY_STATE"))) {
				map.put("sqlMapId", "loadFuncSwitchList");
				Map switchMap = (Map) openService.queryForObject(map);
				String CHECK_XDDYJSL = (String) switchMap.get("CHECK_XDDYJSL");
				if ("true".equals(CHECK_XDDYJSL)) {
					doPrintJSByOrderId(orderId);
				}
			} else {
				doPrintDZByOrderId(orderId);
				doPrintJSByOrderId(orderId);
			}
			doPrintByOrderId(orderId);

		} catch (Exception e) {
			logger.error(e);
		}

	}

	/**
	 * 
	 * @author kqs
	 * @param shopId
	 *            商铺id
	 * @param orderId
	 *            订单id
	 * @param type
	 *            打印类型
	 * @return void
	 * @date 2019年1月21日 - 上午12:01:48
	 * @description: tdbw:堂点备物 wmbw:外卖备物
	 */
	@RequestMapping("/Print_insert_doPrintBW")
	public void doPrint(String shopId, String orderId, String type) {
		try {

			// shopId = "f11099f4816f4a6c99e511c4a7aa82d0";
			// orderId = "4ee530d5468a430b84a9077e8c1ed83a";
			// type = "tdbw";

			// Map<String, Object> map = getParameterMap();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("FK_SHOP", shopId);
			// 根据shopid 去查对应的打印机
			map.put("sqlMapId", "loadInUsePrintList");
			// 先获取到所有的打印机
			List<Map<String, Object>> res = openService.queryForList(map);
			if (res != null && !res.isEmpty()) {
				map.put("ORDER_PK", orderId);
				map.put("sqlMapId", "loadOrderInfoById");
				// 根据orderId 获取对应订单头
				Map<String, Object> order = (Map<String, Object>) openService.queryForObject(map);

				map.put("sqlMapId", "loadOrderDetailInfoById");
				// 根据orderId 获取对应订单详情 总的 给其他打印联用
				List<Map<String, Object>> orderInfo = openService.queryForList(map);
				order.put("orderGoodsList", orderInfo);

				map.put("sqlMapId", "getShopInfo");
				map.put("SHOP_ID", map.get("FK_SHOP"));
				// 根据shopid 获取对应订单头
				Map<String, Object> shop = (Map<String, Object>) openService.queryForObject(map);

				if (type.indexOf("bw") >= 0) {
					// 打印备物联
					// 遍历打印机 获取打印标签
					Map<String, String> labels = new HashMap<>();
					// 标签对应打印机集合
					Map<String, List<Map<String, Object>>> printers = new HashMap<>();
					for (Map<String, Object> rs : res) {
						String onces[] = rs.get("PRINTER_DISHES").toString().split(",");
						for (String once : onces) {
							labels.put(once, once);
							List<Map<String, Object>> printer = new ArrayList<>();
							if (printers.containsKey(once) && (rs.get("PRINTER_LEVEL").toString()).indexOf("1") >= 0) {
								printer = printers.get(once);
							}
							printer.add(rs);
							printers.put(once, printer);
						}
					}
					// 非重 标签列表
					Collection<String> valueCollection = labels.values();
					List<String> valueList = new ArrayList<String>(valueCollection);

					// 打印备物联
					for (String label : valueList) {
						// 该标签要打印的组
						List<Map<String, Object>> goodsList = new ArrayList<>();
						for (Map<String, Object> goods : orderInfo) {
							if ((goods.get("GOODS_PRINT_LABEL").toString()).indexOf(label) >= 0) {
								// 如果标签相同 就放进去
								goodsList.add(goods);
							}
						}
						// 该标签打印机
						List<Map<String, Object>> pList = printers.get(label);
						for (Map<String, Object> p : pList) {
							if ("58mm".equals(p.get("PRINTER_PAGE_WIDTH").toString())) {
								if ("tdbw".equals(type)) {
									// 堂点备物58mm
									// 发送内容
									// shopId!!!deviceId!!!orderId!!!content#
									StringBuilder printStr = new StringBuilder();
									order.put("orderGoodsList", goodsList);
									// shopId
									printStr.append(UUID.randomUUID().toString().replace("-", "")).append("!!!")
											// deviceId
											.append(res.get(0).get("PRINTER_KEY")).append("!!!")
											// orderId
											.append(order.get("ORDER_CODE")).append("!!!")
											// content
											.append("&!*4" + order.get("ORDER_CODE") + "*"
													+ new PrintTemplate58MM(order, shop).getInStoreBWTemplate());
									contPrintService(printStr);
								} else {
									// 外卖备物 58mm
									// 发送内容
									// shopId!!!deviceId!!!orderId!!!content#
									StringBuilder printStr = new StringBuilder();
									// shopId
									printStr.append(UUID.randomUUID().toString().replace("-", "")).append("!!!")
											// deviceId
											.append(res.get(0).get("PRINTER_KEY")).append("!!!")
											// orderId
											.append(order.get("ORDER_CODE")).append("!!!")
											// content
											.append("&!*4" + order.get("ORDER_CODE") + "*"
													+ new PrintTemplate58MM(order, shop).getOutStoreBWTemplate());
									contPrintService(printStr);
								}
							} else if ("80mm".equals(p.get("PRINTER_PAGE_WIDTH").toString())) {
								// TODO 80mm 备物联样式
							}
						}
					}
				}
				return;
			}
			return;
		} catch (Exception e) {
			logger.error(" Exception ", e);
		}
	}

	public void contPrintService(StringBuilder printStr) {
		String host = "119.23.71.153"; // 要连接的服务端IP地址 119.23.71.153
		int port = 8008; // 要连接的服务端对应的监听端口
		try {
			// 与服务端建立连接
			Socket client = new Socket(host, port);
			Writer writer = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
			writer.write(printStr.toString());
			writer.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月27日 - 下午2:42:35
	 * @description:查询所有的商品标签
	 */
	@RequestMapping("/ShopTag_queryForList_selectShopGoodsTagByWhere")
	public void selectShopGoodsTagByWhere(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "selectShopGoodsTagByWhere");
			List<Map<String, Object>> res = openService.queryForList(map);
			if (res != null) {
				output("0000", res);
				return;
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
		return;
	}

	/**
	 * 
	 */
	@RequestMapping("/ShopTag_insert_addShopGoodsTag")
	public void addShopGoodsTag(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "selectShopGoodsTagByWhere");
			List<Map<String, Object>> res = openService.queryForList(map);
			if (res != null) {
				output("0000", res);
				return;
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
		return;
	}
}
