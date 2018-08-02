package cn.wifiedu.ssm.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
	// token 过期时间
	private final long token_timeout = 7200;
	// token 过期时间
	private final long preAuthCode_timeout = 10 * 60 * 1000;

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

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
	public void getComponentVerifyTicket(HttpServletRequest request, HttpSession session) {
		try {
			logger.info("微信第三方平台---------微信推送Ticket消息10分钟一次-----------" + StringDeal.getStringDate());
			processAuthorizeEvent(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		output("success");
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
			if (session.getAttribute("componentAccessToken") != null) {
				Map<String, Object> mapp = (Map<String, Object>) session.getAttribute("componentAccessToken");
				long second = (Long.valueOf(mapp.get("createTime").toString()) - Long.valueOf(StringDeal.timeStamp()))
						/ 1000;
				if (token_timeout - second > 0) {
					return mapp.get("token").toString();
				}
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

				Map<String, Object> mapSession = new HashMap<>();
				mapSession.put("token", componentAccessToken);
				mapSession.put("createTime", StringDeal.timeStamp());
				session.setAttribute("componentAccessToken", mapSession);
				session.setMaxInactiveInterval(1000 * 60 * 60 * 2);
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
			if (session.getAttribute("componentPreAuthCode") != null) {
				Map<String, Object> mapp = (Map<String, Object>) session.getAttribute("componentPreAuthCode");
				long second = (Long.valueOf(mapp.get("createTime").toString()) - Long.valueOf(StringDeal.timeStamp()))
						/ 1000;
				if (preAuthCode_timeout - second > 0) {
					return mapp.get("token").toString();
				}
			}

			String url = CommonUtil.getPath("getWxPreAuthCode").toString();

			url = url.replace("componentAccessToken", getComponentAccessToken(session));

			JSONObject json = new JSONObject();
			json.put("component_appid", component_appid);

			/** 发送Https请求到微信 */
			String retStr = CommonUtil.posts(url, json.toString(), "utf-8");

			JSONObject resultJson = JSONObject.parseObject(retStr);
			/** 在返回结果中获取token */
			String componentPreAuthCode = resultJson.getString("pre_auth_code");
			Map<String, Object> mapSession = new HashMap<>();
			mapSession.put("token", componentPreAuthCode);
			mapSession.put("createTime", StringDeal.timeStamp());
			session.setAttribute("componentPreAuthCode", mapSession);
			session.setMaxInactiveInterval(1000 * 60 * 60 * 2);
			return componentPreAuthCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/getPlatFormAuthorizedCode")
	@ResponseBody
	public void getPlatFormAuthorizedCode(HttpServletRequest request, HttpSession session) {
		try {

			Map<String, Object> map = getParameterMap();

			String url = CommonUtil.getPath("AuthWxPlatFormUrl").toString();

			url = url.replace("componentAppid", component_appid)
					.replace("preAuthCode", getComponentPreAuthCode(session))
					.replace("redirectUri", CommonUtil.getPath("pathUrl").toString() + "json/getRes");

			output("0000", CommonUtil.qrCode(url));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getRes")
	@ResponseBody
	public void getRes(HttpServletRequest request, HttpSession session) {
		try {

			Map<String, Object> map = getParameterMap();

			for (String key : map.keySet()) {
				System.out.println("key:" + key + "----value:" + map.get(key));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
