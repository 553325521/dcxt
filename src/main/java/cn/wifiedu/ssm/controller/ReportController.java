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
 * 
 *
 */
@Controller  
@Scope("prototype")
public class ReportController extends BaseController{  


	private static Logger logger = Logger.getLogger(ReportController.class);

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}
	
    /**
     *添加开题报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Report_insert_insertReport")
  	public void insert(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("sqlMapId", "insertReport");
      		String result=openService.insert(map);
      		if(result!=null){
      			map.put("FK_EXPLORE_REPORT",result);
      			map.put("EXPLORE_STATUS", 6);
      			map.put("sqlMapId", "updateExploreStatus");
      			boolean op=
      		    openService.update(map);
      			System.out.println("修改结果op:"+op);
      		}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
     
    /**
     *修改开题报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Report_update_updateReport")
  	public void update(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("sqlMapId", "updateReport");
      		boolean result=openService.update(map);
      		if(result==true){
      			//修改开题报告后，回到等待提交的状态
      			map.put("EXPLORE_STATUS", 6);
      			map.put("sqlMapId", "updateExploreStatus");
      			openService.update(map);
      		}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
   
    
    /**
     *校级审核开题报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Report_schoolCheck_schoolCheckReport")
  	public void schoolCheck(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		System.out.println("status:"+map.get("STATUS"));
      		map.put("CHECK_LEVEL", map.get("STATUS"));
      		map.put("CHECK_FLAG", map.get("SCHOOL_CHECK_RESULT"));
      		map.put("CHECK_OPINION", map.get("SCHOOL_CHECK_OPINION"));
			int check_flag=Integer.parseInt(map.get("SCHOOL_CHECK_RESULT").toString());
			if(check_flag==1){
				map.put("EXPLORE_STATUS", 8);
			}else if(check_flag==2){
				map.put("EXPLORE_STATUS", 9);
			}else{
				
			}
			map.put("sqlMapId", "schoolCheckReport");
			boolean checkResult=openService.update(map);
			if(checkResult==true){	
				//往审核表里新增审核记录
				map.put("sqlMapId", "insertCheck");
				String checkInsert=openService.insert(map);
				System.out.println("checkInsert:"+checkInsert);
				if(checkInsert!=null){
					map.put("sqlMapId", "updateExploreStatus");
					openService.update(map);
				} 
			   
			}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
    
    /**
     *区级审核开题报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Report_areaCheck_areaCheckReport")
  	public void areaCheck(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		System.out.println("status:"+map.get("STATUS"));
      		map.put("CHECK_LEVEL", map.get("STATUS"));
      		map.put("CHECK_FLAG", map.get("AREA_CHECK_RESULT"));
      		map.put("CHECK_OPINION", map.get("AREA_CHECK_OPINION"));
			int check_flag=Integer.parseInt(map.get("AREA_CHECK_RESULT").toString());
			if(check_flag==1){
				map.put("EXPLORE_STATUS", 10);
			}else if(check_flag==2){
				map.put("EXPLORE_STATUS", 9);
			}else{
				
			}
			map.put("sqlMapId", "areaCheckReport");
			boolean checkResult=openService.update(map);
			if(checkResult==true){		
				map.put("sqlMapId", "insertCheck");
				String checkInsert=openService.insert(map);
				System.out.println("checkInsert:"+checkInsert);
				if(checkInsert!=null){
					map.put("sqlMapId", "updateExploreStatus");
					openService.update(map);
				} 
			   
			}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
 
} 