


(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

	scope = $scope;

	
	// 定义页面标题
	scope.pageTitle = config.pageTitle
		
	//初始化form表单
	scope.form={}
		
//	scope.form.TABLES_NUM = 2
	
	console.info("ggggg")
	console.info(scope.form.TABLES_NUM)
	
	
	//排序序号数据源
	catesetting_pxxh = [1,2,3,4]
	
	
	//初始化排序序号
	
	scope.form.CATESETTING_PXXH = catesetting_pxxh[catesetting_pxxh.length-1]
	//这一行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
	$("#pxxh_select").val(scope.form.CATESETTING_PXXH);
	

	
	//是否启用 初始化
	scope.form.CATESETTING_IS_USE = "是"
	
	
	
	
	
	/*	$scope.$watch('scope.form.TABLES_NUM', function(newValue, oldValue) {
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
				}, true);*/


	// 餐桌区域数据源
	
	
	
	
	
	

	scope.doSave = function(){
		console.info("测试成功")
		
		
		scope.form.CATESETTING_PXXH = $("#pxxh_select").val();
		
		console.info(scope.form)
	}
	
	
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

	comboboxInit()
	
			}
			
			];
		
		
	});
	

	
	
	

}).call(this);




$(function() {
	
})
