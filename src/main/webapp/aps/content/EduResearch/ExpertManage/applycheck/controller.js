(function() {
    define(['jqueryUiZh'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	/*初始化数据*/
            	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            		console.log(data.data);
            		$scope.form=data.data;
            		$scope.$apply();
	            });
            //审核
            $scope.check = function(){
            	$httpService.post(config.checkURL,$scope.form).success(function(data) {
                	if(data.code != '0000'){
                		loggingService.info(data.msg);
                	}else{
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"审核成功！"});
                		eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});//发送更新事件，刷新数据
                		$scope.goback();
                	}
                 }).error(function(data) {
                     loggingService.info('专家审核出错出错！');
                 });
            }	
            //返回	
        	$scope.goback = function() { 
        		var m2 = {
        				url:"aps/content/EduResearch/ExpertManage/list/config.json",
        				contentName:"content",
        				size:"modal-lg",
        				text:"专家评审",
        				icon:"edit"
    				}
    				eventBusService.publish(controllerName, 'appPart.load.content', m2);
    		}
  
            }
        ];
    });
}).call(this);
