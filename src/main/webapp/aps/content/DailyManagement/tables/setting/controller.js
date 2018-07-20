


(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

	scope = $scope;
	

	// 定义页面标题
		scope.pageTitle = '餐桌设置'
		//排序序号列表
		tables_pxxh = [ '1', '2', '3' ];
		//餐桌区域列表
		tables_qy = [ '大厅', '雅间', '小包间', '大包间']
		
		
		//初始化form表单
		scope.form = {}
		
		//排序序号初始化
		scope.form.TABLES_PXXH = tables_pxxh[1]
		////这一行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
		$("#pxxh_select").val(scope.form.TABLES_PXXH);
		
		
		
		//区域初始化
		scope.form.TABLES_QY = tables_qy[1]
		////这一行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
		$("#qy_select").val(scope.form.TABLES_QY);
		
		//是否使用初始化
		scope.form.TABLES_IS_USE = "是"
		
			
		
		scope.doSave = function(){
			
			
			scope.form.TABLES_PXXH = $("#pxxh_select").val();
			scope.form.TABLES_QY = $("#qy_select").val();
			
			console.info(scope.form)
		}
		
		
		
		
		
		
		
		
		
		var checkFontNum = function(){
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
			};

		
		
		
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
	
	
		comboboxInit();

		}
	];
});



}).call(this);






$(function() {
	
})
