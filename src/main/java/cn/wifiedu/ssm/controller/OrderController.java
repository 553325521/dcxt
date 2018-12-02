package cn.wifiedu.ssm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.TextMessage;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.DateUtil;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;
import cn.wifiedu.ssm.websocket.MessageType;
import cn.wifiedu.ssm.websocket.SystemWebSocketHandler;

@Controller
@Scope("prototype")
public class OrderController extends BaseController {

	private static Logger logger = Logger.getLogger(OrderController.class);

	@Resource
	OpenService openService;

	@Resource
	private JedisClient jedisClient;

	@Autowired
	private SystemWebSocketHandler systemWebSocketHandler;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	@Autowired
	private TransactionManagerController txManagerController;

	@Autowired
	private ShoppingCartController shopCartCtrl;

	/**
	 * <p>
	 * Title: loadOrderNumber
	 * </p>
	 * <p>
	 * Description: 查询支付与未支付状态分别订单数量
	 * </p>
	 */
	@RequestMapping(value = "/Order_load_loadOrderNumber", method = RequestMethod.POST)
	public void loadOrderNumber() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("openid").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("CREATE_TIME") || StringUtils.isBlank(map.get("CREATE_TIME").toString())
						|| !map.containsKey("END_TIME") || StringUtils.isBlank(map.get("END_TIME").toString())) {
					output("9999", "时间参数无效");
					return;
				}
				if (!map.containsKey("FK_SHOP") || StringUtils.isBlank(map.get("FK_SHOP").toString())) {
					output("9999", "商铺ID参数无效");
					return;
				}
				map.put("sqlMapId", "selectOrderNumber");
				List<Map<String, Object>> orderDataList = openService.queryForList(map);
				output("0000", orderDataList);
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * Title: loadOrderDataByTime
	 * </p>
	 * <p>
	 * Description: 根据时间、状态查询订单列表
	 * </p>
	 */
	@RequestMapping(value = "/Order_load_loadOrderDataByTime", method = RequestMethod.POST)
	public void loadOrderDataByTime() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("openid").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("CREATE_TIME") || StringUtils.isBlank(map.get("CREATE_TIME").toString())
						|| !map.containsKey("END_TIME") || StringUtils.isBlank(map.get("END_TIME").toString())) {
					output("9999", "时间参数无效");
					return;
				}
				if (!map.containsKey("FK_SHOP") || StringUtils.isBlank(map.get("FK_SHOP").toString())) {
					output("9999", "商铺ID参数无效");
					return;
				}
				if (!map.containsKey("ORDER_PAY_STATE") || StringUtils.isBlank(map.get("ORDER_PAY_STATE").toString())) {
					output("9999", "订单支付状态参数无效");
					return;
				}
				if (map.get("ORDER_PAY_STATE").toString().equals("2")) {
					map.put("ORDER_PAY_STATE", null);
				}
				map.put("sqlMapId", "selectOrderByTime");
				List<Map<String, Object>> orderDataList = openService.queryForList(map);
				output("0000", orderDataList);
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	* <p>Title: loadOrderDataByShopOrTimeOrWay</p>
	* <p>Description:根据选择商铺、时间、支付方式查询订单 </p>
	*/
	@RequestMapping(value = "/Order_load_loadOrderDataByShopOrTimeOrWay", method = RequestMethod.POST)
	public void loadOrderDataByShopOrTimeOrWay(){
		try {
			Map<String,Object> map = getParameterMap();
//			根据前台传的时间参数计算开始时间和结束时间
			String[] dateStrArray = DateUtil.selectTime(map.get("selectTime").toString());
			map.put("START_TIME", dateStrArray[0]);
			map.put("END_TIME", dateStrArray[1]);
			map.put("sqlMapId", "selectOrderByShopOrTimeOrPayWay");
			List<Map<String, Object>> orderDataList = openService.queryForList(map);
			output("0000", orderDataList);
		} catch (ExceptionVo e) {
			output("9999", "获取消费统计失败");
			e.printStackTrace();
		} catch (Exception e) {
			output("9999", "获取消费统计失败");
			e.printStackTrace();
		}
		
	}

	/**
	 * <p>
	 * Title: loadOrderDetailByOrderPK
	 * </p>
	 * <p>
	 * Description:根据订单id查询订单详情
	 * </p>
	 */
	@RequestMapping(value = "/Order_load_loadOrderDetailByOrderPK", method = RequestMethod.POST)
	public void loadOrderDetailByOrderPK() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("openid").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("ORDER_PK") || StringUtils.isBlank(map.get("ORDER_PK").toString())) {
					output("9999", "订单ID无效");
					return;
				}
				map.put("sqlMapId", "selectOrderDetailByOrderPK");
				List<Map<String, Object>> orderDataList = openService.queryForList(map);
				output("0000", orderDataList);
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * Title: tuiCai
	 * </p>
	 * <p>
	 * Description: 订单退菜
	 * </p>
	 */
	@RequestMapping(value = "/Order_delete_tuiCai", method = RequestMethod.POST)
	public void tuiCai() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("openid").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("ORDER_DETAILS_PK")
						|| StringUtils.isBlank(map.get("ORDER_DETAILS_PK").toString())) {
					output("9999", "退菜ID无效");
					return;
				}
				map.put("sqlMapId", "deleteOrderDetailByORDER_DETAILS_PK");
				boolean deleteResult = openService.delete(map);
				if (deleteResult) {
					output("0000", "退菜成功");
				} else {
					output("9999", "退菜失败");
				}
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	* <p>Title: loadWMOrderData</p>
	* <p>Description: 加载外卖订单数据</p>
	*/
	@RequestMapping(value = "/Order_select_loadWMOrderData", method = RequestMethod.POST)
	public void loadWMOrderData(){
		try {
			Map<String,Object> map = getParameterMap();
			List<Map<String,Object>> returnData = new ArrayList<Map<String,Object>>();
			//拿到选择的外卖订单来源
			String [] orderSourceArray = map.get("selectSource").toString().split(",");
			//如果包含智慧云端,加载智慧云端数据
			if(CheckArrayContainsValue(orderSourceArray,"智慧云")){
				map.put("sqlMapId", "selectZHYWMOrderData");
				List<Map<String,Object>> zhyResultList = openService.queryForList(map);
				if(zhyResultList.size()!=0){
					for(Map<String,Object> o:zhyResultList){
						o.put("SOURCENAME", "智慧云");
						returnData.add(o);
					}
				}
				
			}
			//如果包含饿了么端，加载饿了么外卖数据
			if(CheckArrayContainsValue(orderSourceArray,"饿了么")){
				
			}
			
			output("0000",returnData);
		} catch (ExceptionVo e) {
			e.printStackTrace();
			output("9999","没有外卖订单数据");
		} catch (Exception e) {
			e.printStackTrace();
			output("9999","没有外卖订单数据");
		}
	}
	/**
	* <p>Title: CheckArrayContainsValue</p>
	* <p>Description:检查某个字符串数组是否包含某个值 </p>
	* @return
	*/
	private boolean CheckArrayContainsValue(String [] strArray,String value){
		for(String s:strArray){
			if(s.equals(value)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @author kqs
	 * @return void
	 * @date 2018年10月27日 - 上午11:16:04
	 * @description:根据购物车创建订单或挂单
	 */
	@RequestMapping(value = "/Order_insert_createOrder", method = RequestMethod.POST)
	public void createOrder() {
		try {
			Map<String, Object> map = getParameterMap();
			//先查询该桌位是否被使用
			Map<String, Object> mapTables = getParameterMap();
			mapTables.put("sqlMapId", "findTablesById");
			mapTables.put("TABLES_ID", map.get("TABLES_PK"));
			mapTables = (Map<String, Object>)openService.queryForObject(mapTables);
			if (mapTables.containsKey("TABLES_ISUSE") && "1".equals(mapTables.get("TABLES_ISUSE"))) {
				output("3333", "桌位被使用");
				return;
			}
			
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
				map.put("CART_STATE", "zancun");
				map.put("sqlMapId", "selectCartDataByUser");
				List<Map<String, Object>> cartDataList = openService.queryForList(map);
				if (cartDataList != null && !cartDataList.isEmpty()) {
					Map<String, Object> orderMap = new HashMap<>();
					orderMap.put("ORDER_POSITION", map.get("ORDER_POSITION").toString());
					orderMap.put("ORDER_CODE", this.getOrderCode(map));
					orderMap.put("ORDER_RS", map.get("ORDER_RS").toString());
					orderMap.put("CREATE_BY", map.get("FK_USER").toString());
					orderMap.put("ORDER_STATE", map.get("ORDER_STATE").toString());
					orderMap.put("FK_SHOP", map.get("FK_SHOP").toString());
					orderMap.put("sqlMapId", "insertOrderInfo");
					txManagerController.createTxManager();
					String result = openService.insert(orderMap);
					if (result != null) {
						for (Map<String, Object> cartMap : cartDataList) {
							cartMap.put("FK_ORDER", result);
							cartMap.put("FK_SHOP", map.get("FK_SHOP").toString());
							cartMap.put("CREATE_BY", map.get("FK_USER").toString());
							cartMap.put("sqlMapId", "insertOrderDeatilInfo");
							String result1 = openService.insert(cartMap);
							if (result1 == null) {
								throw new RuntimeException();
							}
							
						}
						
						map.put("sqlMapId", "updateCartToOrder");
						if (!openService.update(map)) {
							throw new RuntimeException();
						}
						
						//更改桌位为已使用
						map.put("sqlMapId", "updateTablesIsUseByAreaId");
						map.put("TABLES_ISUSE", 1);
						if (!openService.update(map)) {
							throw new RuntimeException();
						}
						

						txManagerController.commit();
						output("0000", "创建成功");
						//通知客户端创建订单
						systemWebSocketHandler.sendMessageToUser(map.get("FK_SHOP").toString(),new TextMessage(MessageType.UPDATE_ORDERDATA));
						return;
					}
				}
				output("9999", "购物车空空如也~");
				return;
			}
			output("9999", "USER参数无效");
			return;
		} catch (Exception e) {
			logger.error("Order_insert_createOrder error", e);
			txManagerController.rollback();
		}
	}

	/**
	 * @author kqs
	 * @return
	 * @return String
	 * @date 2018年10月27日 - 上午11:45:01
	 * @description:获取订单code
	 */
	private String getOrderCode(Map<String, Object> map) {
		try {
			map.put("sqlMapId", "countByExample");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			if (reMap != null && !reMap.isEmpty()) {
				int count = Integer.valueOf(reMap.get("ORDER_COUNT").toString()) + 1;
				String orderCode = "DD" + StringDeal.getStringDateShort1();
				for (int i = 0; i < (4 - String.valueOf(count).length()); i++) {
					orderCode += "0";
				}
				map.put("ORDER_CODE", orderCode + count);
				map.put("sqlMapId", "checkIsExitByExample");
				Map<String, Object> reMapp = (Map<String, Object>) openService.queryForObject(map);
				if (reMapp != null && Integer.valueOf(reMapp.get("ORDER_COUNT").toString()) > 0) {
					return getOrderCode(map);
				}
				return orderCode + count;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @author kqs
	 * @return void
	 * @date 2018年10月29日 - 下午9:54:45
	 * @description:订单加菜
	 */
	@RequestMapping(value = "/Order_update_updateCartToOrderMore", method = RequestMethod.POST)
	public void updateCartToOrderMore() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("FK_USER").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("FK_ORDER") || StringUtils.isBlank(map.get("FK_ORDER").toString())) {
					output("9999", "订单ID无效");
					return;
				}
				if (!map.containsKey("FK_SHOP") || StringUtils.isBlank(map.get("FK_SHOP").toString())) {
					output("9999", "shopid无效");
					return;
				}
				map.put("CART_STATE", "zancun");
				map.put("sqlMapId", "selectCartDataByUser");
				List<Map<String, Object>> cartDataList = openService.queryForList(map);
				txManagerController.createTxManager();
				for (Map<String, Object> cartMap : cartDataList) {
					cartMap.put("FK_ORDER", map.get("FK_ORDER").toString());
					cartMap.put("FK_SHOP", map.get("FK_SHOP").toString());
					cartMap.put("CREATE_BY", map.get("FK_USER").toString());
					cartMap.put("sqlMapId", "insertOrderDeatilInfo");
					String result1 = openService.insert(cartMap);
					if (result1 == null) {
						throw new RuntimeException();
					}
				}
				
				map.put("sqlMapId", "updateCartToOrder");
				if (!openService.update(map)) {
					throw new RuntimeException();
				}
				txManagerController.commit();
				output("0000", "操作成功~");
				return;
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			logger.error("error", e);
			txManagerController.rollback();
		}
		output("9999", "操作失败~");
		return;
	}
	
	/**
	 * 
	 * @author kqs
	 * @return void
	 * @date 2018年10月29日 - 下午9:54:45
	 * @description:订单加菜
	 */
	@RequestMapping(value = "/Order_select_loadCountOrderWei", method = RequestMethod.POST)
	public void loadCountWei() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("FK_USER").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("FK_SHOP") || StringUtils.isBlank(map.get("FK_SHOP").toString())) {
					output("9999", "shopid无效");
					return;
				}
				map.put("sqlMapId", "loadCountOrderWei");
				Map<String, Object> orderObj = (Map<String, Object>) openService.queryForObject(map);
				output("0000", orderObj);
				return;
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
		output("9999", "操作失败~");
		return;
	}
	
}
