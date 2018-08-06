	(function() {
		define([], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					scope.tsPage = "true";
					scope.form={}
					
					//页面初始化
					var init = function(){
						$httpService.post(config.findURL).success(function(data) {
							if (data.code == '0000') {
								scope.form = data.data;
								if(scope.tsPage == "false" && scope.lookMessage == "false"){
									if("1" != scope.form.USER_SEX && "2" != scope.form.USER_SEX){
										scope.form.USER_SEX = 1;
									}
								}
								scope.$apply();
							} 
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}
					
					init();
					
					
					var $form = $("#form");
					$form.form();
					//保存
					scope.doSave = function(){
						$form.validate(function(error) {
							if (!error) {
								
								//弹出保存询问
								var m2 = {
									"url" : "aps/content/ActingCustomerManagement/perfectInformation/config.json",
									"title" : "提示",
									"contentName" : "modal",
									"text" : "是否保存?"
								}
								eventBusService.publish(controllerName, 'appPart.load.modal', m2);
							}
						})
					}
					
					
					// 弹窗确认事件
					eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
						 $httpService.post(config.saveURL,$scope.form).success(function(data){
							 if(data.code != "0000"){
								 var m2 = {
									"title" : "提示",
									"contentName" : "modal",
									"text" : data.data
								}
							 }else{
								 var m2 = {
									"title" : "提示",
									"contentName" : "modal",
									"text" : data.data,
								 }
								
							 }
							eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						    }).error(function(data){
						    	loggingService.info('获取测试信息出错');
						    });
					});
					
					// 弹窗取消事件
					eventBusService.subscribe(controllerName, controllerName + '.close', function(event, btn) {
						eventBusService.publish(controllerName, 'appPart.load.modal.close', {
							contentName : "modal"
						});
					});
					
					
					//完善信息
					scope.perfectInformation = function(){
						init();
						scope.tsPage = "false";
						scope.lookMessage = "false";
						scope.pageTitle="完善信息";
					}
					
					
					//查看我的信息
					scope.lookInformation = function(){
						init();
						scope.tsPage = "false";
						scope.lookMessage = "true";
						scope.pageTitle="代理信息";
					}
					
					
					//返回按钮
					scope.back = function(){
						init();
						scope.tsPage = "true";
						scope.lookMessage = "false";
					}
					
					
					
					
					function initCuntomerInformation(){
						$httpService.post(config.showAgentShopInfoURL, $scope.form).success(function(data) {
							if (data.code == '0000') {
								scope.customer_information_list = data.data;
								scope.pageShow = "True"
								
								scope.$apply();
							} 
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}
					
			
				}
			];
		});
	}).call(this);

	$(function() {
		
	})