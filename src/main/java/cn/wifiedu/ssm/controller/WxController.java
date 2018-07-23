package cn.wifiedu.ssm.controller;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.WxUtil;

/**
 * 微信与数据库交互相关
 * @author JH_L
 *
 */
@Controller  
@Scope("prototype")
public class WxController extends BaseController {

	private static Logger logger = Logger.getLogger(SiResearchController.class);

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}
	
	/**
	 * 获取微信推送到服务器事件
	 */
	@RequestMapping("/portal")
	public void getUserInfo(HttpServletRequest request, HttpServletResponse reponse ) {
		try {
			Map<String, Object> map = getParameterMap();
			logger.info(map+"");
			
			PrintWriter out = reponse.getWriter();
			
			if(map.containsKey("openid")) {
				map.put("OPENID", map.get("openid").toString());
				map.put("sqlMapId", "checkUserExits");
				List<Map<String, Object>> checkUserList = openService.queryForList(map);
				if(checkUserList.size() == 0) {
					map.put("sqlMapId", "insertUserInitOpenId");
					openService.insert(map);
				}
			}
			
			out.println("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 保存token到数据库
	 * @param token
	 * @return
	 */
	@RequestMapping("/Wx_saveToken_data")
	public void saveToken(String token) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "saveAccessToken");
			openService.insert(map);
			
			output(WxUtil.getToken());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			output("9999","error");
		}
	}
	
	/**
	 * 获取最新的access_token
	 */
	@RequestMapping("/Wx_getToken_data")
	public void getToken() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "getAccessToken");
			Map<String, Object> wxMap = (Map<String, Object>) openService.queryForObject(map);
			output(wxMap.get("ACCESS_TOKEN").toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			output("9999","error");
		}
	}
}
