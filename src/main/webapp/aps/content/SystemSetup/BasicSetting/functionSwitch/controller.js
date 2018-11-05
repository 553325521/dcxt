(function() {
	define([ 'slideleft' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				// 定义页面标题
				$scope.pageTitle = config.pageTitle

				//初始化 form 表单
				scope.form = {};

				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

				var init = function() {
					$httpService.post(config.findURL, scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							for (var tag in data.data) {
								if (tag != 'FK_SHOP') {
									data.data[tag] = data.data[tag] == 'true' ? true : false
								}
							}
							scope.tagList = data.data;
							scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				// 弹窗确认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {

					$httpService.post(config.removeUrl, scope.toRemoveTag).success(function(data) {
						if (data.code != '0000') {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data
							}
						} else {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data
							}
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						init()
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});

				});

				// 弹窗取消事件
				eventBusService.subscribe(controllerName, controllerName + '.close', function(event, btn) {
					eventBusService.publish(controllerName, 'appPart.load.modal.close', {
						contentName : "modal"
					});
				});

				init()

				$scope.changeUseStatus = function(col) {
					$httpService.post(config.updateURL, scope.tagList).success(function(data) {
						var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : data.data
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);

						if (data.code == '0000') {
							scope.tagList[col] = scope.tagList[col] ? true : false;
						}
					}).error(function(data) {
						var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : '操作失败！'
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
					});
				}
			}
		];
	});
}).call(this);