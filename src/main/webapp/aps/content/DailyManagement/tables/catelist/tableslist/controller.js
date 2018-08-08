

(function() {
	define(['zepto','slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

	scope = $scope;
	//未加载完成显示正在加载页面
	scope.pageShow = "False";

	// 定义页面标题
	scope.pageTitle = config.pageTitle;
	area = {}
	area.area_id = params.area_id;//获取传过来的区域id

	// 餐桌数据源
	scope.tables_list = [];
	
	//页面初始化
	var init = function() {
		$httpService.post(config.findURL,area).success(function(data) {
			if (data.code != '0000') {
				loggingService.info(data.data);
			} else {
				scope.tables_list = data.data;
				scope.pageShow = "True";
				scope.$apply();
			}
		}).error(function(data) {
			loggingService.info('获取测试信息出错');
		});
	}
	
	
	init();
	//跳转链接
	scope.toHref = function(path,tables_id) {
		console.info(scope.tables_list.length)
		var m2 = {
			"url" : "aps/content/" + path + "/config.json?area_id="+params.area_id+"&tables_num="+scope.tables_list.length+"&tables_id=" + tables_id,
			"size" : "modal-lg",
			"contentName" : "content"
		}
		eventBusService.publish(controllerName, 'appPart.load.content', m2);
	}
	
	
	//删除按钮
	scope.tablesDelete = function(tables_id){
		scope.del={}
		scope.del.tables_id = tables_id;
		scope.del.area_id = params.area_id;
		var m2 = {
			"url" : "aps/content/DailyManagement/tables/catelist/tableslist/config.json",
			"title" : "提示",
			"contentName" : "modal",
			"text" : "是否删除?"
		}
		eventBusService.publish(controllerName, 'appPart.load.modal', m2);
	}
	
	// 弹窗确认事件
	eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
		 $httpService.post(config.removeURL,scope.del).success(function(data){
			 if(data.code == "0000"){
				 scope.pageShow = "False";
				 init();
			 }
			
			var m2 = {
				"title" : "提示",
				"contentName" : "modal",
				"text" : data.data
			}
			eventBusService.publish(controllerName, 'appPart.load.modal', m2);
		    }).error(function(data){
		    	loggingService.info('获取测试信息出错');
		    });
		
			
	});
	
	// 弹窗取消事件
	eventBusService.subscribe(controllerName, controllerName + '.close', function(event, btn) {
		eventBusService.publish(controllerName, 'appPart.load.modal.close', {
			contentName : "modal"
		});
		//闭合滑块
    	$('.slideleft_cell_bd').css('-webkit-transform', 'translateX(0px)');
	});
	
	
	
	
	

		}
	];
});


}).call(this);

$(function() {})