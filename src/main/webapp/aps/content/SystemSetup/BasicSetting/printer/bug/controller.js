$(function() {});

var List_App = angular.module('List_App', []);

List_App.controller('List_Ctrl', function($scope, $http, $interval) {

	scope = $scope;
	// 定义页面标题
	scope.pageTitle = '打印机列表';

	scope.type_list = [ {
		PRINTER_NAME : '打印机1', // 打印机名称
		PRINTER_PAGE_WIDTH : '58mm', // 打印机宽度
		PRINTER_LEVEL : '收银联', // 打印类型
		PRINTER_PAGE_NUMS : '1', // 打印份数
		PRINTER_DISHES : [] // 菜品
	}, {
		PRINTER_NAME : '打印机2', // 打印机名称
		PRINTER_PAGE_WIDTH : '58mm', // 打印机宽度
		PRINTER_LEVEL : '收银联', // 打印类型
		PRINTER_PAGE_NUMS : '2', // 打印份数
		PRINTER_DISHES : [] // 菜品
	}, {
		PRINTER_NAME : '打印机3', // 打印机名称
		PRINTER_PAGE_WIDTH : '80mm', // 打印机宽度
		PRINTER_LEVEL : '收银联', // 打印类型
		PRINTER_PAGE_NUMS : '3', // 打印份数
		PRINTER_DISHES : [] // 菜品
	} ];

});