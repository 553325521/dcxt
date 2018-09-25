
(function() {
	define(['slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '卡券核销';	
				
				
				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				scope.confirmCancelCard = function(){
					 var m2 = {
				        		"url":"aps/content/DailyManagement/CardCancellation/config.json",
				        		"title":"提示",
				        		"contentName":"modal",
				        		"text":"是否核销"
				        	 }
				    eventBusService.publish(controllerName,"appPart.load.modal",m2);
				}
				// 弹窗默认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					$httpService.post(config.cancelCardURL, $scope.form).success(function(data) {
								var m2 = {
										"title" : "提示",
										"contentName" : "modal",
										"text" : data.data,
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