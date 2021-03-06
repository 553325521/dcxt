package cn.wifiedu.ssm.controller;

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
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * @author kqs
 * @time 2018年7月17日 - 上午10:50:12
 * @description:后台功能列表
 */
@Controller
@Scope("prototype")
public class FunctionController extends BaseController {

	private static Logger logger = Logger.getLogger(FunctionController.class);

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
	 * 
	 * @author kqs
	 * @param @param
	 *            request
	 * @param @param
	 *            session
	 * @return void
	 * @date 2018年7月19日 - 上午10:49:39
	 * @description:查询所有顶级菜单
	 */
	@RequestMapping("/Function_queryForList_findTopFunctions")
	public void findTopFunctions(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "findTopFunctions");
			List<Map<String, Object>> reMap = openService.queryForList(map);
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
	 * @date 2018年7月17日 - 上午10:57:23
	 * @description: 根据用户权限查询对应列表
	 */
	@RequestMapping("/Function_queryForList_loadFunctionListByUserRole")
	public void loadFunctionListByUserRole(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();

			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			// 存储用户对应的shop信息
			String shopJson = jedisClient.get(RedisConstants.REDIS_USER_SHOP_SESSION_KEY + token);
			JSONObject shopObj = JSON.parseObject(shopJson);

			map.put("SHOP_ID", userObj.get("FK_SHOP"));
			map.put("USER_ID", userObj.get("USER_PK"));
			map.put("ROLE_ID", userObj.get("FK_ROLE"));

			map.put("sqlMapId", "loadFunctionListByUserRole");
			List<Map<String, Object>> reList = openService.queryForList(map);

			map.put("functionList", reList);
			map.put("shopName", shopObj.getString("SHOP_NAME"));
			output("0000", map);
			return;
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
	 * @date 2018年8月16日 - 上午1:02:36
	 * @description:根据用户对应的商铺以及传来的权限ID查询对应的权限列表
	 */
	@RequestMapping("/Function_queryForList_findFunctionListByRole")
	public void findFunctionListByRole(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();

			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			map.put("SHOP_ID", userObj.get("FK_SHOP"));

			map.put("sqlMapId", "findFunctionListByRole");
			List<Map<String, Object>> reList = openService.queryForList(map);

			output("0000", reList);
			return;
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
	 * @description:新增功能
	 */
	@RequestMapping("/Function_insert_addFunction")
	public void addFunction(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadCountByPid");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			if (reMap != null) {
				map.put("FUNCTION_SORT", Integer.valueOf(reMap.get("FUNCTION_SORT").toString()) + 1);
				map.put("FUNCTION_ICON", "icon_nav_button");
				map.put("CREATE_BY", "admin");
				map.put("CREATE_TIME", StringDeal.getStringDate());
				map.put("FUNCTION_SWITCH", "on");
				map.put("sqlMapId", "insertFuntion");
				String result = openService.insert(map);
				if (result != null) {
					output("0000", map);
				}
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	@RequestMapping("/Function_queryForList_findAllFunctionURL")
	public void findAllFunctionURL(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "findAllFunctionURL");
			List<Map<String, Object>> reList = openService.queryForList(map);
			output("0000", reList);
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
	 * @date 2018年8月16日 - 下午11:45:31
	 * @description:更新店铺角色对应的权限启用状态
	 */
	@RequestMapping("/Function_update_updateRoleFunStatus")
	public void updateRoleFunStatus(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("IS_USE", (Boolean.valueOf(map.get("checked").toString())) ? "1" : "0");
			map.put("sqlMapId", "updateRoleFunStatus");
			if (openService.update(map)) {
				output("0000", "操作成功!");
				return;
			}
			output("9999", "操作失败!");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	@RequestMapping("/FunctionSwitch_select_loadFuncSwitchList")
	public void loadFuncSwitchList(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			if (StringUtils.isNotBlank(userJson)) {
				JSONObject userObj = JSON.parseObject(userJson);
				map.put("FK_SHOP", userObj.get("FK_SHOP"));
				map.put("sqlMapId", "loadFuncSwitchList");
				Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
				if (reMap != null) {
					output("0000", reMap);
					return;
				}
			}
			output("9999", "查询失败!");
			return;
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
	 * @date 2018年8月16日 - 下午11:45:31
	 * @description:更新店铺角色对应的权限启用状态
	 */
	@RequestMapping("/FunctionSwitch_update_updateFuncSwitch")
	public void updateFuncSwitch(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			if (StringUtils.isNotBlank(userJson)) {
				map.put("sqlMapId", "updateFuncSwitch");
				if (openService.update(map)) {
					output("0000", "操作成功!");
					return;
				}
				output("9999", "操作失败!");
				return;
			}
			output("9999", "非法操作!");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
		return;
	}
	
	/**
	 * 
	 * @author lps
	 * @date Dec 12, 2018 5:21:20 PM 
	 * 
	 * @description: 小程序获取配置开关
	 * @return void
	 */
	@RequestMapping("/FunctionSwitch_select_XCXloadFuncSwitchList")
	public void XCXloadFuncSwitchList(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			String shopid = (String)map.get("FK_SHOP");
			if (StringUtils.isNotBlank(shopid)) {
				map.put("FK_SHOP", shopid);
				map.put("sqlMapId", "loadFuncSwitchList");
				Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
				if (reMap != null) {
					output("0000", reMap);
					return;
				}
			}
			output("9999", "您还没有设置功能开关。");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}
}
