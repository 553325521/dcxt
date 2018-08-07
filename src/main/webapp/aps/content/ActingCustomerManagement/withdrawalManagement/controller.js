
(function() {
	define(['jqueryweui','pickercity'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '提现管理';	
				
				scope.WeChatNumber = '';
				
				scope.WeChatName = '';
				
				scope.balance = '';
				
				scope.account = "";
				scope.form = {};
				
				scope.form.CASH_MONEY="";
				/*获取代理商微信号和微信名称*/
				function initAgentInfo(){
					$httpService.post(config.loadAgentInfoURL, $scope.form).success(function(data) {
						if (data.code == '0000') {
							scope.WeChatNumber = data.data.USER_WX_NUMBER;
							scope.WeChatName = data.data.USER_NAME;
							scope.account = data.data.USER_BALANCE;
							if(scope.account.indexOf(".") == -1){
								scope.account = scope.account+".00";
							}
							scope.balance = scope.account;
							scope.$apply();
							console.info(data.data)
						} 
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				initAgentInfo();
				/*全部提现按钮实现方法*/
				scope.cashAll = function(){
					$("input[name='money']").val(scope.balance);
				}
				var $form = $("#CashForm");
				$form.form();
				scope.cash = function(){
					 $form.validate(function(error){
					        if(!error){
					        	scope.form.CASH_MONEY = $("input[name='money']").val();
								 var m2 = {
							        		"url":"aps/content/ActingCustomerManagement/addBusiness/config.json",
							        		"title":"提示",
							        		"contentName":"modal",
							        		"text":"确定要将"+scope.form.CASH_MONEY+"元提现？"
							      }
							      eventBusService.publish(controllerName,"appPart.load.modal",m2);
						    }
					});
				
					/*$httpService.post(config.updateCashURL, $scope.form).success(function(data) {
						
					}).error(function(data) {
						loggingService.info('更新提现记录错误');
					});*/
				}
				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				// 弹窗默认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					$httpService.post(config.updateCashURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data,
							}
						} else {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data,
								"toUrl" : "aps/content/ActingCustomerManagement/withdrawalManagement/presentRecord/config.json?fid=" + scope.form.fid
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
			}
		];
	});
}).call(this);

$(function() {
	
})