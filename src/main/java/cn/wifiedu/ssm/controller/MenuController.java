package cn.wifiedu.ssm.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.StringDeal;

/**
 * @author kqs
 * @time 2018年7月24日 - 下午11:19:03
 * @description:菜单管理模块
 */
@Controller
@Scope("prototype")
public class MenuController extends BaseController {

	private static Logger logger = Logger.getLogger(MenuController.class);

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
	 * @date 2018年7月24日 - 下午11:30:48
	 * @description:查询所有的一级菜单根据微信appid
	 */
	@RequestMapping("/Menu_queryForList_loadTopMenusByAppId")
	public void loadTopMenus(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadTopMenusByAppId");
			List<Map<String, Object>> reMap = openService.queryForList(map);
			output("0000", reMap);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
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
	@RequestMapping("/Menu_insert_addMenu")
	public void insert(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("FK_APP", "wx6041a1eff32d3c5e");
			map.put("sqlMapId", "insertMenu");
			map.put("CREATE_BY", "admin");
			map.put("CREATE_TIME", StringDeal.getStringDate());
			String result = openService.insert(map);
			if (result != null) {
				output("0000", map);
				return;
			}
			output("9999", " 添加失败  ");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	@RequestMapping("/Menu_insert_test")
	public void test(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			String url = CommonUtil.getPath("wxAuthURL").toString();
			
			output("0000", map);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	@RequestMapping("/Menu_insert_test1")
	public void test1(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}
}
