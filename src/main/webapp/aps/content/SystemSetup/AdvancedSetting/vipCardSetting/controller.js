
(function() {
	define(['zepto','slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService','$rootScope',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService,$rootScope) {

				scope = $scope;
				scope.pageShow = "False";

				// 定义页面标题
				scope.pageTitle = config.pageTitle;
				
				// 会员卡数据源
				scope.vip_card_list = [];
				
				var init = function() {
					$httpService.post(config.findURL).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							scope.vip_card_list = data.data;
							scope.pageShow = "True";
							scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				init();
				
				scope.toHref = function(path,vcard_id) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?vcard_id="+vcard_id+"&fid="+params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				
				
				
				//删除按钮
				scope.vipCardDelete = function(vcard_id){
					scope.del={}
					scope.del.VCARD_ID = vcard_id;
					var m2 = {
						"url" : "aps/content/SystemSetup/AdvancedSetting/vipCardSetting/config.json",
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