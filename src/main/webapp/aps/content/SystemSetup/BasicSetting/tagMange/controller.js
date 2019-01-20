(function() {
	define([ 'slideleft' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				// 定义页面标题
				scope.pageTitle = '标签列表';
				
				scope.init = function () {
					$httpService.post(config.findURL, {}).success(function(data) {
						if (data.code == '0000') {
							for (var index in data.data) {
								if (data.data[index].SHOP_TAG_TYPE == '0') {
									data.data[index].SHOP_TAG_TYPE_NAME = '商品标签';
								} else {
									data.data[index].SHOP_TAG_TYPE_NAME = '打印标签';
								}
							}
							scope.list = data.data;
							scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				scope.init();
				scope.toHref = function(path) { 
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				scope.del = function (id) {
					$httpService.post(config.findURL, {SHOP_TAG_PK : id}).success(function(data) {
						if (data.code == '0000') {
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
			}
		];
	});
}).call(this);