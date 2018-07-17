(function() {
	define([ 'jqueryUiZh', 'ZeroClipboard', 'swfobject', 'uploadify', 'uploadauto' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				//初始化 form 表单
				$scope.form = {};
				$scope.form.SC_EXPLORE_PK = params.pk;

			}
		];
	});
}).call(this);