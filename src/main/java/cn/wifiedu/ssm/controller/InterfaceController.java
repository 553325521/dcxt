package cn.wifiedu.ssm.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.WxUtil;
import cn.wifiedu.ssm.util.qq.weixin.AesException;
import cn.wifiedu.ssm.util.qq.weixin.WXBizMsgCrypt;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * @author kqs
 * @time 2018年8月1日 - 上午10:11:04
 * @description:
 */
@Controller
@Scope("prototype")
public class InterfaceController extends BaseController {

	private static Logger logger = Logger.getLogger(InterfaceController.class);

	// 第三方平台组件加密密钥
	public final String encodingAesKey = "hj4yOdwhgrhLQIkYPrm7It1idg3arP92Q59lA65z3HE";
	// 第三方平台组件token
	public final String component_token = "ddera";
	// 第三方平台组件appid
	public final String component_appid = "wx623296bf9fc03f81";
	// 第三方平台组件secret
	public final String component_secret = "cfe18cc292ad99b2a3c44eb0b3c88938";

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
	private UserTagController userTagCtrl;

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月1日 - 下午6:06:01
	 * @description:接受微信推送的Ticket
	 */
	@RequestMapping(value = "/getComponentVerifyTicket")
	@ResponseBody
	public void getComponentVerifyTicket(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("微信第三方平台---------微信推送Ticket消息10分钟一次-----------" + StringDeal.getStringDate());
			processAuthorizeEvent(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		output2(response, "success");
	}

	/**
	 * @author kqs
	 * @param request
	 * @return void
	 * @date 2018年8月1日 - 下午1:42:05
	 * @description:对微信发来的报文进行处理
	 */
	private void processAuthorizeEvent(HttpServletRequest request) {
		String nonce = request.getParameter("nonce");
		String timestamp = request.getParameter("timestamp");
		String signature = request.getParameter("signature");
		String msgSignature = request.getParameter("msg_signature");

		if (StringUtils.isBlank(msgSignature)) {
			return;
		}

		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader in = request.getReader();

			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			String xml = sb.toString();

			WXBizMsgCrypt pc = new WXBizMsgCrypt(component_token, encodingAesKey, component_appid);

			xml = pc.decryptMsg(msgSignature, timestamp, nonce, xml);

			processAuthorizationEvent(xml);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AesException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * @author kqs
	 * @param xml
	 * @return void
	 * @date 2018年8月1日 - 下午2:01:59
	 * @description:保存Ticket
	 */
	private void processAuthorizationEvent(String xml) {
		Document doc;
		try {
			doc = DocumentHelper.parseText(xml);
			Element rootElt = doc.getRootElement();
			String ticket = rootElt.elementText("ComponentVerifyTicket");
			if (StringUtils.isNotBlank(ticket)) {
				logger.info("ticket = " + ticket);

				Map<String, Object> map = getParameterMap();
				map.put("TICKET_CODE", ticket);
				map.put("INSERT_TIME", StringDeal.getStringDate());
				map.put("sqlMapId", "insertTicket");
				openService.insert(map);

			} else {
				logger.warn("ticket is null");
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (ExceptionVo e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param session
	 * @return
	 * @return String
	 * @date 2018年8月1日 - 下午11:09:05
	 * @description:获取ComponentAccessToken
	 */
	public String getComponentAccessToken() {
		try {
			if (jedisClient.isExit(RedisConstants.WX_COMPONENT_ACCESS_TOKEN)
					&& StringUtils.isNotBlank(jedisClient.get(RedisConstants.WX_COMPONENT_ACCESS_TOKEN))) {
				return jedisClient.get(RedisConstants.WX_COMPONENT_ACCESS_TOKEN);
			}
			Map<String, Object> map = new HashMap<>();
			map.put("sqlMapId", "getNewTicket");
			map = (Map<String, Object>) openService.queryForObject(map);
			String ticket = map.get("TICKET_CODE").toString();
			if (StringUtils.isNotBlank(ticket)) {
				JSONObject json = new JSONObject();
				json.put("component_appid", component_appid);
				json.put("component_appsecret", component_secret);
				json.put("component_verify_ticket", ticket);

				String url = CommonUtil.getPath("getWxPlatFormTokenUrl").toString();

				/** 发送Https请求到微信 */
				String retStr = CommonUtil.posts(url, json.toString(), "utf-8");

				JSONObject resultJson = JSON.parseObject(retStr);
				logger.info("resultJson ==== " + resultJson.toString());

				/** 在返回结果中获取token */
				String componentAccessToken = resultJson.getString("component_access_token");

				logger.info("componentAccessToken ==== " + componentAccessToken);

				jedisClient.set(RedisConstants.WX_COMPONENT_ACCESS_TOKEN, componentAccessToken);

				// 设置componentAccessToken的过期时间1小时
				jedisClient.expire(RedisConstants.WX_COMPONENT_ACCESS_TOKEN, 3600 * 1);

				return componentAccessToken;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @author kqs
	 * @param session
	 * @return
	 * @return String
	 * @date 2018年8月1日 - 下午11:09:05
	 * @description:获取ComponentAccessToken
	 */
	public String getComponentToken() {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("sqlMapId", "getNewTicket");
			map = (Map<String, Object>) openService.queryForObject(map);
			String ticket = map.get("TICKET_CODE").toString();
			if (StringUtils.isNotBlank(ticket)) {
				JSONObject json = new JSONObject();
				json.put("component_appid", component_appid);
				json.put("component_appsecret", component_secret);
				json.put("component_verify_ticket", ticket);

				String url = CommonUtil.getPath("getWxPlatFormTokenUrl").toString();

				/** 发送Https请求到微信 */
				String retStr = CommonUtil.posts(url, json.toString(), "utf-8");

				JSONObject resultJson = JSON.parseObject(retStr);
				logger.info("resultJson ==== " + resultJson.toString());

				/** 在返回结果中获取token */
				String componentAccessToken = resultJson.getString("component_access_token");

				logger.info("componentAccessToken ==== " + componentAccessToken);

				jedisClient.set(RedisConstants.WX_COMPONENT_ACCESS_TOKEN, componentAccessToken);

				// 设置componentAccessToken的过期时间1小时
				jedisClient.expire(RedisConstants.WX_COMPONENT_ACCESS_TOKEN, 3600 * 1);

				return componentAccessToken;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @author kqs
	 * @param session
	 * @return
	 * @return String
	 * @date 2018年8月1日 - 下午11:09:29
	 * @description:获取ComponentPreAuthCode
	 */
	public String getComponentPreAuthCode() {
		try {
			if (jedisClient.isExit(RedisConstants.WX_COMPONENT_PRE_AUTH_CODE)
					&& StringUtils.isNotBlank(jedisClient.get(RedisConstants.WX_COMPONENT_PRE_AUTH_CODE))) {
				return jedisClient.get(RedisConstants.WX_COMPONENT_PRE_AUTH_CODE);
			}

			String url = CommonUtil.getPath("getWxPreAuthCode").toString();

			url = url.replace("componentAccessToken", getComponentAccessToken());

			JSONObject json = new JSONObject();
			json.put("component_appid", component_appid);

			/** 发送Https请求到微信 */
			String retStr = CommonUtil.posts(url, json.toString(), "utf-8");

			JSONObject resultJson = JSON.parseObject(retStr);
			/** 在返回结果中获取pre_auth_code */
			String componentPreAuthCode = resultJson.getString("pre_auth_code");

			jedisClient.set(RedisConstants.WX_COMPONENT_PRE_AUTH_CODE, componentPreAuthCode);

			// 设置componentPreAuthCode的过期时间2小时
			jedisClient.expire(RedisConstants.WX_COMPONENT_PRE_AUTH_CODE, 3600 * 1);

			return componentPreAuthCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @author kqs
	 * @param @param
	 *            request
	 * @param @param
	 *            session
	 * @return void
	 * @date 2018年7月18日 - 上午11:32:19
	 * @description:新增菜单
	 */
	@RequestMapping("/Menu_check_checkAppByUser")
	public void checkAppByUser(HttpServletRequest request) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			// 如果这个用户有了一个对应的公众号appid 且这个appid 不是总平台id 就让他授权小程序
			if (userObj.containsKey("FK_APP") && !(CommonUtil.getPath("AppID")).equals(userObj.getString("FK_APP"))) {
				Map<String, Object> map = new HashMap<>();
				map.put("FK_APP", userObj.getString("FK_APP"));
				map.put("sqlMapId", "chechSmallAppByApp");
				map = (Map<String, Object>) openService.queryForObject(map);
				if (map == null || !map.containsKey("SMALL_APP")) {
					output("0000", getPlatFormAuthorizedCodeSmall());
					return;
				}
				output("0000", "");
				return;
			} else {
				output("9999", getPlatFormAuthorizedCode());
				return;
			}
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
	 * @date 2018年8月5日 - 上午11:19:18
	 * @description:获取微信授权连接
	 */
	public String getPlatFormAuthorizedCode() {
		try {

			String url = CommonUtil.getPath("AuthWxPlatFormUrl").toString();

			url = url.replace("componentAppid", component_appid).replace("preAuthCode", getComponentPreAuthCode())
					.replace("authType", "3")
					.replace("redirectUri", CommonUtil.getPath("project_url").toString().replace("DATA", "getRes"));
			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月5日 - 上午11:19:18
	 * @description:获取小程序授权连接
	 */
	public String getPlatFormAuthorizedCodeSmall() {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			String url = CommonUtil.getPath("AuthWxPlatFormUrl").toString();

			url = url.replace("componentAppid", component_appid).replace("preAuthCode", getComponentPreAuthCode())
					.replace("authType", "2")
					.replace("redirectUri", CommonUtil.getPath("project_url").toString().replace("DATA", "getRes")
							+ "?appid=" + userObj.getString("FK_APP"));
			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @return void
	 * @date 2018年9月3日 - 上午10:31:56
	 * @description:微信、小程序授权逻辑
	 */
	@RequestMapping(value = "/getRes")
	@ResponseBody
	public void getRes(HttpServletRequest request) {
		try {
			String authorization_code = request.getParameter("auth_code");
			String appid = request.getParameter("appid");
			logger.info("status app authorization");
			logger.info("authorization_code:" + authorization_code);
			logger.info("type:" + appid);
			if (StringUtils.isNotBlank(authorization_code)) {
				String url = CommonUtil.getPath("getWxPlatFormInfoURL").toString();
				url = url.replace("componentAccessToken", getComponentAccessToken());
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("component_appid", component_appid);
				jsonObj.put("authorization_code", authorization_code);
				String resContent = CommonUtil.posts(url, jsonObj.toJSONString(), "utf-8");
				if (StringUtils.isNotBlank(resContent)) {
					JSONObject resObj = JSON.parseObject(resContent);
					JSONObject obj = JSON.parseObject(resObj.get("authorization_info").toString());
					String authorizer_appid = obj.getString("authorizer_appid");
					String authorizer_access_token = obj.getString("authorizer_access_token");
					String authorizer_refresh_token = obj.getString("authorizer_refresh_token");

					jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + authorizer_appid, authorizer_access_token);

					Map<String, Object> map = getParameterMap();
					map.put("APP_PK", authorizer_appid);
					if (StringUtils.isNotBlank(appid)) {
						map.put("FK_APP", appid);
					}
					map.put("APP_REFRESH_TOKEN", authorizer_refresh_token);
					map.put("CREATE_TIME", StringDeal.getStringDate());
					map.put("sqlMapId", "insertApp");
					openService.insert(map);
					if (StringUtils.isBlank(appid)) {
						// 创建店员端标签
						String tagId = userTagCtrl.createTagForAppId(authorizer_appid, authorizer_access_token);

						// TODO 创建店员端菜单 用户端菜单
					}
					String btnToken = UUID.randomUUID().toString();
					JSONObject objj = new JSONObject();
					objj.put("status", 0000);
					objj.put("msg", "授权成功！请重新登录！");
					objj.put("data", new ArrayList<JSONObject>());
					// 保存button信息
					jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, objj.toJSONString());
					output2(response, "success");
					response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
							"#toOtherPage/msgPage/" + btnToken));

					return;
				} else {
					String btnToken = UUID.randomUUID().toString();
					JSONObject obj = new JSONObject();
					obj.put("status", 9999);
					obj.put("msg", "授权失败！请重试！");
					obj.put("data", new ArrayList<JSONObject>());
					// 保存button信息
					jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());
					output2(response, "success");
					response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
							"#toOtherPage/msgPage/" + btnToken));
					return;
				}
			} else {
				logger.warn("获取授权码失败");

				String btnToken = UUID.randomUUID().toString();
				JSONObject obj = new JSONObject();
				obj.put("status", 9999);
				obj.put("msg", "获取授权码失败！请重试！");
				obj.put("data", new ArrayList<JSONObject>());
				// 保存button信息
				jedisClient.set(RedisConstants.WX_BUTTON_TOKEN + btnToken, obj.toJSONString());
				output2(response, "success");
				response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json",
						"#toOtherPage/msgPage/" + btnToken));
				return;
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
		output2(response, "success");
		return;
	}

	/**
	 * @author kqs
	 * @param string
	 * @return
	 * @return String
	 * @date 2018年8月8日 - 上午11:43:43
	 * @description:根据appid 获取 refresh_token
	 */
	public String getRefreshTokenByAppId(String appid) {
		try {
			Map<String, Object> obj = (Map<String, Object>) openService.queryForObject(new HashMap<String, Object>() {
				{
					put("APP_PK", appid);
					put("sqlMapId", "getRefreshTokenByAppId");
				}
			});

			return obj.get("APP_REFRESH_TOKEN").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @author kqs
	 * @param response
	 * @param returnvaleue
	 * @return void
	 * @date 2018年8月3日 - 下午1:12:25
	 * @description:回复微信服务器"文本消息"
	 */
	public void output2(HttpServletResponse response, String returnvaleue) {
		try {
			PrintWriter pw = response.getWriter();
			pw.write(returnvaleue);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月1日 - 下午6:06:01
	 * @description:接受小程序用户认证
	 */
	@RequestMapping(value = "/getSmallUserInfo")
	@ResponseBody
	public void getSmallUserInfo(HttpServletRequest request, HttpServletResponse response) {
		try {
			String code = request.getParameter("code");
			String appid = request.getParameter("appid");
			logger.info("微信小程序用户认证---------获取到临时code:" + code + ", 接收时间:" + StringDeal.getStringDate());
			// processAuthorizeEvent(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		output2(response, "success");
	}
	public Element processAuthorizeEvent1(String APPID, HttpServletRequest request) {
		String nonce = request.getParameter("nonce");
		String timestamp = request.getParameter("timestamp");
		String signature = request.getParameter("signature");
		String msgSignature = request.getParameter("msg_signature");

		if (StringUtils.isBlank(msgSignature)) {
			return null;
		}
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader in = request.getReader();

			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			String xml = sb.toString();
			
			String accessToken = "";

			if (!jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + APPID)) {
				accessToken = WxUtil.getWxAccessToken(APPID, this.getComponentAccessToken(),
						this.getRefreshTokenByAppId(APPID));
				jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + APPID, accessToken);
				jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + APPID, 3600 * 1);
			} else {
				accessToken = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + APPID);
			}
			WXBizMsgCrypt pc = new WXBizMsgCrypt(component_token, encodingAesKey, component_appid);
			xml = pc.decryptMsg(msgSignature, timestamp, nonce, xml);
			Document doc = DocumentHelper.parseText(xml);
			Element rootElt = doc.getRootElement();
			System.out.println(xml);
			return rootElt;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AesException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
