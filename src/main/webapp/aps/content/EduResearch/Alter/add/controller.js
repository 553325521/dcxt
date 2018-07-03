(function() {
    define(['jqueryUiZh','ZeroClipboard','swfobject','uploadify','uploadauto'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	var uploadfiletype ='.pdf,.docx,.ppt';
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
            	/*初始化数据*/
            	 var findInfo=function(){
            		 $httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
             			$scope.form=data.data;
                 		 uedesign.ready( function() {
                          	uedesign.setContent(data.data.DESIGN_ARGUMENT);
                          } );
                 		 ueplans.ready( function() {
                 			 ueplans.setContent(data.data.PLANS);
                           } );
                 		 uebasis.ready( function() {
                 			 uebasis.setContent(data.data.BASIS_THEORY);
                           } );
                 		 ueinformation.ready( function() {
                 			 ueinformation.setContent(data.data.INFORMATION_REVIEW);
                           } );
                 		 uecondition.ready( function() {
                 			 uecondition.setContent(data.data.CONDITION);
                           } );
            			console.log(data.data);
                 		$scope.form=data.data;
                 		var user={"TEACHER_ID":data.data.FK_TEACHER,"TEACHER_NAME":$scope.form.TEACHER_NAME};
                 		$scope.peopleList.push(user);
                 		$scope.$apply();
             			schoolTypeList();
             			findMemberList();
             			findTaskList();
             			findResultList();	
     	            });
            	 }
            	
            	//查找已经存在成员的数据
            	$scope.newTeacherList;
            	var FK_TeacherList=new Array();
            	var showTeacherList=new Array();
            	var findMemberList=function(){
            		$httpService.post(config.findMemberListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			for(var i=0;i<data.data.length;i++){
            				var mumber={
            						"MEMBER_PK":data.data[i].MEMBER_PK,
            						"SCHOOL_PK":data.data[i].FK_TEACHER_UNIT,
                    				"SCHOOL_NAME":data.data[i].UNIT_NAME,
                    				"TEACHER_PK":data.data[i].FK_TEACHER,
                    				"TEACHER_NAME":data.data[i].TEACHER_NAME,
                    				"TEACHER_EDUCATION":data.data[i].TEACHER_EDUCATION,
                    				"TEACHER_DEGREE":data.data[i].TEACHER_DEGREE,
                    				"TEACHER_GENDER":data.data[i].GENDER,
                    				"BORN":data.data[i].BORN,
                    				"JOB_TITLE":data.data[i].JOB_TITLE,
                    				"SPECIALTY":data.data[i].SPECIALTY
                    				};
            				showTeacherList.push(mumber);
            				var user={"TEACHER_ID":data.data[i].FK_TEACHER,"TEACHER_NAME":data.data[i].TEACHER_NAME};
            				$scope.peopleList.push(user);
            			}
            			$scope.newTeacherList=showTeacherList;
            			FK_TeacherList=showTeacherList;
                		$scope.$apply();	
    	            });
            		
            	}

            	//查找已有课题的数据
            	var FK_TaskList=new Array();
            	var showTaskList=new Array();
            	$scope.newTaskList;
            	var findTaskList=function(){
            		$httpService.post(config.findTaskListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			for(var i=0;i<data.data.length;i++){
            				var index=showTaskList.length;
            				var task={
            						"INDEX":index,
            						"MEMBER_PROJECT_PK":data.data[i].MEMBER_PROJECT_PK,
            						"PROJECT_NAME":data.data[i].PROJECT_NAME,
            						"PROJECT_KIND":data.data[i].PROJECT_TYPE,
                    				"START_DATE":data.data[i].APPLY_DATE,
                    				"COMPLETION":data.data[i].COMPLETION,
                    				"APPROVE_UNIT":data.data[i].APPROVE_UNIT
                    				};
            				showTaskList.push(task); 	
            			}
            			$scope.newTaskList=showTaskList;
            			FK_TaskList=showTaskList;
                		$scope.$apply();	
    	            });
            	}
            	
            	//查找已有的研究成果
            	var FK_ResultList=new Array();
            	var showResultList=new Array();
            	$scope.newResultList;
            	var findResultList=function(){	
            		$httpService.post(config.findResultListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			for(var i=0;i<data.data.length;i++){	
            				var index=showResultList.length;
            				var result={
            						"INDEX":index,
            						"MEMBER_EXPECTING_PK":data.data[i].MEMBER_EXPECTING_PK,
                    				"TEACHER_ID":data.data[i].TEACHER_ID,
                    				"PEOPLE":data.data[i].TEACHER_NAME,
                    				"STAGE_START_TIME":data.data[i].STAGE_START_TIME,
                    				"STAGE_END_TIME":data.data[i].STAGE_END_TIME,
                    				"RESULT_FORM":data.data[i].RESULT_FORM,
                    				"RESULT_STAGE_NAME":data.data[i].RESULT_STAGE_NAME
                    				};
            				showResultList.push(result); 	
            			}
            			$scope.newResultList=showResultList;
            			FK_ResultList=showResultList;
                		$scope.$apply();	
    	            });
            	}
            	
            	
            	//选择是否变更负责人
                $scope.fzrChange=function(option){
                	if(option=='2'){
                		$scope.form.TEACHER_NAME="";
                		$scope.form.TEACHER_GENDER="";
                		$scope.form.TEACHER_JOB_TITLE="";
                		$scope.form.TEACHER_JOB="";
                		$scope.form.TEACHER_SKILL="";
                		$scope.form.TEACHER_IDCARD="";
                		$scope.form.TEACHER_SCHOOL_TEL="";
                		$scope.form.TEACHER_HOME_TEL="";
                		$scope.form.TEACHER_TEL="";
                		$scope.form.TEACHER_EMAIL="";
                		$scope.form.TEACHER_SCHOOL_NAME="";
                		$scope.form.TEACHER_SCHOOL_POSTAL="";
                		$scope.form.TEACHER_SCHOOL_ADDRESS="";
                		$("#"+controllerName+" #schoolInfo").show();
                		$("#"+controllerName+" #userPk").show();
                		$("#"+controllerName+" #name").hide();
                	}else{
                		$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
                    		$scope.form=data.data;
                    		var user={"TEACHER_ID":data.data.FK_TEACHER,"TEACHER_NAME":$scope.form.TEACHER_NAME};
                    		for(var i=0;i<$scope.peopleList.length;i++){
               					$scope.peopleList.splice(0,1,user);
               					console.log($scope.peopleList);   
    	                 	 }
                    		$scope.$apply();
        	            });	
                		$("#"+controllerName+" #schoolInfo").hide();
                		$("#"+controllerName+" #name").show();
                		$("#"+controllerName+" #userPk").hide();
                		
                	}
                	
                }

            	//查询所有学校类别
            	var schoolTypeList=function(){
            		$httpService.post(config.findSchoolTypeURL).success(function(data) {
       	             	if(data.code != '0000'){
       	             		loggingService.info(data.msg);
       	             	}else{
	       	             	$scope.schoolTypeList = data.data;
		   	             	console.log($scope.schoolTypeList);
		   	             	$scope.$apply();
		   	             	$scope.schoolChange();
		   	             	$scope.typeChange();
       	             	}
                	}).error(function(data) {
                        loggingService.info('获取初始化数据出错');
                    });

            	}
            	
            	//通过学校类别pk获取所有学校
            	$scope.typeChange=function(){
            		if($scope.form.TYPE_PK!= '' && $scope.form.TYPE_PK!=null){
		        		var school_type_pk={"SCHOOL_TYPE_PK":$scope.form.TYPE_PK};
			        	$httpService.post(config.findSchoolByTypeURL,school_type_pk).success(function(data) {
		                	if(data.code != '0000'){
		                		loggingService.info(data.msg);
		                	}else{
		                		$scope.schoolList = data.data;
		   	         		    console.log( $scope.schoolList);
		                		$scope.$apply();
		                		$scope.unitChange();
		                	}
		
		                 }).error(function(data) {
		                     loggingService.info('获取学校出错');
		                 });
		        	}
            	}
            	
            	
            	//通过学校PK获取所有老师
            	$scope.unitChange=function(){
            		console.log($scope.form.SCHOOL_PK);
            		if($scope.form.SCHOOL_PK!= ''&&$scope.form.SCHOOL_PK!=null){
                    	var unit_pk={"UNIT_PK":$scope.form.SCHOOL_PK};
    		        	$httpService.post(config.findTeacherBySchoolURL,unit_pk).success(function(data) {
    	                	if(data.code != '0000'){
    	                		loggingService.info(data.msg);
    	                	}else{
    	                		$scope.userList = data.data;
    	                		$scope.$apply();
    	                		$scope.fzrInfo();		
    	                	}
    	
    	                 }).error(function(data) {
    	                     loggingService.info('获取老师出错');
    	                 });
            		}
            		
            	}
            	
            	//查询负责人详细信息
            	$scope.fzrInfo=function(){
                    console.log($scope.form.USER_PK);
            		if($scope.form.USER_PK!= '' &&$scope.form.USER_PK!=null){
            			var teacher_pk={"TEACHER_PK":$scope.form.USER_PK};
    		        	$httpService.post(config.findTeacherInfoURL,teacher_pk).success(function(data) {
    	                	if(data.code != '0000'){
    	                		loggingService.info(data.msg);
    	                	}else{
    	                		 $scope.form.TEACHER_NAME=data.data.USER_NAME;
    	                		 var user={"TEACHER_ID":data.data.USER_PK,"TEACHER_NAME":data.data.USER_NAME};
    	                 		 for(var i=0;i<$scope.peopleList.length;i++){
                					$scope.peopleList.splice(0,1,user);
                					console.log($scope.peopleList);   
    	                 		 }
    	                 		 $scope.form.TEACHER_NAME=data.data.USER_NAME;
    	                		 $scope.form.TEACHER_GENDER= data.data.GENDER;
    	                		 $scope.form.TEACHER_IDCARD=data.data.ID_CARD;    	      
    	                		 $scope.form.TEACHER_TEL=data.data.MOBILE_PHONE;
    	                		 $scope.form.TEACHER_HOME_TEL=data.data.HOME_PHONE;
    	                		 $scope.form.TEACHER_SCHOOL_TEL=data.data.UNIT_PHONE;
    	                		 $scope.form.TEACHER_EMAIL=data.data.EMAIL;
    	                		 $scope.form.TEACHER_SCHOOL_POSTAL=data.data.POSTCODE;
    	                		 $scope.form.TEACHER_JOB_TITLE=data.data.PROFESS;
    	                		 $scope.form.TEACHER_SCHOOL_NAME=data.data.SCHOOL_NAME;
    	                		 $scope.form.TEACHER_SCHOOL_ADDRESS=data.data.SCHOOL_ADDRESS;	 
    	                		 $scope.$apply();
    	                	}
    	
    	                 }).error(function(data) {
    	                     loggingService.info('获取老师出错');
    	                 });
            		}
            	}        	
            	
            	//通过学校类别pk获取所有学校
            	$scope.schoolChange=function(){
		        	if($scope.form.SCHOOL_TYPE_PK!= '' && $scope.form.SCHOOL_TYPE_PK!=null){
		        		var school_type_pk={"SCHOOL_TYPE_PK":$scope.form.SCHOOL_TYPE_PK};
			        	$httpService.post(config.findSchoolByTypeURL,school_type_pk).success(function(data) {
		                	if(data.code != '0000'){
		                		loggingService.info(data.msg);
		                	}else{
		                		$scope.unitList = data.data;
		   	         		    console.log( $scope.unitList);
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
            		console.log($scope.form.UNIT_PK);
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
            	
            	//查询教师详细信息
            	$scope.teacherInfo=function(){
                    console.log($scope.form.TEACHER_PK);
            		if($scope.form.TEACHER_PK!= '' &&$scope.form.TEACHER_PK!=null){
            			var teacher_pk={"TEACHER_PK":$scope.form.TEACHER_PK};
    		        	$httpService.post(config.findTeacherInfoURL,teacher_pk).success(function(data) {
    	                	if(data.code != '0000'){
    	                		loggingService.info(data.msg);
    	                	}else{
    	                		 $scope.form.NAME=data.data.USER_NAME;
    	                		 $scope.form.BORN=data.data.DATE_BIRTH;
    	                		 $scope.form.GENDER= data.data.GENDER;
    	                		 $scope.form.TEACHER_EDUCATION= data.data.DEUCATION;
    	                		 $scope.form.TEACHER_DEGREE=data.data.EDU_DEGREE;
    	                		 $scope.form.SCHOOL_NAME=data.data.SCHOOL_NAME;
    	                		 console.log(data.data.DATE_BIRTH);
    	                		 $scope.$apply();
    	                		
    	                	}
    	
    	                 }).error(function(data) {
    	                     loggingService.info('获取老师出错');
    	                 });
            		}
            	}
            
        	//添加科研成员
        	$scope.addMember=function(){
        		var pk="01";
        		var mumber={
        				"MEMBER_PK":pk,
        				"SCHOOL_PK":$scope.form.UNIT_PK,
        				"SCHOOL_NAME":$scope.form.SCHOOL_NAME,
        				"TEACHER_PK":$scope.form.TEACHER_PK,
        				"TEACHER_NAME":$scope.form.NAME,
        				"TEACHER_EDUCATION":$scope.form.TEACHER_EDUCATION,
        				"TEACHER_DEGREE":$scope.form.TEACHER_DEGREE,
        				"TEACHER_GENDER":$scope.form.GENDER,
        				"BORN":$scope.form.BORN,
        				"JOB_TITLE":$scope.form.JOB_TITLE,
        				"SPECIALTY":$scope.form.SPECIALTY
        				};
        				showTeacherList.push(mumber);
        				var user={"TEACHER_ID":$scope.form.TEACHER_PK,"TEACHER_NAME":$scope.form.NAME};
        				$scope.peopleList.push(user);
		        		  		
        		$scope.newTeacherList=showTeacherList;
        		FK_TeacherList=showTeacherList;
        	}
        	//移除成员
        	$scope.removeMember=function(teacher){
        		var teacherPk=teacher.TEACHER_PK;
        		for(var i=0;i<showTeacherList.length;i++){
        				var pk=showTeacherList[i].TEACHER_PK;
        				if(teacherPk==pk){
        					showTeacherList.splice($.inArray(showTeacherList[i].TEACHER_PK,showTeacherList),1);
        					console.log(showTeacherList);
        					FK_TeacherList=showTeacherList;
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
           
        	//添加课题
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
        		var index=showTaskList.length;
        		var pk="02";
        		var task={
        				"INDEX":index,
						"MEMBER_PROJECT_PK":pk,
        				"PROJECT_NAME":$scope.form.PROJECT_NAME,
        				"PROJECT_KIND":$scope.form.PROJECT_KIND,
        				"START_DATE":$scope.form.START_DATE,
        				"COMPLETION":$scope.form.COMPLETION,
        				"APPROVE_UNIT":$scope.form.APPROVE_UNIT
        				};
        				showTaskList.push(task);       		
        		$scope.newTaskList=showTaskList;
        		FK_TaskList=showTaskList;
        	}
        	
        	//移除相关课题
        	$scope.removeTask=function(index){
        		for(var i=0;i<showTaskList.length;i++){
        			var sub=showTaskList[i].INDEX
        			if(sub==index){
        				showTaskList.splice($.inArray(showTaskList[i].INDEX,showTaskList),1);
        				FK_TaskList=showTaskList;
        				console.log(showTaskList);
        			}	
        		}
        	}
        	
        
        	//预期研究成果
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
        		var index=showResultList.length;
        		var id=$scope.form.TEACHER_ID
        		for(var i=0;i<$scope.peopleList.length;i++){
        			if(id==$scope.peopleList[i].TEACHER_ID){
        				$scope.form.PEOPLE=$scope.peopleList[i].TEACHER_NAME;
        			}
        		} 	
        		var pk="03";
        		var result={
        				"INDEX":index,
        				"MEMBER_EXPECTING_PK":pk,
        				"TEACHER_ID":$scope.form.TEACHER_ID,
        				"PEOPLE":$scope.form.PEOPLE,
        				"STAGE_START_TIME":$scope.form.STAGE_START_TIME,
        				"STAGE_END_TIME":$scope.form.STAGE_END_TIME,
        				"RESULT_FORM":$scope.form.RESULT_FORM,
        				"RESULT_STAGE_NAME":$scope.form.RESULT_STAGE_NAME
        				};
        				showResultList.push(result);   		
        		$scope.newResultList=showResultList;
        		FK_ResultList=showResultList;
        	}
        	//移除预期研究成果
        	$scope.removeResult=function(index){
        		for(var i=0;i<showResultList.length;i++){
        			var sub=showResultList[i].INDEX	
        			if(index==sub){
        				showResultList.splice($.inArray(showResultList[i].INDEX,showResultList),1);
        				FK_ResultList=showResultList;
        				console.log(showResultList);
        			}	
        		}
        	}
        	
    		//保存变更报告
    		$scope.save=function(){
    			$scope.form.FZR_CHANGE=$('#'+controllerName+' input[type="radio"]:checked').val();
    			$scope.form.STATUS=params.status;
    			var values = [];
    			$('#'+controllerName+' input[name="reason"]:checked').each(function(){ 
            		values.push($(this).val());
            	});
    			var lists=[];
    			$('#'+controllerName+' input[name="content"]:checked').each(function(){ 
    				lists.push($(this).val());
            	});
    			var resson="";
    			for(var i=0;i<values.length;i++){
    				if (i == values.length - 1) {
    					resson += values[i];
    					} else {
    						resson += values[i];
    						resson += ",";
    				}
    			}
    			var content="";
    			for(var i=0;i<lists.length;i++){
    				if (i == lists.length - 1) {
    					content += lists[i];
    					} else {
    						content += lists[i];
    						content += ",";
    					}
    			}
    			$scope.form.REASON=resson;
    			$scope.form.ALTER_CONTENT=content;		
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
    			//校验表单
        		if(!$scope.validateForm()){
        			return;
        		}
        		
        		$httpService.post(config.addURL,$scope.form).success(function(data) {
                	if(data.code != '0000'){
                		loggingService.info(data.msg);
                	}else{
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"变更成功！"});
                		eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});//发送更新事件，刷新数据
                		$scope.goback();
                	}
                 }).error(function(data) {
                     loggingService.info('保存出错！');
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
