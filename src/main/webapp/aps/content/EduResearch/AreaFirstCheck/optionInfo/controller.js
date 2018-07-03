(function() {
    define([], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	//通过选择查看不同阶段的详情
            	
            	$scope.showExp=function(option){
            		$httpService.post(config.findStatusURL, {"SC_EXPLORE_PK":params.pk}).success(function(data) {
	            			$scope.form.apply=data.data.FK_EXPLORE_PETITION;//申请书PK
	                		$scope.form.report=data.data.FK_EXPLORE_REPORT;//申请书PK
	                		$scope.form.progress=data.data.FK_EXPLORE_PROGRESS;
	                		$scope.form.achieve=data.data.FK_PROJECT_ACHIEVE;
	                		if(option==1){
	                			if($scope.form.apply!=null){
	                				eventBusService.publish(controllerName,'appPart.load.modal.close', {contentName:"modal"});
	                    			var m2 = {
	            	        				url:"aps/content/EduResearch/SchoolCheck/applyInfo/config.json?pk="+params.pk,
	            	        				contentName:"content",
	            	        				size:"modal-lg",
	            	        				text:"申请书详情",
	            	        				icon:"edit"
	        	        				}
	        	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
	                			}else{
	                				eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"您还未保存申请书！"});
	                			}
	                			
	                		}else if(option==2){
	                			if($scope.form.report!=null){
	                				eventBusService.publish(controllerName,'appPart.load.modal.close', {contentName:"modal"});
	                    			var m2 = {
	            	        				url:"aps/content/EduResearch/SchoolCheck/reportInfo/config.json?pk="+params.pk,
	            	        				contentName:"content",
	            	        				size:"modal-lg",
	            	        				text:"开题报告详情",
	            	        				icon:"edit"
	        	        				}
	        	        			eventBusService.publish(controllerName, 'appPart.load.content', m2);
	                			}else{
	                				eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"您还未保存开题报告！"});
	                			}
	                			
	                		}else if(option==3){
	                			if($scope.form.progress!=null){
	                				eventBusService.publish(controllerName,'appPart.load.modal.close', {contentName:"modal"});
	                    			var m2 = {
	            	        				url:"aps/content/EduResearch/SchoolCheck/progressinfo/config.json?pk="+params.pk,
	            	        				contentName:"content",
	            	        				size:"modal-lg",
	            	        				text:"中期报告详情",
	            	        				icon:"edit"
	        	        				}
	        	        			eventBusService.publish(controllerName, 'appPart.load.content', m2);
	                			}else{
	                				eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"您还未保存中期报告！"});
	                			}
	                		}else{
	                			if($scope.form.achieve!=null){
	                				eventBusService.publish(controllerName,'appPart.load.modal.close', {contentName:"modal"});
	                    			var m2 = {
	            	        				url:"aps/content/EduResearch/SchoolCheck/achieveinfo/config.json?pk="+params.pk,
	            	        				contentName:"content",
	            	        				size:"modal-lg",
	            	        				text:"结题报告详情",
	            	        				icon:"edit"
	        	        				}
	        	        			eventBusService.publish(controllerName, 'appPart.load.content', m2);
	                			}else{
	                				eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"您还未保存结题报告！"});
	                			}
	                		}
            		      $scope.$apply();
            		    
            		});

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
