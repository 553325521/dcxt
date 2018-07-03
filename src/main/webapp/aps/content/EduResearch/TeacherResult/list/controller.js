(function() {
    define(['jqueryUiZh'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化
            	$scope.form = {};
	
            	//添加
            	eventBusService.subscribe(controllerName, controllerName+'.add', function(event, ojb) {
            		var m2 = {
	        				url:"aps/content/EduResearch/TeacherResult/add/config.json",
	        				contentName:"modal",
	        				size:"modal-lg",
	        				text:"添加教师成果",
	        				icon:"plus"
	        		}
	        		eventBusService.publish(controllerName, 'appPart.load.modal', m2);
        		});
            	
            	//修改
            	eventBusService.subscribe(controllerName, controllerName+'.update', function(event, ojb) {
            		var values = [];
                	$('#'+controllerName+' input[name="dataPk"]:checked').each(function(){ 
                		values.push($(this).val());
                	});
                	
                	if(values.length < 1){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请选择一条数据。"});
                	}else if(values.length > 1){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"不能同时删除多行数据，请选择一条数据。"});
                	}else{
                		var m2 = {
    	        				url:"aps/content/EduResearch/TeacherResult/update/config.json?pk="+values[0],
    	        				contentName:"modal",
    	        				size:"modal-lg",
    	        				text:"修改教师成果",
    	        				icon:"plus"
    	        		}
    	        		eventBusService.publish(controllerName, 'appPart.load.modal', m2);
                	}
        		})

            	eventBusService.subscribe(controllerName, controllerName+'.delete', function(event, ojb) {
            		var values = [];
                	$('#'+controllerName+' input[name="dataPk"]:checked').each(function(){ 
                		values.push($(this).val());
                	});
                	if(values.length < 1){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请选择一条数据。"});
                	}else if(values.length > 1){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"不能同时删除多行数据，请选择一条数据。"});
                	}else{
                		alert(values[0]);
                		if(confirm("是否确认删除！")) {
	                		$httpService.post(config.deleteURL, {"TEACHER_RESULT_PK":values[0]}).success(function(data) {
	                			if(data.code=="0000"){
	                				$scope.find();
	                				eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});
	    	                        eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"删除成功!"});                   			
	                			}
	        	            }).error(function(data) {
	        	            	eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"已经审核的数据，不能删除!"});
	                        });
                		}
                	}
            	});
            	
            	
            	 $scope.find = function() { 
             		$scope.form.page = JSON.stringify($scope.page);
             		$httpService.post(config.findURL, $scope.form).success(function(data) {
                 		$scope.dataList = data.data;
                        PAGE.buildPage($scope,data);
     	            });
 	            };
 	            
 	            $scope.select = function(){
 	            	$scope.page.current = 1;
 	            	$scope.find();
 	            }
 	            
 	            PAGE.iniPage($scope);
            	
            	//接收刷新事件
	            eventBusService.subscribe(controllerName, 'appPart.data.reload', function(event, data) {
	            	$scope.find();
	            });
	            
	            
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
