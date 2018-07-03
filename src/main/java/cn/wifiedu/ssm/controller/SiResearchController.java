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
public class SiResearchController extends BaseController{  


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
     *得到当前时间，还有当前编号
     * @param request 
     * @param session 
     */
    @RequestMapping("/SiResearch_findCourseCode_findCourseCodeByTeacher")
  	public void findCourseCode(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		String applyCode="";
      		//得到当前时间
      		map.put("sqlMapId","findNowYear");
      		Map<String, Object> nowYear = (Map<String, Object>) openService.queryForObject(map);
      		String timeCode=nowYear.get("NOWYEAR").toString();
      		//得到当前条数
			map.put("sqlMapId","findCount");
			Map<String, Object> count = (Map<String, Object>) openService.queryForObject(map);
      		String countCode=count.get("COUNTNUM").toString();
      		//判断条数是否大于100 2013IL000000
      		int num=Integer.parseInt(countCode);
      		if(num>=0 && num<10){
      			applyCode=timeCode+"IL00000"+countCode;		
      		}else if(num>=10 && num<100){
      			applyCode=timeCode+"IL0000"+countCode;
      		}else if(num>=100 && num<1000){
      			applyCode=timeCode+"IL000"+countCode;
      		}else if(num>=1000 && num<10000){
      			applyCode=timeCode+"IL00"+countCode;
      		}else if(num>=10000 && num<1000000){
      			applyCode=timeCode+"IL0"+countCode;
      		}else{
      			applyCode=timeCode+"IL"+countCode;
      		}
      		map.put("APPLY_CODE", applyCode);
      		map.put("sqlMapId","findNowTime");
      		Map<String, Object> nowTime = (Map<String, Object>) openService.queryForObject(map);
      		map.put("APPLY_DATE", nowTime.get("NOWTIME").toString());
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
      
	
    /**
     *添加教育科研申请书
     * @param request 
     * @param session 
     */
    @RequestMapping("/SiResearch_insert_insertSiResearch")
  	public void insert(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("sqlMapId", "insertSiResearch");
      		String result=openService.insert(map); 
      		if(result!=null){
      			map.put("FK_EXPLORE_PETITION", result);
      			map.put("sqlMapId", "insertExplore");	
      			String flag =openService.insert(map);//添加科研主键表中，得到主键新增的主键id
      			if(flag!=null){
      				map.put("FK_EXPLORE", flag);
      				//获取页面所有教师成员，循环添加到科研成员表中
      				JSONArray teacherList =  (JSONArray) JSON.parse(map.get("FK_TeacherList").toString());
      				for (int i = 0; i < teacherList.size(); i++) {
          				map.put("MEMBER_SCHOOL_PK", teacherList.getJSONObject(i).getString("SCHOOL_PK"));
          				map.put("MEMBER_SCHOOL_NAME", teacherList.getJSONObject(i).getString("SCHOOL_NAME"));
          				map.put("MEMBER_USER_PK", teacherList.getJSONObject(i).getString("TEACHER_PK"));
          				map.put("MEMBER_NAME", teacherList.getJSONObject(i).getString("TEACHER_NAME"));
          				map.put("TEACHER_EDUCATION", teacherList.getJSONObject(i).getString("TEACHER_EDUCATION"));
          				map.put("TEACHER_DEGREE", teacherList.getJSONObject(i).getString("TEACHER_DEGREE"));
          				map.put("MEMBER_GENDER", teacherList.getJSONObject(i).getString("TEACHER_GENDER"));
          				map.put("BORN", teacherList.getJSONObject(i).getString("BORN"));
          				map.put("SPECIALTY", teacherList.getJSONObject(i).getString("SPECIALTY"));
          				map.put("JOB_TITLE", teacherList.getJSONObject(i).getString("JOB_TITLE"));
          				map.put("sqlMapId", "insertMember");
          				openService.insert(map);
          			}
      			    //获取页面，循环添加到成员课题研究表中
      				JSONArray taskList =  (JSONArray) JSON.parse(map.get("FK_TaskList").toString());
      				if(taskList!=null){
      					for (int i = 0; i < taskList.size(); i++) {
              				map.put("PROJECT_NAME", taskList.getJSONObject(i).getString("PROJECT_NAME"));
              				map.put("PROJECT_KIND", taskList.getJSONObject(i).getString("PROJECT_KIND"));
              				map.put("START_DATE", taskList.getJSONObject(i).getString("START_DATE"));
              				map.put("COMPLETION", taskList.getJSONObject(i).getString("COMPLETION"));
              				map.put("APPROVE_UNIT", taskList.getJSONObject(i).getString("APPROVE_UNIT"));
              				map.put("sqlMapId", "insertMemberProject");
              				openService.insert(map);
              			}
      				}
      				//获取所有成果，循环添加到研究成果表中
      				JSONArray resultList =  (JSONArray) JSON.parse(map.get("FK_ResultList").toString());
      				if(resultList!=null){
      					for (int i = 0; i < resultList.size(); i++) {
              				map.put("PEOPLE_PK", resultList.getJSONObject(i).getString("TEACHER_ID"));
              				map.put("PEOPLE_NAME", resultList.getJSONObject(i).getString("PEOPLE"));
              				map.put("STAGE_START_TIME", resultList.getJSONObject(i).getString("STAGE_START_TIME"));
              				map.put("STAGE_END_TIME", resultList.getJSONObject(i).getString("STAGE_END_TIME"));
              				map.put("RESULT_FORM", resultList.getJSONObject(i).getString("RESULT_FORM"));
              				map.put("RESULT_STAGE_NAME", resultList.getJSONObject(i).getString("RESULT_STAGE_NAME"));
              				map.put("sqlMapId", "insertMemberExpecting");
              				openService.insert(map);
              			}
      				}	
      				
      			}	
      		}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
    
    
    /**
     *再次申请
     * @param request 
     * @param session 
     */
    @RequestMapping("/SiResearch_reapply_reapplySiResearch")
  	public void reapply(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("sqlMapId", "reapplySiResearch");
      		String result=openService.insert(map); 
      		if(result!=null){
      			map.put("FK_EXPLORE_PETITION", result);
      			map.put("sqlMapId", "insertExplore");	
      			String flag =openService.insert(map);
      			if(flag!=null){
      				map.put("FK_EXPLORE", flag);
      				JSONArray teacherList =  (JSONArray) JSON.parse(map.get("FK_TeacherList").toString());
      				for (int i = 0; i < teacherList.size(); i++) {
          				map.put("MEMBER_SCHOOL_PK", teacherList.getJSONObject(i).getString("SCHOOL_PK"));
          				map.put("MEMBER_SCHOOL_NAME", teacherList.getJSONObject(i).getString("SCHOOL_NAME"));
          				map.put("MEMBER_USER_PK", teacherList.getJSONObject(i).getString("TEACHER_PK"));
          				map.put("MEMBER_NAME", teacherList.getJSONObject(i).getString("TEACHER_NAME"));
          				map.put("TEACHER_EDUCATION", teacherList.getJSONObject(i).getString("TEACHER_EDUCATION"));
          				map.put("TEACHER_DEGREE", teacherList.getJSONObject(i).getString("TEACHER_DEGREE"));
          				map.put("MEMBER_GENDER", teacherList.getJSONObject(i).getString("TEACHER_GENDER"));
          				map.put("BORN", teacherList.getJSONObject(i).getString("BORN"));
          				map.put("SPECIALTY", teacherList.getJSONObject(i).getString("SPECIALTY"));
          				map.put("JOB_TITLE", teacherList.getJSONObject(i).getString("JOB_TITLE"));
          				map.put("sqlMapId", "insertMember");
          				openService.insert(map);
          			}
      				
      				JSONArray taskList =  (JSONArray) JSON.parse(map.get("FK_TaskList").toString());
      				if(taskList!=null){
      					for (int i = 0; i < taskList.size(); i++) {
              				map.put("PROJECT_NAME", taskList.getJSONObject(i).getString("PROJECT_NAME"));
              				map.put("PROJECT_KIND", taskList.getJSONObject(i).getString("PROJECT_KIND"));
              				map.put("START_DATE", taskList.getJSONObject(i).getString("START_DATE"));
              				map.put("COMPLETION", taskList.getJSONObject(i).getString("COMPLETION"));
              				map.put("APPROVE_UNIT", taskList.getJSONObject(i).getString("APPROVE_UNIT"));
              				map.put("sqlMapId", "insertMemberProject");
              				openService.insert(map);
              			}
      				}
      				
      				JSONArray resultList =  (JSONArray) JSON.parse(map.get("FK_ResultList").toString());
      				if(resultList!=null){
      					for (int i = 0; i < resultList.size(); i++) {
              				map.put("PEOPLE_PK", resultList.getJSONObject(i).getString("TEACHER_ID"));
              				map.put("PEOPLE_NAME", resultList.getJSONObject(i).getString("PEOPLE"));
              				map.put("STAGE_START_TIME", resultList.getJSONObject(i).getString("STAGE_START_TIME"));
              				map.put("STAGE_END_TIME", resultList.getJSONObject(i).getString("STAGE_END_TIME"));
              				map.put("RESULT_FORM", resultList.getJSONObject(i).getString("RESULT_FORM"));
              				map.put("RESULT_STAGE_NAME", resultList.getJSONObject(i).getString("RESULT_STAGE_NAME"));
              				map.put("sqlMapId", "insertMemberExpecting");
              				openService.insert(map);
              			}
      				}	
      				
      			}	
      		}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
 
    
 
    /**
     *申请书添加成功后修改教育科研状态
     * @param request 
     * @param session 
     */
    @RequestMapping("/SiResearch_update_updateExploreStatus")
  	public void updateExploreStatus(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("EXPLORE_STATUS", 1);//修改科研项目状态
      		map.put("sqlMapId", "updateExploreStatus");
      		openService.update(map);	
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
 
    
    /**
     *修改教育科研申请书
     * @param request 
     * @param session 
     */
    @RequestMapping("/SiResearch_update_updateSiResearch")
  	public void update(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("FK_EXPLORE", map.get("SC_EXPLORE_PK"));
      		map.put("EXPLORE_PK", map.get("SC_EXPLORE_PK"));
      		map.put("sqlMapId", "updateSiResearch");
      		boolean result=openService.update(map); 
      		if(result==true){
      		//修改科研成员
      			map.put("sqlMapId","findMemberListByPk");
          		List<Map<String, Object>> teacherOldList = openService.queryForList(map);
          	
      			JSONArray teacherNewList =  (JSONArray) JSON.parse(map.get("FK_TeacherList").toString());
      		
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
    					map.put("sqlMapId","insertMember");
    		      		openService.insert(map);
    				}
    			}

          		for (int i = 0; i < teacherOldList.size(); i++) {
          			map.put("MEMBER_PK",teacherOldList.get(i).get("MEMBER_PK"));
          			map.put("sqlMapId","deleteMember");	 
          			openService.delete(map);
          			
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
          				//已经存在
    						isExist = true;
    						taskOldList.remove(j);
          					break ;
    					}
    				}
          			if (isExist) {
    					
    				}else {	
    					map.put("PROJECT_NAME", taskNewList.getJSONObject(i).getString("PROJECT_NAME"));
          				map.put("PROJECT_KIND", taskNewList.getJSONObject(i).getString("PROJECT_KIND"));
          				map.put("START_DATE", taskNewList.getJSONObject(i).getString("START_DATE"));
          				map.put("COMPLETION", taskNewList.getJSONObject(i).getString("COMPLETION"));
          				map.put("APPROVE_UNIT", taskNewList.getJSONObject(i).getString("APPROVE_UNIT"));
          				map.put("sqlMapId", "insertMemberProject");
          				openService.insert(map);
    				}
    			}

          		for (int i = 0; i < taskOldList.size(); i++) {
          			map.put("MEMBER_PROJECT_PK",taskOldList.get(i).get("MEMBER_PROJECT_PK"));
          			map.put("sqlMapId","deleteMemberProject");
          			openService.delete(map);
          			
    			}
          		
          		
          		//修改预期研究成果
          		map.put("sqlMapId","findResultListByPk");
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
    					map.put("PEOPLE_PK", resultNewList.getJSONObject(i).getString("TEACHER_ID"));
          				map.put("PEOPLE_NAME", resultNewList.getJSONObject(i).getString("PEOPLE"));
          				map.put("STAGE_START_TIME", resultNewList.getJSONObject(i).getString("STAGE_START_TIME"));
          				map.put("STAGE_END_TIME", resultNewList.getJSONObject(i).getString("STAGE_END_TIME"));
          				map.put("RESULT_FORM", resultNewList.getJSONObject(i).getString("RESULT_FORM"));
          				map.put("RESULT_STAGE_NAME", resultNewList.getJSONObject(i).getString("RESULT_STAGE_NAME"));
          				map.put("sqlMapId", "insertMemberExpecting");
          				openService.insert(map);
          				
    				}
    			}

          		for (int i = 0; i < resultOldList.size(); i++) {
          			map.put("MEMBER_EXPECTING_PK",resultOldList.get(i).get("MEMBER_EXPECTING_PK"));
          			map.put("sqlMapId","deleteMemberExpecting");
          			openService.delete(map);
    			}
          		//修改教科研申请书后，回到等待提交的状态
          		map.put("EXPLORE_STATUS", 0);
          		map.put("sqlMapId", "updateExploreStatus");
          		openService.update(map);
      		}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
    

    /**
     *校级审核申请书
     * @param request 
     * @param session 
     */
    @RequestMapping("/SiResearch_schoolCheck_schoolCheckApply")
  	public void schoolCheck(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		/*System.out.println("status:"+map.get("STATUS"));*/
      		map.put("CHECK_LEVEL", map.get("STATUS"));
      		map.put("CHECK_FLAG", map.get("SCHOOL_CHECK_RESULT"));
      		map.put("CHECK_OPINION", map.get("SCHOOL_CHECK_OPINION"));
			int check_flag=Integer.parseInt(map.get("SCHOOL_CHECK_RESULT").toString());
			if(check_flag==1){//审核通过
				map.put("EXPLORE_STATUS", 2);
			}else if(check_flag==2){//审核不通过
				map.put("EXPLORE_STATUS", 5);
			}else{
				
			}
			map.put("sqlMapId", "schoolCheckApply");
			boolean checkResult=openService.update(map);
			if(checkResult==true){
				//往审核表里新增审核记录
				map.put("sqlMapId", "insertCheck");
				String checkInsert=openService.insert(map);
				/*System.out.println("checkInsert:"+checkInsert);*/
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
     *区级审核申请书  
     * @param request 
     * @param session 
     */
    @RequestMapping("/SiResearch_areaFristCheck_areaFirstCheckApply")
  	public void areaFristCheck(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();      		
      		map.put("CHECK_LEVEL", map.get("STATUS"));
      		map.put("CHECK_FLAG", map.get("AREA_CHECK_RESULT"));
      		map.put("CHECK_OPINION", map.get("AREA_CHECK_OPINION"));
			int check_flag=Integer.parseInt(map.get("AREA_CHECK_RESULT").toString());
			int is_flag=Integer.parseInt(map.get("IS_FLAG").toString());
			if(check_flag==1){//审核通过
				if(is_flag==1){//是否提交专家评审
					map.put("EXPLORE_STATUS", 3);//1是，提交专家评审
				}else{
					map.put("EXPLORE_STATUS", 4);//2不提交给专家评审
				}
				
			}else if(check_flag==2){//审核不通过
				map.put("EXPLORE_STATUS", 5);
			}else{
				
			}
			map.put("sqlMapId", "areaFirstCheckApply");
			boolean checkResult=openService.update(map);
			if(checkResult==true){
				//往专家评审表添加记录
				JSONArray userList=  (JSONArray) JSON.parse(map.get("FK_userList").toString());
					if(userList!=null){
						for(int i=0;i<userList.size();i++){
							String USER_PK=userList.getJSONObject(i).getString("USER_PK");
							map.put("FK_EXPERT_TEACHER", USER_PK);
							map.put("USER_PK",USER_PK);
							map.put("sqlMapId", "findUserInfo");
							Map<String, Object>  userMap = (Map<String, Object>) openService.queryForObject(map);
							map.put("EXPERT_TEACHER_NAME", userMap.get("USER_SN").toString());
							map.put("EXPERT_LEVEL", 1);//申请书专家审核级别
							map.put("sqlMapId", "insertExploreExpert");
							openService.insert(map);
						}
					}
					//往审核表里新增审核记录
					map.put("sqlMapId", "insertCheck");
					String checkInsert=openService.insert(map);
					if(checkInsert!=null){
						map.put("sqlMapId", "updateExploreStatus");//修改科研状态
						openService.update(map);
					} 
			}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
 
    
    /**
     *区级复审申请书  
     * @param request 
     * @param session 
     */
    @RequestMapping("/SiResearch_areaLastCheck_areaLastCheckApply")
  	public void areaLastCheck(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();		
      		map.put("CHECK_LEVEL", map.get("STATUS"));
      		map.put("CHECK_FLAG", map.get("CHECK_RESULT"));
      		map.put("CHECK_OPINION", map.get("CHECK_OPINION"));
			int check_flag=Integer.parseInt(map.get("CHECK_RESULT").toString());
			
			if(check_flag==1){//审核通过
				map.put("EXPLORE_STATUS", 6);
				
			}else if(check_flag==2){//审核不通过
				map.put("EXPLORE_STATUS", 5);
			}else{
				
			}
			//往审核表里新增审核记录
			map.put("sqlMapId", "insertCheck");
			String checkInsert=openService.insert(map);
			if(checkInsert!=null){
				map.put("sqlMapId", "updateExploreStatus");//修改科研状态
				openService.update(map);
			} 
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
    
} 