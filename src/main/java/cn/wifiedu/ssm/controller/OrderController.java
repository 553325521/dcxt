package cn.wifiedu.ssm.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.alibaba.fastjson.JSON;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.DateUtil;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;
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
				map.put("sqlMapId", "selectOrderDetailTableByOrderPK");
				List<Map<String, Object>> orderDataList = openService.queryForList(map);
				List<Map<String, Object>> orderDetailList = (List<Map<String, Object>>)orderDataList.get(0).get("orders");
				int totalFS = 0;
				if(orderDataList.get(0).containsKey("CREATE_TIME")){
					String createTime = orderDataList.get(0).get("CREATE_TIME").toString();
					orderDataList.get(0).put("TIME_YMD",createTime.substring(0,10));
					orderDataList.get(0).put("TIME_HMS",createTime.substring(11));
				}
				if(orderDetailList!=null && orderDetailList.size()!=0){
					for(Map<String, Object> goods:orderDetailList){
						if(goods.containsKey("ORDER_DETAILS_FS")){
							totalFS = totalFS + Integer.parseInt(goods.get("ORDER_DETAILS_FS").toString());
						}
					}
				}
				orderDataList.get(0).put("totalFS",totalFS);
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
	 * Title: loadOrderDetailByOrderPK
	 * </p>lps 2018年12月03日06:15:00
	 * <p>
	 * Description:根据订单id查询订单详情，优化版,返回list嵌套list
	 * </p>
	 */
	@RequestMapping(value = "/Order_load_loadOrderDetailTableByOrderPK", method = RequestMethod.POST)
	public void loadOrderDetailTableByOrderPK() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("openid").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("ORDER_PK") || StringUtils.isBlank(map.get("ORDER_PK").toString())) {
					output("9999", "订单ID无效");
					return;
				}
				map.put("sqlMapId", "selectOrderDetailTableByOrderPK");
				Map<String, Object> reMap = (Map<String, Object> )openService.queryForObject(map);
				output("0000", reMap);
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
//					output("9999", "退菜ID无效");
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
			Map<String,Object> returnMap = new HashMap<String,Object>();
			List<Map<String,Object>> returnData = new ArrayList<Map<String,Object>>();
			Map<String,Object> numberData = new HashMap<String,Object>();
			int noConfirmNumber = 0;
			int isConfirmNumber = 0;
			int isFinishNumber = 0;
			//拿到选择的外卖订单来源
			String [] orderSourceArray = map.get("selectSource").toString().split(",");
			//如果包含智慧云端,加载智慧云端数据
			if(CheckArrayContainsValue(orderSourceArray,"智慧云")){
				//订单数据
				map.put("sqlMapId", "selectZHYWMOrderData");
				List<Map<String,Object>> zhyResultList = openService.queryForList(map);
				if(zhyResultList.size()!=0){
					for(Map<String,Object> o:zhyResultList){
						o.put("SOURCENAME", "智慧云");
						returnData.add(o);
					}
				}
				//订单数量
				map.put("sqlMapId", "loadZHYOrderNumber");
				List<Map<String,Object>> zhyNumberResultList = openService.queryForList(map);
				if(zhyNumberResultList.size()!=0){
					for(Map<String,Object> o:zhyNumberResultList){
						if(o.get("WM_ORDER_STATE").equals("1")){
							noConfirmNumber = noConfirmNumber + Integer.parseInt(o.get("ORDER_NUMBER").toString());
						}else if(o.get("WM_ORDER_STATE").equals("9")){
							isFinishNumber = isFinishNumber+Integer.parseInt(o.get("ORDER_NUMBER").toString());
						}else if(o.get("WM_ORDER_STATE").equals("5")){
							isConfirmNumber = isConfirmNumber + Integer.parseInt(o.get("ORDER_NUMBER").toString());
						}
					}
				}
				
			}
			//如果包含百度外卖端，加载百度外卖数据
			if(CheckArrayContainsValue(orderSourceArray,"百度外卖")){
				map.put("ORDER_FROM","1");
				map.put("sqlMapId", "selectEBWMOrderData");
				List<Map<String,Object>> bdResultList = openService.queryForList(map);
				if(bdResultList.size()!=0){
					for(Map<String,Object> o:bdResultList){
						o.put("SOURCENAME", "百度外卖");
						returnData.add(o);
					}
				}
				//订单数量
				map.put("sqlMapId", "loadEBOrderNumber");
				List<Map<String,Object>> bdNumberResultList = openService.queryForList(map);
				if(bdNumberResultList.size()!=0){
					for(Map<String,Object> o:bdNumberResultList){
						if(o.containsKey("WM_ORDER_STATE")&&o.get("WM_ORDER_STATE").equals("1")){
							noConfirmNumber = noConfirmNumber + Integer.parseInt(o.get("ORDER_NUMBER").toString());
						}else if(o.containsKey("WM_ORDER_STATE")&&o.get("WM_ORDER_STATE").equals("9")){
							isFinishNumber = isFinishNumber+Integer.parseInt(o.get("ORDER_NUMBER").toString());
						}else if(o.containsKey("WM_ORDER_STATE")&&o.get("WM_ORDER_STATE").equals("5")){
							isConfirmNumber = isConfirmNumber + Integer.parseInt(o.get("ORDER_NUMBER").toString());
						}
					}
				}
			}
			//如果包含饿了么外卖端，加载饿了么外卖数据
			if(CheckArrayContainsValue(orderSourceArray,"饿了么")){
				map.put("ORDER_FROM","2");
				map.put("sqlMapId", "selectEBWMOrderData");
				List<Map<String,Object>> bdResultList = openService.queryForList(map);
				if(bdResultList.size()!=0){
					for(Map<String,Object> o:bdResultList){
						o.put("SOURCENAME", "饿了么");
						returnData.add(o);
					}
				}
				//订单数量
				map.put("sqlMapId", "loadEBOrderNumber");
				List<Map<String,Object>> eNumberResultList = openService.queryForList(map);
				if(eNumberResultList.size()!=0){
					for(Map<String,Object> o:eNumberResultList){
						if(o.containsKey("WM_ORDER_STATE")&&o.get("WM_ORDER_STATE").equals("1")){
							noConfirmNumber = noConfirmNumber + Integer.parseInt(o.get("ORDER_NUMBER").toString());
						}else if(o.containsKey("WM_ORDER_STATE")&&o.get("WM_ORDER_STATE").equals("9")){
							isFinishNumber = isFinishNumber+Integer.parseInt(o.get("ORDER_NUMBER").toString());
						}else if(o.containsKey("WM_ORDER_STATE")&&o.get("WM_ORDER_STATE").equals("5")){
							isConfirmNumber = isConfirmNumber + Integer.parseInt(o.get("ORDER_NUMBER").toString());
						}
					}
				}
			}
			//如果包含美团外卖端，加载美团外卖数据
			if(CheckArrayContainsValue(orderSourceArray,"美团外卖")){
				
			}
			numberData.put("noConfirmNumber",noConfirmNumber);
			numberData.put("isFinishNumber",isFinishNumber);
			numberData.put("isConfirmNumber",isConfirmNumber);
			returnMap.put("orderData", returnData);
			returnMap.put("orderNumber", numberData);
			output("0000",returnMap);
		} catch (ExceptionVo e) {
			e.printStackTrace();
			output("9999","没有外卖订单数据");
		} catch (Exception e) {
			e.printStackTrace();
			output("9999","没有外卖订单数据");
		}
	}
	/**
	* <p>Title: loadWMDetailsDataByOrderPk</p>
	* <p>Description:加载外卖订单详情数据 </p>
	*/
	@RequestMapping(value = "/Order_select_loadWMDetailsDataByOrderPk", method = RequestMethod.POST)
	public void loadWMDetailsDataByOrderPk(){
		try {
			Map<String,Object> map = getParameterMap();
			String partName = map.get("partName").toString();
			if(partName.equals("智慧云")){
				map.put("sqlMapId", "selectZHYOrderDetailByPk");
				List<Map<String,Object>> orderList = openService.queryForList(map);
				String sendTime = orderList.get(0).get("WM_ORDER_SEND_TIME").toString();
				String createTime = orderList.get(0).get("CREATE_TIME").toString();
				List<Map<String,Object>> goodsList = ((List<Map<String,Object>>)orderList.get(0).get("orderDetailList"));
				int goodsNumber = 0;
				for(Map<String,Object> goods : goodsList){
					if(goods.containsKey("ORDER_DETAILS_FS")){
						goodsNumber = goodsNumber+Integer.parseInt(goods.get("ORDER_DETAILS_FS").toString());
					}
				}
				if(sendTime!=null &&!sendTime.equals("")){
					String sendTimePeriod = jTime(sendTime).substring(11,16)+"-"+sendTime.substring(11,16);
					orderList.get(0).put("WM_ORDER_SEND_TIME",sendTimePeriod);
					orderList.get(0).put("CREATE_TIME",createTime.substring(11, 16));
				}
				orderList.get(0).put("GOODS_NUMBER",goodsNumber);
				output("0000", orderList);
			}
		} catch (ExceptionVo e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			output("9999", "无法加载外卖订单详情数据");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			output("9999", "无法加载外卖订单详情数据");
		}
		 
	}
	private String jTime(String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DATE_TIME_FMT);
		try {
			Date date = sdf.parse(dateStr);
			Long time = date.getTime()-45*60*1000;
			return sdf.format(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
						map.put("sqlMapId", "updateTablesIsUseStatusByTableId");
						map.put("TABLES_ISUSE", 1);
						if (!openService.update(map)) {
							throw new RuntimeException();
						}
						

						txManagerController.commit();
						output("0000", "创建成功");
						//通知客户端创建订单
//						systemWebSocketHandler.sendMessageToUser(map.get("FK_SHOP").toString(),new TextMessage(MessageType.UPDATE_ORDERDATA));
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
	public static void main(String[] args) {
		System.out.println(new OrderController().jTime("2018-12-04 15:30:00").substring(11,16));
	}
	
	
	/**
	 * 
	 * @author lps
	 * @return void
	 * @date 2018年12月05日03:47:20
	 * @description:订单加菜-本地购物车
	 */
	@RequestMapping(value = "/Order_insert_OrderAddGoods", method = RequestMethod.POST)
	public void OrderAddGoods() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("FK_USER").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("FK_SHOP") || StringUtils.isBlank(map.get("FK_SHOP").toString())) {
					output("9999", "shopid无效");
					return;
				}
				//获取购物车信息
				Map<String, Object> shoppingCart = (Map<String, Object>)JSON.parse((String)map.get("SHOPPING_CART"));
				
				txManagerController.createTxManager();
				
				for (Map<String, Object> good : (List<Map<String, Object>>)shoppingCart.get("goods")) {
					Map<String, Object> goodMap = new HashMap<String,Object>();
					goodMap.put("FK_ORDER", map.get("ORDER_PK"));
					goodMap.put("FK_SHOP", map.get("FK_SHOP").toString());
					goodMap.put("ORDER_DETAILS_GNAME", good.get("GOODS_NAME").toString());
					goodMap.put("ORDER_DETAILS_FS", good.get("GOODS_NUMBER").toString());
					Object price;
					if(good.containsKey("GOODS_TRUE_PRICE")) {
						price = good.get("GOODS_TRUE_PRICE");
					}else {
						price = good.get("GOODS_PRICE");
					}
					goodMap.put("ORDER_DETAILS_GMONEY", price);
					goodMap.put("ORDER_DETAILS_FORMAT", good.get("GOODS_FORMAT").toString());
					goodMap.put("ORDER_DETAILS_TASTE", good.get("GOODS_TASTE").toString());
					goodMap.put("ORDER_DETAILS_MAKING", good.get("GOODS_MAKING").toString());
					goodMap.put("ORDER_DETAILS_DW", good.get("GOODS_DW").toString());
					goodMap.put("FK_GOODS", good.get("GOODS_PK").toString());
					goodMap.put("CREATE_BY", map.get("FK_USER").toString());
					
					goodMap.put("sqlMapId", "insertCartOrderDeatilInfo");
					String result1 = openService.insert(goodMap);
					if (result1 == null) {
						throw new RuntimeException();
					}
					
				}
				
				map.put("sqlMapId", "updateOrderOrderYfmoneyByOrderId");
				map.put("opera","1");
				map.put("operaMoney", shoppingCart.get("totalMoney"));
				
				if(!openService.update(map)) {
					throw new RuntimeException();
				}
				
				txManagerController.commit();
				output("0000", "已加菜");
				return;
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			logger.error("error", e);
			txManagerController.rollback();
		}
		output("9999", "系统错误");
		return;
	}
	
	
	
	/**
	 * 
	 * @author lps
	 * @return void
	 * @date 2018年12月05日01:59:14
	 * @description:根据购物车创建订单
	 */
	@RequestMapping(value = "/Order_insert_shoppingcreateOrder", method = RequestMethod.POST)
	public void shoppingCreateOrder() {
		Map<String, Object> map = null;
		try {
			map = getParameterMap();
			System.out.println("");
			
			Map<String, Object> orderMap =  (Map<String, Object>)JSON.parse((String)map.get("SHOPPING_CART"));
			Map<String, Object> tableMap = (Map<String, Object>)orderMap.get("table");
			
			Map<String, Object> checkMap = getParameterMap();
			//TODO 查询设置的，查询选择开台了没，选择开台就不差啊桌位被使用了
			checkMap.put("sqlMapId", "findTablesById");
			checkMap.put("TABLES_ID", tableMap.get("TABLES_PK"));
			checkMap = (Map<String, Object>)openService.queryForObject(checkMap);
			if (checkMap.containsKey("TABLES_ISUSE") && "1".equals(checkMap.get("TABLES_ISUSE"))) {
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
				List<Map<String, Object>> goodsList = (List<Map<String, Object>>)orderMap.get("goods");
				if(goodsList == null || goodsList.size() < 1) {
					output("9999", "购物车没有商品");
					return;
				}
				
				Map<String, Object> insertOrderMap = new HashMap<>();
				insertOrderMap.put("ORDER_POSITION", tableMap.get("TABLES_PK").toString());
				insertOrderMap.put("ORDER_CODE", this.getOrderCode(map));
				insertOrderMap.put("ORDER_RS", orderMap.get("personNum").toString());
				insertOrderMap.put("CREATE_BY", map.get("FK_USER").toString());
				insertOrderMap.put("FK_USER", map.get("FK_USER").toString());
				insertOrderMap.put("FK_SHOP", map.get("FK_SHOP").toString());
				insertOrderMap.put("ORDER_YFMONEY", orderMap.get("totalMoney").toString());
				insertOrderMap.put("ORDER_DIVISION", map.get("ORDER_DIVISION").toString());
				insertOrderMap.put("sqlMapId", "insertCartOrderInfo");
				txManagerController.createTxManager();
				String result = openService.insert(insertOrderMap);
				if(result == null) {
					throw new RuntimeException();
				}
				//循环插入订单详情信息
				for (Map<String, Object> good : goodsList) {
					Map<String, Object> goodMap = new HashMap<String,Object>();
					goodMap.put("FK_ORDER", result);
					goodMap.put("FK_SHOP", map.get("FK_SHOP").toString());
					goodMap.put("ORDER_DETAILS_GNAME", good.get("GOODS_NAME").toString());
					goodMap.put("ORDER_DETAILS_FS", good.get("GOODS_NUMBER").toString());
					Object price;
					if(good.containsKey("GOODS_TRUE_PRICE")) {
						price = good.get("GOODS_TRUE_PRICE");
					}else {
						price = good.get("GOODS_PRICE");
					}
					goodMap.put("ORDER_DETAILS_GMONEY", price);
					Object goodsFormat = good.get("GOODS_FORMAT");
					if(goodsFormat != null) {
						goodMap.put("ORDER_DETAILS_FORMAT", goodsFormat.toString());
					}
					Object goodsTaste = good.get("GOODS_TASTE");
					if(goodsTaste != null) {
						goodMap.put("ORDER_DETAILS_TASTE", goodsTaste.toString());
					}
					Object goodsMaking = good.get("ORDER_DETAILS_MAKING");
					if(goodsMaking != null) {
						goodMap.put("ORDER_DETAILS_MAKING", goodsMaking.toString());
					}
					
					goodMap.put("ORDER_DETAILS_DW", good.get("GOODS_DW").toString());
					goodMap.put("FK_GOODS", good.get("GOODS_PK").toString());
					goodMap.put("CREATE_BY", map.get("FK_USER").toString());
					
					goodMap.put("sqlMapId", "insertCartOrderDeatilInfo");
					String result1 = openService.insert(goodMap);
					if (result1 == null) {
						throw new RuntimeException();
					}
					
				}
				
				//更改桌位为已使用
				Map<String, Object> updateTableMap = new HashMap<String, Object>();
				updateTableMap.put("sqlMapId", "updateTablesIsUseStatusByTableId");
				updateTableMap.put("TABLES_ISUSE", 1);
				updateTableMap.put("TABLES_PK", tableMap.get("TABLES_PK"));
				if (!openService.update(updateTableMap)) {
					throw new RuntimeException();
				}
				
				txManagerController.commit();
				output("0000", result);
				//通知客户端创建订单  lps通知客户端来订单了
				//不能放在这，用户收不到信息我就返回错误了
				
			}else {
				output("9999", "USER参数无效");
				return;
			}
		} catch (Exception e) {
			logger.error("Order_insert_createOrder error", e);
			txManagerController.rollback();
			output("9999", "操作失败");
		}
//		systemWebSocketHandler.sendMessageToUser(map.get("FK_SHOP").toS tring(),new TextMessage(MessageType.UPDATE_ORDERDATA));
		return;
	}
	
	@RequestMapping(value = "/Order_update_cancelOrder", method = RequestMethod.POST)
	public void cancelOrder(){
		try {
			Map<String, Object> map = getParameterMap();
			//判断当前用户是否是店员，否则不具有退单的操作
			map.put("sqlMapId", "selectUserRoleByShopIdAndOpenId");
			Map<String,String> userRoleMap = (Map<String,String>)openService.queryForObject(map);
//			if(userRoleMap == null || !"6".equals(userRoleMap.get("FK_ROLE"))) {//说明权限不足，return
//				logger.info("没有退单的权限");
//				output("9999", "退单失败");
//				return;
//			}
			map.put("ORDER_STATE", "1");
			map.put("sqlMapId", "updateOrderState");
			boolean updateResult = openService.update(map);
			if(updateResult){
				output("0000", "退单成功");
				return;
			}
			output("9999", "退单失败");
			
		} catch (ExceptionVo e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @author lps
	 * @date Dec 5, 2018 7:46:51 PM 
	 * 
	 * @description: 用户端支付
	 * @return void
	 * @throws ExceptionVo 
	 * 
	 */
	@RequestMapping(value = "/Order_update_OrderShopSettleAccounts", method = RequestMethod.POST)
	public void OrderShopSettleAccounts() throws ExceptionVo {
		//TODO
		//先查询该用户对该订单有没有操作的权限  优惠支付，等一些东西都没弄好
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId","selectOrderPayStatusByOrderId");
			
			Map<String, Object> payStatusMap = (Map<String, Object>)openService.queryForObject(map);
			if(payStatusMap != null) {
				String payStatus = (String)payStatusMap.get("ORDER_PAY_STATE");
				if(payStatus!= null && "1".equals(payStatus)) {
					output("9999","订单已支付，请勿重复提交订单");
					return;
				}
			}
			
			
			txManagerController.createTxManager();
			
			//OPEN_ID或者USER_PK， SHOP_FK
			map.put("sqlMapId", "selectUserRoleByShopIdAndOpenId");
			Map<String,String> userRoleMap = (Map<String,String>)openService.queryForObject(map);
			if(userRoleMap == null || "2".equals(userRoleMap.get("FK_ROLE"))) {//说明权限不足，return
				System.out.println("权限不足");
				//return;		//现在是测试，先注释
			}
			//TODO 支付前未验证价格信息，需验证
			String payWay = (String)map.get("payWay");
			String payWayList[] = {"1","20","30","4","5","6","21","22","31","32"};
			int payWayIndex = -1;
			for (int i=0;i<payWayList.length;i++) {
				if(payWayList[i].equals(payWay)) {
					payWayIndex = i;
				}
			}
			if(payWayIndex == -1) {
				return;
			}
			
			
			
			//先判断是不是储值支付
			if(payWay.equals("5")) {
				//储值支付，开始吧
			}
			
			
			map.put("sqlMapId", "updateOrderPayWayAndStatusByOrderId");
			map.put("ORDER_PAY_WAY", payWay);
			if(payWayIndex < 6) {//都不需要后续操作，直接支付成功
				map.put("ORDER_PAY_STATE", 1);
			}else {
				map.put("ORDER_PAY_STATE", 0);
			}
			if(!openService.update(map)) {
				throw new RuntimeException();
			}
			txManagerController.commit();
			output("0000", "成功");
			return;
		} catch (Exception e) {
			txManagerController.rollback();
			logger.error(e);
			output("0000","操作失败");
		}
	}
	
}
