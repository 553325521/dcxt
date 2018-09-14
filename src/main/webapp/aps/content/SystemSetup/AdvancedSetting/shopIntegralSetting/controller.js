(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					//初始化form表单
					scope.form = {};
					scope.pageShow = "False";
					scope.pageTitle = config.pageTitle;
					var init = function(){
						$httpService.post(config.findURL).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else {
								scope.pageShow = "True";
								scope.form = data.data;
								scope.$apply();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
						
					}
					
					init();
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					var $form = $("#form");
					$form.form();
					//保存
					scope.doSave = function(){
						$form.validate(function(error) {
							if (!error) {
								//弹出保存询问
								var m2 = {
									"url" : "aps/content/SystemSetup/AdvancedSetting/shopIntegralSetting/config.json",
									"title" : "提示",
									"contentName" : "modal",
									"text" : "是否保存?"
								}
								eventBusService.publish(controllerName, 'appPart.load.modal', m2);
							}
						});
					}
					
					// 弹窗确认事件
					eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
						$httpService.post(config.saveURL,scope.form).success(function(data) {
								 var m2 = {
									"title" : "提示",
									"contentName" : "modal",
									"text" : data.data
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
			}
		];
	});
}).call(this);
$(function() {
})