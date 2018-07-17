
(function() {
	define([], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;


				// 定义页面标题
				scope.pageTitle = '区域分类'


				// 餐桌区域数据源
				scope.tables_area = [
					{
						id : 1,
						name : '大厅',
						count : 5,	//	5人/一桌
						status : 1,   //	1代表已启用 	0代表已停用
						pxxh : 1	//排序序号
					},
					{
						id : 2,
						name : '雅间',
						count : 8,
						status : 1,
						pxxh : 2
					},
					{
						id : 3,
						name : '小包间 ',
						count : 10,
						status : 0,
						pxxh : 3
					},
					{
						id : 4,
						name : '大包间',
						count : 15,
						status : 1,
						pxxh : 4
					}
				];
			}
		];
	});

}).call(this);