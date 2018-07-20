
	(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					// 定义页面标题
					scope.pageTitle = '商品设置';
					
					scope.selectArray = ['荤菜', '素菜', '豆类' ];
					
					scope.selectUnit = ['份','盘','个'];
					
					scope.selectGoods = ['青椒肉丝','鱼香肉丝'];
					
					scope.show_area = [
						{
							value : 1,
							name : '堂点',
							checked: true
						},
						{
							value : 2,
							name : '外卖',
							checked: false
						},
						{
							value : 3,
							name : '预订',
							checked: false
						}
					];
					
					//初始化form表单
					scope.form = {};
					
					scope.form.SELECTTYPE = scope.selectArray[1];
					
					scope.form.SELECTUNIT = scope.selectUnit[1];
					
					scope.form.SELECTGOODS = scope.selectGoods[1];
					
					/*angular.element("#fs_select").get(0).value = scope.form.SELECTTYPE;*/
					$("#fs_select").val(scope.selectArray[1]);
					$("#unit_select").val(scope.selectUnit[1]);
					$(".goods_select").val(scope.selectGoods[1]);
					function comboboxInit() {	
						$("#fs_select").picker({
							title : "选择分类",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values : scope.selectArray,
									displayValues : scope.selectArray
								}
							]
						});
						$("#unit_select").picker({
							title : "选择单位",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values : scope.selectUnit,
									displayValues : scope.selectUnit
								}
							]
						});
						$.each($(".goods_select"),function(i,n){
							$(this).picker({
								title : "选择商品",
								toolbarCloseText : '确定',
								cols : [
									{
										textAlign : 'center',
										values : scope.selectGoods,
										displayValues : scope.selectGoods
									}
								]
							});
						});
					}
					comboboxInit();
				}
			];
		});
	}).call(this);

	$(function() {
	})