package cn.wifiedu.ssm.controller;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.waimai.down.Result;
import cn.wifiedu.ssm.util.waimai.EBWaiMai;
import cn.wifiedu.ssm.util.waimai.SignUtil;

		/**
		 * 
		 * @author lps
		 * @date 2018年9月20日 下午8:01:38
		 * @Description:	外卖相关
		 * @version V1.0
		 *
		 */
		@Controller
		@Scope("prototype")
		public class WaiMaiController extends BaseController {

			private static Logger logger = Logger.getLogger(WaiMaiController.class);

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
			 * 
			 * @date 2018年9月20日 下午8:01:26 
			 * @author lps
			 * 
			 * @Description:  美团外卖云端心跳回调URL
			 * @return void 
			 *
			 */
			@RequestMapping("/test/MT_Heartbeat_CallBack")
			public void heartbeatCallBack(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				
				Map map = null;
				try {
					map = getParameterMap();
					logger.info("----------MT_Heartbeat_CallBack----------");
					logger.info(map);
					

					reponse.getWriter().write("{\"data\":\"success\"}");
					return;
				} catch (Exception e) {
					logger.error("-------------MT_Heartbeat_CallBack fail-------------------");
					logger.error(map);
					logger.error(e);
				}
				
				
			}
			
			
			/**
			 * 
			 * @date 2018年9月20日 下午8:17:49 
			 * @author lps
			 * 
			 * @Description:  美团订单推送
			 * @return void 
			 *
			 */
			@RequestMapping("/test/MT_Push_Order")
			public void pushOrder(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				Map map = null;
				try {
					map = getParameterMap();
					

					BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
					StringBuffer sb = new StringBuffer("");
					String temp;
					while ((temp = br.readLine()) != null) {
					sb.append(temp);
					}
					br.close();
					String params = sb.toString();
					logger.info("-------------MT_Push_Order-------------------");
					logger.info(params);
					
					logger.info("----------MT_Push_Order----------");
					logger.info(map);
					
					reponse.getWriter().write("{\"data\":\"success\"}");
					return;
				} catch (Exception e) {
					logger.error("-------------MT_Push_Order fail-------------------");
					logger.error(map);
					logger.error(e);
				}
			}
			
			/**
			 * 
			 * @date 2018年9月23日 上午12:36:48 
			 * @author lps
			 * 
			 * @Description: 美团店铺映射
			 * @param request
			 * @param seesion
			 * @param reponse 
			 * @return void 
			 *
			 */
			@RequestMapping("/test/MT_Shop_Map")
			public void shopMap(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				Map map = null;
				try {
					map = getParameterMap();
					
//					BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
//					StringBuffer sb = new StringBuffer("");
//					String temp;
//					while ((temp = br.readLine()) != null) {
//					sb.append(temp);
//					}
//					br.close();
//					String params = sb.toString();
//					logger.info("-------------MD-------------------");
//					logger.info(params);
					
					logger.info("----------MDYS----------");
					map.put("sqlMapId", "insertMtShopMapping");
					String insert = openService.insert(map);
					if(insert == null) {
						return;
					}
					logger.info(map);
					reponse.getWriter().write("{\"data\":\"success\"}");
					return;
				} catch (Exception e) {
					logger.error("-------------MTYS fail-------------------");
					logger.error(map);
					logger.error(e);
				}
			}
			
			
			/**
			 * 
			 * @date 2018年9月23日 上午12:37:07 
			 * @author lps
			 * 
			 * @Description: 取消店铺映射
			 * @param request
			 * @param seesion
			 * @param reponse 
			 * @return void 
			 *
			 */
			@RequestMapping("/test/MT_Shop_RelieveMap")
			public void shopRelieveMap(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				Map map = null;
				try {
					map = getParameterMap();
					
					logger.info("----------MDYSunun----------");
					map.put("sqlMapId", "deleteMtShopMappingByShopId");
					boolean delete = openService.delete(map);
					if(!delete) {
						return;
					}
					logger.info(map);
					reponse.getWriter().write("{\"data\":\"success\"}");
					return;
				} catch (Exception e) {
					logger.error("-------------MTcancalYS fail-------------------");
					logger.error(map);
					logger.error(e);
				}
			}
			
			
			
			/**
			 * 
			 * @author lps
			 * @date 2018年10月5日 下午4:01:16 
			 * 
			 * @description: 美团外卖取消订单
			 * @return void
			 */
			@RequestMapping("/test/MT_Cancel_Order")
			public void cancelOrder(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				Map map = null;
				try {
					map = getParameterMap();
					
					BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
					StringBuffer sb = new StringBuffer("");
					String temp;
					while ((temp = br.readLine()) != null) {
					sb.append(temp);
					}
					br.close();
					String params = sb.toString();
					logger.info("-------------MDCancel-------------------");
					logger.info(params);
					
					logger.info("----------MDCancel----------");
					logger.info(map);
					reponse.getWriter().write("{\"data\":\"success\"}");
					return;
				} catch (Exception e) {
					logger.error("-------------MDCancel fail-------------------");
					logger.error(map);
					logger.error(e);
				}
			}
			
			
			/**
			 * 
			 * @author lps
			 * @date 2018年10月5日 下午4:01:16 
			 * 
			 * @description: 美团外卖订单完成
			 * @return void
			 */
			@RequestMapping("/test/MT_Order_Finish")
			public void MTOrderFinish(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				Map map;
				try {
					map = getParameterMap();
					
					BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
					StringBuffer sb = new StringBuffer("");
					String temp;
					while ((temp = br.readLine()) != null) {
					sb.append(temp);
					}
					br.close();
					String params = sb.toString();
					logger.info("-------------MT_Order_Finish-------------------");
					logger.info(params);
					
					logger.info("----------MT_Order_Finish----------");
					logger.info(map);
					reponse.getWriter().write("{\"data\":\"success\"}");
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			/**
			 * 
			 * @author lps
			 * @date 2018年10月5日 下午4:01:16 
			 * 
			 * @description: 美团外卖订单确认
			 * @return void
			 */
			@RequestMapping("/test/MT_Order_Enter")
			public void MTOrderEnter(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				Map map;
				try {
					map = getParameterMap();
					
					BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
					StringBuffer sb = new StringBuffer("");
					String temp;
					while ((temp = br.readLine()) != null) {
					sb.append(temp);
					}
					br.close();
					String params = sb.toString();
					logger.info("-------------MT_Order_Enter-------------------");
					logger.info(params);
					
					logger.info("----------MT_Order_Enter----------");
					logger.info(map);
					reponse.getWriter().write("{\"data\":\"success\"}");
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			
			/**
			 * 
			 * @author lps
			 * @date 2018年10月5日 下午4:01:16 
			 * 
			 * @description: 美团外卖订单确认
			 * @return void
			 */
			@RequestMapping("/test/MT_Order_Status")
			public void MTOrderStatus(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				Map map;
				try {
					map = getParameterMap();
					
					BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
					StringBuffer sb = new StringBuffer("");
					String temp;
					while ((temp = br.readLine()) != null) {
					sb.append(temp);
					}
					br.close();
					String params = sb.toString();
					logger.info("-------------MT_Order_Status-------------------");
					logger.info(params);
					
					logger.info("----------MT_Order_Status----------");
					logger.info(map);
					reponse.getWriter().write("{\"data\":\"success\"}");
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			
			
			/**
			 * 
			 * @author lps
			 * @date 2018年10月5日 下午9:24:52 
			 * 
			 * @description: 饿了么回调
			 * @return void
			 */
			@RequestMapping("/ele/sandbox/Push_Message")
			public void eleSandboxPushMessage(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				Map map;
				try {
					map = getParameterMap();
					
					BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
					StringBuffer sb = new StringBuffer("");
					String temp;
					while ((temp = br.readLine()) != null) {
					sb.append(temp);
					}
					br.close();
					String params = sb.toString();
					logger.info("-------------elePushMessage-------------------");
					logger.info(params);
					
//					logger.info("----------MDCancel----------");
//					logger.info(map);
//					reponse.getWriter().write("{\"data\":\"success\"}");
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			
			
			
			/**
			 * 
			 * @author lps
			 * @date 2018年10月5日 下午9:26:13 
			 * 
			 * @description: 饿百测试
			 * @return void
			 */
			@RequestMapping("eb/test/Push_Message")
			public void EBTestPushMessage(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				Map map;
				try {
					map = getParameterMap();
					
					if(map.containsKey("OperatingSystem")) {
						map.remove("OperatingSystem");
					}
					if(map.containsKey("AccessIp")) {
						map.remove("AccessIp");
					}
					if(map.containsKey("sessionId")) {
						map.remove("sessionId");
					}
					if(map.containsKey("Browser")) {
						map.remove("Browser");
					}
					
					logger.info("----------elebaidutestPushMessage-map----------");
					logger.info(map);
					
					Map<String, Object> config = new HashMap<String, Object>();
		            config.put("secret", "ab1a243587a5c2bd");
		            //校验sign是否正确
		            boolean flag = SignUtil.checkSign(map, config);
	                //设置返回结果
	                Result res = new Result(map);

	                if ("order.create".equals(map.get("cmd"))) {
	                	//从推送的信息取出订单
                    	String orderId = JSON.parseObject(map.get("body").toString()).getString("order_id");
                    	 //后续操作
                    	String ZHYOrderId = "";
	                    if(flag) {
	                    	//开始查询订单详细信息，然后插入数据库，并推送给用户
	                    	//发送请求取订单详细信息
	                    	String ebOrderMessage = EBWaiMai.EBOrderGet(orderId);
	                    	//订单详细信息结果集转换 json->object
	                    	Map parse = (Map)JSON.parse(ebOrderMessage);
	        				Map reBody = (Map)parse.get("body");
	        				//判断获取订单返回码
	        				if(!"0".equals(reBody.get("errno").toString())) {
	        					throw new ExceptionVo("get Order Message Result error:", ebOrderMessage);
	        				}
	        				Map reData = (Map)reBody.get("data");
	                    	Map reShop = (Map)reData.get("shop");
	                    	Map reOrder = (Map)reData.get("order");
	                    	Map reUser = (Map)reData.get("user");
	                    	List reProducts = (List)reData.get("products");
	                    	
	                    	//先插入到数据库
	                    	Map<String, Object> beMap = new HashMap<String,Object>();
	                    	beMap.put("sqlMapId", "insertWaimaiOrder");
	                    	beMap.put("ORDER_ID", orderId);
	                    	beMap.put("SHOP_BAIDU_ID", reShop.get("baidu_shop_id"));
	                    	beMap.put("ORDER_FROM", reOrder.get("order_from"));
	                    	beMap.put("ORDER_SEND_IMMEDIATELY", reOrder.get("send_immediately"));
	                    	beMap.put("ORDER_STATUS", reOrder.get("status"));
	                    	beMap.put("ORDER_STATUS_TIME", reOrder.get("create_time"));
	                    	beMap.put("ORDER_REMARK", reOrder.get("remark"));
	                    	beMap.put("ORDER_PACKAGE_FEE", reOrder.get("package_fee"));
	                    	beMap.put("ORDER_SEND_FEE", reOrder.get("send_fee"));
	                    	beMap.put("ORDER_DISCOUNT_FEE", reOrder.get("discount_fee"));
	                    	beMap.put("ORDER_USER_FEE", reOrder.get("user_fee"));
	                    	beMap.put("ORDER_TOTAL_FEE", reOrder.get("total_fee"));
	                    	beMap.put("ORDER_SHOP_FEE", reOrder.get("shop_fee"));
	                    	beMap.put("ORDER_SEND_TIME", reOrder.get("send_time"));
	                    	beMap.put("ORDER_CREATE_TIME", reOrder.get("create_time"));
	                    	
	                    	beMap.put("USER_NAME", reUser.get("name"));
	                    	beMap.put("USER_PHONE", reUser.get("phone"));
	                    	beMap.put("USER_GENDER", reUser.get("gender"));
	                    	beMap.put("USER_ADDRESS", reUser.get("address"));
	                    	
	                    	beMap.put("SHOP_NAME", reShop.get("name"));
	                    	beMap.put("PRODUCTS", reProducts.toString());
	                    	beMap.put("CREATE_BY", "admin");
	                    	
	                    	
	                    	ZHYOrderId = openService.insert(beMap);
	                    	
	                    	
	                    }
	                  //创建返回信息
	                    res.setCreateResult(config, flag, ZHYOrderId);
	                	
	                   
	                } else if ("order.status.push".equals(map.get("cmd"))) {
	                	//创建返回信息
	                    res.setPushStatusResult(config, flag);
	                    //后续操作
	                    if(flag) {
	                    	
	                    }
	                } else if ("order.user.cancel".equals(map.get("cmd"))) {
	                	//创建返回信息
	                    res.setCancelResult(config, flag);
	                    //后续操作
	                    if(flag) {
	                    	
	                    }
	                } else if ("order.partrefund.push".equals(map.get("cmd"))) {
	                	//创建返回信息
	                    res.setPartRefundPushResult(config, flag);
	                    //后续操作
	                    if(flag) {
	                    	
	                    }
	                }
	                
	                //将结果转化为json返回
	               
	                String result = JSON.toJSON(res.getResult()).toString();
	                reponse.getWriter().write(result);
					
					return;
				} catch (Exception e) {
					logger.error("EBTestPushMessage error");
					logger.error(e.toString());
					e.printStackTrace();
				}
			}
			
			
			/**
			 * 
			 * @author lps
			 * @date 2018年10月5日 下午9:26:13 
			 * 
			 * @description: 饿百
			 * @return void
			 */
			@RequestMapping("eb/Push_Message")
			public void EBPushMessage(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				Map map;
				try {
					map = getParameterMap();
					
					if(map.containsKey("OperatingSystem")) {
						map.remove("OperatingSystem");
					}
					if(map.containsKey("AccessIp")) {
						map.remove("AccessIp");
					}
					if(map.containsKey("sessionId")) {
						map.remove("sessionId");
					}
					if(map.containsKey("Browser")) {
						map.remove("Browser");
					}
					
					logger.info("----------elebaiduPushMessage-map----------");
					logger.info(map);
					
					
					Map<String, Object> config = new HashMap<String, Object>();
		            config.put("secret", "ab1a243587a5c2bd");
		            //校验sign是否正确
		            boolean flag = SignUtil.checkSign(map, config);
	                //设置返回结果
	                Result res = new Result(map);
	                
	                //订单下行 - order.create-创建订单
	                if ("order.create".equals(map.get("cmd"))) {
	                	String ZHYOrderId = orderCreate(map,flag);
	                  //创建返回信息
	                    res.setCreateResult(config, flag, ZHYOrderId);
	                } else if ("order.status.push".equals(map.get("cmd"))) {
	                    res.setPushStatusResult(config, flag);
	                } else if ("order.user.cancel".equals(map.get("cmd"))) {
	                    res.setCancelResult(config, flag);
	                } else if ("order.partrefund.push".equals(map.get("cmd"))) {
	                    res.setPartRefundPushResult(config, flag);
	                }else if ("shop.bind.msg".equals(map.get("cmd"))) {//门店绑定信息推送
	                    res.setPartRefundPushResult(config, flag);
	                }else if ("shop.unbind.msg".equals(map.get("cmd"))) {//门店解绑消息推送
	                    res.setPartRefundPushResult(config, flag);
	                }else {
	                	return;
	                }
	                
	                //将结果转化为json返回
	                String result = JSON.toJSON(res.getResult()).toString();
	                reponse.getWriter().write(result);
					
					
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			
			//订单下行 - order.create-创建订单
			public String orderCreate(Map map,boolean flag) throws Exception {
				//从推送的信息取出订单
            	String orderId = JSON.parseObject(map.get("body").toString()).getString("order_id");
            	 //后续操作
            	String ZHYOrderId = "";
                if(flag) {
                	//开始查询订单详细信息，然后插入数据库，并推送给用户
                	//发送请求取订单详细信息
                	String ebOrderMessage = EBWaiMai.EBOrderGet(orderId);
                	//订单详细信息结果集转换 json->object
                	Map parse = (Map)JSON.parse(ebOrderMessage);
    				Map reBody = (Map)parse.get("body");
    				//判断获取订单返回码
    				if(!"0".equals(reBody.get("errno").toString())) {
    					throw new ExceptionVo("get Order Message Result error:", ebOrderMessage);
    				}
    				Map reData = (Map)reBody.get("data");
                	Map reShop = (Map)reData.get("shop");
                	Map reOrder = (Map)reData.get("order");
                	Map reUser = (Map)reData.get("user");
                	List reProducts = (List)reData.get("products");
                	
                	//先插入到数据库
                	Map<String, Object> beMap = new HashMap<String,Object>();
                	beMap.put("sqlMapId", "insertWaimaiOrder");
                	beMap.put("ORDER_ID", orderId);
                	beMap.put("SHOP_BAIDU_ID", reShop.get("baidu_shop_id"));
                	beMap.put("ORDER_FROM", reOrder.get("order_from"));
                	beMap.put("ORDER_SEND_IMMEDIATELY", reOrder.get("send_immediately"));
                	beMap.put("ORDER_STATUS", reOrder.get("status"));
                	beMap.put("ORDER_STATUS_TIME", reOrder.get("create_time"));
                	beMap.put("ORDER_REMARK", reOrder.get("remark"));
                	beMap.put("ORDER_PACKAGE_FEE", reOrder.get("package_fee"));
                	beMap.put("ORDER_SEND_FEE", reOrder.get("send_fee"));
                	beMap.put("ORDER_DISCOUNT_FEE", reOrder.get("discount_fee"));
                	beMap.put("ORDER_USER_FEE", reOrder.get("user_fee"));
                	beMap.put("ORDER_TOTAL_FEE", reOrder.get("total_fee"));
                	beMap.put("ORDER_SHOP_FEE", reOrder.get("shop_fee"));
                	beMap.put("ORDER_SEND_TIME", reOrder.get("send_time"));
                	beMap.put("ORDER_CREATE_TIME", reOrder.get("create_time"));
                	
                	beMap.put("USER_NAME", reUser.get("name"));
                	beMap.put("USER_PHONE", reUser.get("phone"));
                	beMap.put("USER_GENDER", reUser.get("gender"));
                	beMap.put("USER_ADDRESS", reUser.get("address"));
                	
                	beMap.put("SHOP_NAME", reShop.get("name"));
                	beMap.put("PRODUCTS", reProducts.toString());
                	beMap.put("CREATE_BY", "admin");
                	
                	ZHYOrderId = openService.insert(beMap);
                	return ZHYOrderId;
                }
                return null;
			}
			
			
			
			public static void main(String[] args) {
				
				
				String s = "{\"body\":{\"errno\":0,\"error\":\"success\",\"data\":{\"source\":\"62863\",\"shop\":{\"id\":\"test_781544_62863\",\"name\":\"\\u738b\\u666f\\u9f99\\u5927\\u6392\\u6863\",\"baidu_shop_id\":\"2234526556\"},\"order\":{\"order_from\":\"2\",\"cold_box_fee\":\"0\",\"eleme_order_id\":\"2100170426395197452\",\"order_flag\":0,\"ext\":{\"taoxi_flag\":0},\"is_cold_box_order\":0,\"expect_time_mode\":1,\"pickup_time\":0,\"atshop_time\":0,\"delivery_time\":0,\"delivery_phone\":\"\",\"finished_time\":\"0\",\"confirm_time\":\"1539718241\",\"meal_num\":\"\",\"commission\":0,\"order_id\":\"15397181282021\",\"order_index\":\"3\",\"status\":10,\"send_immediately\":1,\"send_time\":\"1\",\"send_fee\":0,\"package_fee\":0,\"total_fee\":1,\"shop_fee\":1,\"user_fee\":1,\"responsible_party\":\"\\u9910\\u5385\",\"down_flag\":0,\"pay_type\":2,\"pay_status\":2,\"need_invoice\":2,\"invoice_title\":\"\",\"taxer_id\":\"\",\"remark\":\"\",\"delivery_party\":6,\"create_time\":\"1539718127\",\"cancel_time\":\"1539718272\",\"is_private\":0,\"discount_fee\":0},\"user\":{\"name\":\"\\u9646\\u4e15\\u5c71\",\"phone\":\"17865218840\",\"gender\":1,\"address\":\"\\u6d4e\\u5b81\\u5b66\\u9662\\u6587\\u5316\\u9152\\u5e971\\u697c\\u5317\\u95e8\",\"province\":null,\"city\":null,\"district\":null,\"coord\":{\"longitude\":116.962363,\"latitude\":35.557141}},\"products\":[[{\"baidu_product_id\":\"1539717705337067\",\"upc\":\"wm85535096914917\",\"custom_sku_id\":\"\",\"product_name\":\"\\u874c\\u86aa\\u5543\\u8721\",\"product_type\":1,\"product_price\":1,\"product_amount\":1,\"product_fee\":1,\"package_price\":0,\"package_amount\":0,\"package_fee\":0,\"total_fee\":1,\"product_attr\":[],\"product_features\":[],\"product_custom_index\":\"1539717705337067_0_0\",\"product_subsidy\":{\"discount\":0,\"baidu_rate\":0,\"shop_rate\":0,\"user_rate\":0,\"agent_rate\":0,\"logistics_rate\":0},\"prescription_id\":\"\",\"supply_type\":0}]],\"discount\":[]}},\"cmd\":\"resp.order.get\",\"encrypt\":\"\",\"sign\":\"6C83EF3EF76117FB007547279064039C\",\"source\":\"62863\",\"ticket\":\"5001ABFE-43B1-C887-A7C7-EAF9F16F8D3D\",\"timestamp\":1539940745,\"version\":\"3\"}";
				Map parse = (Map)JSON.parse(s);
				Map reBody = (Map)parse.get("body");
				
				
				System.out.println("0".equals(reBody.get("errno").toString()));
				
				
				
				
				
//				//测试饿了么外卖
//				Account account = new Account("gkcwKMhwF8", "6a2a227c0d8df6bc8474c4c1b54ddb86659ecf3c");
//		        List<Account> accounts = new ArrayList<Account>();
//		        accounts.add(account);
//		        System.out.println("action");
//		        Config config = new Config(accounts,
//		                new BusinessHandle() {
//		                    @Override
//		                    public boolean onMessage(String message) {
//		                    	System.out.println("--message--");
//		                        System.out.println(message);
//		                        return true;
//		                    }
//		                },
//		                new ElemeSdkLogger() {
//		                    @Override
//		                    public void info(String message) {
//		                    	System.out.println("--your info log 处理 message--");
//		                        System.out.println(message);
//		                        //your info log 处理
//		                    }
//
//		                    @Override
//		                    public void error(String message) {
//		                    	System.out.println("--your error log 处理 message--");
//		                        System.out.println(message);
//		                        //your error log 处理
//		                    }
//		                }
//		        );
//		        try {
//		            Bootstrap.start(config);
//		        } catch (UnableConnectionException e) {
//		            e.printStackTrace();
//		        }
//				
//				
//				
			}
			
		
		}

