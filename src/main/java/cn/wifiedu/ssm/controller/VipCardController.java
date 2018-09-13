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
		public class VipCardController extends BaseController {

			private static Logger logger = Logger.getLogger(VipCardController.class);

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
			private static final String VIPCARD_PICPATH = "assets/img/vipcard";	//会员卡图片存储位置
			private static final String VIPCARD_TWJS_PICPATH = "assets/img/vipcard_twjs"; //会员卡图文介绍图片存储位置
			
			/**
			 * 
			 * @date 2018年9月11日 下午5:26:02 
			 * @author lps
			 * 
			 * @Description: 插入会员卡
			 * @param request
			 * @param session 
			 * @return void 
			 *
			 */
			
			@RequestMapping("/VipCard_insert_insertOrUpdateVipCard")
			public void addVipCard(HttpServletRequest request, HttpSession session){
				try {
					Map map = getParameterMap();
					
					//表单验证
					if(StringUtils.isBlank((String)map.get("VCARD_NAME")) || 
							StringUtils.isBlank((String)map.get("IS_USE")) || 
							StringUtils.isBlank((String)map.get("ALLOTTED_TIME")) || 
							StringUtils.isBlank((String)map.get("VCARD_ZKXS")) ||
							StringUtils.isBlank((String)map.get("VCARD_JFXS")) ||
							StringUtils.isBlank((String)map.get("START_MONEY")) ||
							StringUtils.isBlank((String)map.get("START_JF")) || 
							StringUtils.isBlank((String)map.get("USE_SHOP")) || 
							StringUtils.isBlank((String)map.get("VCARD_LOGO")) || 
							StringUtils.isBlank((String)map.get("BACKGROUND_IMAGE"))
							){
						output("9999","信息填写不完成");
						return;
					}
					Integer onePicSize = map.get("VCARD_LOGO").toString().length()*3/4;
					if(onePicSize < 10){output("9999","添加一个LOGO"); return;}
					if(onePicSize > ONE_PIC_MAXSIZE){
						output("9999","单张图片大小不允许超过" + Math.floor(ONE_PIC_MAXSIZE/1000000) + "M");
						return;
					}
					
					onePicSize = map.get("BACKGROUND_IMAGE").toString().length()*3/4;
					if(onePicSize < 10){output("9999","添加一个背景图片"); return;}
					if(onePicSize > ONE_PIC_MAXSIZE){
						output("9999","单张图片大小不允许超过" + Math.floor(ONE_PIC_MAXSIZE/1000000) + "M");
						return;
					}
					
					//图文说明里边的图片和文字验证
					List<Map<String, String>> twDescList = (List<Map<String, String>>) JSON.parse((String)map.get("VCARD_TWJS"));
//					String result = twDescList.stream().forEach(action);
					
					for (Map<String, String> twDescMap : twDescList) {
						if(twDescMap.get("img").length()*3/4 > ONE_PIC_MAXSIZE){
							output("9999","单张图片大小不允许超过" + Math.floor(ONE_PIC_MAXSIZE/1000000) + "M");
							return;
						}
						if(twDescMap.get("text").length() > 200){
							output("9999","图文介绍文字超过200");
							return;
						}
					}
					
					//表单验证成功，开始插入
					
					//插入店铺LOGO
					if(map.get("VCARD_LOGO").toString().indexOf("data:image/") != -1){
						map.put("VCARD_LOGO",PictureUtil.base64ToImage(map.get("VCARD_LOGO").toString(), VIPCARD_PICPATH));
					}
					//插入会员卡背景照片
					if(map.get("BACKGROUND_IMAGE").toString().indexOf("data:image/") != -1){
						map.put("BACKGROUND_IMAGE",PictureUtil.base64ToImage(map.get("BACKGROUND_IMAGE").toString(), VIPCARD_PICPATH));
					}
					//插入图文介绍图片
					for (Map<String, String> twDescMap : twDescList) {
						if(twDescMap.get("img").toString().indexOf("data:image/") != -1){
							twDescMap.put("img", PictureUtil.base64ToImage(twDescMap.get("img").toString(), VIPCARD_TWJS_PICPATH));
						}
					}
					map.put("VCARD_TWJS", JSONObject.toJSONString(twDescList));
					
					//判断是添加还是删除
					if(StringUtils.isBlank((String)map.get("VCARD_PK")) || "undefined".equals(map.get("VCARD_PK"))){
						//是添加
						map.put("sqlMapId", "insertVipCard");
						String insert = openService.insert(map);
						if(insert != null){
							//添加商铺会员卡对应关系表
							String shopsStr = (String)map.get("USE_SHOP");
							String[] shops = shopsStr.split(",");
							map.put("sqlMapId", "insertShopVipCard");
							for(int i=0;i<shops.length;i++){
								map.put("SHOP_ID", shops[i]);
								map.put("VIP_CARD_ID", insert);
								openService.insert(map);
							}
							output("0000","添加成功");
							return;
						}else{
							output("9999","添加失败");
							return;
						}
					}else{//是更新
						map.put("sqlMapId", "updaeteVipCardById");
						boolean b = openService.update(map);
						if(b){
							//再添加现在的会员卡关系
							map.put("sqlMapId", "removeShopVipCardByVipCardId");
							map.put("VIP_CARD_ID",map.get("VCARD_PK"));
							boolean delete = openService.delete(map);
							if(delete){
								String shopsStr = (String)map.get("USE_SHOP");
								String[] shops = shopsStr.split(",");
								map.put("sqlMapId", "insertShopVipCard");
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
							output("9999","更新失败");
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
			 * @Description: 根绝会员卡id查询会员卡信息
			 * @param request
			 * @param session 
			 * @return void 
			 *
			 */
			@RequestMapping("/VipCard_select_findVipCardById")
			public void findVipCardById(HttpServletRequest request, HttpSession session){
				try {
					Map map = getParameterMap();
					map.put("sqlMapId", "selectVipCardById");
							
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
			 * @Description: 查询该商铺下所有的会员卡
			 * @param request
			 * @param session 
			 * @return void 
			 *
			 */
			@RequestMapping("/ShopVipCard_select_findVipCard")
			public void findVipCardByShopId(HttpServletRequest request, HttpSession session){
				try {
					Map map = getParameterMap();
					map.put("sqlMapId", "selectVipCardListByShopId");
					
					
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
			
}

