
(function() {
	define([], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

				scope = $scope;


				// 定义页面标题
				scope.pageTitle = '区域分类'


				// 餐桌区域数据源
				scope.tables_area = [
					{
						id : 1,
						name : '大厅',
						count : 5,	//	5人/一桌
						status : 1,   //	1代表已启用 	0代表已停用
						pxxh : 1	//排序序号
					},
					{
						id : 2,
						name : '雅间',
						count : 8,
						status : 1,
						pxxh : 2
					},
					{
						id : 3,
						name : '小包间 ',
						count : 10,
						status : 0,
						pxxh : 3
					},
					{
						id : 4,
						name : '大包间',
						count : 15,
						status : 1,
						pxxh : 4
					}
				];
				
			
				
				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json",
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}

				var init = function() {
					$httpService.post(config.findURL, $scope.form).success(function(data) {
						if (data.code != '0000') {
							loggingService.info(data.data);
						} else {
							$scope.userList = data.data;
							$scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				
				
				
				
				
				
			}
		];
	});

}).call(this);