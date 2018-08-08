package cn.wifiedu.ssm.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.StringDeal;
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
	private final String encodingAesKey = "hj4yOdwhgrhLQIkYPrm7It1idg3arP92Q59lA65z3HE";
	// 第三方平台组件token
	private final String component_token = "ddera";
	// 第三方平台组件appid
	private final String component_appid = "wx623296bf9fc03f81";
	// 第三方平台组件secret
	private final String component_secret = "cfe18cc292ad99b2a3c44eb0b3c88938";

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
	public String getComponentAccessToken(HttpSession session) {
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

				JSONObject resultJson = JSONObject.parseObject(retStr);
				System.out.println("resultJson ==== " + resultJson.toString());

				/** 在返回结果中获取token */
				String componentAccessToken = resultJson.getString("component_access_token");

				System.out.println("componentAccessToken ==== " + componentAccessToken);

				jedisClient.set(RedisConstants.WX_COMPONENT_ACCESS_TOKEN, componentAccessToken);

				// 设置componentAccessToken的过期时间1小时
				jedisClient.expire(RedisConstants.WX_COMPONENT_ACCESS_TOKEN, 1000 * 60 * 60 * 1);

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
	public String getComponentPreAuthCode(HttpSession session) {
		try {
			if (jedisClient.isExit(RedisConstants.WX_COMPONENT_PRE_AUTH_CODE)
					&& StringUtils.isNoneBlank(jedisClient.get(RedisConstants.WX_COMPONENT_PRE_AUTH_CODE))) {
				return jedisClient.get(RedisConstants.WX_COMPONENT_PRE_AUTH_CODE);
			}

			String url = CommonUtil.getPath("getWxPreAuthCode").toString();

			url = url.replace("componentAccessToken", getComponentAccessToken(session));

			JSONObject json = new JSONObject();
			json.put("component_appid", component_appid);

			/** 发送Https请求到微信 */
			String retStr = CommonUtil.posts(url, json.toString(), "utf-8");

			JSONObject resultJson = JSONObject.parseObject(retStr);
			/** 在返回结果中获取pre_auth_code */
			String componentPreAuthCode = resultJson.getString("pre_auth_code");

			jedisClient.set(RedisConstants.WX_COMPONENT_PRE_AUTH_CODE, componentPreAuthCode);

			// 设置componentPreAuthCode的过期时间2小时
			jedisClient.expire(RedisConstants.WX_COMPONENT_PRE_AUTH_CODE, 1000 * 60 * 60 * 1);

			return componentPreAuthCode;
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
	 * @description:获取微信授权连接
	 */
	@RequestMapping(value = "/getPlatFormAuthorizedCode")
	@ResponseBody
	public void getPlatFormAuthorizedCode(HttpServletRequest request, HttpSession session) {
		try {

			String url = CommonUtil.getPath("AuthWxPlatFormUrl").toString();

			url = url.replace("componentAppid", component_appid)
					.replace("preAuthCode", getComponentPreAuthCode(session))
					.replace("redirectUri", CommonUtil.getPath("project_url").toString().replace("DATA", "getRes"));

			output(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getRes")
	@ResponseBody
	public void getRes(HttpServletRequest request, HttpSession session) {
		try {

			String authorization_code = request.getParameter("auth_code");
			if (StringUtils.isNotBlank(authorization_code)) {
				String url = CommonUtil.getPath("getWxPlatFormInfoURL").toString();

				url = url.replace("componentAccessToken", getComponentAccessToken(session));

				JSONObject jsonObj = new JSONObject();

				jsonObj.put("component_appid", component_appid);
				jsonObj.put("authorization_code", authorization_code);

				String resContent = CommonUtil.posts(url, jsonObj.toJSONString(), "utf-8");

				if (StringUtils.isNotBlank(resContent)) {
					JSONObject resObj = JSONObject.parseObject(resContent);
					JSONObject obj = JSONObject.parseObject(resObj.get("authorization_info").toString());
					String authorizer_appid = obj.getString("authorizer_appid");
					String authorizer_access_token = obj.getString("authorizer_access_token");
					String authorizer_refresh_token = obj.getString("authorizer_refresh_token");

					jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + authorizer_appid, authorizer_access_token);

					Map<String, Object> map = getParameterMap();
					map.put("APP_PK", authorizer_appid);
					map.put("APP_REFRESH_TOKEN", authorizer_refresh_token);
					map.put("CREATE_TIME", StringDeal.getStringDate());
					map.put("sqlMapId", "insertApp");
					openService.insert(map);
				}
			} else {
				logger.warn("获取授权码失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		output2(response, "success");
	}
	
	/**
	 * 
	 * @author kqs
	 * @param authorizer_appid
	 * @param authorizer_refresh_token
	 * @return
	 * @return String
	 * @date 2018年8月7日 - 下午11:00:08 
	 * @description:获取对应授权app的token
	 */
	public String getWxComponentAccessToken(String authorizer_appid, String authorizer_refresh_token) {
		try {
			String url = CommonUtil.getPath("getWxComponentAccessToken").toString();
			String authorizer_access_token = "";
			if (jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + authorizer_appid)) {
				authorizer_access_token = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + authorizer_appid);
			}
			url = url.replace("componentAccessToken", authorizer_access_token);
			JSONObject postStr = new JSONObject();
			postStr.put("component_appid", component_appid);
			postStr.put("authorizer_appid", authorizer_appid);
			postStr.put("authorizer_refresh_token", authorizer_refresh_token);
			String res = CommonUtil.posts(url, postStr.toJSONString(), "utf-8");
			String authorizer_access_token_new = JSONObject.parseObject(res).getString("authorizer_access_token");
			jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + authorizer_appid, authorizer_access_token_new);
			jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + authorizer_appid, 1000 * 60 * 60 * 1);
			return authorizer_access_token_new;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return "";
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

}
