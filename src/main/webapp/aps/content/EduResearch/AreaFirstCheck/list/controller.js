(function() {
    define([], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$scope.form = {};
            
            	$scope.check=function(pk,status,report_pk){
            		if(status==2){
            			var m2 = {
    	        				url:"aps/content/EduResearch/AreaFirstCheck/applycheck/config.json?pk="+pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"申请书区级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==8){
            			var m2 = {
    	        				url:"aps/content/EduResearch/AreaFirstCheck/reportcheck/config.json?pk="+pk+"&report_pk="+report_pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"开题报告区级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==12){
            			var m2 = {
    	        				url:"aps/content/EduResearch/AreaFirstCheck/progresscheck/config.json?pk="+pk+"&report_pk="+report_pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"中期报告区级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==16){
            			var m2 = {
    	        				url:"aps/content/EduResearch/AreaFirstCheck/achievecheck/config.json?pk="+pk+"&report_pk="+report_pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"结题报告区级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==23){
            			var m2 = {
    	        				url:"aps/content/EduResearch/AreaFirstCheck/altercheck/config.json?pk="+pk+"&report_pk="+report_pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"变更报告区级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==27){
            			var m2 = {
    	        				url:"aps/content/EduResearch/AreaFirstCheck/delaycheck/config.json?pk="+pk+"&report_pk="+report_pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"终止、延期报告区级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else {
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
                 		console.log($scope.dataList);
                        PAGE.buildPage($scope,data);
                        for(var i=0;i<$scope.dataList.length;i++){
                        	var status=$scope.dataList[i].STATUS;
                        	if(status==2 || status==8 || status==12 ){
                         		$("#"+controllerName+" #areaCheck"+$scope.dataList[i].SC_EXPLORE_PK).show();
                         	}else if( status==16 || status==23 ||status==27){
                         		$("#"+controllerName+" #areaCheck"+$scope.dataList[i].SC_EXPLORE_PK).show();
                         	}else{
                         		$("#"+controllerName+" #areaCheck"+$scope.dataList[i].SC_EXPLORE_PK).hide();
                         	}
                        }
                       
     	            });
 	            };
 	            
 	          
 	            //查看详情
 	           $scope.info=function(pk,petition_pk,report_pk,progress_pk,achieve_pk){
 	        	  var m2 = {
	        				url:"aps/content/EduResearch/AreaFirstCheck/optionInfo/config.json?pk="
	        					+pk+"&petition_pk="+petition_pk+"&report_pk="+report_pk+"&progress_pk="+
	        					progress_pk+"&achieve_pk="+achieve_pk,
	        				contentName:"modal"		
      				}
      			  eventBusService.publish(controllerName, 'appPart.load.modal', m2);
 	           }
 	            
 	            
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
