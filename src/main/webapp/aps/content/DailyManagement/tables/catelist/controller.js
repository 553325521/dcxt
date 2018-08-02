
(function() {
	define(['zepto','slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService','$rootScope',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService,$rootScope) {

				scope = $scope;
				scope.pageShow = "False";

				// 定义页面标题
				scope.pageTitle = config.pageTitle;

				// 餐桌区域数据源
				scope.tables_area_list = [];
				
				var init = function() {
					$httpService.post(config.findURL).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							scope.tables_area_list = data.data;
							scope.pageShow = "True";
							scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				init();
				
				scope.toHref = function(path,area_id) {
					console.info(scope.tables_area_list.length)
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?area_id="+area_id+"&area_num="+scope.tables_area_list.length,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				
				
				
				//删除按钮
				scope.tablesDelete = function(area_id){
					scope.del={}
					scope.del.area_id = area_id;
					var m2 = {
						"url" : "aps/content/DailyManagement/tables/catelist/config.json",
						"title" : "提示",
						"contentName" : "modal",
						"text" : "是否删除?"
					}
					eventBusService.publish(controllerName, 'appPart.load.modal', m2);
				}
				
				// 弹窗确认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					 $httpService.post(config.removeURL,scope.del).success(function(data){
						 if(data.code == "0000"){
							 scope.pageShow = "False";
							 init();
						 }
						
						var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : data.data
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
					//闭合滑块
		        	$('.slideleft_cell_bd').css('-webkit-transform', 'translateX(0px)');
				});
				
				
				
			}
		];
	});

}).call(this);