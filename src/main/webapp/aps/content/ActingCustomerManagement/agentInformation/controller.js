
	(function() {
		define(['slideleft'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					
					// 代理信息
					scope.agent_info = {}
					
					//页面初始化
					var init = function(){
						$httpService.post(config.findURL).success(function(data) {
							if (data.code == '0000') {
								scope.agent_info = data.data;
								scope.$apply();
							} 
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}
					
					init();
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json",
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