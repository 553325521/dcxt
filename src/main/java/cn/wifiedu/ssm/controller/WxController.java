package cn.wifiedu.ssm.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.util.SessionUtil;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.QRCode;
import cn.wifiedu.ssm.util.WXJSUtil;
import cn.wifiedu.ssm.util.WxUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * 微信与数据库交互相关
 * 
 * @author JH_L
 *
 */
@Controller
@Scope("prototype")
public class WxController extends BaseController {

	private static Logger logger = Logger.getLogger(WxController.class);

	@Resource
	OpenService openService;

	@Resource
	MenuController menucontroller;

	@Resource
	InterfaceController interfaceController;

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

	@RequestMapping("/Qrcode_testQrcode_data")
	public void testQrcode(HttpServletRequest request, HttpServletResponse reponse) {
		try {
			Map<String, Object> map = getParameterMap();
			logger.info(map + "");
			// String url =
			// "http://localhost:8088/dcxt/json/Qrcode_qrauth_data.json";
			String url = CommonUtil.getPath("project_url").replace("DATA", "Qrcode_testQrcodeJieShou_data");
			logger.info("myUrl:" + url);
			CommonUtil.qrCode(url);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	@RequestMapping("/Qrcode_testQrcodeJieShou_data")
	public void testQrcodeJieShou() {
		try {
			Map<String, Object> map = getParameterMap();
			logger.info(map + "");

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * 处理参数 带参跳转回参数URL
	 */
	@RequestMapping("/Qrcode_qrCommonAuth_data")
	public void qrCommonAuth() {
		try {
			String code = request.getParameter("code");
			if (null != code && !"".equals(code)) {
				String openId = getOpenIdByCode(code);
				logger.info("WeChart openId : " + openId);

				String state = request.getParameter("state");
				logger.info("WeChart params : " + state);

				Map<String, Object> map = getParameterMap();
				map.put("OPENID", openId);
				map.put("sqlMapId", "checkUserWx");

				List<Map<String, Object>> checkList = openService.queryForList(map);
				logger.info("checkList: " + checkList);

				String allParams = "openId=" + openId;
				String redirectUrl = "";
				if (checkList.size() == 1) {
					String params1[] = state.split("-");
					for (int i = 0; i < params1.length; i++) {
						String params2[] = params1[i].split("_");
						if (!params2[0].equals("rEdIrEcTuRi")) {
							allParams = allParams + "&" + params2[0] + "=" + params2[1];
						} else {
							for (int j = 1; j < params2.length; j++) {
								redirectUrl = redirectUrl + "_" + params2[j];
							}
							redirectUrl = redirectUrl.substring(1, redirectUrl.length());
						}
					}
					String result = redirectUrl + "?" + allParams;
					logger.info("result: " + result);
					response.sendRedirect(result);
				} else if (checkList.size() == 0) {
					// 如果用户没绑定微信，跳转到登陆页
					logger.info(openId + "_user_no_wx");
					request.getSession().removeAttribute("userInfo");
					session.removeAttribute("userInfo");
					request.getSession().setAttribute("openid", map.get("OPENID").toString());
					session.setAttribute("openid", map.get("OPENID").toString());
					response.sendRedirect(
							CommonUtil.getPath("project_url").replace("json/DATA.json", "") + "/qrcode_error.jsp");
				} else {
					output("error");
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 二维码统一跳到此处 获取扫码人openId
	 */
	@RequestMapping("/Qrcode_qrCommon_data")
	public void qrCommon() {
		try {
			if (null != request.getParameter("redirect_qrcode")) {
				String url = CommonUtil.getPath("Auth-wx-qrcode-url");
				url = url.replace("STATE", request.getParameter("params")).replace("REDIRECT_URI", URLEncoder.encode(
						CommonUtil.getPath("project_url").replace("DATA", "Qrcode_qrCommonAuth_data"), "UTF-8"));
				logger.info("qrcodeURL:" + url);
				response.sendRedirect(url);
			} else {
				output("无效二维码");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 页面授权根据参数回调响应页面
	 */
	@RequestMapping("/Wxcode_ymsqCommon_data")
	public void ymsqCommon() {
		try {
			String url = CommonUtil.getPath("Auth-wx-qrcode-url-plat");
			url = url.replace("APPID", request.getParameter("appid")).replace("REDIRECT_URI", URLEncoder.encode(
					CommonUtil.getPath("project_url").replace("DATA", request.getParameter("params")), "UTF-8"));
			logger.info("qrcodeURL:" + url);
			response.sendRedirect(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 小程序授权
	 */
	@RequestMapping("/toSmallProgram")
	public void toSmallProgram() {
		try {
			String code = request.getParameter("code");
			String appid = request.getParameter("appid");
			logger.info("request params:" + getParameterMap());
			logger.info("request code:" + code + ", request appid:" + appid);
			if (null != code && !"".equals(code)) {
				String openId = getOpenIdByCodeForMini(code, appid);
				logger.info("WeChart openId : " + openId);
				Map<String, Object> map = getParameterMap();
				map.put("OPENID", openId);
				map.put("sqlMapId", "checkUserWx");

				List<Map<String, Object>> checkList = openService.queryForList(map);
				logger.info("checkList: " + checkList);
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("USER_WX", openId);
				userMap.put("FK_APP", appid);
				if (checkList != null && checkList.size() == 0) {
					// 查询 openId 根据 用户详细信息
					//getWxUserInfo(openId, userMap);
					map.put("USER_UNIONID", jedisClient.get(RedisConstants.WX_UNIONID + openId));
					map.put("sqlMapId", "insertUserInitOpenIdMini");

					String USER_PK = openService.insert(map);

					userMap.put("USER_PK", USER_PK);
				} else {
					// 根据openId 获取 系统中的 商铺、权限、功能
					getUserInfo(openId, userMap);
				}
				// redis存储用户登录信息
				jedisClient.set(RedisConstants.REDIS_USER_SESSION_KEY + openId, JSONObject.toJSONString(userMap));

				// 添加写cookie的逻辑，cookie的有效期是关闭浏览器就失效。
				CookieUtils.setCookie(request, response, "DCXT_TOKEN", openId);

				output("0000", openId);
				return;
			}
			output("9999", "error");
			return;
		} catch (Exception e) {
			logger.error("error", e);
			output("9999", e);
		}
		return;
	}

	/**
	 * 跳转店员页面
	 */
	@RequestMapping("/welcome")
	public void welcome() {
		try {
			String code = request.getParameter("code");
			String appid = request.getParameter("appid");
			if (null != code && !"".equals(code)) {
				String openId = getOpenIdByCode2(code, appid);
				logger.info("WeChart openId : " + openId);

				Map<String, Object> map = getParameterMap();
				map.put("OPENID", openId);
				map.put("sqlMapId", "checkUserWx");

				List<Map<String, Object>> checkList = openService.queryForList(map);
				logger.info("checkList: " + checkList);
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("USER_WX", openId);
				userMap.put("FK_APP", appid);
				if (checkList != null && checkList.size() == 0) {
					// 查询 openId 根据 用户详细信息
					getWxUserInfo(openId, userMap);

					map.put("sqlMapId", "insertUserInitOpenId");

					String USER_PK = openService.insert(map);

					userMap.put("USER_PK", USER_PK);
				} else {
					// 根据openId 获取 系统中的 商铺、权限、功能
					getUserInfo(openId, userMap);
				}
				// redis存储用户登录信息
				jedisClient.set(RedisConstants.REDIS_USER_SESSION_KEY + openId, JSONObject.toJSONString(userMap));

				// 添加写cookie的逻辑，cookie的有效期是关闭浏览器就失效。
				CookieUtils.setCookie(request, response, "DCXT_TOKEN", openId);

				String url = CommonUtil.getPath("project_url").replace("json/DATA.json", "");
				response.sendRedirect(url);
			} else {
				// 生成token
				String btnToken = UUID.randomUUID().toString();
				JSONObject obj = new JSONObject();
				obj.put("status", 9999);
				obj.put("msg", "获取微信授权失败！请重新登录！");
				obj.put("data", new ArrayList<JSONObject>());

				// 保存button信息
				jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());

				response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
						"#toOtherPage/msgPage/" + btnToken));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 跳转代理设置页面
	 */
	@RequestMapping("/ActingCustomerManagement")
	public void ActingCustomerManagement() {
		try {
			String code = request.getParameter("code");
			if (null != code && !"".equals(code)) {
				String openId = getOpenIdByCode(code);
				logger.info("WeChart openId : " + openId);
				Map<String, Object> map = getParameterMap();
				map.put("OPENID", openId);
				map.put("sqlMapId", "checkUserWx");
				List<Map<String, Object>> checkList = openService.queryForList(map);
				logger.info("checkList: " + checkList);
				Map<String, Object> userMap = new HashMap<>();
				if (checkList != null && checkList.size() == 0) {
					// 用户第一次授权
					// 查询 openId 根据 用户详细信息
					getWxUserInfo(openId, userMap);
					map.put("sqlMapId", "insertUserInitOpenId");
					String USER_PK = openService.insert(map);
					userMap.put("USER_PK", USER_PK);
				} else {
					userMap.putAll(checkList.get(0));
				}
				userMap.put("USER_WX", openId);
				userMap.put("FK_ROLE", "7");
				userMap.put("FK_USER_TAG", "141");
				// redis存储用户登录信息
				jedisClient.set(RedisConstants.REDIS_USER_SESSION_KEY + openId, JSONObject.toJSONString(userMap));

				// 添加写cookie的逻辑，cookie的有效期是关闭浏览器就失效。
				CookieUtils.setCookie(request, response, "DCXT_TOKEN", openId);

				String url = CommonUtil.getPath("project_url").replace("json/DATA.json",
						"#ActingCustomerManagement/ActingCustomerManagement");

				response.sendRedirect(url);
			} else {
				// 生成token
				String btnToken = UUID.randomUUID().toString();
				JSONObject obj = new JSONObject();
				obj.put("status", 9999);
				obj.put("msg", "获取微信授权失败！");
				obj.put("data", new ArrayList<JSONObject>());

				// 保存button信息
				jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());

				response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
						"#toOtherPage/msgPage/" + btnToken));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author kqs
	 * @param openId
	 * @param userMap
	 * @return void
	 * @date 2018年8月5日 - 下午4:25:55
	 * @description:根据openId 获取 系统中的 商铺、权限、功能
	 */
	private void getUserInfo(String openId, Map<String, Object> userMap) {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("OPENID", openId);
			map.put("FK_APP", userMap.get("FK_APP"));
			map.put("sqlMapId", "selectUserInfo");
			map = (Map<String, Object>) openService.queryForObject(map);
			if (map != null) {
				map.remove("OPENID");
				map.remove("sqlMapId");
				userMap.putAll(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getWxUserInfo(String openId, Map<String, Object> map) {
		try {
			String url = CommonUtil.getPath("wx_userinfo_get_url");

			String accessToken = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + openId);

			url = url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);

			String res = CommonUtil.get(url);
			logger.error("获取用户基本信息时候accessToken:" + accessToken);
			logger.error("获取用户基本信息时候res:" + res);
			if (res != null && res.indexOf("errcode") <= 0) {
				logger.info("userInfo====" + res);
				res = new String(res.getBytes("ISO-8859-1"), "UTF-8");
				logger.info("userInfo====" + res);
				JSONObject obj = JSONObject.parseObject(res);
				map.put("USER_SN", obj.getString("nickname"));
				map.put("USER_SEX", obj.getString("sex"));
				// map.put("province", obj.get("province"));
				// map.put("city", obj.get("city"));
				// map.put("country", obj.get("country"));
				map.put("USER_HEAD_IMG", obj.getString("headimgurl"));
				map.put("USER_WX_REFRESH_TOKEN", jedisClient.get(RedisConstants.WX_REFRESH_TOKEN + openId));
			} else {
				logger.error("获取用户基本信息失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param code
	 * @return
	 * @return String
	 * @date 2018年8月8日 - 下午4:03:38
	 * @description:根据第三方平台基础获取openId
	 */
	public String getOpenIdByCode2(String code, String appid) {
		String url = CommonUtil.getPath("WX_GET_OPENID_URL-plat");
		url = url.replace("CODE", code).replace("APPID", appid).replace("COMPONENT_ACCESS_TOKEN",
				interCtrl.getComponentAccessToken());
		logger.info("getOpenIdByCode url:" + url);
		String res = CommonUtil.get(url);
		logger.info("getOpenIdByCode response:" + res);
		JSONObject succesResponse = JSONObject.parseObject(res);

		String openId = succesResponse.getString("openid");

		String refresh_token = succesResponse.getString("refresh_token");

		String access_token = succesResponse.getString("access_token");

		// redis存储refresh_token
		jedisClient.set(RedisConstants.WX_REFRESH_TOKEN + openId, refresh_token);
		// jedisClient.expire(RedisConstants.WX_REFRESH_TOKEN + openId, 1000 *
		// 60 * 60 * 24 * 30);

		// redis存储access_token信息
		jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + openId, access_token);
		// 设置access_token的过期时间2小时
		jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + openId, 3600 * 1);

		logger.info(appid + "====" + openId);
		return openId;
	}

	/**
	 * 
	 * @author kqs
	 * @param code
	 * @param appid
	 * @return
	 * @return String
	 * @date 2018年9月7日 - 上午10:54:43
	 * @description:小程序用户授权
	 */
	public String getOpenIdByCodeForMini(String code, String appid) {
		// String url = CommonUtil.getPath("wxSmallLoginURL");
		// url = url.replace("JSCODE", code).replace("APPID",
		// appid).replace("COMPONENT_ACCESS_TOKEN",
		// interCtrl.getComponentAccessToken());
		String url = CommonUtil.getPath("wxSmallLoginURLtemp");
		url = url.replace("JSCODE", code).replace("APPID", appid).replace("COMPONENT_ACCESS_TOKEN",
				interCtrl.getComponentAccessToken());
		logger.info("getOpenIdByCode url:" + url);
		String res = CommonUtil.get(url);
		logger.info("getOpenIdByCode response:" + res);
		JSONObject succesResponse = JSONObject.parseObject(res);

		String openId = succesResponse.getString("openid");

		String session_key = succesResponse.getString("session_key");

		String unionid = succesResponse.getString("unionid");

		jedisClient.set(RedisConstants.WX_UNIONID + openId, unionid);

		jedisClient.set(RedisConstants.WX_SESSION_KEY + openId, session_key);

		logger.info(appid + "====" + openId);
		return openId;
	}

	public String getOpenIdByCode(String code) {
		String url = CommonUtil.getPath("WX_GET_OPENID_URL");
		url = url.replace("CODE", code);
		logger.info("getOpenIdByCode=" + url);
		String res = CommonUtil.get(url);
		Object succesResponse = JSON.parse(res);
		Map result = (Map) succesResponse;

		String openId = result.get("openid").toString();

		String refresh_token = result.get("refresh_token").toString();

		String access_token = result.get("access_token").toString();

		logger.info("openId:" + openId);
		logger.info("refresh_token:" + refresh_token);
		logger.info("access_token:" + access_token);
		// redis存储refresh_token
		jedisClient.set(RedisConstants.WX_REFRESH_TOKEN + openId, refresh_token);

		// redis存储access_token信息
		jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + openId, access_token);
		// 设置access_token的过期时间2小时
		jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + openId, 3600 * 1);
		return openId;
	}

	/**
	 * 用户扫码后跳转到updateUserRole方法 经过微信验证，获取到用户openId 跳转回此方法
	 */
	@RequestMapping("/Qrcode_qrauth_data")
	public void setUserRole() {
		try {
			String code = request.getParameter("code");
			if (null != code && !"".equals(code)) {
				String openId = getOpenIdByCode(code);
				logger.info("WeChart openId : " + openId);

				String state = request.getParameter("state");
				logger.info("WeChart COURSE : " + state);

				Map<String, Object> map = getParameterMap();
				map.put("OPENID", openId);
				map.put("ROLE_PK", state);
				map.put("sqlMapId", "checkUserWx");
				Object userInfo = null;

				List<Map<String, Object>> checkList = openService.queryForList(map);
				logger.info("checkList: " + checkList);
				if (checkList.size() == 1) {
					userInfo = openService.queryForObject(map);
					session.setAttribute("userInfo", userInfo);
					request.getSession().setAttribute("userInfo", userInfo);

					String token = WxUtil.getToken();
					if (token != null) {
						if (!checkList.get(0).get("FK_USER_TAG").equals("NULL")) {
							// 取消用户微信标签
							String url = CommonUtil.getPath("user_tag_cancel").toString();
							url = url.replace("ACCESS_TOKEN", token);
							Map p = new HashMap();
							ArrayList<String> list = new ArrayList<String>();
							list.add(openId);
							p.put("openid_list", list);
							p.put("tagid", state);
							String resMsg = CommonUtil.posts(url, JSON.toJSONString(p), "utf-8");
							if (resMsg.indexOf("ok") <= 0) {
								logger.info("cancel user tag error: " + resMsg.trim());
								output("error");
							}
							logger.info("cancel user tag success: " + resMsg.trim());
						}

						// 改变数据库用户标签
						map.put("sqlMapId", "updateScUserTag");
						boolean flag = openService.update(map);
						if (!flag) {
							logger.info("update user_tag error");
							output("error");
						} else {
							logger.info("update user_tag success");
						}

						// 改变微信用户标签
						String url = CommonUtil.getPath("user_tag_add").toString();
						url = url.replace("ACCESS_TOKEN", token);
						Map p = new HashMap();
						ArrayList<String> list = new ArrayList<String>();
						list.add(openId);
						p.put("openid_list", list);
						p.put("tagid", state);
						String resMsg = CommonUtil.posts(url, JSON.toJSONString(p), "utf-8");
						logger.info("add user tag: " + resMsg.trim());
						if (resMsg.indexOf("ok") <= 0) {
							logger.info("add user tag error: " + resMsg.trim());
							output("error");
						} else {
							logger.info("add user tag success: " + resMsg.trim());
						}

						// 插入agent
						String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY
								+ CookieUtils.getCookieValue(request, "DCXT_TOKEN"));
						JSONObject userObj = JSONObject.parseObject(userJson);

						map.put("sqlMapId", "insertAgentInfoById");
						map.put("USER_ID", checkList.get(0).get("USER_PK").toString());
						map.put("CREATE_BY", userObj.get("USER_PK") + "");
						if (openService.insert(map).length() > 0) {
							logger.info("add user agent success");
						} else {
							logger.info("add user agent error");
						}
						;

						// 返回到成功页面
						response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json", "")
								+ "/qrcode_success.jsp");
					}

				} else if (checkList.size() == 0) {
					// 如果用户没绑定微信，跳转到登陆页
					logger.info(openId + "_user_no_wx");
					request.getSession().removeAttribute("userInfo");
					session.removeAttribute("userInfo");
					request.getSession().setAttribute("openid", map.get("OPENID").toString());
					session.setAttribute("openid", map.get("OPENID").toString());
					response.sendRedirect(
							CommonUtil.getPath("project_url").replace("json/DATA.json", "") + "/qrcode_error.jsp");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 输出微信扫描的二维码 更改用户标签 调用此方法传role_pk后生成二维码
	 */
	@RequestMapping("/Qrcode_qrcode_data")
	public void updateUserRole() {
		try {
			if (null != request.getParameter("redirect_qrcode")) {
				String url = CommonUtil.getPath("Auth-wx-qrcode-url");
				url = url.replace("STATE", request.getParameter("role_pk")).replace("REDIRECT_URI", URLEncoder
						.encode(CommonUtil.getPath("project_url").replace("DATA", "Qrcode_qrauth_data"), "UTF-8"));
				logger.info("qrcodeURL:" + url);
				response.sendRedirect(url);
			} else {
				String redirect_qrcode = session.getId();
				HttpSession webSession = SessionUtil.getSession(redirect_qrcode);
				if (null == webSession) {
					SessionUtil.addSession(session);
				}
				Map<String, Object> map = getParameterMap();

				logger.info("qrcodeMap:" + map);

				BufferedImage image = QRCode
						.genBarcode(
								CommonUtil.getPath("project_url").replace("DATA", "Qrcode_qrcode_data")
										+ "?redirect_qrcode=" + redirect_qrcode + "&role_pk=" + map.get("role_pk"),
								200, 200);
				response.setContentType("image/png");
				response.setHeader("pragma", "no-cache");
				response.setHeader("cache-control", "no-cache");
				response.reset();
				ImageIO.write(image, "png", response.getOutputStream());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取微信推送到服务器事件
	 */
	@RequestMapping("/{APPID}/portal")
	public void getUserInfo(HttpServletRequest request, HttpServletResponse reponse,@PathVariable String APPID) {
		try {
			Element returnElement  = interfaceController.processAuthorizeEvent1(APPID, request);
			/*领取卡券事件处理*/
			String eventStr = returnElement.elementText("Event");
			if(eventStr.equals("user_get_card")){
				String card_id = returnElement.elementText("CardId");
				String code_id = returnElement.elementText("UserCardCode");
				String openid = returnElement.elementText("FromUserName");
				String createTime = returnElement.elementText("CreateTime");
				String IsGiveByFriend = returnElement.elementText("IsGiveByFriend");
				String unionID = returnElement.elementText("UnionId");
				String OldUserCardCode = returnElement.elementText("OldUserCardCode");
				Map<String,Object> param = new HashMap<String,Object>();
				if(IsGiveByFriend.equals("0")){
					param.put("card_code", code_id);
					param.put("card_id", card_id);
					param.put("open_id", openid);
					param.put("create_time", createTime);
					param.put("unionid", unionID);
					param.put("sqlMapId", "insertUserCard");
					openService.insert(param);
				}else{
					param.put("card_code", OldUserCardCode);
					param.put("sqlMapId", "deleteUserCard");
					boolean deleteResult = openService.delete(param);
					if(deleteResult){
						logger.info("卡券转赠事件删除旧code成功");
						param.clear();
						param.put("card_code", code_id);
						param.put("card_id", card_id);
						param.put("open_id", openid);
						param.put("create_time", createTime);
						param.put("unionid", unionID);
						param.put("sqlMapId", "insertUserCard");
						openService.insert(param);
						logger.info("卡券转赠事件插入新code成功");
					}
				}
			/*转赠卡券处理*/
			}else if(eventStr.equals("user_gifting_card")){
				logger.info("============================卡券转赠事件处理============================");
			}else{
				logger.info("=============================无事件处理微信返回结果逻辑================================");
			}
			logger.info("领券方帐号"+returnElement.elementText("FromUserName"));
			logger.info("消息创建时间 "+returnElement.elementText("CreateTime"));
			logger.info("消息类型 "+returnElement.elementText("MsgType"));
			logger.info("事件类型 "+returnElement.elementText("Event"));
			logger.info("卡券ID"+returnElement.elementText("CardId"));
			logger.info("是否为转赠领取 "+returnElement.elementText("IsGiveByFriend"));
			logger.info("code序列号 "+returnElement.elementText("UserCardCode"));
			logger.info("领券用户的UnionId "+returnElement.elementText("UnionId"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 保存token到数据库
	 * 
	 * @param token
	 * @return
	 */
	@RequestMapping("/Wx_saveToken_data")
	public void saveToken(String token) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "saveAccessToken");
			openService.insert(map);

			output(WxUtil.getToken());
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "error");
		}
	}

	/**
	 * 获取最新的access_token
	 */
	@RequestMapping("/Wx_getToken_data")
	public void getToken() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "getAccessToken");
			Map<String, Object> wxMap = (Map<String, Object>) openService.queryForObject(map);
			output(wxMap.get("ACCESS_TOKEN").toString());
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "error");
		}
	}

	/**
	 * 根据btnToken 获取 btn信息
	 */
	@RequestMapping("/Wx_getBtnToken_data")
	public void getBtnToken() {
		try {
			Map<String, Object> map = getParameterMap();
			if (map.containsKey("LOCALPATH") && "msgPage".equals(map.get("LOCALPATH"))) {
				if (map.containsKey("btnToken") && StringUtils.isNotBlank(map.get("btnToken").toString())) {
					String btnToken = map.get("btnToken").toString();
					String json = jedisClient.get(RedisConstants.WX_BUTTON_TOKEN + btnToken);
					if (StringUtils.isNotBlank(json)) {
						jedisClient.del(RedisConstants.WX_BUTTON_TOKEN + btnToken);
						output("0000", JSONObject.parseObject(json));
					}
				}
			}
		} catch (Exception e) {
			output("9999", e);
		}
	}

	/**
	 * 
	 * @author lps
	 * @return
	 * @return String
	 * @description:根据appid获取JsApiTicket
	 */
	public String getJsApiTicket(String appid) {
		try {
			if (jedisClient.isExit(RedisConstants.WX_JS_API_Ticker + appid)
					&& StringUtils.isNotBlank(jedisClient.get(RedisConstants.WX_JS_API_Ticker + appid))) {
				return jedisClient.get(RedisConstants.WX_JS_API_Ticker + appid);
			}

			String accessToken = "";

			if (!jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + appid)) {
				accessToken = WxUtil.getWxAccessToken(appid, interfaceController.getComponentAccessToken(),
						interfaceController.getRefreshTokenByAppId(appid));
				jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + appid, accessToken);
				jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + appid, 3600 * 1);
			} else {
				accessToken = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + appid);
			}

			String url = CommonUtil.getPath("WX_GET_JSAPI_TICKET").replace("ACCESS_TOKEN", accessToken);
			/** 发送Https请求到微信 */
			String retStr = CommonUtil.get(url);

			JSONObject resultJson = JSONObject.parseObject(retStr);
			logger.info("resultJson ==== " + resultJson.toString());

			/** 在返回结果中获取token */
			String jsApiTicket = resultJson.getString("ticket");

			logger.info("jsApiTicket ==== " + jsApiTicket);

			jedisClient.set(RedisConstants.WX_JS_API_Ticker + appid, jsApiTicket);

			// 设置jsApiTicket的过期时间1小时
			jedisClient.expire(RedisConstants.WX_JS_API_Ticker + appid, 3600 * 1);

			return jsApiTicket;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @author lps
	 * @Description: 获取微信config信息
	 * @return void
	 *
	 */
	@RequestMapping("/Wx_getWxConfigMess")
	public void getWxConfig(HttpServletRequest request, HttpServletResponse reponse) {
		try {
			Map<String, Object> map = getParameterMap();
			String appId = CommonUtil.getPath("AppID");
			map.put("jsapi_ticket", getJsApiTicket(appId));
			map = WXJSUtil.getWxConfigMess(map);
			map.put("appId", appId);

			output("0000", map);
			return;
		} catch (ExceptionVo e) {
			e.printStackTrace();
			output("0000", e);
		}
	}
}
