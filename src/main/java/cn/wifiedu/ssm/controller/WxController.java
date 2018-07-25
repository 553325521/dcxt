package cn.wifiedu.ssm.controller;

import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
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
import cn.wifiedu.core.util.SessionUtil;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.QRCode;
import cn.wifiedu.ssm.util.WxUtil;

/**
 * 微信与数据库交互相关
 * @author JH_L
 *
 */
@Controller  
@Scope("prototype")
public class WxController extends BaseController {

	private static Logger logger = Logger.getLogger(SiResearchController.class);

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}
	
	@RequestMapping("/Qrcode_testQrcode_data")
	public void testQrcode(HttpServletRequest request, HttpServletResponse reponse) {
		try {
			Map<String, Object> map = getParameterMap();
			logger.info(map+"");
			//String url = "http://localhost:8088/dcxt/json/Qrcode_qrauth_data.json";
			String url = CommonUtil.getPath("project_url").replace("DATA", "Qrcode_testQrcodeJieShou_data");
			logger.info("myUrl:"+url);
			CommonUtil.qrCode(session, map, reponse, url);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping("/Qrcode_testQrcodeJieShou_data")
	public void testQrcodeJieShou() {
		try {
			Map<String, Object> map = getParameterMap();
			logger.info(map+"");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 处理参数
	 * 带参跳转回参数URL
	 */
	@RequestMapping("/Qrcode_qrCommonAuth_data")
	public void qrCommonAuth() {
		try {
			String code = request.getParameter("code");
			if(null != code && !"".equals(code)){
				String openId = getOpenIdByCode(code);
				System.out.println("WeChart openId : "+openId);
				
				String state = request.getParameter("state");
				System.out.println("WeChart params : "+state);
				
				Map<String, Object> map = getParameterMap();
				map.put("OPENID", openId);
				map.put("sqlMapId", "checkUserWx");
				
				List<Map<String, Object>> checkList = openService.queryForList(map);
				logger.info("checkList: "+checkList);
				
				String allParams = "openId=" + openId;
				String redirectUrl = "";
				if(checkList.size() == 1) {
					String params1 [] = state.split("-");
					for(int i=0; i<params1.length; i++) {
						String params2 [] = params1[i].split("_");
						if(!params2[0].equals("rEdIrEcTuRi")) {
							allParams = allParams + "&" + params2[0] + "=" + params2[1];
						}else {
							for(int j=1; j<params2.length; j++) {
								redirectUrl = redirectUrl + "_" + params2[j];
							}
							redirectUrl = redirectUrl.substring(1,redirectUrl.length());
						}
					}
					String result = redirectUrl + "?" + allParams ;
					logger.info("result: "+result);
					response.sendRedirect(result);
				}else if (checkList.size() == 0) {
					//如果用户没绑定微信，跳转到登陆页
					logger.info(openId + "_user_no_wx");
					request.getSession().removeAttribute("userInfo");
					session.removeAttribute("userInfo");
					request.getSession().setAttribute("openid", map.get("OPENID").toString());
					session.setAttribute("openid", map.get("OPENID").toString());
					response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json", "")+"/qrcode_error.jsp");
				}else {
					output("error");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 二维码统一跳到此处
	 * 获取扫码人openId
	 */
	@RequestMapping("/Qrcode_qrCommon_data")
	public void qrCommon() {
		try {
			if(null != request.getParameter("redirect_qrcode")){
				String url = CommonUtil.getPath("Auth-wx-qrcode-url");
				url = url.replace("STATE", request.getParameter("params")).replace("REDIRECT_URI", URLEncoder.encode(CommonUtil.getPath("project_url").replace("DATA", "Qrcode_qrCommonAuth_data"),"UTF-8"));
				System.out.println("qrcodeURL:"+url);
				response.sendRedirect(url);
			}else{
				output("无效二维码");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getOpenIdByCode(String code){
		String url = CommonUtil.getPath("WX_GET_OPENID_URL");
		url = url.replace("CODE", code);
		System.out.println("getOpenIdByCode="+url);
		String res = CommonUtil.get(url);
		Object succesResponse = JSON.parse(res);
		Map result = (Map)succesResponse; 
		
		String openId= result.get("openid").toString();
		System.out.println(openId);
		return openId;
	}
	
	
	
	
	/**
	 * 用户扫码后跳转到updateUserRole方法
	 * 经过微信验证，获取到用户openId
	 * 跳转回此方法
	 */
	@RequestMapping("/Qrcode_qrauth_data")
	public void setUserRole() {
		try {
			String code = request.getParameter("code");
			if(null != code && !"".equals(code)){
				String openId = getOpenIdByCode(code);
				System.out.println("WeChart openId : "+openId);
				
				String state = request.getParameter("state");
				System.out.println("WeChart COURSE : "+state);
				
				Map<String, Object> map = getParameterMap();
				map.put("OPENID", openId);
				map.put("ROLE_PK", state);
				map.put("sqlMapId", "checkUserWx");
				Object userInfo = null;
				
				List<Map<String, Object>> checkList = openService.queryForList(map);
				logger.info("checkList: "+checkList);
				if(checkList.size() == 1) {
					userInfo = openService.queryForObject(map);
					session.setAttribute("userInfo", userInfo);
					request.getSession().setAttribute("userInfo", userInfo);
					
					String token = WxUtil.getToken();
					if (token != null) {
						if(!checkList.get(0).get("FK_USER_TAG").equals("NULL")) {
							//取消用户微信标签
							String url = CommonUtil.getPath("user_tag_cancel").toString();
							url = url.replace("ACCESS_TOKEN", token);
							Map p = new HashMap();
							ArrayList<String> list = new ArrayList<String>();
							list.add(openId);
							p.put("openid_list", list);
							p.put("tagid", state);
							String resMsg = CommonUtil.posts(url, JSON.toJSONString(p), "utf-8");
							if(resMsg.indexOf("ok") <= 0) {
								logger.info("cancel user tag error: "+resMsg.trim());
								output("error");
							}
							logger.info("cancel user tag success: "+resMsg.trim());
						}
						
						//改变数据库用户标签
						map.put("sqlMapId", "updateScUserTag");
						boolean flag = openService.update(map);
						if(!flag) {
							logger.info("update user_tag error");
							output("error");
						}else {
							logger.info("update user_tag success");
						}
						
						//改变微信用户标签
						String url = CommonUtil.getPath("user_tag_add").toString();
						url = url.replace("ACCESS_TOKEN", token);
						Map p = new HashMap();
						ArrayList<String> list = new ArrayList<String>();
						list.add(openId);
						p.put("openid_list", list);
						p.put("tagid", state);
						String resMsg = CommonUtil.posts(url, JSON.toJSONString(p), "utf-8");
						logger.info("add user tag: "+resMsg.trim());
						if(resMsg.indexOf("ok") <= 0) {
							logger.info("add user tag error: "+resMsg.trim());
							output("error");
						}else {
							logger.info("add user tag success: "+resMsg.trim());
						}
						
						//返回到成功页面
						response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json", "")+"/qrcode_success.jsp");
					}
					
					
				}else if (checkList.size() == 0) {
					//如果用户没绑定微信，跳转到登陆页
					logger.info(openId + "_user_no_wx");
					request.getSession().removeAttribute("userInfo");
					session.removeAttribute("userInfo");
					request.getSession().setAttribute("openid", map.get("OPENID").toString());
					session.setAttribute("openid", map.get("OPENID").toString());
					response.sendRedirect(CommonUtil.getPath("project_url").replace("json/DATA.json", "")+"/qrcode_error.jsp");
				}
				
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 输出微信扫描的二维码
	 * 更改用户标签
	 * 调用此方法传role_pk后生成二维码
	 */
	@RequestMapping("/Qrcode_qrcode_data")
	public void updateUserRole() {
		try {
			if(null != request.getParameter("redirect_qrcode")){
				String url = CommonUtil.getPath("Auth-wx-qrcode-url");
				url = url.replace("STATE", request.getParameter("role_pk")).replace("REDIRECT_URI", URLEncoder.encode(CommonUtil.getPath("project_url").replace("DATA", "Qrcode_qrauth_data"),"UTF-8"));
				System.out.println("qrcodeURL:"+url);
				response.sendRedirect(url);
			}else{
				String redirect_qrcode = session.getId();
				HttpSession webSession = SessionUtil.getSession(redirect_qrcode);
				if(null == webSession){
					SessionUtil.addSession(session);
				}
				Map<String, Object> map = getParameterMap();
				
				System.out.println("qrcodeMap:"+map);
				
				BufferedImage image = QRCode.genBarcode(CommonUtil.getPath("project_url").replace("DATA", "Qrcode_qrcode_data")+"?redirect_qrcode="+redirect_qrcode+"&role_pk="+map.get("role_pk"),200, 200);  
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
	@RequestMapping("/portal")
	public void getUserInfo(HttpServletRequest request, HttpServletResponse reponse ) {
		try {
			Map<String, Object> map = getParameterMap();
			logger.info(map+"");
			
			PrintWriter out = reponse.getWriter();
			
			if(map.containsKey("openid")) {
				map.put("OPENID", map.get("openid").toString());
				map.put("sqlMapId", "checkUserExits");
				List<Map<String, Object>> checkUserList = openService.queryForList(map);
				if(checkUserList.size() == 0) {
					map.put("sqlMapId", "insertUserInitOpenId");
					openService.insert(map);
				}
			}
			
			out.println("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 保存token到数据库
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			output("9999","error");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			output("9999","error");
		}
	}
}
