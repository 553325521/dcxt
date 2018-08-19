(function() {
	define(['jqueryweui','pickercity'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				$scope.pageTitle = config.pageTitle;
				$scope.form = {};
				$scope.fenzi = 0;
				$scope.fenmu = 1000;
				
				//初始化店铺类型
				shop_type = {
					"中餐":["川菜", "鲁菜","湘菜"],
					"西餐":["西餐1","西餐2","西餐3"]
				}
				
				//初始化店铺类型
				shop_type_first = [];
				for(var key in shop_type){
					shop_type_first.push(key)
				}
				
				scope.form.SHOP_TYPE_1 = shop_type_first[0]
				
				//刷新第二个下拉框
				var flushShopType2 = function(){
					$("#SHOP_TYPE_2").picker({
						title : "商铺类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : shop_type[scope.form.SHOP_TYPE_1]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								scope.form.SHOP_TYPE_2 = value
							}
						}
					});
					$("#SHOP_TYPE_2").val(shop_type[scope.form.SHOP_TYPE_1][0])
				} 
				
				
				
				flushShopType2(shop_type[shop_type_first[0]])
				
				$scope.updateNum = function(){
					$scope.fenzi = $scope.form.SHOP_REMARK.length;
					//$scope.fenmu = 1000 - $scope.fenzi;
				}
				
				
				
				$scope.doSave = function(){
//					$scope.form.SHOP_TYPE = scope.form.SHOP_TYPE_1 + " " + scope.form.SHOP_TYPE_2;
//					$scope.form.SHOP_AREA = $("#SHOP_AREA_1").val() + " " + $("#SHOP_AREA_2").val() + " " + $("#SHOP_AREA_3").val();
					console.info(scope.form)
					/*if($scope.form.SHOP_REMARK != undefined && $scope.form.SHOP_REMARK.length > 1000){
						return;
					}
					$httpService.post(config.saveShopInfoURL, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.toHref("welcome");
						} else {
							$scope.toHref("welcome");
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});*/
				}
				
				$scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				
			
				
				var comboboxInit = function() {
					$("#SHOP_TYPE_1").picker({
						title : "商铺类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : shop_type_first
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								scope.form.SHOP_TYPE_1 = value
								flushShopType2(shop_type[value]);
							}
						}
					});
					

					$("#ssx").cityPicker({
				        title: "选择省市县",
				        toolbarCloseText : '完成'
				     });
				}
				comboboxInit();
				
				
				var init = function(){
					/*$scope.form.SHOP_TYPE_1 = "中餐";
					$scope.form.SHOP_TYPE_1_VALUE = '1';
					
					$scope.form.SHOP_TYPE_2 = "火锅";
					$scope.form.SHOP_TYPE_2_VALUE = '1';
					
					$scope.form.SHOP_AREA_1 = "山东省";
					$scope.form.SHOP_AREA_1_VALUE = '1';
					
					$scope.form.SHOP_AREA_2 = "潍坊市";
					$scope.form.SHOP_AREA_2_VALUE = '1';
					
					$scope.form.SHOP_AREA_3 = "潍城区";
					$scope.form.SHOP_AREA_3_VALUE = '1';
					
					$scope.form.SHOP_GPS = "自动定位商铺位置";
					$scope.form.SHOP_ADDRESS = "输入详细地址街/门牌号";
					$scope.form.SHOP_TIME = "输入营业时间";
					$scope.form.SHOP_TEL = "输入联系电话";
					$scope.form.SHOP_SUPPORT = "输入相关配套，如免费停车场、WIFI";*/
					
					//获取用户商铺信息
					$httpService.post(config.getShopInfoURL, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.form = data.data[0];
							var typeStr = data.data[0].SHOP_TYPE.split(" ");
							$scope.form.SHOP_TYPE_1 = typeStr[0];
							$scope.form.SHOP_TYPE_2 = typeStr[1];
							
							var shopArea = data.data[0].SHOP_AREA.split(" ");
							$scope.form.SHOP_AREA_1 = shopArea[0];
							$scope.form.SHOP_AREA_2 = shopArea[1];
							$scope.form.SHOP_AREA_3 = shopArea[2];
							
							$scope.form.SHOP_GPS = data.data[0].SHOP_ADDRESS;
							console.log($scope.form);
							$scope.$apply();
						} else {
							
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
					
				}
				init();
				
			}
		];
	});
}).call(this);