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

				scope.form.USER_TAG_NAME = params.tagName;

				scope.form.USER_TAG_ID = params.tagId;

				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

				// 弹窗确认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {

					$httpService.post(config.updateURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data,
								"toUrl" : "aps/content/SystemSetup/BasicSetting/userTag/config.json?fid=" + params.fid
							}
						} else {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data,
								"toUrl" : "aps/content/SystemSetup/BasicSetting/userTag/config.json?fid=" + params.fid
							}
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
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

				scope.doSave = function() {
					var $form = $("#updateTagForm");
					$form.form();
					$form.validate(function(error) {
						if (!error) {
							var m2 = {
								"url" : "aps/content/SystemSetup/BasicSetting/userTag/update/config.json",
								"title" : "提示",
								"contentName" : "modal",
								"text" : "是否确定保存",
								"toUrl" : "aps/content/SystemSetup/BasicSetting/userTag/config.json"
							}
							eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						}
					});
				}

			}
		];
	});
}).call(this);