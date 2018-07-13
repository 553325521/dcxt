$(function() {})
	/*Array.prototype.indexOf = function(val) {
		for (var i = 0; i < this.length; i++) {
			if (this[i] == val)
				return i;
		}
		return -1;
	};
	Array.prototype.remove = function(val) {
		var index = this.indexOf(val);
		if (index > -1) {
			this.splice(index, 1);
		}
	};*/

	var settingApp = angular.module('addGoodsApp', []);
    
	settingApp.controller('addGoodsCtrl', function($scope, $http, $interval) {

		scope = $scope;
		// 定义页面标题
		scope.pageTitle = '商品分类';	
		//选择分类数据源
		scope.select_type=  [
			{
				title : "中餐",
				value : "001"
			},
			{
				title : "西餐",
				value : "002"
			},
			{
				title : "其他餐",
				value : "003"
			}
		];
		
		// 打印类型数据源
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
		
		scope.form.IS_START = 1;
		
		scope.form.SELECT_TYPE = scope.select_type[0].value;
		
		
		scope.doSave = function() {

			console.info("aa");
		}


		scope.reset = function() {
			scope.form = {};
			console.info("aa");
		}
  });