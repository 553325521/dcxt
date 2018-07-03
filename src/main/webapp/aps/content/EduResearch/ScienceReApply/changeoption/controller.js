(function() {
    define([], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	
            	//通过选择查看不同阶段的详情
            	$scope.showExp=function(option){		
            		if(option==1){
            			eventBusService.publish(controllerName,'appPart.load.modal.close', {contentName:"modal"});
            			var m2 = {
    	        				url:"aps/content/EduResearch/Alter/add/config.json?pk="+params.pk+"&status="+params.status,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"教科研终止延期报告",
    	        				icon:"edit"
	        				}
	        			eventBusService.publish(controllerName, 'appPart.load.content', m2);
            			
            		}else{
            			eventBusService.publish(controllerName,'appPart.load.modal.close', {contentName:"modal"});
            			var m2 = {
    	        				url:"aps/content/EduResearch/Delay/add/config.json?pk="+params.pk+"&status="+params.status,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"教科研终止延期报告",
    	        				icon:"edit"
	        				}
	        			eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}
            		
            		
            	}
 
            	//接收关闭按钮事件
            	eventBusService.subscribe(controllerName, controllerName+'.close', function(event, btn) {
                  	eventBusService.publish(controllerName,'appPart.load.modal.close', {contentName:"modal"});
                });
       
            	//初始化表单校验
            	VALIDATE.iniValidate($scope);
            	
            }
        ];
    });
}).call(this);
