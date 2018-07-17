


(function() {
	define([], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

	scope = $scope;

	
	// 定义页面标题
	scope.pageTitle = config.pageTitle
		
	//初始化form表单
	scope.form=[]
		
//	scope.form.TABLES_NUM = 2
	
	console.info("ggggg")
	console.info(scope.form.TABLES_NUM)
	
	
		$scope.$watch('scope.form.TABLES_NUM', function(newValue, oldValue) {
			console.info(newValue,oldValue)
//					if (newValue === oldValue) {
//						return;
//					}
//					if (newValue == 'all' || newValue == undefined) {
//						scope.form.KIND_OF_DISHES = false;
//						if (newValue == 'all') {
//							scope.noCheckArr = []
//						} else {
//							scope.noCheckArr = [ 1, 2, 3, 4, 5 ]
//						}
//					} else {
//						scope.form.KIND_OF_DISHES = true;
//
//						scope.noCheckArr = [ 1, 2, 3, 4, 5 ]
//					}
				}, true);


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

	scope.doSave = function(){
		console.info("测试成功")
		
		scope.form.TABLES_NUM = angular.element("#dw_select").get(0).value;
		scope.form.TABLES_PXXH = angular.element("#pxxh_select").get(0).value;
		
		console.info(scope.form)
	}
	
			}
			
			];
	});

}).call(this);



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

$(function() {
	comboboxInit()
})
