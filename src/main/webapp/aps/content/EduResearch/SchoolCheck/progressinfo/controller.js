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
            		$scope.form.EXPLORE_PROGRESS_PK=data.data.EXPLORE_PROGRESS_PK;
            		$scope.form.SPENTFUND=data.data.SPENTFUND;
            		$scope.form.BOOK_NUMBER=data.data.BOOK_NUMBER;
            		$scope.form.THESIS_NUMBER=data.data.THESIS_NUMBER;
            		$scope.form.RESEARCH_NUMBER=data.data.RESEARCH_NUMBER;
            		$scope.form.WORK_PROGRESS=data.data.WORK_PROGRESS;
            		$scope.form.REPRESENT_RESULTS=data.data.REPRESENT_RESULTS;
            		/*$scope.form.SCHOOL_CHECK_USER=data.data.SCHOOL_CHECK_USER;
            		$scope.form.SCHOOL_CHECK_TIME=data.data.SCHOOL_CHECK_TIME;
            		$scope.form.SCHOOL_CHECK_RESULT=data.data.SCHOOL_CHECK_RESULT;*/
            		$scope.form.SCHOOL_CHECK_OPINION=data.data.SCHOOL_CHECK_OPINION;
            		$scope.form.AREA_CHECK_OPINION=data.data.AREA_CHECK_OPINION;
            		$scope.$apply();
            		projectInfo();
            		findAchieveList();
	            });
            	
            	//申请书详情
            	var projectInfo=function(){
            		$httpService.post(config.findProjectInfoURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            			$scope.form.PROJECT_TITLE=data.data.PROJECT_TITLE;
                		$scope.form.PROJECT_TYPE=data.data.PROJECT_TYPE;
                		$scope.form.PROJECT_LEVEL=data.data.PROJECT_LEVEL;
                		$scope.form.TEACHER_NAME=data.data.TEACHER_NAME;
                		$scope.form.TEACHER_SCHOOL_NAME=data.data.TEACHER_SCHOOL_NAME;
                		$scope.form.PRICE=data.data.PRICE;
                		$scope.form.APPLY_DATE=data.data.APPLY_DATE;
                		$scope.form.APPLY_CODE=data.data.APPLY_CODE;
                		$scope.form.FINISH_DATE=data.data.FINISH_DATE;
                		$scope.form.EXPECT_RESULT=data.data.EXPECT_RESULT;
                		$scope.form.FINANCE_MONEY=data.data.FINANCE_MONEY;
                		$scope.$apply();
    	            });
            	}
            	     	
            	//查询中期报告取得成果      	
            	$scope.newAchieveList;
            	var findAchieveList=function(){
            		$httpService.post(config.findAchieveListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			$scope.newAchieveList=data.data;
                    		$scope.$apply();	
        	            });
                		
            	}
            	//返回上一页
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
