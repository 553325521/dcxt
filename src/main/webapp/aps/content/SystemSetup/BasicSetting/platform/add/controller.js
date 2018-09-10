(function() {
	define([ 'jqueryweui' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				// 定义页面标题
				scope.pageTitle = config.pageTitle

				scope.form = {}

				scope.form.MENU_PLAT = params.plattype;

				scope.form.FK_APP = params.AppId;

				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid + "&AppId=" + params.AppId + "&plattype=" + params.plattype,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

				// 弹窗确认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					$httpService.post(config.saveURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data,
								"toUrl" : "aps/content/SystemSetup/BasicSetting/platform/config.json?fid=" + params.fid + "&AppId=" + params.AppId + "&plattype=" + params.plattype
							}
						} else {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data,
								"toUrl" : "aps/content/SystemSetup/BasicSetting/platform/config.json?fid=" + params.fid + "&AppId=" + params.AppId + "&plattype=" + params.plattype
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

				scope.doSave = function() {
					var $form = $("#menuAddForm");
					$form.form();
					$form.validate(function(error) {
						if (!error) {
							var m2 = {
								"url" : "aps/content/SystemSetup/BasicSetting/platform/add/config.json",
								"title" : "提示",
								"contentName" : "modal",
								"text" : "是否确定保存?"
							}
							eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						}
					})
				}

				var init = function() {
					$httpService.post(config.findURL, scope.form).success(function(data) {
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

					$httpService.post(config.findAllFunctionURL, scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							var values = []
							if (data.data.length > 0) {
								for (var index in data.data) {
									var obj = data.data[index]
									if (obj.FUNCTION_NAME != undefined) {
										values.push(obj.FUNCTION_NAME)
									}
								}
								scope.FunList = data.data
							}

							$("#gnxz_select").picker({
								title : "请选择功能",
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
										$.each(scope.FunList, function(index, val) {
											if (val.FUNCTION_NAME == value) {
												scope.form.MENU_LINK = val.FUNCTION_URL
											}
										})
									}
								}
							});

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
									$.each(scope.MenuList, function(index, val) {
										if (val.MENU_NAME == value) {
											scope.form.MENU_FATHER_PK = val.MENU_PK
										}
									})
								}
							}
						}
					});

					$("#cdlx_select").picker({
						title : "选择菜单类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '小程序', '页面' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								scope.form.VIEW_TYPE = value
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
									scope.form.MENU_TYPE = value
									$("div#gnxz").show();
									$("div#ljdz").hide();
								} else if (value == '外部链接') {
									scope.form.MENU_TYPE = value
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