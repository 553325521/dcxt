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
import cn.wifiedu.ssm.util.StringDeal;

/**
 * @author kqs
 * @time 2018年7月17日 - 上午10:50:12
 * @description:后台功能列表
 */
@Controller
@Scope("prototype")
public class FunctionController extends BaseController {

	private static Logger logger = Logger.getLogger(FunctionController.class);

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
	 * @param @param
	 *            request
	 * @param @param
	 *            session
	 * @return void
	 * @date 2018年7月19日 - 上午10:49:39
	 * @description:查询所有顶级菜单
	 */
	@RequestMapping("/Function_queryForList_findTopFunctions")
	public void findTopFunctions(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "findTopFunctions");
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
	 * @date 2018年7月17日 - 上午10:57:23
	 * @description: 根据用户权限查询对应列表
	 */
	@RequestMapping("/Function_queryForList_loadFunctionListByUserRole")
	public void loadFunctionListByUserRole(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadFunctionListByUserRole");
			List<Map<String, Object>> reList = openService.queryForList(map);
			map.put("functionList", reList);
			output("0000", map);
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
	 * @description:新增功能
	 */
	@RequestMapping("/Function_insert_addFunction")
	public void addFunction(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadCountByPid");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			if (reMap != null) {
				map.put("FUNCTION_SORT", Integer.valueOf(reMap.get("FUNCTION_SORT").toString()) + 1);
				map.put("FUNCTION_ICON", "icon_nav_button");
				map.put("CREATE_BY", "admin");
				map.put("CREATE_TIME", StringDeal.getStringDate());
				map.put("FUNCTION_SWITCH", "on");
				map.put("sqlMapId", "insertFuntion");
				String result = openService.insert(map);
				if (result != null) {
					output("0000", map);
				}
			}
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
	@RequestMapping("/Function_insert_addMenu")
	public void insert(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "insertMenu");
			String result = openService.insert(map);
			output("0000", map);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}
}
