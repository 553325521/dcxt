

(function() {
	define(['zepto','slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

	scope = $scope;


	// 定义页面标题
	scope.pageTitle = '餐桌设置'
		
	cid = params.cid;

	// 餐桌区域数据源
	scope.tables_list = [
		{
			id : 1,
			name : "01",	//桌子名字
			status : 1,   //1代表已启用 	0代表已停用,
			count : 5,	//5人/一桌
			pxxh : 1,
			sjc : "1587411",
			desc : "没备注"
		},
		{
			id : 2,
			name : "02",	//桌子名字
			status : 1,   //1代表已启用 	0代表已停用,
			count : 6,	//5人/一桌
			pxxh : 2,
			sjc : "1587411",
			desc : "没备注"
		},
		{
			id : 3,
			name : "03",	//桌子名字
			status : 1,   //1代表已启用 	0代表已停用,
			count : 6,	//5人/一桌
			pxxh : 3,
			sjc : "1587411",
			desc : "没备注"
		},
		{
			id : 4,
			name : "04",	//桌子名字
			status : 0,   //1代表已启用 	0代表已停用,
			count : 6,	//5人/一桌
			pxxh : 4,
			sjc : "1587411",
			desc : "没备注"
		},
		{
			id : 5,
			name : "05",	//桌子名字
			status : 1,   //1代表已启用 	0代表已停用,
			count : 10,	//5人/一桌
			pxxh : 5,
			sjc : "1587411",
			desc : "没备注"
		},
	];
	
	
	
	//跳转链接
	scope.toHref = function(path) {
		var m2 = {
			"url" : "aps/content/" + path + "/config.json",
			"size" : "modal-lg",
			"contentName" : "content"
		}
		eventBusService.publish(controllerName, 'appPart.load.content', m2);
	}
	
	
	
	
	//添加或者修改
	scope.addOrChange = function(index) {
		//初始化要传输的数据
		tables_data_list = {}
		console.info(index)
		//如果是修改
		if(index != null && index != ''){
			tables_data_list.status = "0000";
			tables_data_list.tables_form = angular.toJson(scope.tables_list[index]);
			tables_data_list.tables_count = scope.tables_list.length;
			tables_data_list.tables_index = index;
			
		}else{
			tables_data_list.status = "1111";
			tables_data_list.tables_count = scope.tables_list.length;
		}
		
		var m2 = {
			"url" : "aps/content/DailyManagement/tables/catelist/tableslist/setting/config.json?tables_data_list=" + angular.toJson(tables_data_list)+"&cid=" + cid,
			"size" : "modal-lg",
			"contentName" : "content"
		}
		eventBusService.publish(controllerName, 'appPart.load.content', m2);
	}
	
	
	
	//根据索引删除当前元素
	scope.tablesDelete = function(index){
		console.info(index)
		current_tables_list = scope.tables_list[index];
		
		$.confirm("您确定要删除 " + current_tables_list.name + " 吗?", "确认删除?", function() {
	        	
	        //先发送请求删除，再本地删除
			//TODO
		       
			scope.tables_list.splice(index,1);
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

$(function() {})