(function() {
    define(['jqueryUiZh'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	//接收保存按钮事件
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	/*初始化数据*/
            	
            	//初始化数据
            	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            		$scope.form.USER_WORK=data.data.USER_WORK;
            		$scope.form.PUBLISH_TITLE=data.data.PUBLISH_TITLE;
            		$scope.form.PUBLISH_SORT=data.data.PUBLISH_SORT;
            		$scope.form.PERIODICALS_FLAG=data.data.PERIODICALS_FLAG;
            		//
            		var option=$scope.form.PERIODICALS_FLAG;
        			if(option==1){
        				$("#"+controllerName+" #publish").show();//
        				$("#"+controllerName+" #appear").show();
        			}else{
        				$("#"+controllerName+" #publish").hide();
        				$("#"+controllerName+" #appear").hide();
        			}
            		$scope.form.PERIODICALS_NAME=data.data.PERIODICALS_NAME;
            		$scope.form.PERIODICALS_DATE=data.data.PERIODICALS_DATE;
            		$scope.form.PERIODICALS_NUMBER=data.data.PERIODICALS_NUMBER;
            		$scope.form.WORK_DESCRIBE=data.data.WORK_DESCRIBE;
            		$scope.form.FINAL_REPORT=data.data.FINAL_REPORT;
            		$scope.form.EXPLORE_ACHIEVE_PK=data.data.EXPLORE_ACHIEVE_PK;
            		$scope.$apply();
            		projectInfo();
            		findMemberList();
            		findFileList();
            		findCheckInfo();
	            });
            	//申请书详情
            	var projectInfo=function(){
            		$httpService.post(config.findProjectInfoURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            			$scope.form.STATUS=data.data.STATUS;
            			$scope.form.APPLY_CODE=data.data.APPLY_CODE;
            			$scope.form.PROJECT_TITLE=data.data.PROJECT_TITLE;
                		$scope.form.PROJECT_TYPE=data.data.PROJECT_TYPE;
                		$scope.form.PROJECT_LEVEL=data.data.PROJECT_LEVEL;
                		$scope.form.TEACHER_NAME=data.data.TEACHER_NAME;
                		$scope.form.TEACHER_SCHOOL_NAME=data.data.TEACHER_SCHOOL_NAME;
                		$scope.form.PRICE=data.data.PRICE;
                		$scope.form.APPLY_DATE=data.data.APPLY_DATE;
                		$scope.form.FINISH_DATE=data.data.FINISH_DATE;
                		$scope.form.EXPECT_RESULT=data.data.EXPECT_RESULT;
                		$scope.form.TEACHER_JOB=data.data.TEACHER_JOB;
                		$scope.form.TEACHER_SKILL=data.data.TEACHER_SKILL;
                		$scope.form.TEACHER_GENDER=data.data.TEACHER_GENDER;
                		$scope.$apply();
    	            });
            	}
            	
            	
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
                    				"TEACHER_WORK":data.data[i].TEACHER_WORK,
                    				"TEACHER_GENDER":data.data[i].GENDER,
                    				"JOB_TITLE":data.data[i].JOB_TITLE,
                    				"SPECIALTY":data.data[i].SPECIALTY
                    				};
            				showTeacherList.push(mumber);
            			}
            			$scope.newTeacherList=showTeacherList;
            			FK_TeacherList=showTeacherList;
                		$scope.$apply();	
    	            });
            		
            	}
            	//文件
           var findFileList=function(){
        	   $httpService.post(config.findFileListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
	        		$scope.newFileList=data.data;
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
                    loggingService.info('审核开题报告出错！');
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
