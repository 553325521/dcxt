(function() {
	define([ 'jqueryweui' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				// 定义页面标题
				scope.pageTitle = config.pageTitle

				scope.form = {}
				
				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json",
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

				scope.doSave = function() {
					$httpService.post(config.saveURL, scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							eventBusService.publish(controllerName, 'appPart.load.modal.alert', {
								"title" : "操作提示",
								"content" : "添加成功！"
							});
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				var init = function() {
					$httpService.post(config.findURL, {}).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							var values = []
							values.push('新建一级菜单')
							if (data.data.length > 0) {
								for (var index in data.data) {
									var obj = data.data[index]
									if (obj.MENU_NAME != undefined) {
										values.push(obj.MENU_NAME)
									}
								}
								scope.MenuList = data.data
							}
							comboboxInit(values)
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				init()

				var comboboxInit = function(values) {
					$("#cdjb_select").picker({
						title : "选择菜单级别",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : values
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								if (value != '新建一级菜单') {
									$.each(scope.MenuList, function(index, value) {
										if (value.MENU_NAME == value) {
											scope.form.MENU_FATHER_PK = value.MENU_PK
										}
									})
								}
							}
						}
					});

					$("#gnlx_select").picker({
						title : "选择功能类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '内置功能', '外部链接' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								if (value == '内置功能') {
									scope.form.MENU_TYPE = '1'
									$("div#gnxz").show();
									$("div#ljdz").hide();
								} else if (value == '外部链接') {
									scope.form.MENU_TYPE = '2'
									$("div#gnxz").hide();
									$("div#ljdz").show();
								}
							}
						}
					});

					$("div#gnxz").hide();
					$("div#ljdz").hide();
				}

			}
		];
	});
}).call(this);