(function() {
    define(['jqueryUiZh'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	//接收保存按钮事件
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	//承担人
            	 $scope.peopleList=new Array();
            	/*初始化数据*/
            	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            		console.log(data.data);
            		$scope.form=data.data;
            		var user={"TEACHER_ID":data.data.FK_TEACHER,"TEACHER_NAME":$scope.form.TEACHER_NAME};
            		$scope.peopleList.push(user);
            		$scope.$apply();
        			findMemberList();
        			findTaskList();
        			findResultList();
	            });
            	
            	$scope.newTeacherList;
            	//临时存放添加成员的数据
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
            				FK_TeacherList.push(mumber);
            				showTeacherList.push(mumber);
            				var user={"TEACHER_ID":data.data[i].FK_TEACHER,"TEACHER_NAME":data.data[i].TEACHER_NAME};
            				$scope.peopleList.push(user);
            			}
            			$scope.newTeacherList=showTeacherList;
                		$scope.$apply();	
    	            });
            		
            	}

            	//临时存放课题的数据
            	var FK_TaskList=new Array();
            	var showTaskList=new Array();
            	$scope.newTaskList;
            	var findTaskList=function(){
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
            				FK_TaskList.push(task);
            				showTaskList.push(task); 	
            			}
            			$scope.newTaskList=showTaskList;
                		$scope.$apply();	
    	            });
            	}
            	
            	//临时存放研究成果
            	var FK_ResultList=new Array();
            	var showResultList=new Array();
            	$scope.newResultList;
            	var findResultList=function(){
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
            				FK_ResultList.push(result);
            				showResultList.push(result); 	
            			}
            			$scope.newResultList=showResultList;
                		$scope.$apply();	
    	            });
            	}
            
            	/*//审核详情
                var findCheckInfo=function(){
                	$httpService.post(config.findCheckInfoURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			$scope.checkInfoList=data.data;
                		$scope.$apply();	
    	            });
                }*/
            	
            //通过
            $scope.success=function(){
            	$scope.form.SCHOOL_CHECK_RESULT='1';
            	var money=$scope.form.FINANCE_MONEY;
        		if (money == null || money == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写经费。"});
        			return;
				}
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
                     loggingService.info('校级审核申请书出错！');
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
