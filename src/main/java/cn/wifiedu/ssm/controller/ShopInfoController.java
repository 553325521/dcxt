package cn.wifiedu.ssm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.PictureUtil;
import cn.wifiedu.ssm.util.WXJSUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

@Controller
@Scope("prototype")
public class ShopInfoController extends BaseController {

	private static Logger logger = Logger.getLogger(FunctionController.class);

	public static final long ONE_PIC_MAXSIZE = 2100000L;	//允许的单张图片最大大小
	private static final String SHOP_PICPATH = "assets/img/shoppic";	//图片存储位置
	
	private static final long SHOP_DESC_MAX_SIZE = 6900000L; //图文说明允许的最大大小
	private static final String SHOP_DESC_PIC_PATH = "assets/img/shopdesc";
	
	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	@Resource
	private JedisClient jedisClient;
	@Resource
	private  WxController wxcontroller;
	
	/**
	 * 获取用户信息
	 * @throws Exception
	 */
	@RequestMapping("/ShopInfo_getUserInfo_data")
	public void getUserInfo() throws Exception {
		Map<String, Object> map = getParameterMap();
		
		//String token = "o40NVwcZRjgFCE5GSb9JKb6luzb4";
		String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
		String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
		JSONObject userObj = JSON.parseObject(userJson);
		
		map.put("userId", userObj.get("USER_PK"));
		map.put("shopId", userObj.get("FK_SHOP"));
		
		output(map);
	}
	
	
	
	@RequestMapping("/ShopInfo_editYouhuimaidan_data")
	public void editYouhuimaidan() throws Exception {
		Map<String, Object> map = getParameterMap();
		
		String rulePk = map.get("rulePk").toString();
		
		//String token = "o40NVwcZRjgFCE5GSb9JKb6luzb4";
		String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
		String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
		JSONObject userObj = JSON.parseObject(userJson);
		
		map.put("SHOP_ID", userObj.get("FK_SHOP"));
		map.put("USER_ID", userObj.get("USER_PK")); 
		map.put("ROLE_ID", userObj.get("FK_ROLE")); 
		
		if(map.get("rule_model").toString().equals("1")) {
			map.put("rule_model_first", map.get("yh_zkxf").toString());
			map.put("rule_model_second", map.get("yh_zkyh").toString());
		}else if(map.get("rule_model").toString().equals("2")) {
			map.put("rule_model_first", map.get("yh_gdxf").toString());
			map.put("rule_model_second", map.get("yh_gdj").toString());
		}else if(map.get("rule_model").toString().equals("3")) {
			map.put("rule_model_first", map.get("yh_sjxf").toString());
			map.put("rule_model_second", map.get("yh_sjj").toString());
		}
		
		map.put("sqlMapId", "editYouhuimaidan");
		openService.update(map);
		
		map.put("fk_preferential_rule", rulePk);
		
		map.put("sqlMapId", "deleteAllRuleGood");
		if(openService.update(map)) {
			List<Map<String, Object>> list = toListMap(map.get("goodType").toString());
			
			for(int i=0; i<list.size(); i++) {
				map.put("sqlMapId", "saveYouhuimaidanGood");
				map.put("fk_goodtype", list.get(i).get("GTYPE_PK").toString());
				openService.insert(map);
			}
		}
		output("success");
	}
	
	
	/**
	 * 优惠买单设置
	 * @throws Exception 
	 */
	@RequestMapping("/ShopInfo_saveYouhuimaidan_data")
	public void saveYouhuimaidan() throws Exception {
		Map<String, Object> map = getParameterMap();
		
		//String token = "o40NVwcZRjgFCE5GSb9JKb6luzb4";
		String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
		String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
		JSONObject userObj = JSON.parseObject(userJson);
		
		map.put("SHOP_ID", userObj.get("FK_SHOP"));
		map.put("USER_ID", userObj.get("USER_PK")); 
		map.put("ROLE_ID", userObj.get("FK_ROLE")); 
		
		if(map.get("rule_model").toString().equals("1")) {
			map.put("rule_model_first", map.get("yh_zkxf").toString());
			map.put("rule_model_second", map.get("yh_zkyh").toString());
		}else if(map.get("rule_model").toString().equals("2")) {
			map.put("rule_model_first", map.get("yh_gdxf").toString());
			map.put("rule_model_second", map.get("yh_gdj").toString());
		}else if(map.get("rule_model").toString().equals("3")) {
			map.put("rule_model_first", map.get("yh_sjxf").toString());
			map.put("rule_model_second", map.get("yh_sjj").toString());
		}
		
		map.put("sqlMapId", "saveYouhuimaidan");
		String rulePk = openService.insert(map);
		
		map.put("fk_preferential_rule", rulePk);
		
		List<Map<String, Object>> list = toListMap(map.get("goodType").toString());
		
		for(int i=0; i<list.size(); i++) {
			map.put("sqlMapId", "saveYouhuimaidanGood");
			map.put("fk_goodtype", list.get(i).get("GTYPE_PK").toString());
			openService.insert(map);
		}
		
		output("success");
	}
	
	
	/**
	 * 获取当前用户商铺信息
	 * @param request
	 * @param session
	 */
	@RequestMapping("/ShopInfo_getUserShopInfo_data")
	public void getUserShopInfo(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();

			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			
			map.put("SHOP_ID", userObj.get("FK_SHOP"));
			map.put("USER_ID", userObj.get("USER_PK")); 
			map.put("ROLE_ID", userObj.get("FK_ROLE")); 
			
			map.put("sqlMapId", "getShopInfo");
			List<Map<String, Object>> shopInfoList = openService.queryForList(map);
			shopInfoList.get(0).put("userPk", userObj.get("USER_PK"));
			
			Map<String,String> configMap = new HashMap<String,String>();

			
			String appId = CommonUtil.getPath("AppID");
			map.put("jsapi_ticket", wxcontroller.getJsApiTicket(appId));
			configMap = WXJSUtil.getWxConfigMess(map);
			configMap.put("appId",appId);
			
			Map<String,Object> reMap = new HashMap<String,Object>();

			reMap.put("shopinfo", shopInfoList);
			if(configMap !=null){
				reMap.put("config", configMap);
				output("0000", reMap);
				return;
			}
			
			output("9999", "未知异常");
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}
	
	/**
	 * 
	 * @date 2018年8月20日 上午5:51:26 
	 * @author lps
	 * 
	 * @Description:  
	 * @return void 
	 *
	 */
	@RequestMapping("/ShopInfo_update_saveShopInfo")
	public void updateShopInfoById(){
		try {
			Map<String, Object> map = getParameterMap();
			
			map.put("SHOP_TYPE", map.get("SHOP_TYPE_1") + " " + map.get("SHOP_TYPE_2"));
			
			String imgLogo = (String) map.get("IMG_LOGO");	//店铺logo base64码
			if(imgLogo == null){
				output("9999", "请添加一个店铺LOGO");
				return;
			}
			String imgHead = (String) map.get("IMG_HEAD");	//店铺门头	base64码
			if(imgHead == null){
				output("9999", "请添加一个店铺门头图片");
				return;
			}
			String imgBoday = (String) map.get("IMG_BODAY");	//店铺形象	base64码
			if(imgBoday == null){
				output("9999", "请添加一个店铺形象图片");
				return;
			}
			//判断图片是否超出大小
			if(imgLogo.length()*3/4 > ONE_PIC_MAXSIZE || imgHead.length()*3/4 > ONE_PIC_MAXSIZE || imgBoday.length()*3/4 > ONE_PIC_MAXSIZE){
				output("9999", "单张图片不允许超出" + Math.floor(ONE_PIC_MAXSIZE/1000000) + "M");
				return;
			}
			
			//图文说明处理
			String shopRemark = (String) map.get("SHOP_REMARK");
			if(shopRemark.length()*3/4 > SHOP_DESC_MAX_SIZE){
				output("9999", "图文说明图片太大，图片总大小不能超过" + Math.floor(SHOP_DESC_MAX_SIZE/1000000) + "M");
				return;
			}
			
			//开始保存图片
			if(imgLogo.indexOf("data:image/") != -1){
				map.put("IMG_LOGO", PictureUtil.base64ToImage(imgLogo, SHOP_PICPATH));
			}
			if(imgHead.indexOf("data:image/") != -1){
				map.put("IMG_HEAD", PictureUtil.base64ToImage(imgHead, SHOP_PICPATH));
			}
			if(imgBoday.indexOf("data:image/") != -1){
				map.put("IMG_BODAY", PictureUtil.base64ToImage(imgBoday, SHOP_PICPATH));
			}
			
			//替换图文说明里边的base64转换成图片
			Pattern p = Pattern.compile("<img src=\"(.*?)\">");
		      // 获取 matcher 对象
		     Matcher m = p.matcher(shopRemark);
		     StringBuffer sb = new StringBuffer();
		     while(m.find()){
		    	 String base64Str = m.group(1);
		    	 if(base64Str.indexOf("data:image/")!=-1){
		    		 m.appendReplacement(sb,"<img src=\""+ PictureUtil.base64ToImage(base64Str, SHOP_DESC_PIC_PATH) +"\">");
		    	 }
		     }
		     m.appendTail(sb);
		     map.put("SHOP_REMARK",sb.toString());
			
			
			map.put("sqlMapId", "saveShopInfo");
			boolean update = openService.update(map);
			if(!update){
				output("9999", "修改失败");
				return;
			}
			
			output("0000", "修改成功");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
		
		
		
	}

	
	public static List<Map<String, Object>> toListMap(String json){
    	List<Object> list =JSON.parseArray(json);
    	
    	List< Map<String,Object>> listw = new ArrayList<Map<String,Object>>();
    	for (Object object : list){
    	Map<String,Object> ageMap = new HashMap<String,Object>();
    	Map <String,Object> ret = (Map<String, Object>) object;//取出list里面的值转为map
    	
    	listw.add(ret);
    	}
		return listw;
    	
    }
	
	
	/**
	 * 
	 * @date 2018年9月13日 下午9:18:32 
	 * @author lps
	 * 
	 * @Description:  查询店铺积分
	 * @return void 
	 *
	 */
	@RequestMapping("/Shop_select_findShopIntegraByShopId")
	public void findShopIntegraByShopId() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectShopIntegraByShopId");
			
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
	 * @Description:  修改店铺积分折扣
	 * @return void 
	 *
	 */
	
	@RequestMapping("/Shop_update_updateShopIntegra")
	public void updateShopIntegra() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "updateShopIntegraByShopId");
			
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			
			map.put("SHOP_ID", userObj.get("FK_SHOP")); 
			
			boolean b = openService.update(map);
			if(!b){
				output("9999", "更新失败");
				return;
			}
			output("0000", "更新成功");
			return;
		} catch (Exception e) {
			logger.error("error", e);
			output("9999", "出错");
			return;
		}
	}
	
	
	/**
	 * 
	 * @date 2018年9月14日 上午11:31:00 
	 * @author lps
	 * 
	 * @Description:  查询该店铺的会员卡发放记录
	 * @return void 
	 *
	 */
	@RequestMapping("/Shop_select_findVipGiveOutByShopId")
	public void findVipGiveOutByShopId() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectVipGiveOutByShopId");
			
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			
			map.put("SHOP_ID", userObj.get("FK_SHOP")); 
			
			List reList = openService.queryForList(map);
			if(reList == null){
				output("9999", "查询错误");
				return;
			}
			
			//查询该店铺下的所有会员卡名称
			map.put("sqlMapId", "selectVipCardNameListByShopId");
			List<Map<String, Object>> resultList = openService.queryForList(map);
			if(resultList == null){
				output("9999", "你还没有会员卡");
				return;
			}
			//把List<Map<String,String>>转换为List<String>,去除原来map中的key
			List reList2 = resultList.stream().map(a -> a.get("VCARD_NAME")).collect(Collectors.toList());
			
			Map reMap = new HashMap<String, List>();
			reMap.put("vcard_record", reList);
			reMap.put("vcard_name_list", reList2);
			
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
	 * @date 2018年9月17日 下午9:50:33 
	 * @author lps
	 * 
	 * @Description:  查询该店铺的转盘中奖记录
	 * @return void 
	 *
	 */
	@RequestMapping("/Shop_select_findTurntablePrizeRecordByShopId")
	public void findTurntablePrizeRecordByShopId() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectTurntablePrizeRecordByShopId");
			
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			
			map.put("SHOP_ID", userObj.get("FK_SHOP")); 
			
			List reList = openService.queryForList(map);
			if(reList == null){
				output("9999", "查询错误");
				return;
			}
			
			//查询该店铺下的所有转盘名称
			map.put("sqlMapId", "selectTurntableNameListByShopId");
			List<Map<String, Object>> resultList = openService.queryForList(map);
			if(resultList == null){
				output("9999", "你还没有转盘");
				return;
			}
			//把List<Map<String,String>>转换为List<String>,去除原来map中的key
			List reList2 = resultList.stream().map(a -> a.get("ACTIVITY_NAME")).collect(Collectors.toList());
			
			Map reMap = new HashMap<String, List>();
			reMap.put("turntable_record", reList);
			reMap.put("turntable_name_list", reList2);
			
			output("0000", reMap);
			return;
		} catch (Exception e) {
			logger.error("error", e);
			output("9999", "出错");
			return;
		}
	}
	
	
	
	public static void main(String[] args) {
		List<Map<String, Object>> reList2 = new ArrayList<Map<String,Object>>();
		Map map = new HashMap<String, String>();
		map.put("VCARD_NAME", "222");
		reList2.add(map);
		
		map = new HashMap<String, String>();
		map.put("VCARD_NAME", "555");
		reList2.add(map);
		List list3 = reList2;
		
		System.out.println(list3);
	}
	
	
}
