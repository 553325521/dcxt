(function() {
    define(['jqueryUiZh'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
             	$httpService.post(config.findByIdURL,{"TEACHER_RESULT_PK":params.pk}).success(function(data) {
   	             	if(data.code != '0000'){
   	             		loggingService.info(data.msg);
   	             	}else{
	   	             	 $scope.form=data.data;
	   	             	 $scope.form.USER_PK=data.data.FK_AUTHOR
	   	             	 $scope.$apply();
	   	             	 $scope.findTeacherInfo();
   	             	}
            	}).error(function(data) {
                    loggingService.info('获取初始化数据出错');
                });
            	
            	
             	$scope.findTeacherInfo=function(){
             		var USER_PK={"USER_PK":$scope.form.USER_PK};
		        	$httpService.post(config.findTeacherInfoURL,USER_PK).success(function(data) {
	                	if(data.code != '0000'){
	                		loggingService.info(data.msg);
	                	}else{
	                		$scope.form.SCHOOL_TYPE_PK= data.data.SCHOOL_TYPE;
	                		$scope.form.UNIT_PK=data.data.UNIT_PK;
	                		$scope.form.USER_PK=data.data.USER_PK;
	                		$scope.$apply();
	                		$scope.shoolType();
	                	}
	
	                 }).error(function(data) {
	                     loggingService.info('获取学校出错');
	                 });
             	}
             	
             	
            	
            	$scope.shoolType=function(){
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
            	}
            	
            	
            	//通过学校类别pk获取所有学校
            	$scope.schoolChange=function(){
            		console.log($scope.form.SCHOOL_TYPE_PK);
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
            	
            	//通过学校PK获取所有老师
            	$scope.teacherChange=function(){
            		console.log($scope.form.UNIT_PK);
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
            	
            	$scope.teacherInfo=function(){
            		console.log($scope.form.USER_PK);
            		var user_pk={"USER_PK":$scope.form.USER_PK};
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
            	//保存按钮事件
            	eventBusService.subscribe(controllerName, controllerName+'.save', function(event, btn) {
            		
            		//校验表单
            		if(!$scope.validateForm()){
            			return;
            		}
            		
            		$httpService.post(config.updateURL,$scope.form).success(function(data) {
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
