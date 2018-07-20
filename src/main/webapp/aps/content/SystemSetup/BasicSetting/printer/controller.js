(function() {
	define([ 'slideleft' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

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


				//初始化 form 表单
				scope.form = {};

				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
			}
		];
	});
}).call(this);