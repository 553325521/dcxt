package cn.wifiedu.ssm.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.DateUtil;
import cn.wifiedu.ssm.util.QRCode;
import cn.wifiedu.ssm.util.WxConstants;
import cn.wifiedu.ssm.util.WxUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * 商铺与数据库交互
 * 
 * @author wangjinglong
 *
 */

@Controller
@Scope("prototype")
public class ShopController extends BaseController {

	private static Logger logger = Logger.getLogger(ShopController.class);

	@Resource
	OpenService openService;

	@Resource
	PlatformTransactionManager transactionManager;

	@Autowired
	WxController wxControllerl;

	@Autowired
	private InterfaceController interCtrl;

	@Resource
	private JedisClient jedisClient;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	/**
	 * 添加商户
	 * 
	 * @author wangjinglong 2018年8月4日23:21:41 修改 lps 添加或修改商铺
	 * 
	 */
	@RequestMapping(value = "/Shop_insert_insertShop", method = RequestMethod.POST)
	public void saveShopData() {
		DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
		defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);
		try {
			Map<String, Object> map = getParameterMap();
			// 合二为一
			map.put("SHOP_TYPE", map.get("SHOP_TYPE_FIRSET") + " " + map.get("SHOP_TYPE_SECOND"));
			// 判断是添加还是修改
			String shopId = (String) map.get("SHOP_ID");
			if (shopId != null && !"".equals(shopId.trim())) {// 是修改
				map.put("sqlMapId", "updateShopBaseInfoById");
				map.put("UPDATE_BY", "admin");
				boolean b = openService.update(map);
				if (b) {
					transactionManager.commit(status);
					output("0000", "修改成功");
				} else {
					throw new Exception();
				}
				return;
			}

			// 是添加
			/*
			 * //先查询出来商铺的服务类型 map.put("sqlMapId", "findServiceTypeIdByName");
			 * Map serviceMap = (Map) openService.queryForObject(map); String
			 * serviceId = (String) serviceMap.get("SERVICE_PK");
			 */
			/*
			 * if(serviceId == null){ throw new Exception(); }
			 */
			map.put("sqlMapId", "insertShop");
			map.put("CREATE_BY", "admin");
			map.put("SERVICETYPE_FK", "");
			String insert = openService.insert(map);

			if (insert == null) {
				throw new Exception();
			}
			// 插入用户商铺中间表
			map.put("sqlMapId", "insertUserShop");
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			map.put("USER_ID", userObj.get("USER_PK"));
			map.put("SHOP_ID", insert);
			map.put("ROLE_ID", "7");
			map.put("tagName", "代理端");
			map.put("FK_APP", "wx6041a1eff32d3c5e");
			String insert2 = openService.insert(map);
			if (insert2 != null) {
				transactionManager.commit(status);
				output("0000", "保存成功");
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			transactionManager.rollback(status);
			output("9999", "保存失败");
		}
	}

	/**
	 * 显示当前登录代理商下的商铺信息 2018年8月8日23:50:27 修改 lps
	 * 
	 * @author wangjinglong
	 */
	@RequestMapping(value = "/Shop_select_findAgentShopInfo", method = RequestMethod.POST)
	public void showAgentShopInfo() {
		try {

			// 如果未认证，跳转完善信息界面
			Map<String, Object> map = getParameterMap();
			// 获取当前session信息
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			map.put("USER_ID", userObj.get("USER_PK"));
			map.put("sqlMapId", "selectAgentInfoById");

			Map<String, Object> reMap1 = (Map) openService.queryForObject(map);
			// 如果还未认证，跳转到认证界面
			if (!reMap1.containsKey("AUTH_STATUS") || "0".equals(reMap1.get("AUTH_STATUS"))) {
				output("5555", "代理信息不完善");
				return;
			}
			// 开始查询当前登录代理商下的商铺信息
			map.put("sqlMapId", "findAgentShopInfo");
			List<Map<String, Object>> reMap = openService.queryForList(map);
			for (int i = 0; i < reMap.size(); i++) {
				Map<String, Object> singleMap = reMap.get(i);
				if (singleMap.get("OVER_DATA") != null) {
					reMap.get(i).put("DAYS", countDays(singleMap.get("OVER_DATA").toString()));
				} else {
					reMap.get(i).put("DAYS", "已过期");
				}
			}
			output("0000", reMap);
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "查询失败");
		}
	}

	/**
	 * 创建商户认领店铺二维码
	 * 
	 * @author wangjinglong
	 */
	@RequestMapping(value = "/Shop_create_createShopClaimQrCode", method = RequestMethod.GET)
	public void createShopClaimQrCode() {
		try {
			Map<String, Object> map = getParameterMap();

			String url = CommonUtil.getPath("Auth-wx-qrcode-url-plat");
			url = url.replace("APPID", CommonUtil.getPath("AppID")).replace("STATE", map.get("SHOPID").toString())
					.replace("REDIRECT_URI", URLEncoder
							.encode(CommonUtil.getPath("project_url").replace("DATA", "responseShopClaim"), "UTF-8"));

			// String url = CommonUtil.getPath("Auth-wx-qrcode-url");
			// url = url.replace("STATE",
			// map.get("SHOPID").toString()).replace("REDIRECT_URI",
			// URLEncoder.encode(CommonUtil.getPath("project_url").replace("DATA",
			// "responseShopClaim"), "UTF-8"));
			BufferedImage image = QRCode.genBarcode(url, 200, 200);
			response.setContentType("image/png");
			response.setHeader("pragma", "no-cache");
			response.setHeader("cache-control", "no-cache");
			response.reset();
			ImageIO.write(image, "png", response.getOutputStream());

		} catch (ExceptionVo | WriterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 认领店铺用户扫码后的功能实现
	 * 
	 * @author wangjinglong
	 */
	@RequestMapping("/responseShopClaim")
	public void responseShopClaim(HttpServletResponse reponse) {
		DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
		defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			/* 判断该商户是否已被认领 */
			param.put("SHOP_ID", request.getParameter("state"));
			param.put("sqlMapId", "checkShopIsClaim");
			Map<String, Object> rMap = (Map<String, Object>) openService.queryForObject(param);
			logger.info("rMap:" + rMap);
			if (Integer.parseInt(rMap.get("nums").toString()) > 0) {
				String btnToken = UUID.randomUUID().toString();
				JSONObject obj = new JSONObject();
				obj.put("status", 9999);
				obj.put("msg", "商铺已经被认领,请勿重复扫码");
				obj.put("data", new ArrayList<JSONObject>());
				// 保存button信息
				jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());
				response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
						"#toOtherPage/msgPage/" + btnToken));
				return;
			} else {
				String code = request.getParameter("code");
				String appid = request.getParameter("appid");
				if (null != code && !"".equals(code)) {
					String openId = wxControllerl.getOpenIdByCode2(code, appid);
					logger.info("openId:" + openId);
					String state = request.getParameter("state");
					String userId = "";
					if (openId != null && !openId.equals("")) {
						param.clear();
						/* 判断当前用户openID是否存在 */
						param.put("OPENID", openId);
						param.put("sqlMapId", "checkUserExits");
						List<Map<String, Object>> checkUserList = openService.queryForList(param);
						logger.info("checkUserList:" + checkUserList);
						if (checkUserList.size() == 0) {
							/* 没存在插入到用户表 */
							param.clear();
							param.put("OPENID", openId);
							wxControllerl.getWxUserInfo(openId, param);
							param.put("sqlMapId", "insertUserInitOpenId");
							userId = openService.insert(param);
						} else {
							userId = checkUserList.get(0).get("USER_PK").toString();
						}
					} else {
						throw new Exception();
					}
					/* 添加到用户商铺中间表里 */
					param.clear();
					param.put("USER_ID", userId);
					param.put("ROLE_ID", 2);
					param.put("tagName", "店员端");
					param.put("FK_APP", "wx6041a1eff32d3c5e");
					param.put("SHOP_ID", state);
					param.put("sqlMapId", "insertUserShop");
					String insertResult = openService.insert(param);
					logger.info("insertResult:" + insertResult);
					/* 插入成功修改商铺认领状态 */
					if (insertResult != null && !insertResult.equals("")) {
						param.clear();
						param.put("SHOP_FK", state);
						param.put("SHOP_STATE", 1);
						param.put("sqlMapId", "UpdateShopState");
						boolean updateResult = openService.update(param);
						param.clear();
						param.put("SHOP_ID", state);
						param.put("sqlMapId", "insertFuntionForShop");
						String insertStr = openService.insert(param);
						if (insertStr == null || insertStr.equals("")) {
							throw new Exception();
						}
						if (updateResult) {
							String token = "";
							if (!jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + appid)) {
								token = WxUtil.getWxAccessToken(appid,
										interCtrl.getComponentAccessToken(),
										interCtrl.getRefreshTokenByAppId(appid));
								jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + appid, token);
								jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + appid, 3600 * 1);
							} else {
								token = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + appid);
							}
							if (token != null) {
								String tagAddURL = CommonUtil.getPath("user_tag_add");
								tagAddURL = tagAddURL.replace("ACCESS_TOKEN", token);
								JSONObject postObj = new JSONObject();
								param.clear();
								param.put("USER_TAG_NAME", "店员端");
								param.put("sqlMapId", "findUserTagIdByUserTagName");
								Map<String, Object> resMap = (Map<String, Object>) openService.queryForObject(param);
								postObj.put("tagid", "148");
								postObj.put("openid_list", new ArrayList<String>() {
									{
										add(openId);
									}
								});
								String resCont = CommonUtil.posts(tagAddURL, postObj.toJSONString(), "utf-8");
								JSONObject resObj = JSON.parseObject(resCont);
								if (WxConstants.ERRORCODE_0.equals(resObj.getString("errcode"))) {

									/* 插入到shopApp表 */
									param.clear();
									param.put("FK_APP", "wx6041a1eff32d3c5e");
									param.put("FK_SHOP", state);
									param.put("sqlMapId", "insertShopApp");
									String insertShopAppStr = openService.insert(param);
									if (insertShopAppStr == null || insertShopAppStr.equals("")) {
										throw new Exception();
									}
									// 重定向成功页面
									String btnToken = UUID.randomUUID().toString();
									JSONObject obj = new JSONObject();
									Map<String, Object> userMap = new HashMap<>();
									userMap.put("USER_WX", openId);
									userMap.put("OPENID", openId);
									wxControllerl.getWxUserInfo(openId, userMap);
									obj.put("status", 0000);
									obj.put("msg", "商户认领成功！");
									obj.put("data", new ArrayList<JSONObject>() {
										{
											JSONObject btn1 = new JSONObject();
											btn1.put("buttonName", "");
											btn1.put("buttonLink",
													CommonUtil.getPath("project_url").replace("json/DATA.json", "")
															+ "/index.jsp");
											add(btn1);
										}
									});
									// 保存button信息
									jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());

									// redis存储用户登录信息
									jedisClient.set(RedisConstants.REDIS_USER_SESSION_KEY + openId,
											JSON.toJSONString(userMap));

									// 添加写cookie的逻辑，cookie的有效期是关闭浏览器就失效。
									CookieUtils.setCookie(request, response, "DCXT_TOKEN", openId);

									response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
											"#toOtherPage/msgPage/" + btnToken));
									transactionManager.commit(status);
									return;
								} else if (WxConstants.ERRORCODE_1.equals(resObj.getString("errcode"))) {
									throw new RuntimeException("1");
								} else if (WxConstants.ERRORCODE_40032.equals(resObj.getString("errcode"))) {
									throw new RuntimeException("40032");
								} else if (WxConstants.ERRORCODE_49003.equals(resObj.getString("errcode"))) {
									throw new RuntimeException("49003");
								} else if (WxConstants.ERRORCODE_45159.equals(resObj.getString("errcode"))) {
									throw new RuntimeException("45159");
								} else if (WxConstants.ERRORCODE_45059.equals(resObj.getString("errcode"))) {
									throw new RuntimeException("45059");
								} else if (WxConstants.ERRORCODE_40003.equals(resObj.getString("errcode"))) {
									throw new RuntimeException("40003");
								} else if (WxConstants.ERRORCODE_50005.equals(resObj.getString("errcode"))) {
									// 重定向成功页面
									transactionManager.rollback(status);
									String btnToken = UUID.randomUUID().toString();
									JSONObject obj = new JSONObject();
									obj.put("status", 9999);
									obj.put("msg", "请先关注公众号后再认领！");
									obj.put("data", new ArrayList<JSONObject>());
									// 保存button信息
									jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());

									response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
											"#toOtherPage/msgPage/" + btnToken));
									return;
								}
							} else {
								throw new Exception();
							}
						} else {
							throw new Exception();
						}
					} else {
						throw new Exception();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			transactionManager.rollback(status);
			e.printStackTrace();
		}
		String btnToken = UUID.randomUUID().toString();
		JSONObject obj = new JSONObject();
		obj.put("status", 9999);
		obj.put("msg", "商户认领失败");
		obj.put("data", new ArrayList<JSONObject>());
		// 保存button信息
		jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());
		try {
			response.sendRedirect(
					CommonUtil.getPath("project_url").replace("json/DATA.json", "#toOtherPage/msgPage/" + btnToken));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String countDays(String date) {
		// 算两个日期间隔多少天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null;
		;
		try {
			date1 = format.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int a = (int) ((date1.getTime() - new Date().getTime()) / (1000 * 3600 * 24));
		if (a < 0) {
			return "已过期";
		}
		return a + "天";
	}

	/**
	 * 
	 * @date 2018年8月4日 下午8:05:45
	 * @author lps
	 * 
	 * @Description: 根据商铺id查询商铺信息
	 * @return void
	 *
	 */
	@RequestMapping(value = "/Shop_select_findShopInfoById")
	public void findShopInfoById() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "SelectByPrimaryKey");
			map.put("SHOP_FK", map.get("shopid"));
			Map reMap = (Map) openService.queryForObject(map);
			if (reMap == null) {
				output("9999", "查询失败");
				return;
			}
			output("0000", reMap);
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "查询失败");
		}

	}

	/**
	 * 
	 * @date 2018年8月12日 下午9:57:00
	 * @author lps
	 * 
	 * @Description: 根据商铺id初始化购买服务的信息
	 * @param request
	 * @param seesion
	 * @return void
	 *
	 */

	@RequestMapping("/Shop_query_initShopBuyServiceMessage")
	public void initShopBuyServiceMessage(HttpServletRequest request, HttpSession seesion) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "findServiceTypeList");
			List<Map<String, Object>> reList = openService.queryForList(map);
			List<Map<String, Object>> serviceRule = ServiceTypeController.getServiceRule(openService);
			if (reList == null || serviceRule == null) {
				output("9999", "加载失败");
				return;
			}

			// 通过shopid获取之前购买的服务类型的价格和过期时间
			map.put("sqlMapId", "selectNODSTAndODByShopId");
			Map perServiceMess = (Map) openService.queryForObject(map);
			int discountsMoney = 0;
			if (perServiceMess != null) {
				discountsMoney = ShopPurchaseRecordController.getDiscountsMoney(perServiceMess, serviceRule);
				perServiceMess.put("deduction_money", discountsMoney);// 抵扣金额
			}

			Map<String, Object> reMap = new HashMap<String, Object>();
			reMap.put("pre_service_mess", perServiceMess);
			reMap.put("service_type", reList);
			reMap.put("service_rule", serviceRule);

			output("0000", reMap);
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
			return;
		}
	}

	/**
	 * 
	 * @author kqs
	 * @return void
	 * @date 2018年8月23日 - 上午10:35:41
	 * @description:查询用户所有关联的店铺
	 */
	@RequestMapping("/Shop_queryForList_loadShopListByUser")
	public void loadShopListByUser() {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			Map<String, Object> map = new HashMap<>();
			map.put("USER_PK", userObj.getString("USER_PK"));
			map.put("sqlMapId", "loadShopListByUser");
			List<Map<String, Object>> reList = openService.queryForList(map);
			// 更新 店铺列表数据
			jedisClient.set(RedisConstants.REDIS_USER_SHOP_LIST_KEY + token, JSON.toJSONString(reList));
			if (reList != null && reList.size() == 1) {
				userObj.put("FK_APP", reList.get(0).get("FK_APP"));
				userObj.put("FK_SHOP", reList.get(0).get("FK_SHOP"));
				userObj.put("FK_ROLE", reList.get(0).get("FK_ROLE"));
				userObj.put("FK_USER_TAG", reList.get(0).get("FK_USER_TAG"));
				// 重新对用户redis赋值
				jedisClient.set(RedisConstants.REDIS_USER_SESSION_KEY + token, userObj.toJSONString());
				// 存储用户对应的shop信息
				jedisClient.set(RedisConstants.REDIS_USER_SHOP_SESSION_KEY + token,
						JSON.toJSONString(reList.get(0)));
				output("0000", "success");
				return;
			} else {
				if (jedisClient.isExit(RedisConstants.REDIS_USER_SHOP_SESSION_KEY + token) && StringUtils
						.isNotBlank(jedisClient.get(RedisConstants.REDIS_USER_SHOP_SESSION_KEY + token))) {
					Map<String, Object> mapp = (Map<String, Object>) JSON
							.parse(jedisClient.get(RedisConstants.REDIS_USER_SHOP_SESSION_KEY + token));
					userObj.put("FK_APP", mapp.get("FK_APP"));
					userObj.put("FK_SHOP", mapp.get("FK_SHOP"));
					userObj.put("FK_ROLE", mapp.get("FK_ROLE"));
					userObj.put("FK_USER_TAG", mapp.get("FK_USER_TAG"));
					// 重新对用户redis赋值
					jedisClient.set(RedisConstants.REDIS_USER_SESSION_KEY + token, userObj.toJSONString());
					output("0000", "success");
					return;
				} else {
					output("9999", "shopList");
					return;
				}
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
	}

	/**
	 * 
	 * @author kqs
	 * @return void
	 * @date 2018年8月23日 - 上午10:35:41
	 * @description:查询用户所有关联的店铺
	 */
	@RequestMapping("/Shop_queryForList_loadShopListByRedis")
	public void loadShopListByRedis() {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			List<Map<String, Object>> reList = (List<Map<String, Object>>) JSON
					.parse(jedisClient.get(RedisConstants.REDIS_USER_SHOP_LIST_KEY + token));
			output("0000", reList);
			return;
		} catch (Exception e) {
			logger.error("error", e);
		}
	}

	/**
	 * 
	 * @author kqs
	 * @return void
	 * @date 2018年8月23日 - 上午10:35:41
	 * @description:查询用户所有关联的店铺
	 */
	@RequestMapping("/Shop_queryForList_selectShop")
	public void selectShop() {
		try {
			Map<String, Object> map = getParameterMap();

			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			userObj.put("FK_APP", map.get("FK_APP"));
			userObj.put("FK_SHOP", map.get("FK_SHOP"));
			userObj.put("FK_ROLE", map.get("FK_ROLE"));
			userObj.put("FK_USER_TAG", map.get("FK_USER_TAG"));
			// 重新对用户redis赋值
			jedisClient.set(RedisConstants.REDIS_USER_SESSION_KEY + token, userObj.toJSONString());
			// 存储用户对应的shop信息
			jedisClient.set(RedisConstants.REDIS_USER_SHOP_SESSION_KEY + token, JSON.toJSONString(map));
			output("0000", "welcome");
			return;
		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	
	/**
	 * 
	 * @date 2018年9月12日 下午10:06:37 
	 * @author lps
	 * 
	 * @Description:  根据用户id查询该用户管理的店铺
	 * @return void 
	 *
	 */
	@RequestMapping("/Shop_queryForList_selectManagerShopIdAndNameByUserId")
	public void findShopByVipManagerId() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectManagerShopIdAndNameByUserId");
			
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			
			map.put("USER_ID", userObj.get("USER_PK")); 
			//商铺的名字和id
			List<Map<String,Object>> ShopList = openService.queryForList(map);
			
			if(ShopList == null){
				output("9999", "查询错误");
				return;
			}
			
			//优惠券的名字和id
			
			
			
			
			List reList = new ArrayList<Map>();
			reList.add(ShopList);
			
			
			
			output("0000", ShopList);
			return;
		} catch (Exception e) {
			logger.error("error", e);
			output("9999", "出错");
			return;
		}
	}
	
	/**
	* <p>Title: pushSession</p>
	* <p>Description:将用户openid和shopid放入session </p>
	*/
	@RequestMapping("/Shop_pushSession_pushOpenIDAndShopIDSession")
	public void pushSession(HttpServletRequest request){
		try {
			Map<String, Object> map = getParameterMap();
			HttpSession session  = request.getSession();
			session.setAttribute("openId",map.get("openId"));
			session.setAttribute("shopId",map.get("shopId"));
			System.out.println(session.getAttribute("openId"));
			output("0000","push成功");
		} catch (ExceptionVo e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	* <p>Title: loadShopReportByShopID</p>
	* <p>Description: 加载商铺报表数据</p>
	*/
	@RequestMapping("/Shop_load_loadShopReportByShopID")
	public void loadShopReportByShopID(){
		try {
			Map<String,Object> resultMap = new HashMap<String, Object>();
			//加载商铺消费总金额
			double SUM_MONEY = 0;
			Map<String, Object> map = getParameterMap();
			String []dateArray = DateUtil.returnTodayStartAndEnd();
			map.put("todayStart", dateArray[0]);
			map.put("todayEnd", dateArray[1]);
			//计算智慧云订单金额
			map.put("sqlMapId", "selectZHYMoney");
			List<Map<String,Object>> zhyMoneyList = openService.queryForList(map);
			SUM_MONEY = SUM_MONEY+this.jsTotalMoney(zhyMoneyList,"ORDER_SHOPMONEY");
			//计算饿百订单金额
			map.put("sqlMapId", "selectEBMoney");
			List<Map<String,Object>> ebMoneyList = openService.queryForList(map);
			SUM_MONEY = SUM_MONEY+this.jsTotalMoney(ebMoneyList,"ORDER_SHOP_FEE");
			resultMap.put("SUM_MONEY", SUM_MONEY);
			//商铺卖出去商品总份数
			int totalQuantity = 0;
			//计算智慧云平台卖出去的订单中的商品总数
			map.put("sqlMapId", "selectZHYOrderNumber");
			List<Map<String,Object>> zhyOrderNumberList = openService.queryForList(map);
			totalQuantity = totalQuantity+Integer.parseInt(zhyOrderNumberList.get(0).get("ZHY_ORDER_NUMBER").toString());
			//计算饿百平台卖出去的订单中的商品总数
			map.put("sqlMapId", "selectEBOrderNumber");
			List<Map<String,Object>> ebOrderNumberList = openService.queryForList(map);
			totalQuantity = totalQuantity+Integer.parseInt(ebOrderNumberList.get(0).get("EB_ORDER_NUMBER").toString());
			resultMap.put("SUM_ORDER",totalQuantity);
			//计算今日该店铺新增会员个数
			resultMap.put("SUM_MEMBER",0);
			output("0000",resultMap);
			
		} catch (ExceptionVo e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	* <p>Title: loadShopSideByUserUnionId</p>
	* <p>Description:加载店员端报表----根据当前登录用户显示所管理的商铺 </p>
	*/
	@RequestMapping("/Shop_load_loadShopSideByUserUnionId")
	public void loadShopSideByUserUnionId(){
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadShopSideListByUserUNIONID");
			List<Map<String,Object>> resultList = openService.queryForList(map);
			output("0000",resultList);
		} catch (ExceptionVo e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@RequestMapping("/Shop_load_loadShopRunData")
	public void loadShopRunData(){
		try {
			Map<String, Object> map = getParameterMap();
			Map<String,Object> resultMap = new HashMap<String, Object>();
//			根据前台传的时间参数计算开始时间和结束时间
			String[] dateStrArray = DateUtil.selectTime(map.get("selectTime").toString());
			map.put("START_TIME", dateStrArray[0]);
			map.put("END_TIME", dateStrArray[1]);
//			计算新增会员个数
			map.put("sqlMapId","selectNewAddMemberNumberByTime");
			int addMemberNumber = Integer.parseInt(openService.queryForList(map).get(0).get("VIP_NUMBER").toString());
			resultMap.put("memberNumber", addMemberNumber);
			//计算新增店内订单的数量
			
			
			output("0000","000");
		} catch (ExceptionVo e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*计算总金额*/
	private double jsTotalMoney(List<Map<String,Object>> moneyList,String key){
		double sumMoney = 0.0;
		for(Map<String,Object> map:moneyList){
			double currentMoney = Double.parseDouble(map.get(key).toString());
			sumMoney = sumMoney+currentMoney;
		}
		return sumMoney/100;
	}
	
}
