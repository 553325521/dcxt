
	(function() {
		define(['slideleft'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					
					
					scope.pageTitle = config.pageTitle;
					
					
					// 餐桌区域数据源
					scope.tables_area_list = [
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
						
						$(".weui-payselect-li").on('click',function(){
							$(this).children().addClass("weui-payselect-on");
							$(this).siblings().children().removeClass("weui-payselect-on");
							return false;
						})
					  
					
					}
				}
			];
		});
	}).call(this);

	$(function() {
		
	})