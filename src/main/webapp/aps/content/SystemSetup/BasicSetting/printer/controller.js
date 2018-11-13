(function() {
	define([ 'slideleft' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				// 定义页面标题
				scope.pageTitle = '打印机列表';

				//初始化 form 表单
				scope.form = {};

				scope.printerList = [];

				scope.goodType = [];

				var init = function() {
					$httpService.post(config.findURL, {}).success(function(data) {
						if (data.code != '0000') {
						} else {
							for (item in data.data) {
								if (data.data[item].PRINTER_LEVEL == '1') {
									data.data[item].PRINTER_LEVEL_NAME = '结算联'
								} else if (data.data[item].PRINTER_LEVEL == '2') {
									data.data[item].PRINTER_LEVEL_NAME = '对账联'
								} else if (data.data[item].PRINTER_LEVEL == '3') {
									data.data[item].PRINTER_LEVEL_NAME = '备物联'
								}
								if (data.data[item].PRINTER_DISHES != undefined) {
									data.data[item].PRINTER_DISHES_TEXT = data.data[item].PRINTER_DISHES
									for (index in scope.goodType) {
										data.data[item].PRINTER_DISHES_TEXT = (data.data[item].PRINTER_DISHES_TEXT).replace(scope.goodType[index].GTYPE_PK, scope.goodType[index].GTYPE_NAME)
									}
									data.data[item].PRINTER_DISHES_TEXT = (data.data[item].PRINTER_DISHES_TEXT).replace(/,/g, '/');
								}
							}
							scope.printerList = data.data;
							scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				var loadGoodType = function() {
					$httpService.post(config.findGoodTypeURL, {
						GTYPE_PID : 0
					}).success(function(data) {
						if (data.code != '0000') {
						} else {
							scope.goodType = data.data;
							init()
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				loadGoodType();

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