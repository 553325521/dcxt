$(function() {
	comboboxInit()
})



var settingApp = angular.module('tablesCateSetApp', []);

settingApp.controller('tablesCateSetCtrl', function($scope, $http, $interval) {

	scope = $scope;


	// 定义页面标题
	scope.pageTitle = '区域分类'


	// 餐桌区域数据源
	scope.tables_area = [
		{
			id : 1,
			name : '大厅',
			count : 5,	//	5人/一桌
			status : 1   //	1代表已启用 	0代表已停用
		},
		{
			id : 2,
			name : '雅间',
			count : 8,
			status : 1
		},
		{
			id : 3,
			name : '小包间 ',
			count : 10,
			status : 0
		},
		{
			id : 4,
			name : '大包间',
			count : 15,
			status : 1
		}
	];




})



function comboboxInit() {
	
	$("#dw_select").picker({
		title : "选择单位",
		toolbarCloseText : '确定',
		cols : [
			{
				textAlign : 'center',
				values : [ '1', '2', '3' ],
				displayValues : [ '1', '2', '3' ]
			}
		]
	});
	
	$("#pxxh_select").picker({
		title : "排序序号",
		toolbarCloseText : '确定',
		cols : [
			{
				textAlign : 'center',
				values : [ '1', '2', '3' ],
				displayValues : [ '1', '2', '3' ]
			}
		]
	});
}
