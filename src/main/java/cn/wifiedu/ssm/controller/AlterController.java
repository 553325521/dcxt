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
 * 教师分组管理
 *
 */
@Controller  
@Scope("prototype")
public class AlterController extends BaseController{  


	private static Logger logger = Logger.getLogger(AlterController.class);

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}
	
    /**
     *添加变更报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Alter_insert_insertAlter")
  	public void insert(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("OLD_STATUS", map.get("STATUS"));
      		map.put("PROJECT_NAME",map.get("PROJECT_TITLE"));
      		map.put("EXPLORE_PK", map.get("SC_EXPLORE_PK"));
      		map.put("FK_EXPLORE", map.get("SC_EXPLORE_PK"));
      		int fzr_change=Integer.parseInt(map.get("FZR_CHANGE").toString());
      		String result="";
      		if(fzr_change==1){//不改变
      			map.put("sqlMapId", "insertAlterNoChange");
      			 result=openService.insert(map);
          		
      		}else{//fzr_change==2改变
      			map.put("sqlMapId", "findSiResearchById");
      			Map<String, Object>  apply = (Map<String, Object>) openService.queryForObject(map);
      			map.put("OLD_PROJECT_NAME", apply.get("PROJECT_TITLE"));
      			map.put("OLD_EXPECT_RESULT", apply.get("EXPECT_RESULT"));
      			map.put("OLD_TEACHER_PK", apply.get("FK_TEACHER"));
      			map.put("OLD_TEACHER_NAME", apply.get("TEACHER_NAME"));
      			map.put("OLD_TEACHER_GENDER", apply.get("TEACHER_GENDER"));
      			map.put("OLD_TEACHER_IDCARD", apply.get("TEACHER_IDCARD"));
      			map.put("OLD_TEACHER_TEL", apply.get("TEACHER_TEL"));
      			map.put("OLD_TEACHER_HOME_TEL", apply.get("TEACHER_HOME_TEL"));
      			map.put("OLD_TEACHER_JOB", apply.get("TEACHER_JOB"));
      			map.put("OLD_TEACHER_JOB_TITLE", apply.get("TEACHER_JOB_TITLE"));	
      			map.put("OLD_TEACHER_SKILL", apply.get("TEACHER_SKILL"));
      			map.put("OLD_SCHOOL_PK", apply.get("FK_TEACHER_SCHOOL"));
      			map.put("OLD_SCHOOL_NAME", apply.get("TEACHER_SCHOOL_NAME"));
      			map.put("OLD_SCHOOL_TEL", apply.get("TEACHER_SCHOOL_TEL"));
      			map.put("OLD_SCHOOL_ADDRESS", apply.get("TEACHER_SCHOOL_ADDRESS"));
      			map.put("OLD_SCHOOL_POSTAL", apply.get("TEACHER_SCHOOL_POSTAL"));
      			map.put("OLD_BASIS_THEORY", apply.get("BASIS_THEORY"));
      			map.put("OLD_PLANS", apply.get("PLANS"));
      			map.put("OLD_CONDITION", apply.get("CONDITION"));
      			map.put("OLD_INFORMATION_REVIEW", apply.get("INFORMATION_REVIEW"));
      			map.put("OLD_DESIGN_ARGUMENT", apply.get("DESIGN_ARGUMENT"));
      			/*System.out.println("map:"+map);*/
      			map.put("sqlMapId", "insertAlterChange");
          		result=openService.insert(map);
          		
      		}
      		//查询变更前的成员
      		map.put("sqlMapId","findMemberListByPk");
      		List<Map<String, Object>> teacherOldList = openService.queryForList(map);
      		
  			JSONArray teacherNewList =  (JSONArray) JSON.parse(map.get("FK_TeacherList").toString());
  			
  			for (int i = 0; i < teacherNewList.size(); i++) {
      			map.put("FK_MEMBER",teacherNewList.getJSONObject(i).getString("MEMBER_PK"));		
      			boolean isExist =false;
      			for (int j = teacherOldList.size()-1; j >=0 ; j--) {
      				if (map.get("FK_MEMBER").equals(teacherOldList.get(j).get("MEMBER_PK"))) {
      				//数据有变更
						isExist = true;
      					break ;
					}
				}
      			if (isExist) {
					
				}else {	
					for(int k=0;k<teacherOldList.size() ;k++){
						map.put("MEMBER_PK",teacherOldList.get(k).get("MEMBER_PK"));
						map.put("ALTER_SORT", 2);
						map.put("FK_EXPLORE_ALTER", result);
						map.put("sqlMapId", "updateAlterMemeber");
						openService.update(map);
						
					}
					for(int j=0;j<teacherNewList.size();j++){
						map.put("FK_EXPLORE_ALTER", result);
						map.put("ALTER_SORT",1);
						map.put("MEMBER_SCHOOL_PK", teacherNewList.getJSONObject(j).getString("SCHOOL_PK"));
	      				map.put("MEMBER_SCHOOL_NAME", teacherNewList.getJSONObject(j).getString("SCHOOL_NAME"));
	      				map.put("MEMBER_USER_PK", teacherNewList.getJSONObject(j).getString("TEACHER_PK"));
	      				map.put("MEMBER_NAME", teacherNewList.getJSONObject(j).getString("TEACHER_NAME"));
	      				map.put("TEACHER_EDUCATION", teacherNewList.getJSONObject(j).getString("TEACHER_EDUCATION"));
	      				map.put("TEACHER_DEGREE", teacherNewList.getJSONObject(j).getString("TEACHER_DEGREE"));
	      				map.put("MEMBER_GENDER", teacherNewList.getJSONObject(j).getString("TEACHER_GENDER"));
	      				map.put("BORN", teacherNewList.getJSONObject(j).getString("BORN"));
	      				map.put("SPECIALTY", teacherNewList.getJSONObject(j).getString("SPECIALTY"));
	      				map.put("JOB_TITLE", teacherNewList.getJSONObject(j).getString("JOB_TITLE"));
	      				map.put("sqlMapId","insertAlterMember");
			      		
			      		openService.insert(map);
			      		
					}	
				}
			}
  			
  		//修改近年研究课题
      		map.put("sqlMapId","findTaskListByPk");
      		List<Map<String, Object>> taskOldList = openService.queryForList(map);
      		JSONArray taskNewList =  (JSONArray) JSON.parse(map.get("FK_TaskList").toString());
      	
      		for (int i = 0; i < taskNewList.size(); i++) {
      			map.put("FK_MEMBER_PROJECT",taskNewList.getJSONObject(i).getString("MEMBER_PROJECT_PK"));		
      			boolean isExist = false;
      			for (int j = taskOldList.size()-1; j >=0 ; j--) {
      				if (map.get("FK_MEMBER_PROJECT").equals(taskOldList.get(j).get("MEMBER_PROJECT_PK"))) {
      				//数据有变更
						isExist =true;
      					break ;
					}
				}
      			if (isExist) {
					
				}else {	
					for(int k=0;k<taskOldList.size() ;k++){
						map.put("MEMBER_PROJECT_PK", taskOldList.get(k).get("MEMBER_PROJECT_PK"));
						map.put("ALTER_SORT", 2);
						map.put("FK_EXPLORE_ALTER", result);
						map.put("sqlMapId", "updateAlterTask");
						openService.update(map);
					}
					
					for(int j=0;j<taskNewList.size();j++){
						map.put("FK_EXPLORE_ALTER", result);
						map.put("ALTER_SORT",1);
						map.put("PROJECT_NAME", taskNewList.getJSONObject(j).getString("PROJECT_NAME"));
	      				map.put("PROJECT_KIND", taskNewList.getJSONObject(j).getString("PROJECT_KIND"));
	      				map.put("START_DATE", taskNewList.getJSONObject(j).getString("START_DATE"));
	      				map.put("COMPLETION", taskNewList.getJSONObject(j).getString("COMPLETION"));
	      				map.put("APPROVE_UNIT", taskNewList.getJSONObject(j).getString("APPROVE_UNIT"));
	      				map.put("sqlMapId", "insertAlterTask");
	      				openService.insert(map);
					}
				}
			}
  			
      	   //修改预期研究成果
      		map.put("sqlMapId","findResultListByPk");
      		List<Map<String, Object>> resultOldList = openService.queryForList(map);
      		JSONArray resultNewList =  (JSONArray) JSON.parse(map.get("FK_ResultList").toString());
      		//判读是否更改了预期研究成果
      		for (int i = 0; i <resultNewList .size(); i++) {
      			map.put("FK_MEMBER_EXPECTING",resultNewList .getJSONObject(i).getString("MEMBER_EXPECTING_PK"));		
      			boolean isExist = false;
      			for (int j = resultOldList.size()-1; j >=0 ; j--) {
      				if (map.get("FK_MEMBER_EXPECTING").equals(resultOldList.get(j).get("MEMBER_EXPECTING_PK"))) {
      				//已经存在
      					isExist =true;
      					break ;
					}
				}
      			System.out.println("isExist:"+isExist);
      			if (isExist) {
					
				}else {	
					for(int k=0;k<resultOldList.size() ;k++){
						map.put("MEMBER_EXPECTING_PK", resultOldList.get(k).get("MEMBER_EXPECTING_PK"));
						map.put("ALTER_SORT", 2);
						map.put("FK_EXPLORE_ALTER", result);
						map.put("sqlMapId", "updateAlterResult");
						openService.update(map);
						
					}
					
					for(int k=0;k<resultNewList.size();k++){
						map.put("FK_EXPLORE_ALTER", result);
						map.put("ALTER_SORT",1);
						map.put("PEOPLE_PK", resultNewList.getJSONObject(k).getString("TEACHER_ID"));
	      				map.put("PEOPLE_NAME", resultNewList.getJSONObject(k).getString("PEOPLE"));
	      				map.put("STAGE_START_TIME", resultNewList.getJSONObject(k).getString("STAGE_START_TIME"));
	      				map.put("STAGE_END_TIME", resultNewList.getJSONObject(k).getString("STAGE_END_TIME"));
	      				map.put("RESULT_FORM", resultNewList.getJSONObject(k).getString("RESULT_FORM"));
	      				map.put("RESULT_STAGE_NAME", resultNewList.getJSONObject(k).getString("RESULT_STAGE_NAME"));
	      				map.put("sqlMapId", "insertAlterResult");
	      				openService.insert(map);
	      				
					}			
				}
			}

  			if(result!=null){
      			map.put("FK_EXPLORE_ALTER",result);
      			map.put("EXPLORE_STATUS", 21);
      			map.put("sqlMapId", "updateExploreStatus");
      			boolean op=
      		      		openService.update(map);
      			System.out.println("修改状态op:"+op);
      		}
  			output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
     
    
    /**
     *修改变更报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Alter_update_updateAlterByPk")
  	public void update(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		System.out.println("SC_EXPLORE_PK:"+map.get("SC_EXPLORE_PK"));
      		map.put("EXPLORE_PK", map.get("SC_EXPLORE_PK"));
      		map.put("FK_EXPLORE", map.get("SC_EXPLORE_PK"));
      		map.put("sqlMapId", "updateAlterByPk");
      		boolean result=openService.update(map);
      		System.out.println("result:"+result);
      		if(result==true){
      			map.put("sqlMapId","findMemberAlterSortByPk");
      			List<Map<String, Object>> teacherOldList = openService.queryForList(map);
      			JSONArray teacherNewList =  (JSONArray) JSON.parse(map.get("FK_TeacherList").toString());
      			System.out.println("teacherOldList:"+teacherOldList);
      			System.out.println("teacherNewList:"+teacherNewList);
      			for (int i = 0; i < teacherNewList.size(); i++) {
          			map.put("FK_MEMBER",teacherNewList.getJSONObject(i).getString("MEMBER_PK"));		
          			boolean isExist = false;
          			for (int j = teacherOldList.size()-1; j >=0 ; j--) {
          				if (map.get("FK_MEMBER").equals(teacherOldList.get(j).get("MEMBER_PK"))) {
          				//已经存在
    						isExist = true;
          					teacherOldList.remove(j);
          					break ;
    					}
    				}
          			if (isExist) {
    					
    				}else {
    					map.put("FK_EXPLORE_ALTER", map.get("EXPLORE_ALTER_PK"));
    					map.put("ALTER_SORT", 1);
    					map.put("MEMBER_SCHOOL_PK", teacherNewList.getJSONObject(i).getString("SCHOOL_PK"));
	      				map.put("MEMBER_SCHOOL_NAME", teacherNewList.getJSONObject(i).getString("SCHOOL_NAME"));
	      				map.put("MEMBER_USER_PK", teacherNewList.getJSONObject(i).getString("TEACHER_PK"));
	      				map.put("MEMBER_NAME", teacherNewList.getJSONObject(i).getString("TEACHER_NAME"));
	      				map.put("TEACHER_EDUCATION", teacherNewList.getJSONObject(i).getString("TEACHER_EDUCATION"));
	      				map.put("TEACHER_DEGREE", teacherNewList.getJSONObject(i).getString("TEACHER_DEGREE"));
	      				map.put("MEMBER_GENDER", teacherNewList.getJSONObject(i).getString("TEACHER_GENDER"));
	      				map.put("BORN", teacherNewList.getJSONObject(i).getString("BORN"));
	      				map.put("SPECIALTY", teacherNewList.getJSONObject(i).getString("SPECIALTY"));
	      				map.put("JOB_TITLE", teacherNewList.getJSONObject(i).getString("JOB_TITLE"));
    					map.put("sqlMapId","insertAlterMember");
    		      		openService.insert(map);
    				}
    			}

          		for (int i = 0; i < teacherOldList.size(); i++) {
          			map.put("MEMBER_PK",teacherOldList.get(i).get("MEMBER_PK"));
          			map.put("sqlMapId","deleteAlterMember");	 
          			openService.delete(map);
          			
    			}
          		
          		//修改近年研究课题
          		map.put("sqlMapId","findTaskAlterSortByPk");
          		List<Map<String, Object>> taskOldList = openService.queryForList(map);
          		JSONArray taskNewList =  (JSONArray) JSON.parse(map.get("FK_TaskList").toString());
          	
          		for (int i = 0; i < taskNewList.size(); i++) {
          			map.put("FK_MEMBER_PROJECT",taskNewList.getJSONObject(i).getString("MEMBER_PROJECT_PK"));		
          			boolean isExist = false;
          			for (int j = taskOldList.size()-1; j >=0 ; j--) {
          				if (map.get("FK_MEMBER_PROJECT").equals(taskOldList.get(j).get("MEMBER_PROJECT_PK"))) {
          				//已经存在
    						isExist = true;
    						taskOldList.remove(j);
          					break ;
    					}
    				}
          			if (isExist) {
    					
    				}else {	
    					map.put("FK_EXPLORE_ALTER", map.get("EXPLORE_ALTER_PK"));
    					map.put("ALTER_SORT", 1);
    					map.put("PROJECT_NAME", taskNewList.getJSONObject(i).getString("PROJECT_NAME"));
	      				map.put("PROJECT_KIND", taskNewList.getJSONObject(i).getString("PROJECT_KIND"));
	      				map.put("START_DATE", taskNewList.getJSONObject(i).getString("START_DATE"));
	      				map.put("COMPLETION", taskNewList.getJSONObject(i).getString("COMPLETION"));
	      				map.put("APPROVE_UNIT", taskNewList.getJSONObject(i).getString("APPROVE_UNIT"));
          				map.put("sqlMapId", "insertAlterTask");
          				openService.insert(map);
    				}
    			}

          		for (int i = 0; i < taskOldList.size(); i++) {
          			map.put("MEMBER_PROJECT_PK",taskOldList.get(i).get("MEMBER_PROJECT_PK"));
          			map.put("sqlMapId","deleteAlterTask");
          			openService.delete(map);
          			
    			}

      		}
      		
      		//修改预期研究成果
      		map.put("sqlMapId","findResultAlterSortByPk");
      		List<Map<String, Object>> resultOldList = openService.queryForList(map);
      		JSONArray resultNewList =  (JSONArray) JSON.parse(map.get("FK_ResultList").toString());
      		for (int i = 0; i <resultNewList .size(); i++) {
      			map.put("FK_MEMBER_EXPECTING",resultNewList .getJSONObject(i).getString("MEMBER_EXPECTING_PK"));		
      			boolean isExist = false;
      			for (int j = resultOldList.size()-1; j >=0 ; j--) {
      				if (map.get("FK_MEMBER_EXPECTING").equals(resultOldList.get(j).get("MEMBER_EXPECTING_PK"))) {
      				//已经存在
						isExist = true;
						resultOldList.remove(j);
      					break ;
					}
				}
      			if (isExist) {
					
				}else {	
					map.put("FK_EXPLORE_ALTER", map.get("EXPLORE_ALTER_PK"));
					map.put("ALTER_SORT", 1);
					map.put("PEOPLE_PK", resultNewList.getJSONObject(i).getString("TEACHER_ID"));
      				map.put("PEOPLE_NAME", resultNewList.getJSONObject(i).getString("PEOPLE"));
      				map.put("STAGE_START_TIME", resultNewList.getJSONObject(i).getString("STAGE_START_TIME"));
      				map.put("STAGE_END_TIME", resultNewList.getJSONObject(i).getString("STAGE_END_TIME"));
      				map.put("RESULT_FORM", resultNewList.getJSONObject(i).getString("RESULT_FORM"));
      				map.put("RESULT_STAGE_NAME", resultNewList.getJSONObject(i).getString("RESULT_STAGE_NAME"));
      				map.put("sqlMapId", "insertAlterResult");
      				openService.insert(map);
      				
				}
			}

      		for (int i = 0; i < resultOldList.size(); i++) {
      			map.put("MEMBER_EXPECTING_PK",resultOldList.get(i).get("MEMBER_EXPECTING_PK"));
      			map.put("sqlMapId","deleteAlterResult");
      			openService.delete(map);
			}
      		
  			output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
    
    /**
     *校级审核变更报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Alter_update_schoolCheckAlter")
  	public void schoolCheck(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		System.out.println("status:"+map.get("STATUS"));
      		map.put("CHECK_LEVEL", map.get("STATUS"));
      		map.put("CHECK_FLAG", map.get("SCHOOL_CHECK_RESULT"));
      		map.put("CHECK_OPINION", map.get("SCHOOL_CHECK_OPINION"));
      		int check_flag=Integer.parseInt(map.get("SCHOOL_CHECK_RESULT").toString());
			if(check_flag==1){
				map.put("EXPLORE_STATUS",23);//审核通过
			}else if(check_flag==2){
				map.put("EXPLORE_STATUS",24);
			}else{
				
			}
			map.put("sqlMapId", "schoolCheckAlter");
			boolean checkResult=openService.update(map);
			if(checkResult==true){
				//往审核表里新增审核记录
				map.put("sqlMapId", "insertCheck");
				String checkInsert=openService.insert(map);
				System.out.println("checkInsert:"+checkInsert);
				if(checkInsert!=null){
					map.put("sqlMapId","updateExploreStatus");
				    openService.update(map);
				}
			}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
    
    /**
     *区级审核终止或延期报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Alter_update_areaCheckAlter")
  	public void areaCheck(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		System.out.println("status:"+map.get("STATUS"));
      		map.put("CHECK_LEVEL", map.get("STATUS"));
      		map.put("CHECK_FLAG", map.get("AREA_CHECK_RESULT"));
      		map.put("CHECK_OPINION", map.get("AREA_CHECK_OPINION"));
			int check_flag=Integer.parseInt(map.get("AREA_CHECK_RESULT").toString());
			if(check_flag==1){	
				map.put("EXPLORE_STATUS", map.get("OLD_STATUS"));
			}else {
				map.put("EXPLORE_STATUS", 24);
			}
			//审核
			map.put("sqlMapId", "areaCheckAlter");
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