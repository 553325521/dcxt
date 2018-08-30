Array.prototype.indexOf = function(val) {
	for (var i = 0; i < this.length; i++) {
		if (this[i] == val)
			return i;
	}
	return -1;
};
Array.prototype.remove = function(val) {
	var index = this.indexOf(val);
	if (index > -1) {
		this.splice(index, 1);
	}
};


(function() {
	define([], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				// 定义页面标题
				scope.pageTitle = '打印机列表';

				scope.form = {};

				// 初始化打印份数
				scope.form.PRINT_BUG_NUM = 1;

				scope.form.PRINT_PRICE = '600.00';
				scope.form.PRINT_PRICE_ONCE = '600.00';

				// 打印份数减法
				scope.fsDoReduce = function() {
					if (scope.form.PRINT_BUG_NUM > 1) {
						scope.form.PRINT_BUG_NUM -= 1;
						scope.refreshPrice();
					}
				}

				// 打印份数加法
				scope.fsDoAdd = function() {
					scope.form.PRINT_BUG_NUM += 1;
					scope.refreshPrice();
				}

				// 打印份数直接修改数字
				scope.fsReduceNum = function() {
					if (scope.form.PRINT_BUG_NUM <= 0 || scope.form.PRINT_BUG_NUM == null) {
						scope.form.PRINT_BUG_NUM = 1
						scope.refreshPrice();
					}
				}

				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

				scope.changeCheckArr = function(item) {
					if (item.PRINT_RELEVANT_TYPE == 'name') {
						$.each(scope.nameList, function(index, value) {
							value.outer = false;
						})
						$.each(scope.typeList, function(index, value) {
							value.outer = false;
						})
						$.each(scope.widthList, function(index, value) {
							value.outer = false;
						})
						scope.checkList(item.PRINT_RELEVANT_PK);
					} else if (item.PRINT_RELEVANT_TYPE == 'type') {
						$.each(scope.typeList, function(index, value) {
							value.outer = false;
						})
					} else if (item.PRINT_RELEVANT_TYPE == 'width') {
						$.each(scope.widthList, function(index, value) {
							value.outer = false;
						})
						scope.checkTypeList(item);
					}
					item.outer = true;

					scope.checkPirce();

				}

				scope.checkList = function(fid) {
					scope.loadTypeList = [];
					scope.loadWidthList = [];
					$.each(scope.typeList, function(index, value) {
						if (value.PRINT_RELEVANT_FPK == fid) {
							scope.loadTypeList.push(value);
						}
					})
					$.each(scope.widthList, function(index, value) {
						if (value.PRINT_RELEVANT_FPK == fid) {
							scope.loadWidthList.push(value);
						}
					})

					scope.form.PRINT_TYPE = undefined;
					scope.form.PRINT_WIDTH = undefined;
				}

				scope.checkTypeList = function(item) {
					scope.loadTypeList = [];
					for (var index in scope.typeList) {
						var value = scope.typeList[index]
						if (value.PRINT_RELEVANT_FPK == item.PRINT_RELEVANT_FPK) {
							if (item.PRINT_RELEVANT_NAME == '58mm' && value.PRINT_RELEVANT_NAME == 'WIFI+切刀') {
								continue;
							}
							scope.loadTypeList.push(value);
						}
					}

					scope.form.PRINT_TYPE = undefined;
					scope.form.PRINT_WIDTH = undefined;
				}

				var init = function() {
					$httpService.post(config.findURL, scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							scope.relevantList = data.data;

							scope.nameList = [];
							scope.widthList = [];
							scope.typeList = [];

							$.each(scope.relevantList, function(index, value) {
								if (value.PRINT_RELEVANT_TYPE == 'name') {
									scope.nameList.push(value);
								} else if (value.PRINT_RELEVANT_TYPE == 'type') {
									scope.typeList.push(value);
								} else if (value.PRINT_RELEVANT_TYPE == 'width') {
									scope.widthList.push(value);
								}
							})
							scope.nameList[0].outer = true;
							scope.form.PRINT_PK = '1';
							scope.checkList(scope.nameList[0].PRINT_RELEVANT_PK);

							scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				init();

				scope.doSave = function() {
					var m2 = {
						"title" : "提示",
						"contentName" : "modal"
					}

					if (scope.form.PRINT_WIDTH == undefined || scope.form.PRINT_WIDTH == '') {
						m2.text = "请选择出纸宽度!"
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						return;
					}

					if (scope.form.PRINT_TYPE == undefined || scope.form.PRINT_TYPE == '') {
						m2.text = "请选择网络类型!"
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						return;
					}

					$httpService.post(config.addPrintPriceURL, scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							m2.text = "购买成功!"
							eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				scope.checkPirce = function() {
					$.each(scope.relevantList, function(index, value) {
						if (value.outer) {
							if (value.PRINT_RELEVANT_TYPE == 'name') {
								scope.form.PRINT_PK = value.PRINT_RELEVANT_PK;
							} else if (value.PRINT_RELEVANT_TYPE == 'type') {
								scope.form.PRINT_TYPE = value.PRINT_RELEVANT_NAME;
							} else if (value.PRINT_RELEVANT_TYPE == 'width') {
								scope.form.PRINT_WIDTH = value.PRINT_RELEVANT_NAME;
							}
						}
					})

					if (scope.form.PRINT_PK != undefined && scope.form.PRINT_TYPE != undefined && scope.form.PRINT_WIDTH != undefined) {
						$httpService.post(config.findPriceURL, scope.form).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else {
								if (data.data == undefined) {
									scope.form.PRINT_PRICE = '0.00';
									scope.form.PRINT_PRICE_ONCE = '0.00';
								} else {
									scope.form.PRINT_PRICE = data.data.PRINT_PRICE;
									scope.form.PRINT_PRICE_ONCE = data.data.PRINT_PRICE;
									scope.refreshPrice();
								}
								scope.$apply();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}
				}

				scope.refreshPrice = function() {
					scope.form.PRINT_PRICE = scope.form.PRINT_PRICE_ONCE * scope.form.PRINT_BUG_NUM;
				}
			}
		];
	});
}).call(this);