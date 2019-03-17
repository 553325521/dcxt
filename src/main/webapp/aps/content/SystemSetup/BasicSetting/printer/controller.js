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
								var printLever = data.data[item].PRINTER_LEVEL;
								if (printLever != undefined) {
									if (printLever.indexOf('1') >= 0) {
										printLever = printLever.replace('1', '结算联');
									}
									if (printLever.indexOf('2') >= 0) {
										printLever = printLever.replace('2', '对账联');
									}
									if (printLever.indexOf('3') >= 0) {
										printLever = printLever.replace('3', '备物联');
									}
									if (printLever.indexOf(',') >= 0) {
										printLever = printLever.replace(',', '/');
									}
									data.data[item].PRINTER_LEVEL_NAME = printLever;
								}
								//data.data[item].PRINTER_LEVEL = data.data[item].PRINTER_LEVEL.replace('1', '结算联').replace('2', '对账联').replace('3', '备物联').replace(/,/g, '/')
								//								if (data.data[item].PRINTER_LEVEL == '1') {
								//									data.data[item].PRINTER_LEVEL_NAME = '结算联'
								//								} else if (data.data[item].PRINTER_LEVEL == '2') {
								//									data.data[item].PRINTER_LEVEL_NAME = '对账联'
								//								} else if (data.data[item].PRINTER_LEVEL == '3') {
								//									data.data[item].PRINTER_LEVEL_NAME = '备物联'
								//								}
								if (data.data[item].PRINTER_DISHES != undefined) {
									data.data[item].PRINTER_DISHES_TEXT = data.data[item].PRINTER_DISHES
									for (index in scope.goodType) {
										data.data[item].PRINTER_DISHES_TEXT = (data.data[item].PRINTER_DISHES_TEXT).replace(scope.goodType[index].SHOP_TAG_PK, scope.goodType[index].SHOP_TAG_NAME)
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

				// 弹窗确认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					$httpService.post(config.removeUrl, scope.toRemoveTag).success(function(data) {
						if (data.code != '0000') {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data
							}
						} else {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data
							}
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						init()
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});

				});

				// 弹窗取消事件
				eventBusService.subscribe(controllerName, controllerName + '.close', function(event, btn) {
					eventBusService.publish(controllerName, 'appPart.load.modal.close', {
						contentName : "modal"
					});
				});

				scope.removeTag = function(tag) {
					var m2 = {
						"url" : "aps/content/SystemSetup/BasicSetting/printer/config.json",
						"title" : "提示",
						"contentName" : "modal",
						"text" : "是否确定删除该记录?"
					}
					eventBusService.publish(controllerName, 'appPart.load.modal', m2);

					scope.toRemoveTag = tag
				}

				scope.updateTag = function(tag) {
					var m2 = {
						"url" : "aps/content/SystemSetup/BasicSetting/printer/update/config.json?fid=" + params.fid + "&PRINTER_PK=" + tag.PRINTER_PK,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
			}
		];
	});
}).call(this);