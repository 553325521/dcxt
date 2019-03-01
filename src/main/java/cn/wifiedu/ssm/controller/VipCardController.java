package cn.wifiedu.ssm.controller;

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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.DateUtil;
import cn.wifiedu.ssm.util.PictureUtil;
import cn.wifiedu.ssm.util.WxUtil;
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

	@Resource
	InterfaceController interfaceController;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	public static final long ONE_PIC_MAXSIZE = 2100000L; // 允许的单张图片最大大小
	public static final long ALL_PICMAX_SIZE = 6300000L; // 允许的所有图片最大大小
	public static final int MAX_PIC_NUM = 10; // 允许的图片最大数量
	private static final String VIPCARD_PICPATH = "assets/img/vipcard"; // 会员卡图片存储位置
	private static final String VIPCARD_TWJS_PICPATH = "assets/img/vipcard_twjs"; // 会员卡图文介绍图片存储位置

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
	public void addVipCard(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();

			// 表单验证
			if (StringUtils.isBlank((String) map.get("VCARD_NAME")) || StringUtils.isBlank((String) map.get("IS_USE"))
					|| StringUtils.isBlank((String) map.get("ALLOTTED_TIME"))
					|| StringUtils.isBlank((String) map.get("VCARD_ZKXS"))
					|| StringUtils.isBlank((String) map.get("VCARD_JFXS"))
					|| StringUtils.isBlank((String) map.get("START_MONEY"))
					|| StringUtils.isBlank((String) map.get("START_JF")) ||
					// StringUtils.isBlank((String)map.get("USE_SHOP")) ||
					StringUtils.isBlank((String) map.get("VCARD_LOGO"))
					|| StringUtils.isBlank((String) map.get("BACKGROUND_IMAGE"))) {
				output("9999", "信息填写不完成");
				return;
			}

			// 元转换成分
			map.put("START_MONEY", (long) (Double.parseDouble((String) map.get("START_MONEY")) * 100));
			// 判断单张图片大小
			Integer onePicSize = map.get("VCARD_LOGO").toString().length() * 3 / 4;
			if (onePicSize < 10) {
				output("9999", "添加一个LOGO");
				return;
			}
			if (onePicSize > ONE_PIC_MAXSIZE) {
				output("9999", "单张图片大小不允许超过" + Math.floor(ONE_PIC_MAXSIZE / 1000000) + "M");
				return;
			}

			onePicSize = map.get("BACKGROUND_IMAGE").toString().length() * 3 / 4;
			if (onePicSize < 10) {
				output("9999", "添加一个背景图片");
				return;
			}
			if (onePicSize > ONE_PIC_MAXSIZE) {
				output("9999", "单张图片大小不允许超过" + Math.floor(ONE_PIC_MAXSIZE / 1000000) + "M");
				return;
			}

			// 图文说明里边的图片和文字验证
			List<Map<String, String>> twDescList = (List<Map<String, String>>) JSON
					.parse((String) map.get("VCARD_TWJS"));
			List<Map<String, String>> twDescList1 = (List<Map<String, String>>) JSON
					.parse((String) map.get("VCARD_TWJS"));
			// String result = twDescList.stream().forEach(action);

			for (Map<String, String> twDescMap : twDescList) {
				if (twDescMap.get("img").length() * 3 / 4 > ONE_PIC_MAXSIZE) {
					output("9999", "单张图片大小不允许超过" + Math.floor(ONE_PIC_MAXSIZE / 1000000) + "M");
					return;
				}
				if (twDescMap.get("text").length() > 200) {
					output("9999", "图文介绍文字超过200");
					return;
				}
			}

			/* 获取appid */
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			logger.info("redis日志:创建会员卡userJson" + userJson);
			JSONObject userObj = JSON.parseObject(userJson);
			String accessToken = "";
			String appid = userObj.getString("FK_APP");
			System.out.println("创建会员卡的appid:" + appid);
			/* 获取accessToken */
			if (!jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + appid)) {
				accessToken = WxUtil.getWxAccessToken(appid, interfaceController.getComponentAccessToken(),
						interfaceController.getRefreshTokenByAppId(appid));
				jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + appid, accessToken);
				jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + appid, 3600 * 1);
			} else {
				accessToken = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + appid);
			}
			// 表单验证成功，开始插入

			// 插入店铺LOGO
			String VCARD_LOGO_STR = map.get("VCARD_LOGO").toString();
			if (map.get("VCARD_LOGO").toString().indexOf("data:image/") != -1) {
				map.put("VCARD_LOGO", PictureUtil.base64ToImage(map.get("VCARD_LOGO").toString(), VIPCARD_PICPATH));
			}
			// 插入会员卡背景照片
			String BACKGROUND_IMAGE_STR = map.get("BACKGROUND_IMAGE").toString();
			if (map.get("BACKGROUND_IMAGE").toString().indexOf("data:image/") != -1) {
				map.put("BACKGROUND_IMAGE",
						PictureUtil.base64ToImage(map.get("BACKGROUND_IMAGE").toString(), VIPCARD_PICPATH));
			}
			// 插入图文介绍图片
			for (Map<String, String> twDescMap : twDescList) {
				if (twDescMap.get("img").toString().indexOf("data:image/") != -1) {
					twDescMap.put("img",
							PictureUtil.base64ToImage(twDescMap.get("img").toString(), VIPCARD_TWJS_PICPATH));
				}
			}
			map.put("VCARD_TWJS", JSON.toJSONString(twDescList));
			// 判断是添加还是修改
			if (StringUtils.isBlank((String) map.get("VCARD_PK")) || "undefined".equals(map.get("VCARD_PK"))) {
				// 添加的业务逻辑
				/* 往微信上传店铺LOGO */
				String logoWxUrl = "";
				if (VCARD_LOGO_STR.indexOf("data:image/") != -1) {
					String imgLOGOStr = VCARD_LOGO_STR;
					String[] imgLogoArray = imgLOGOStr.split(",");
					String logoReStr = CommonUtil.uploadImg(imgLogoArray[1],
							String.valueOf(VCARD_LOGO_STR.length() * 3 / 4), imgLogoArray[0], accessToken);
					JSONObject obj = JSON.parseObject(logoReStr);
					logoWxUrl = obj.get("url").toString();
				}
				// 往微信上传背景图片
				String bgImgUrl = "";
				if (BACKGROUND_IMAGE_STR.indexOf("data:image/") != -1) {
					String[] bgImgArray = BACKGROUND_IMAGE_STR.split(",");
					String bgReStr = CommonUtil.uploadImg(bgImgArray[1],
							String.valueOf(BACKGROUND_IMAGE_STR.length() * 3 / 4), bgImgArray[0], accessToken);
					JSONObject obj = JSON.parseObject(bgReStr);
					bgImgUrl = obj.get("url").toString();
				}
				// 往微信上传图文介绍里的图片
				JSONArray instroduceJsonArray = new JSONArray();
				if (twDescList1.size() != 0) {
					for (int i = 0; i < twDescList1.size(); i++) {
						JSONObject instroduceJson = new JSONObject();
						Map<String, String> instroduceMap = twDescList1.get(i);
						String base64 = instroduceMap.get("img");
						String[] instroduceImgArray = base64.split(",");
						String instroduceReStr = CommonUtil.uploadImg(instroduceImgArray[1],
								String.valueOf(base64.length() * 3 / 4), instroduceImgArray[0], accessToken);
						JSONObject obj = JSON.parseObject(instroduceReStr);
						String instroduceImgUrl = obj.get("url").toString();
						instroduceJson.put("image_url", instroduceImgUrl);
						instroduceJson.put("text", instroduceMap.get("text"));
						instroduceJsonArray.add(instroduceJson);
					}
				}
				// 组装post数据
				JSONObject postJsonObj = new JSONObject();
				JSONObject cardJsonObj = new JSONObject();
				JSONObject memberCardJsonObj = new JSONObject();
				JSONObject baseInfoJsonObj = new JSONObject();
				JSONObject skuJsonObj = new JSONObject();
				JSONObject dateJsonObj = new JSONObject();
				JSONObject swipeJsonObj = new JSONObject();
				JSONObject customJsonObj = new JSONObject();
				JSONObject advanceJsonObj = new JSONObject();
				JSONObject bonusRuleJsonObj = new JSONObject();
				JSONObject payinfoJsonObj = new JSONObject();
				postJsonObj.put("card", cardJsonObj);
				cardJsonObj.put("card_type", "MEMBER_CARD");
				cardJsonObj.put("member_card", memberCardJsonObj);
				memberCardJsonObj.put("background_pic_url", bgImgUrl);
				memberCardJsonObj.put("base_info", baseInfoJsonObj);
				baseInfoJsonObj.put("logo_url", logoWxUrl);
				/* 根据商铺ID查询商铺名称 */
				Map<String, Object> selectShopMap = new HashMap<>();
				selectShopMap.put("SHOP_FK", userObj.getString("FK_SHOP"));
				selectShopMap.put("sqlMapId", "SelectByPrimaryKey");
				Map<String, Object> reShopMap = (Map<String, Object>) openService.queryForObject(selectShopMap);
				/* 装商铺名称 */
				baseInfoJsonObj.put("brand_name", reShopMap.get("SHOP_NAME"));
				baseInfoJsonObj.put("code_type", "CODE_TYPE_TEXT");
				baseInfoJsonObj.put("title", map.get("VCARD_NAME"));
				baseInfoJsonObj.put("color", map.get("confirmName"));
				baseInfoJsonObj.put("use_custom_code", false);
				baseInfoJsonObj.put("notice", "结账时出示会员卡");
				baseInfoJsonObj.put("description", map.get("VCARD_SYXZ"));
				baseInfoJsonObj.put("sku", skuJsonObj);
				skuJsonObj.put("quantity", 50);
				baseInfoJsonObj.put("date_info", dateJsonObj);
				if (map.get("ALLOTTED_TIME").equals("1")) {
					dateJsonObj.put("type", "DATE_TYPE_PERMANENT");
				} else {
					dateJsonObj.put("type", "DATE_TYPE_FIX_TIME_RANGE");
					String[] allowTimeArray = map.get("ALLOTTED_TIME_PERIOD").toString().split(" ");
					dateJsonObj.put("begin_timestamp", Integer.parseInt(DateUtil.date2TimeStamp(allowTimeArray[0])));
					dateJsonObj.put("end_timestamp", Integer.parseInt(DateUtil.date2TimeStamp(allowTimeArray[1])));
				}
				baseInfoJsonObj.put("get_limit", 1);
				baseInfoJsonObj.put("pay_info", payinfoJsonObj);
				payinfoJsonObj.put("swipe_card", swipeJsonObj);
				swipeJsonObj.put("is_swipe_card", true);
				baseInfoJsonObj.put("is_pay_and_qrcode", true);
				memberCardJsonObj.put("prerogative", map.get("VCARD_TQSM"));
				// memberCardJsonObj.put("auto_activate",true);
				memberCardJsonObj.put("wx_activate", true);
				memberCardJsonObj.put("wx_activate_after_submit", true);
				// 接受用户提交信息的商家界面链接
				String submitInfoUrl = CommonUtil.getPath("project_url").replace("DATA", "toActiveMemberCardPage");
				memberCardJsonObj.put("wx_activate_after_submit_url", submitInfoUrl);
				memberCardJsonObj.put("supply_bonus", true);
				memberCardJsonObj.put("supply_balance", false);
				memberCardJsonObj.put("custom_field1", customJsonObj);
				customJsonObj.put("name_type", "FIELD_NAME_TYPE_LEVEL");
				customJsonObj.put("url", "http://www.qq.com");
				memberCardJsonObj.put("advanced_info", advanceJsonObj);
				advanceJsonObj.put("text_image_list", instroduceJsonArray);
				memberCardJsonObj.put("discount",
						((int) Double.parseDouble(map.get("VCARD_ZKXS").toString())) / 10 - 1);
				memberCardJsonObj.put("bonus_rule", bonusRuleJsonObj);
				/*
				 * bonusRuleJsonObj.put("cost_money_unit",10000);
				 * bonusRuleJsonObj.put("increase_bonus",10);
				 * bonusRuleJsonObj.put("max_increase _bonus",100);
				 * bonusRuleJsonObj.put("init_increase _bonus",5);
				 * bonusRuleJsonObj.put("cost_bonus_unit",5);
				 * bonusRuleJsonObj.put("reduce_money",50);
				 * p40NVwbooz1RGO07t85XadG_yW-4
				 * bonusRuleJsonObj.put("least_moneyto_use_bonus",500);
				 * bonusRuleJsonObj.put("max_reduce _bonus",100);
				 */
				bonusRuleJsonObj.put("cost_money_unit", 10000);
				bonusRuleJsonObj.put("increase_bonus", (int) Double.parseDouble(map.get("VCARD_JFXS").toString()));
				bonusRuleJsonObj.put("init_increase_bonus", (int) Double.parseDouble(map.get("START_JF").toString()));
				/* 创建会员卡，往微信发送POST请求 */
				String url = CommonUtil.getPath("WX_CREATE_CARD");
				url = url.replace("ACCESS_TOKEN", accessToken);
				String result = CommonUtil.WxPOST(url, postJsonObj.toJSONString(), "UTF-8");
				System.out.println("accessToken===========" + accessToken);
				System.out.println("参数============" + postJsonObj.toJSONString());
				JSONObject createCard = JSON.parseObject(result);
				System.out.println("================" + createCard + "======================");
				if (createCard.containsKey("errcode") && !createCard.getString("errmsg").equals("ok")) {
					output(createCard.getString("errcode"), "创建失败");
					return;
				} else {
					JSONObject setFormPost = new JSONObject();
					setFormPost.put("card_id", createCard.get("card_id"));
					JSONObject required_form = new JSONObject();
					setFormPost.put("required_form", required_form);
					required_form.put("can_modify", false);
					String[] common_field_id_list = new String[] { "USER_FORM_INFO_FLAG_MOBILE" };
					required_form.put("common_field_id_list", common_field_id_list);
					String setUrl = CommonUtil.getPath("Wx_Set_activateUserForm");
					setUrl = setUrl.replace("ACCESS_TOKEN", accessToken);
					logger.info("提交参数" + setFormPost.toJSONString());
					String aResult = CommonUtil.WxPOST(setUrl, setFormPost.toJSONString(), "UTF-8");
					System.out.println(aResult);
					logger.info("创建form返回结果" + aResult);
					map.put("VCARD_IDNUMBER", createCard.get("card_id"));
					map.put("sqlMapId", "insertVipCard");
					String insert = openService.insert(map);
					if (insert != null) {
						// 查询店铺信息
						Map shopMap = new HashMap<String, Object>();
						shopMap.put("sqlMapId", "selectShopInfoById");
						shopMap.put("SHOP_ID", userObj.getString("FK_SHOP"));
						Map<String, Object> shopInfo = (Map<String, Object>) openService.queryForObject(shopMap);
						String isChain = (String) shopInfo.get("SHOP_IS_CHAIN");
						map.put("sqlMapId", "insertShopVipCard");
						if ("1".equals(isChain)) {// 是连锁
							// 根据用户查询管理的店铺
							Map manageShopMap = new HashMap<String, Object>();
							manageShopMap.put("sqlMapId", "selectManagerShopIdAndNameByUserId");

							manageShopMap.put("USER_ID", userObj.get("USER_PK"));
							// 商铺的名字和id
							List<Map<String, Object>> manageShopList = openService.queryForList(manageShopMap);
							// 添加商铺会员卡对应关系表
							for (Map manageShop : manageShopList) {
								map.put("SHOP_ID", manageShop.get("value"));
								map.put("VIP_CARD_ID", insert);
								openService.insert(map);
							}
						} else {// 不是连锁
							map.put("SHOP_ID", userObj.getString("FK_SHOP"));
							map.put("VIP_CARD_ID", insert);
							openService.insert(map);
						}

						// //添加商铺会员卡对应关系表
						// String shopsStr = (String)map.get("USE_SHOP");
						// String[] shops = shopsStr.split(",");
						// map.put("sqlMapId", "insertShopVipCard");
						// for(int i=0;i<shops.length;i++){
						// map.put("SHOP_ID", shops[i]);
						// map.put("VIP_CARD_ID", insert);
						// openService.insert(map);
						// }
						output("0000", "添加成功");
						return;
					} else {
						output("9999", "添加失败");
						return;
					}
				}
			} else {// 是更新
				/* 往微信上传店铺LOGO */
				String logoWxUrl = "";
				if (VCARD_LOGO_STR.indexOf("data:image/") != -1) {
					String imgLOGOStr = VCARD_LOGO_STR;
					String[] imgLogoArray = imgLOGOStr.split(",");
					String logoReStr = CommonUtil.uploadImg(imgLogoArray[1],
							String.valueOf(VCARD_LOGO_STR.length() * 3 / 4), imgLogoArray[0], accessToken);
					JSONObject obj = JSON.parseObject(logoReStr);
					logoWxUrl = obj.get("url").toString();
				}
				// 往微信上传背景图片
				String bgImgUrl = "";
				if (BACKGROUND_IMAGE_STR.indexOf("data:image/") != -1) {
					String[] bgImgArray = BACKGROUND_IMAGE_STR.split(",");
					String bgReStr = CommonUtil.uploadImg(bgImgArray[1],
							String.valueOf(BACKGROUND_IMAGE_STR.length() * 3 / 4), bgImgArray[0], accessToken);
					JSONObject obj = JSON.parseObject(bgReStr);
					bgImgUrl = obj.get("url").toString();
				}
				// 组装post数据
				JSONObject postJsonObj = new JSONObject();
				JSONObject memberCardJsonObj = new JSONObject();
				JSONObject baseInfoJsonObj = new JSONObject();
				JSONObject dateJsonObj = new JSONObject();
				JSONObject bonusRuleJsonObj = new JSONObject();
				// 获取会员卡number
				map.put("sqlMapId", "selectVipCardById");
				List<Map<String, Object>> singleVipCard = openService.queryForList(map);
				String vipNumber = singleVipCard.get(0).get("VCARD_IDNUMBER").toString();
				postJsonObj.put("card_id", vipNumber);
				postJsonObj.put("member_card", memberCardJsonObj);
				memberCardJsonObj.put("base_info", baseInfoJsonObj);
				// 装背景图片路径
				memberCardJsonObj.put("background_pic_url", bgImgUrl);
				// 装logo路径
				baseInfoJsonObj.put("logo_url", logoWxUrl);
				// 装会员卡名称
				baseInfoJsonObj.put("title", map.get("VCARD_NAME"));
				// 装使用说明
				baseInfoJsonObj.put("description", map.get("VCARD_SYXZ"));
				baseInfoJsonObj.put("date_info", dateJsonObj);
				// 若有效期为时间段，则进行修改(仅支持修改时间段)
				if (singleVipCard.get(0).get("ALLOTTED_TIME").toString().equals("2")) {
					dateJsonObj.put("type", 1);
					String[] allowTimeArray = map.get("ALLOTTED_TIME_PERIOD").toString().split(" ");
					dateJsonObj.put("begin_timestamp", Integer.parseInt(DateUtil.date2TimeStamp(allowTimeArray[0])));
					dateJsonObj.put("end_timestamp", Integer.parseInt(DateUtil.date2TimeStamp(allowTimeArray[1])));
				} else {
					dateJsonObj.put("type", 3);
				}

				// 装特权说明
				memberCardJsonObj.put("prerogative", map.get("VCARD_TQSM"));
				// 折扣
				memberCardJsonObj.put("discount",
						((int) Double.parseDouble(map.get("VCARD_ZKXS").toString())) / 10 - 1);
				// 积分规则
				memberCardJsonObj.put("bonus_rule", bonusRuleJsonObj);
				bonusRuleJsonObj.put("cost_money_unit", 10000);
				bonusRuleJsonObj.put("increase_bonus", (int) Double.parseDouble(map.get("VCARD_JFXS").toString()));
				bonusRuleJsonObj.put("init_increase_bonus", (int) Double.parseDouble(map.get("START_JF").toString()));
				/* 创建卡券，往微信发送POST请求 */
				String url = CommonUtil.getPath("WX_UPDATE_CARD");

				url = url.replace("ACCESS_TOKEN", accessToken);

				String result = CommonUtil.WxPOST(url, postJsonObj.toJSONString(), "UTF-8");

				JSONObject createCard = JSON.parseObject(result);

				System.out.println(createCard.containsKey("errcode") + "code" + createCard.getInteger("errcode"));
				if (createCard.containsKey("errcode") && createCard.getInteger("errcode").intValue() == 40100) {
					output("9999", "有效期设置不正确");
					return;
				} else if (createCard.containsKey("errcode") && createCard.getInteger("errcode").intValue() != 0) {
					output("9999", "修改失败");
					return;
				}
				map.put("sqlMapId", "updaeteVipCardById");
				boolean b = openService.update(map);
				if (b) {
					// 先删除原来的店铺-会员卡关系
					map.put("sqlMapId", "removeShopVipCardByVipCardId");
					map.put("VIP_CARD_ID", map.get("VCARD_PK"));
					boolean delete = openService.delete(map);
					if (delete) {
						// 再添加现在的会员卡关系
						map.put("sqlMapId", "insertShopVipCard");
						Map shopMap = new HashMap<String, Object>();
						shopMap.put("sqlMapId", "selectShopInfoById");
						shopMap.put("SHOP_ID", userObj.getString("FK_SHOP"));
						Map<String, Object> shopInfo = (Map<String, Object>) openService.queryForObject(shopMap);
						String isChain = (String) shopInfo.get("SHOP_IS_CHAIN");
						map.put("sqlMapId", "insertShopVipCard");
						if ("1".equals(isChain)) {// 是连锁
							// 根据用户查询管理的店铺
							Map manageShopMap = new HashMap<String, Object>();
							manageShopMap.put("sqlMapId", "selectManagerShopIdAndNameByUserId");

							manageShopMap.put("USER_ID", userObj.get("USER_PK"));
							// 商铺的名字和id
							List<Map<String, Object>> manageShopList = openService.queryForList(manageShopMap);
							// 添加商铺会员卡对应关系表
							for (Map manageShop : manageShopList) {
								map.put("SHOP_ID", manageShop.get("value"));
								openService.insert(map);
							}
						} else {// 不是连锁
							map.put("SHOP_ID", userObj.getString("FK_SHOP"));
							openService.insert(map);
						}

						// String shopsStr = (String)map.get("USE_SHOP");
						// String[] shops = shopsStr.split(",");
						// map.put("sqlMapId", "insertShopVipCard");
						// for(int i=0;i<shops.length;i++){
						// map.put("SHOP_ID", shops[i]);
						// openService.insert(map);
						// }
					} else {
						output("9999", "修改失败");
						return;
					}

					output("0000", "修改成功");
					return;
				} else {
					output("9999", "修改失败");
					return;
				}
			}
		} catch (Exception e) {
			output("9999", e);
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
	public void findVipCardById(HttpServletRequest request, HttpSession session) {
		try {
			Map map = getParameterMap();
			map.put("sqlMapId", "selectVipCardById");

			Map reMap = (Map) openService.queryForObject(map);
			output("0000", reMap);
			return;
		} catch (Exception e) {
			output("9999", e);
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
	public void findVipCardByShopId(HttpServletRequest request, HttpSession session) {
		try {
			Map map = getParameterMap();
			map.put("sqlMapId", "selectVipCardListByShopId");

			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			map.put("SHOP_ID", userObj.get("FK_SHOP"));

			List queryForList = openService.queryForList(map);
			if (queryForList == null) {
				output("9999", "未知错误");
				return;
			}
			output("0000", queryForList);
			return;
		} catch (Exception e) {
			output("9999", e);
			logger.error(e);
		}

	}

	/**
	 * 
	 * @date 2018年9月15日 下午9:23:04
	 * @author lps
	 * 
	 * @Description: 根据会员卡id删除会员卡
	 * @param request
	 * @param session
	 * @return void
	 *
	 */
	@RequestMapping("/VIPCard_delete_removeVIPCardById")
	public void removeVIPCardById(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			logger.info("redis日志:删除会员卡userJson" + userJson);
			JSONObject userObj = JSON.parseObject(userJson);
			/* 根据ID查询会员卡信息 */
			map.put("VCARD_PK", map.get("VCARD_ID"));
			map.put("sqlMapId", "selectVipCardById");
			List<Map<String, Object>> singleVipCard = openService.queryForList(map);
			String vipNumber = singleVipCard.get(0).get("VCARD_IDNUMBER").toString();
			JSONObject cardJsonObj = new JSONObject();
			cardJsonObj.put("card_id", vipNumber);
			String url = CommonUtil.getPath("WX_DELETE_CARD");
			/* 获取accessToken */
			String accessToken = "";
			String appid = userObj.getString("FK_APP");
			if (!jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + appid)) {
				accessToken = WxUtil.getWxAccessToken(appid, interfaceController.getComponentAccessToken(),
						interfaceController.getRefreshTokenByAppId(appid));
				jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + appid, accessToken);
				jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + appid, 3600 * 1);
			} else {
				accessToken = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + appid);
			}
			url = url.replace("ACCESS_TOKEN", accessToken);
			String result = CommonUtil.WxPOST(url, cardJsonObj.toJSONString(), "UTF-8");
			JSONObject deleteCardResult = JSON.parseObject(result);
			if (deleteCardResult.containsKey("errcode") && deleteCardResult.getInteger("errcode").intValue() != 0) {
				output("9999", "删除失败");
				return;
			}
			map.put("SHOP_ID", userObj.get("FK_SHOP"));
			// 先删除会员卡信息
			map.put("sqlMapId", "removeVIPCardById");
			boolean delete = openService.delete(map);
			if (!delete) {
				output("9999", "删除失败");
				return;
			}
			// 再删除店铺-会员卡关系
			map.put("sqlMapId", "removeShopVipCardByVipCardId");
			delete = openService.delete(map);
			if (!delete) {
				output("9999", "删除失败");
				return;
			}

			output("0000", "删除成功");
			return;
		} catch (Exception e) {
			output("9999", e);
			e.printStackTrace();
		}
	}
}
