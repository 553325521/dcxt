
	(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					scope.pageShow = "False";
					scope.form = {};
					scope.pageTitle = config.pageTitle;
					scope.fromUrl = params.fromUrl;
					scope.form.SHOP_ID = params.shopid;
					//购买期限数据源
					buying_select = [];
					//初始化优惠栏不显示
					scope.show_discounts = "False";
					scope.form.PAY_TYPE = 1;
					//之前购买的服务的价格
					pre_service_price = -1;
					scope.deduction_money = 0;
					
					//页面初始化
					var init = function() {
						$httpService.post(config.findUrl,scope.form).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else {
								//服务类型
								scope.service_type = data.data["service_type"];
								service_rule = data.data["service_rule"];
								pre_service_rule = data.data["pre_service_mess"];
								if(pre_service_rule !== null && pre_service_rule !== undefined){
									pre_service_price = pre_service_rule.SERVICE_PRICE;		
									
								}
								scope.pageShow = "True";
								scope.current_service = scope.service_type[0];
								//当前选择的服务类型
								angular.forEach(scope.service_type,function(data, index, array) {
									if(data.SERVICE_PRICE == pre_service_price){
										scope.current_service = scope.service_type[index];
									}
								});
								//form表单初始化服务类型值
								scope.form.SERVICE_ID = scope.current_service.SERVICE_PK;
								//初始化获取优惠之前的总价格
								scope.total_money_before = scope.current_service.SERVICE_PRICE;
								//初始化优惠之后的总价格
								scope.form.TRANSACTION_MONEY = scope.total_money_before;
								
								//把购买优惠规则转换成以月数或年数为key的字典
								service_rule_dictionaries = {};
								angular.forEach(service_rule,function(data, index, array) {
									key = data["BUYSERVICE_RULE_XFSJ"];
									service_rule_dictionaries[key] = data; 
									//购买期限数据源
									buying_select.push(key);
								});
								
								//初始化当前购买时间
								scope.buy_time = buying_select[0];
								scope.form.BUY_TIME = service_rule_dictionaries[scope.buy_time]["BUYSERVICE_RULE_SJYS"];
								scope.$apply();
								comboboxInit();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}
					
					init();
					
					//数据更新了 开始刷新
					scope.refreshPage = function(){
						//获取当前服务类型id
						scope.form.SERVICE_ID = scope.current_service.SERVICE_PK;
						//获取当前选择的购买期数
//						scope.buy_time = $("#buying_select").val();
						//转换当前选择的购买期数为月数
						scope.form.BUY_TIME = service_rule_dictionaries[scope.buy_time]["BUYSERVICE_RULE_SJYS"];
						//计算当前选择的总价钱，折扣前
						scope.total_money_before = scope.current_service.SERVICE_PRICE * scope.form.BUY_TIME;
						//判断当前购买期数是否享有优惠
						if(service_rule_dictionaries.hasOwnProperty(scope.buy_time)){
							//优惠几个月
							scope.discounts_month = service_rule_dictionaries[scope.buy_time]["BUYSERVICE_RULE_YHYS"];
							//优惠多少钱
							scope.discounts_money = scope.discounts_month * scope.current_service.SERVICE_PRICE;
							//优惠后多少钱
							scope.form.TRANSACTION_MONEY = scope.total_money_before - scope.discounts_money;
						}else{
							scope.discounts_money = 0
							//获取优惠之后的总价格
							scope.form.TRANSACTION_MONEY = scope.total_money_before;
						}
						scope.form.TRANSACTION_MONEY -= scope.deduction_money;
						scope.form.TRANSACTION_MONEY = scope.form.TRANSACTION_MONEY < 0 ? 0 : scope.form.TRANSACTION_MONEY;
						scope.discounts_show = scope.discounts_money == 0 ? "False":"True";
						scope.$apply();
					}
					
					//微信支付函数
					function onBridgeReady(data){
						   WeixinJSBridge.invoke(
						      'getBrandWCPayRequest', {
						         "appId": data.apiAppid,     //公众号名称，由商户传入     
						         "timeStamp": data.apiTimestamp,         //时间戳，自1970年以来的秒数     
						         "nonceStr": data.apiNoncestr, //随机串     
						         "package": data.apiPackage,     
						         "signType": data.apiSigntype, //微信签名方式：     
						         "paySign": data.apiPaysign //微信签名 
						      },
						      function(res){
						      if(res.err_msg == "get_brand_wcpay_request:ok" ){
						      // 使用以上方式判断前端返回,微信团队郑重提示：
						            //res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
						    		var m2 = {
											"title" : "提示",
											"contentName" : "modal",
											"text" : "支付成功",
											"toUrl" : "aps/content/ActingCustomerManagement/config.json"
										}
								
								eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						      } 
						      console.info(res)
						   }); 
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
					
					
					scope.selectPay = function(type){
						scope.form.PAY_TYPE = type;
					}
					
					// 弹窗确认事件
					eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
						$httpService.post(config.saveUrl, $scope.form).success(function(data) {
							if (data.code == '0000') {
								var m2 = {
										"title" : "提示",
										"contentName" : "modal",
										"text" : data.data,
										"toUrl" : "aps/content/ActingCustomerManagement/config.json"
									}
							} else if(data.code == '5555'){
								//取消弹窗
								eventBusService.publish(controllerName, 'appPart.load.modal.close', {
									contentName : "modal"
								});
								//发起微信支付
								if (typeof WeixinJSBridge == "undefined"){
									   if( document.addEventListener ){
									       document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
									   }else if (document.attachEvent){
									       document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
									       document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
									   }
									}else{
									   onBridgeReady(data.data);
									}
								return;
							}else{
								var m2 = {
										"title" : "提示",
										"contentName" : "modal",
										"text" : data.data
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
							],
							onChange : function(e) {
								if (e != undefined && e.value[0] != undefined) {
									var value = e.value[0]
									scope.buy_time = value;
									scope.refreshPage();
								}
							}
						});
						
						$(".dcxt-shopselects").on('click','.dcxt-shopselect',function(){
							serviceMess = scope.service_type[$(this)[0].dataset.value];
							if(serviceMess.SERVICE_PRICE < pre_service_price){
								$.toptips('升级服务不允许降级购买')
								return;
							}
							
							if(serviceMess.SERVICE_PRICE > pre_service_price && pre_service_price != -1){
								scope.deduction_show = "True"
								scope.deduction_money = pre_service_rule.deduction_money
							}else{
								scope.deduction_show = "False";
								scope.deduction_money = 0;
							}
							$(this).addClass("dcxt-shopselect-on");
							$(this).siblings().removeClass("dcxt-shopselect-on");
							scope.current_service =  serviceMess;
							//页面数据变化，刷新数据
							scope.refreshPage();
						})
						
					}
				}
			];
		});
	}).call(this);
	
$(function() {})