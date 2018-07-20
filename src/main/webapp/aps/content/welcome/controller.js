(function() {
	define([], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				// 定义页面标题
				$scope.pageTitle = '后台系统'

				//初始化 form 表单
				scope.form = {};

				scope.toHref = function(path, fid) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + fid,
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