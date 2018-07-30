
	(function() {
		define(['slideleft'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					scope.pageShow = "False";
					scope.input_value = '';
					scope.trading_Record_List = [];
					
					function init(){
						$httpService.post(config.fingTradingRecordURL, $scope.form).success(function(data) {
							if (data.code == '0000') {
								console.info(data.data)
								scope.trading_Record_List = data.data;
								scope.pageShow = "True"
								scope.$apply();
							} 
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}
					init();
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json",
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					
					comboboxInit();
					
					function comboboxInit(){
						//清除按钮
						$(".weui_icon_clear").click(function(){
							console.info("5");
							scope.input_value = '';
							scope.$apply();
						});
					
					}
				}
			];
		});
	}).call(this);

	$(function() {
		
	})