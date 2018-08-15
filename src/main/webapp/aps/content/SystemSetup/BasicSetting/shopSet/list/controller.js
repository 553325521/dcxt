(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				$scope.pageTitle = config.pageTitle;
				$scope.form = {};
				$scope.fenzi = 0;
				$scope.fenmu = 1000;
				
				$scope.updateNum = function(){
					$scope.fenzi = $scope.form.SHOP_REMARK.length;
					//$scope.fenmu = 1000 - $scope.fenzi;
				}
				
				$scope.doSave = function(){
					$scope.form.SHOP_TYPE = $("#SHOP_TYPE_1").val() + " " + $("#SHOP_TYPE_2").val();
					$scope.form.SHOP_AREA = $("#SHOP_AREA_1").val() + " " + $("#SHOP_AREA_2").val() + " " + $("#SHOP_AREA_3").val();
					if($scope.form.SHOP_REMARK != undefined && $scope.form.SHOP_REMARK.length > 1000){
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
					});
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
								values : [ '中餐', '西餐' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								if (value == '中餐') {
									$scope.form.SHOP_TYPE_1_VALUE = '1'
								} else if (value == '西餐') {
									$scope.form.SHOP_TYPE_1_VALUE = '2'
									
								}
							}
						}
					});
					
					$("#SHOP_TYPE_2").picker({
						title : "商铺类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '火锅', '鲁菜','湘菜' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								if (value == '火锅') {
									$scope.form.SHOP_TYPE_2_VALUE = '1'
								} else if (value == '鲁菜') {
									$scope.form.SHOP_TYPE_2_VALUE = '2'
								}else if (value == '湘菜') {
									$scope.form.SHOP_TYPE_2_VALUE = '3'
								}
							}
						}
					});

					$("#SHOP_AREA_1").picker({
						title : "省",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '山东省', '北京市','上海市' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								
							}
						}
					});
					
					$("#SHOP_AREA_2").picker({
						title : "市",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '潍坊市', '青岛市','济南市' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								
							}
						}
					});
					
					$("#SHOP_AREA_3").picker({
						title : "区",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '潍城区', '高新区','奎文区' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								
							}
						}
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