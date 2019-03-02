(function() {
	define([ 'jqueryweui' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;

				// 定义页面标题
				$scope.pageTitle = config.pageTitle

				//初始化 form 表单
				scope.form = {};

				scope.form.fid = params.fid

				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + scope.form.fid,
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
								"text" : data.data
							}
						} else {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data,
								"toUrl" : "aps/content/SystemSetup/BasicSetting/tagMange/config.json?fid=" + scope.form.fid
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

				function comboboxInit() {
					$("#sd_select").picker({
						title : "选择标签类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '打印标签', '商品标签' ],
								displayValues : [ '打印标签', '商品标签' ]
							}
						],
						onChange : function(e) {
							var value = e.value[0]
							$scope.form.SHOP_TAG_TYPE = value
						}
					});
				}

				comboboxInit();

				scope.doSave = function() {

					if (scope.form.SHOP_TAG_TYPE == undefined || scope.form.SHOP_TAG_TYPE == '') {
						var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : '请选择标签类型!'
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						return;
					}

					var $form = $("#addTagForm");
					$form.form();
					$form.validate(function(error) {
						if (!error) {
							var m2 = {
								"url" : "aps/content/SystemSetup/BasicSetting/tagMange/add/config.json",
								"title" : "提示",
								"contentName" : "modal",
								"text" : "是否确定保存?"
							}
							eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						}
					})
				}

			}
		];
	});
}).call(this);