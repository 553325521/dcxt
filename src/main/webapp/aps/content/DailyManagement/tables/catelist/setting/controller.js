


(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService,$rootScope) {

	scope = $scope;

	
	// 定义页面标题
	scope.pageTitle = config.pageTitle
		
	//初始化form表单
	scope.form={}
	
	
	console.info(params)
	//获取传过来的数据
	tables_data_list = params.tables_data_list
	
	//判断
	if(tables_data_list != null){
		//获取url中的参数
		tables_data_list = angular.fromJson(tables_data_list);
		catesetting_pxxh = Array(tables_data_list.tables_area_count + 1).fill().map((v,i) => i+1);//填充排序列表 1-n
		console.info(catesetting_pxxh)
		if(tables_data_list.status == "0000"){//是修改
			
			tables_form = angular.fromJson(tables_data_list.tables_form)
			
			//开始填充数据
			//是否启用
			scope.form.CATESETTING_IS_USE = tables_form.status == 1 ? "是" : "否";  
			//分类名称
			scope.form.CATESETTING_CATE_NAME = tables_form.name;
			//桌位个数
			scope.form.CATESETTING_TABLES_NUM = tables_form.count;
			//备注
			scope.form.CATESETTING_DESC = tables_form.desc;
			//id
			scope.form.CATESETTING_ID = tables_form.id;
			//排序序号
			scope.form.CATESETTING_PXXH = tables_data_list.tables_index;
			
			//初始化字数个数
			scope.font_count = scope.form.CATESETTING_DESC.length
			
		}else if(tables_data_list.status == "1111"){
			//初始化排序序号
			scope.form.CATESETTING_PXXH = tables_data_list.tables_area_count + 1;
			//是否启用	-初始化
			scope.form.CATESETTING_IS_USE = "是";
			
			scope.font_count = 0
		}
	}else{
		//不是修改也不是添加
		return
	}
	
	//这一行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
	$("#pxxh_select").val(scope.form.CATESETTING_PXXH);
	
	
	
	
	
	scope.$watch('scope.form.CATESETTING_DESC', function(newValue, oldValue) {
			console.info(newValue,oldValue)
//					if (newValue === oldValue) {
//						return;
//					}
//					if (newValue == 'all' || newValue == undefined) {
//						scope.form.KIND_OF_DISHES = false;
//						if (newValue == 'all') {
//							scope.noCheckArr = []
//						} else {
//							scope.noCheckArr = [ 1, 2, 3, 4, 5 ]
//						}
//					} else {
//						scope.form.KIND_OF_DISHES = true;
//
//						scope.noCheckArr = [ 1, 2, 3, 4, 5 ]
//					}
				}, true);


	// 餐桌区域数据源
	
	

	scope.toHref = function(path,cid) {
		var m2 = {
			"url" : "aps/content/" + path + "/config.json?cid=" + cid,
			"size" : "modal-lg",
			"contentName" : "content"
		}
		eventBusService.publish(controllerName, 'appPart.load.content', m2);
	}
	
	
	
	
	//保存

	scope.doSave = function(){
		
		
		console.info("测试成功")
		
		
		scope.form.CATESETTING_PXXH = $("#pxxh_select").val();
		
		console.info(scope.form)
	}
	
	
	
	comboboxInit()
	
			}
			
			];
		
		
	});
	

	
	
	

}).call(this);


function comboboxInit(){
	
	$("#pxxh_select").picker({
		title : "排序序号",
		toolbarCloseText : '确定',
		cols : [
			{
				textAlign : 'center',
				values : catesetting_pxxh,
				displayValues : catesetting_pxxh
			}
		]
	});
	
	//备注超出字数限制操作
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
