(function() {
	define(['zepto','slideleft','jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				$scope.pageTitle = config.pageTitle;
				$scope.form = {};
				$scope.form.rulePk = params.pk;
				$scope.form.goodType = new Array();
				
				$scope.doEdit = function(){
					$scope.form.rulePk = params.pk;
					$scope.form.goodType = JSON.stringify($scope.form.goodType);
					console.log($scope.form);
					
					$httpService.post(config.editYouhuimaidan, $scope.form).success(function(data) {
						console.log(data.code === '0000');
						if (data.code === '0000') {
							//alert("添加成功");
							$.toast("修改成功!");
							var m2 = {
									"url" : "aps/content/advnceSet/BasicSetting/favourablePay/baseList/config.json",
									"contentName" : "content"
								}
								eventBusService.publish(controllerName, 'appPart.load.content', m2);
								$scope.$apply();
							//$scope.toHref("advnceSet/BasicSetting/favourablePay/baseList");
							//$scope.reset();
						} else {
							//alert("请重新填写完整");
							$scope.form = {};
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				$scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				$scope.selectType = function(item){
						var action = (item.checked ? 'add' : 'remove');
						if (action == "add") {
							$scope.form.goodType.push(item);
							
						} else {
							$scope.form.goodType.remove(item);
							
						}
				}
				
				$scope.good_scope = function(pa){
					if(pa == "0"){
						$(".goodType").hide();
						$scope.form.goodType = $scope.goodType.slice(0);
					}else{
						$(".goodType").show();
						$scope.form.goodType = [];
					}
				}
				
				$scope.doAdd = function(){
					$scope.form.goodType = JSON.stringify($scope.form.goodType);
					console.log($scope.form);

					$httpService.post(config.saveYouhuimaidan, $scope.form).success(function(data) {
						console.log(data.code === '0000');
						if (data.code === '0000') {
							//alert("添加成功");
							$.toast("删除成功!");
							var m2 = {
									"url" : "aps/content/advnceSet/BasicSetting/favourablePay/baseList/config.json",
									"contentName" : "content"
								}
								eventBusService.publish(controllerName, 'appPart.load.content', m2);
								$scope.$apply();
							//$scope.toHref("advnceSet/BasicSetting/favourablePay/baseList");
							//$scope.reset();
						} else {
							//alert("请重新填写完整");
							$scope.form = {};
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				$scope.isUser = function(pa){
					$scope.form.is_use = pa;
				}
				
				$scope.reset = function(){
					$scope.form = {};
				}
				
				$scope.remarkChange = function(){
					$scope.remarkNum = $scope.form.remark.length;
				}
				
				$scope.checkRuleModel = function(pa){
					console.log(pa);
					$(".rule_model_1").hide();
					$(".rule_model_2").hide();
					$(".rule_model_3").hide();
					$(".rule_model_"+pa).show();
					$scope.form.rule_model = pa;
				}
				
				var getRuleInfo = function(){
					$httpService.post(config.getBaseInfoURL, $scope.form).success(function(data) {
						console.log(data.code === '0000');
						if (data.code === '0000') {
							$scope.form = data.data[0];
							$scope.form.goodType = new Array();
							console.log($scope.form);
							
							
							
							$scope.checkRuleModel($scope.form.rule_model);
							$scope.form.yh_zkxf = $scope.form.rule_model_first;
							$scope.form.yh_gdxf = $scope.form.rule_model_first;
							$scope.form.yh_sjxf = $scope.form.rule_model_first;
							
							$scope.form.yh_zkyh = $scope.form.rule_model_second;
							$scope.form.yh_gdj = $scope.form.rule_model_second;
							$scope.form.yh_sjj = $scope.form.rule_model_second;
							
							if($scope.form.rule_model == "1"){
								$scope.form.yhfs_zkyh = "0";
							}else if($scope.form.rule_model == "2"){
								$scope.form.yhfs_gdmj = "0";
							}else if($scope.form.rule_model == "3"){
								$scope.form.yhfs_sjmj = "0";
							}
							
							if($scope.form.good_scope == "0"){
								for(var i=0; i<$scope.goodType.length; i++){
									$scope.goodType[i].checked = true;
								}
							}else{
								$scope.form.rulePk = params.pk;
								$httpService.post(config.getGoodTypeForRule, $scope.form).success(function(data) {
									if (data.code === '0000') {
										for(var i=0; i<$scope.goodType.length; i++){
											for(var j=0; j<data.data.length; j++){
												console.log($scope.goodType[i].GTYPE_PK+","+data.data[j].fk_goodtype);
												console.log($scope.goodType[i].GTYPE_PK == data.data[j].fk_goodtype);
												if($scope.goodType[i].GTYPE_PK == data.data[j].fk_goodtype){
													$scope.goodType[i].checked = true;
													$scope.form.goodType.push($scope.goodType[i]);
												}
											}
										}
										$scope.$apply();
									} 
								}).error(function(data) {
									loggingService.info('获取测试信息出错');
								});
							}
							
							$scope.$apply();
						} else {
							
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				$scope.toHistory = function(){
					var m2 = {
							"url" : "aps/content/advnceSet/BasicSetting/favourablePay/history/config.json",
							"size" : "modal-lg",
							"contentName" : "content"
						}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				var getGoodType = function(){
					$scope.form.GTYPE_PID = "0";
					$httpService.post(config.getGoodType, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.goodType = data.data;
							//$scope.good_scope("0");
							getRuleInfo();
							$scope.$apply();
						} else {
							
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				var init = function(){
					console.log("baseEdit");
					
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
					
					$("#SHOP_TYPE_1").picker({
						title : "优惠方式",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '请选择优惠方式','啊啊啊', '啥啥啥','对对对' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								
							}
						}
					});
					
					$("#SHOP_TYPE_2").picker({
						title : "适用门店",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '请选择适用门店','啊啊啊', '啥啥啥','对对对' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								
							}
						}
					});
					
					$scope.form.SHOP_TYPE_1 = "请选择优惠方式";
					$scope.form.SHOP_TYPE_1_VALUE = '1';
					
					$scope.form.SHOP_TYPE_2 = "请选择适用门店";
					$scope.form.SHOP_TYPE_2_VALUE = '1';
					
					getGoodType();
					
				}
				init();
				
			}
		];
	});
}).call(this);