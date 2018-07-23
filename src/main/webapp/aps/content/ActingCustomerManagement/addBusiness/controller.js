
(function() {
	define(['jqueryweui','pickercity'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '添加商户';	
				
				scope.shop_type_first = ['餐饮行业','水果行业'];
				
				scope.shop_type_second = ['火锅','垃圾'];
				
				scope.form = {};
				
				scope.form.SHOP_TYPE_FIRSET = scope.shop_type_first[1];
				
				scope.form.SHOP_TYPE_SECOND = scope.shop_type_second[1];
				
				$("#fs_select").val(scope.shop_type_first[1]);
				
				$("#fs_select1").val(scope.shop_type_second[1]);
				
				
				//跳转链接
				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json",
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				function comboboxInit() {	
					$("#fs_select").picker({
						title : "选择类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : scope.shop_type_first,
								displayValues : scope.shop_type_first
							}
						]
					});
					$("#fs_select1").picker({
						title : "选择类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : scope.shop_type_second,
								displayValues : scope.shop_type_second
							}
						]
					});
				}
				$("#ssx").cityPicker({
			        title: "选择省市县",
			        toolbarCloseText : '完成'
			     });
				comboboxInit();
			}
		];
	});
}).call(this);

$(function() {
	
})