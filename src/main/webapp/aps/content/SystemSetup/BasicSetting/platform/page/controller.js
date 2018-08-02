(function() {
	define([ 'jqueryweui' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				// 定义页面标题
				scope.pageTitle = config.pageTitle

				scope.form = {}
				
				scope.form.url = realurl + "/" + config.findAuthorizedCodeURL
				
				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid + "&AppId=" + params.AppId,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

//				var init = function() {
//					$httpService.post(config.findAuthorizedCodeURL, $scope.form).success(function(data) {
//						if (data.code != '0000') {
//							loggingService.info(data.data);
//						} else {
//							console.info(data)
//						}
//					}).error(function(data) {
//						loggingService.info('获取测试信息出错');
//					});
//				}
//
//				init()

			}
		];
	});
}).call(this);