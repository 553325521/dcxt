	(function() {
		define(['slideleft'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					scope.pageShow = "False";
					
					scope.input_value = '';
					
					scope.customer_information_list = [];
					
					
					
					
					scope.toHref = function(path,shopid) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fromUrl=" + config.currentUrl + "&shopid=" + shopid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					
					
					function initCuntomerInformation(){
						$httpService.post(config.showAgentShopInfoURL, $scope.form).success(function(data) {
							if (data.code == '0000') {
								scope.customer_information_list = data.data;
								scope.pageShow = "True"
								scope.$apply();
							}else if(data.code == "5555"){//信息没完善，需要完善信息
								scope.toHref('ActingCustomerManagement/perfectInformation','');
								scope.$apply();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}
					initCuntomerInformation();
				
					
			
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