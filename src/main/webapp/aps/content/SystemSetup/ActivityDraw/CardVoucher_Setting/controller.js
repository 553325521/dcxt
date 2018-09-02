
	(function() {
		define([], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					scope.pageShow = "False";
					//初始化form表单
					scope.form = {},
					// 定义页面标题
					scope.pageTitle = '卡券设置';	
					
					scope.CardVoucher_LIST = [{
						ACTIVITY_NAME:"活动1",
						ISSTART:"开启",
						DESCRIPTION:"券面20元/预发1000张/消费200元可用/自动发送开启"
					},{
						ACTIVITY_NAME:"活动2",
						ISSTART:"关闭",
						DESCRIPTION:"券面20元/预发1000张/消费200元可用/自动发送开启"
					},{
						ACTIVITY_NAME:"活动3",
						ISSTART:"关闭",
						DESCRIPTION:"券面20元/预发1000张/消费200元可用/自动发送开启"
					}];
					
					scope.form.fid = params.fid;

					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fid=" + scope.form.fid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					var init = function() {
					/*	$httpService.post(config.findURL, scope.form).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else {
								scope.goods_list = data.data;
								scope.pageShow = "True";
								scope.$apply();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});*/
						scope.pageShow = "True";
					}
					
					init();
					
					//删除按钮
					/*scope.tablesDelete = function(goods_id){
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
					*/
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