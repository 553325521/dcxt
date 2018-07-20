
(function() {
	define(['zepto','slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService','$rootScope',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService,$rootScope) {

				scope = $scope;


				// 定义页面标题
				scope.pageTitle = '区域分类'


				// 餐桌区域数据源
				scope.tables_area_list = [
					{
						id : 1,
						name : '大厅',
						count : 5,	//	5人/一桌
						status : 1,   //	1代表已启用 	0代表已停用
						pxxh : 1,	//排序序号
						sjc : 1532074528,
						desc:"大厅的桌子",
					},
					{
						id : 2,
						name : '雅间',
						count : 8,
						status : 1,
						pxxh : 1,
						sjc : 1532074689,
						desc:"大厅的桌子",
					},
					{
						id : 3,
						name : '小包间 ',
						count : 10,
						status : 0,
						pxxh : 3,
						sjc : 1532074789,
						desc:"大厅的桌子",
					},
					{
						id : 4,
						name : '大包间',
						count : 15,
						status : 1,
						pxxh : 4,
						sjc : 1532074889,
						desc:"",
					}
				];
				
			
				//添加或者修改
				scope.addOrChange = function(index) {
					console.info("addOrChange: "+index)
					//初始化要传输的数据
					tables_data_list = {}
					console.info(index)
					//如果是修改
					if(index != null && index != ''){
						tables_data_list.status = "0000";
						tables_data_list.tables_form = angular.toJson(scope.tables_area_list[index]);
						tables_data_list.tables_area_count = scope.tables_area_list.length;
						tables_data_list.tables_index = index;
						
					}else{
						tables_data_list.status = "1111";
						tables_data_list.tables_area_count = scope.tables_area_list.length;
					}
					
					var m2 = {
						"url" : "aps/content/DailyManagement/tables/catelist/setting/config.json?tables_data_list=" + angular.toJson(tables_data_list),
						"size" : "modal-lg",
						"contentName" : "content"
					}
					console.info(tables_data_list)
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				
				
				scope.toHref = function(path,cid) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?cid="+cid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				

//				var init = function() {
//					$httpService.post(config.findURL, $scope.form).success(function(data) {
//						if (data.code != '0000') {
//							loggingService.info(data.data);
//						} else {
//							$scope.userList = data.data;
//							$scope.$apply();
//						}
//					}).error(function(data) {
//						loggingService.info('获取测试信息出错');
//					});
//				}
				
				
				//根绝索引删除当前元素
				scope.tablesDelete = function(index){
					console.info(index)
					current_tables_area = scope.tables_area_list[index];
					
					$.confirm("您确定要删除 " + current_tables_area.name + " 吗?", "确认删除?", function() {
				        	
				        //先发送请求删除，再本地删除
						//TODO
					       
						scope.tables_area_list.splice(index,1);
					    //刷新数据	
						scope.$apply();	
					        		
					      //请求成功开始删除
						$.toast("删除成功!");
					        
				      }, function() {
				        	//闭合滑块
				        	$('.slideleft_cell_bd').css('-webkit-transform', 'translateX(0px)');
				        	
				      });
					
				}
				
				
			}
		];
	});

}).call(this);