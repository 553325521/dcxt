(function() {
    define(['jqueryUiZh'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	//初始化数据
            	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            		$scope.form.ACTIVITY_DESIGN=data.data.ACTIVITY_DESIGN;
            		$scope.form.REPORT_POINT=data.data.REPORT_POINT;
            		$scope.form.REPORT_IMPORTANT_CHANGE=data.data.REPORT_IMPORTANT_CHANGE;
            		$scope.form.EXPLORE_REPORT_PK=data.data.EXPLORE_REPORT_PK;
            		$scope.$apply();
            		projectInfo();
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
