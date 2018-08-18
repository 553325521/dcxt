(function() {
	define(['zepto','slideleft','jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				$scope.pageTitle = config.pageTitle;
				$scope.form = {};
				$scope.form.favourPK = params.pk;
				var baseStr = new Array;
				var shopStr = new Array;
				$scope.form.weekList = new Array;
				$scope.form.timeList = new Array;
				$scope.remarkLength = "0";
				
				$scope.reSet = function(){
					getUserInfo();
				}
				
				$scope.remarkChange = function(){
					$scope.remarkLength = $scope.form.remark.length;
				}
				
				$scope.doAdd = function(){
					$scope.form.start_time = $("#start_time").val();
					$scope.form.end_time = $("#end_time").val();
					$scope.form.time_week = "";
					$scope.form.time_time = "";
					for(var i=0; i<$scope.printer_level.length; i++){
						if($scope.printer_level[i].checked == true){
							$scope.form.time_week = $scope.form.time_week + "," + $scope.printer_level[i].value;
						}
					}
					$scope.form.time_week = $scope.form.time_week.substring(1,$scope.form.time_week.length);
					
					for(var i=0; i<$scope.printer_level_2.length; i++){
						if($scope.printer_level_2[i].checked == true){
							$scope.form.time_time = $scope.form.time_time + "," + $scope.printer_level_2[i].value;
						}
						$scope.form.time_time = $scope.form.time_time + "," + $scope.printer_level_2[i].value;
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
					$httpService.post(config.editURL, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$.toast("修改成功!");
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
					console.log(pa);
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
				
				$scope.toHistory = function(){
					var m2 = {
							"url" : "aps/content/advnceSet/BasicSetting/favourablePay/history/config.json",
							"size" : "modal-lg",
							"contentName" : "content"
						}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				var init = function(){
					// 
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
					
					//$scope.xianTime("0");
				}
				
				var getListInfo = function(){
					$httpService.post(config.getFavourListURL, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.form = data.data[0];
							$scope.form.favourPK = params.pk;
							$scope.form.jifen = data.data[0].points;
							$("#SHOP_TYPE_2").val($scope.form.shop_name);
							$("#SHOP_TYPE_1").val($scope.form.rule_name);
							$("#start_time").val($scope.form.start_time);
							$("#end_time").val($scope.form.end_time);
							$scope.form.baseRule = $scope.baseRule;
							$scope.form.myShop = $scope.myShop;
							if($scope.form.remark != undefined){
								$scope.remarkLength = $scope.form.remark.length;
							}

							$scope.form.userId = $scope.userId;
							
							$scope.form.weekList = new Array;
							$scope.form.timeList = new Array;
							
							if($scope.form.period_week != undefined){
								var weekStr = $scope.form.period_week.split(",");
								for(var i=0; i<$scope.printer_level.length; i++){
									for(var j=0; j<weekStr.length; j++){
										if($scope.printer_level[i].value == weekStr[j]){
											$scope.printer_level[i].checked = true;
										}
									}
								}
							}
							
							if($scope.form.period_time != undefined){
								var timeStr = $scope.form.period_time.split(",");
								for(var i=0; i<$scope.printer_level_2.length; i++){
									for(var j=0; j<timeStr.length; j++){
										if($scope.printer_level_2[i].value == timeStr[j]){
											$scope.printer_level_2[i].checked = true;
										}
									}
								}
							}
							
							$scope.xianTime($scope.form.period);
							
							$scope.$apply();
						} else {
							
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				var getMyShop = function(){
					$httpService.post(config.getMyShop, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.form.myShop = data.data;
							$scope.myShop = data.data;
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
							$scope.baseRule = data.data;
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
							$scope.userId = $scope.form.userInfo.userId;
							//$scope.form.shopId = $scope.form.userInfo.shopId;
							console.log($scope.form);
							getMyShop();
							getYouHui();
							init();
							getListInfo();
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