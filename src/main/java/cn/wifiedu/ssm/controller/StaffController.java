package cn.wifiedu.ssm.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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

	@RequestMapping("/Staff_queryForList_findStaffList")
	public void loadTopMenus(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "findStaffList");
			List<Map<String, Object>> reMap = openService.queryForList(map);
			output("0000", reMap);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

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
			params += "&FK_APP=" + userObj.getString("FK_APP");
			params += "&ROLE_ID=6";
			String url = CommonUtil.getPath("Auth-wx-qrcode-url");
			url = url.replace("REDIRECT_URI", URLEncoder
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

	@RequestMapping("/Staff_add_addStaff")
	public void addStaff(HttpServletRequest request, HttpSession session) {
		try {
			String code = request.getParameter("code");
			if (null != code && !"".equals(code)) {
				Map<String, Object> userMap = CommonUtil.getWxUserInfo(code);
				userMap.put("sqlMapId", "insertUserInitOpenId");
				String USER_PK = openService.insert(userMap);
				System.out.println("USER_PK====" + USER_PK);
				if (StringUtils.isNotBlank(USER_PK)) {
					Map<String, Object> map = getParameterMap();
					map.put("tagName", "店员端");
					map.put("USER_ID", USER_PK);
					map.put("sqlMapId", "insertUserShop");
					String res = openService.insert(map);
					if (res != null) {
						String token = WxUtil.getToken();
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
							}
						} else {
							throw new RuntimeException("404");
						}
					}
				} else {
					// 重定向失败页面
				}
			} else {
				// 重定向失败页面
			}
		} catch (Exception e) {
			logger.info(e);
			e.printStackTrace();
			if ("404".equals(e.getMessage())) {
				logger.error("获取微信token失败");
				try {
					response.sendRedirect("");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
