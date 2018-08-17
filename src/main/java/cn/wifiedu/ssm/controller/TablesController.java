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
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
	import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.StringDeal;
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
		public class TablesController extends BaseController {

			private static final String Map = null;

			private static Logger logger = Logger.getLogger(TablesController.class);

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
			 * @Description: 查询店铺某区域桌位列表
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/Tables_query_findTablesListByAreaId")
			public void findTablesList(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					map.put("sqlMapId", "selectTablesByAreaId");
					map.put("TABLES_AREA_ID", map.get("area_id"));
					
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
			 * @Description: 根据桌位id删除指定桌位
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/Tables_delete_removeTablesById")
			public void removeTablesById(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
				/*	//先查询当前桌位是否在当前商铺，不然不能删除
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSONObject.parseObject(userJson);
					
					map.put("SHOP_ID", userObj.get("FK_SHOP"));
				
					*/
					
					//先查询桌位总数量
					map.put("sqlMapId", "findTablesCountByAreaId");
					map.put("TABLES_AREA_ID", map.get("area_id"));
					
					Map reMap = (Map)openService.queryForObject(map);
					long areaCount = (long) reMap.get("tables_count");
					//再获取当前桌位的排序序号
					map.put("TABLES_ID", map.get("tables_id"));
					map.put("sqlMapId", "findTablesById");
					
					reMap = (Map)openService.queryForObject(map);
					Integer bef_pxxh = Integer.parseInt((String)reMap.get("TABLES_PXXH"));
					
					//判断当前是不是最后一个数据
					if(areaCount != bef_pxxh){
						//如果不等于，进行排序序号重置
						map.put("sqlMapId", "updateTablesPxxhById");
						map.put("sub", true);
						map.put("SMALL_TABLES_PXXH", bef_pxxh);
						
						boolean update = openService.update(map);
						
						if(!update){
							output("9999", "删除失败！");
							return;
						}
					}
					//开始删除
					map.put("sqlMapId", "removeTablesById");
					
					boolean b = openService.delete(map);
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
			 * @Description: 查询单个桌位信息
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/Tables_query_findTablesById")
			public void findTablesById(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					map.put("sqlMapId", "findTablesById");
					map.put("TABLES_ID", map.get("tables_id"));
					
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
			 * @Description: 添加table
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/Tables_save_saveTables")
			public void saveTables(HttpServletRequest request,HttpSession seesion){
				try {
					Map<String, Object> map = getParameterMap();
					
					//先查询当前区域桌位总数量
					map.put("sqlMapId", "findTablesCountByAreaId");
					map.put("TABLES_AREA_ID", map.get("area_id"));
					
					Map reMap = (Map)openService.queryForObject(map);
					long areaCount = (long) reMap.get("tables_count");
					Integer pxxh = Integer.parseInt((String) map.get("TABLES_PXXH"));
					//判断，如果当前排序序号不是最后一个，开始把当前序号后边的依次加一
					if(pxxh - 1 != areaCount){
						map.put("sqlMapId", "updateTablesPxxhById");
						map.put("SMALL_TABLES_PXXH", pxxh);
						boolean b = openService.update(map);
						if(!b){
							output("9999", "添加失败！");
							return;
						}
					}
					//开始添加桌位
					map.put("sqlMapId", "insertTables");
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
			 * @date 2018年8月8日 上午4:46:49 
			 * @author lps
			 * 
			 * @Description: 根据tableId更新table
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/Tables_update_updateTablesById")
			public void updateTablesById(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					//先查询当前桌位的排序序号
					map.put("sqlMapId", "findTablesById");
					map.put("TABLES_ID", map.get("TABLES_PK"));
					Map reMap = (Map)openService.queryForObject(map);
					Integer bef_pxxh = Integer.parseInt((String)reMap.get("TABLES_PXXH"));
					Integer after_pxxh = Integer.parseInt((String)map.get("TABLES_PXXH"));
					
					//判断是否设置已启用而父节点未启用
					map.put("TABLES_AREA_ID", map.get("area_id"));
					map.put("sqlMapId", "findTablesAreaById");
					Map<String,String> areaMess = (Map) openService.queryForObject(map);
					if(areaMess == null){
						output("9999", "修改失败！");
						return;
					}
					//如果是的话，直接返回错误
					if("0".equals(areaMess.get("TABLES_AREA_STATUS")) && "1".equals(map.get("TABLES_STATUS"))){
						output("9999", "该区域已停用，不允许启用该桌位！");
						return;
					}
					
					
					//判断当前商品排序等于之前的排序吗
					if(bef_pxxh != after_pxxh){
						if(bef_pxxh > after_pxxh){//如果更新前的序号大于更新后的，那就把更新后的后边的序号依次加一
							map.put("sqlMapId", "updateTablesPxxhById");
							map.put("SMALL_TABLES_PXXH", after_pxxh);
							map.put("BIG_TABLES_PXXH", bef_pxxh);
						}else if(bef_pxxh < after_pxxh){//如果更新前的序号小于更新后的，那就把更新后的后边的序号依次减一
							map.put("SMALL_TABLES_PXXH", bef_pxxh);
							map.put("BIG_TABLES_PXXH", after_pxxh);
							map.put("sqlMapId", "updateTablesPxxhById");
							map.put("sub", true);
						}
						boolean b = openService.update(map);
						if(!b){
							output("9999", "修改失败！");
							return;
						}
					}
					//开始更新桌位信息
					map.put("sqlMapId", "updateTablesById");
					map.put("TABLES_ID", map.get("TABLES_PK"));
					map.put("UPDATE_BY", "admin");
					boolean update = openService.update(map);
					if(update){
						output("0000", "修改成功！");
						return;
					}
					output("9999", "修改失败！");
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
			}
		}
