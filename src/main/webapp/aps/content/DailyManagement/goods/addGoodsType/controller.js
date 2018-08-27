
(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '商品分类';
				
				// 显示范围数据源
				scope.GTYPE_AREA = [
					{
						name : '堂点',
						checked: false
					},
					{
						name : '外卖',
						checked: false
					},
					{
						name : '预订',
						checked: false
					}
				];
				/*[{name:'堂点',checked:true},{name:'外卖',checked:true}]*/
				//初始化form表单
				scope.form = {};
				
			/*	初始化排序序号
				scope.form.GTYPE_ORDER = 1;*/
				
				/*	初始化上一级分类ID*/
				scope.form.GTYPE_PID = params.GTYPE_PID;
				
				/*选择分类初始化*/
				scope.form.GTYPE_PNAME = "一级分类";
				
				/*是否启用初始化*/
				scope.form.GTYPE_STATE = 1;
				
				/*显示范围数组初始化*/
				scope.form.GTYPE_AREA_Array = [];
				
				/*显示范围值初始化*/
				scope.form.GTYPE_AREA = "";
				
				/*初始化商品类别等级*/
				scope.form.GTYPE_LEVEL = 1;
				
				console.info(params.Last_Page);
				
				/*根据PID查询更新要添加分类的上一级分类名称*/
				function loadLastGoodsTypeName(){
					$httpService.post(config.selectGoodsTypePNameByPIDURL,params).success(function(data) {
						/*$scope.$apply();*/
						if(data.code == '0000' && data.data !=null){
							scope.form.GTYPE_PNAME = data.data.GTYPE_NAME;
							if(params.GTYPE_PID != 0){
								scope.GTYPE_AREA = eval(data.data.GTYPE_AREA);
								scope.form.GTYPE_AREA = JSON.stringify(scope.GTYPE_AREA);
								if(data.data.GTYPE_STATE == "0"){
									scope.form.GTYPE_STATE = 0;
									$("#GTYPE_STATE_ISSHOW").hide();
								}
							}
							$scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				/*第一次进来的数据初始化*/
				loadLastGoodsTypeName();
				
				scope.toHref = function(path) {
					var m2 = {
							"url" : "aps/content/" + path + "/config.json?fid=" + params.fid+"&fromUrl=" + config.currentUrl + "&GTYPE_PID=" + scope.form.GTYPE_PID+"&Last_Page="+params.Last_Page,
							"size" : "modal-lg",
							"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				// 范围勾选
				scope.selectArea = function(item) {
					var action = (item.checked ? 'add' : 'remove');
					if (action == "add") {
						scope.form.GTYPE_AREA_Array.push({name:item.name,checked:item.checked});
						scope.form.GTYPE_AREA = JSON.stringify(scope.form.GTYPE_AREA_Array);
					} else {
						scope.form.GTYPE_AREA_Array.remove({name:item.name,checked:item.checked});
						scope.form.GTYPE_AREA = JSON.stringify(scope.form.GTYPE_AREA_Array);
					}
				}
				
				scope.doSave = function() {
					 var m2 = {
				        		"url":"aps/content/DailyManagement/goods/addGoodsType/config.json",
				        		"title":"提示",
				        		"contentName":"modal",
				        		"text":"是否保存"
				        	 }
				    eventBusService.publish(controllerName,"appPart.load.modal",m2);
				}
				/*加载排序序号
				function loadGoodsTypeOrder(){
					//发送post请求
					$httpService.post(config.loadGoodsTypeOrderURL,params).success(function(data) {
						$scope.$apply();
						if(data.code == '0000' && data.data !=null){
							scope.form.GTYPE_ORDER = parseInt(data.data.GTYPE_ORDER)+1;
							$scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				调用加载排序序号方法
				loadGoodsTypeOrder();*/
				
				// 弹窗默认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					$httpService.post(config.addURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data,
							}
						} else {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data,
								"toUrl" : "aps/content/DailyManagement/goods/config.json?fid=" + scope.form.fid
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
			}
		];
	});
}).call(this);

$(function() {
	
})