package cn.wifiedu.ssm.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.starpos.pay.StarPosPay;
import cn.wifiedu.ssm.starpos.pay.StartPosUtil;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.QRCode;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;
	
			/**
		 * 
		 * @author lps
		 * @Description:新大陆星POS支付
		 * @version V1.0
		 *
		 */
		@Controller
		@Scope("prototype")
		public class StarPosPayController extends BaseController {

			private static Logger logger = Logger.getLogger(StarPosPayController.class);

			@Resource
			OpenService openService;
			
			@Resource
			private JedisClient jedisClient;
			
			@Resource
			private StarPosPay starPosPay;
			
			@Resource
			private WxController wxController;
			
			@Resource
			private PrinterController printerController;
			
			public OpenService getOpenService() {
				return openService;
			}

			public void setOpenService(OpenService openService) {
			this.openService = openService;
			}
			
			/**
			 * 
			 * @date 22018年8月29日23:06:04
			 * @author lps
			 * 
			 * @Description: 星pos支付异步通知
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			
			@RequestMapping(value = "/starPosPay_async_notify", method = RequestMethod.POST)
			public void findAgentInfoById(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				String params = null;
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "gbk"));
					StringBuffer sb = new StringBuffer("");
					String temp;
					while ((temp = br.readLine()) != null) {
					sb.append(temp);
					}
					br.close();
					params = sb.toString();
					logger.info("-------------pos-------------------");
					logger.info(params);
					
					Map<String, String> reMap = (Map<String, String>)JSON.parse(params);
					
					//更新至数据库
					Map<String, Object> map = new HashMap<String, Object>();
					
					map.put("OPEN_ID", reMap.get("UserId"));
					map.put("NOTIFY_BAL_DATE", reMap.get("BalDate"));
					map.put("NOTIFY_TRADING_TIME", reMap.get("TxnDate") + reMap.get("TxnTime"));
					map.put("TRADE_NO", reMap.get("AgentId"));
					map.put("MERC_ID", reMap.get("BusinessId"));
					map.put("TRM_NO", reMap.get("SDTermNo"));
					map.put("NOTIFY_TXN_CODE", reMap.get("TxnCode"));
					map.put("NOTIFY_PAY_CHANNEL", reMap.get("PayChannel"));
					map.put("NOTIFY_TXNAMT", reMap.get("TxnAmt"));
					map.put("NOTIFY_TXN_STATUS", reMap.get("TxnStatus"));
					map.put("BANK_TYPE", JSON.toJSONString(reMap.get("BankType")));
					map.put("OFFICE_ID", reMap.get("OfficeId"));
					map.put("SEL_ORDER_NO", reMap.get("ChannelId"));
					map.put("CRD_FLG", JSON.toJSONString(reMap.get("CrdFlg")));
					map.put("LOG_NO", reMap.get("logNo"));
					map.put("UPDATE_BY", "admin");
					if("1".equals(reMap.get("TxnStatus"))) {
						map.put("RETURN_RESULT", "S");
						map.put("RETURN_MESSAGE", "交易成功");
					}
					
					map.put("sqlMapId", "updateStarPosPayByLogNo");
					boolean update = openService.update(map);
					if(!update){
						logger.error("更新失败，新大陆异步通知返回结果插入数据库失败");
						throw new Exception("更新失败，新大陆异步通知返回结果插入数据库失败");
					}
					
					//回调url要传递的参数，为了保持和传入的时候参数名一致，所以新生成一个map转换
					Map<String, Object> postMap = new HashMap<String, Object>();
					postMap.put("mercId", reMap.get("BusinessId"));
					postMap.put("logNo", reMap.get("logNo"));
					postMap.put("openid", reMap.get("UserId"));
					postMap.put("tradingTime", reMap.get("TxnDate") + reMap.get("TxnTime"));
					postMap.put("officeId", reMap.get("OfficeId"));
					map.put("notifyTxnStatus", reMap.get("TxnStatus"));
					
					//判断有没有回调，有的话调用url
					if(jedisClient.isExit(RedisConstants.STARPOS_PAY_CALLBACK_URL + reMap.get("logNo"))
					&& StringUtils.isNotBlank(jedisClient.get(RedisConstants.STARPOS_PAY_CALLBACK_URL + reMap.get("logNo")))){
						String mess = jedisClient.get(RedisConstants.STARPOS_PAY_CALLBACK_URL + reMap.get("logNo"));//取出之前存在redis的map信息
						Map<String, Object> messMap = JSON.parseObject(mess);
						String callBackUrl = (String) messMap.get("callBackUrl");
						messMap.remove("callBackUrl");
						postMap.putAll(messMap);
						CommonUtil.posts(CommonUtil.getPath("project_url").replace("DATA", callBackUrl), JSON.toJSONString(postMap), "utf-8");
						jedisClient.del(RedisConstants.STARPOS_PAY_CALLBACK_URL + reMap.get("logNo"));
					}
					
					new Thread(new Runnable() {
						//TODO
						//提示用户收款多少钱
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
						}
					}).start();
					
//					output("000000","success                                                     ");
					//RspCode	RspDes
					reponse.getWriter().write("{\"RspCode\":\"000000\",\"RspDes\":\"success                                                     \"}");
					return;
				} catch (Exception e) {
					logger.error(e);
					logger.error("return message---->"+params);
					output("999999", " Exception ", e);
					return;
				}
			}
			
			
			/**
			 * 
			 * @author lps
			 * @date Nov 27, 2018 3:30:09 AM 
			 * 
			 * @description: 参数里边会有订单id
			 * @return void
			 */
			@RequestMapping(value = "/starPosPay_generatePayQRCode")
			public void generatePayQRCode(HttpServletRequest request, HttpServletResponse reponse){
				try {
					//里边会有订单id
					Map<String, Object> map = getParameterMap(); //orderId
					String orderId = (String) map.get("orderId");
					String orderType = (String) map.get("orderType");
					String money = (String) map.get("money");
					String shopId = (String) map.get("shopId");
					String url = "https://m.ddera.com/json/pay.json?orderId="+orderId+"&orderType=" + orderType+"&money="+money+"&shopId="+shopId;
					BufferedImage image = QRCode.genBarcode(url, 200, 200);
					response.setContentType("image/png");
					response.setHeader("pragma", "no-cache");
					response.setHeader("cache-control", "no-cache");
					response.reset();
					ImageIO.write(image, "png", response.getOutputStream());
				} catch (Exception e) {
					logger.error("StarPosPayContro 199");
					logger.error(e);
					e.printStackTrace();
				}
			}
			
			/**
			 * 
			 * @author lps
			 * @date Nov 15, 2018 5:41:03 AM 
			 * 
			 * @description: 聚合支付，用户扫码
			 * @return void
			 */
			@RequestMapping(value = "/pay")
			public void aggregationPay(HttpServletRequest request, HttpServletResponse reponse){
				try {
					Map<String, Object> map = getParameterMap();
					String orderType = (String) map.get("orderType");
					if("2".equals(orderType)) {
						//生成订单
						Map generateMap = new HashMap<String, Object>();
						generateMap.put("sqlMapId", "insertCartOrderInfo");
						generateMap.put("ORDER_YFMONEY", map.get("money"));
						generateMap.put("ORDER_DIVISION", map.get("5"));
						generateMap.put("FK_SHOP", map.get("shopId"));
						String insert = openService.insert(generateMap);
						if(insert == null) {
							output("9999", "支付失败！");
							return;
						}
						map.put("orderId", insert);
					}
					
					logger.info("215");
					logger.info(map);
					//从map中取出来userid和订单id
					//链接里边有个参数，传过来订单id
					//TODO 根据userid和订单id查询出多少钱，然后进行支付（算了，不要userid了，下边入如果是微信支付的话直接获取，对。）
					String orderId = (String)map.get("orderId");
					logger.error("orderId"+orderId);
					if(orderId == null || orderId == "") {
						return;
					}
					
					String code = (String)map.get("code");
					if(code != null)
						logger.info(code);
	
					Map<String, Object> newMap = new HashMap<String, Object>();
					newMap.put("DCXT_ORDER_FK", orderId);  //订单id
					//第一步：根据订单id查出来消费了多少和该店铺是哪个店，再查出来该店铺的公众号appid，查不出来就滚蛋return"不支持聚合支付"
					
					map.put("sqlMapId", "selectOrderFinalMoneyAndShopAppidByOrderId");
					map.put("ORDER_PK", orderId);
					logger.error("234");
					Map<String,Object> orderMap = (Map<String,Object>)openService.queryForObject(map);
					String amount = (String)orderMap.get("ORDER_SHOPMONEY");
					logger.error("amount"+amount);
					logger.error("orderMap"+orderMap);
					logger.error("237");
					newMap.put("amount", "1");//假设根据订单id查出来消费了多少
					String shopId = (String)orderMap.get("FK_SHOP");
					String appid = (String)orderMap.get("FK_APP");
					String payPay ="";
					logger.error("242");
					if(code == null || StringUtils.isEmpty(code)) {
						payPay = StartPosUtil.checkPayWay(request.getHeader("user-agent"));
						if("".equals(payPay)) {
							reponse.getWriter().print("请在支付宝或微信中扫码~😤");
							return;
						}
						//第二步：判断是微信的话，拿到openid
						if (payPay.equals(StarPosPay.PAY_CHANNEL_WEIXIN)) {
							logger.error("248");
							boolean hasOpenId = false;
							String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
							logger.error("251");
							if(!(token == null || StringUtils.isEmpty(token))) {//如果用户cookie里边没有记录，说明没关注过公众号或者cookie过期了
								String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
								logger.error("254");
								if(!(userJson == null || StringUtils.isEmpty(userJson))) {
									JSONObject userObj = JSON.parseObject(userJson);
									newMap.put("USER_ID", userObj.get("USER_PK"));
									logger.error("258");
									if(appid.equals(userObj.get("FK_APP"))) {
										newMap.put("openid",userObj.get("USER_WX"));
										hasOpenId = true;
									}
								}
							}
							
							if(!hasOpenId){
								logger.error("267");
								//授权获取openid
								String url = CommonUtil.getPath("Auth-wx-qrcode-url-plat");
								logger.error(url);
								//https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_userinfo&state=STATE&component_appid=wx623296bf9fc03f81#wechat_redirect
								logger.error("appid"+appid);
								logger.error("orderId"+orderId);
								url = url.replace("APPID", appid).replace("snsapi_userinfo", "snsapi_base").replace("REDIRECT_URI", 
										URLEncoder.encode("https://m.ddera.com/json/pay.json?orderId=" + orderId, "UTF-8"));
								logger.error("272");
								logger.info("qrcodeURL:" + url);
								logger.error("274");
								response.sendRedirect(url);
								return;
							}
						}
						
					}else {
						logger.info("213");
						newMap.put("openid", wxController.getOpenIdByCode2(code,appid));
						payPay = StarPosPay.PAY_CHANNEL_WEIXIN;
					}
					logger.info("217");
					newMap.put("USER_ID", "lupishan");//userid是我自己设置的必须的，酌情去除
					newMap.put("payChannel", payPay);
					String payWay = "";
					
					Map starPosPayMap = getStarPosMessageByShopId(shopId);
					if(starPosPayMap != null) {
						newMap.putAll(starPosPayMap);
					}
					
					logger.info(shopId+"shopId");
					
					logger.info(starPosPayMap);
					
					if(payPay.equals(StarPosPay.PAY_CHANNEL_ALIPAY)) {
						newMap = starPosPay.psoPay(newMap);
						if("000000".equals(newMap.get("returnCode").toString())) {
							logger.info("payCode)"+newMap.get("payCode"));
							reponse.sendRedirect((String) newMap.get("payCode"));
						}
						payWay = "2";
					}else{
						logger.error("308");
						newMap = starPosPay.pubSigPay(newMap,null,null);
						logger.error("310");
						if("000000".equals(newMap.get("returnCode").toString())) {
							logger.info(newMap);
							request.setAttribute("payMap", newMap);
							request.getRequestDispatcher("/pay.jsp").forward(request, response);
						}
						payWay = "1";
					}
					
					map.put("ORDER_PAY_WAY", "3"+payWay);
					if("000000".equals(newMap.get("returnCode"))) {
						this.addCallBackMethod((String)newMap.get("logNo"), "ShopCodePay_nextOper", map);
						output("0000",newMap);
						return;
					}else {
						logger.error("支付未成功-->"+newMap);
						output("9999", "支付失败！");
					}
					
				} catch (Exception e) {
					logger.error(e);
					output("9999","支付失败，请在账单中查看是否扣款！");
				}
			}
			
			/**
			 * 公众号支付,应该是用户发起支付
			 * @author lps
			 * @date Nov 26, 2018 9:22:31 PM 
			 * 
			 * @description: 
			 * @return void
			 */
			@RequestMapping(value = "/pubSigPay")
			public void pubSigPay(HttpServletRequest request, HttpServletResponse reponse){
				try {
					Map<String, Object> map = getParameterMap();//里边有订单id
					
					Map<String, Object> newMap = new HashMap<String, Object>();
					newMap.put("DCXT_ORDER_FK", "lps");  //订单id
					//根据订单id查出来需要多少钱
					newMap.put("USER_ID", "2222222");
					newMap.put("amount", "1"); 
					newMap.put("openid",map.get("openid"));
					
					
					
					newMap = starPosPay.pubSigPay(newMap,null,null);
					if("000000".equals(newMap.get("returnCode").toString())) {
						logger.info(newMap);
						output("0000", newMap);
						//request.setAttribute("payMap", newMap);
						//.getRequestDispatcher("/pay.jsp").forward(request, response);
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			/**
			 * 商户扫码支付(店员点餐小程序)
			 * @author lps
			 * @date Nov 27, 2018 12:27:53 AM 
			 * 
			 * @description: 
			 * @return void
			 */
			@RequestMapping(value = "/ShopScanPay")
			public void ShopScanPay(HttpServletRequest request, HttpServletResponse reponse){
				try {
					Map<String, Object> map = getParameterMap();//里边有商铺id,订单id,还有条形码，支付渠道
					String code = (String) map.get("qrCode");
					if(code == null) {
						return;
					}
					//根据条形码判断是哪家的
					String headFlag = code.substring(0,2);
					String payWay = "";
					if(Arrays.asList(StarPosPay.PAY_WEIXIN_HEADCODE).contains(headFlag)) {
						payWay = "1";
					}else if(Arrays.asList(StarPosPay.PAY_ALIPAY_HEADCODE).contains(headFlag)) {
						payWay = "2";
					}else if(Arrays.asList(StarPosPay.PAY_YLPAY_HEADCODE).contains(headFlag)) {
						payWay = "3";
					}else {
						output("9999", "无效支付码");
						return;
					}
					
					String orderType = (String) map.get("orderType");
					if("2".equals(orderType)) {
						//生成订单
						Map generateMap = new HashMap<String, Object>();
						generateMap.put("sqlMapId", "insertCartOrderInfo");
						generateMap.put("ORDER_YFMONEY", map.get("money"));
						generateMap.put("ORDER_DIVISION", map.get("5"));
						generateMap.put("FK_SHOP", map.get("shopId"));
						String insert = openService.insert(generateMap);
						if(insert == null) {
							output("9999", "支付失败！");
							return;
						}
						map.put("ORDER_PK", insert);
					}
					
					
					String orderId = (String) map.get("ORDER_PK");//map里边一定是ORDER_PK
					Map<String, Object> newMap = new HashMap<String, Object>();
					newMap.put("DCXT_ORDER_FK", orderId);  //订单id
					//根据订单id查出来需要多少钱
					newMap.put("USER_ID", "2222222");
					
					newMap.put("amount", "1"); 
					newMap.put("authCode", code);
					//支付渠道
					if("1".equals(payWay)) {
						newMap.put("payChannel", StarPosPay.PAY_CHANNEL_WEIXIN); 
					}else if("2".equals(payWay)){
						newMap.put("payChannel", StarPosPay.PAY_CHANNEL_ALIPAY); 
					}else if("3".equals(payWay)) {
						newMap.put("payChannel", StarPosPay.PAY_CHANNEL_YLPAY); 
					}else {
						output("9999", "支付方式不对");
						return;
					}
					
					Map starPosPayMap = getStarPosMessageByShopId((String)map.get("shopId"));
					if(starPosPayMap != null) {
						newMap.putAll(starPosPayMap);
					}
					logger.info(newMap);
					newMap = starPosPay.pay(newMap);
					if("000000".equals(newMap.get("returnCode").toString())) {
						if(!("S".equals(newMap.get("result")))) {
							//如果不是立即支付成功，那么给他一个回调函数,里边有支付成功后调用的方法，和要发送的数据
							map.put("ORDER_PAY_WAY", "2"+payWay);
							this.addCallBackMethod((String)newMap.get("logNo"), "ShopScanPay_nextOper", map);
						}else {
							map.put("ORDER_PAY_WAY", "2"+payWay);
							map.put("sqlMapId", "updateOrderPayStatusSuccessByOrderId");
							map.put("ORDER_PAY_STATE", "1");
							boolean update = openService.update(map);
							if(!update) {
								output("9999", "交易成功，订单状态修改失败！");
								return;
							}
						}
						logger.info(newMap);
						output("0000", newMap);
						//request.setAttribute("payMap", newMap);
						//.getRequestDispatcher("/pay.jsp").forward(request, response);
						return;
					}
					output("9999", newMap);
					return;
				} catch (Exception e) {
					output("9999", "交易失败");
					logger.error(e);
				}
			}
			
			
			
			/**
			 * 
			 * @author lps
			 * @date Nov 27, 2018 3:24:59 AM 
			 * 
			 * @description: 新大陆支付成功后回调函数，完成对订单的支付状态修改
			 * @return void
			 */
			@RequestMapping(value = "/ShopCodePay_nextOper", method = RequestMethod.POST)
			public void ShopCodePayNextOper(HttpServletRequest request,HttpSession seesion){
				try {
					Map<String, Object> map = getParameterMap();//里边有订单id
					
					String orderId = (String) map.get("ORDER_PK");
					
					//异步发送打印请求
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							printerController.doPrintDZByOrderId(orderId);
							//根据orderid查询店铺
							Map shopMap = new HashMap<String, Object>();
							//根据订单id查询出来商铺id
							shopMap.put("sqlmapId", "slectShopIdByOrderId");
							shopMap.put("ORDER_PK", orderId);
							try {
								shopMap = (Map) openService.queryForObject(shopMap);
								
								String shopId = (String) shopMap.get("FK_SHOP");
								
								//查询打不打印结算联
								Map switchMap = new HashMap<String, Object>();
								switchMap.put("sqlMapId", "loadFuncSwitchList");
								switchMap.put("FK_SHOP", shopId);
								switchMap = (Map) openService.queryForObject(shopMap);
								String CHECK_XDDYJSL = (String) switchMap.get("CHECK_XDDYJSL");
								if("true".equals(CHECK_XDDYJSL)) {
									printerController.doPrintJS(shopId, orderId, "tdjs");
								}
							
							} catch (Exception e) {
								logger.error(e);
								return;
							}
						}
					}).start();
					
					logger.info("ShopCodePay_nextOper   360");
					map = getCallBackMapParam(map);
//					logger.info(request);
					
					//TODO修改订单状态为支付成功
					map.put("sqlMapId", "updateOrderPayStatusSuccessByOrderId");
					
					map.put("ORDER_PAY_STATE", "1");
					logger.info(map);
					boolean update = openService.update(map);
					if(!update) {
						logger.info("update fail");
					}
				} catch (Exception e) {
					logger.info(e);
					logger.info("starposPayController pay success but status is not setting 1  ---471line");
				}
				
			}
			
			
			/**
			 * 
			 * @author lps
			 * @date Dec 24, 2018 2:17:43 AM 
			 * 
			 * @description: 新大陆支付异步回调函数调用后转换Map
			 * @return Map<String,Object>
			 */
			private Map<String, Object> getCallBackMapParam(Map<String, Object> map) {
				String callBackString = (map.toString());
				callBackString = callBackString.split("\\{")[2].split("\\}")[0].replaceAll("\\\\", "").replaceAll("=", ":");
				callBackString = "{" + callBackString + "}";
				System.err.println(callBackString);
				map = (Map) JSON.parse(callBackString);
				return map;
			}

			/**
			 * 
			 * @author lps
			 * @date Nov 27, 2018 3:24:59 AM 
			 * 
			 * @description: 新大陆支付成功后回调函数，完成对订单的支付状态修改
			 * @return void
			 */
			@RequestMapping(value = "/ShopScanPay_nextOper", method = RequestMethod.POST)
			public void ShopScanPayNextOper(HttpServletRequest request,HttpSession seesion){
				try {
					Map<String, Object> map = getParameterMap();//里边有订单id
					
					String orderId = (String) map.get("ORDER_PK");
					
					//异步发送打印请求
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							printerController.doPrintDZByOrderId(orderId);
							//根据orderid查询店铺
							Map shopMap = new HashMap<String, Object>();
							//根据订单id查询出来商铺id
							shopMap.put("sqlmapId", "slectShopIdByOrderId");
							shopMap.put("ORDER_PK", orderId);
							try {
								shopMap = (Map) openService.queryForObject(shopMap);
								
								String shopId = (String) shopMap.get("FK_SHOP");
								
								//查询打不打印结算联
								Map switchMap = new HashMap<String, Object>();
								switchMap.put("sqlMapId", "loadFuncSwitchList");
								switchMap.put("FK_SHOP", shopId);
								switchMap = (Map) openService.queryForObject(shopMap);
								String CHECK_XDDYJSL = (String) switchMap.get("CHECK_XDDYJSL");
								if("true".equals(CHECK_XDDYJSL)) {
									printerController.doPrintJS(shopId, orderId, "tdjs");
								}
							
							} catch (Exception e) {
								logger.error(e);
								return;
							}
						}
					}).start();
					
					logger.info("ShopScanPay_nextOper   495");
					logger.info(map);
					map = getCallBackMapParam(map);
					//TODO修改订单状态为支付成功
					map.put("sqlMapId", "updateOrderPayStatusSuccessByOrderId");
					map.put("ORDER_PAY_STATE", "1");
					
					openService.update(map);
					
				} catch (Exception e) {
					logger.info(e);
					logger.info("starposPayController pay success but status is not setting 1  ---408line");
				}
				
			}
			
			@RequestMapping(value = "/aaa_aaa_aaa", method = RequestMethod.POST)
			public String test(HttpServletRequest request,HttpSession seesion){
				logger.info("-------------------test success----------------");
				Map map = null;
				try {
					map = getParameterMap();
				} catch (ExceptionVo e) {
					e.printStackTrace();
					return "9999";
				}
				logger.info(map);
				return "0000";
			}
			
			
			
			/**
			 * 添加回调函数到redis，现在的用处是新大陆支付成功异步回调，成功后执行一些我要的操作
			 * @author lps
			 * @date Nov 27, 2018 3:13:54 AM 
			 * 
			 * @description: 
			 * @return boolean
			 */
			public boolean addCallBackMethod(String logNo, String callBackUrl,Map callBackParamMap) {
				  if(StringUtils.isNotBlank(callBackUrl)){
			        	 if(callBackParamMap == null){
			        		 callBackParamMap = new HashMap<String, Object>();
			        	 }
			        	 callBackParamMap.put("callBackUrl", callBackUrl);
			        	 //把回调url存入redis，收到新大陆的异步消息后立马回调
			      		String b = jedisClient.set(RedisConstants.STARPOS_PAY_CALLBACK_URL + logNo, JSON.toJSONString(callBackParamMap));
			      		//设置过期时间2小时
			      		jedisClient.expire(RedisConstants.STARPOS_PAY_CALLBACK_URL + logNo, 3600 * 2);
			      		
			      		logger.info("------------------starpospaycontroller-387---------------------------");
			    		logger.info(jedisClient.get(RedisConstants.STARPOS_PAY_CALLBACK_URL + logNo));
			    		
			    		if (b != null) {
			    			return true;
			    		}
			         }
				return false;
			} 
			
			/**
			 * 
			 * @author lps
			 * @date Dec 17, 2018 2:13:18 AM 
			 * 
			 * @description: 根据商铺ID 获取 新大陆下绑定的公众号appid，没有则返回智慧云appid
			 * @return String
			 * @throws Exception 
			 */
			public String getStarPosWxAppidByShopId(String shopId) throws Exception {
				Map appidMap = new HashMap<String, String>();
				appidMap.put("sqlMapId", "selectByShopFK");
				appidMap.put("SHOP_FK", shopId);
				logger.info("496");
				Map paySetting = (Map) openService.queryForObject(appidMap);
				String appid = (String)paySetting.get("POS_WX_APPID");
				logger.info("499");
				logger.info(appid);
				if(StringUtils.isBlank(appid)) {
					appid = CommonUtil.getPath("AppID");
				}
				logger.info(appid);
				return appid;
			}
			

//			
			/**
			 * 
			 * @author lps
			 * @date 2018年12月17日03:39:46
			 * 
			 * @description: 根据商铺ID 获取 新大陆的识别号和终端号,没有则返回空map
			 * @return String
			 * @throws Exception 
			 */
			public Map<String, Object> getStarPosMessageByShopId(String shopId) throws Exception {
				Map<String, Object> paySettingMap = new HashMap<String, Object>();
				//获取新大陆设备号
				paySettingMap.put("sqlMapId", "selectByShopFK");
				paySettingMap.put("SHOP_FK", shopId);
				paySettingMap = (Map<String, Object>) openService.queryForObject(paySettingMap);
				if(paySettingMap != null) {
					String mercId = (String) paySettingMap.get("SHOP_IDENTIFY_NUMBER");
					String trmNo = (String) paySettingMap.get("STARPOS_TRM_NO");
					String spKey = (String) paySettingMap.get("STARPOS_KEY");
					Map<String, Object> newMap = new HashMap<String, Object>();
					if(StringUtils.isNotBlank(mercId) && StringUtils.isNotBlank(trmNo) && StringUtils.isNotBlank(spKey)) {
						newMap.put("mercId", mercId);
						newMap.put("trmNo", trmNo);
						newMap.put("spKey", spKey);
						return newMap;
					}
				}
				return null;
			}
			public static void main(String[] args) throws Exception {
				
				System.out.println(new StarPosPayController().getStarPosMessageByShopId("f11099f4816f4a6c99e511c4a7aa82d0"));
			}
			
		}

