
	(function() {
		define(['slideleft'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					
					
					scope.input_value = ''
					
					
					// 餐桌区域数据源
					scope.tables_area_list = [
						{
							id : '用户名1',
							1 : '续费100.00',
							2 : '某某某餐厅',	//	5人/一桌
							3 : '提成10.00',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : '2016-05-01',
						},
						{
							id : '用户名2',
							1 : '代付200.00',
							2 : '某某某餐厅',	//	5人/一桌
							3 : '提成20.00',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : '2016-04-01',
						},
						{
							id : '用户名3',
							1 : '续费100.00',
							2 : '某某某餐厅',	//	5人/一桌
							3 : '提成10.00',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : '2016-03-01',
						},
						{
							id : '用户名4',
							1 : '续费100.00',
							2 : '某某某餐厅',	//	5人/一桌
							3 : '提成10.00',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : '2016-02-01',
						}
					];
					
					
					
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