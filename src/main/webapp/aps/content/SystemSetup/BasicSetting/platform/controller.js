(function() {
	define([ 'jqueryweui' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				// 定义页面标题
				scope.pageTitle = config.pageTitle
				
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
								scope.form.MENU_PLAT = value
								
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
								if (data.data[index].DICTIONARY_NAME != undefined) {
									values.push(data.data[index].DICTIONARY_NAME)
								}
							}
							comboboxInit(values)
							
							if (params.plattype != undefined) {
								
								scope.form.MENU_PLAT = params.plattype
								
								$("#lx_select").val(params.plattype);
								
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

				init()

			}
		];
	});
}).call(this);