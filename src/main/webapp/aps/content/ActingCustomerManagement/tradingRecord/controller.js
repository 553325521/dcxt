
	(function() {
		define(['slideleft'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					
					
					scope.input_value = ''
					
					
					/*// 餐桌区域数据源
					scope.tables_area_list = [
						{
							id : '代付',
							1 : '支出',
							2 : '某某某餐厅',	
							3 : '-100.00',  
							4 : '大店版',	
							5 : '2016-05-01',
						},
						{
							id : '提成',
							1 : '收入',
							2 : '某某某餐厅',	
							3 : '+10.00',  
							4 : '小店版',	
							5 : '2016-05-01',
						},
						{
							id : '提现',
							1 : '支出',
							2 : '总部',	
							3 : '-220.00',  
							4 : '总部',	
							5 : '2016-05-01',
						},
						{
							id : '充值',
							1 : '收入',
							2 : '总部',	
							3 : '+500.00',  
							4 : '总部',	
							5 : '2016-05-01',
						},
						{
							id : '提成',
							1 : '收入',
							2 : '某某某餐厅',	
							3 : '+10.00',  
							4 : '小店版',	
							5 : '2016-05-01',
						},
						{
							id : '提成',
							1 : '收入',
							2 : '某某某餐厅',	
							3 : '+10.00',  
							4 : '小店版',	
							5 : '2016-05-01',
						}
					];*/
					scope.trading_Record_List = [];
					
					function initTradingRecord(){
						$httpService.post(config.fingTradingRecordURL, $scope.form).success(function(data) {
							if (data.code == '0000') {
								scope.trading_Record_List = data.data;
								console.info(scope.trading_Record_List);
								scope.$apply();
							} 
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}
					initTradingRecord();
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json",
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					
					//测试，可以删
					
					scope.addOrChange = function(a){
						console.info(a);
					}
			
					comboboxInit();
					
					function comboboxInit(){
						
						//清除按钮
						$(".weui_icon_clear").click(function(){
							console.info("5")
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