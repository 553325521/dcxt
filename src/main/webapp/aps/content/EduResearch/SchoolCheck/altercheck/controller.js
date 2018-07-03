(function() {
    define(['jqueryUiZh'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	
            	/*初始化数据*/
            	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            		console.log(data.data);
            		var resson=data.data.REASON;
            		var ressonList=new Array();
            		var ressonList= resson.split(",");
            		for(var i=0;i<ressonList.length;i++){
        				var reason=ressonList[i];
        				console.log(reason);
        				if(reason=='1'){
        					$("#"+controllerName+" #reason1").prop("checked",true);
        				}else if(reason=='2'){
        					$("#"+controllerName+" #reason2").prop("checked",true);
        				}else{
        					$("#"+controllerName+" #reason3").prop("checked",true);
        				}
        			}
            	
            		var content=data.data.ALTER_CONTENT;
            		var contentList=new Array();
            		var contentList= content.split(",");
            		for(var i=0;i<contentList.length;i++){
            			var content=contentList[i];
            			if(content=='1'){
        					$("#"+controllerName+" #content1").prop("checked",true);
        				}else if(content=='2'){
        					$("#"+controllerName+" #content2").prop("checked",true);
        				}else if(content=='3'){
        					$("#"+controllerName+" #content3").prop("checked",true);
        				}else if(content=='4'){
        					$("#"+controllerName+" #content4").prop("checked",true);
        				}else if(content=='5'){
        					$("#"+controllerName+" #content5").prop("checked",true);
        				}else if(content=='6'){
        					$("#"+controllerName+" #content6").prop("checked",true);
        				}else{
        					$("#"+controllerName+" #content7").prop("checked",true);
        				}
            		}	
            		$scope.form=data.data;
            		$scope.$apply();
        			findMemberList();
        			findTaskList();
        			findResultList();
        			findCheckInfo();
	            });
            	
            	$scope.newTeacherList;
            	$scope.alterTeacherList
            	//查询已有成员数据
            	var FK_TeacherList=new Array();
            	var showTeacherList=new Array();
            	
            	var findMemberList=function(){
            		$httpService.post(config.findMemberAlterSortURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			var flag=true;//是没有更改
            			for(var i=0;i<data.data.length;i++){
            				var alter_sort=data.data[i].ALTER_SORT;
            				if(alter_sort!=undefined||alter_sort!=null){				
            					flag=false;
            					break;
                			}else{
                				flag=true;
                			}
            			}
            			console.log("flag");
            			console.log(flag);
                		if(flag==true){
                			findNoChangeMember();
                		}else{
                			
                			findChangeMember();
                		}
            			
                		$scope.$apply();	
    	            });
            		
            	}

            	var findNoChangeMember=function(){
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
            			}
            			$scope.newTeacherList=showTeacherList;
            			$scope.alterTeacherList=showTeacherList;
                		$scope.$apply();	
    	            });
            	}
            	
            	var findChangeMember=function(){
            		$httpService.post(config.findMemberAlterSortURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			for(var i=0;i<data.data.length;i++){
     	        		   var alter_sort=data.data[i].ALTER_SORT;
     	   					if(alter_sort==1){
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
     	   						FK_TeacherList.push(mumber); 
     	   					}else{
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
     	   					}
             		   }
            			$scope.newTeacherList=showTeacherList;
            			$scope.alterTeacherList=FK_TeacherList;
                		$scope.$apply();	
    	            });
            	}
            	
            	//查询已有的课题数据
            	var FK_TaskList=new Array();
            	var showTaskList=new Array();
            	$scope.newTaskList;
            	$scope.alterTaskList;
            	var findTaskList=function(){
            		$httpService.post(config.findTaskAlterSortURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			var flag=true;//是没有更改
            			for(var i=0;i<data.data.length;i++){
            				var alter_sort=data.data[i].ALTER_SORT;
            				if(alter_sort!=undefined||alter_sort!=null){				
            					flag=false;
            					break;
                			}else{
                				flag=true;
                			}
            			}
                		if(flag==true){
                			findNoChangeTask();
                		}else{
                			
                			findChangeTask();
                		}
            			
                		$scope.$apply();	
    	            });
            	}
            	//课题没有更改
            	var findNoChangeTask=function(){
            		$httpService.post(config.findTaskListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			for(var i=0;i<data.data.length;i++){	
            				var task={
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
            			$scope.alterTaskList=showTaskList;
                		$scope.$apply();	
    	            });
            	}
            	
            	var  findChangeTask=function(){
            		$httpService.post(config.findTaskAlterSortURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			for(var i=0;i<data.data.length;i++){
     	        		   var alter_sort=data.data[i].ALTER_SORT;
     	   					if(alter_sort==1){
     	   					var task={
	     	   						"MEMBER_PROJECT_PK":data.data[i].MEMBER_PROJECT_PK,
	     	   						"PROJECT_NAME":data.data[i].PROJECT_NAME,
	     	   						"PROJECT_KIND":data.data[i].PROJECT_TYPE,
	     	           				"START_DATE":data.data[i].APPLY_DATE,
	     	           				"COMPLETION":data.data[i].COMPLETION,
	     	           				"APPROVE_UNIT":data.data[i].APPROVE_UNIT
	     	           				};
		     	   				FK_TaskList.push(task);
     	   					}else{
	     	   					var task={
	     	   						"MEMBER_PROJECT_PK":data.data[i].MEMBER_PROJECT_PK,
	     	   						"PROJECT_NAME":data.data[i].PROJECT_NAME,
	     	   						"PROJECT_KIND":data.data[i].PROJECT_TYPE,
	     	           				"START_DATE":data.data[i].APPLY_DATE,
	     	           				"COMPLETION":data.data[i].COMPLETION,
	     	           				"APPROVE_UNIT":data.data[i].APPROVE_UNIT
	     	           				};
		     	   				showTaskList.push(task);
     	   					}
             		   }
            			$scope.newTaskList=showTaskList;
            			$scope.alterTaskList=FK_TaskList;	
                		$scope.$apply();	
    	            });
            	}
            	
            	//查询已有的研究成果数据
            	$scope.alterResultList;//变更后的数据
            	$scope.newResultList;//变更前的数据
            	var showResultList=new Array();
            	var FK_ResultList=new Array();
            	var findResultList=function(){		
            		$httpService.post(config.findResultAlterSortURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			var flag=true;//是没有更改
            			for(var i=0;i<data.data.length;i++){
            				var alter_sort=data.data[i].ALTER_SORT;
            				if(alter_sort!=undefined||alter_sort!=null){				
            					flag=false;
            					break;
                			}else{
                				flag=true;
                			}
            			}
                		if(flag==true){
                			findNoChangeResult();
                		}else{
                			
                			findChangeResult();
                		}
                		$scope.$apply();	
    	            });
            		
            	}
	
            	
            //预期成果变更前后没有改变的
           var findNoChangeResult=function(){
        	   $httpService.post(config.findResultListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
       			for(var i=0;i<data.data.length;i++){	
       				var result={
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
       			$scope.alterResultList=showResultList;	
           		$scope.$apply();	
	            });
           }
            	
           
         //预期成果变更前后有改变的
           var findChangeResult=function(){
        	   $httpService.post(config.findResultAlterSortURL,{"EXPLORE_PK":params.pk}).success(function(data) {
        		   for(var i=0;i<data.data.length;i++){
	        		   var alter_sort=data.data[i].ALTER_SORT;
	   					if(alter_sort==1){
	   						var result={
	   	       						"MEMBER_EXPECTING_PK":data.data[i].MEMBER_EXPECTING_PK,
	   	               				"TEACHER_ID":data.data[i].TEACHER_ID,
	   	               				"PEOPLE":data.data[i].TEACHER_NAME,
	   	               				"STAGE_START_TIME":data.data[i].STAGE_START_TIME,
	   	               				"STAGE_END_TIME":data.data[i].STAGE_END_TIME,
	   	               				"RESULT_FORM":data.data[i].RESULT_FORM,
	   	               				"RESULT_STAGE_NAME":data.data[i].RESULT_STAGE_NAME
	   	               				};
	   						FK_ResultList.push(result); 
	   					}else{
	   						var result={
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
        		   }
       			$scope.newResultList=showResultList;
       			$scope.alterResultList=FK_ResultList;	
           		$scope.$apply();	
	            });
           }
           
           //审核详情
           var findCheckInfo=function(){
           	$httpService.post(config.findCheckInfoURL,{"EXPLORE_PK":params.pk}).success(function(data) {
       			$scope.checkInfoList=data.data;
           		$scope.$apply();	
	            });
           }
           //通过	
            $scope.success=function(){
            	$scope.form.SCHOOL_CHECK_RESULT='1';
        		$scope.check();
            }
            //不通过	
            $scope.fail=function(){
            	$scope.form.SCHOOL_CHECK_RESULT='2';
            	var checkInfo = $scope.form.SCHOOL_CHECK_OPINION;
        		if (checkInfo == null || checkInfo == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写审核不通过的意见。"});
        			return;
				}
        		$scope.check();
            }
            
    		//审核
    		$scope.check=function(){
        		$httpService.post(config.checkURL,$scope.form).success(function(data) {
                	if(data.code != '0000'){
                		loggingService.info(data.msg);
                	}else{
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"审核成功！"});
                		eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});//发送更新事件，刷新数据
                		$scope.goback();
                	}
                 }).error(function(data) {
                     loggingService.info('校级审核出错！');
                 });

    		}
    		
    		
        	$scope.goback = function() { 
        		var m2 = {
        				url:"aps/content/EduResearch/SchoolCheck/list/config.json",
        				contentName:"content",
        				size:"modal-lg",
        				text:"校级审核",
        				icon:"edit"
    				}
    				eventBusService.publish(controllerName, 'appPart.load.content', m2);
    		}
  
            }
        ];
    });
}).call(this);
