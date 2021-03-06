(function() {
	define(['zepto','slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				$scope.pageTitle = config.pageTitle;
				$scope.form = {};
				
				$scope.addOrChange = function(obj){
					var m2 = {
							"url" : "aps/content/advnceSet/BasicSetting/favourablePay/baseEdit/config.json?fid=" + params.fid+"&pk="+obj.preferential_rule_pk,
							"size" : "modal-lg",
							"contentName" : "content"
						}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
					/*$scope.$apply();*/
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
							"url" : "aps/content/advnceSet/BasicSetting/favourablePay/baseAdd/config.json?fid=" + params.fid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
							
				//根绝索引删除当前元素
				$scope.tablesDelete = function(obj){
//					$.confirm("您确定要删除 " + obj.rule_name + " 吗?", "确认删除?", function() {
				        
						$scope.form.rulePk = obj.preferential_rule_pk;
						$httpService.post(config.deleteURL, $scope.form).success(function(data) {
							if (data.code === '0000') {
								$.toast("删除成功!");
								//闭合滑块
					        	$('.slideleft_cell_bd').css('-webkit-transform', 'translateX(0px)');
					        	$scope.form.rulePk ='';
								init();
							} else {
								
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
//						
//					        
//				      }, function() {
//				        	//闭合滑块
//				        	$('.slideleft_cell_bd').css('-webkit-transform', 'translateX(0px)');
//				        	
//				      });
					
				}
				
				$scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				

				var getBase = function(){
					$scope.form.shopId = $scope.form.userInfo.shopId;
					$scope.form.userId = $scope.form.userInfo.userId;
					$httpService.post(config.getBaseInfo, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.dataList = data.data;
							for(var i = 0;i<$scope.dataList.length;i++){
								var rule = $scope.dataList[i].rule_model;
								var jsonArray = JSON.parse(rule);
								$scope.dataList[i].rule_model = jsonArray[0]["YH_WAY"];
							}
							console.info($scope.dataList);
							$scope.$apply();
						} else {
							
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				
				var init = function(){
					getBase();
				}
				
				var getUserInfo = function(){
					$httpService.post(config.getUserInfo, $scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.form.userInfo = data.data;
							console.log($scope.form);
							init();
						} else {
							
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				getUserInfo();
				
				
			}
		];
	});
}).call(this);