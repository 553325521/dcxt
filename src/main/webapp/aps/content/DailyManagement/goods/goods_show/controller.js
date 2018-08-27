
	(function() {
		define([], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					scope.pageShow = "False";
					//初始化form表单
					scope.form = {},
					scope.form.GTYPE_ID = params.GTYPE_PK;
					// 定义页面标题
					scope.pageTitle = '商品列表';	
					scope.goods_list = [];
					
					scope.Last_Array = "";
					
					if(params.Last_Page == undefined || params.Last_Page == "undefined"){
						scope.Last_Array = params.Last_Array;
					}else{
						scope.Last_Array = params.Last_Page.split(",");
						scope.Last_Array.pop();
					}
					var init = function() {
						$httpService.post(config.findURL, scope.form).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else {
								scope.goods_list = data.data;
								scope.pageShow = "True";
								scope.$apply();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}
					
					init();
					
					/*返回按钮点击事件*/
					scope.returnClick = function(){
						
						var m2 = {
								"url" : "aps/content/DailyManagement/goods/config.json?fromUrl=" + config.currentUrl +"&gtype_id=" + params.GTYPE_PK+"&goods_count="+scope.goods_list.length+"&Last_Page="+scope.Last_Array,
								"size" : "modal-lg",
								"contentName" : "content"
							}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					scope.toHref = function(path,goods_id) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fromUrl=" + config.currentUrl + "&goods_id=" + goods_id+"&gtype_id=" + params.GTYPE_PK+"&goods_count="+scope.goods_list.length+"&Last_Array="+scope.Last_Array,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					
					
					
					//删除按钮
					scope.tablesDelete = function(goods_id){
						console.info(goods_id)
						scope.del={}
						scope.del.GOODS_ID = goods_id;
						var m2 = {
							"url" : "aps/content/DailyManagement/goods/goods_show/config.json",
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

	$(function() {
	})