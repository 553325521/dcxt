(function() {
	define([], function() {
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
				
				scope = $scope;

				scope.noCheckArr = [ 1, 2, 3, 4, 5 ];

				// 定义页面标题
				$scope.pageTitle = '打印机设置'

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

				// 打印速度数据源
				scope.printer_speed = [
					{
						title : "低速",
						value : "001"
					},
					{
						title : "中速",
						value : "002"
					},
					{
						title : "快速",
						value : "003"
					}
				];

				// 菜品数据源
				scope.printer_dishes_list = [
					{
						title : "荤菜",
						value : 1
					},
					{
						title : "素菜",
						value : 2
					},
					{
						title : "凉菜",
						value : 3
					},
					{
						title : "汤类",
						value : 4
					},
					{
						title : "酒水",
						value : 5
					}
				];

				//初始化 form 表单
				scope.form = {};

				// 初始化打印份数
				scope.form.PRINTER_PAGE_NUMS = 1;

				// 初始化打印速度
				scope.form.RPINTER_SPEED = scope.printer_speed[0].value;

				// 初始化 菜品列表
				scope.form.KIND_OF_DISHES = false;

				// 打印份数减法
				scope.fsDoReduce = function() {
					if (scope.form.PRINTER_PAGE_NUMS > 1) {
						scope.form.PRINTER_PAGE_NUMS -= 1;
					}
				}

				// 打印份数加法
				scope.fsDoAdd = function() {
					scope.form.PRINTER_PAGE_NUMS += 1;
				}

				// 打印份数直接修改数字
				scope.fsReduceNum = function() {
					if (scope.form.PRINTER_PAGE_NUMS <= 0 || scope.form.PRINTER_PAGE_NUMS == null) {
						scope.form.PRINTER_PAGE_NUMS = 1
					}
				}

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

				// 菜品勾选
				scope.selectDishes = function(item) {
					var action = (item.checked ? 'add' : 'remove');
					if (action == "remove") {
						scope.noCheckArr.push(item.value);
					} else {
						scope.noCheckArr.remove(item.value);
					}
				}

				$scope.$watch('form.PRINTER_PRODUCT_RANGE', function(newValue, oldValue) {
					if (newValue === oldValue) {
						return;
					}
					if (newValue == 'all' || newValue == undefined) {
						scope.form.KIND_OF_DISHES = false;
						if (newValue == 'all') {
							scope.noCheckArr = []
						} else {
							scope.noCheckArr = [ 1, 2, 3, 4, 5 ]
						}
					} else {
						scope.form.KIND_OF_DISHES = true;

						scope.noCheckArr = [ 1, 2, 3, 4, 5 ]
					}
				}, true);

				scope.doSave = function() {

					// 定义菜品字段
					scope.form.PRINTER_DISHES = []

					$.each(scope.printer_dishes_list, function(index, item) {
						if (scope.noCheckArr.indexOf(item.value) == -1) {
							scope.form.PRINTER_DISHES.push(item.value)
						}
					})

					console.info(scope.form);
					
					var m2 = {
	        				  "url":"aps/content/testTemp/config.json",
	        			      "size":"modal-lg",
	        			      "contentName":"content"
	        				}   
	        			eventBusService.publish(controllerName,'appPart.load.content', m2);
				}


				scope.reset = function() {
					//初始化 form 表单
					scope.form = {}

					// 初始化打印份数
					scope.form.PRINTER_PAGE_NUMS = 1;

					// 初始化打印速度
					scope.form.RPINTER_SPEED = scope.printer_speed[0].value;

					// 取消勾选
					scope.printer_level_selected.checked = false

					scope.form.PRINTER_PRODUCT_RANGE = undefined
				}
			}
		];
	});
}).call(this);

