(function() {
	define([], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				// 定义页面标题
				$scope.pageTitle = config.pageTitle

				//初始化 form 表单
				scope.form = {};

				scope.form.fid = params.fid

				scope.form.url = realurl + "/" + config.getCodeURL

				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + scope.form.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

			}
		];
	});
}).call(this);