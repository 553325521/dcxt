package cn.wifiedu.ssm.controller;


	import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

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

			private static Logger logger = Logger.getLogger(AgentInfoController.class);

			@Resource
			OpenService openService;
			
			@Resource
			private JedisClient jedisClient;
			
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
					
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSON.parseObject(userJson);
					
//					map.put("SHOP_ID", userObj.get("FK_SHOP"));
					map.put("USER_ID", userObj.get("USER_PK")); 
//					map.put("ROLE_ID", userObj.get("FK_ROLE")); 

					
					map.put("sqlMapId", "selectAgentInfoById");
					
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
					Map<String, Object> map = getParameterMap();
					
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSON.parseObject(userJson);
					
					map.put("USER_ID", userObj.get("USER_PK")); 
					
					//如果已经认证成功，就不能修改了
					map.put("sqlMapId", "selectAgentInfoById");
					Map<String, Object> reMap = (Map)openService.queryForObject(map);
					
					if((String)map.get("AGENT_PK") == null){
						//说明代理信息表没东西，插入
						//代理表有东西，更新代理信息表
						map.put("sqlMapId", "insertAgentInfoById");
						map.put("CREATE_BY", "admin");
						
						String insert = openService.insert(map);
						
						if(insert == null){
							output("9999", "保存失败");
							return;
						}
						
					}else if( "1".equals(reMap.get("AUTH_STATUS"))){
						output("9999", "保存失败");
						return;
					}else{
						//代理表有东西，更新代理信息表
						map.put("sqlMapId", "updateAgentInfoById");
						map.put("UPDATE_BY", "admin");
						
						boolean update = openService.update(map);
						if(!update){
							output("9999", "保存失败");
							return;
						}
					}
					
					//然后更新用户表信息
					map.put("sqlMapId", "updateUserBaseInfoById");
					boolean update = openService.update(map);
					
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

