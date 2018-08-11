(function() {
	define([ 'jqueryweui' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				//初始化 form 表单
				$scope.form = {};
				$scope.form.FK_USER = params.id;
				// 定义页面标题
				$scope.pageTitle = config.pageTitle

				$scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

				$scope.doSave = function() {
					var $form = $("#menuAddForm");
					$form.form();
					$form.validate(function(error) {
						if (!error) {
							var m2 = {
								"url" : "aps/content/SystemSetup/BasicSetting/staff/setting/config.json",
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
					$httpService.post(config.updateStaffInfo, $scope.form).success(function(data) {
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
								"toUrl" : "aps/content/SystemSetup/BasicSetting/staff/config.json?fid=" + params.fid
							}
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
					}).error(function(data) {
						var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : '请求错误！'
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
					});

				});

				// 弹窗取消事件
				eventBusService.subscribe(controllerName, controllerName + '.close', function(event, btn) {
					eventBusService.publish(controllerName, 'appPart.load.modal.close', {
						contentName : "modal"
					});
				});

				var initText = function() {
					var max = $('#count_max').text();
					$('#textarea').on('input', function() {
						var text = $(this).val();
						var len = text.length;
						$('#count').text(len);
						if (len > max) {
							$(this).closest('.weui_cell').addClass('weui_cell_warn');
						} else {
							$(this).closest('.weui_cell').removeClass('weui_cell_warn');
						}
					});
				}

				initText()

				var init = function() {
					$httpService.post(config.findByIdURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data);
						} else {
							$scope.form = data.data;
							initRoleList();
							$scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}

				var initRoleList = function() {
					$httpService.post(config.findRoleList, $scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							var values = []
							if (data.data.length > 0) {
								for (var index in data.data) {
									var obj = data.data[index]
									if (obj.ROLE_NAME != undefined) {
										values.push(obj.ROLE_NAME)
									}
								}
								$scope.RoleList = data.data
							}
							comboboxInit(values)
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}


				init();

				var comboboxInit = function(values) {
					$("#gnxz_select").picker({
						title : "请选择角色",
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
								$.each($scope.RoleList, function(index, val) {
									if (val.ROLE_NAME == value) {
										$scope.form.FK_ROLE = val.ROLE_PK
									}
								})
							}
						}
					});

					$.each($scope.RoleList, function(index, val) {
						if ($scope.form.FK_ROLE != undefined && $scope.form.FK_ROLE == val.ROLE_PK) {
							$("#gnxz_select").val(val.ROLE_NAME);
						}
					})
				}

			}
		];
	});
}).call(this);