
(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService,$rootScope) {

		scope = $scope;
	
		scope.showPage = "False"
		dw_select = ['桌','间'];
		
		//初始化form表单
		scope.form={}
		
		var init = function() {
			//获取传过来的数据
			area_id = params.area_id
			//判断
			if(area_id != undefined && area_id != 'undefined' && area_id != ''){
				//是修改
				// 定义页面标题
				scope.pageTitle = "修改区域"
				//初始化排序序号列表
				catesetting_pxxh = Array(Number(params.area_num)).fill().map((v,i) => i+1);//填充排序列表 1-n
				
				//发送post请求
				$httpService.post(config.findURL,params).success(function(data) {
					if (data.code != '0000') {
						loggingService.info(data.data);
					} else {
						scope.form = data.data;
						scope.pageShow = "True";
						scope.$apply();
						//这两行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
						$("#pxxh_select").val(scope.form.TABLES_AREA_PXXH);
						$("#dw_select").val(scope.form.TABLES_AREA_NUM);
						comboboxInit()
					}
				}).error(function(data) {
					loggingService.info('获取测试信息出错');
				});
			}else{
				// 定义页面标题
				scope.pageTitle = "添加区域"
				scope.pageShow = "True";
				//初始化排序序号列表
				catesetting_pxxh = Array(Number(params.area_num)+1).fill().map((v,i) => i+1);//填充排序列表 1-n
				//这两行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
				$("#pxxh_select").val(Number(params.area_num)+1);
				$("#pxxh_select").val(dw_select[0]);
				scope.form.TABLES_AREA_NUM = dw_select[0];
				scope.form.TABLES_AREA_PXXH = Number(params.area_num)+1;
				scope.form.TABLES_AREA_STATUS = "1";
				scope.form.TABLES_AREA_DESC="";
				comboboxInit()
			}
			
		}
		
		init()
	
		scope.toHref = function(path,cid) {
			var m2 = {
				"url" : "aps/content/" + path + "/config.json?cid=" + cid,
				"size" : "modal-lg",
				"contentName" : "content"
			}
			eventBusService.publish(controllerName, 'appPart.load.content', m2);
		}
		
		var $form = $("#form");
		$form.form();
		//保存
		scope.doSave = function(){
			$form.validate(function(error) {
				if (!error) {
					//获取排序序号
					scope.form.TABLES_AREA_PXXH = $("#pxxh_select").val();
					
					//弹出保存询问
					var m2 = {
						"url" : "aps/content/DailyManagement/tables/catelist/setting/config.json",
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
			if(area_id != undefined && area_id != 'undefined' && area_id != ''){
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
						"toUrl" : "aps/content/DailyManagement/tables/catelist/config.json"
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
		
	}];
			
	});
		
	
		
	
	

}).call(this);


function comboboxInit(){
	
	/*$("#pxxh_select").picker({
		title : "排序序号",
		toolbarCloseText : '确定',
		cols : [
			{
				textAlign : 'center',
				values : catesetting_pxxh,
				displayValues : catesetting_pxxh
			}
		]
	});*/
	
	$("#dw_select").picker({
		title : "单位",
		toolbarCloseText : '确定',
		cols : [
			{
				textAlign : 'center',
				values : dw_select,
				displayValues : dw_select
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
