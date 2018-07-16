(function() {
    define([], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$scope.form = {};
            	
/*            	$scope.save = function(){
            		var m2 = {
        				  "url":"aps/content/testTemp/config.json",
        			      "size":"modal-lg",
        			      "contentName":"content"
        				}   
        			eventBusService.publish(controllerName,'appPart.load.content', m2);
            	}*/
            	
            	$scope.save = function(){
            		$httpService.post(config.saveURL,$scope.form).success(function(data) {
	                	if(data.code != '0000'){
	                		loggingService.info(data.data);
	                	}else{
	                		init();
	                	}
	
	                 }).error(function(data) {
	                     loggingService.info('获取测试信息出错');
	                 });
            	}
            	
            	var init = function(){
            		$httpService.post(config.findURL,$scope.form).success(function(data) {
	                	if(data.code != '0000'){
	                		loggingService.info(data.data);
	                	}else{
	                		$scope.userList = data.data;
	                		$scope.$apply();
	                	}
	                 }).error(function(data) {
	                     loggingService.info('获取测试信息出错');
	                 });
            	}
            	init();
            
            }
        ];
    });
}).call(this);
