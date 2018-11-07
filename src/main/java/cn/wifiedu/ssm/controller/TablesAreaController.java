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
		 * @date 2018年8月1日00:19:35
		 * @Description:
		 * @version V1.0
		 *
		 */
		@Controller
		@Scope("prototype")
		public class TablesAreaController extends BaseController {

			private static final String Map = null;

			private static Logger logger = Logger.getLogger(TablesAreaController.class);

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
			 * @Description: 查询店铺区域列表
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/TablesArea_query_findTablesAreaList")
			public void findTablesAreaList(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSON.parseObject(userJson);

					map.put("SHOP_ID", userObj.get("FK_SHOP"));
					map.put("sqlMapId", "selectTablesArea");
					
					List<Map<String, Object>> reMap = openService.queryForList(map);
					
					output("0000", reMap);
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
			}
			
			
			/**
			 * 
			 * @date 2018年8月1日 上午12:41:31 
			 * @author lps
			 * 
			 * @Description: 根据区域id删除指定区域
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/TablesArea_delete_removeTablesAreaById")
			public void removeTablesAreaById(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSON.parseObject(userJson);
					
					//先查询区域总数量
					map.put("sqlMapId", "findTablesAreaCountByShopId");
					map.put("SHOP_ID", userObj.get("FK_SHOP"));
					
					Map reMap = (Map)openService.queryForObject(map);
					long areaCount = (long) reMap.get("area_count");
					
					//再获取当前区域的排序序号
					map.put("TABLES_AREA_ID", map.get("area_id"));
					map.put("sqlMapId", "findTablesAreaById");
					
					reMap = (Map)openService.queryForObject(map);
					Integer bef_pxxh = Integer.parseInt((String)reMap.get("TABLES_AREA_PXXH"));
					
					//判断当前是不是最后一个数据
					if(areaCount != bef_pxxh){
						//如果不等于，进行排序序号重置，后边的区域序号依次减一
						map.put("sqlMapId", "updateTablesAreaPxxhSubById");
						map.put("SMALL_TABLES_AREA_PXXH", bef_pxxh);
						
						boolean update = openService.update(map);
						if(!update){
							output("9999", "删除失败！");
							return;
						}
					}
					
					//先删除区域下边的桌位
					map.put("sqlMapId", "removeTablesByAreaId");
					boolean b = openService.delete(map);
					
					//开始删除区域
					map.put("sqlMapId", "removeTablesAreaById");
					
					b = openService.delete(map);
					if(b){
						output("0000", "删除成功！");
						return;
					}
					output("9999", "删除失败！");
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
			}
			
			
			/**
			 * 
			 * @date 2018年8月1日 上午1:56:48 
			 * @author lps
			 * 
			 * @Description: 查询单个区域信息
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/TablesArea_query_findTablesAreaById")
			public void findTablesAreaById(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					map.put("sqlMapId", "findTablesAreaById");
					map.put("TABLES_AREA_ID", map.get("area_id"));
					
					Object object = openService.queryForObject(map);
					if(object != null){
						output("0000", object);
						return;
					}
					output("9999", "操作失败！");
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
			}
			
			
			/**
			 * 
			 * @date 2018年8月1日 下午5:48:29 
			 * @author lps
			 * 
			 * @Description: 
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/TablesArea_save_saveTablesArea")
			public void saveTablesArea(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSON.parseObject(userJson);
					
					//先查询区域总数量
					map.put("sqlMapId", "findTablesAreaCountByShopId");
					map.put("SHOP_ID", userObj.get("FK_SHOP"));
					
					Map reMap = (Map)openService.queryForObject(map);
					long areaCount = (long) reMap.get("area_count");
					Integer pxxh = Integer.parseInt((String) map.get("TABLES_AREA_PXXH"));
					
					//判断序号如果小于0或大于总数，返回错误
					if(pxxh <= 0){
						output("9999", "排序序号不允许为负数！");
						return;
					}else if(pxxh > areaCount + 1){
						output("9999", "排序序号不允许大于区域总数量！");
						return;
					}
					//判断，如果当前排序序号不是最后一个，开始把当前序号后边的依次加一
					if(pxxh - 1 != areaCount){
						map.put("sqlMapId", "updateTablesAreaPxxhAddById");
						map.put("SMALL_TABLES_AREA_PXXH", pxxh);
						boolean b = openService.update(map);
						if(!b){
							output("9999", "添加失败！");
							return;
						}
					}
					//开始添加区域
					map.put("sqlMapId", "insertTablesArea");
					map.put("CREATE_BY", "admin");
					String insert = openService.insert(map);
					if(insert != null){
						output("0000", "添加成功！");
						return;
					}
					output("9999", "添加失败！");
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
			}
			
			
			/**
			 * 
			 * @date 2018年8月9日 上午1:21:31 
			 * @author lps
			 * 
			 * @Description: 通过区域ID更改区域信息
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/TablesArea_update_updateTablesAreaById")
			public void updateTablesArea(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					//先查询当前区域的排序序号
					map.put("sqlMapId", "findTablesAreaById");
					map.put("TABLES_AREA_ID", map.get("TABLES_AREA_PK"));
					Integer after_pxxh = Integer.parseInt((String)map.get("TABLES_AREA_PXXH"));
					Map reMap = (Map)openService.queryForObject(map);
					Integer bef_pxxh = Integer.parseInt((String)reMap.get("TABLES_AREA_PXXH"));
					String bef_status = (String) reMap.get("TABLES_AREA_STATUS");//获取之前的区域状态
					
					//判断当前区域的排序位置是不是最后一个
					if(bef_pxxh != after_pxxh){
						if(bef_pxxh > after_pxxh){//如果更新前的序号大于更新后的，那就把更新后的后边的序号依次加一
							map.put("sqlMapId", "updateTablesAreaPxxhAddById");
							map.put("SMALL_TABLES_AREA_PXXH", after_pxxh);
							map.put("BIG_TABLES_AREA_PXXH", bef_pxxh);
						}else if(bef_pxxh < after_pxxh){//如果更新前的序号小于更新后的，那就把更新后的后边的序号依次减一
							map.put("SMALL_TABLES_AREA_PXXH", bef_pxxh);
							map.put("BIG_TABLES_AREA_PXXH", after_pxxh);
							map.put("sqlMapId", "updateTablesAreaPxxhSubById");
						}
						//从session取数据
						String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
						String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
						JSONObject userObj = JSON.parseObject(userJson);
						
						map.put("SHOP_ID", userObj.get("FK_SHOP"));
						boolean b = openService.update(map);
						if(!b){
							output("9999", "修改失败！");
							return;
						}
					}
					//开始更新区域信息
					map.put("sqlMapId", "updateTablesAreaById");
					map.put("TABLES_AREA_ID", map.get("TABLES_AREA_PK"));
					map.put("UPDATE_BY", "admin");
					boolean update = openService.update(map);
					if(!update){
						output("9999", "修改失败！");
						return;
					}
					//判断区域状态是不是改变，是的话是不是改成已停用，如果是，就把子节点全都改成已停用
					if(!bef_status.equals(map.get("TABLES_AREA_STATUS")) && bef_status.equals("1")){
						map.put("sqlMapId", "updateTablesStatusByAreaId");
						update = openService.update(map);
					}
					
					output("0000", "修改成功！");
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
			}
			
			
		
		}

