
(function() {
	define(['jqueryweui','pickercity'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				// 弹窗默认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					
					
					$httpService.post(config.addURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data,
								"toUrl" : "aps/content/ActingCustomerManagement/addBusiness/config.json?fid=" + scope.form.fid
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
				/*	var m2 = {
							"title" : "提示",
							"contentName" : "modal",
							"text" : "支付成功",
							"toUrl" : "aps/content/ActingCustomerManagement/config.json"
						}
					eventBusService.publish(controllerName, 'appPart.load.modal', m2);*/
					

					
				});
				// 弹窗取消事件
				eventBusService.subscribe(controllerName, controllerName + '.close', function(event, btn) {
					eventBusService.publish(controllerName, 'appPart.load.modal.close', {
						contentName : "modal"
					});
				});
				
				
				
				
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '添加商户';	
				
				scope.shop_type_first = ['餐饮行业','水果行业'];
				
				scope.shop_type_second = ['火锅','垃圾'];
				
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
				
				initShopVersion();
				
				scope.form = {};
				
				scope.form.SHOP_TYPE_FIRSET = scope.shop_type_first[0];
				
				scope.form.SHOP_TYPE_SECOND = scope.shop_type_second[0];
				
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
				
				scope.toHref = function(path,cid) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?cid=" + cid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				comboboxInit();
				var $form = $("#SHOPFORM");
				$form.form();
				scope.doSave = function(){
					scope.form.SHOPAREA = $("#ssx").val();
					scope.form.SHOP_TYPE_FIRSET = $("#fs_select").val();
					scope.form.SHOP_TYPE_SECOND = $("#fs_select1").val();
					scope.form.SIZE = "";
					scope.form.VERSION = $("#version_select").val();
					for(var i = 0;i < scope.shop_version_text.length;i++){
						if(scope.form.VERSION == scope.shop_version_text[i]){
							scope.form.SIZE = scope.shop_version_value[i];
							break;
						}
					}
					 $form.validate(function(error){
				        if(!error){
					           /* $.toptips('验证通过提交','ok');*/
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
				
			}
		];
	});
}).call(this);

$(function() {
	
})