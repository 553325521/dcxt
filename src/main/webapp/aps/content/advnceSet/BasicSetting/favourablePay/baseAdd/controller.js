(function() {
	define(['zepto','slideleft','jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				$scope.pageTitle = config.pageTitle;
				$scope.form = {};
				$scope.form.goodType = new Array();
				
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
						if (data.code === '0000') {
							$scope.toHref("welcome");
						} else {
							$scope.toHref("welcome");
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
					$(".rule_model_1").hide();
					$(".rule_model_2").hide();
					$(".rule_model_3").hide();
					$(".rule_model_"+pa).show();
					$scope.form.rule_model = pa;
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
							for(var i=0; i<$scope.goodType.length; i++){
								$scope.goodType[i].checked = false;
							}
							$scope.good_scope("0");
							$scope.$apply();
						} else {
							
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
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
					$scope.form.good_scope = "0";
					$scope.checkRuleModel('1');
					
					getGoodType();
					
				}
				init();
				
			}
		];
	});
}).call(this);