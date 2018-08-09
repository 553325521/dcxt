package cn.wifiedu.ssm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.WxUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * @author kqs
 * @time 2018年7月24日 - 下午11:19:03
 * @description:菜单管理模块
 */
@Controller
@Scope("prototype")
public class MenuController extends BaseController {

	private static Logger logger = Logger.getLogger(MenuController.class);

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	@Resource
	PlatformTransactionManager transactionManager;

	@Resource
	private JedisClient jedisClient;

	@Autowired
	private InterfaceController interfaceController;

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年7月24日 - 下午11:30:48
	 * @description:查询所有的一级菜单根据微信appid
	 */
	@RequestMapping("/Menu_queryForList_loadTopMenusByAppId")
	public void loadTopMenus(HttpServletRequest request, HttpSession session) {
		try {
			String usertoken = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + usertoken);
			JSONObject userObj = JSONObject.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("FK_APP", userObj.getString("FK_APP"));
			map.put("sqlMapId", "loadTopMenusByAppId");
			List<Map<String, Object>> reMap = openService.queryForList(map);
			output("0000", reMap);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年7月24日 - 下午11:30:48
	 * @description:查询所有的菜单根据微信appid
	 */
	@RequestMapping("/Menu_queryForList_loadAllMenusByAppId")
	public void loadAllMenusByAppId(HttpServletRequest request, HttpSession session) {
		try {

			String usertoken = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + usertoken);
			JSONObject userObj = JSONObject.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("FK_APP", userObj.getString("FK_APP"));
			map.put("sqlMapId", "loadTopMenusByAppId");
			List<Map<String, Object>> reMap = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> fatherMap = openService.queryForList(map);
			for (Map<String, Object> fMap : fatherMap) {
				reMap.add(fMap);
				String MENU_PK = fMap.get("MENU_PK").toString();
				map.put("sqlMapId", "loadSonMenusByAppId");
				map.put("MENU_FATHER_PK", MENU_PK);
				List<Map<String, Object>> sonMap = openService.queryForList(map);
				if (sonMap != null && !sonMap.isEmpty()) {
					reMap.addAll(sonMap);
				}
			}
			output("0000", reMap);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param @param
	 *            request
	 * @param @param
	 *            session
	 * @return void
	 * @date 2018年7月18日 - 上午11:32:19
	 * @description:新增菜单
	 */
	@RequestMapping("/Menu_insert_addMenu")
	public void insert(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadCountByFMenuId");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			if (reMap != null && reMap.containsKey("nums")) {
				int nums = Integer.valueOf(reMap.get("nums").toString());
				map.put("sqlMapId", "insertMenu");
				map.put("MENU_SORT", String.valueOf(nums + 1));
				map.put("CREATE_BY", "admin");
				map.put("CREATE_TIME", StringDeal.getStringDate());
				String result = openService.insert(map);
				if (result != null) {
					output("0000", "添加成功");
					return;
				}
			}
			output("9999", " 添加失败  ");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	@RequestMapping("/Menu_update_updateWxMenuForTagId")
	public void updateWxMenuForTagId(HttpServletRequest request, HttpSession session) {
		try {
			String usertoken = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + usertoken);
			JSONObject userObj = JSONObject.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			if (map.containsKey("MENU_PLAT")) {
				String tagId = map.get("MENU_PLAT").toString();
				if ("".equals(tagId)) {
					output("9999", " 获取对应微信标签信息失败 ");
					return;
				}
				// 删除个性化套餐
				delMenuById(map);
				// 获取所有的菜单
				map.put("FK_APP", userObj.getString("FK_APP"));
				map.put("sqlMapId", "loadTopMenusByAppId");
				List<Map<String, Object>> fatherMap = openService.queryForList(map);
				if (!fatherMap.isEmpty()) {
					this.insertAppMenu(map, fatherMap, tagId);
					return;
				} else {
					output("9999", " 该菜单为空不可更新! ");
					return;
				}
			}
			output("9999", " 参数异常 ");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * @author kqs
	 * @param map
	 * @param fatherMap
	 * @param tagId
	 * @return void
	 * @date 2018年7月30日 - 上午11:16:57
	 * @description:处理微信菜单逻辑
	 */
	private void insertAppMenu(Map<String, Object> map, List<Map<String, Object>> fatherMap, String tagId) {
		try {
			String usertoken = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + usertoken);
			JSONObject userObj = JSONObject.parseObject(userJson);

			Map<String, Object> postMap = new HashMap<>();

			List<Map<String, Object>> postMap2ToBtn = new ArrayList<>();

			for (Map<String, Object> fMap : fatherMap) {
				Map<String, Object> fmap = new HashMap<>();
				String ftype = "view";
				String MENU_PK = fMap.get("MENU_PK").toString();
				map.put("sqlMapId", "loadSonMenusByAppId");
				map.put("MENU_FATHER_PK", MENU_PK);
				List<Map<String, Object>> sonMap = openService.queryForList(map);
				if (sonMap != null && !sonMap.isEmpty()) {
					ftype = "";
					List<Map<String, Object>> sonMapList = new ArrayList<>();
					for (Map<String, Object> sMap : sonMap) {
						Map<String, Object> smap = new HashMap<>();
						String stype = "view";
						smap.put("type", stype);
						smap.put("url",
								CommonUtil.getPath("project_url").replace("DATA", "Wxcode_ymsqCommon_data") + "?params="
										+ sMap.get("MENU_LINK").toString() + "&appid=" + userObj.getString("FK_APP"));
						smap.put("name", sMap.get("MENU_NAME"));
						sonMapList.add(smap);
					}
					fmap.put("sub_button", sonMapList);
				}
				if (!"".equals(ftype)) {
					fmap.put("type", ftype);
				}
				fmap.put("name", fMap.get("MENU_NAME"));
				if ("view".equals(ftype)) {
					fmap.put("url", CommonUtil.getPath("project_url").replace("DATA", "Wxcode_ymsqCommon_data")
							+ "?params=" + fMap.get("MENU_LINK").toString() + "&appid=" + userObj.getString("FK_APP"));
				}
				postMap2ToBtn.add(fmap);
			}

			Map<String, Object> postMap2ToRule = new HashMap<>();

			String postURL = "";

			if (!"".equals(tagId)) {
				// 设置对应微信用户标签id
				postMap2ToRule.put("tag_id", tagId);
				// 设置中文
				postMap2ToRule.put("language", "zh_CN");

				postMap.put("matchrule", postMap2ToRule);

				postURL = CommonUtil.getPath("addconditionalURL").toString();
			} else {
				postURL = CommonUtil.getPath("menuAddURL").toString();
			}

			postMap.put("button", postMap2ToBtn);

			String postStr = JSON.toJSONString(postMap);
			
			String token = "";
			
			if (!jedisClient.isExit(RedisConstants.WX_ACCESS_TOKEN + userObj.getString("FK_APP"))) {
				token = WxUtil.getWxAccessToken(userObj.getString("FK_APP"),
						interfaceController.getComponentAccessToken(), getRefreshTokenByAppId(userObj.getString("FK_APP")));
				jedisClient.set(RedisConstants.WX_ACCESS_TOKEN + userObj.getString("FK_APP"), token);
				jedisClient.expire(RedisConstants.WX_ACCESS_TOKEN + userObj.getString("FK_APP"), 1000 * 60 * 60 * 1);
			} else {
				token = jedisClient.get(RedisConstants.WX_ACCESS_TOKEN + userObj.getString("FK_APP"));
			}
			
			postURL = postURL.replace("ACCESS_TOKEN", token);

			String resContent = CommonUtil.posts(postURL, postStr, "utf-8");

			if (resContent.indexOf("errcode") <= 0) {
				JSONObject resObj = JSON.parseObject(resContent);
				map.put("CREATE_BY", "admin");
				map.put("CREATE_TIME", StringDeal.getStringDate());
				map.put("FK_MENU_WX", resObj.get("menuid"));
				map.put("sqlMapId", "insertMenuApp");
				openService.insert(map);
				output("0000", " 同步成功! ");
				return;
			} else {
				output("9999", " 菜单同步失败! ");
				return;
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * @author kqs
	 * @param string
	 * @return
	 * @return String
	 * @date 2018年8月8日 - 上午11:43:43
	 * @description:根据appid 获取 refresh_token
	 */
	public String getRefreshTokenByAppId(String appid) {
		try {
			Map<String, Object> obj = (Map<String, Object>) openService.queryForObject(new HashMap<String, Object>() {
				{
					put("APP_PK", appid);
					put("sqlMapId", "getRefreshTokenByAppId");
				}
			});

			return obj.get("APP_REFRESH_TOKEN").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @author kqs
	 * @param map
	 * @return
	 * @return int
	 * @date 2018年7月31日 - 上午9:55:51
	 * @description:删除个性化菜单
	 */
	public int delMenuById(Map<String, Object> map) {

		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus status = transactionManager.getTransaction(definition);

		try {
			map.put("sqlMapId", "findMenuIdByAppId");
			Map<String, Object> obj = (Map<String, Object>) openService.queryForObject(map);
			if (obj != null && obj.containsKey("FK_MENU_WX")) {
				String postStr = "{\"menuid\":\"" + obj.get("FK_MENU_WX") + "\"}";
				map.put("sqlMapId", "deleteMenuApp");
				if (openService.delete(map)) {
					String url = CommonUtil.getPath("deleteconditionalURL").toString();
					String token = WxUtil.getToken();
					url = url.replace("ACCESS_TOKEN", token);
					String res = CommonUtil.posts(url, postStr, "utf-8");
					if (res != null) {
						JSONObject resObj = JSON.parseObject(res);
						if ("0".equals(resObj.get("errcode"))) {
							transactionManager.commit(status);
							return 1;
						}
					}
					throw new RuntimeException();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
		}
		return 0;
	}

	@RequestMapping("/Menu_insert_test")
	public void test(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();

			// String url = CommonUtil.getPath("menuWxGetList").toString();
			// String token = WxUtil.getToken();
			// url = url.replace("ACCESS_TOKEN", token);
			// String res = CommonUtil.get(url);
			String url = CommonUtil.getPath("deleteconditionalURL").toString();
			String token = WxUtil.getToken();
			url = url.replace("ACCESS_TOKEN", token);
			String res = CommonUtil.posts(url, "{\"menuid\":\"430283769\"}", "utf-8");
			output("0000", res);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

}
