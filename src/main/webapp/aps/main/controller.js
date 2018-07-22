(function() {
	define([], function() {
		return [
			'$scope', '$location', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $location, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				//初始化中间模块
				if ($location.search()[config.contentName]) {
					$scope.appPartSrc = $location.search()[config.contentName];
				} else {
					$scope.appPartSrc = config.defaultAppPartSrc;
				}

				//移除不用的控制器里的接收器
				eventBusService.subscribe(controllerName, 'appPart.terminate', function(event, eventBusUnloadData) {
					if (null != eventBusUnloadData.appPartSrc && eventBusUnloadData.appPartSrc != undefined) {
						$httpService.post(eventBusUnloadData.appPartSrc, {}).success(function(data) {
							eventBusService.unsubscribeAll(data.name);
						});
					}
				});

				//接收模块加载事件
				eventBusService.subscribe(controllerName, 'appPart.load.content', function(event, m2) {
					if (m2.contentName === config.contentName) {
						if ($scope.appPartSrc != m2.url) {
							eventBusService.publish(controllerName, 'appPart.terminate', {
								appPartSrc : $scope.appPartSrc
							});
						}
						eventBusService.publish(controllerName, 'appPart.load.content.checkMenu', m2);

						//$location.search(m2.contentName, m2.url);
						return $scope.appPartSrc = m2.url;
					}
				});

				// 弹窗默认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					if (btn.url == undefined) {
						if (btn.toUrl != undefined) {
							var m2 = {
								"url" : btn.toUrl,
								"size" : "modal-lg",
								"contentName" : "content"
							}
							eventBusService.publish(controllerName, 'appPart.load.content', m2);
						}
					}
					eventBusService.publish(controllerName, 'appPart.load.modal.close', {
						contentName : "modal"
					});
				});

				// alert 确认弹窗事件
				eventBusService.subscribe(controllerName, 'appPart.load.modal', function(event, m2) {

					if (m2.contentName === config.modalName) {
						$scope.modal = m2;
						if (m2.url == undefined) {
							m2.url = "aps/main/config.json";
						}
						$httpService.post(m2.url, {}).success(function(data) {
							if (m2.url == "aps/main/config.json") {
								data.config.menu.buttons[0].toUrl = m2.toUrl
							}
							$scope.buttonsData = data.config.menu.buttons;
							$('#mainModal').show();
							$('#mainModal .weui_mask').addClass('weui_mask_visible');
							$('#mainModal .weui-custom-pop').addClass('weui-dialog-visible');
							$scope.$apply();
						}).error(function(data) {
							eventBusService.publish(controllerName, 'appPart.load.modal', {
								"title" : "操作提示",
								"content" : "打开窗口出错"
							});
						});
					}
				});

				// alert 确认弹窗事件关闭事件
				eventBusService.subscribe(controllerName, 'appPart.load.modal.close', function(event, m2) {
					if (m2.contentName === config.modalName) {
						$('#mainModal').hide();
						$('#mainModal .weui_mask').removeClass('weui_mask_visible');
						$('#mainModal .weui-custom-pop').removeClass('weui-dialog-visible');
						$scope.modal = {};
					}
				});

				//模态窗口下的按钮点击事件
				$scope.clickButton = function(btn) {
					eventBusService.publish(controllerName, btn.buttonLink, btn);
				};

			}
		];
	});
}).call(this);