(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				$scope.form = {};
				scope.addIsShow = "False";
				//如果已经设置过优惠买单去修改界面
				function initPage(){
					$httpService.post(config.checkFavourableExistURL, $scope.form).success(function(data) {
						if (data.code == '0000') {
							var preferntial_pk = data.data;
							var m2 = {
									"url" : "aps/content/advnceSet/BasicSetting/favourablePay/edit/config.json?fid=" + params.fid
										+"&pk="+preferntial_pk,
									"size" : "modal-lg",
									"contentName" : "content"
							}
							eventBusService.publish(controllerName, 'appPart.load.content', m2);
							scope.$apply();
						} else {
							scope.addIsShow = "True";
							$scope.pageTitle = config.pageTitle;
							//初始化有效时段数组
							scope.form.timePeriodArray = [];
							var baseStr = new Array;
							var shopStr = new Array;
							$scope.form.weekList = new Array;
							$scope.form.timeList = new Array;
							$scope.remarkLength = "0";
							//初始化是否开启
							scope.form.is_use = "0";
							//初始化适用范围
							scope.form.actor = "0";
							//有效时段初始化
							scope.form.period = "0";
							//是否选择优惠方式
							scope.form.is_favourable = "否";
							
							scope.favourable_way_is_show = "False";
							
							scope.favourWayIsShow = "False";
							
							//监听是否选择优惠方式
							scope.$watch("form.is_favourable",function(newValue,oldValue, scope){
								console.info(newValue);
								if(newValue == "否"){
									scope.favourable_way_is_show = "False";
								}else{
									scope.favourable_way_is_show = "True";
								}
							});
							
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
								$scope.form.fk_rule = "";
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
								$httpService.post(config.saveURL, $scope.form).success(function(data) {
									if (data.code === '0000') {
										$.toast("新增成功!");
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
							comboboxInit();
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
								$scope.xianTime("0");
							}
							var getYouHui = function(){
								$httpService.post(config.getBaseInfo, $scope.form).success(function(data) {
									if (data.code === '0000') {
										$scope.form.baseRule = data.data;
										
										for(var i=0; i<$scope.form.baseRule.length; i++){
											baseStr.push($scope.form.baseRule[i].rule_name);
										}
										console.log("getYh");
										console.log(baseStr);
										if(baseStr.length != 0){
											scope.favourWayIsShow = "True";
											scope.favourable_way_is_show = "True";
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
										//$scope.form.shopId = $scope.form.userInfo.shopId;
										console.log($scope.form);
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
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
					}
				 initPage();
				}
			
		];
	});
}).call(this);