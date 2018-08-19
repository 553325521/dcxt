
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
				
				/*初始化排序序号*/
				scope.form.GTYPE_ORDER = 1;
				
				/*	初始化上一级分类ID*/
				scope.form.GTYPE_PID = params.GTYPE_PID;
				
				scope.form.PID = params.GTYPE_PID;
				
				scope.form.GTYPE_NAME = "";
				console.info("接收的我的ID:"+params.GTYPE_PK);
				console.info("编辑里边的array:"+params.Last_Page);
				console.info("编辑里边的:"+scope.form.GTYPE_PID);
				/*选择分类初始化*/
				scope.form.GTYPE_PNAME = "顶级类";
				
				/*是否启用初始化*/
				scope.form.GTYPE_STATE = 1;
				
				/*显示范围数组初始化*/
				scope.form.GTYPE_AREA_Array = [];
				
				/*显示范围值初始化*/
				scope.form.GTYPE_AREA = "";
				
				/*初始化商品类别等级*/
				scope.form.GTYPE_LEVEL = 1;
				
				/*排序序号数据源*/
				scope.select_order = [];
				
				/*初始化商品分类备注*/
				scope.form.GTYPE_BZ = "";
				
				scope.form.GTYPE_PK ="";
				
				scope.form.GTYPE_OLD_ORDER = 1;
				//下拉框的初始化
				function comboboxInit() {
					$("#order_select").picker({
						title : "选择分类",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : scope.select_order,
								displayValues : scope.select_order
							}
						]
					});
				}
				/*根据PID查询更新要添加分类的上一级分类名称*/
				function loadLastGoodsTypeName(){
					$httpService.post(config.selectGoodsTypePNameByPIDURL,params).success(function(data) {
						/*$scope.$apply();*/
						if(data.code == '0000' && data.data !=null){
							scope.form.GTYPE_PNAME = data.data.GTYPE_NAME;
							if(params.GTYPE_PID != 0){
								scope.GTYPE_AREA = eval(data.data.GTYPE_AREA);
								for(var i = 0;i<scope.GTYPE_AREA.length;i++ ){
									scope.GTYPE_AREA[i].checked = false;
								}
								console.info("scope.GTYPE_AREA:"+scope.GTYPE_AREA);
								scope.form.GTYPE_AREA = JSON.stringify(scope.GTYPE_AREA);
								if(data.data.GTYPE_STATE == "0"){
									scope.form.GTYPE_STATE = 0;
									$("#GTYPE_STATE_ISSHOW").hide();
								}
							}
							$scope.$apply();
							/*如果当前编辑分类不是顶级加载选择分类数据源*/
						/*	if(scope.form.GTYPE_PID != 0){
								$httpService.post(config.loadGoodsTypeNameURL,scope.form).success(function(data) {
									if(data.code == '0000' && data.data !=null){
										scope.select_pName = [];
										for(var i = 0;i< data.data.length;i++){
											scope.select_pName.push(data.data[i].GTYPE_NAME);
										}
										$scope.$apply();
										console.info(scope.select_pName);
										console.info("GTYPE_Name:"+scope.form.GTYPE_PNAME);
										$("#select_gType_pName").val(scope.form.GTYPE_PNAME);
										comboboxInit();
									}else{
										comboboxInit();
									}
								}).error(function(data) {
									loggingService.info('获取测试信息出错');
								});
							}*/
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				/*第一次进来的数据初始化*/
				loadLastGoodsTypeName();
				scope.toHref = function(path) {
					var m2 = {
							"url" : "aps/content/" + path + "/config.json?fromUrl=" + config.currentUrl + "&GTYPE_PID=" + scope.form.GTYPE_PID+"&Last_Page="+params.Last_Page,
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
				        		"url":"aps/content/goods/updateGoodsType/config.json",
				        		"title":"提示",
				        		"contentName":"modal",
				        		"text":"是否保存"
				        	 }
				    eventBusService.publish(controllerName,"appPart.load.modal",m2);
				}
				/*加载排序序号*/
				function loadGoodsTypeOrder(){
					params.GTYPE_PID = params.GTYPE_PK;
					//发送post请求
					$httpService.post(config.selectGoodsTypePNameByPIDURL,params).success(function(data) {
						/*$scope.$apply();*/
						if(data.code == '0000' && data.data !=null){
							scope.form.GTYPE_NAME = data.data.GTYPE_NAME;
							scope.form.GTYPE_BZ = data.data.GTYPE_NAME;
							scope.form.GTYPE_ORDER = data.data.GTYPE_ORDER;
							scope.form.GTYPE_PK = data.data.GTYPE_PK;
							scope.form.GTYPE_OLD_ORDER = data.data.GTYPE_ORDER;
							$scope.$apply();
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
					$httpService.post(config.loadGoosTypeOrderDataURL,scope.form).success(function(data) {
						if(data.code == '0000' && data.data !=null){
							for(var i = 0;i< data.data.length;i++){
								scope.select_order.push(data.data[i].GTYPE_ORDER);
							}
							$scope.$apply();
							$("#order_select").val(scope.form.GTYPE_ORDER);
							comboboxInit();
						}else{
							comboboxInit();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				/*调用加载排序序号方法*/
				loadGoodsTypeOrder();
				
				
				// 弹窗默认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					scope.form.GTYPE_ORDER = $("#order_select").val();
					$httpService.post(config.updateGoodsTypeURL, $scope.form).success(function(data) {
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
								"toUrl" : "aps/content/goods/config.json?fid=" + scope.form.fid
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