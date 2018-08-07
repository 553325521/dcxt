
	(function() {
		define([], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					// 定义页面标题
					scope.pageTitle = '提现记录';	
				
					scope.yearMonth = '2016-06';
					
					scope.total = '1280元';
					
					scope.form = {};
					
					/*scope.record_list = [{
						monthDay:'07-23',
						time:'17:30',
						type:'微信提现',
						money:'1200.00元',
						state:'待结算'
					},{
						monthDay:'07-23',
						time:'17:30',
						type:'微信提现',
						money:'1200.00元',
						state:'待结算'
					},{
						monthDay:'07-23',
						time:'17:30',
						type:'微信提现',
						money:'1200.00元',
						state:'待结算'
					},{
						monthDay:'07-23',
						time:'17:30',
						type:'微信提现',
						money:'1200.00元',
						state:'待结算'
					}];*/
					
					scope.record_list = [];
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					function init_record_list(){
						$httpService.post(config.findCashRecordURL, $scope.form).success(function(data) {
							scope.record_list = data.data;
							scope.$apply();
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
					}
					init_record_list();
				}
			];
		});
	}).call(this);

	$(function() {
	})