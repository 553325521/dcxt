package cn.wifiedu.ssm.controller;

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
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.WxConstants;
import cn.wifiedu.ssm.util.WxUtil;

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
			if (map.containsKey("USER_TAG_NAME") && !"".equals(map.get("USER_TAG_NAME"))) {
				String token = WxUtil.getToken();
				if (token != null) {
					String url = CommonUtil.getPath("userTagCreate").toString();
					url = url.replace("ACCESS_TOKEN", token);
					Map p = new HashMap();
					Map pp = new HashMap();
					pp.put("name", map.get("USER_TAG_NAME"));
					p.put("tag", pp);
					String resMsg = CommonUtil.posts(url, JSON.toJSONString(p), "utf-8");
					if (resMsg != null) {
						if (resMsg.indexOf("errcode") <= 0) {
							JSONObject obj = JSON.parseObject(resMsg);
							JSONObject object = JSON.parseObject(obj.get("tag").toString());
							map.put("sqlMapId", "loadUserTagCount");
							Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
							if (reMap != null && reMap.containsKey("USER_TAG_COUNT")) {
								int USER_TAG_COUNT = Integer.valueOf(reMap.get("USER_TAG_COUNT").toString());
								if (USER_TAG_COUNT >= 20) {
									output("9999", "用户标签数量最多20个");
									return;
								}
								map.put("USER_TAG_ID", object.get("id").toString());
								map.put("CREATE_BY", "admin");
								map.put("CREATE_TIME", StringDeal.getStringDate());
								map.put("sqlMapId", "insertUserTag");
								String result = openService.insert(map);
								if (result != null) {
									output("0000", "添加成功!");
									return;
								}
							}
							output("9999", "系统异常!");
							return;
						}

						JSONObject obj = JSON.parseObject(resMsg);
						String errcode = obj.get("errcode").toString();
						String errMsg = "";
						if (WxConstants.ERRORCODE_1.equals(errcode)) {
							errMsg = WxConstants.ERRORCODE_1_MSG;
						} else if (WxConstants.ERRORCODE_45157.equals(errcode)) {
							errMsg = WxConstants.ERRORCODE_45157_MSG;
						} else if (WxConstants.ERRORCODE_45158.equals(errcode)) {
							errMsg = WxConstants.ERRORCODE_45158_MSG;
						} else if (WxConstants.ERRORCODE_45056.equals(errcode)) {
							errMsg = WxConstants.ERRORCODE_45056_MSG;
						}
						output("9999", errMsg);
						return;
					}
					output("9999", " 添加标签失败 ");
					return;
				}
				output("9999", " 获取微信token失败 ");
				return;
			}
			output("9999", " 系统异常参数为空 ");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
			return;
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年7月22日 - 上午10:10:39
	 * @description:删除用户标签
	 */
	@RequestMapping("/UserTag_update_updateUserTag")
	public void updateUserTag(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			if (map.containsKey("USER_TAG_ID") && !"".equals(map.get("USER_TAG_ID"))) {
				String token = WxUtil.getToken();
				if (token != null) {
					String url = CommonUtil.getPath("userTagUpdate").toString();
					url = url.replace("ACCESS_TOKEN", token);
					Map p = new HashMap();
					Map pp = new HashMap();
					pp.put("name", map.get("USER_TAG_NAME"));
					pp.put("id", map.get("USER_TAG_ID"));
					p.put("tag", pp);
					System.out.println(JSON.toJSONString(p));
					String resMsg = CommonUtil.posts(url, JSON.toJSONString(p), "utf-8");
					if (resMsg != null) {
						JSONObject obj = JSON.parseObject(resMsg);
						String errcode = obj.get("errcode").toString();
						String errMsg = "";
						if (WxConstants.ERRORCODE_1.equals(errcode)) {
							errMsg = WxConstants.ERRORCODE_1_MSG;
						} else if (WxConstants.ERRORCODE_45058.equals(errcode)) {
							errMsg = WxConstants.ERRORCODE_45058_MSG;
						} else if (WxConstants.ERRORCODE_45157.equals(errcode)) {
							errMsg = WxConstants.ERRORCODE_45157_MSG;
						} else if (WxConstants.ERRORCODE_45158.equals(errcode)) {
							errMsg = WxConstants.ERRORCODE_45158_MSG;
						} else if (WxConstants.ERRORCODE_0.equals(errcode)) {
							map.put("UPDATE_BY", "admin");
							map.put("UPDATE_TIME", StringDeal.getStringDate());
							map.put("sqlMapId", "updateUserTag");
							openService.update(map);
							output("0000", " 操作成功   ");
							return;
						}
						output("9999", errMsg);
						return;
					}
					output("9999", "系统异常");
					return;
				}
				output("9999", " 获取微信token失败 ");
				return;
			}
			output("9999", " 系统异常参数为空 ");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
			return;
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年7月22日 - 上午10:10:39
	 * @description:删除用户标签
	 */
	@RequestMapping("/UserTag_remove_removeUserTag")
	public void removeUserTag(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			if (map.containsKey("USER_TAG_ID") && !"".equals(map.get("USER_TAG_ID"))) {
				String token = WxUtil.getToken();
				if (token != null) {
					String url = CommonUtil.getPath("userTagDelete").toString();
					url = url.replace("ACCESS_TOKEN", token);
					Map p = new HashMap();
					Map pp = new HashMap();
					pp.put("id", map.get("USER_TAG_ID"));
					p.put("tag", pp);
					String resMsg = CommonUtil.posts(url, JSON.toJSONString(p), "utf-8");
					if (resMsg != null) {
						JSONObject obj = JSON.parseObject(resMsg);
						String errcode = obj.get("errcode").toString();
						String errMsg = "";
						if (WxConstants.ERRORCODE_1.equals(errcode)) {
							errMsg = WxConstants.ERRORCODE_1_MSG;
						} else if (WxConstants.ERRORCODE_45058.equals(errcode)) {
							errMsg = WxConstants.ERRORCODE_45058_MSG;
						} else if (WxConstants.ERRORCODE_45057.equals(errcode)) {
							errMsg = WxConstants.ERRORCODE_45057_MSG;
						} else if (WxConstants.ERRORCODE_0.equals(errcode)) {
							map.put("sqlMapId", "removeUserTag");
							openService.delete(map);
							errMsg = WxConstants.ERRORCODE_0_MSG;
							output("0000", errMsg);
							return;
						}
						output("9999", errMsg);
						return;
					}
					output("9999", "系统异常");
					return;
				}
				output("9999", " 获取微信token失败 ");
				return;
			}
			output("9999", " 系统异常参数为空 ");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
			return;
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年7月24日 - 下午10:37:23
	 * @description:查询平台类型根据AppId
	 */
	@RequestMapping("/UserTag_queryForList_findPlatformTypeByAppId")
	public void findPlatformTypeList(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			// 一会根据用户缓存拿
			String appId = "wx6041a1eff32d3c5e";
			map.put("FK_APP", appId);
			map.put("sqlMapId", "findPlatformTypeByAppId");
			List<Map<String, Object>> reMap = openService.queryForList(map);
			output("0000", reMap);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * @author kqs
	 * @param appid
	 * @param token
	 * @return void
	 * @date 2018年8月9日 - 下午2:45:06
	 * @description:创建app对应的店员端标签 限定标签名称必须为店员端
	 */
	public String createTagForAppId(String appid, String token) {
		try {
			logger.info("appid:" + appid + ",token:" + token + ",create userTag");
			String url = CommonUtil.getPath("userTagCreate").toString();
			url = url.replace("ACCESS_TOKEN", token);
			Map p = new HashMap();
			Map pp = new HashMap();
			pp.put("name", "店员端");
			p.put("tag", pp);
			String resMsg = CommonUtil.posts(url, JSON.toJSONString(p), "utf-8");
			if (resMsg != null) {
				if (resMsg.indexOf("errcode") <= 0) {
					JSONObject obj = JSON.parseObject(resMsg);
					JSONObject object = JSON.parseObject(obj.get("tag").toString());
					Map<String, Object> map = new HashMap<>();
					map.put("FK_APP", appid);
					map.put("USER_TAG_ID", object.get("id").toString());
					map.put("CREATE_TIME", StringDeal.getStringDate());
					map.put("sqlMapId", "insertUserTag");
					String result = openService.insert(map);
					if (result != null) {
						logger.info("appid:" + appid + ",token:" + token + ", create userTag success");
						return object.get("id").toString();
					}
					logger.error("appid:" + appid + ",token:" + token + ",create userTag error, insert to DB error");
					return null;
				}
				JSONObject obj = JSON.parseObject(resMsg);
				String errcode = obj.get("errcode").toString();
				String errMsg = "";
				if (WxConstants.ERRORCODE_1.equals(errcode)) {
					errMsg = WxConstants.ERRORCODE_1_MSG;
				} else if (WxConstants.ERRORCODE_45157.equals(errcode)) {
					errMsg = WxConstants.ERRORCODE_45157_MSG;
				} else if (WxConstants.ERRORCODE_45158.equals(errcode)) {
					errMsg = WxConstants.ERRORCODE_45158_MSG;
				} else if (WxConstants.ERRORCODE_45056.equals(errcode)) {
					errMsg = WxConstants.ERRORCODE_45056_MSG;
				}
				logger.error("appid:" + appid + ",token:" + token + ",create userTag error, errormsg:" + errMsg);
				return null;
			}
			logger.error("appid:" + appid + ",token:" + token + ",create userTag error");
			return null;
		} catch (Exception e) {
			logger.error("appid:" + appid + ",token:" + token + ",create userTag error", e);
			return null;
		}
	}

}
