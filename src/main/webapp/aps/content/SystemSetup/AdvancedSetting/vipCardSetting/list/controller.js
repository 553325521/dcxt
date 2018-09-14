(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					//初始化form表单
					scope.record_list = [];
					scope.pageShow = "False";
					scope.pageTitle = config.pageTitle;
					//初始化选择框内容
					scope.vcard_name_list = ['全部会员'];
					scope.cardCate = scope.vcard_name_list[0]
					var init = function(){
						$httpService.post(config.findURL).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else if(data.code == '9999'){
								$.toptips(data.data);
							}else{
								scope.pageShow = "True";
								console.info(data.data)
								scope.record_list = data.data.vcard_record;
								scope.vcard_name_list = data.data.vcard_name_list;
								scope.vcard_name_list.unshift('全部会员')
								initPicker();
								scope.$apply();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
						
					}
					
					init();
					
					//初始化选择框
					var initPicker = function(){
						$("#filter_select").picker({
							title : "选择会员",
							cols : [
								{
									textAlign : 'center',
									values : scope.vcard_name_list,
									displayValues : scope.vcard_name_list
								}
							],
							onChange : function(e){
								console.info(e.value[0])
								if(e.value[0] == scope.vcard_name_list[0]){
									scope.filter_select = ""
								}else{
									scope.filter_select = e.value[0]
								}
								
								scope.$apply()
							}
						});
						
					}
					
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					var $form = $("#form");
					$form.form();
					//保存
					scope.doSave = function(){
						$form.validate(function(error) {
							if (!error) {
								//弹出保存询问
								var m2 = {
									"url" : "aps/content/SystemSetup/AdvancedSetting/shopIntegralSetting/config.json",
									"title" : "提示",
									"contentName" : "modal",
									"text" : "是否保存?"
								}
								eventBusService.publish(controllerName, 'appPart.load.modal', m2);
							}
						});
					}
					// 弹窗确认事件
					eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
						$httpService.post(config.saveURL,scope.form).success(function(data) {
								 var m2 = {
									"title" : "提示",
									"contentName" : "modal",
									"text" : data.data
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
			}
		];
	});
}).call(this);
$(function() {
})