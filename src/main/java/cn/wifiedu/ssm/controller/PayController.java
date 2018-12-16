package cn.wifiedu.ssm.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
		public class PayController extends BaseController {

			private static Logger logger = Logger.getLogger(PayController.class);

			@Resource
			OpenService openService;
			
			@Resource
			private JedisClient jedisClient;
			
			@Resource
			private StarPosPay starPosPay;
			
			@Resource
			private WxController wxController;
			
			@Resource
			private StarPosPayController starPosPayController;
			
			public OpenService getOpenService() {
				return openService;
			}

			public void setOpenService(OpenService openService) {
			this.openService = openService;
			}
			
//			
//			/**
//			 * 
//			 * @author lps
//			 * @date Nov 27, 2018 3:30:09 AM 
//			 * 
//			 * @description: 参数里边会有订单id
//			 * @return void
//			 */
//			@RequestMapping(value = "/starPosPay_generatePayQRCode")
//			public void generatePayQRCode(HttpServletRequest request, HttpServletResponse reponse){
//				try {
//					//里边会有订单id
//					Map<String, Object> map = getParameterMap(); //orderId
//					String orderId = (String) map.get("orderId");
			
//					String url = "https://m.ddera.com/json/pay.json?orderId="+orderId;
//					BufferedImage image = QRCode.genBarcode(url, 200, 200);
//					response.setContentType("image/png");
//					response.setHeader("pragma", "no-cache");
//					response.setHeader("cache-control", "no-cache");
//					response.reset();
//					ImageIO.write(image, "png", response.getOutputStream());
//				} catch (Exception e) {
//					logger.error("StarPosPayContro 199");
//					logger.error(e);
//					e.printStackTrace();
//				}
//			}
			
			/**
			 * 
			 * @author lps
			 * @date 2018年12月17日00:09:57
			 * 
			 * @description: 商家固定二维码，用户扫码
			 * @return void
			 */
			@RequestMapping(value = "/pay_page")
			public void aggregationPay(HttpServletRequest request, HttpServletResponse reponse){
				try {
					String payPay = StartPosUtil.checkPayWay(request.getHeader("user-agent"));
//					if("".equals(payPay)) {
//						reponse.getWriter().print("<h1>请在支付宝或微信中扫码~😤</h1>");
//						return;
//					}
					
					//获取商铺ID
					Map<String, Object> map = getParameterMap();
					String shopId = (String)map.get("shop");
					if(StringUtils.isBlank(shopId)) {
						return;
					}
					
					//查询商铺名字
					Map<String, Object> shopMap = new HashMap<String, Object>();
					shopMap.put("sqlMapId", "selectShopNameByShopId");
					shopMap.put("SHOP_ID", shopId);
					Map shopNameMap = (Map) openService.queryForObject(shopMap);
					if(shopNameMap == null) {
						return;
					}
					
					String money = (String)map.get("money");
					String code = (String)map.get("code");
					//获取了需要支付的money
					//根据店铺id查询
					//第二步：判断是微信的话，拿到openid
					//如果money为空，说明是第一次扫码进来，获取openid
					if(StringUtils.isBlank(money)) {
						String openId = "";
						String userId = "";
						if(StringUtils.isBlank(code)) {
							if (payPay.equals(StarPosPay.PAY_CHANNEL_WEIXIN)) {
								boolean hasOpenId = false;
								String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
								if(!(token == null || StringUtils.isEmpty(token))) {//如果用户cookie里边没有记录，说明没关注过公众号或者cookie过期了
									String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
									if(!(userJson == null || StringUtils.isEmpty(userJson))) {
										JSONObject userObj = JSON.parseObject(userJson);
										userId = (String)userObj.get("USER_PK");
										if(shopId.equals(userObj.get("FK_APP"))) {
											openId = (String)userObj.get("USER_WX");
											hasOpenId = true;
										}
									}
								}
							
								if(!hasOpenId){
									//授权获取openid
									String url = CommonUtil.getPath("Auth-wx-qrcode-url-plat");
									//获取商户对应的新大陆下的微信appid
									String appid = starPosPayController.getStarPosWxAppidByShopId(shopId);
									url = url.replace("APPID", appid).replace("snsapi_userinfo", "snsapi_base").replace("REDIRECT_URI", 
											URLEncoder.encode("https://m.ddera.com/json/pay_page.json?shop=" + shopId, "UTF-8"));
									logger.info("qrcodeURL:" + url);
									response.sendRedirect(url);
									return;
								}
							}
							
							
						}else {
							//code不为空
							String appid = starPosPayController.getStarPosWxAppidByShopId(shopId);
							openId =  wxController.getOpenIdByCode2(code,appid);
//							payPay = StarPosPay.PAY_CHANNEL_WEIXIN;
						}
						
						if(StringUtils.isNotBlank(openId)) {
							//如果openID不为空，说明是微信支付，查询优惠信息
							request.setAttribute("openId", openId);
						}
						
						String shopName = (String) shopNameMap.get("SHOP_NAME");
						request.setAttribute("shopName", shopName);
						request.setAttribute("shopId", shopId);
						request.getRequestDispatcher("/pay_page.jsp").forward(request, response);
						return;
					}
					
				} catch (Exception e) {
					logger.error("payController-196"+e);
					e.printStackTrace();
				}
//				try {
//					Map<String, Object> map = getParameterMap();
//					//从map中取出来userid和订单id
//					//链接里边有个参数，传过来订单id
//					//TODO 根据userid和订单id查询出多少钱，然后进行支付（算了，不要userid了，下边入如果是微信支付的话直接获取，对。）
//					String orderId = (String)map.get("orderId");
//					if(orderId == null || orderId == "") {
//						return;
//					}
//					
//					String code = (String)map.get("code");
//					logger.info(code);
//	
//					Map<String, Object> newMap = new HashMap<String, Object>();
//					newMap.put("DCXT_ORDER_FK", orderId);  //订单id
//					//第一步：根据订单id查出来消费了多少和该店铺是哪个店，再查出来该店铺的公众号appid，查不出来就滚蛋return"不支持聚合支付"
//					
//					map.put("sqlMapId", "selectOrderFinalMoneyAndShopAppidByOrderId");
//					map.put("ORDER_PK", map.get("orderId"));
//					Map<String,Object> orderMap = (Map<String,Object>)openService.queryForObject(map);
//					String amount = (String)orderMap.get("ORDER_SHOPMONEY");
//					
//					newMap.put("amount", "1");//假设根据订单id查出来消费了多少
//					String shopId = (String)orderMap.get("FK_APP");//假设拿到了appid
//					String appid = (String)orderMap.get("FK_APP");
//					String payPay ="";
//					if(code == null || StringUtils.isEmpty(code)) {
//						payPay = StartPosUtil.checkPayWay(request.getHeader("user-agent"));
//						if("".equals(payPay)) {
//							reponse.getWriter().print("请在支付宝或微信中扫码~😤");
//							return;
//						}
//						//第二步：判断是微信的话，拿到openid
//						if (payPay.equals(StarPosPay.PAY_CHANNEL_WEIXIN)) {
//							boolean hasOpenId = false;
//							String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
//							if(!(token == null || StringUtils.isEmpty(token))) {//如果用户cookie里边没有记录，说明没关注过公众号或者cookie过期了
//								String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
//								if(!(userJson == null || StringUtils.isEmpty(userJson))) {
//									JSONObject userObj = JSON.parseObject(userJson);
//									newMap.put("USER_ID", userObj.get("USER_PK"));
//									if(shopId.equals(userObj.get("FK_APP"))) {
//										newMap.put("openid",userObj.get("USER_WX"));
//										hasOpenId = true;
//									}
//								}
//							}
//							
//							if(!hasOpenId){
//								//授权获取appid
//								String url = CommonUtil.getPath("Auth-wx-qrcode-url-plat");
//								url = url.replace("APPID", appid).replace("snsapi_userinfo", "snsapi_base").replace("REDIRECT_URI", 
//										URLEncoder.encode("https://m.ddera.com/json/pay.json&orderId=" + orderId, "UTF-8"));
//								logger.info("qrcodeURL:" + url);
//								response.sendRedirect(url);
//								return;
//							}
//						}
//						
//					}else {
//						logger.error("213");
//						newMap.put("openid", wxController.getOpenIdByCode2(code,appid));
//						payPay = StarPosPay.PAY_CHANNEL_WEIXIN;
//					}
//					logger.error("217");
//					newMap.put("USER_ID", "lupishan");//userid是我自己设置的必须的，酌情去除
//					newMap.put("payChannel", payPay);
//
//					if(payPay.equals(StarPosPay.PAY_CHANNEL_ALIPAY)) {
//						newMap = starPosPay.psoPay(newMap);
//						if("000000".equals(newMap.get("returnCode").toString())) {
//							logger.info("payCode)"+newMap.get("payCode"));
//							reponse.sendRedirect((String) newMap.get("payCode"));
//							return;
//						}
//					}else{
//						logger.error("228");
//						newMap = starPosPay.pubSigPay(newMap,null,null);
//						if("000000".equals(newMap.get("returnCode").toString())) {
//							logger.info(newMap);
//							request.setAttribute("payMap", newMap);
//							request.getRequestDispatcher("/pay.jsp").forward(request, response);
//							return;
//						}
//					}
					
//					logger.error("支付未成功-->"+newMap);
//				} catch (Exception e) {
//					logger.error(e);
//				}
			}
			
			
			
			
			/**
			 * 
			 * @author lps
			 * @date 2018年12月17日02:50:05
			 * 
			 * @description: 用户手动输入金额发起公众号支付
			 * @return void
			 */
			@RequestMapping(value = "/shoppay")
			public void pubSigPay(HttpServletRequest request, HttpServletResponse reponse){
				try {
					Map<String, Object> map = getParameterMap();//里边有订单id
					String money = (String)map.get("totalMoney");
					Float moneyF = Float.parseFloat(money);
					logger.info("发起支付了310");
					if(moneyF == null || moneyF <= 0) {
						output("9999","金额有误");
						return ;
					}
					Integer moneyFen = Math.round(moneyF * 100);
					
					logger.info(map);
					logger.info("发起支付了");
					
					String openId = (String)map.get("openId");
					String shopId = (String)map.get("shopId");
					
					logger.info(openId);
					logger.info(shopId);
					
					//获取新大陆设备号
					Map<String, Object> newMap = starPosPayController.getStarPosMessageByShopId(shopId);
					if(newMap == null) {
						output("9999", "该商户未开通新大陆支付");
						return;
					}
					logger.info("322");
					logger.info(moneyFen.toString());
					newMap.put("USER_ID", "zhy");
					newMap.put("amount", moneyFen.toString());
					newMap.put("DCXT_ORDER_FK", "zhy");
					if(openId == null) {
						logger.info("338");
						//支付宝支付
						newMap.put("payChannel", StarPosPay.PAY_CHANNEL_ALIPAY);
							newMap = starPosPay.psoPay(newMap);
							if("000000".equals(newMap.get("returnCode").toString())) {
								logger.info("payCode)"+newMap.get("payCode"));
								reponse.sendRedirect((String) newMap.get("payCode"));
								logger.info(newMap);
							}else {
								output("9999",newMap.get("message"));
								logger.info(newMap);
							}
					}else {
						//微信支付
						logger.info("352");
						logger.info(openId);
						newMap.put("openid",openId);
						//TODO	根据openid查询优惠信息
						logger.info(newMap);
						newMap = starPosPay.pubSigPay(newMap,null,null);
						if("000000".equals(newMap.get("returnCode").toString())) {
							logger.info("359");
							request.setAttribute("payMap", newMap);
							request.getRequestDispatcher("/pay.jsp").forward(request, response);
						}else {
							logger.info("365");
							output("9999",newMap.get("message"));
							logger.info(newMap);
						}
					}
					return;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			
			
			
			
}

