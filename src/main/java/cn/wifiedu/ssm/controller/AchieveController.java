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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;

/**
 * 
 * @author wm
 * @version
 *
 */
@Controller
@Scope("prototype")
public class AchieveController extends BaseController {

	private static Logger logger = Logger.getLogger(AchieveController.class);

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	/**
	 * @param request
	 * @param session
	 */
	@RequestMapping("/Achieve_insert_insertAchieve")
	public void insert(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "insertAchieve");
			String result = openService.insert(map);
			if (result != null) {
				JSONArray fileList = (JSONArray) JSON.parse(map.get("FK_FileList").toString());
				if (fileList != null) {
					for (int i = 0; i < fileList.size(); i++) {
						map.put("DOCUMENT_SEQUENCE", fileList.getJSONObject(i).getString("DOCUMENT_SEQUENCE"));
						map.put("DOCUMENT_NAME", fileList.getJSONObject(i).getString("DOCUMENT_NAME"));
						map.put("DOCUMENT_NUMBER", fileList.getJSONObject(i).getString("DOCUMENT_NUMBER"));
						map.put("sqlMapId", "insertAchieveFile");
						openService.insert(map);

					}
				}
				JSONArray userList = (JSONArray) JSON.parse(map.get("FK_UserList").toString());
				if (userList != null) {
					for (int i = 0; i < userList.size(); i++) {
						map.put("TEACHER_WORK", userList.getJSONObject(i).getString("TEACHER_WORK"));
						map.put("MEMBER_PK", userList.getJSONObject(i).getString("MEMBER_PK"));
						map.put("sqlMapId", "updateMemeberInfo");
						openService.update(map);
					}
				}
				map.put("FK_PROJECT_ACHIEVE", result);
				map.put("EXPLORE_STATUS", 14);
				map.put("sqlMapId", "updateExploreStatus");
				openService.update(map);

			}
			output("0000", map);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * @param request
	 * @param session
	 */
	@RequestMapping("/Achieve_update_updateAchieve")
	public void update(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("EXPLORE_PK", map.get("SC_EXPLORE_PK"));
			map.put("sqlMapId", "updateAchieve");
			boolean result = openService.update(map);
			if (result == true) {
				// 淇敼鏂囦欢
				map.put("sqlMapId", "findFileListBypk");
				List<Map<String, Object>> fileOldList = openService.queryForList(map);
				JSONArray fileNewList = (JSONArray) JSON.parse(map.get("FK_FileList").toString());
				for (int i = 0; i < fileNewList.size(); i++) {
					map.put("FK_DOCUMENT", fileNewList.getJSONObject(i).getString("DOCUMENT_PK"));
					boolean isExist = false;
					for (int j = fileOldList.size() - 1; j >= 0; j--) {
						if (map.get("FK_DOCUMENT").equals(fileOldList.get(j).get("DOCUMENT_PK"))) {
							// 宸茬粡瀛樺湪
							isExist = true;
							fileOldList.remove(j);
							break;
						}
					}
					if (isExist) {

					} else {
						map.put("DOCUMENT_SEQUENCE", fileNewList.getJSONObject(i).getString("DOCUMENT_SEQUENCE"));
						map.put("DOCUMENT_NAME", fileNewList.getJSONObject(i).getString("DOCUMENT_NAME"));
						map.put("DOCUMENT_NUMBER", fileNewList.getJSONObject(i).getString("DOCUMENT_NUMBER"));
						map.put("sqlMapId", "insertAchieveFile");
						openService.insert(map);
					}
				}
				for (int i = 0; i < fileOldList.size(); i++) {
					map.put("DOCUMENT_PK", fileOldList.get(i).get("DOCUMENT_PK"));
					map.put("sqlMapId", "deleteFile");
					openService.delete(map);

				}
				JSONArray userList = (JSONArray) JSON.parse(map.get("FK_UserList").toString());
				if (userList != null) {
					for (int i = 0; i < userList.size(); i++) {
						map.put("TEACHER_WORK", userList.getJSONObject(i).getString("TEACHER_WORK"));
						map.put("MEMBER_PK", userList.getJSONObject(i).getString("MEMBER_PK"));
						map.put("sqlMapId", "updateMemeberInfo");
						openService.update(map);
					}
				}
				map.put("EXPLORE_STATUS", 14);
				map.put("sqlMapId", "updateExploreStatus");
				openService.update(map);
			}
			output("0000", map);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * @param request
	 * @param session
	 */
	@RequestMapping("/Achieve_schoolCheck_schoolCheckAchieve")
	public void schoolCheck(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			System.out.println("status:" + map.get("STATUS"));
			map.put("CHECK_LEVEL", map.get("STATUS"));
			map.put("CHECK_FLAG", map.get("SCHOOL_CHECK_RESULT"));
			map.put("CHECK_OPINION", map.get("SCHOOL_CHECK_OPINION"));
			int check_flag = Integer.parseInt(map.get("SCHOOL_CHECK_RESULT").toString());
			if (check_flag == 1) {
				map.put("EXPLORE_STATUS", 16);
			} else if (check_flag == 2) {
				map.put("EXPLORE_STATUS", 19);
			} else {

			}
			map.put("sqlMapId", "schoolCheckAchieve");
			boolean checkResult = openService.update(map);
			if (checkResult == true) {
				map.put("sqlMapId", "insertCheck");
				String checkInsert = openService.insert(map);
				System.out.println("checkInsert:" + checkInsert);
				if (checkInsert != null) {
					map.put("sqlMapId", "updateExploreStatus");
					openService.update(map);
				}
			}
			output("0000", map);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * @param request
	 * @param session
	 */
	@RequestMapping("/Achieve_areaFristCheck_areaCheckAchieve")
	public void areaFristCheck(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			System.out.println("status:" + map.get("STATUS"));
			map.put("CHECK_LEVEL", map.get("STATUS"));
			map.put("CHECK_FLAG", map.get("AREA_CHECK_RESULT"));
			map.put("CHECK_OPINION", map.get("AREA_CHECK_OPINION"));
			int check_flag = Integer.parseInt(map.get("AREA_CHECK_RESULT").toString());
			int is_flag = Integer.parseInt(map.get("IS_FLAG").toString());
			if (check_flag == 1) {
				if (is_flag == 1) {
					map.put("EXPLORE_STATUS", 17);
				} else {
					map.put("EXPLORE_STATUS", 18);
				}
			} else if (check_flag == 2) {
				map.put("EXPLORE_STATUS", 19);
			} else {

			}
			map.put("sqlMapId", "areaCheckAchieve");
			boolean checkResult = openService.update(map);
			if (checkResult == true) {
				JSONArray userList = (JSONArray) JSON.parse(map.get("FK_userList").toString());
				if (userList != null) {
					for (int i = 0; i < userList.size(); i++) {
						String USER_PK = userList.getJSONObject(i).getString("USER_PK");
						map.put("FK_EXPERT_TEACHER", USER_PK);
						map.put("USER_PK", USER_PK);
						map.put("sqlMapId", "findUserInfo");
						Map<String, Object> userMap = (Map<String, Object>) openService.queryForObject(map);
						map.put("EXPERT_TEACHER_NAME", userMap.get("USER_SN").toString());
						map.put("EXPERT_LEVEL", 2);
						map.put("sqlMapId", "insertExploreExpert");
						openService.insert(map);
					}
				}
				map.put("sqlMapId", "insertCheck");
				String checkInsert = openService.insert(map);
				System.out.println("checkInsert:" + checkInsert);
				if (checkInsert != null) {
					map.put("sqlMapId", "updateExploreStatus");
					openService.update(map);
				}

			}
			output("0000", map);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * @param request
	 * @param session
	 */
	@RequestMapping("/Achieve_areaLastCheck_areaLastCheckAchieve")
	public void areaLastCheck(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("CHECK_LEVEL", map.get("STATUS"));
			map.put("CHECK_FLAG", map.get("CHECK_RESULT"));
			map.put("CHECK_OPINION", map.get("CHECK_OPINION"));
			int check_flag = Integer.parseInt(map.get("CHECK_RESULT").toString());
			if (check_flag == 1) {
				map.put("EXPLORE_STATUS", 20);
			} else if (check_flag == 2) {
				map.put("EXPLORE_STATUS", 19);
			} else {

			}
			map.put("sqlMapId", "areaLastCheckAchieve");
			boolean checkResult = openService.update(map);
			if (checkResult == true) {
				map.put("sqlMapId", "insertCheck");
				String checkInsert = openService.insert(map);
				if (checkInsert != null) {
					map.put("sqlMapId", "updateExploreStatus");
					openService.update(map);
				}
				JSONArray expertList = (JSONArray) JSON.parse(map.get("FK_ExpertList").toString());
				if (expertList != null) {
					for (int i = 0; i < expertList.size(); i++) {
						map.put("EXPERT_NAME", expertList.getJSONObject(i).getString("EXPERT_NAME"));
						map.put("EXPERT_ROLE", expertList.getJSONObject(i).getString("EXPERT_ROLE"));
						map.put("EXPERT_UNIT", expertList.getJSONObject(i).getString("EXPERT_UNIT"));
						map.put("EXPERT_JOB", expertList.getJSONObject(i).getString("EXPERT_JOB"));
						map.put("EXPERT_SKILL", expertList.getJSONObject(i).getString("EXPERT_SKILL"));
						map.put("sqlMapId", "insertExpert");
						openService.insert(map);
					}
				}

			}
			output("0000", map);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

}