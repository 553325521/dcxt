
	(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					// 定义页面标题
					scope.pageTitle = '商品设置';
					
					scope.selectArray = ['荤菜', '素菜', '豆类' ];
					
					scope.selectUnit = ['份','盘','个'];
					
					scope.selectSize = ['大份','小份'];
					
					scope.toHref = function(path,shopid) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fromUrl=" + config.currentUrl + "&shopid=" + shopid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
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
					
					//初始化form表单
					scope.form = {};
					
					scope.form.SELECTTYPE = scope.selectArray[1];
					
					scope.form.SELECTUNIT = scope.selectUnit[1];
					
					scope.form.SELECTSIZE = scope.selectSize[1];
					
					/*angular.element("#fs_select").get(0).value = scope.form.SELECTTYPE;*/
					$("#fs_select").val(scope.selectArray[1]);
					$("#unit_select").val(scope.selectUnit[1]);
					$("#size_select").val(scope.selectSize[1]);
					function comboboxInit() {	
						$("#fs_select").picker({
							title : "选择分类",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values : scope.selectArray,
									displayValues : scope.selectArray
								}
							]
						});
						$("#unit_select").picker({
							title : "选择单位",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values : scope.selectUnit,
									displayValues : scope.selectUnit
								}
							]
						});
						$("#size_select").picker({
							title : "选择份型",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values : scope.selectSize,
									displayValues : scope.selectSize
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