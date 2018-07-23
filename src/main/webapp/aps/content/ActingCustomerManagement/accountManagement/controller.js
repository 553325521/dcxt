
(function() {
	define(['slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '账号管理';	
				
				scope.account_list = [{
					wxNumber:'134@qq.com',
					wxName:'善待好人',
					realName:'王家军'
				},{
					wxNumber:'134@qq.com',
					wxName:'善待好人',
					realName:'王家军'
				},{
					wxNumber:'134@qq.com',
					wxName:'善待好人',
					realName:'王家军'
				}];
			}
		];
	});
}).call(this);

$(function() {
	
})