(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				$scope.pageTitle = config.pageTitle;
				scope = $scope;
				$scope.form = {};
				$scope.form.favourPK = params.pk;
				var baseStr = new Array;
				var shopStr = new Array;
				$scope.form.weekList = new Array;
				$scope.form.timeList = new Array;
				$scope.remarkLength = "0";
				scope.editFavourLoadMoreIsShow = "True";
				scope.editFavourIsShow = "False";
				$scope.reSet = function(){
					getUserInfo();
				}
				
				//监听是否选择优惠方式
				scope.$watch("form.is_favourable",function(newValue,oldValue, scope){
					console.info(newValue);
					if(newValue == "否"){
						scope.favourable_way_is_show = "False";
					}else{
							scope.favourable_way_is_show = "True";
					}
				});
				
				$scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				$scope.remarkChange = function(){
					$scope.remarkLength = $scope.form.remark.length;
				}
				
				$scope.doAdd = function(){
					$scope.form.start_time = $("#start_time").val();
					$scope.form.end_time = $("#end_time").val();
					$scope.form.time_week = "";
					$scope.form.time_time = "";
					//初始化有效时段数组
					scope.form.timePeriodArray = [];
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
					if(scope.form.period == 0){
						scope.form.timePeriodArray.push({"periodName":"不限时段"});
					}else if(scope.form.period == 1){
						scope.form.timePeriodArray.push({"periodName":"限星期"});
						scope.form.timePeriodArray.push({"timeDetail":$scope.form.time_week});
					}else{
						scope.form.timePeriodArray.push({"periodName":"限时段"});
						scope.form.timePeriodArray.push({"timeDetail":$scope.form.time_time});
					}
					scope.form.timePeriodStr = JSON.stringify(scope.form.timePeriodArray);
					console.log($scope.form);
					$httpService.post(config.editURL, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$.toast("修改成功!");
							var m2 = {
								"url" : "aps/content/advnceSet/BasicSetting/favourablePay/add/config.json?fid=" + params.fid,
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
				//初始化日期样式
				function initDateStyle(){
					$("#start_time").datetimePicker({title:"选择日期", toolbarCloseText : '确定',m:1});
					$("#end_time").datetimePicker({title:"选择日期",toolbarCloseText : '确定',m:1});
				}
				/*initDateStyle();*/
				/*初始化商铺选择数据源*/
				scope.shopArray = [];
				function comboboxInit() {
						$httpService.post(config.loadShopDataURL,scope.form).success(function(data){
							console.info(data.data);
							for(var i = 0;i < data.data.length;i++){
								scope.shopArray.push({title:data.data[i].SHOP_NAME,value:data.data[i].FK_SHOP});
								/*scope.shopArray.push({title:'测试',value:'fdsf1123'});*/
							}
							scope.$apply();
							console.info("shopArray:"+scope.shopArray);
							$("#d3").select({
						        title: "选择门店",
						        multi: true,
						        split:',',
						        closeText:'完成',
						        items:scope.shopArray,
						        onChange: function(d) {
						        	scope.form.SHOPID = d.values;
						        	scope.form.SHOP_NAME = d.titles;
						        	scope.$apply();
						        }
						      });
					    }).error(function(data){
					    	loggingService.info('获取测试信息出错');
					    });
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
							"url" : "aps/content/advnceSet/BasicSetting/favourablePay/history/config.json?fid=" + params.fid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				var init = function(){
					// 打印类型数据源
					$scope.printer_level = [
						{
							value : '周一 ',
							name : '周一 '
						},
						{
							value : '周二',
							name : '周二'
						},
						{
							value : '周三 ',
							name : '周三 '
						},
						{
							value : '周四 ',
							name : '周四 '
						},
						{
							value : '周五 ',
							name : '周五 '
						},
						{
							value :'周六 ',
							name : '周六 '
						},
						{
							value : '周日 ',
							name : '周日 '
						}
					];
					
					$scope.printer_level_2 = [
						{
							value : '上午 ',
							name : '上午 '
						},
						{
							value : '上午 ',
							name : '上午 '
						},
						{
							value : '晚上 ',
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
							console.info(data.data);
							$scope.form = data.data[0];
							$scope.form.favourPK = params.pk;
							if(scope.form.is_favourable == "否"){
								$scope.favourable_way_is_show = "False";
							}else{
								$scope.favourable_way_is_show = "True";
							}
							$scope.form.jifen = data.data[0].points;
							$("#SHOP_TYPE_2").val($scope.form.shop_name);
							$("#SHOP_TYPE_1").val($scope.form.rule_name);
							$("#start_time").val($scope.form.start_time);
							$("#end_time").val($scope.form.end_time);
							var periodArray = JSON.parse($scope.form.period);
							console.info("array");
							console.info(periodArray);
							var periodName = periodArray[0].periodName;
							$scope.form.weekList = [];
							$scope.form.timeList = [];
							if(periodName == "不限时段"){
								scope.form.period = "0";
							}else if(periodName == "限星期"){
								scope.form.period = "1";
								var weekStr = periodArray[1].timeDetail.split(",");
								for(var i=0; i<$scope.printer_level.length; i++){
									for(var j=0; j<weekStr.length; j++){
										if($scope.printer_level[i].value == weekStr[j]){
											$scope.printer_level[i].checked = true;
											$scope.form.weekList.push($scope.printer_level[i]);
										}
									}
								}
							}else{
								scope.form.period = "2";
								var timeStr = periodArray[1].timeDetail.split(",");
								for(var i=0; i<$scope.printer_level_2.length; i++){
									for(var j=0; j<timeStr.length; j++){
										if($scope.printer_level_2[i].value == timeStr[j]){
											$scope.printer_level_2[i].checked = true;
											$scope.form.timeList.push($scope.printer_level_2[i]);
										}
									}
								}
							}
							$scope.form.baseRule = $scope.baseRule;
							//$scope.form.myShop = $scope.myShop;
							if($scope.form.remark != undefined){
								$scope.remarkLength = $scope.form.remark.length;
							}

							$scope.form.userId = $scope.userId;
							$scope.xianTime($scope.form.period);
							$scope.editFavourIsShow = "True";
							scope.editFavourLoadMoreIsShow = "False";
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
							if(baseStr.length == 0){
								scope.favourWayIsShow = "False";
								scope.favourable_way_is_show = "False";
							}else{
								scope.favourWayIsShow = "True";
							}
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
				function initData(){
					initDateStyle();
					comboboxInit();
					getUserInfo();
				}
				initData();
			}
		];
	});
}).call(this);