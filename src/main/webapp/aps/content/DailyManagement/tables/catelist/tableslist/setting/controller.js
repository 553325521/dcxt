
(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

		scope = $scope;
		scope.pageShow = "False";
		
		//初始化form表单
		scope.form = {}
		scope.form.area_id = params.area_id;
		
		console.info(params)
		
		var init = function() {
			//获取传过来的数据
			tables_id = params.tables_id
			//判断
			if(tables_id != null && tables_id != ''){
				//是修改
				// 定义页面标题
				scope.pageTitle = config.pageTitle;
				//初始化排序序号列表
				tables_pxxh = Array(Number(params.tables_num)).fill().map((v,i) => i+1);//填充排序列表 1-n
				
				//发送post请求
				$httpService.post(config.findURL,params).success(function(data) {
					if (data.code != '0000') {
						loggingService.info(data.data);
					} else {
						scope.form = data.data;
						scope.form.area_id = params.area_id;
						scope.font_length = scope.form.TABLES_DESC.length
						scope.pageShow = "True";
						scope.$apply();
						//这一行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
						$("#pxxh_select").val(scope.form.TABLES_PXXH);
						comboboxInit()
					}
				}).error(function(data) {
					loggingService.info('获取测试信息出错');
				});
			}else{
				// 定义页面标题
				scope.pageTitle = "添加餐桌";
				scope.pageShow = "True";
				//初始化排序序号列表
				tables_pxxh = Array(Number(params.tables_num)+1).fill().map((v,i) => i+1);//填充排序列表 1-n
				//这一行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
				$("#pxxh_select").val(Number(params.tables_num)+1);
				scope.form.TABLES_PXXH = Number(params.tables_num)+1;
				scope.form.TABLES_STATUS = "1";
				scope.form.TABLES_DESC="";
				comboboxInit()
			}
			
		}
		
		init()
		
		
		/*	//餐桌区域列表
		tables_qy = [ '大厅', '雅间', '小包间', '大包间']
		
		
		//区域初始化
		scope.form.TABLES_QY = tables_qy[1]
		////这一行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
		$("#qy_select").val(scope.form.TABLES_QY);*/
		

		
		
		
		
		//跳转
		scope.toHref = function(path) {
			var m2 = {
				"url" : "aps/content/" + path + "/config.json?area_id=" + params.area_id,
				"size" : "modal-lg",
				"contentName" : "content"
			}
			eventBusService.publish(controllerName, 'appPart.load.content', m2);
		}
		
		
		
		var $form = $("#form");
		$form.form();
		//保存
		
		scope.doSave = function(){
			console.info(scope.form)
			$form.validate(function(error) {
				if (!error) {
					//获取排序序号
					scope.form.TABLES_PXXH = $("#pxxh_select").val();
//					scope.form.TABLES_QY = $("#qy_select").val();
					//弹出保存询问
					var m2 = {
						"url" : "aps/content/DailyManagement/tables/catelist/tableslist/setting/config.json",
						"title" : "提示",
						"contentName" : "modal",
						"text" : "是否保存?"
					}
					eventBusService.publish(controllerName, 'appPart.load.modal', m2);
				}
			})
		}
		
		// 弹窗确认事件
		eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
			//判断是修改还是添加
			if(tables_id != null && tables_id != ''){
				url = config.updateURL;
			}else{
				url = config.saveURL;
			}
			 $httpService.post(url,$scope.form).success(function(data){
				 
				 if(data.code != "0000"){
					 var m2 = {
						"title" : "提示",
						"contentName" : "modal",
						"text" : data.data
					}
				 }else{
					 var m2 = {
						"title" : "提示",
						"contentName" : "modal",
						"text" : data.data,
						"toUrl" : "aps/content/DailyManagement/tables/catelist/tableslist/config.json?area_id=" + params.area_id
					 }
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
		});
		

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
	
/*	$("#qy_select").picker({
		title : "选择区域",
		toolbarCloseText : '确定',
		cols : [
			{
				textAlign : 'center',
				values : tables_qy,
				displayValues : tables_qy
			}
		]
	});*/
	
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
