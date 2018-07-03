(function() {
    define(['jqueryUiZh'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	
            	$httpService.post(config.findSchoolTypeURL).success(function(data) {
   	             	if(data.code != '0000'){
   	             		loggingService.info(data.msg);
   	             	}else{
	   	             	 $scope.schoolTypeList = data.data;
	   	             	 console.log($scope.schoolTypeList);
	   	             	 $scope.$apply();
	   	             	 $scope.schoolChange();
   	             	}
            	}).error(function(data) {
                    loggingService.info('获取初始化数据出错');
                });
            	
            	
            	//通过学校类别pk获取所有学校
            	$scope.schoolChange=function(){
            		if($scope.form.SCHOOL_TYPE_PK!=''&&$scope.form.SCHOOL_TYPE_PK!=null&&$scope.form.SCHOOL_TYPE_PK!=undefined){
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
            		if($scope.form.UNIT_PK!=''&&$scope.form.UNIT_PK!=null&&$scope.form.UNIT_PK!=undefined){
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
            	//通过老师PK获取老师信息
            	$scope.teacherInfo=function(){
            		if($scope.form.USER_PK!=''&&$scope.form.USER_PK!=null&&$scope.form.USER_PK!=undefined){
            			var user_pk={"TEACHER_PK":$scope.form.USER_PK};
    		        	$httpService.post(config.findTeacherInfoURL,user_pk).success(function(data) {
    	                	if(data.code != '0000'){
    	                		loggingService.info(data.msg);
    	                	}else{
    	                		 $scope.form.GENDER= data.data.GENDER;
    	                		 $scope.form.SCHOOL_NAME=data.data.SCHOOL_NAME;
    	                		 $scope.form.AUTHOR=data.data.USER_NAME;
    	                		 $scope.$apply();	
    	                	}
    	                 }).error(function(data) {
    	                     loggingService.info('获取老师出错');
    	                 });
            		}
            	}
            	
            	//接收保存按钮事件
            	$scope.form.DELETE_FLAG=0;
            	eventBusService.subscribe(controllerName, controllerName+'.save', function(event, btn) {
            		
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
                    		eventBusService.publish(controllerName,'appPart.load.modal.close', {contentName:"modal"});	//关闭模态窗口
                    	}
                     }).error(function(data) {
                         loggingService.info('添加教师成果出错！');
                     });
            		
            		
            	});
            	//初始化表单校验
            	VALIDATE.iniValidate($scope);
            	
            	//接收关闭按钮事件
            	eventBusService.subscribe(controllerName, controllerName+'.close', function(event, btn) {
                  	eventBusService.publish(controllerName,'appPart.load.modal.close', {contentName:"modal"});
                });
            	
            	
            	//初始化
            	$scope.form.PUBLISH_DATE='  ';
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
            	
            	
            }
        ];
    });
}).call(this);
