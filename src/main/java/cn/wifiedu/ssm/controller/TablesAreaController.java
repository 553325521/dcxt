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
		 * @date 2018年8月1日00:19:35
		 * @Description:
		 * @version V1.0
		 *
		 */
		@Controller
		@Scope("prototype")
		public class TablesAreaController extends BaseController {

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
					map.put("sqlMapId", "selectTablesArea");
					map.put("SHOP_ID", "9a312aeb91514e79bd7837124b1b5242");
					
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
					//先查询区域总数量
					map.put("sqlMapId", "findTablesAreaCount");
					
					Map reMap = (Map)openService.queryForObject(map);
					long areaCount = (long) reMap.get("area_count");
					
					//再获取当前区域的排序序号
					map.put("TABLES_AREA_ID", map.get("area_id"));
					map.put("sqlMapId", "findTablesAreaById");
					
					reMap = (Map)openService.queryForObject(map);
					Integer bef_pxxh = Integer.parseInt((String)reMap.get("TABLES_AREA_PXXH"));
					
					//判断当前是不是最后一个数据
					if(areaCount != bef_pxxh){
						//如果不等于，进行排序序号重置
						map.put("sqlMapId", "updateTablesAreaPxxhSubById");
						map.put("SMALL_TABLES_AREA_PXXH", bef_pxxh);
						map.put("SHOP_ID", "9a312aeb91514e79bd7837124b1b5242");
						
						boolean update = openService.update(map);
						
						if(!update){
							output("9999", "删除失败！");
							return;
						}
					}
					//开始删除
					map.put("sqlMapId", "removeTablesAreaById");
					
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
					map.put("sqlMapId", "insertTablesArea");
					map.put("SHOP_ID", "9a312aeb91514e79bd7837124b1b5242");
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
			
			
			
			@RequestMapping("/TablesArea_update_updateTablesAreaById")
			public void updateTablesArea(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					//先查询当前区域的排序序号
					map.put("sqlMapId", "findTablesAreaById");
					map.put("TABLES_AREA_ID", map.get("TABLES_AREA_PK"));
					Map reMap = (Map)openService.queryForObject(map);
					Integer bef_pxxh = Integer.parseInt((String)reMap.get("TABLES_AREA_PXXH"));
					Integer after_pxxh = Integer.parseInt((String)map.get("TABLES_AREA_PXXH"));
					
					if(bef_pxxh != after_pxxh){
						if(bef_pxxh > after_pxxh){
							map.put("sqlMapId", "updateTablesAreaPxxhAddById");
							map.put("SMALL_TABLES_AREA_PXXH", after_pxxh);
							map.put("BIG_TABLES_AREA_PXXH", bef_pxxh);
						}else if(bef_pxxh < after_pxxh){
							map.put("SMALL_TABLES_AREA_PXXH", bef_pxxh);
							map.put("BIG_TABLES_AREA_PXXH", after_pxxh);
							map.put("sqlMapId", "updateTablesAreaPxxhSubById");
						}
						map.put("SHOP_ID", "9a312aeb91514e79bd7837124b1b5242");
						boolean b = openService.update(map);
						
						if(!b){
							output("0000", "修改失败！");
							return;
						}
					
					}
					
					map.put("sqlMapId", "updateTablesAreaById");

					
					map.put("sqlMapId", "updateTablesAreaById");
					map.put("TABLES_AREA_ID", map.get("TABLES_AREA_PK"));
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

