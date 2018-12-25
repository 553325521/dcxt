
(function() {
	define(['jqueryweui','pickercity'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				
				//初始化form表单
				scope.form={}
				
				//是否连锁是否显示(添加时候显示)
				scope.isAddShowCHAIN = "True";
				//是否连锁默认值
				scope.form.SHOP_IS_CHAIN = '0';
				/*//商铺类型
				shop_type_first = ['餐饮行业','水果行业'];
				shop_type_second = ['火锅','垃圾'];*/
				
				//初始化店铺类型
				shop_type_list = {
						"美食":["粤菜","茶餐厅","川菜","湘菜","东北菜","西北菜","火锅","自助餐","小吃","快餐","日本料理","韩国料理","东南亚菜","西餐","面包甜点","咖啡厅","江浙菜","其他美食","外卖","其他"],
						"休闲娱乐":["冷饮","茶馆","茶楼","酒吧","其他"],
						"购物":["超市","便利店","水果超市","鲜花礼品","酒类","其他"]
				}
				
				
				//初始化店铺类型1
				shop_type_first = [];
				for(var key in shop_type_list){
					shop_type_first.push(key)
				}
				
				//刷新第二个下拉框
				var flushShopType2 = function(){
					//给第二个下拉框赋默认值
					scope.form.SHOP_TYPE_SECOND = shop_type_list[scope.form.SHOP_TYPE_FIRSET][0]
					$("#SHOP_TYPE_2").val(scope.form.SHOP_TYPE_SECOND)
					
					//填充下拉框
					$("#SHOP_TYPE_2").picker({
						title : "商铺类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : shop_type_list[scope.form.SHOP_TYPE_FIRSET]
							}
						],
					});
				} 
				
				
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
								//是否连锁是否显示(更新时候不显示)
								scope.isAddShowCHAIN = "False";
								//初始化商铺类型
								//分割商铺类型
								scope.form.SHOP_ID = shop_id;
								shop_type = scope.form.SHOP_TYPE.split(" ");
								scope.form.SHOP_TYPE_FIRSET = shop_type[0];
								scope.form.SHOP_TYPE_SECOND = shop_type[1];
								
								//这几行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
								$("#SHOP_TYPE_1").val(scope.form.SHOP_TYPE_FIRSET);
								$("#SHOP_TYPE_2").val(scope.form.SHOP_TYPE_SECOND);
								
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
							
						//初始化商铺类型下拉框
						scope.form.SHOP_TYPE_FIRSET = shop_type_first[0]
						scope.form.SHOP_TYPE_SECOND = shop_type_list[scope.form.SHOP_TYPE_FIRSET][0]
						$("#SHOP_TYPE_2").val(scope.form.SHOP_TYPE_SECOND)
						$("#SHOP_TYPE_1").val(scope.form.SHOP_TYPE_FIRSET)
						
						
						//初始化下拉框
						comboboxInit();
						
						//刷新商铺类型第二个下拉框
						flushShopType2()
					}
				}
				//页面初始化
				init();
				
				scope.shop_version_value = new Array();
				scope.shop_version_text = new Array();
				
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
					scope.form.SHOP_TYPE_FIRSET = $("#SHOP_TYPE_1").val();
					scope.form.SHOP_TYPE_SECOND = $("#SHOP_TYPE_2").val();
//					scope.form.SERVICE_TYPE = $("#version_select").val();
					
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
					
					$("#SHOP_TYPE_1").picker({
						title : "选择类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : shop_type_first,
								displayValues : shop_type_first
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								console.info(value)
								scope.form.SHOP_TYPE_FIRSET = value
								
								if(shop_type_list[value].indexOf(scope.form.SHOP_TYPE_SECOND) == -1){
									//刷新商铺类型第二个下拉框
									flushShopType2()
								}
									
							}
						}
						
						
					});
				}
				
			}
		];
	});
}).call(this);

$(function() {
	
})