
(function() {
	define(['jqueryweui','pickercity'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				
				//初始化form表单
				scope.form={}
				
				//商铺类型
				shop_type_first = ['餐饮行业','水果行业'];
				shop_type_second = ['火锅','垃圾'];
				
				
				
				var init = function() {
					//获取传过来的数据
					shop_id = params.shopid
					//判断
					if(shop_id != null && shop_id != ''){//是修改
						// 定义页面标题
						scope.pageTitle = '修改商户';	
						//发送post请求
						$httpService.post(config.findURL,params).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else {
								scope.form = data.data;//获取商铺信息
								
								//初始化商铺类型
								//分割商铺类型
								shop_type = scope.form.SHOP_TYPE.split(" ");
								scope.form.SHOP_TYPE_FIRSET = shop_type[0];
								scope.form.SHOP_TYPE_SECOND = shop_type[1];
								
								//这几行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
								$("#fs_select").val(scope.form.SHOP_TYPE_FIRSET);
								$("#fs_select1").val(scope.form.SHOP_TYPE_FIRSET);
								
								//初始化地区
								//这一行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
								$("#ssx").val(scope.form.SHOP_AREA);
								
								$scope.$apply();
								
								//开始初始化下拉框数据
								comboboxInit();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}else{
						// 定义页面标题
						scope.pageTitle = '添加商户';	
						scope.form.SHOP_AREA="北京 北京 东城区"
						//商类型初始化
						scope.form.SHOP_TYPE_FIRSET = shop_type_first[0];
						scope.form.SHOP_TYPE_SECOND = shop_type_second[0];
						
						//这几行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
						$("#fs_select").val(shop_type_first[0]);
						$("#fs_select1").val(shop_type_second[0]);
						
						//初始化店铺类型
						initShopVersion();
						//初始化下拉框
						comboboxInit();
					}
					
				}
				//页面初始化
				init();
				
				
				scope.shop_version_value = new Array();
				scope.shop_version_text = new Array();
				
				function initShopVersion(){
					$httpService.post(config.findVersionURL, null).success(function(data) {
						scope.shop_version = data.data;
						for(var i = 0;i< scope.shop_version.length;i++){
							scope.shop_version_value[i] = scope.shop_version[i].SERVICE_PK;
							scope.shop_version_text[i] = scope.shop_version[i].SERVICE_TYPE;
						}
						/*scope.form.VERSION = scope.shop_version_text[1];*/
						$("#version_select").val(scope.shop_version_text[0]);
						$("#version_select").picker({
							title : "选择类型",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values : scope.shop_version_text,
									displayValues : scope.shop_version_text
								}
							]
						});
					})
				}
				
				
				
				scope.toHref = function(path,cid) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?cid=" + cid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				var $form = $("#SHOPFORM");
				$form.form();
				scope.doSave = function(){
					
					//解决下拉框数据不同步
					scope.form.SHOP_AREA = $("#ssx").val();
					scope.form.SHOP_TYPE_FIRSET = $("#fs_select").val();
					scope.form.SHOP_TYPE_SECOND = $("#fs_select1").val();
					scope.form.SERVICE_TYPE = $("#version_select").val();
					
					 $form.validate(function(error){
				        if(!error){
					        	 var m2 = {
					        		"url":"aps/content/ActingCustomerManagement/addBusiness/config.json",
					        		"title":"提示",
					        		"contentName":"modal",
					        		"text":"是否保存"
					        	 }
					        	 eventBusService.publish(controllerName,"appPart.load.modal",m2);
					        }
					 });
				}
				
				
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
								"toUrl" : "aps/content/ActingCustomerManagement/config.json?fid=" + scope.form.fid
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
				
				
				
				
				
				//下拉框的初始化
				function comboboxInit() {
					$("#ssx").cityPicker({
				        title: "选择省市县",
				        toolbarCloseText : '完成'
				     });
					
					$("#fs_select").picker({
						title : "选择类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : shop_type_first,
								displayValues : shop_type_first
							}
						]
					});
					$("#fs_select1").picker({
						title : "选择类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : shop_type_second,
								displayValues : shop_type_second
							}
						]
					});
				}
				
			}
		];
	});
}).call(this);

$(function() {
	
})