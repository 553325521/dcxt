
	(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					
					
					scope.pageTitle = config.pageTitle;
					
					scope.form = {}
					
					
					// 购买期限数据源，单位/月 
					buying_select = ['1','2','3','6','12','24','60','120'];
					
					buying_select_transform = []
					
					angular.forEach(buying_select,function(data,index,array){
						if(data % 12 == 0){
							buying_select_transform.push(data/12 + '年');
						}else{
							buying_select_transform.push(data + '月')
						}
						
					});
					
					
					scope.form.BS_BUYINGPERIOD = buying_select_transform[0];
					
					
					
					
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json",
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					$httpService.post('json/ServiceType_queryForList_findServiceTypeList').success(function(data) {
						if (data.code != '0000') {
							console.info(data)
						} else {
							console.info(data)
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
					
					
					
					
					
					
					scope.confirmPayment = function(){
						
						/*var $form = $("#addTagForm");
						$form.form();
						$form.validate(function(error) {*/
							/*if (!error) {*/
								var m2 = {
									"url" : "aps/content/ActingCustomerManagement/buyService/config.json",
									"title" : "提示",
									"contentName" : "modal",
									"text" : "是否确定保存?"
								}
								eventBusService.publish(controllerName, 'appPart.load.modal', m2);
							/*}*/
						/*})*/
						
						
					}
					
					
					
					// 弹窗确认事件
					eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
						
						
						
						
						
						
						var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : "支付成功",
								"toUrl" : "aps/content/ActingCustomerManagement/config.json"
							}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						

						
						
						
						
						
//						$httpService.post(config.saveURL, $scope.form).success(function(data) {
//							if (data.code != '0000') {
//								var m2 = {
//									"title" : "提示",
//									"contentName" : "modal",
//									"text" : data.data,
//									"toUrl" : "aps/content/SystemSetup/BasicSetting/userTag/config.json?fid=" + scope.form.fid
//								}
//							} else {
//								var m2 = {
//									"title" : "提示",
//									"contentName" : "modal",
//									"text" : data.data,
//									"toUrl" : "aps/content/SystemSetup/BasicSetting/userTag/config.json?fid=" + scope.form.fid
//								}
//							}
//							eventBusService.publish(controllerName, 'appPart.load.modal', m2);
//						}).error(function(data) {
//							loggingService.info('获取测试信息出错');
//						});

					});
					
					
					
					// 弹窗取消事件
					eventBusService.subscribe(controllerName, controllerName + '.close', function(event, btn) {
						eventBusService.publish(controllerName, 'appPart.load.modal.close', {
							contentName : "modal"
						});
					});
					
					
			
					
					
					comboboxInit();
					
				}
			];
		});
	}).call(this);
	
	
	function comboboxInit(){
		
	
		$("#buying_select").picker({
			title : "购买期限",
			toolbarCloseText : '确定',
			cols : [
				{
					textAlign : 'center',
					values : buying_select_transform,
					displayValues : buying_select_transform
				}
			]
		});
		
		$(".dcxt-shopselect").on('click',function(){
			$(this).addClass("dcxt-shopselect-on");
			$(this).siblings().removeClass("dcxt-shopselect-on");
			return false;
		})
		
		
	}


	$(function() {
		
	})