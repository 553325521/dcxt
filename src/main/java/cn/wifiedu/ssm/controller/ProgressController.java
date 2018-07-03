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
import cn.wifiedu.core.util.PropertiesUtil;
  
/**
 * 
 * @author wm
 * @version 
 * 教师分组管理
 *
 */
@Controller  
@Scope("prototype")
public class ProgressController extends BaseController{  


	private static Logger logger = Logger.getLogger(ProgressController.class);

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}
	
      
  
	/**
     *添加中期报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Progress_insert_insertProgress")
  	public void insert(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();		
      		map.put("sqlMapId", "insertProgress");
      		String result=openService.insert(map);
      		if(result!=null){			
      			//添加中期报告
      			JSONArray achieveList =  (JSONArray) JSON.parse(map.get("FK_AchieveList").toString());
  				if(achieveList!=null){
  					for (int i = 0; i < achieveList.size(); i++) {
          				map.put("STAGE_NAME", achieveList.getJSONObject(i).getString("STAGE_NAME"));
          				map.put("STAGE_TYPE", achieveList.getJSONObject(i).getString("STAGE_TYPE"));
          				map.put("STAGE_AUTHER", achieveList.getJSONObject(i).getString("STAGE_AUTHER"));
          				map.put("STAGE_DEP", achieveList.getJSONObject(i).getString("STAGE_DEP"));
          				map.put("sqlMapId", "insertStageResult");
          				openService.insert(map);
          			}
  				}	
      			map.put("FK_EXPLORE_PROGRESS",result);
      			map.put("EXPLORE_STATUS", 10);
      			map.put("sqlMapId", "updateExploreStatus");
      		    openService.update(map);
      			
      		}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
    
    
    /**
     *修改中期报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Progress_update_updateProgress")
  	public void update(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("EXPLORE_PK", map.get("SC_EXPLORE_PK"));
      		map.put("sqlMapId", "updateProgress");
      		boolean result=openService.update(map);
      		if(result==true){			
      			//通过科研主键查询中期报告研究成果
      			map.put("sqlMapId","findStageResultListBypk");
          		List<Map<String, Object>> achieveOldList = openService.queryForList(map);		
      			JSONArray achieveNewList =  (JSONArray) JSON.parse(map.get("FK_AchieveList").toString());	
      			//修改预期研究成果
          		for (int i = 0; i <achieveNewList .size(); i++) {
          			map.put("FK_PROGRESS_STAGE",achieveNewList.getJSONObject(i).getString("PROGRESS_STAGE_PK"));		
          			boolean isExist = false;
          			for (int j = achieveOldList.size()-1; j >=0 ; j--) {
          				if (map.get("FK_PROGRESS_STAGE").equals(achieveOldList.get(j).get("PROGRESS_STAGE_PK"))) {
          				//已经存在
    						isExist = true;
    						achieveOldList.remove(j);
          					break ;
    					}
    				}
          			if (isExist) {
    					
    				}else {	
    					map.put("STAGE_NAME", achieveNewList.getJSONObject(i).getString("STAGE_NAME"));
          				map.put("STAGE_TYPE", achieveNewList.getJSONObject(i).getString("STAGE_TYPE"));
          				map.put("STAGE_AUTHER", achieveNewList.getJSONObject(i).getString("STAGE_AUTHER"));
          				map.put("STAGE_DEP", achieveNewList.getJSONObject(i).getString("STAGE_DEP"));
          				map.put("sqlMapId", "insertStageResult");
          				openService.insert(map);			
    				}
    			}
          		for (int i = 0; i < achieveOldList.size(); i++) {
          			map.put("PROGRESS_STAGE_PK",achieveOldList.get(i).get("PROGRESS_STAGE_PK"));
          			map.put("sqlMapId","deleteStageResult");
          			openService.delete(map);
    			}
          		//中期报告修改后回到等待提交状态
          		map.put("EXPLORE_STATUS", 10);
      			map.put("sqlMapId", "updateExploreStatus");
      			openService.update(map);
      		}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
    
    
      
	
    /**
     *校级审核中期报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Progress_schoolCheck_schoolCheckProgress")
  	public void schoolCheck(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("CHECK_LEVEL", map.get("STATUS"));
      		map.put("CHECK_FLAG", map.get("SCHOOL_CHECK_RESULT"));
      		map.put("CHECK_OPINION", map.get("SCHOOL_CHECK_OPINION"));
			int check_flag=Integer.parseInt(map.get("SCHOOL_CHECK_RESULT").toString());
			if(check_flag==1){
				map.put("EXPLORE_STATUS",12);
			}else if(check_flag==2){
				map.put("EXPLORE_STATUS", 13);
			}else{
				
			}
			map.put("sqlMapId", "schoolCheckProgress");
			boolean checkResult=openService.update(map);
			if(checkResult==true){	
				//往审核表里新增审核记录
				map.put("sqlMapId", "insertCheck");
				String checkInsert=openService.insert(map);
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
     *区级审核中期报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Progress_areaCheck_areaCheckProgress")
  	public void areaCheck(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("CHECK_LEVEL", map.get("STATUS"));
      		map.put("CHECK_FLAG", map.get("AREA_CHECK_RESULT"));
      		map.put("CHECK_OPINION", map.get("AREA_CHECK_OPINION"));
			int check_flag=Integer.parseInt(map.get("AREA_CHECK_RESULT").toString());
			if(check_flag==1){
				map.put("EXPLORE_STATUS", 14);
			}else if(check_flag==2){
				map.put("EXPLORE_STATUS", 13);
			}else{
				
			}
			map.put("sqlMapId", "areaCheckProgress");
			boolean checkResult=openService.update(map);
			if(checkResult==true){	
				//往审核表里新增审核记录
				map.put("sqlMapId", "insertCheck");
				String checkInsert=openService.insert(map);
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