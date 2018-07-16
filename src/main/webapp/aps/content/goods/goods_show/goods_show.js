$(function() {

})	

	var settingApp = angular.module('GoodsShowApp', []);
    
	settingApp.controller('GoodsShowCtrl', function($scope, $http, $interval) {

		scope = $scope;
		// 定义页面标题
		scope.pageTitle = '商品设置';	
	
		scope.goods_list = [{
			order:1,
			name:'青椒肉丝',
			type:'单品',
			price:'20.00/份'
		},{
			order:2,
			name:'水煮肉片',
			type:'单品',
			price:'20.00/份'
		},{
			order:3,
			name:'泡脚猪肝',
			type:'单品',
			price:'20.00/份'
		}];
  });

