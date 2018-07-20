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

				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + scope.form.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

				var init = function() {
					$httpService.post(config.findURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							$.each(data.data.functionList, function(index, value) {
								value.FUNCTION_ICON = realurl + "/assets/weui/images/" + value.FUNCTION_ICON + ".png"
							})

							$scope.functionList = data.data.functionList;
							$scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				init()
			}
		];
	});
}).call(this);