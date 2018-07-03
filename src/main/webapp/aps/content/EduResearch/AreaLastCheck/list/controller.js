(function() {
    define([], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$scope.form = {};
            	//审核
            	$scope.check=function(pk,status,report_pk){
            		if(status==3||status==4){
            			var m2 = {
    	        				url:"aps/content/EduResearch/AreaLastCheck/applycheck/config.json?pk="+pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"申请书区级复审",
    	        				icon:"edit"
	        			}
	        			eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==17||status==18){
            			var m2 = {
    	        				url:"aps/content/EduResearch/AreaLastCheck/achievecheck/config.json?pk="+pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"结题报告教育局审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else {
            			
            		}
            	}
            	
            	$scope.checkInfo=function(pk){
            		var m2 = {
	        				url:"aps/content/EduResearch/AreaLastCheck/checkinfo/config.json?pk="+pk,
	        				contentName:"content",
	        				size:"modal-lg",
	        				text:"申请书区级复审",
	        				icon:"edit"
        				}
        			eventBusService.publish(controllerName, 'appPart.load.content', m2);
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
                 		/*console.log($scope.dataList);*/
                        PAGE.buildPage($scope,data);
                        for(var i=0;i<$scope.dataList.length;i++){
                        	var status=$scope.dataList[i].STATUS;
                        	if(status==3|| status==4 || status==17|| status==18){
                        		$("#"+controllerName+" #check"+$scope.dataList[i].SC_EXPLORE_PK).show();
                        	}else{
                        		$("#"+controllerName+" #check"+$scope.dataList[i].SC_EXPLORE_PK).hide();
                        	}
                        	if(status==20){
                        		$("#"+controllerName+" #checkInfo"+$scope.dataList[i].SC_EXPLORE_PK).show();
                        	}else{
                        		$("#"+controllerName+" #checkInfo"+$scope.dataList[i].SC_EXPLORE_PK).hide();
                        	}
                        }
     	            });
 	            };
 	            
 	          
 	            //查看详情
 	           $scope.info=function(pk,petition_pk,report_pk,progress_pk,achieve_pk){
 	        	  var m2 = {
	        				url:"aps/content/EduResearch/SchoolCheck/optionInfo/config.json?pk="
	        					+pk+"&petition_pk="+petition_pk+"&report_pk="+report_pk+"&progress_pk="+
	        					progress_pk+"&achieve_pk="+achieve_pk,
	        				contentName:"modal"
	        				
      				}
      			  eventBusService.publish(controllerName, 'appPart.load.modal', m2);
 	           }
 	            
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
