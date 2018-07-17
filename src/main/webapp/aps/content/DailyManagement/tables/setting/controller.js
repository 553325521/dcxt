


(function() {
	define([], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

	scope = $scope;


	// 定义页面标题
	scope.pageTitle = '餐桌设置'



			}
			];
	});



}).call(this);





function comboboxInit() {


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
	
	$("#qy_select").picker({
		title : "选择区域",
		toolbarCloseText : '确定',
		cols : [
			{
				textAlign : 'center',
				values : [ '大厅', '雅间', '小包间', '大包间'],
				displayValues : [ '大厅', '雅间', '小包间', '大包间']
			}
		]
	});
	
	$("#rs_select").picker({
		title : "餐位人数",
		toolbarCloseText : '确定',
		cols : [
			{
				textAlign : 'center',
				values : [ '1人', '2人', '3人' ],
				displayValues : [ '1人', '2人', '3人' ]
			}
		]
	});
	
}

$(function() {
	comboboxInit();
})
