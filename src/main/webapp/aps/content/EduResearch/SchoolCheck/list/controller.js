(function() {
    define([], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$scope.form = {};
            
            	$scope.check=function(pk,status){
            		if(status==1){
            			var m2 = {
    	        				url:"aps/content/EduResearch/SchoolCheck/applycheck/config.json?pk="+pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"申请书校级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==7){
            			var m2 = {
    	        				url:"aps/content/EduResearch/SchoolCheck/reportcheck/config.json?pk="+pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"开题报告校级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==11){
            			var m2 = {
    	        				url:"aps/content/EduResearch/SchoolCheck/progresscheck/config.json?pk="+pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"中期报告校级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==15){
            			var m2 = {
    	        				url:"aps/content/EduResearch/SchoolCheck/achievecheck/config.json?pk="+pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"结题报告校级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==22){
            			var m2 = {
    	        				url:"aps/content/EduResearch/SchoolCheck/altercheck/config.json?pk="+pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"延期或终止校级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else if(status==26){
            			var m2 = {
    	        				url:"aps/content/EduResearch/SchoolCheck/delaycheck/config.json?pk="+pk,
    	        				contentName:"content",
    	        				size:"modal-lg",
    	        				text:"延期或终止校级审核",
    	        				icon:"edit"
	        				}
	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            		}else {
            		}
            	}
            	//初始化数据
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
                        	if(status==1 || status==7 || status==11 ){
                        		$("#"+controllerName+" #check"+$scope.dataList[i].SC_EXPLORE_PK).show();
                        	}else if( status==15 || status==22 ||status==26){
                        		$("#"+controllerName+" #check"+$scope.dataList[i].SC_EXPLORE_PK).show();
                        	}else{
                        		$("#"+controllerName+" #check"+$scope.dataList[i].SC_EXPLORE_PK).hide();
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
