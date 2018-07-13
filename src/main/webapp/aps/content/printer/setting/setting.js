$(function() {
	comboboxInit()
})

var settingApp = angular.module('settingApp', []);

settingApp.controller('settingCtrl', function($scope, $http, $interval) {

	scope = $scope;
	
	scope.pageTitle = '打印机设置'
	
	scope.printer_level = [
		{
			value : 1,
			name : '结算联 '
		},
		{
			value : 2,
			name : '对账联'
		},
		{
			value : 3,
			name : '备物联 '
		}
	];

	scope.printer_level_selected = '';

	//初始化 form 表单
	scope.form = {};

	scope.fsDoReduce = function() {

		if ($('#sd_select').attr('data-values') != undefined) {
			scope.form.PRINTER_SPEED = $('#sd_select').attr('data-values')
		}

		console.info(scope.form)
	}

	scope.doAdd = function() {

		if ($('#sd_select').attr('values') != undefined) {
			scope.form.PRINTER_SPEED = $('#sd_select').attr('values')
		}

		console.info(scope.form)
	}

	$scope.selectLevel = function(item) {
		console.info(item.checked)
		printer_level_selected = item
		if (scope.printer_level_selected == '') {
			scope.printer_level_selected = item;
		} else if (scope.printer_level_selected != item) {
			item.checked = false;
		}
		console.info(scope.printer_level_selected)
	}
})


function fsDoAdd() {
	console.info($('#fsQuantity').val())
}

function comboboxInit() {
	$("#sd_select").select({
		title : "选择速度",
		items : [
			{
				title : "低速",
				value : "001",
			},
			{
				title : "中速",
				value : "002",
			},
			{
				title : "快速",
				value : "003",
			}
		]
	});
}