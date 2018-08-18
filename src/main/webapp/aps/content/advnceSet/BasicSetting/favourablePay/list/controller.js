(function() {
	define(['zepto','slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				$scope.pageTitle = config.pageTitle;
				$scope.form = {};
				
				$scope.addOrChange = function(obj){
					var m2 = {
							"url" : "aps/content/advnceSet/BasicSetting/favourablePay/edit/config.json?pk="+obj.preferntial_pk,
							"size" : "modal-lg",
							"contentName" : "content"
						}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
					//$scope.$apply();
				}
				
				// 餐桌区域数据源
				$scope.tables_area_list = [
					{
						id : 1,
						name : '上午95折优惠	',
						count : 5,	//	5人/一桌
						status : 1,   //	1代表已启用 	0代表已停用
						pxxh : 1,	//排序序号
						sjc : 1532074528,
						desc:"大厅的桌子",
					},
					{
						id : 2,
						name : '下午8折优惠',
						count : 8,
						status : 1,
						pxxh : 1,
						sjc : 1532074689,
						desc:"大厅的桌子",
					}
				];
				
				$scope.add = function(){
					var m2 = {
							"url" : "aps/content/advnceSet/BasicSetting/favourablePay/add/config.json",
							"size" : "modal-lg",
							"contentName" : "content"
						}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
							
				//根绝索引删除当前元素
				$scope.tablesDelete = function(obj){
					
					$.confirm("您确定要删除 " + obj.name + " 吗?", "确认删除?", function() {
						$scope.form.favPk = obj.preferntial_pk;
						$httpService.post(config.delFavourListURL, $scope.form).success(function(data) {
							if (data.code === '0000') {
								$.toast("删除成功!");
								init();
							} else {
								
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
				      }, function() {
				        	//闭合滑块
				        	$('.slideleft_cell_bd').css('-webkit-transform', 'translateX(0px)');
				        	
				      });
					
				}
				
				$scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				
				
				var getListInfo = function(){
					$httpService.post(config.getFavourListURL, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.dataList = data.data;
							$scope.$apply();
						} else {
							
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				var getUserInfo = function(){
					$httpService.post(config.getUserInfo, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.form.userInfo = data.data;
							$scope.form.userId = $scope.form.userInfo.userId;
							//$scope.form.shopId = $scope.form.userInfo.shopId;
							getListInfo();
						} else {
							
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				var init = function(){
					getUserInfo();
				}
				init();
				
			}
		];
	});
}).call(this);