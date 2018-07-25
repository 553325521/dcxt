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
							$.alert("添加成功", "提示");
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				var comboboxInit = function() {
					$("#FUNCTION_ISPID").picker({
						title : "选择是否顶部",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '是', '否' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								if (value == '是') {
									scope.form.FUNCTION_ISPID = '1'
									$("div#pid").hide();
								} else if (value == '否') {
									scope.form.FUNCTION_ISPID = '2'
									$("div#pid").show();
								}
							}
						}
					});

					$("div#pid").hide();
				}

				comboboxInit()

				var init = function() {

					$httpService.post(config.findURL, scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data);
						} else {
							var values = []
							$.each(data.data, function(index, value) {
								values.push(value.FUNCTION_NAME)
							})

							$("#FUNCTION_PK_PID").picker({
								title : "选择所属父级",
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
										$.each(data.data, function(i, v) {
											if (value == v.FUNCTION_NAME) {
												scope.form.FUNCTION_PK_PID = v.FUNCTION_PK
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

				init();

			}
		];
	});
}).call(this);