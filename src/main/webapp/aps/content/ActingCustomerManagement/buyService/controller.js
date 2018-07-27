
	(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					scope.pageShow = "False"
					scope.form = {}
					scope.pageTitle = config.pageTitle;
					scope.fromUrl = params.fromUrl;
					scope.form.SHOP_ID = params.shopid;
					scope.form.TRANSACTION_TYPE = 1;//初始化交易类型 
					if(scope.fromUrl == "ActingCustomerManagement"){
						scope.form.TRANSACTION_TYPE = 0;
					}
					
					
					//页面初始化
					var init = function() {
						$httpService.post(config.findUrl).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else {
								//服务类型
								scope.service_type = data.data;
								scope.pageShow = "True"
								//转换以版本类型为key的map字典
								angular.forEach(scope.service_type,function(data,index,array){
									service_type_dictionaries[data.SERVICE_TYPE] = data;
								});
								//当前选择的服务类型
								scope.current_service = scope.service_type[0];
								//form表单初始化服务类型值
								scope.form.SERVICE_ID = scope.current_service.SERVICE_PK;
								//初始化获取优惠之前的总价格
								scope.total_money_before = scope.service_type[0].SERVICE_PRICE;
								//初始化优惠栏显示不显示
								scope.show_discounts = "False";
								//初始化优惠之后的总价格
								scope.form.TRANSACTION_MONEY = scope.total_money_before;
								scope.$apply();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}
					
					init();
					
					// 购买期限数据源
					buying_select = ['1月','2月','3月','4月','5月','6月','7月','8月','9月','1年','2年','3年','4年','5年'];
					//购买期数对应月数的字典
					buying_month = {"1月":1,"2月":2,"3月":3,"4月":4,"5月":5,"6月":6,"7月":7,"8月":8,"9月":9,"1年":12,"2年":24,"3年":36,"4年":48,"5年":60}
					
					//当前选择的服务版本
					scope.current_service = []
					//以版本类型为key的map字典
					service_type_dictionaries = [];
					//平台优惠策略
					service_discounts_table = {
							"1年" : {
								"discounts_month" : "2",		//优惠月份
							} ,
							"2年" : {
								"discounts_month" : "5",
							} ,
							"3年" : {
								"discounts_month" : "9",
							} ,
							"4年" : {
								"discounts_month" : "12",
							}  ,
							"5年" : {
								"discounts_month" : "15",
							} 
					}
					
					//初始化当前购买时间
					scope.buy_time = buying_select[0];
					scope.form.BUY_TIME = buying_month[scope.buy_time];
//					
					comboboxInit();
					
					//数据更新了 开始刷新
					scope.refreshPage = function(){
						//获取当前服务类型id
						scope.form.SERVICE_ID = scope.current_service.SERVICE_PK;
						//获取当前选择的购买期数
						scope.buy_time = $("#buying_select").val();
						//转换当前选择的购买期数为月数
						scope.form.BUY_TIME = buying_month[scope.buy_time];
						//计算当前选择的总价钱，折扣前
						scope.total_money_before = scope.current_service.SERVICE_PRICE * scope.form.BUY_TIME;
						//判断当前购买期数是否享有优惠
						if(scope.form.BUY_TIME >= 12){
							//优惠几个月
							scope.discounts_month = service_discounts_table[scope.buy_time]["discounts_month"];
							//优惠多少钱
							scope.discounts_money = scope.discounts_month * scope.current_service.SERVICE_PRICE;
							//优惠后多少钱
							scope.form.TRANSACTION_MONEY = scope.total_money_before - scope.discounts_money;
						}else{
							scope.discounts_money = 0
							//获取优惠之后的总价格
							scope.form.TRANSACTION_MONEY = scope.total_money_before;
						}
						scope.discounts_show = scope.discounts_money == 0 ? "False":"True";
						scope.$apply();
					}
					
					//支付按钮
					scope.confirmPayment = function(){
						console.info(scope.form)
						var m2 = {
							"url" : "aps/content/ActingCustomerManagement/buyService/config.json",
							"title" : "提示",
							"contentName" : "modal",
							"text" : "是否支付?"
						}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
					}
					
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json",
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					// 弹窗确认事件
					eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
						$httpService.post(config.saveUrl, $scope.form).success(function(data) {
							if (data.code != '0000') {
								var m2 = {
									"title" : "提示",
									"contentName" : "modal",
									"text" : data.data
								}
							} else {
								var m2 = {
									"title" : "提示",
									"contentName" : "modal",
									"text" : data.data,
									"toUrl" : "aps/content/ActingCustomerManagement/config.json"
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
					
					function comboboxInit(){
						$("#buying_select").picker({
							title : "购买期限",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values : buying_select,
									displayValues : buying_select
								}
							]
						});
						
						$(".dcxt-shopselects").on('click','.dcxt-shopselect',function(){
							$(this).addClass("dcxt-shopselect-on");
							$(this).siblings().removeClass("dcxt-shopselect-on");
							scope.current_service =  scope.service_type[$(this)[0].dataset.value];
							//页面数据变化，刷新数据
							scope.refreshPage()
						})
						
						document.getElementById("buying_select").onchange = function(event) {
							scope.refreshPage()
						};
					}
				}
			];
		});
	}).call(this);
	
$(function() {})