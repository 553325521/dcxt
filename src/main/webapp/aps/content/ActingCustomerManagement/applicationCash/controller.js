
(function() {
	define(['jqueryweui','pickercity'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '提现管理';	
				
				scope.WeChatNumber = '123@qq.com';
				
				scope.WeChatName = '王家军';
				
				scope.balance = '305.50';
				
				scope.cashAll = function(){
					console.info("aa");
				}
			}
		];
	});
}).call(this);

$(function() {
	
})