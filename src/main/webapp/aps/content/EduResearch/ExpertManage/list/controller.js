(function() {
    define([], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$scope.form = {};
            	//修改
            	$scope.update=function(pk,status,expert_pk){
            		if(status==3){
            			var m2 = {
    	        				url:"aps/content/EduResearch/ExpertManage/applyupdate/config.json?pk="+pk+"&expert_pk="+expert_pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"申请书专家评审修改",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==17){
            			var m2 = {
    	        				url:"aps/content/EduResearch/ExpertManage/achieveupdate/config.json?pk="+pk+"&expert_pk="+expert_pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"结题报告专家评审",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}
            	}
            	
            	
            	//专家评审
            	$scope.check=function(pk,status){
            		if(status==3){
            			var m2 = {
    	        				url:"aps/content/EduResearch/ExpertManage/applycheck/config.json?pk="+pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"申请书专家评审",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==17){
            			var m2 = {
    	        				url:"aps/content/EduResearch/ExpertManage/achievecheck/config.json?pk="+pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"结题报告专家评审",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}
            	}
            	
            	 $scope.find = function() { 
             		$scope.form.page = JSON.stringify($scope.page);
             		$httpService.post(config.findURL, $scope.form).success(function(data) {
                 		for( var i=0;i<data.data.length;i++){
                 			if(data.data[i].DELTA_TIME>=0){
                 				data.data[i].DELTA_TIME=data.data[i].DELTA_TIME;
                 			}else{
                 				data.data[i].DELTA_TIME="已超出";
                 			}
                 		}
             			$scope.dataList = data.data;
                        PAGE.buildPage($scope,data);
                        for(var i=0;i<$scope.dataList.length;i++){
                        	var status=$scope.dataList[i].STATUS;
                        	var check_flag=$scope.dataList[i].EXPERT_STATUS;
         	            	if(status==3||status==17){
         	            		$("#"+controllerName+" #expertCheck"+$scope.dataList[i].SC_EXPLORE_PK).show();
         	            	}else{
         	            		$("#"+controllerName+" #expertCheck"+$scope.dataList[i].SC_EXPLORE_PK).hide();
         	            	}
         	            	if(status==3||status==17){
         	            		if(check_flag==0){
             	            		$("#"+controllerName+" #expertCheck"+$scope.dataList[i].SC_EXPLORE_PK).show();
             	            		$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).hide();
             	            	}else if(check_flag==1){
             	            		$("#"+controllerName+" #expertCheck"+$scope.dataList[i].SC_EXPLORE_PK).hide();
             	            		$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).show();
             	            	}
         	            	}else{
         	            		$("#"+controllerName+" #expertCheck"+$scope.dataList[i].SC_EXPLORE_PK).hide();
         	            		$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).hide();
         	            	}
         	            	
         	            }
                       
     	            });
 	            };
 	            //查询
 	            $scope.select = function(){
 	            	$scope.page.current = 1;
 	            	$scope.find();
 	            }
 	            
 	            PAGE.iniPage($scope);
            	
            	//接收刷新事件
	            eventBusService.subscribe(controllerName, 'appPart.data.reload', function(event, data) {
	            	$scope.find();
	            });

            	
            }
        ];
    });
}).call(this);
