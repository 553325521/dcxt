
(function() {
	define([], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService','$rootScope',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService,$rootScope) {

				scope = $scope;
				scope.pageShow = "False";
				// 定义页面标题
				scope.pageTitle = config.pageTitle;
				scope.form = {}
				scope.all_goods = {};
				
				scope.form.CURRENT_CLICK = "";
				
				//点击事件
				scope.clickGoods = function(goods_id){
					if(scope.form.CURRENT_CLICK != goods_id){
						scope.form.CURRENT_CLICK = goods_id;
					}else{
						scope.form.CURRENT_CLICK = ""
					}
				}
				
				
				var init = function() {
					$httpService.post(config.findURL).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							scope.all_goods = data.data;
						}
						scope.pageShow = "True";
						scope.$apply();
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
				
				//估清按钮
				scope.goodsOut = function(){
					if(scope.form.CURRENT_CLICK === ""){
						return;
					}
					var m2 = {
						"url" : "aps/content/DailyManagement/goodsout/config.json",
						"title" : "提示",
						"contentName" : "modal",
						"text" : "是否估清?"
					}
					eventBusService.publish(controllerName, 'appPart.load.modal', m2);
				}
				
				// 弹窗确认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					 $httpService.post(config.updateURL,scope.form).success(function(data){
						 if(data.code == "0000"){
							 scope.all_goods = {};
							 //估清完清除当前点击对象
							 scope.form.CURRENT_CLICK = "";
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
				});
				
			}
		];
	});

}).call(this);