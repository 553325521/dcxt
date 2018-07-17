

(function() {
	define([], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

	scope = $scope;


	// 定义页面标题
	scope.pageTitle = '餐桌设置'


	// 餐桌区域数据源
	scope.tables_list = [
		{
			id : 1,
			num : "01",	//桌子编号
			status : 1,   //1代表已启用 	0代表已停用,
			count : 5	//5人/一桌
			
		},
		{
			id : 2,
			num : "02",	//桌子编号
			status : 1,   //1代表已启用 	0代表已停用,
			count : 6	//5人/一桌
			
		},
		{
			id : 3,
			num : "03",	//桌子编号
			status : 1,   //1代表已启用 	0代表已停用,
			count : 6	//5人/一桌
			
		},
		{
			id : 4,
			num : "04",	//桌子编号
			status : 0,   //1代表已启用 	0代表已停用,
			count : 6	//5人/一桌
			
		},
		{
			id : 5,
			num : "05",	//桌子编号
			status : 1,   //1代表已启用 	0代表已停用,
			count : 10	//5人/一桌
			
		},
	];

			}
			];
	});


}).call(this);

$(function() {})