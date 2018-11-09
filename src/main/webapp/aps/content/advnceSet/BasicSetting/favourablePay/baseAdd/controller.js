(function() {
	define(['zepto','slideleft','jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				
				scope = $scope;
				
				$scope.pageTitle = config.pageTitle;
				
				//初始化折扣、固减、随减显示
				scope.zk_yh = "True";
				scope.gdmj = "False";
				scope.sjmj = "False";
				
				$scope.form = {};
				//初始化备注说明
				scope.form.remark = "";
				//是否启用初始化
				$scope.form.is_use = 0;
				//优惠方式初始化
				$scope.form.yh_way = "折扣优惠";
				//当优惠方式为折扣优惠的数组
				scope.zkyh_array = [{"zk_smallmoney":"","zk_bigmoney":"","zk_discount":""}];
				//当优惠方式为固定满减的数组
				scope.gd_array = [{"gd_smallmoney":"","gd_bigmoney":"","gd_jmoney":""}];
				//当优惠方式为随机满减的数组
				scope.sj_array = [{"sj_smallmoney":"","sj_bigmoney":"","sj_jsmallmoney":"","sj_jbigmoney":""}];
				//监听优惠方式选择值
				$scope.$watch('form.yh_way', function(newValue, oldValue) {
					if (newValue === '折扣优惠') {
						scope.zk_yh = "True";
						scope.gdmj = "False";
						scope.sjmj = "False";
						scope.zkyh_array = [{"zk_smallmoney":"","zk_bigmoney":"","zk_discount":""}];
					}else if(newValue === '固定满减'){
						scope.zk_yh = "False";
						scope.gdmj = "True";
						scope.sjmj = "False";
						scope.gd_array = [{"gd_smallmoney":"","gd_bigmoney":"","gd_jmoney":""}];
					}else{
						scope.zk_yh = "False";
						scope.gdmj = "False";
						scope.sjmj = "True";
						scope.sj_array = [{"sj_smallmoney":"","sj_bigmoney":"","sj_jsmallmoney":"","sj_jbigmoney":""}];
					}
				})
				//添加优惠方式的元素
				scope.add = function(){
					if(scope.form.yh_way == '折扣优惠'){
						scope.zkyh_array.push({"zk_smallmoney":"","zk_bigmoney":"","zk_discount":""});
					}else if(scope.form.yh_way == '固定满减'){
						scope.gd_array.push({"gd_smallmoney":"","gd_bigmoney":"","gd_jmoney":""})
					}else{
						scope.sj_array.push({"sj_smallmoney":"","sj_bigmoney":"","sj_jsmallmoney":"","sj_jbigmoney":""})
					}
				}
				scope.aa = function(){
					console.info("aa");
				}
				//删除优惠方式的元素
				scope.del = function(index){
					if(scope.form.yh_way == '折扣优惠'){
						scope.zkyh_array.splice(index,1);
					}else if(scope.form.yh_way == '固定满减'){
						scope.gd_array.splice(index,1);
					}else{
						scope.sj_array.splice(index,1);
					}
				}
				
				$scope.form.goodType = [];
				
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
					if(pa == "全部商品"){
						$(".goodType").hide();
						$scope.form.goodType = [];
					}else{
						$(".goodType").show();
						$scope.form.goodType = [];
					}
				}
				
				$scope.doAdd = function(){
					//初始化最后的优惠方式数组
					scope.form.yh_array = [];
					//初始化最后的商品范围数组
					scope.form.goods_area = [];
					scope.form.yh_array.push({"YH_WAY":scope.form.yh_way});
					scope.form.goods_area.push({"GOODS_AREA":scope.form.good_scope});
					scope.form.goods_area.push({"AREA_DETAIL":scope.form.goodType});
					if(scope.form.yh_way == '折扣优惠'){
						scope.form.yh_array.push({"WAY_DETAIL":scope.zkyh_array});
					}else if(scope.form.yh_way == '固定满减'){
						scope.form.yh_array.push({"WAY_DETAIL":scope.gd_array});
					}else{
						scope.form.yh_array.push({"WAY_DETAIL":scope.sj_array});
					}
					scope.form.goods_area = JSON.stringify($scope.form.goods_area);
					$scope.form.yh_array = JSON.stringify($scope.form.yh_array);
					
					console.log($scope.form);
					
					$httpService.post(config.saveYouhuimaidan, $scope.form).success(function(data) {
						if (data.code === '0000') {
							var m2 = {
									"url" : "aps/content/advnceSet/BasicSetting/favourablePay/baseList/config.json",
									"contentName" : "content"
								}
								eventBusService.publish(controllerName, 'appPart.load.content', m2);
								$scope.$apply();
							//$scope.toHref("advnceSet/BasicSetting/favourablePay/baseList");
							//$scope.reset();
						} else {
							$scope.form = {};
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				$scope.reset = function(){
					$scope.form = {};
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
							$scope.good_scope("全部商品");
							$scope.$apply();
						} else {
							
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				var init = function(){
					$scope.form.good_scope = "全部商品";
					$scope.checkRuleModel('1');
					getGoodType();
					
				}
				init();
				
			}
		];
	});
}).call(this);