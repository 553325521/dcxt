(function() {
	define([ 'jqueryweui' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

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

				scope = $scope;

				scope.noCheckArr = [];

				// 定义页面标题
				scope.pageTitle = config.pageTitle

				// 打印类型的被选中对象
				scope.printer_level_selected = ''

				// 打印类型数据源
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

				//初始化 form 表单
				scope.form = {};

				// 初始化打印份数
				scope.form.PRINTER_PAGE_NUMS = 1;

				// 初始化打印速度
				scope.form.RPINTER_SPEED = '低速';

				// 初始化 菜品列表
				scope.form.KIND_OF_DISHES = false;

				$scope.$watch('form.PRINTER_PRODUCT_RANGE', function(newValue, oldValue) {
					if (newValue === oldValue) {
						return;
					}
					if (newValue == 'all' || newValue == undefined) {
						scope.form.KIND_OF_DISHES = false;
						if (newValue == 'all') {
							scope.noCheckArr = []
						} else {
							initNoCheckArr()
						}
					} else {
						scope.form.KIND_OF_DISHES = true;

						initNoCheckArr()
					}
				}, true);

				// 打印类型单选实现
				scope.selectLevel = function(item) {
					if (scope.printer_level_selected == '') {
						scope.printer_level_selected = item
					} else if (scope.printer_level_selected != item) {
						scope.printer_level_selected.checked = false
						item.checked = true
						scope.printer_level_selected = item
					}
					scope.form.PRINTER_LEVEL = item.value
				}

				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

				// 菜品勾选
				scope.selectDishes = function(item) {
					var action = (item.checked ? 'add' : 'remove');
					if (action == "remove") {
						scope.noCheckArr.push(item.value);
					} else {
						scope.noCheckArr.remove(item.value);
					}
				}

				scope.doSave = function() {
					var $form = $("#menuAddForm");
					$form.form();
					$form.validate(function(error) {
						if (!error) {
							if (scope.form.PRINTER_KEY == undefined || scope.form.PRINTER_KEY == '') {
								var m2 = {
									"title" : "提示",
									"contentName" : "modal",
									"text" : '请选择打印机密匙!'
								}
								eventBusService.publish(controllerName, 'appPart.load.modal', m2);
								return;
							}

							if (scope.form.PRINTER_LEVEL == undefined || scope.form.PRINTER_LEVEL == '') {
								var m2 = {
									"title" : "提示",
									"contentName" : "modal",
									"text" : '请选择打印类型!'
								}
								eventBusService.publish(controllerName, 'appPart.load.modal', m2);
								return;
							}

							// 定义菜品字段
							scope.form.PRINTER_DISHES = []

							$.each(scope.printer_dishes_list, function(index, item) {
								debugger;
								if (scope.noCheckArr.indexOf(item.value) == -1) {
									scope.form.PRINTER_DISHES.push(item.value)
								}
							})

							if (scope.form.PRINTER_DISHES.length <= 0) {
								var m2 = {
									"title" : "提示",
									"contentName" : "modal",
									"text" : '请选择范围!'
								}
								eventBusService.publish(controllerName, 'appPart.load.modal', m2);
								return;
							}

							var m2 = {
								"url" : "aps/content/SystemSetup/BasicSetting/printer/setting/config.json",
								"title" : "提示",
								"contentName" : "modal",
								"text" : "是否确定保存?"
							}
							eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						}
					})
				}

				// 弹窗确认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					scope.form.PRINTER_DISHES_STR = scope.form.PRINTER_DISHES.join(',');
					$httpService.post(config.saveURL, $scope.form).success(function(data) {
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
								"text" : data.data,
								"toUrl" : "aps/content/SystemSetup/BasicSetting/printer/config.json?fid=" + params.fid
							}
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
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

				var init = function() {
					$httpService.post(config.findURL, {
						PRINTER_PK : params.PRINTER_PK
					}).success(function(data) {
						if (data.code != '0000') {
						} else {
							scope.form = data.data;
							scope.form.PRINTER_PK = params.PRINTER_PK;
							$scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});

					$httpService.post(config.findGoodTypeURL, {
					}).success(function(data) {
						if (data.code != '0000') {
						} else {
							// 菜品数据源
							scope.printer_dishes_list = [];
							scope.goodTypes = data.data;
							$.each(scope.goodTypes, function(index, value) {
								scope.printer_dishes_list.push({
									title : value.SHOP_TAG_NAME,
									value : value.SHOP_TAG_PK
								});
								scope.noCheckArr.push(value.SHOP_TAG_PK);
							})
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				init()

				function initNoCheckArr() {
					scope.noCheckArr = []
					$.each(scope.goodTypes, function(index, value) {
						scope.noCheckArr.push(value.SHOP_TAG_PK);
					})
				}

				function comboboxInit() {
					$("#fs_select").picker({
						title : "选择份数",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '1', '2', '3' ],
								displayValues : [ '1', '2', '3' ]
							}
						]
					});

					$("#sd_select").picker({
						title : "选择速度",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '低速', '中速', '高速' ],
								displayValues : [ '低速', '中速', '高速' ]
							}
						]
					});
				}

				comboboxInit()
			}
		];
	});
}).call(this);