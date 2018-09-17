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
					//初始化选择框内容
					scope.turntable_name_list = ['全部转盘'];
					scope.turntableCate = scope.turntable_name_list[0]
					var init = function(){
						$httpService.post(config.findURL).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else if(data.code == '9999'){
								$.toptips(data.data);
							}else{
								scope.pageShow = "True";
								console.info(data.data)
								scope.turntable_record = data.data.turntable_record;
								scope.turntable_name_list = data.data.turntable_name_list;
								scope.turntable_name_list.unshift('全部转盘')
								initPicker();
								scope.$apply();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
						
					}
					
					init();
					
					//初始化选择框
					var initPicker = function(){
						$("#filter_select").picker({
							title : "选择会员",
							cols : [
								{
									textAlign : 'center',
									values : scope.turntable_name_list,
									displayValues : scope.turntable_name_list
								}
							],
							onChange : function(e){
								console.info(e.value[0])
								if(e.value[0] == scope.turntable_name_list[0]){
									scope.filter_select = ""
								}else{
									scope.filter_select = e.value[0]
								}
								
								scope.$apply()
							}
						});
						
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