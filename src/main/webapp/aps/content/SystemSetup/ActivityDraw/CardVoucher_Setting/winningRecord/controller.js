(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					//初始化form表单
					scope.record_list = [];
					scope.pageShow = "False";
					scope.pageTitle = config.pageTitle;
					scope.click_select = 1;
					//初始化选择框内容
					scope.card_voucher_list = [];
					scope.form = {};
					scope.form.card_status = 0;
					scope.form.current_timestamp = "";
					
					var init = function(){
						$httpService.post(config.findURL,scope.form).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else if(data.code == '9999'){
								$.toptips(data.data);
							}else{
								scope.pageShow = "True";
								console.info(data.data)
								scope.card_voucher_list = data.data;
								/*initPicker();*/
								scope.$apply();
							}
							console.info(data);
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
						
					}
					
					init();
					
					scope.pageShow = "True";
					
					//选中事件
					scope.click = function(index){
						scope.click_select = index
						if(index == 1){
							scope.card_voucher_list = [];
							scope.form.card_status = 0;
							$httpService.post(config.findURL,scope.form).success(function(data) {
								if (data.code != '0000') {
									loggingService.info(data.data);
								} else if(data.code == '9999'){
									$.toptips(data.data);
								}else{
									scope.pageShow = "True";
									console.info(data.data)
									scope.card_voucher_list = data.data;
									/*initPicker();*/
									scope.$apply();
								}
								console.info(data);
							}).error(function(data) {
								loggingService.info('获取测试信息出错');
							});
						}else if(index == 2){
							scope.card_voucher_list = [];
							scope.form.card_status = 1;
							$httpService.post(config.findURL,scope.form).success(function(data) {
								if (data.code != '0000') {
									loggingService.info(data.data);
								} else if(data.code == '9999'){
									$.toptips(data.data);
								}else{
									scope.pageShow = "True";
									console.info(data.data)
									scope.card_voucher_list = data.data;
									/*initPicker();*/
									scope.$apply();
								}
								console.info(data);
							}).error(function(data) {
								loggingService.info('获取测试信息出错');
							});
						}else{
							scope.card_voucher_list = [];
							scope.form.card_status = 0;
							/*scope.form.current_timestamp = Math.floor(new Date().getTime()/1000);1537372800*/
							scope.form.current_timestamp = "1537372838";
							$httpService.post(config.findURL,scope.form).success(function(data) {
								if (data.code != '0000') {
									loggingService.info(data.data);
								} else if(data.code == '9999'){
									$.toptips(data.data);
								}else{
									scope.pageShow = "True";
									console.info(data.data)
									scope.card_voucher_list = data.data;
									/*initPicker();*/
									scope.$apply();
								}
								console.info(data);
							}).error(function(data) {
								loggingService.info('获取测试信息出错');
							});
						}
						
					}
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
			}
		];
	});
}).call(this);
$(function() {
})