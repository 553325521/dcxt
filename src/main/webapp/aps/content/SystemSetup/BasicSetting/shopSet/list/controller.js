(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				$scope.pageTitle = config.pageTitle;
				$scope.form = {};
				
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
					$scope.form.SHOP_TYPE_1 = "中餐";
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
					$scope.form.SHOP_SUPPORT = "输入相关配套，如免费停车场、WIFI";
				}
				init();
				
			}
		];
	});
}).call(this);