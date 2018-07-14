$(function() {})



var settingApp = angular.module('tablesApp', []);

settingApp.controller('tablesCtrl', function($scope, $http, $interval) {

	scope = $scope;


	// 定义页面标题
	scope.pageTitle = '区域分类'


	// 餐桌区域数据源
	scope.tables_area = [
		{
			value : 1,
			name : '大厅',
			num : 5,
			status : 1   //1代表已启用 	0代表已停用
		},
		{
			value : 2,
			name : '雅间',
			num : 8,
			status : 1
		},
		{
			value : 3,
			name : '小包间 ',
			num : 10,
			status : 0
		},
		{
			value : 4,
			name : '大包间',
			num : 15,
			status : 1
		}
	];




})