
	(function() {
		define(['slideleft'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					//页面暂时未加载完毕，显示加载中动画
					scope.pageShow = "False"
					scope.input_value = '';
					
					
					// 提成记录数据源
					scope.commission_record_list = [];
					
					//链接跳转
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json",
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					
					//初始化页面操作
					var init = function() {
						$httpService.post(config.findURL, '').success(function(data) {
						
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else {
								console.info(data.data)
								scope.commission_record_list = data.data;
								scope.pageShow = "True";//页面加载完毕，显示页面，取消加载中动画
								scope.$apply();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}

					init();
					
					
					comboboxInit();
					
					function comboboxInit(){
						
						//清除按钮
						$(".weui_icon_clear").click(function(){
							scope.input_value = ''
							scope.$apply()
						});
					
					}
				}
			];
		});
	}).call(this);

	$(function() {
		
	})