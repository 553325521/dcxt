package cn.wifiedu.ssm.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.StringDeal;

/**
 * @author kqs
 * @time 2018年7月24日 - 下午10:34:43
 * @description:打印机模块
 */
@Controller
@Scope("prototype")
public class PrinterController extends BaseController {

	private static Logger logger = Logger.getLogger(PrinterController.class);

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	@Autowired
	private TransactionManagerController txManagerController;

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年7月24日 - 下午10:37:23
	 * @description:打印机设置
	 */
	@RequestMapping("/Print_insert_addPinterInfo")
	public void findPlatformTypeList(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("INSERT_TIME", StringDeal.getStringDate());
			map.put("sqlMapId", "addPinterInfo");
			String res = openService.insert(map);
			if (res != null) {
				output("0000", "保存成功!");
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	@RequestMapping("/Print_queryForList_loadPrintList")
	public void loadPrintList(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("INSERT_TIME", StringDeal.getStringDate());
			map.put("sqlMapId", "loadPrintList");
			List<Map<String, Object>> res = openService.queryForList(map);
			if (res != null) {
				output("0000", "保存成功!");
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	@RequestMapping("/Print_queryForList_loadPrintRelevantList")
	public void loadPrintRelevantList(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadPrintRelevantList");
			List<Map<String, Object>> res = openService.queryForList(map);
			output("0000", res);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}
}
