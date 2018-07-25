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
					if (path == 'SystemSetup/BasicSetting/platform/add') {
						if (scope.form.PLATFORM_TYPE == undefined || scope.form.PLATFORM_TYPE == '') {
							var modal = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : '请先选择平台类型'
							}
							eventBusService.publish(controllerName, 'appPart.load.modal', modal);
							return;
						} else {
							var model = {
								"url" : "aps/content/" + path + "/config.json?fid=" + params.fid + "&plattype=" + scope.form.PLATFORM_TYPE,
								"size" : "modal-lg",
								"contentName" : "content"
							}
							console.info(model.url)
							eventBusService.publish(controllerName, 'appPart.load.content', model);
							return;
						}
					}
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
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
								scope.form.PLATFORM_TYPE = value
							}
						}
					});
				}

				var init = function() {
					$httpService.post(config.findURL, $scope.form).success(function(data) {
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