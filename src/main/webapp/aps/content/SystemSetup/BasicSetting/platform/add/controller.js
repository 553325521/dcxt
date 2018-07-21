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

				var comboboxInit = function() {
					$("#cdjb_select").picker({
						title : "选择菜单级别",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '一级菜单', '二级菜单' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								if (value == '一级菜单') {
									scope.form.MENU_LEVEL = '1'
									$("div#cdpx2").hide();
									$("div#cdpx1").show();
									$("div#ssfj").hide();
								} else if (value == '二级菜单') {
									scope.form.MENU_LEVEL = '2'
									$("div#cdpx1").hide();
									$("div#cdpx2").show();
									$("div#ssfj").show();
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
									$("div#gnmc").hide();
									$("div#ljdz").hide();
								} else if (value == '外部链接') {
									scope.form.MENU_TYPE = '2'
									$("div#gnxz").hide();
									$("div#gnmc").show();
									$("div#ljdz").show();
								}
							}
						}
					});

					$("#cdpx_select1").picker({
						title : "选择菜单排序",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '1', '2', '3' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								scope.form.MENU_SORT = value
							}
						}
					});

					$("#cdpx_select2").picker({
						title : "选择菜单排序",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : [ '1', '2', '3', '4', '5' ]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								scope.form.MENU_SORT = value
							}
						}
					});

					$("div#cdpx1").hide();
					$("div#cdpx2").hide();
					$("div#gnxz").hide();
					$("div#gnmc").hide();
					$("div#ljdz").hide();
					$("div#ssfj").hide();
				}

				comboboxInit()

			}
		];
	});
}).call(this);