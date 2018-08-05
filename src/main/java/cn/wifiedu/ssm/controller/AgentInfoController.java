package cn.wifiedu.ssm.controller;


	import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
	import java.util.Map;

	import javax.annotation.Resource;
import javax.json.Json;
import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpSession;

	import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;
import org.springframework.context.annotation.Scope;
	import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import cn.wifiedu.core.controller.BaseController;
	import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.StringDeal;

		/**
		 * 
		 * @author lps
		 * @date 2018年8月3日18:54:58
		 * @Description:
		 * @version V1.0
		 *
		 */
		@Controller
		@Scope("prototype")
		public class AgentInfoController extends BaseController {

			private static final String Map = null;

			private static Logger logger = Logger.getLogger(UserTagController.class);

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
			 * @date 2018年8月1日 上午12:25:36 
			 * @author lps
			 * 
			 * @Description: 查询代理信息
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/AgentInfo_query_findAgentInfoById")
			public void findAgentInfoById(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					map.put("sqlMapId", "selectAgentInfoById");
					map.put("USER_ID", "4b8cea73b03a4ddfacf8fbaf7a31028d");
					
					Map<String, Object> reMap = (Map)openService.queryForObject(map);
					output("0000", reMap);
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
			}
			
			
			
			
			
			/**
			 * 
			 * @date 2018年8月6日 上午2:58:53 
			 * @author lps
			 * 
			 * @Description: 更新代理信息
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/AgentInfo_update_updateAgentInfoById")
			public void updateAgentInfoById(HttpServletRequest request,HttpSession seesion){
				try {
					
					//如果已经认证成功，就不能修改了
					Map<String, Object> map = getParameterMap();
					map.put("sqlMapId", "selectAgentInfoById");
					map.put("USER_ID", "4b8cea73b03a4ddfacf8fbaf7a31028d");
					Map<String, Object> reMap = (Map)openService.queryForObject(map);
					
					if(reMap == null || "1".equals(reMap.get("AUTH_STATUS"))){
						output("9999", "保存失败");
						return;
					}
					
					//先更新代理信息表
					map.put("sqlMapId", "updateAgentInfoById");
					map.put("UPDATE_BY", "admin");
					
					boolean update = openService.update(map);
					
					if(!update){
						output("9999", "保存失败");
						return;
					}
					
					//然后更新用户表信息
					
					map.put("sqlMapId", "updateUserBaseInfoById");
					
					update = openService.update(map);
					
					if(update){
						output("0000", "完善成功，请等待管理员审核");
					}else{
						output("9999", "保存失败");
					}
					
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
			}
			
			
			
		
		}

