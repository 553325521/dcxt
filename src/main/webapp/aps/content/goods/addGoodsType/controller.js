
(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '商品分类';
				
				scope.selectType =  [ '热菜', '凉菜', '汤类' ];
				// 显示范围数据源
				scope.show_area = [
					{
						value : 1,
						name : '堂点',
						checked: true
					},
					{
						value : 2,
						name : '外卖',
						checked: false
					},
					{
						value : 3,
						name : '预订',
						checked: false
					}
				];
				
				scope.toHref = function(path,shopid) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fromUrl=" + config.currentUrl + "&shopid=" + shopid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				//初始化form表单
				scope.form = {};
				
				scope.form.SELECTTYPE = scope.selectType[1];
				
				/*angular.element("#fs_select").get(0).value = scope.form.SELECTTYPE;*/
				$("#fs_select").val(scope.selectType[1]);
				
				scope.doSave = function() {

					console.info("aa");
				}
				function comboboxInit() {	
					$("#fs_select").picker({
						title : "选择分类",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : scope.selectType,
								displayValues : scope.selectType
							}
						]
					});
				}
				comboboxInit();
			}
		];
	});
}).call(this);

$(function() {
	
})