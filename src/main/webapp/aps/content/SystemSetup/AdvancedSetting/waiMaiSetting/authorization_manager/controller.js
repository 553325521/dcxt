(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					//初始化form表单
					scope.record_list = [];
					scope.pageShow = "True";
					scope.pageTitle = config.pageTitle;
			
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
						$httpService.post(config.findURL).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else {
								for (var tag in data.data) {
									if (tag == 'MT_SWITCH' || tag == 'EB_SWITCH' || tag == 'WM_ZDQRDD') {
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
					
					//授权
					$scope.SQ = function(col) {
						a = {}
						a.col = col
						$httpService.post(config.SQURL, a).success(function(data) {
							debugger;

							if (data.code == '0000') {
								window.location.href=data.data
							}else{
								var m2 = {
										"title" : "提示",
										"contentName" : "modal",
										"text" : data.data
									}
									eventBusService.publish(controllerName, 'appPart.load.modal', m2);
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
					
					//取消授权
					$scope.cancalSQ = function(col) {
						a = {}
						a.col = col
						$httpService.post(config.cancalSQURL, a).success(function(data) {
							if (data.code == '0000') {
								window.location.href=data.data
							}else{
								var m2 = {
										"title" : "提示",
										"contentName" : "modal",
										"text" : data.data
									}
									eventBusService.publish(controllerName, 'appPart.load.modal', m2);
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
$(function() {
})