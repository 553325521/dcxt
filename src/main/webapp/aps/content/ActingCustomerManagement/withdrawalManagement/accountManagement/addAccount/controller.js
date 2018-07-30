
	(function() {
		define([], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					scope.qrUrl = "aps/content/ActingCustomerManagement/shopclaim/code.png";
					scope.pageTitle = "添加提现账号";
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
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