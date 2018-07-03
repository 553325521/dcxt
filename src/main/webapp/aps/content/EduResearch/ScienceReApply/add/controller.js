(function() {
    define(['jqueryUiZh','ZeroClipboard','swfobject','uploadify','uploadauto'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$httpService.css("assets/css/uploadify.css");
            	//初始化 form 表单  
            	$scope.form={};
            	var uploadfiletype ='.pdf,.doc,.ppt';
            	var uploadapp="jky";
            	var UserID= "";
            	var url=config.uploadurl;  	
            	//研究方案
            	UE.delEditor('design_argument');
                var uedesign = UE.getEditor('design_argument');
            	//研究方案(匿名)
            	UE.delEditor('plans');
                var ueplans = UE.getEditor('plans');               
                //文献综述
                UE.delEditor('basis_theory');
                var uebasis= UE.getEditor('basis_theory');   
                //文献综述(匿名)
                UE.delEditor('information_review');
                var ueinformation = UE.getEditor('information_review');    
            	//完成项目可见分析
            	UE.delEditor('condition');
                var uecondition = UE.getEditor('condition');
            	//承担人
            	 $scope.peopleList=new Array();
             	//查询当前老师的相关信息
            	 var findInfo=function(){
            		 $httpService.post(config.findInfoURL,$scope.form).success(function(data) {
                 		$scope.form.USER_PK=data.data.USER_PK;	
                 		UserID =data.data.USER_PK;
                 		$scope.form.USER_NAME =data.data.USER_NAME;
                 		var user={"TEACHER_ID":$scope.form.USER_PK,"TEACHER_NAME":$scope.form.USER_NAME};
                 		$scope.peopleList.push(user);
                 		/*console.log("$scope.peopleList");
                 		console.log($scope.peopleList);  */    		
                 		$scope.form.USER_GENDER=data.data.GENDER; 
                 		$scope.form.USER_UNIT_PK=data.data.UNIT_PK;
                 		$scope.form.USER_SCHOOL_NAME =data.data.SCHOOL_NAME;
                 		$scope.form.USER_SCHOOL_ADDRESS=data.data.SCHOOL_ADDRESS;
                 		$scope.form.USER_DATE_BIRTH=data.data.DATE_BIRTH;
                 		$scope.form.USER_JOB_TITLE=data.data.PROFESS;
                 		$scope.form.USER_POSTCODE=data.data.POSTCODE;
                 		$scope.form.USER_EMAIL=data.data.EMAIL;
                 		$scope.form.USER_MOBILE_PHONE=data.data.MOBILE_PHONE;
                 		$scope.form.USER_HOME_PHONE=data.data.HOME_PHONE;
                 		$scope.form.USER_UNIT_PHONE=data.data.UNIT_PHONE;
                 		$scope.form.USER_ID_CARD=data.data.ID_CARD;
                 		console.log($scope.form);
                 		$scope.$apply();
                 		findCode();
             			schoolTypeList();
     	            });
            	 }
            	
            	//查询当前时间跟编号
            	var findCode=function(){
            		$httpService.post(config.findCodeURL,{}).success(function(data) {
            			$scope.form.APPLY_DATE= data.data.APPLY_DATE;
            			$scope.form.APPLY_CODE=data.data.APPLY_CODE;
            			$scope.$apply();	
    	            });
            	}
            	
            	//查询所有学校类别
            	var schoolTypeList=function(){
            		$httpService.post(config.findSchoolTypeURL).success(function(data) {
       	             	if(data.code != '0000'){
       	             		loggingService.info(data.msg);
       	             	}else{
	       	             	$scope.schoolTypeList = data.data;
		   	             	/*console.log($scope.schoolTypeList);*/
		   	             	$scope.$apply();
		   	             	$scope.schoolChange();
       	             	}
                	}).error(function(data) {
                        loggingService.info('获取初始化数据出错');
                    });

            	}
            	
            	//通过学校类别pk获取所有学校
            	$scope.schoolChange=function(){
            		/*console.log($scope.form.SCHOOL_TYPE_PK);*/
		        	if($scope.form.SCHOOL_TYPE_PK!= '' && $scope.form.SCHOOL_TYPE_PK!=null){
		        		var school_type_pk={"SCHOOL_TYPE_PK":$scope.form.SCHOOL_TYPE_PK};
			        	$httpService.post(config.findSchoolByTypeURL,school_type_pk).success(function(data) {
		                	if(data.code != '0000'){
		                		loggingService.info(data.msg);
		                	}else{
		                		$scope.unitList = data.data;
		   	         		   /* console.log( $scope.unitList);*/
		                		$scope.$apply();
		                		$scope.teacherChange();
		                	}
		
		                 }).error(function(data) {
		                     loggingService.info('获取学校出错');
		                 });
		        	}
            			
            	}
            	
            	//通过学校PK获取所有老师
            	$scope.teacherChange=function(){
            		/*console.log($scope.form.UNIT_PK);*/
            		if($scope.form.UNIT_PK!= ''&&$scope.form.UNIT_PK!=null){
                    	var unit_pk={"UNIT_PK":$scope.form.UNIT_PK};
    		        	$httpService.post(config.findTeacherBySchoolURL,unit_pk).success(function(data) {
    	                	if(data.code != '0000'){
    	                		loggingService.info(data.msg);
    	                	}else{
    	                		$scope.teacherList = data.data;
    	                		$scope.$apply();
    	                		$scope.teacherInfo();
    	                		
    	                	}
    	
    	                 }).error(function(data) {
    	                     loggingService.info('获取老师出错');
    	                 });
            		}
            		
            	}
            	
            	//查询老师的所有信息
            	$scope.teacherInfo=function(){
                    /*console.log($scope.form.TEACHER_PK);*/
            		if($scope.form.TEACHER_PK!= '' &&$scope.form.TEACHER_PK!=null){
            			var teacher_pk={"TEACHER_PK":$scope.form.TEACHER_PK};
    		        	$httpService.post(config.findTeacherInfoURL,teacher_pk).success(function(data) {
    	                	if(data.code != '0000'){
    	                		loggingService.info(data.msg);
    	                	}else{
    	                		 $scope.form.TEACHER_NAME=data.data.USER_NAME;
    	                		 $scope.form.BORN=data.data.DATE_BIRTH;
    	                		 $scope.form.TEACHER_GENDER= data.data.GENDER;
    	                		 $scope.form.TEACHER_EDUCATION= data.data.DEUCATION;
    	                		 $scope.form.TEACHER_DEGREE=data.data.EDU_DEGREE;
    	                		 $scope.form.TEACHER_SCHOOL_NAME=data.data.SCHOOL_NAME;
    	                		 $scope.$apply();
    	                		
    	                	}
    	
    	                 }).error(function(data) {
    	                     loggingService.info('获取老师出错');
    	                 });
            		}
            	}

            	//临时存放添加成员的数据
            	var FK_TeacherList=new Array();
            	var showTeacherList=new Array();
            	$scope.newTeacherList;
            	$scope.addMember=function(){
       
            		if ($scope.form.TEACHER_NAME == null || $scope.form.TEACHER_NAME == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全姓名。"});
            			return;
    				}
            		
            		if ($scope.form.TEACHER_GENDER == null || $scope.form.TEACHER_GENDER == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全性别。"});
            			return;
    				}
            		
            		if ($scope.form.TEACHER_EDUCATION == null || $scope.form.TEACHER_EDUCATION == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全学历。"});
            			return;
    				}
            		
            		if ($scope.form.TEACHER_DEGREE == null || $scope.form.TEACHER_DEGREE == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全学位。"});
            			return;
    				}
            		
            		if ($scope.form.BORN == null || $scope.form.BORN == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全出生年月。"});
            			return;
    				}
            		
            		if ($scope.form.JOB_TITLE == null || $scope.form.JOB_TITLE == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写职务。"});
            			return;
    				}
            		
            		if ($scope.form.SPECIALTY == null || $scope.form.SPECIALTY == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写研究专长。"});
            			return;
    				}
            		
            		if ($scope.form.TEACHER_SCHOOL_NAME == null || $scope.form.TEACHER_SCHOOL_NAME == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全工作单位。"});
            			return;
    				}
            		
            		var mumber={
            			"SCHOOL_PK":$scope.form.UNIT_PK,
            			"SCHOOL_NAME":$scope.form.TEACHER_SCHOOL_NAME,
            			"TEACHER_PK":$scope.form.TEACHER_PK,
            			"TEACHER_NAME":$scope.form.TEACHER_NAME,
            			"TEACHER_EDUCATION":$scope.form.TEACHER_EDUCATION,
            			"TEACHER_DEGREE":$scope.form.TEACHER_DEGREE,
            			"TEACHER_GENDER":$scope.form.TEACHER_GENDER,
            			"BORN":$scope.form.BORN,
            			"JOB_TITLE":$scope.form.JOB_TITLE,
            			"SPECIALTY":$scope.form.SPECIALTY
            		};
            				
            		if(showTeacherList.length==0){
            			FK_TeacherList.push(mumber);
    	        		showTeacherList.push(mumber);
    	        		var user={"TEACHER_ID":$scope.form.TEACHER_PK,"TEACHER_NAME":$scope.form.TEACHER_NAME};
    	        		$scope.peopleList.push(user);
    	        		$scope.form.TEACHER_PK="";
    	        		$scope.form.TEACHER_NAME="";
    	        		$scope.form.UNIT_PK="";
    	        		$scope.form.TEACHER_SCHOOL_NAME="";
    	        		$scope.form.TEACHER_EDUCATION="";
    	        		$scope.form.TEACHER_DEGREE="";
    	        		$scope.form.TEACHER_GENDER="";
    	        		$scope.form.JOB_TITLE="";
    	        		$scope.form.BORN="";
    	        		$scope.form.SPECIALTY="";
            		}else{
            			var flag=false;
            			for(var i=0;i<showTeacherList.length;i++){
        		        	if(mumber.TEACHER_PK===showTeacherList[i].TEACHER_PK){
        		        		flag=true;
        		        		alert("该老师已经存在课题组中！");		
        		        	}else{
        		        		flag=false;
        		        	}      	
        		        } 
            			if(flag==false){
            				FK_TeacherList.push(mumber);
    		        		showTeacherList.push(mumber);
    		        		var user={"TEACHER_ID":$scope.form.TEACHER_PK,"TEACHER_NAME":$scope.form.TEACHER_NAME};
    		        		$scope.peopleList.push(user);
    		        		$scope.form.TEACHER_PK="";
    		        		$scope.form.TEACHER_NAME="";
    		        		$scope.form.UNIT_PK="";
    		        		$scope.form.TEACHER_SCHOOL_NAME="";
    		        		$scope.form.TEACHER_EDUCATION="";
    		        		$scope.form.TEACHER_DEGREE="";
    		        		$scope.form.TEACHER_GENDER="";
    		        		$scope.form.JOB_TITLE="";
    		        		$scope.form.BORN="";
    		        		$scope.form.SPECIALTY="";
            			}
            		}	     		
            		$scope.newTeacherList=showTeacherList;
            	}
        	//移除成员
        	$scope.removeMember=function(teacher){
        		var teacherPk=teacher.TEACHER_PK;
        		for(var i=0;i<showTeacherList.length;i++){
        				var pk=showTeacherList[i].TEACHER_PK;
        				if(teacherPk==pk){
        					showTeacherList.splice($.inArray(showTeacherList[i].TEACHER_PK,showTeacherList),1);
        					console.log(showTeacherList);
        				}
        		}
        		for(var i=0;i<$scope.peopleList.length;i++){
    				var pk=$scope.peopleList[i].TEACHER_ID;
    				if(teacherPk==pk){
    					$scope.peopleList.splice($.inArray($scope.peopleList[i].TEACHER_ID,$scope.peopleList),1);
    					console.log($scope.peopleList);
    			}
    		}
        		
        	}
           
        	//临时存放课题的数据
        	var FK_TaskList=new Array();
        	var showTaskList=new Array();
        	$scope.newTaskList;
        	$scope.addTask=function(){
        		if ($scope.form.PROJECT_NAME == null || $scope.form.PROJECT_NAME == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写课题名称。"});
        			return;
				}
        		
        		if ($scope.form.PROJECT_KIND == null || $scope.form.PROJECT_KIND == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写课题类型。"});
        			return;
				}
        		
        		if ($scope.form.START_DATE == null || $scope.form.START_DATE == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写立项时间。"});
        			return;
				}
        		
        		if ($scope.form.COMPLETION == null || $scope.form.COMPLETION == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写完成情况。"});
        			return;
				}
        		
        		if ($scope.form.APPROVE_UNIT == null || $scope.form.APPROVE_UNIT == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写批准单位。"});
        			return;
				}	
        		var task={
        				"PROJECT_NAME":$scope.form.PROJECT_NAME,
        				"PROJECT_KIND":$scope.form.PROJECT_KIND,
        				"START_DATE":$scope.form.START_DATE,
        				"COMPLETION":$scope.form.COMPLETION,
        				"APPROVE_UNIT":$scope.form.APPROVE_UNIT
        				};
        				FK_TaskList.push(task);
        				showTaskList.push(task);
        				$scope.form.PROJECT_NAME="";
        				$scope.form.PROJECT_KIND="";
        				$scope.form.START_DATE="";
        				$scope.form.COMPLETION="";
        				$scope.form.APPROVE_UNIT="";
        		$scope.newTaskList=showTaskList;
        	}
        	//移除相关课题
        	$scope.removeTask=function(taskVo){
        		$scope.newTaskList.remove(taskVo);
        	}
        	
        	//临时存放研究成果
        	var FK_ResultList=new Array();
        	var showResultList=new Array();
        	$scope.newResultList;
        	$scope.addResult=function(){
        		
        		if ($scope.form.TEACHER_ID == null || $scope.form.TEACHER_ID == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写承担人。"});
        			return;
				}
        		
        		if ($scope.form.STAGE_START_TIME == null || $scope.form.STAGE_START_TIME == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"研究阶段开始时间。"});
        			return;
				}
        		
        		if ($scope.form.STAGE_END_TIME == null || $scope.form.STAGE_END_TIME == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"研究阶段结束时间。"});
        			return;
				}
        		
        		if ($scope.form.RESULT_FORM == null || $scope.form.RESULT_FORM == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写成果形式。"});
        			return;
				}
        		
        		if ($scope.form.RESULT_STAGE_NAME == null || $scope.form.RESULT_STAGE_NAME == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写阶段成果名称。"});
        			return;
				}
        		
        		var id=$scope.form.TEACHER_ID;
        		
        		//承担人
        		for(var i=0;i<$scope.peopleList.length;i++){
        			if(id==$scope.peopleList[i].TEACHER_ID){
        				$scope.form.PEOPLE=$scope.peopleList[i].TEACHER_NAME;
        			}
        		} 		
        		var result={
        				"TEACHER_ID":$scope.form.TEACHER_ID,
        				"PEOPLE":$scope.form.PEOPLE,
        				"STAGE_START_TIME":$scope.form.STAGE_START_TIME,
        				"STAGE_END_TIME":$scope.form.STAGE_END_TIME,
        				"RESULT_FORM":$scope.form.RESULT_FORM,
        				"RESULT_STAGE_NAME":$scope.form.RESULT_STAGE_NAME
        				};
        				FK_ResultList.push(result);
        				showResultList.push(result);
        				$scope.form.TEACHER_ID="";
        				$scope.form.PEOPLE="";
        				$scope.form.STAGE_START_TIME="";
        				$scope.form.STAGE_END_TIME="";
        				$scope.form.RESULT_FORM="";
        				$scope.form.RESULT_STAGE_NAME="";
        		$scope.newResultList=showResultList;
        	}
        	
        	//移除预期研究成果
        	$scope.removeResult=function(resultVo){
        		$scope.newResultList.remove(resultVo);
        	}
        	
        	//保存
    		$scope.save=function(){	
    			var apply_date=$scope.form.APPLY_DATE;
    			var finish_date=$scope.form.FINISH_DATE;
    			if(apply_date==finish_date){
    				eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"申请日期跟完成日期不能是同一天。"});
        			return;
    			}
    			 var arrStart = apply_date.split("-");
    			 var startTime = new Date(arrStart[0], arrStart[1], arrStart[2]);
    			 var startTimes = startTime.getTime();
    			 var arrEnd = finish_date.split("-");
    			 var endTime = new Date(arrEnd[0], arrEnd[1], arrEnd[2]);
    			 var endTimes = endTime.getTime();
    			 if (endTimes<startTimes) {
    				 eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"预计完成时间不能小于填表时间。"});
         			return;		  
    			}
    			 
    			 /*if ($scope.form.USER_JOB_TITLE == null || $scope.form.USER_JOB_TITLE == '' ) {
         			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全职称信息。"});
         			return;
 				}
    			 
    			 if ($scope.form.USER_UNIT_PHONE == null || $scope.form.USER_UNIT_PHONE == '' ) {
          			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全单位号码信息。"});
          			return;
  				}
    			 
    			 if ($scope.form.USER_HOME_PHONE == null || $scope.form.USER_HOME_PHONE == '' ) {
          			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全家庭号码信息。"});
          			return;
  				}
    			
    			 if ($scope.form.USER_MOBILE_PHONE == null || $scope.form.USER_MOBILE_PHONE == '' ) {
           			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全移动电话信息。"});
           			return;
   				}
    			 
    			 if ($scope.form.USER_EMAIL == null || $scope.form.USER_EMAIL == '' ) {
           			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全电子信箱信息。"});
           			return;
   				}
    			 
    			 if ($scope.form.USER_POSTCODE == null || $scope.form.USER_POSTCODE == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全邮政编码信息。"});
            			return;
    			}*/
    			 
    			var length=FK_ResultList.length;
    			if(length<=0){
    				eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"至少要添加一项预计成果。"});
        			return;
    			}
    			$scope.form.FK_ResultList=JSON.stringify(FK_ResultList);
            	$scope.form.FK_TaskList=JSON.stringify(FK_TaskList);
            	$scope.form.FK_TeacherList = JSON.stringify(FK_TeacherList);
            	$scope.form.DESIGN_ARGUMENT=uedesign.getPlainTxt();
            	$scope.form.PLANS=ueplans.getPlainTxt();
                $scope.form.BASIS_THEORY =uebasis.getPlainTxt();
                $scope.form.INFORMATION_REVIEW =ueinformation.getPlainTxt();
            	$scope.form.CONDITION=uecondition.getPlainTxt();
            	$scope.form.SCHOOL_CHECK_RESULT='0';
        		$scope.form.AREA_CHECK_RESULT='0';
        		
    			//校验表单
        		if(!$scope.validateForm()){
        			return;
        		}
        		
        		$httpService.post(config.addURL,$scope.form).success(function(data) {
                	if(data.code != '0000'){
                		loggingService.info(data.msg);
                	}else{
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"添加成功！"});
                		eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});//发送更新事件，刷新数据
                		$scope.goback();
                	}
                 }).error(function(data) {
                     loggingService.info('添加出错！');
                 });

    		}
    		    //初始化表单校验
            	VALIDATE.iniValidate($scope);
         
            	//附件上传（回调函数）
            	var callonComplete = function(event, queueID, fileObj, response, dataObj) {
              	    //转换为json对象
                	console.log(response);
                	var data = eval("("+response+")");
                	if(data.code == "4444"){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"不支持此类型文件的上传!"});
                		return;
                	}
                   if(data.code == "0000"){
                	   $scope.form.RES_FILE_LINK_PK=data.data.RES_FILE_LINK_PK;
                	   $scope.form.FILE_NAME=data.data.ORI_FILENAME;
                	   $scope.form.EXT_NAME=data.data.EXTNAME;
                	   $scope.form.FILE_PATH = data.data.FILE_PATH;
                	   $scope.form.FILE_ID=data.data.FILE_ID;
                	   $('#'+controllerName+' .uploadfile').show();
                       $('#'+controllerName+' .uploadresult').show();
                       $('#'+controllerName+' .uploadfile').html(data.data.ORI_FILENAME+"."+data.data.EXTNAME);
                   }
                    
                };
            	//初始化数据
            	var init = function(){
            		findInfo();
            		UPLOADAUTO.iniUploadauto($('#uploadifyfile'),uploadfiletype,uploadapp,"0",UserID,url,callonComplete);
            	}
            	init();
            	//初始化
            	$scope.form.FINISH_DATE='  ';
            	$scope.form.START_DATE='  ';
            	$scope.form.STAGE_START_TIME='  ';
            	$scope.form.STAGE_END_TIME='  ';
            	//时间控件样式
                $httpService.css("http://cdn.sjedu.cn/js/jqueryUi/css/custom-theme/jquery-ui-1.9.2.custom.css");
            	//设置时间控件
            	$('#'+controllerName+' .datepicker').datepicker(
            			{	onSelect: function(dateText, inst) 
            				{
            					eval("$scope." + $(this).attr('ng-model') + "='"+$(this).val()+"'");
                            }
            			}
            	);
            	$('#'+controllerName+' .datepicker').datepicker('option', 'dateFormat','yy-mm-dd');
            	
            	//返回上一页
            	$scope.goback = function() { 
             		var menu = {
             		    "CONTROLLER_NAME": "ScienceReApply", 
             		    "CREATE_BY": "SJAAAAX44858", 
             		    "CREATE_TIME": 1508169600000, 
             		    "MENU_CODE": "0000710104", 
             		    "MENU_FATHER_PK": "a381053e77244085bdf5ead406cdb1b1", 
             		    "MENU_IMG": "file", 
             		    "MENU_LINK": "aps/content/EduResearch/ScienceReApply/list/config.json", 
             		    "MENU_NAME": "我的科研申请", 
             		    "MENU_PK": "58330ff069634d38829d41b25c9a4362", 
             		    "MENU_STATUS": "0", 
             		    "MENU_TYPE": "0"
             		}
             		var changeControllerData = {
    	                  url:menu.MENU_LINK,
    	                  contentName:"content",
    	                  hasButton:"right",
    	                  data:menu
    	                }
          	        return eventBusService.publish(controllerName,'appPart.load.content', changeControllerData);
        		}
            }
        ];
    });
}).call(this);
