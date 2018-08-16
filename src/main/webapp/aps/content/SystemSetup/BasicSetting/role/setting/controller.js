(function() {
	define([ 'jqueryweui' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				//初始化 form 表单
				$scope.form = {};

				$scope.form.ROLE_ID = params.rid;

				// 定义页面标题
				$scope.pageTitle = config.pageTitle

				$scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

				// 弹窗确认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					$httpService.post(config.updateStaffInfo, $scope.form).success(function(data) {
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
								"text" : data.data,
								"toUrl" : "aps/content/SystemSetup/BasicSetting/staff/config.json?fid=" + params.fid
							}
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
					}).error(function(data) {
						var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : '请求错误！'
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
					});

				});

				// 弹窗取消事件
				eventBusService.subscribe(controllerName, controllerName + '.close', function(event, btn) {
					eventBusService.publish(controllerName, 'appPart.load.modal.close', {
						contentName : "modal"
					});
				});

				var init = function() {
					$httpService.post(config.findFunctionListByRoleURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data);
						} else {
							$.each(data.data, function(index, value) {
								if (value.IS_USE == '1') {
									value.checked = true;
								} else {
									value.checked = false;
								}
							})
							$scope.funList = data.data;
							$scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				init();

				$scope.changeUseStatus = function(item) {
					$httpService.post(config.updateRoleFunStatusURL, item).success(function(data) {
						var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : data.data
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);

						if (data.code != '0000') {
							item.checked = !item.checked;
						} else {
							item.IS_USE = item.checked ? '1' : '0';
						}
					}).error(function(data) {
						var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : '操作失败！'
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						item.checked = !item.checked;
					});
				}

			}
		];
	});
}).call(this);