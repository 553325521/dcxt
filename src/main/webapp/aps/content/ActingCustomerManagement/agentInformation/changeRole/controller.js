
(function() {
	define(['jqueryweui','pickercity'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				// 弹窗默认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : "支付成功",
							"toUrl" : "aps/content/ActingCustomerManagement/config.json"
						}
					eventBusService.publish(controllerName, 'appPart.load.modal', m2);
				});
				// 弹窗取消事件
				eventBusService.subscribe(controllerName, controllerName + '.close', function(event, btn) {
					eventBusService.publish(controllerName, 'appPart.load.modal.close', {
						contentName : "modal"
					});
				});
				
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '角色切换';	
				
				scope.form = {};
				
				scope.toHref = function(path,cid) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?cid=" + cid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
			}
		];
	});
}).call(this);

$(function() {
	
})