package cn.wifiedu.ssm.controller;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.QRCode;

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
			String url = CommonUtil.getPath("project_url").toString().replace("DATA", "Staff_add_addStaff");

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
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "addStaff");
			List<Map<String, Object>> reMap = openService.queryForList(map);
			output("0000", reMap);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}
}
