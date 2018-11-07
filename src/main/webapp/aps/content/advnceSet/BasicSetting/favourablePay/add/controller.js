(function() {
	define(['zepto','slideleft','jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				$scope.pageTitle = config.pageTitle;
				$scope.form = {};
				var baseStr = new Array;
				var shopStr = new Array;
				$scope.form.weekList = new Array;
				$scope.form.timeList = new Array;
				$scope.remarkLength = "0";
				
				$scope.reSet = function(){
					$scope.form = {};
				}
				
				$scope.remarkChange = function(){
					$scope.remarkLength = $scope.form.remark.length;
				}
				
				$scope.doAdd = function(){
					$scope.form.start_time = $("#start_time").val();
					$scope.form.end_time = $("#end_time").val();
					$scope.form.time_week = "";
					$scope.form.time_time = "";
					for(var i=0; i<$scope.form.weekList.length; i++){
						$scope.form.time_week = $scope.form.time_week + "," + $scope.form.weekList[i].value;
					}
					$scope.form.time_week = $scope.form.time_week.substring(1,$scope.form.time_week.length);
					
					for(var i=0; i<$scope.form.timeList.length; i++){
						$scope.form.time_time = $scope.form.time_time + "," + $scope.form.timeList[i].value;
					}
					$scope.form.time_time = $scope.form.time_time.substring(1,$scope.form.time_time.length);
					
					for(var i=0; i<$scope.form.baseRule.length; i++){
						if($("#SHOP_TYPE_1").val() == $scope.form.baseRule[i].rule_name){
							$scope.form.fk_rule = $scope.form.baseRule[i].preferential_rule_pk;
						}
					}
					
					for(var i=0; i<$scope.form.myShop.length; i++){
						if($("#SHOP_TYPE_2").val() == $scope.form.myShop[i].shop_name){
							$scope.form.fk_shop = $scope.form.myShop[i].fk_shop;
						}
					}
					console.log($scope.form);
					$httpService.post(config.saveURL, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$.toast("新增成功!");
							var m2 = {
								"url" : "aps/content/advnceSet/BasicSetting/favourablePay/list/config.json",
								"contentName" : "content"
							}
							eventBusService.publish(controllerName, 'appPart.load.content', m2);
							$scope.$apply();
						} else {
							
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				$scope.selectWeek = function(item){
					console.log(item);
					var action = (item.checked ? 'add' : 'remove');
					if (action == "add") {
						$scope.form.weekList.push(item);
						
					} else {
						$scope.form.weekList.remove(item);
						
					}
			}
				
				$scope.selectTime = function(item){
					var action = (item.checked ? 'add' : 'remove');
					if (action == "add") {
						$scope.form.timeList.push(item);
						
					} else {
						$scope.form.timeList.remove(item);
						
					}
			}
				
				$scope.xianTime = function(pa){
					if(pa == "0"){
						$(".time_week").hide();
						$(".time_time").hide();
					}else if(pa == "1"){
						$(".time_week").show();
						$(".time_time").hide();
					}else if(pa == "2"){
						$(".time_time").show();
						$(".time_week").hide();
					}
				}
				//跳转方法
				$scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				//初始化日期样式
				function initDateStyle(){
					$("#start_time").datetimePicker({title:"选择日期", toolbarCloseText : '确定',m:1});
					$("#end_time").datetimePicker({title:"选择日期",toolbarCloseText : '确定',m:1});
				}
				initDateStyle();
				
				$scope.toHistory = function(){
					var m2 = {
							"url" : "aps/content/advnceSet/BasicSetting/favourablePay/history/config.json",
							"size" : "modal-lg",
							"contentName" : "content"
						}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				var init = function(){
					// 打印类型数据源
					$scope.printer_level = [
						{
							value : 1,
							name : '周一 '
						},
						{
							value : 2,
							name : '周二'
						},
						{
							value : 3,
							name : '周三 '
						},
						{
							value : 4,
							name : '周四 '
						},
						{
							value : 5,
							name : '周五 '
						},
						{
							value : 6,
							name : '周六 '
						},
						{
							value : 7,
							name : '周日 '
						}
					];
					
					$scope.printer_level_2 = [
						{
							value : 1,
							name : '上午 '
						},
						{
							value : 2,
							name : '中午'
						},
						{
							value : 3,
							name : '晚上 '
						}
					];
					
					$scope.form.SHOP_TYPE_1 = "请选择优惠方式";
					$scope.form.SHOP_TYPE_1_VALUE = '1';
					
					$scope.form.SHOP_TYPE_2 = "请选择适用门店";
					$scope.form.SHOP_TYPE_2_VALUE = '1';
					
					$scope.xianTime("0");
				}
				
				var getMyShop = function(){
					$httpService.post(config.getMyShop, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.form.myShop = data.data;
							for(var i=0; i<$scope.form.myShop.length; i++){
								console.log($scope.form.myShop[i].shop_name);
								shopStr.push($scope.form.myShop[i].shop_name);
								console.log(shopStr);
							}
							console.log($scope.form);
							console.log(shopStr);
							$("#SHOP_TYPE_2").picker({
								title : "适用门店",
								toolbarCloseText : '确定',
								cols : [
									{
										textAlign : 'center',
										values : shopStr
									}
								],
								onChange : function(e) {
									if (e != undefined && e.value[0] != undefined) {
										var value = e.value[0]
										
									}
								}
							});
							
							$scope.$apply();
						} else {
							
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				var getYouHui = function(){
					$httpService.post(config.getBaseInfo, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.form.baseRule = data.data;
							
							for(var i=0; i<$scope.form.baseRule.length; i++){
								baseStr.push($scope.form.baseRule[i].rule_name);
							}
							console.log($scope.form);
							console.log(baseStr);
							
							$("#SHOP_TYPE_1").picker({
								title : "优惠方式",
								toolbarCloseText : '确定',
								cols : [
									{
										textAlign : 'center',
										values : baseStr
									}
								],
								onChange : function(e) {
									if (e != undefined && e.value[0] != undefined) {
										var value = e.value[0]
										
									}
								}
							});
							
							$scope.$apply();
						} else {
							
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				var getUserInfo = function(){
					$httpService.post(config.getUserInfo, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.form.userInfo = data.data;
							$scope.form.userId = $scope.form.userInfo.userId;
							//$scope.form.shopId = $scope.form.userInfo.shopId;
							console.log($scope.form);
							getMyShop();
							getYouHui();
							init();
							$scope.$apply();
						} else {
							
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				getUserInfo();
				
			}
		];
	});
}).call(this);