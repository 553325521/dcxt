(function() {
	define([ 'jqueryweui' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				scope.form = {}

				$scope.form.MENU_FATHER_PK = 'all'

				scope.form.FK_APP = params.AppId

				scope.toHref = function(path) {
					if (path == 'SystemSetup/BasicSetting/platform/add') {
						if (scope.form.MENU_PLAT == undefined || scope.form.MENU_PLAT == '') {
							var modal = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : '请先选择平台类型'
							}
							eventBusService.publish(controllerName, 'appPart.load.modal', modal);
							return;
						} else {
							var model = {
								"url" : "aps/content/" + path + "/config.json?fid=" + params.fid + "&AppId=" + params.AppId + "&plattype=" + scope.form.MENU_PLAT,
								"size" : "modal-lg",
								"contentName" : "content"
							}
							eventBusService.publish(controllerName, 'appPart.load.content', model);
							return;
						}
					}
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid + "&AppId=" + params.AppId,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				scope.toHref1 = function() {
					window.location.href = scope.authorizerURL;
				}
				
				scope.updateToWxPlat = function() {
					if (scope.form.MENU_PLAT == undefined || scope.form.MENU_PLAT == '') {
						var modal = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : '请先选择平台类型'
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', modal);
						return;
					}

					$httpService.post(config.updateWxMenuForTagIdURL, $scope.form).success(function(data) {
						var modal = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : data.data
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', modal);
						return;
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});

				}

				var comboboxInit = function(values) {
					$("#lx_select").picker({
						title : "选择平台类型",
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
								for (var ind in scope.userTagList) {
									if (scope.userTagList[ind].USER_TAG_NAME != undefined) {
										let val = scope.userTagList[ind];
										if (value == val.USER_TAG_NAME) {
											scope.form.MENU_PLAT = val.USER_TAG_ID
											$("#lx_select").val(value);
											break;
										}
									}
								}

								$httpService.post(config.findAllMenuURL, $scope.form).success(function(data) {
									if (data.code != '0000') {
										loggingService.info(data.data);
									} else {
										scope.itemList = data.data
										scope.$apply()
									}
								}).error(function(data) {
									loggingService.info('获取测试信息出错');
								});
							}
						}
					});
				}

				var init = function() {
					$httpService.post(config.findDictionaryURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							var values = []
							for (var index in data.data) {
								if (data.data[index].USER_TAG_NAME != undefined) {
									values.push(data.data[index].USER_TAG_NAME)
								}
							}

							scope.userTagList = data.data;

							comboboxInit(values)

							if (params.plattype != undefined) {
								for (var ind in scope.userTagList) {
									if (scope.userTagList[ind].USER_TAG_NAME != undefined) {
										let value = scope.userTagList[ind];
										if (params.plattype == value.USER_TAG_ID) {
											scope.form.MENU_PLAT = params.plattype
											$("#lx_select").val(value.USER_TAG_NAME);
											break;
										}
									}
								}

								$httpService.post(config.findAllMenuURL, $scope.form).success(function(data) {
									if (data.code != '0000') {
										loggingService.info(data.data);
									} else {
										scope.itemList = data.data
										scope.$apply()
									}
								}).error(function(data) {
									loggingService.info('获取测试信息出错');
								});

							}
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});

				}

				var initt = function() {
					$httpService.post(config.findURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							// 定义页面标题
							scope.pageTitle = '设备授权'

							scope.isList = false;

							scope.isSmall = true;

							scope.authorizerURL = data.data;
							
							scope.authorize = false;
							
						} else {
							init()
							// 定义页面标题
							scope.pageTitle = config.pageTitle

							scope.isList = true;

							if (data.data != "" && data.data != null && data.data != undefined) {
								
								scope.authorize = true;
								
								scope.authorizerURL = data.data;
							}
						}
						scope.$apply()
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}


				initt()

			}
		];
	});
}).call(this);