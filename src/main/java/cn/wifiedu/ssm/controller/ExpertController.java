package cn.wifiedu.ssm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Insert;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;











import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.util.PropertiesUtil;
  
/**
 * 
 * @author wm
 * @version 
 * 教育科研
 *
 */
@Controller  
@Scope("prototype")
public class ExpertController extends BaseController{  


	private static Logger logger = Logger.getLogger(ExpertController.class);

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}
	
      
    /**
     *专家评审申请书  
     * @param request 
     * @param session 
     */
    @RequestMapping("/Expert_expertCheckApply_expertCheckApply")
  	public void expertCheckApply(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("EXPERT_LEVEL", 1);
      		map.put("sqlMapId", "expertCheckApply");
      		openService.update(map);    		
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
    /**
     *专家评审结题报告 
     * @param request 
     * @param session 
     */
    @RequestMapping("/Expert_expertCheckAchieve_expertCheckAchieve")
  	public void expertCheckAchieve(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("EXPERT_LEVEL", 2);
      		map.put("sqlMapId", "expertCheckAchieve");
      		openService.update(map);    		
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
    
    
} 