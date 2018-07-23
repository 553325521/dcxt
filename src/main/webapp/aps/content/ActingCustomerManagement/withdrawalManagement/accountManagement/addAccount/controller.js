
	(function() {
		define([], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					scope.qrUrl = "aps/content/ActingCustomerManagement/shopclaim/code.png";
				}
			];
		});
	}).call(this);

	$(function() {
	})