package cn.wifiedu.ssm.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.QRCode;
import cn.wifiedu.ssm.util.WxConstants;
import cn.wifiedu.ssm.util.WxUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * @author kqs
 * @time 2018年8月2日 - 下午9:25:29
 * @description:员工管理模块
 */
@Controller
@Scope("prototype")
public class StaffController extends BaseController {

	private static Logger logger = Logger.getLogger(StaffController.class);

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
	private InterfaceController interCtrl;

	@Autowired
	private WxController wxCtrl;

	@Autowired
	private TransactionManagerController txManagerController;

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月13日 - 下午9:58:44
	 * @description:更新员工信息
	 */
	@RequestMapping("/User_update_updateStaffInfo")
	public void updateStaffInfo(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSONObject.parseObject(userJson);
			Map<String, Object> map = getParameterMap();
			map.put("IS_USE", String.valueOf(map.get("IS_USE")));
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "updateStaffInfo");
			txManagerController.createTxManager();
			logger.info("update staffInfo: info [ " + map + "]");
			if (openService.update(map)) {
				map.put("sqlMapId", "updateUserInfo");
				logger.info("update userInfo: info [ " + map + "]");
				if (openService.update(map)) {
					txManagerController.commit();
					output("0000", "保存成功！");
					return;
				}
			}
			output("9999", "保存失败！");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
			txManagerController.rollback();
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月13日 - 下午9:59:04
	 * @description:根据员工id获取具体信息
	 */
	@RequestMapping("/User_queryForObject_findUserInfoById")
	public void findUserInfoById(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSONObject.parseObject(userJson);
			Map<String, Object> map = getParameterMap();
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "findStaffByShopIdAndUserId");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			output("0000", reMap);
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
	 * @date 2018年8月13日 - 下午9:59:33
	 * @description:获取店铺所有员工
	 */
	@RequestMapping("/Staff_queryForList_findStaffList")
	public void loadTopMenus(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSONObject.parseObject(userJson);
			Map<String, Object> map = getParameterMap();
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "findStaffByShopId");
			List<Map<String, Object>> reMap = openService.queryForList(map);
			output("0000", reMap);
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
	 * @date 2018年8月13日 - 下午9:59:49
	 * @description:员工添加二维码
	 */
	@RequestMapping("/Staff_add_getCodeToRes")
	public void getCodeToRes(HttpServletRequest request, HttpSession session) {
		try {

			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSONObject.parseObject(userJson);

			String params = "?SHOP_ID=" + userObj.getString("FK_SHOP");
			if (!userObj.containsKey("FK_APP")) {
				userObj.put("FK_APP", CommonUtil.getPath("AppID"));
			}
			params += "&ROLE_ID=4";
			String url = CommonUtil.getPath("Auth-wx-qrcode-url-plat");
			url = url.replace("APPID", userObj.getString("FK_APP")).replace("REDIRECT_URI", URLEncoder
					.encode(CommonUtil.getPath("project_url").replace("DATA", "Staff_add_addStaff") + params, "UTF-8"));

			BufferedImage image = QRCode.genBarcode(url, 200, 200);
			response.setContentType("image/png");
			response.setHeader("pragma", "no-cache");
			response.setHeader("cache-control", "no-cache");
			response.reset();
			ImageIO.write(image, "png", response.getOutputStream());
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
	 * @date 2018年8月13日 - 下午10:00:01
	 * @description:员工添加逻辑
	 */
	@RequestMapping("/Staff_add_addStaff")
	public void addStaff(HttpServletRequest request, HttpSession session) {
		txManagerController.createTxManager();
		try {
			String code = request.getParameter("code");
			String appid = request.getParameter("appid");
			Map<String, Object> map = getParameterMap();
			map.put("FK_APP", appid);
			if (null != code && !"".equals(code)) {
				String openId = wxCtrl.getOpenIdByCode2(code, appid);
				map.put("OPENID", openId);
				map.put("sqlMapId", "checkUserWx");
				// 判断是否之前已注册
				Map<String, Object> check = (Map<String, Object>) openService.queryForObject(map);
				Map<String, Object> userMap = new HashMap<>();
				String USER_PK = "";
				userMap.put("USER_WX", openId);
				userMap.put("OPENID", openId);
				if (check == null || !check.containsKey("USER_PK")) {
					// 未注册获取详细信息 进行注册
					wxCtrl.getWxUserInfo(openId, userMap);
					userMap.put("sqlMapId", "insertUserInitOpenId");
					logger.info("userMap : " + userMap);
					USER_PK = openService.insert(userMap);
				} else {
					USER_PK = check.get("USER_PK").toString();
				}
				System.out.println("USER_PK====" + USER_PK);
				if (StringUtils.isNotBlank(USER_PK)) {
					map.put("tagName", "店员端");
					map.put("USER_ID", USER_PK);
					map.put("sqlMapId", "checkUserShop");
					logger.info("查询是否已是该店铺店员");
					Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
					if (Integer.valueOf(reMap.get("nums").toString()) > 0) {
						String btnToken = UUID.randomUUID().toString();
						JSONObject obj = new JSONObject();
						obj.put("status", 9999);
						obj.put("msg", "该用户已是该店铺店员，请勿重复添加！");
						obj.put("data", new ArrayList<JSONObject>());
						// 保存button信息
						jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());

						response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
								"#toOtherPage/msgPage/" + btnToken));
						return;
					}
					map.put("sqlMapId", "insertUserShop");
					String res = openService.insert(map);
					if (res != null) {
						String token = "";
						if (!jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + appid)) {
							token = WxUtil.getWxAccessToken(appid, interCtrl.getComponentAccessToken(),
									interCtrl.getRefreshTokenByAppId(appid));
							jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + appid, token);
							jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + appid, 3600 * 1);
						} else {
							token = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + appid);
						}
						logger.info("apptoken===" + token);
						if (token != null) {
							String tagAddURL = CommonUtil.getPath("user_tag_add");
							tagAddURL = tagAddURL.replace("ACCESS_TOKEN", token);
							JSONObject postObj = new JSONObject();
							map.put("USER_TAG_NAME", "店员端");
							map.put("sqlMapId", "findUserTagIdByUserTagName");
							Map<String, Object> resMap = (Map<String, Object>) openService.queryForObject(map);
							postObj.put("tagid", resMap == null ? "" : resMap.get("USER_TAG_ID").toString());
							postObj.put("openid_list", new ArrayList<String>() {
								{
									add(userMap.get("USER_WX").toString());
								}
							});
							System.out.println("tagAddURL====" + tagAddURL);
							String resCont = CommonUtil.posts(tagAddURL, postObj.toJSONString(), "utf-8");
							System.out.println("resCont====" + resCont);
							JSONObject resObj = JSONObject.parseObject(resCont);
							if (WxConstants.ERRORCODE_0.equals(resObj.getString("errcode"))) {
								// 重定向成功页面
								String btnToken = UUID.randomUUID().toString();
								JSONObject obj = new JSONObject();
								obj.put("status", 0000);
								obj.put("msg", "注册店员成功！");
								obj.put("data", new ArrayList<JSONObject>() {
									{
										JSONObject btn1 = new JSONObject();
										btn1.put("buttonName", "登录");
										btn1.put("buttonLink",
												CommonUtil.getPath("project_url").replace("json/DATA.json", ""));
										add(btn1);
									}
								});
								// 保存button信息
								jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());

								// redis存储用户登录信息
								jedisClient.set(RedisConstants.REDIS_USER_SESSION_KEY + openId,
										JSONObject.toJSONString(userMap));

								// 添加写cookie的逻辑，cookie的有效期是关闭浏览器就失效。
								CookieUtils.setCookie(request, response, "DCXT_TOKEN", openId);
								txManagerController.commit();
								response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
										"#toOtherPage/msgPage/" + btnToken));
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
								String btnToken = UUID.randomUUID().toString();
								JSONObject obj = new JSONObject();
								obj.put("status", 9999);
								obj.put("msg", "请先关注公众号后再注册店员！");
								obj.put("data", new ArrayList<JSONObject>());
								// 保存button信息
								jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());

								response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
										"#toOtherPage/msgPage/" + btnToken));
								return;
							}
						} else {
							throw new RuntimeException("404");
						}
					}
				} else {
					String btnToken = UUID.randomUUID().toString();
					JSONObject obj = new JSONObject();
					obj.put("status", 9999);
					obj.put("msg", "注册店员失败！请重试！");
					obj.put("data", new ArrayList<JSONObject>());
					// 保存button信息
					jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());

					response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
							"#toOtherPage/msgPage/" + btnToken));
					return;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			txManagerController.rollback();
		}
		String btnToken = UUID.randomUUID().toString();
		JSONObject obj = new JSONObject();
		obj.put("status", 9999);
		obj.put("msg", "用户授权失败！");
		obj.put("data", new ArrayList<JSONObject>());
		// 保存button信息
		jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());
		try {
			response.sendRedirect(
					CommonUtil.getPath("project_url").replace("json/DATA.json", "#toOtherPage/msgPage/" + btnToken));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
