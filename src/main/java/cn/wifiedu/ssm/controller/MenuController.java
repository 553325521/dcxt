package cn.wifiedu.ssm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.WxUtil;

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
			Map<String, Object> map = getParameterMap();
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
			Map<String, Object> map = getParameterMap();
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
			Map<String, Object> map = getParameterMap();
			if (map.containsKey("MENU_PLAT")) {
				String token = WxUtil.getToken();
				if (token != null) {
					String url = CommonUtil.getPath("userTagGetList").toString();
					url = url.replace("ACCESS_TOKEN", token);
					String resMsg = CommonUtil.get(url);
					if (resMsg != null) {
						JSONObject obj = JSON.parseObject(resMsg);
						JSONArray tags = JSONObject.parseArray(obj.get("tags").toString());
						String tagId = "";
						for (Object tag : tags) {
							JSONObject tagInfo = JSON.parseObject(tag.toString());
							if (tagInfo != null && (map.get("MENU_PLAT")).equals(tagInfo.get("name"))) {
								tagId = tagInfo.get("id").toString();
								break;
							}
						}
						if (!"用户端".equals(map.get("MENU_PLAT"))) {
							if ("".equals(tagId)) {
								output("9999", " 获取对应微信标签信息失败 ");
								return;
							}
						}
						// 获取所有的菜单
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
					output("9999", " 获取微信标签列表失败 ");
					return;
				}
				output("9999", " 获取微信token失败 ");
				return;
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
								CommonUtil.getPath("project_url").replace("DATA", "Wxcode_ymsqCommon_data") + "?params=" + sMap.get("MENU_LINK").toString());
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
					fmap.put("url",
							CommonUtil.getPath("project_url").replace("DATA", "Wxcode_ymsqCommon_data") + "?params=" + fMap.get("MENU_LINK").toString());
				}
				postMap2ToBtn.add(fmap);
			}

			Map<String, Object> postMap2ToRule = new HashMap<>();

			String postURL = "";

			if (!"用户端".equals(map.get("MENU_PLAT"))) {
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

			String token = WxUtil.getToken();

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
			String res = CommonUtil.posts(url, "{\"menuid\":\"430245209\"}", "utf-8");
			output("0000", res);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}
}
