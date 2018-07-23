
(function() {
	define(['slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '账号管理';	
				
				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
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