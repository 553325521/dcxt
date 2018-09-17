package cn.wifiedu.ssm.controller;


	import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.PictureUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

		@Controller
		@Scope("prototype")
		public class TurntableController extends BaseController {

			private static Logger logger = Logger.getLogger(TurntableController.class);

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
			
			public static final long ONE_PIC_MAXSIZE = 2100000L;	//允许的单张图片最大大小
			public static final long ALL_PICMAX_SIZE = 6300000L;	//允许的所有图片最大大小
			public static final int MAX_PIC_NUM = 10;				//允许的图片最大数量
			private static final long MAX_TWDESC_LENGTH = 1000;	//图文说明文字最大大小
			private static final String Turntable_TWJS_PICPATH = "assets/img/turntable_twjs"; //转盘图文介绍图片存储位置
			
			/**
			 * 
			 * @date 2018年9月11日 下午5:26:02 
			 * @author lps
			 * 
			 * @Description: 插入转盘
			 * @param request
			 * @param session 
			 * @return void 
			 *
			 */
			
			@RequestMapping("/Turntable_insert_insertOrUpdateTurntable")
			public void addTurntable(HttpServletRequest request, HttpSession session){
				try {
					Map map = getParameterMap();
					
					//表单验证
					if(StringUtils.isBlank((String)map.get("ACTIVITY_NAME")) || 
							StringUtils.isBlank((String)map.get("IS_USE")) || 
							StringUtils.isBlank((String)map.get("TURNTABLE_CYDX")) || 
							StringUtils.isBlank((String)map.get("TURNTABLE_YXQX")) ||
							StringUtils.isBlank((String)map.get("PARTICIPATION_WAY")) ||
							StringUtils.isBlank((String)map.get("PARTICIPATION_KCJF")) ||
							StringUtils.isBlank((String)map.get("TURNTABLE_ZJGL")) || 
							StringUtils.isBlank((String)map.get("TURNTABLE_LQQX")) || 
							StringUtils.isBlank((String)map.get("USE_SHOP")) || 
							StringUtils.isBlank((String)map.get("TURNTABLE_PRIZE"))
							){
						output("9999","信息填写不完整");
						return;
					}
					
					//图文说明里边的图片和文字验证
					List<Map<String, String>> twDescList = (List<Map<String, String>>) JSON.parse((String)map.get("TURNTABLE_BZSM"));
					//判断图文说明的长度
					long twDescLength = 0;
					for (Map<String, String> twDescMap : twDescList) {
						if(twDescMap.get("img").length()*3/4 > ONE_PIC_MAXSIZE){
							output("9999","单张图片大小不允许超过" + Math.floor(ONE_PIC_MAXSIZE/1000000) + "M");
							return;
						}
						twDescLength += twDescMap.get("text").length();
					}
					if(twDescLength > MAX_TWDESC_LENGTH){
						output("9999","图文介绍文字长度超过" + MAX_TWDESC_LENGTH);
						return;
					}
					
					//表单验证成功，开始插入
					//插入图文介绍图片
					for (Map<String, String> twDescMap : twDescList) {
						if(twDescMap.get("img").toString().indexOf("data:image/") != -1){
							twDescMap.put("img", PictureUtil.base64ToImage(twDescMap.get("img").toString(), Turntable_TWJS_PICPATH));
						}
					}
					map.put("TURNTABLE_BZSM", JSONObject.toJSONString(twDescList));
					
					//如果转盘为开启，把其他的关闭
					if("1".equals(map.get("IS_USE"))){
						map.put("sqlMapId", "updaeteTurntableIsUseById");
						map.put("TURNTABLE_IS_USE", "0");
						openService.update(map);
					}
					
					
					//判断是添加还是修改
					if(StringUtils.isBlank((String)map.get("TURNTABLE_PK")) || "undefined".equals(map.get("TURNTABLE_PK"))){
						//是添加
						map.put("sqlMapId", "insertTurntable");
						String insert = openService.insert(map);
						if(insert != null){
							//添加商铺转盘对应关系表
							String shopsStr = (String)map.get("USE_SHOP");
							String[] shops = shopsStr.split(",");
							map.put("sqlMapId", "insertShopTurntable");
							for(int i=0;i<shops.length;i++){
								map.put("SHOP_ID", shops[i]);
								map.put("TURNTABLE_ID", insert);
								openService.insert(map);
							}
							output("0000","添加成功");
							return;
						}else{
							output("9999","添加失败");
							return;
						}
					}else{//是更新
						map.put("sqlMapId", "updaeteTurntableById");
						boolean b = openService.update(map);
						if(b){
							//再添加现在的转盘关系
							map.put("sqlMapId", "removeShopTurntableByTurntableId");
							map.put("TURNTABLE_ID",map.get("TURNTABLE_PK"));
							boolean delete = openService.delete(map);
							if(delete){
								String shopsStr = (String)map.get("USE_SHOP");
								String[] shops = shopsStr.split(",");
								map.put("sqlMapId", "insertShopTurntable");
								for(int i=0;i<shops.length;i++){
									map.put("SHOP_ID", shops[i]);
									openService.insert(map);
								}
							}else{
								output("9999","修改失败");
								return;
							}
							
							output("0000","修改成功");
							return;
						}else{
							output("9999","修改失败");
							return;
						}
					}
				} catch (Exception e) {
					output("9999",e);
					e.printStackTrace();
				}
			}
			
			
			/**
			 * 
			 * @date 2018年9月11日 下午5:25:20 
			 * @author lps
			 * 
			 * @Description: 根绝转盘id查询转盘信息
			 * @param request
			 * @param session 
			 * @return void 
			 *
			 */
			@RequestMapping("/Turntable_select_findTurntableById")
			public void findTurntableById(HttpServletRequest request, HttpSession session){
				try {
					Map map = getParameterMap();
					map.put("sqlMapId", "selectTurntableById");
							
					Map reMap = (Map) openService.queryForObject(map);
					output("0000",reMap);
					return;
				} catch (Exception e) {
					output("9999",e);
					e.printStackTrace();
				}
				
				
			}
			
			
			/**
			 * 
			 * @date 2018年9月11日 下午5:24:48 
			 * @author lps
			 * 
			 * @Description: 查询该商铺下所有的转盘
			 * @param request
			 * @param session 
			 * @return void 
			 *
			 */
			@RequestMapping("/ShopTurntable_select_findTurntable")
			public void findTurntableByShopId(HttpServletRequest request, HttpSession session){
				try {
					Map map = getParameterMap();
					map.put("sqlMapId", "selectTurntableListByShopId");
					
					
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSONObject.parseObject(userJson);
					
					map.put("SHOP_ID", userObj.get("FK_SHOP"));
					
					List queryForList = openService.queryForList(map);
					if(queryForList == null){
						output("9999","未知错误");
						return;
					}
					output("0000",queryForList);
					return;
				} catch (Exception e) {
					output("9999",e);
					e.printStackTrace();
				}
				
				
			}
			
			
			/**
			 * 
			 * @date 2018年9月15日 下午9:25:59 
			 * @author lps
			 * 
			 * @Description: 根据转盘id删除转盘
			 * @param request
			 * @param session 
			 * @return void 
			 *
			 */
			@RequestMapping("/Turntable_delete_removeTurntableById")
			public void removeVIPCardById(HttpServletRequest request, HttpSession session){
				try {
					Map map = getParameterMap();
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSONObject.parseObject(userJson);
					
					map.put("SHOP_ID", userObj.get("FK_SHOP"));
					
					//先删除会员卡信息
					map.put("sqlMapId", "removeTurntableById");
					
					boolean delete = openService.delete(map);
					if(!delete){
						output("9999","删除失败");
						return;
					}
					//再删除店铺-会员卡关系
					map.put("sqlMapId", "removeShopTurntableByVipCardId");
					delete = openService.delete(map);
					if(!delete){
						output("9999","删除失败");
						return;
					}
					
					output("0000","删除成功");
					return;
				} catch (Exception e) {
					output("9999",e);
					e.printStackTrace();
				}
			}
			
}

