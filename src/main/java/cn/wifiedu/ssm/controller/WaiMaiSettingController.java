package cn.wifiedu.ssm.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;
import cn.wifiedu.ssm.util.waimai.MTYSUtil;

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
		public class WaiMaiSettingController extends BaseController {

			private static Logger logger = Logger.getLogger(WaiMaiSettingController.class);


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
			 * @date 2018年9月13日 下午9:18:32 
			 * @author lps
			 * 
			 * @Description:  查询店铺外卖设置
			 * @return void 
			 *
			 */
			@RequestMapping("/WaiMaiSetting_select_findWaiMaiSettingByShopId")
			public void findShopIntegraByShopId() {
				try {
					Map<String, Object> map = getParameterMap();
					map.put("sqlMapId", "selectWaiMaiSettingByShopId");
					
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSON.parseObject(userJson);
					
					map.put("SHOP_ID", userObj.get("FK_SHOP")); 
					
					Map reMap = (Map) openService.queryForObject(map);
					if(reMap == null){
						output("9999", "查询错误");
						return;
					}
					output("0000", reMap);
					return;
				} catch (Exception e) {
					logger.error("error", e);
					output("9999", "出错");
					return;
				}
			}
			
			/**
			 * 
			 * @date 2018年9月13日 下午9:22:18 
			 * @author lps
			 * 
			 * @Description:  修改店铺外卖设置
			 * @return void 
			 *
			 */
			
			@RequestMapping("/WaiMaiSetting_save_insertWaiMaiSetting")
			public void updateShopIntegra() {
				try {
					Map<String, Object> map = getParameterMap();
					String waiMaiId = (String) map.get("WM_PK");
					if(StringUtils.isNoneBlank(waiMaiId)) {
						map.put("sqlMapId", "updateWaiMaiSettingByWaiMaiId");
					}else {
						map.put("sqlMapId", "insertWaiMaiSetting");
					}
					
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSON.parseObject(userJson);
					
					map.put("SHOP_ID", userObj.get("FK_SHOP")); 
					
					if(StringUtils.isNoneBlank(waiMaiId)) {
						map.put("UPDATE_BY", userObj.get("USER_PK")); 
						boolean b = openService.update(map);
						if(!b){
							output("9999", "保存失败");
							return;
						}
					}else {
						map.put("CREATE_BY", userObj.get("USER_PK")); 
						String insert = openService.insert(map);
						if(insert == null) {
							output("9999", "保存失败");
							return;
						}
					}
					
					output("0000", "保存成功");
					return;
				} catch (Exception e) {
					logger.error("error", e);
					output("9999", "出错");
					return;
				}
			}
			
			
			
			/**
			 * 
			 * @date 2018年9月13日 下午9:22:18 
			 * @author lps
			 * 
			 * @Description:  修改店铺外卖设置
			 * @return void 
			 *
			 */
			
			@RequestMapping("/WaiMaiSetting_update_updateThirdWaiMaiSettingById")
			public void WaiMaiSetting_update_updateThirdWaiMaiSettingById() {
				try {
					Map<String, Object> map = getParameterMap();
					String waiMaiId = (String) map.get("WM_PK");
					if(StringUtils.isNoneBlank(waiMaiId)) {
						map.put("sqlMapId", "updateThirdWaiMaiSettingByWaiMaiId");
					}else {
						map.put("sqlMapId", "insertWaiMaiSetting");
					}
					
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSON.parseObject(userJson);
					
					map.put("SHOP_ID", userObj.get("FK_SHOP")); 
					
					if(StringUtils.isNoneBlank(waiMaiId)) {
						map.put("UPDATE_BY", userObj.get("USER_PK")); 
						boolean b = openService.update(map);
						if(!b){
							output("9999", "保存失败");
							return;
						}
					}else {
						map.put("CREATE_BY", userObj.get("USER_PK")); 
						String insert = openService.insert(map);
						if(insert == null) {
							output("9999", "保存失败");
							return;
						}
					}
					
					output("0000", "保存成功");
					return;
				} catch (Exception e) {
					logger.error("error", e);
					output("9999", "出错");
					return;
				}
			}
			
			/**
			 * 
			 * @author lps
			 * @date Jan 5, 2019 3:35:01 AM 
			 * 
			 * @description: 外卖取消授权
			 * @return void
			 */
			@RequestMapping("/WaiMaiSetting_cancalSQ")
			public void WaiMaiSetting_cancalSQ() {
				try {
					Map<String, Object> map = getParameterMap();
					
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSON.parseObject(userJson);
					
					map.put("SHOP_ID", userObj.get("FK_SHOP")); 
					
					String col = (String) map.get("col");
					if(col == null) {
						return;
					}
					
					//TODO
					//先查询授权的appAuthToken
					if("1".equals(col)) {//美团外卖
						
						map.put("sqlMapId", "selectMtShopMappingByShopId");
						Map mtShopMappingMap = (Map) openService.queryForObject(map);
						if(mtShopMappingMap == null || mtShopMappingMap.get("MTYS_APPAUTHTOKEN") == null) {
							output("9999", "该店铺暂未授权美团外卖");
							return;
						}
						
						//美团门店映射
						output("0000", MTYSUtil.getcancalYSUrl((String)mtShopMappingMap.get("MTYS_APPAUTHTOKEN")));
						return;
					}else if("2".equals(col)) {
						output("0000", "https://be.ele.me/crm?qt=apishopunbindpage");
						return;
					}
					
					output("9999", "失败");
					return;
				} catch (Exception e) {
					logger.error("error", e);
					output("9999", "出错");
					return;
				}
			}
			
			
			/**
			 * 
			 * @author lps
			 * @date Jan 5, 2019 3:35:01 AM 
			 * 
			 * @description: 外卖授权
			 * @return void
			 */
			@RequestMapping("/WaiMaiSetting_SQ")
			public void WaiMaiSetting_SQ(HttpServletRequest request, HttpServletResponse reponse) {
				try {
					Map<String, Object> map = getParameterMap();
					
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSON.parseObject(userJson);
					
					map.put("SHOP_ID", userObj.get("FK_SHOP")); 
					
					String col = (String) map.get("col");
					if(col == null) {
						return;
					}
					
					//查询商铺信息
					map.put("sqlMapId", "selectShopInfoById");
					Map shopMap = (Map) openService.queryForObject(map);
					if(shopMap == null) {
						return;
					}
					
					
					//TODO
					//先查询授过权没有
					if("1".equals(col)) {//美团外卖
						
						map.put("sqlMapId", "selectMtShopMappingByShopId");
						Map mtShopMappingMap = (Map) openService.queryForObject(map);
						if(mtShopMappingMap != null) {
							return;
						}
						
						//美团门店映射
						output("0000", MTYSUtil.getYSUrl((String)userObj.get("FK_SHOP"), (String)shopMap.get("SHOP_NAME")));
						return;
					}else if("2".equals(col)) {
						
						
						
						output("0000", "https://be.ele.me/crm?qt=apishopbindpage&source=F3DE3551DA3874B09C4CBFCB8F4D7AE43D8DD2DF8BD78C1B84EB07DA7565F9A2");
						return;
					}
					
					
					output("0000", "成功");
					return;
				} catch (Exception e) {
					logger.error("error", e);
					output("9999", "出错");
					return;
				}
			}
			
		
		}

