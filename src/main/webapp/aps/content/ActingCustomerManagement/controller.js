
	(function() {
		define(['slideleft'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					
					
					scope.input_value = ''
					
					
					// 餐桌区域数据源
					scope.customer_information_list = [
						{
							id : '用户名1',
							1 : '268天',
							2 : '某某某餐厅',	//	5人/一桌
							3 : '王家军',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : 13883107775,
						},
						{
							id : '用户名1',
							1 : '268天',
							2 : '某某某餐厅',	//	5人/一桌
							3 : '王家军',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : 13883107775,
						},
						{
							id : '用户名1',
							1 : '268天',
							2 : '某某某餐厅',	//	5人/一桌
							3 : '王家军',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : 13883107775,
						},
						{
							id : '用户名1',
							1 : '268天',
							2 : '某某某餐厅某某某餐厅某某某餐厅',	//	5人/一桌
							3 : '王家军',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : 13883107775,
						},
						{
							id : '用户名1',
							1 : '268天',
							2 : '某某某餐厅',	//	5人/一桌
							3 : '王家军',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : 13883107775,
						},
						{
							id : '用户名1',
							1 : '268天',
							2 : '某某某餐厅',	//	5人/一桌
							3 : '王家军',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : 13883107775,
						},
						{
							id : '用户名1',
							1 : '268天',
							2 : '某某某餐厅',	//	5人/一桌
							3 : '王家军',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : 13883107775,
						},
						{
							id : '用户名1',
							1 : '268天',
							2 : '某某某餐厅',	//	5人/一桌
							3 : '王家军',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : 13883107775,
						},
						{
							id : '用户名1',
							1 : '268天',
							2 : '某某某餐厅啊',	//	5人/一桌
							3 : '王家军',   //	1代表已启用 	0代表已停用
							4 : '大店版',	//排序序号
							5 : 13883107775,
						}
					];
					
					
					scope.toHref = function(path,shopid) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fromUrl=" + config.currentUrl + "&shopid=" + shopid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
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