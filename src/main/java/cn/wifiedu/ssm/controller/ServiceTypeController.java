package cn.wifiedu.ssm.controller;


import java.util.HashMap;
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
	 * 
	 * @author lps
	 * @date 2018年7月24日 下午4:38:44
	 * @Description:
	 * @version V1.0
	 *
	 */
	@Controller
	@Scope("prototype")
	public class ServiceTypeController extends BaseController {

		private static Logger logger = Logger.getLogger(ServiceTypeController.class);

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
		 * @date 2018年7月24日 下午4:41:06 
		 * @author lps
		 * 
		 * @Description: 查询所有服务的类型
		 * @param request
		 * @param seesion 
		 * @return void 
		 *
		 */
		@RequestMapping("/ServiceType_queryForList_findServiceTypeList")
		public void findServiceTypeList(HttpServletRequest request,HttpSession seesion){
			try {
				Map<String, Object> map = getParameterMap();
				map.put("sqlMapId", "findServiceTypeList");
				List<Map<String, Object>> reList  = openService.queryForList(map);
				
				output("0000", reList);
				return;
			} catch (Exception e) {
				output("9999", " Exception ", e);
				return;
			}
		}
		
		/**
		 * 
		 * @date 2018年8月11日 下午6:44:34 
		 * @author lps
		 * 
		 * @Description: 查询购买服务计算规则
		 * @return 
		 * @return List<Map<String,Object>> 
		 * @throws Exception 
		 *
		 */
		public static List<Map<String, Object>> getServiceRule(OpenService openService) throws Exception{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("sqlMapId", "findServiceRuleList");
				List<Map<String, Object>> reList = openService.queryForList(map);
				return reList;
		}
		
		
	
	}

