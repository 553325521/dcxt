package cn.wifiedu.ssm.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.StringDeal;

/**
 * @author kqs
 * @time 2018年7月17日 - 上午10:50:12
 * @description:用户标签设置
 */
@Controller
@Scope("prototype")
public class UserTagController extends BaseController {

	private static Logger logger = Logger.getLogger(UserTagController.class);

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
	 * @param @param
	 *            request
	 * @param @param
	 *            session
	 * @return void
	 * @date 2018年7月19日 - 上午10:49:39
	 * @description:查询所有的用户标签
	 */
	@RequestMapping("/UserTag_queryForList_loadUserTagList")
	public void findTopFunctions(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadUserTagList");
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
	 * @date 2018年7月21日 - 下午3:31:13
	 * @description:插入新的用户标签
	 */
	@RequestMapping("/UserTag_insert_insertUserTag")
	public void insertUserTag(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadUserTagCount");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			if (reMap != null && reMap.containsKey("USER_TAG_COUNT")) {
				int USER_TAG_COUNT = Integer.valueOf(reMap.get("USER_TAG_COUNT").toString());
				if (USER_TAG_COUNT >= 20) {
					output("9999", "用户标签数量最多20个");
					return;
				}
				map.put("CREATE_BY", "admin");
				map.put("CREATE_TIME", StringDeal.getStringDate());
				map.put("sqlMapId", "insertUserTag");
				String result = openService.insert(map);
				output("0000", result);
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}
}
