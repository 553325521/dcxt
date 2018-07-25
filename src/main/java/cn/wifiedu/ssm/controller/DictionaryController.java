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

/**
 * @author kqs
 * @time 2018年7月24日 - 下午10:34:43
 * @description:字典
 */
@Controller
@Scope("prototype")
public class DictionaryController extends BaseController {
	
	private static Logger logger = Logger.getLogger(DictionaryController.class);

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
	 * @date 2018年7月24日 - 下午10:37:23 
	 * @description:查询平台类型
	 */
	@RequestMapping("/Dictionary_queryForList_findPlatformTypeList")
	public void findPlatformTypeList(HttpServletRequest request, HttpSession session){
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "findPlatformTypeList");
			List<Map<String, Object>> reMap = openService.queryForList(map);
			output("0000", reMap);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}
}
