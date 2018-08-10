(function() {
	define([ 'jqueryweui' ], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				//初始化 form 表单
				$scope.form = {};
				$scope.form.IS_USE = 0;
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
							console.info('12321');
						}
					})
				}

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
				
				var init = function () {
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
					
					$httpService.post(config.findByIdURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
					
				}
				
				init();
				
				var comboboxInit = function (values) {
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
				}
				
			}
		];
	});
}).call(this);