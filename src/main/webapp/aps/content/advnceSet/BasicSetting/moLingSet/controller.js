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

				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

				scope.doSave = function() {

					if (scope.form.SHOP_MOLING_VALUE == undefined || scope.form.SHOP_MOLING_VALUE == '') {
						var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : "请选择抹零模式!"
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						return;
					}

					var $form = $("#menuAddForm");
					$form.form();
					$form.validate(function(error) {
						if (!error) {
							var m2 = {
								"url" : "aps/content/advnceSet/BasicSetting/moLingSet/config.json",
								"title" : "提示",
								"contentName" : "modal",
								"text" : "是否确定保存?"
							}
							eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						}
					})
				}

				// 弹窗确认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					$httpService.post(config.updateShopMoLingURL, $scope.form).success(function(data) {
						var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : data.data
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
					$httpService.post(config.findURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
							scope.form.SHOP_MOLING_VALUE = 1;
						} else {
							scope.form = data.data;
							scope.$apply();
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