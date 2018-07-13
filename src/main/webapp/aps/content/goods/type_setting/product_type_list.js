$(function() {});

var settingApp = angular.module('Goods_Type_List_App', []);

settingApp.controller('Goods_Type_List_Ctrl', function($scope, $http, $interval) {

	scope = $scope;
	// 定义页面标题
	scope.pageTitle = '商品分类';	
	
	scope.type_list = [{
		order:1,
		name:'荤菜',
		isStart:'启用',
		area:'堂点'
	},{
		order:2,
		name:'素菜',
		isStart:'启用',
		area:'外卖'
	}];
	
});