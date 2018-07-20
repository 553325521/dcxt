


(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

		scope = $scope;

		// 定义页面标题
		scope.pageTitle = config.pageTitle;
		
		//初始化form表单
		scope.form = {}
		
		console.info(params)
		
		//获取穿过来的数据
		tables_data_list = params.tables_data_list
		
		//判断
		if(tables_data_list != null){
			//获取url中的参数
			tables_data_list = angular.fromJson(tables_data_list);
			tables_pxxh = Array(tables_data_list.tables_count + 1).fill().map((v,i) => i+1);//填充排序列表 1-n
			console.info(tables_pxxh)
			if(tables_data_list.status == "0000"){//是修改
				
				tables_form = angular.fromJson(tables_data_list.tables_form)
				
				//开始填充数据
				//是否启用
				scope.form.TABLES_IS_USE = tables_form.status == 1 ? "是" : "否";  
				//餐桌名称
				scope.form.TABLES_NAME = tables_form.name;
				//桌位人数
				scope.form.TABLES_NUM = tables_form.count;
				//备注
				scope.form.TABLES_DESC = tables_form.desc;
				//桌位id
				scope.form.TABLES_ID = tables_form.id;
				//排序序号
				scope.form.TABLES_PXXH = tables_data_list.tables_index;
				
				//初始化字数个数
				scope.font_count = scope.form.TABLES_DESC.length
				
			}else if(tables_data_list.status == "1111"){
				//初始化排序序号
				scope.form.TABLES_PXXH = tables_data_list.tables_count + 1;
				//是否启用	-初始化
				scope.form.TABLES_IS_USE = "是";
				
				scope.font_count = 0
			}
		}else{
			//不是修改也不是添加
			return
		}
		
		//这一行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
		$("#pxxh_select").val(scope.form.TABLES_PXXH);
		
		
		
		//餐桌区域列表
		tables_qy = [ '大厅', '雅间', '小包间', '大包间']
		
		
		
		//区域初始化
		scope.form.TABLES_QY = tables_qy[1]
		////这一行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
		$("#qy_select").val(scope.form.TABLES_QY);
		

		
		//保存
		scope.doSave = function(){
			scope.form.TABLES_PXXH = $("#pxxh_select").val();
			scope.form.TABLES_QY = $("#qy_select").val();
			console.info(scope.form)
		}
		
		
		//跳转
		scope.toHref = function(path) {
			var m2 = {
				"url" : "aps/content/" + path + "/config.json?cid=" + params.cid,
				"size" : "modal-lg",
				"contentName" : "content"
			}
			eventBusService.publish(controllerName, 'appPart.load.content', m2);
		}
		
		
		
		
		comboboxInit();
	
	
		

		}
	];
});



}).call(this);



function comboboxInit() {
	
	$("#pxxh_select").picker({
		title : "排序序号",
		toolbarCloseText : '确定',
		cols : [
			{
				textAlign : 'center',
				values : tables_pxxh,
				displayValues : tables_pxxh
			}
		]
	});
	
	$("#qy_select").picker({
		title : "选择区域",
		toolbarCloseText : '确定',
		cols : [
			{
				textAlign : 'center',
				values : tables_qy,
				displayValues : tables_qy
			}
		]
	});
	
	var max = $('#count_max').text();
	  $('#textarea').on('input', function(){
	     var text = $(this).val();
	     var len = text.length;   
	     $('#count').text(len);    
	     if(len > max){
	       $(this).closest('.weui_cell').addClass('weui_cell_warn');
	     }
	     else{
	       $(this).closest('.weui_cell').removeClass('weui_cell_warn');
	     }     
	  });
	
}


$(function() {
	
})
