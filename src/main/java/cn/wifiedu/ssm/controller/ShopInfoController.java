package cn.wifiedu.ssm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.PictureUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

@Controller
@Scope("prototype")
public class ShopInfoController extends BaseController {

	private static Logger logger = Logger.getLogger(FunctionController.class);

	public static final long ONE_PIC_MAXSIZE = 2100000L;	//允许的单张图片最大大小
	private static final String SHOP_PICPATH = "assets/shoppic";	//图片存储位置
	
	private static final long SHOP_DESC_MAX_SIZE = 6900000L; //图文说明允许的最大大小
	private static final String SHOP_DESC_PIC_PATH = "assets/shopdesc";
	
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
		JSONObject userObj = JSONObject.parseObject(userJson);
		
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
		JSONObject userObj = JSONObject.parseObject(userJson);
		
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
		JSONObject userObj = JSONObject.parseObject(userJson);
		
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

			//String token = "o40NVwcZRjgFCE5GSb9JKb6luzb4";
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSONObject.parseObject(userJson);
			
			map.put("SHOP_ID", userObj.get("FK_SHOP"));
			map.put("USER_ID", userObj.get("USER_PK")); 
			map.put("ROLE_ID", userObj.get("FK_ROLE")); 
		
			
			map.put("sqlMapId", "getShopInfo");
			List<Map<String, Object>> shopInfoList = openService.queryForList(map);
			shopInfoList.get(0).put("userPk", userObj.get("USER_PK"));
			
			output(shopInfoList);
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
			
			String imgLogo = (String) map.get("IMG_LOGO");	//店铺logo base64码
			String imgHead = (String) map.get("IMG_HEAD");	//店铺门头	base64码
			String imgBoday = (String) map.get("IMG_BODAY");	//店铺形象	base64码
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
	
	public static void main(String[] args) {
		String s = "<img src=\"b\"> <img src=\"data:image/jpeg;base64,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\">";
		 
		Pattern p = Pattern.compile("<img src=\"(.*?)\">");
	      // 获取 matcher 对象
	     Matcher m = p.matcher(s);
	     StringBuffer sb = new StringBuffer();
	     while(m.find()){
	    	 String base64Str = m.group(1);
	    	 if(base64Str.indexOf("data:image/")!=-1){
	    		 m.appendReplacement(sb,"<img src=\""+ PictureUtil.base64ToImage(base64Str, SHOP_DESC_PIC_PATH) +"\">");
	    	 }
	     }
	     m.appendTail(sb);
	     System.out.println(sb.toString());
				
	}
}
