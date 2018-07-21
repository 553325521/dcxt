
	(function() {
		define(['slideleft'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					
					
					
					
					
					
					
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
					
					
					//测试，可以删
					
					scope.addOrChange = function(a){
						console.info(a);
					}
					
					
					
					
					
				}
			];
		});
	}).call(this);

	$(function() {
		$('.searchbar_wrap').searchBar({
		    cancelText:"取消",
		    searchText:'关键字',
		    onfocus: function (value) {
		        
		    },
		    onblur:function(value) {

		    },
		    oninput: function(value) {
		 
		    },
		    onsubmit:function(value){
		    },
		    oncancel:function(){

		    },

		    onclear:function(){

		    }
		});
	})