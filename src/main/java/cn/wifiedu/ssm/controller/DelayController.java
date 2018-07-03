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
public class DelayController extends BaseController{  


	private static Logger logger = Logger.getLogger(DelayController.class);

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}
	
    /**
     *添加终止或延期报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Delay_insert_insertDelay")
  	public void insert(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("sqlMapId", "insertDelay");
      		String result=openService.insert(map);
      		if(result!=null){//添加成功后，改变科研申请的状态。
      			map.put("FK_EXPLORE_DELAY",result);
      			map.put("EXPLORE_STATUS", 25);
      			map.put("sqlMapId", "updateExploreStatus");	
      		    openService.update(map);		
      		}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
     
    
    /**
     *修改终止或延期报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Delay_update_updateDelay")
  	public void update(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("sqlMapId", "updateDelay");
      		boolean result=openService.update(map);
      		if(result==true){
      		 //延期或终止报告修改后回到等待提交状态
          		map.put("EXPLORE_STATUS", 25);
      			map.put("sqlMapId", "updateExploreStatus");
      			openService.update(map);	
      		}
      		output("0000",map);
  		} catch (Exception e) {
  			output("9999"," Exception ",e);
  		}
  	}
     
    
    /**
     *校级审核终止或延期报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Delay_schoolCheck_schoolCheckDelay")
  	public void schoolCheck(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("CHECK_LEVEL", map.get("STATUS"));
      		map.put("CHECK_FLAG", map.get("SCHOOL_CHECK_RESULT"));
      		map.put("CHECK_OPINION", map.get("SCHOOL_CHECK_OPINION"));
      		int check_flag=Integer.parseInt(map.get("SCHOOL_CHECK_RESULT").toString());
			if(check_flag==1){//审核通过
				map.put("EXPLORE_STATUS",27);
			}else if(check_flag==2){//审核不通过
				map.put("EXPLORE_STATUS", 28);
			}else{
				
			}
			map.put("sqlMapId", "schoolCheckDelay");
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
     *区级审核终止或延期报告
     * @param request 
     * @param session 
     */
    @RequestMapping("/Delay_areaCheck_areaCheckDelay")
  	public void areaCheck(HttpServletRequest request,HttpSession session){
      	try {
      		Map<String, Object> map=getParameterMap();
      		map.put("CHECK_LEVEL", map.get("STATUS"));
      		map.put("CHECK_FLAG", map.get("AREA_CHECK_RESULT"));
      		map.put("CHECK_OPINION", map.get("AREA_CHECK_OPINION"));
			int check_flag=Integer.parseInt(map.get("AREA_CHECK_RESULT").toString());
			int delay_sort=Integer.parseInt(map.get("DELAY_SORT").toString());
			if(delay_sort==1){
				if(check_flag==1){	
					map.put("EXPLORE_STATUS", 29);
				}else {
					map.put("EXPLORE_STATUS", 28);
				}
			}else{
				if(check_flag==1){
					//修改预计完成时间
					map.put("sqlMapId", "updateFinishDate");
					openService.update(map);		
					map.put("EXPLORE_STATUS",map.get("OLD_STATUS"));
				}else {
					map.put("EXPLORE_STATUS", 28);
				}
			}
			//审核
			map.put("sqlMapId", "areaCheckDelay");
			boolean checkResult=openService.update(map);
			if(checkResult==true){
				//往审核表里新增审核记录
				map.put("sqlMapId", "insertCheck");
				String checkInsert=openService.insert(map);
				//审核成功后，修改科研申请项目状态
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