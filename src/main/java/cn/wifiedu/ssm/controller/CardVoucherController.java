package cn.wifiedu.ssm.controller;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.DateUtil;
import cn.wifiedu.ssm.util.PictureUtil;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.WxUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
* <p>Title: CardVoucherController</p>
* <p>Description:卡券与微信以及数据库进行交互 </p>
* <p>Company: feixu</p>
* @author    wangjinglong
* @date       2018年9月3日
*/
@Controller
@Scope("prototype")
public class CardVoucherController extends BaseController{
	
	@Resource
	private JedisClient jedisClient;

	@Resource
	InterfaceController interfaceController;
	
	@Resource
	OpenService openService;
	
	private static Logger logger = Logger.getLogger(WxController.class);
	
	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
	this.openService = openService;
	}
	
	private static final String CARD_PICPATH = "assets/cardspic";	//图片存储位置
	
	 private enum WeekEnum  
	    {  
		 	MONDAY,TUESDAY 
	    };  
	    
	/**
	* <p>Title: showAgentShopInfo</p>
	* <p>Description:创建卡券 </p>
	*/
	@RequestMapping(value = "/CardVoucher_create_createCard", method = RequestMethod.POST)
	public void createCard() {
		try {
			
			Map<String, Object> map = getParameterMap();
			
			//数据库保存参数
			Map<String, Object> param = new HashMap<String,Object>();
			/*获取appid*/
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			String accessToken = "";
			String appid = userObj.getString("FK_APP");
			logger.info("redis日志:创建卡券获取userJson"+userJson);
			logger.info("redis日志:创建卡券获取appid"+appid);
			/*获取accessToken*/
			if (!jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + appid)) {
				accessToken = WxUtil.getWxAccessToken(appid,
						interfaceController.getComponentAccessToken(), interfaceController.getRefreshTokenByAppId(appid));
				jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + appid, accessToken);
				jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + appid, 3600 * 1);
			} else {
				accessToken = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + appid);
			}
			logger.info("redis日志:创建卡券获取appid"+accessToken);
			/*上传店铺LOGO*/
			String imgLOGOStr = map.get("IMG_LOGO_STR").toString();
			String [] imgLogoArray = imgLOGOStr.split(",");
			String logoReStr = CommonUtil.uploadImg(imgLogoArray[1],"600000", imgLogoArray[2], accessToken);
			JSONObject obj = JSON.parseObject(logoReStr);
			String logoWxUrl = obj.get("url").toString();
			System.out.println("店铺LOGOurl:"+logoWxUrl);
			/*上传卡券形象图片*/
			String imgBodayStr = map.get("IMG_BODAY_STR").toString();
			String [] imgBodayArray = imgBodayStr.split(",");
			String imgBodaySize = imgBodayArray[3].substring(0, imgBodayArray[3].length()-1);
			String imgBodayReStr = CommonUtil.uploadImg(imgBodayArray[1], imgBodaySize, imgBodayArray[2], accessToken);
			JSONObject imgBodayObj = JSON.parseObject(imgBodayReStr);
			String bodyWxUrl = imgBodayObj.get("url").toString();
			System.out.println("卡券形象图片url:"+bodyWxUrl);
			/*上传卡券图文说明中的图片到微信服务器*/
			JSONArray jsa = new JSONArray();
			JSONArray localPicArray = new JSONArray();
			String introduceStr = map.get("INTRODUCE_STR").toString();
			JSONArray introduceArray = JSON.parseArray(introduceStr);
			for(Object j: introduceArray){
				JSONObject jb = (JSONObject)j;
				String introduceBase64 = jb.get("img").toString().split(",")[1];
				String introduceReStr = CommonUtil.uploadImg(introduceBase64, jb.get("imgSize").toString(), jb.get("imgName").toString(), accessToken);
				JSONObject introduceReObj = JSON.parseObject(introduceReStr);
				String introduceURL = introduceReObj.get("url").toString();
				JSONObject jsb = new JSONObject();
				JSONObject localJsb = new JSONObject();
				localJsb.put("text", jb.get("text"));
				localJsb.put("image_url",PictureUtil.base64ToImage(jb.get("img").toString(), CARD_PICPATH));
				jsb.put("image_url", introduceURL);
				jsb.put("text", jb.get("text"));
				localPicArray.add(localJsb);
				jsa.add(jsb);
			}
			/*组装POST数据*/
			/*JSONArray jsonPost = new JSONArray();*/
			JSONObject cardJsonObj = new JSONObject();
			JSONObject baseInfoJsonObj = new JSONObject();
			JSONObject baseInfoInnerJsonObj = new JSONObject();
			JSONObject dateInfoJsonObj = new JSONObject();
			JSONObject skuJsonObj = new JSONObject();
			JSONObject advanceInfoJsonObj = new JSONObject();
			JSONObject totalObject = new JSONObject();
		/*	jsonPost.add(cardJsonObj);*/
			cardJsonObj.put("card_type", map.get("CARD_VOUCHER_TYPE").toString());
			cardJsonObj.put( map.get("CARD_VOUCHER_TYPE").toString().toLowerCase(), baseInfoJsonObj);
			baseInfoJsonObj.put("base_info",baseInfoInnerJsonObj);
			/*装logo图片URL*/
			baseInfoInnerJsonObj.put("logo_url",logoWxUrl);
			param.put("logo_url",PictureUtil.base64ToImage(map.get("IMG_LOGO_STR").toString(), CARD_PICPATH));
			/*根据商铺ID查询商铺名称*/
			Map<String, Object> selectShopMap = new HashMap<>();
			selectShopMap.put("SHOP_FK", userObj.getString("FK_SHOP"));
			selectShopMap.put("sqlMapId", "SelectByPrimaryKey");
			Map<String, Object> reShopMap = (Map<String, Object>)openService.queryForObject(selectShopMap);
			/*装商铺名称*/
			baseInfoInnerJsonObj.put("brand_name",reShopMap.get("SHOP_NAME"));
			param.put("brand_name",reShopMap.get("SHOP_NAME"));
			/*装卡券码型*/
			baseInfoInnerJsonObj.put("code_type",map.get("code_type"));
			param.put("code_type",map.get("code_type"));
			/*装卡券名称*/
			baseInfoInnerJsonObj.put("title",map.get("title"));
			param.put("title",map.get("title"));
			/*装卡券颜色*/
			baseInfoInnerJsonObj.put("color",map.get("confirmName"));
			param.put("color",map.get("confirmName"));
			/*装卡券使用提醒*/
			baseInfoInnerJsonObj.put("notice",map.get("notice"));
			param.put("notice",map.get("notice"));
			/*装卡券服务电话*/
			baseInfoInnerJsonObj.put("service_phone",reShopMap.get("SHOP_TEL"));
			param.put("service_phone",reShopMap.get("SHOP_TEL"));
			/*装卡券使用说明*/
			baseInfoInnerJsonObj.put("description",map.get("description")+"\n请在消费时向商户提出\n"+map.get("SHOP_NAME")+"店内均可使用");
			param.put("description",map.get("description")+"\n请在消费时向商户提出\n"+map.get("SHOP_NAME")+"店内均可使用");
			/*装卡券有效期设置*/
			dateInfoJsonObj.put("type", map.get("EXPIRY_DATE"));
			param.put("date_type",map.get("EXPIRY_DATE"));
			/*根据卡券有效期选择的不同类型分别处理*/
			if(map.get("EXPIRY_DATE").equals("DATE_TYPE_FIX_TIME_RANGE")){
				dateInfoJsonObj.put("begin_timestamp", Integer.parseUnsignedInt(DateUtil.date2TimeStamp(map.get("begin_time").toString())));
				dateInfoJsonObj.put("end_timestamp", Integer.parseUnsignedInt(DateUtil.date2TimeStamp(map.get("end_time").toString())));
				param.put("begin_timestamp", Integer.parseUnsignedInt(DateUtil.date2TimeStamp(map.get("begin_time").toString())));
				param.put("end_timestamp", Integer.parseUnsignedInt(DateUtil.date2TimeStamp(map.get("end_time").toString())));
			}else{
				dateInfoJsonObj.put("fixed_term",map.get("fixed_term"));
				dateInfoJsonObj.put("fixed_begin_term",map.get("fixed_begin_term"));
				param.put("fixed_term",map.get("fixed_term"));
				param.put("fixed_begin_term",map.get("fixed_begin_term"));
			}
			baseInfoInnerJsonObj.put("date_info",dateInfoJsonObj);
			/*装入卡券数量*/
			baseInfoInnerJsonObj.put("sku", skuJsonObj);
			skuJsonObj.put("quantity", Integer.parseInt(map.get("PREVIEW_COUNT").toString()));
			param.put("quantity", Integer.parseInt(map.get("PREVIEW_COUNT").toString()));
			/*设置用户可领取与核销的卡券数量*/
			baseInfoInnerJsonObj.put("use_limit",Integer.parseInt(map.get("use_limit").toString()));
			baseInfoInnerJsonObj.put("get_limit",Integer.parseInt(map.get("use_limit").toString()));
			param.put("use_limit",Integer.parseInt(map.get("use_limit").toString()));
			param.put("get_limit",Integer.parseInt(map.get("use_limit").toString()));
			/*设置卡券是否可以分享*/
			baseInfoInnerJsonObj.put("can_share",Boolean.parseBoolean(map.get("can_share").toString()));
			param.put("can_share",map.get("can_share"));
			/*设置卡券是否可以转赠*/
			baseInfoInnerJsonObj.put("can_give_friend",Boolean.parseBoolean(map.get("SHARE_TYPE").toString()));
			param.put("can_give_friend",map.get("SHARE_TYPE").toString());
			/*设置卡券适用门店*/
			baseInfoInnerJsonObj.put("location_id_list",map.get("SHOPID").toString().split(","));
			/*处理选择卡券的类型*/
			String cardType = map.get("CARD_VOUCHER_TYPE").toString();
			param.put("card_type",map.get("CARD_VOUCHER_TYPE").toString());
			/*代金券*/
			if(cardType.equals("CASH")){
				baseInfoJsonObj.put("least_cost", Integer.parseInt(map.get("least_cost").toString())*100);
				baseInfoJsonObj.put("reduce_cost", Integer.parseInt(map.get("reduce_cost").toString())*100);
				param.put("least_cost", Integer.parseInt(map.get("least_cost").toString())*100);
				param.put("reduce_cost", Integer.parseInt(map.get("reduce_cost").toString())*100);
			/*折扣券*/	
			}else if(cardType.equals("DISCOUNT")){
				baseInfoJsonObj.put("discount", 100-Integer.parseInt(map.get("discount").toString())*10);
				param.put("discount", 100-Integer.parseInt(map.get("discount").toString())*10);
				/*兑换券*/
			}else if(cardType.equals("GIFT")){
				baseInfoJsonObj.put("gift",map.get("gift"));
				param.put("gift",map.get("gift"));
				/*抵消券*/
			}else{
				baseInfoJsonObj.put("default_detail",map.get("description"));
				baseInfoInnerJsonObj.put("promotion_url_name","次数/金额");
				baseInfoInnerJsonObj.put("promotion_url_sub_title","查看");
				baseInfoInnerJsonObj.put("promotion_app_brand_user_name","gh_86a091e50ad4@app");
				baseInfoInnerJsonObj.put("promotion_app_brand_pass","API/cardPage");
				param.put("dk_total_time",map.get("dk_total_time"));
				param.put("dk_use_time",map.get("dk_use_time"));
				param.put("dk_total_money",map.get("dk_total_money"));
				param.put("dk_use_money",map.get("dk_use_money"));
			}
			/*装入卡券高级信息*/
			baseInfoJsonObj.put("advanced_info",advanceInfoJsonObj);
			JSONObject useCondition = new JSONObject();
			JSONObject abstractJsonObj = new JSONObject();
			advanceInfoJsonObj.put("use_condition", useCondition);
			/*拼写满多少元使用*/
			if(map.get("least_cost")!=null && !map.get("least_cost").toString().equals("")){
				useCondition.put("least_cost", Integer.parseInt(map.get("least_cost").toString())*100);
			}
			if(cardType.equals("GIFT")){
				useCondition.put("least_cost", Integer.parseInt(map.get("dh_cost_acount").toString())*100);
			}
			/*是否可共享*/
			if(map.get("FAVOURABLE_SHARE").toString().equals("不与其他优惠共享")){
				useCondition.put("can_use_with_other_discount", false);
				param.put("can_use_with_other_discount", false);
			}else{
				useCondition.put("can_use_with_other_discount", true);
				param.put("can_use_with_other_discount", true);
			}
			/*封面摘要结构体设置*/
			advanceInfoJsonObj.put("abstract",abstractJsonObj);
			
			abstractJsonObj.put("abstract", reShopMap.get("SHOP_NAME")+"推出多种新季菜品，期待您的光临");
			param.put("abstract", true);
			/*设置封面图片列表*/
			JSONArray picJsonArray = new JSONArray();
			picJsonArray.add(bodyWxUrl);
			abstractJsonObj.put("icon_url_list",picJsonArray);
			param.put("icon_url_list",PictureUtil.base64ToImage(map.get("IMG_BODAY_STR").toString(), CARD_PICPATH));
			/*设置图文列表*/
			advanceInfoJsonObj.put("text_image_list", jsa);
			if(map.get("EFFECTIVE_TIME").toString().equals("1")){
				JSONArray ja = new JSONArray();
				if(map.get("TIMEDUAN").toString().equals("0")){
					String [] weekArray = map.get("weekStr").toString().split(",");
					for(int i = 0;i < weekArray.length;i++){
						JSONObject timeLimitJsonObj = new JSONObject();
						timeLimitJsonObj.put("type",weekArray[i]);
						ja.add(timeLimitJsonObj);
					}
				}else{
					String [] weekArray = map.get("weekStr").toString().split(",");
					String [] begin_hm = map.get("begin_hhmm").toString().split(" ");
					String [] end_hm = map.get("end_hhmm").toString().split(" ");
					for(int i = 0;i < weekArray.length;i++){
						JSONObject timeLimitJsonObj = new JSONObject();
						timeLimitJsonObj.put("type",weekArray[i]);
						timeLimitJsonObj.put("begin_hour",Integer.parseInt(begin_hm[0]));
						timeLimitJsonObj.put("end_hour",Integer.parseInt(end_hm[0]));
						timeLimitJsonObj.put("begin_minute",Integer.parseInt(begin_hm[1]));
						timeLimitJsonObj.put("end_minute",Integer.parseInt(end_hm[1]));
						ja.add(timeLimitJsonObj);
					}
				}
				advanceInfoJsonObj.put("time_limit", ja);
			}
			param.put("text_image_list", localPicArray.toJSONString());
			totalObject.put("card", cardJsonObj);
			/*创建卡券，往微信发送POST请求*/
			String url = CommonUtil.getPath("WX_CREATE_CARD");

			url = url.replace("ACCESS_TOKEN", accessToken);
			
			String result = CommonUtil.WxPOST(url, totalObject.toJSONString(), "UTF-8");
			
			JSONObject createCard = JSON.parseObject(result);
			
			String card_id = "";
			
			if(createCard.containsKey("errcode")&&createCard.get("errmsg").equals("ok")){
				
				JSONObject cardUpdateJsonObj = new JSONObject();
				JSONObject baseUpdateInfoJsonObj = new JSONObject();
				JSONObject baseUpdateInfoInnerJsonObj = new JSONObject();
			/*	jsonPost.add(cardJsonObj);*/
				cardUpdateJsonObj.put("card_id",createCard.get("card_id").toString());
				cardUpdateJsonObj.put(cardType.toLowerCase(), baseUpdateInfoJsonObj);
				baseUpdateInfoJsonObj.put("base_info",baseUpdateInfoInnerJsonObj);
				baseUpdateInfoInnerJsonObj.put("promotion_url","API/cardPage/"+createCard.get("card_id").toString());
				String updateUrl = CommonUtil.getPath("WX_UPDATE_CARD");
				updateUrl = updateUrl.replace("ACCESS_TOKEN", accessToken);
				String updateResult = CommonUtil.WxPOST(updateUrl, cardUpdateJsonObj.toJSONString(), "UTF-8");
				JSONObject updateCard = JSON.parseObject(updateResult);
				System.out.println("更新外连接url返回结果"+updateCard);
				card_id = createCard.get("card_id").toString();
				param.put("card_id",card_id);
				param.put("create_time",StringDeal.getStringDate());
				param.put("create_by", userObj.get("USER_NAME"));
				param.put("sqlMapId", "insertCard");
				String insertRStr = openService.insert(param);
				if(insertRStr!=null){
					param.clear();
					String [] shopArray = map.get("SHOPID").toString().split(",");
					for(int i = 0;i < shopArray.length;i++){
						param.put("FK_SHOP", shopArray[i]);
						param.put("FK_CARD", card_id);
						param.put("sqlMapId", "insertShopAndCard");
						openService.insert(param);
					}
					output("0000","创建成功");
				}
			}
			String str = totalObject.toJSONString();
			System.out.println("参数:"+str);
			System.out.println("创建卡券返回结果:"+result);
			
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "创建失败");
		}
	}
	
	/**
	* <p>Title: updateCard</p>
	* <p>Description:修改卡券 </p>
	*/
	
	@RequestMapping(value = "/CardVoucher_update_updateCard", method = RequestMethod.POST)
	public void updateCard() {
		try {
			
			Map<String, Object> map = getParameterMap();
			
			//数据库保存参数
			Map<String, Object> param = new HashMap<String,Object>();
			param.put("card_id", map.get("card_id"));
			/*获取appid*/
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			logger.info("redis日志:修改卡券获取userJson"+userJson);
			JSONObject userObj = JSON.parseObject(userJson);
			String accessToken = "";
			String appid = userObj.getString("FK_APP");
			logger.info("redis日志:修改卡券获取appid"+appid);
			/*获取accessToken*/
			if (!jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + appid)) {
				accessToken = WxUtil.getWxAccessToken(appid,
						interfaceController.getComponentAccessToken(), interfaceController.getRefreshTokenByAppId(appid));
				jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + appid, accessToken);
				jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + appid, 3600 * 1);
			} else {
				accessToken = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + appid);
			}
			logger.info("redis日志:修改卡券获取accessToken"+accessToken);
			System.out.println("修改卡券的accessToken:"+accessToken);
			/*上传店铺LOGO*/
			String imgLOGOStr = map.get("IMG_LOGO_STR").toString();
			String [] imgLogoArray = imgLOGOStr.split(",");
			String logoWxUrl = null;
			if(imgLogoArray.length != 1){
				String imgSize = imgLogoArray[3].substring(0, imgLogoArray[3].length()-1);
				String logoReStr = CommonUtil.uploadImg(imgLogoArray[1], imgSize, imgLogoArray[2], accessToken);
				JSONObject obj = JSON.parseObject(logoReStr);
				logoWxUrl = obj.get("url").toString();
			}
			System.out.println("店铺LOGOurl:"+logoWxUrl);
			/*组装POST数据*/
			JSONObject cardJsonObj = new JSONObject();
			JSONObject baseInfoJsonObj = new JSONObject();
			JSONObject baseInfoInnerJsonObj = new JSONObject();
			JSONObject dateInfoJsonObj = new JSONObject();
		/*	jsonPost.add(cardJsonObj);*/
			cardJsonObj.put("card_id", map.get("card_id"));
			cardJsonObj.put( map.get("card_type").toString().toLowerCase(), baseInfoJsonObj);
			baseInfoJsonObj.put("base_info",baseInfoInnerJsonObj);
			/*装logo图片URL*/
			if(logoWxUrl!=null){
				baseInfoInnerJsonObj.put("logo_url",logoWxUrl);
				param.put("logo_url",PictureUtil.base64ToImage(map.get("IMG_LOGO_STR").toString(), CARD_PICPATH));
			}
			/*装卡券码型*/
			baseInfoInnerJsonObj.put("code_type",map.get("code_type"));
			param.put("code_type",map.get("code_type"));
			/*装卡券颜色*/
			baseInfoInnerJsonObj.put("color",map.get("confirmName"));
			param.put("color",map.get("confirmName"));
			/*装卡券使用提醒*/
			baseInfoInnerJsonObj.put("notice",map.get("notice"));
			param.put("notice",map.get("notice"));
			/*装卡券使用说明*/
			baseInfoInnerJsonObj.put("description",map.get("description")+"\n请在消费时向商户提出\n"+map.get("SHOP_NAME")+"店内均可使用");
			param.put("description",map.get("description")+"\n请在消费时向商户提出\n"+map.get("SHOP_NAME")+"店内均可使用");
			/*装卡券有效期设置*/
			dateInfoJsonObj.put("type", map.get("EXPIRY_DATE"));
			param.put("date_type",map.get("EXPIRY_DATE"));
			/*根据卡券有效期选择的不同类型分别处理*/
			if(map.get("EXPIRY_DATE").equals("DATE_TYPE_FIX_TIME_RANGE")){
				dateInfoJsonObj.put("begin_timestamp", Integer.parseUnsignedInt(DateUtil.date2TimeStamp(map.get("begin_time").toString())));
				dateInfoJsonObj.put("end_timestamp", Integer.parseUnsignedInt(DateUtil.date2TimeStamp(map.get("end_time").toString())));
				param.put("begin_timestamp", Integer.parseUnsignedInt(DateUtil.date2TimeStamp(map.get("begin_time").toString())));
				param.put("end_timestamp", Integer.parseUnsignedInt(DateUtil.date2TimeStamp(map.get("end_time").toString())));
			}
			baseInfoInnerJsonObj.put("date_info",dateInfoJsonObj);
			/*设置用户可领取与核销的卡券数量*/
			baseInfoInnerJsonObj.put("get_limit",Integer.parseInt(map.get("get_limit").toString()));
			param.put("get_limit",Integer.parseInt(map.get("get_limit").toString()));
			/*设置卡券是否可以分享*/
			baseInfoInnerJsonObj.put("can_share",Boolean.parseBoolean(map.get("can_share").toString()));
			param.put("can_share",map.get("can_share"));
			/*设置卡券是否可以转赠*/
			baseInfoInnerJsonObj.put("can_give_friend",Boolean.parseBoolean(map.get("SHARE_TYPE").toString()));
			param.put("can_give_friend",map.get("SHARE_TYPE").toString());
			/*设置卡券适用门店*/
			baseInfoInnerJsonObj.put("location_id_list",map.get("SHOPID").toString().split(","));
		
		/*	totalObject.put("card", cardJsonObj);*/
			/*创建卡券，往微信发送POST请求*/
			String url = CommonUtil.getPath("WX_UPDATE_CARD");

			url = url.replace("ACCESS_TOKEN", accessToken);
			
			String result = CommonUtil.WxPOST(url, cardJsonObj.toJSONString(), "UTF-8");
			
			JSONObject createCard = JSON.parseObject(result);
			
			System.out.println(createCard.containsKey("errcode")+"code"+createCard.getInteger("errcode"));
			if(createCard.containsKey("errcode")&&createCard.getInteger("errcode").intValue() == 40100){
				output("40100", "有效期设置不正确");
			}else if(createCard.containsKey("errcode")&&createCard.getInteger("errcode").intValue() == 0){
				param.put("update_time",StringDeal.getStringDate());
				param.put("update_by", userObj.get("USER_NAME"));
				param.put("sqlMapId", "updateCardById");
				boolean updateResult = openService.update(param);
				if(!map.get("SHOPID").equals("")){
					param.clear();
					param.put("card_id", map.get("card_id"));
					param.put("sqlMapId", "deleteByCardID");
					boolean deleteResult = openService.delete(param);
					param.clear();
					String [] shopArray = map.get("SHOPID").toString().split(",");
					for(int i = 0;i < shopArray.length;i++){
						param.put("FK_SHOP", shopArray[i]);
						param.put("FK_CARD", map.get("card_id"));
						param.put("sqlMapId", "insertShopAndCard");
						openService.insert(param);
					}
				}
				output("0000","修改成功");
				String str = cardJsonObj.toJSONString();
				System.out.println("参数:"+str);
				System.out.println("创建卡券返回结果:"+result);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "创建失败");
		}
	}
	
	/**
	* <p>Title: loadCardData</p>
	* <p>Description: 显示卡券列表</p>
	*/
	@RequestMapping(value = "/Card_select_loadCardData", method = RequestMethod.POST)
	public void loadCardData() {
		try {
			Map<String, Object> map = new HashMap<String,Object>();
			// 获取当前session信息
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			logger.info("redis日志:显示卡券列表获取userJson"+userJson);
			map.put("SHOP_ID", userObj.get("FK_SHOP"));
			map.put("sqlMapId", "loadCardData");
			List<Map<String,Object>> dataList = openService.queryForList(map);
			if(dataList!=null && dataList.size() !=0){
				output("0000",dataList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "查询失败");
		}
	}
	
	/**
	* <p>Title: deleteCard</p>
	* <p>Description:删除卡券 </p>
	*/
	@RequestMapping(value = "/Card_delete_deleteCard", method = RequestMethod.POST)
	public void deleteCard() {
		try {
			Map<String, Object> map = getParameterMap();
			String card_id = map.get("card_id").toString();
			/*获取appid*/
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			logger.info("redis日志:删除卡券获取userJson"+userJson);
			JSONObject userObj = JSON.parseObject(userJson);
			String accessToken = "";
			String appid = userObj.getString("FK_APP");
			logger.info("redis日志:删除卡券获取appid"+appid);
			/*获取accessToken*/
			if (!jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + appid)) {
				accessToken = WxUtil.getWxAccessToken(appid,
						interfaceController.getComponentAccessToken(), interfaceController.getRefreshTokenByAppId(appid));
				jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + appid, accessToken);
				jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + appid, 3600 * 1);
			} else {
				accessToken = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + appid);
			}
			logger.info("redis日志:删除卡券获取accessToken"+accessToken);
			JSONObject cardJsonObj = new JSONObject();
			
			cardJsonObj.put("card_id", card_id);
			
			String url = CommonUtil.getPath("WX_DELETE_CARD");

			url = url.replace("ACCESS_TOKEN", accessToken);
			
			String result = CommonUtil.WxPOST(url, cardJsonObj.toJSONString(), "UTF-8");
			
			JSONObject createCard = JSON.parseObject(result);
			
			if(createCard.containsKey("errcode")&&createCard.getInteger("errcode").intValue() == 0){
				map.put("sqlMapId", "deleteByCardID");
				boolean deleteResult = openService.delete(map);
				map.put("sqlMapId", "deleteCard");
				boolean deleteCardResult = openService.delete(map);
				if(deleteResult && deleteCardResult){
					output("0000", "删除成功");
				}else{
					output("9999", "删除失败");
				}
			}else{
				output("9999", "删除失败");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "删除失败");
		}
	}
	
	/**
	* <p>Title: loadCardById</p>
	* <p>Description: 根据卡券ID查询卡群信息</p>
	*/
	@RequestMapping(value = "/Card_select_loadCardById", method = RequestMethod.POST)
	public void loadCardById() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadCardInfoById");
			List<Map<String,Object>> dataList = openService.queryForList(map);
			map.put("sqlMapId", "loadShopByCodeID");
			List<Map<String,Object>> shopNameArray = openService.queryForList(map);
			String shopName = "";
			for(int i = 0; i < shopNameArray.size();i++){
				shopName = shopName +","+shopNameArray.get(i).get("SHOP_NAME");
			}
			dataList.get(0).put("shopName", shopName.substring(1, shopName.length()));
			if(dataList!=null && dataList.size() !=0){
				output("0000",dataList.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "查询失败");
		}
	}
	/**
	* <p>Title: loadCardGetList</p>
	* <p>Description: 获取卡券领取列表</p>
	*/
	@RequestMapping(value = "/Card_select_loadCardGetList", method = RequestMethod.POST)
	public void loadCardGetList() {
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			logger.info("redis日志:获取卡券领取列表userJson"+userJson);
			JSONObject userObj = JSON.parseObject(userJson);
			map.put("SHOP_ID", userObj.get("FK_SHOP"));
			map.put("sqlMapId", "selectCardList");
			List<Map<String,Object>> dataList = openService.queryForList(map);
			for(int i = 0;i < dataList.size();i++ ){
				dataList.get(i).put("begin_timestamp",timeConvert(Long.parseLong(dataList.get(i).get("begin_timestamp").toString())));
				dataList.get(i).put("end_timestamp",timeConvert(Long.parseLong(dataList.get(i).get("end_timestamp").toString())));
			}
			output("0000", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "查询失败");
		}
	}
	/**
	* <p>Title: cancelCard</p>
	* <p>Description: 核销卡券</p>
	*/
	@RequestMapping(value = "/Card_cancel_cancelCard", method = RequestMethod.POST)
	public void cancelCard() {
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			logger.info("redis日志:核销卡券userJson"+userJson);
			JSONObject userObj = JSON.parseObject(userJson);
			String accessToken = "";
			String appid = userObj.getString("FK_APP");
			/*获取accessToken*/
			if (!jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + appid)) {
				accessToken = WxUtil.getWxAccessToken(appid,
						interfaceController.getComponentAccessToken(), interfaceController.getRefreshTokenByAppId(appid));
				jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + appid, accessToken);
				jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + appid, 3600 * 1);
			} else {
				accessToken = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + appid);
			}
			String code_id = map.get("code").toString();
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("card_code",code_id);
			param.put("sqlMapId", "selectUserCardByCodeID");
			List<Map<String, Object>> singleUserCard = openService.queryForList(param);
			param.clear();
			param.put("card_id",singleUserCard.get(0).get("card_id"));
			param.put("sqlMapId", "loadCardInfoById");
			List<Map<String, Object>> singleCard = openService.queryForList(param);
			String card_type = singleCard.get(0).get("card_type").toString();
			int syTime = 0;
			double syMoney = 0.0;
			if(card_type.equals("GENERAL_COUPON")){
				Object totalTime = singleCard.get(0).get("dk_total_time");
				Object totalMoney = singleCard.get(0).get("dk_total_money");
				Object dk_use_money = singleCard.get(0).get("dk_use_money");
				Object use_time = singleUserCard.get(0).get("dk_use_time");
				Object use_money = singleUserCard.get(0).get("dk_use_money");
				if(totalTime != null && totalMoney == null){
					syTime = Integer.parseInt(totalTime.toString())- Integer.parseInt(use_time.toString());
					if(syTime == 1){
						JSONObject cardJsonObj = new JSONObject();
						
						cardJsonObj.put("code", map.get("code"));
						
						String url = CommonUtil.getPath("WX_CANCEL_CARD");

						url = url.replace("ACCESS_TOKEN", accessToken);
						
						String result = CommonUtil.WxPOST(url, cardJsonObj.toJSONString(), "UTF-8");
						
						JSONObject cancelCard = JSON.parseObject(result);
						if(cancelCard.getIntValue("errcode") == 0){
							output("0000", "核销成功");
						}else{
							output("9999", "code码输入不正确");
						}
					}else{
						param.clear();
						param.put("card_code",code_id);
						param.put("dk_use_time",String.valueOf(Integer.parseInt(use_time.toString()+1)));
						param.put("sqlMapId", "updateUserCardByCode");
						boolean flag = openService.update(param);
						if(flag){
							output("0000", "核销成功");
						}else{
							output("9999", "code码输入不正确");
						}
					}
				}else{
					syMoney = Double.parseDouble(totalMoney.toString())- Double.parseDouble(use_money.toString());
					
					if(syMoney <= Double.parseDouble(use_money.toString())){
						JSONObject cardJsonObj = new JSONObject();
						
						cardJsonObj.put("code", map.get("code"));
						
						String url = CommonUtil.getPath("WX_CANCEL_CARD");

						url = url.replace("ACCESS_TOKEN", accessToken);
						
						String result = CommonUtil.WxPOST(url, cardJsonObj.toJSONString(), "UTF-8");
						
						JSONObject cancelCard = JSON.parseObject(result);
						if(cancelCard.getIntValue("errcode") == 0){
							output("0000", "核销成功");
						}else{
							output("9999", "code码输入不正确");
						}
					}else{
						param.clear();
						param.put("card_code",code_id);
						param.put("dk_use_money",String.valueOf(Double.parseDouble(use_money.toString())+Double.parseDouble(dk_use_money.toString())));
						param.put("sqlMapId", "updateUserCardByCode");
						boolean flag = openService.update(param);
						if(flag){
							output("0000", "核销成功");
						}else{
							output("9999", "code码输入不正确");
						}
					}
				}
				
			}else{
				JSONObject cardJsonObj = new JSONObject();
				
				cardJsonObj.put("code", map.get("code"));
				
				String url = CommonUtil.getPath("WX_CANCEL_CARD");

				url = url.replace("ACCESS_TOKEN", accessToken);
				
				String result = CommonUtil.WxPOST(url, cardJsonObj.toJSONString(), "UTF-8");
				
				JSONObject cancelCard = JSON.parseObject(result);
				if(cancelCard.getIntValue("errcode") == 0){
					output("0000", "核销成功");
				}else{
					output("9999", "code码输入不正确");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "code码输入不正确");
		}
	}
	 private String timeConvert(long dateValue){
		Date date = new Date(dateValue*1000);
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		return s.format(date);
	}
}
