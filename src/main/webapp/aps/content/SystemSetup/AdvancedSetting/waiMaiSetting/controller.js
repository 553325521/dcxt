
(function() {
	define(['zepto','slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService','$rootScope',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService,$rootScope) {

				scope = $scope;
				scope.pageShow = "False";

				// 定义页面标题
				scope.pageTitle = config.pageTitle;
				
//				// 会员卡数据源
//				scope.vip_card_list = [];
//				
				var init = function(){
					$httpService.post(config.findURL).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							scope.pageShow = "True";
							scope.form = data.data;
							scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
					
				}
				
				init();
//				
				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid="+params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				
				var $form = $("#form");
				$form.form();
				//保存
				scope.doSave = function(){
					$form.validate(function(error) {
						if (!error) {
							//弹出保存询问
							var m2 = {
								"url" : "aps/content/SystemSetup/AdvancedSetting/waiMaiSetting/config.json",
								"title" : "提示",
								"contentName" : "modal",
								"text" : "是否保存?"
							}
							eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						}
					});
				}

				
				
				// 弹窗确认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					 $httpService.post(config.saveURL,scope.form).success(function(data){
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
//		        	$('.slideleft_cell_bd').css('-webkit-transform', 'translateX(0px)');
				});
				
				
				//页面调用jssdk之前所必须的config配置
				var initConfig = function(data){
					wx.config({
//						debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
					    appId: data.appId, // 必填，公众号的唯一标识
					    timestamp: data.timestamp,// 必填，生成签名的时间戳
					    nonceStr: data.noncestr, // 必填，生成签名的随机串
					    signature: data.signature,// 必填，签名
					    jsApiList: ['getLocation'] // 必填，需要使用的JS接口列表
					});

					wx.error(function(res){
						console.info("config失败")
						console.info(res)
					});
				}
				
				//获取位置信息
				scope.getLocation = function(){
					wx.getLocation({
						type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
						success: function (res) {
							var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
							var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
							scope.form.WM_PSQY = "经:"+latitude+"  纬:"+longitude;
							scope.$apply();
						}
					});
				}
			}
		];
	});

}).call(this);