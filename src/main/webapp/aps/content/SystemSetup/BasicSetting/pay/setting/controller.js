
(function() {
	define(['jqueryweui'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {

		scope = $scope;
		
		scope.pageShow = "False";
		
		/*初始化页面标题*/
		scope.pageTitle = config.pageTitle;
		
		//初始化form表单
		scope.form = {}
		
		/*初始化微信支付开启状态*/
		
		scope.form.WX_STATUS = 0;
		
		scope.wxShow = "False";
		/*初始化支付宝支付开启状态*/
		
		scope.form.ALIPAY_STATUS = 0;
		
		scope.alipayShow = "False";	
		/*初始化星Pos支付开启状态*/
		scope.form.POSPAY_STATUS = 0;
		
		/*初始化是添加还是编辑标识*/
		scope.method = "add";
		
		
		var init = function() {
			
			scope.pageShow = "True";
			
			$httpService.post(config.loadPaySettingInfo, $scope.form).success(function(data) {
				if(data.data != 0){
					scope.method = "update";
					scope.form.PAYSETTING_PK = data.data[0].PAYSETTING_PK;
					if(data.data[0].WX_STATUS == '1'){
						scope.wxShow = "True";
						scope.form.WX_STATUS = data.data[0].WX_STATUS;
						scope.form.ORIGINAL_ID = data.data[0].ORIGINAL_ID;
						scope.form.WX_APPID = data.data[0].WX_APPID;
						scope.form.WX_SHOP_ID = data.data[0].WX_SHOP_ID;
						scope.form.SUB_SHOP_APPID = data.data[0].SUB_SHOP_APPID;
						scope.form.SUB_SHOPID = data.data[0].SUB_SHOPID;
					}
					if(data.data[0].ALIPAY_STATUS == '1'){
						scope.alipayShow = "True";	
						scope.form.ALIPAY_STATUS = data.data[0].ALIPAY_STATUS;
						scope.form.ALIPAY_EMAIL = data.data[0].ALIPAY_EMAIL;
						scope.form.ALIPAY_PID = data.data[0].ALIPAY_PID;
						scope.form.ALIPAY_KEY = data.data[0].ALIPAY_KEY;
						scope.form.ALIPAY_APP_ID = data.data[0].ALIPAY_APP_ID;
						scope.form.ALIPAY_RSA = data.data[0].ALIPAY_RSA;
					}
					if(data.data[0].POSPAY_STATUS == '1'){
						scope.pospayShow = "True";
						scope.form.POSPAY_STATUS = data.data[0].POSPAY_STATUS;
						scope.form.SHOP_IDENTIFY_NUMBER = data.data[0].SHOP_IDENTIFY_NUMBER;
						scope.form.SHOP_STORE_NUMBER = data.data[0].SHOP_STORE_NUMBER;
						scope.form.RECEIPT_SHOP_NUMBER = data.data[0].RECEIPT_SHOP_NUMBER;
						scope.form.PLATFORM_TERMINAL_NUMBER = data.data[0].PLATFORM_TERMINAL_NUMBER;
						scope.form.POS_WX_APPID = data.data[0].POS_WX_APPID;
					}
					$scope.$apply();
				}
			}).error(function(data) {
				loggingService.info('获取测试信息出错');
			});
			
		}
		init();
		
		/*监听微信支付是否启用状态值*/

		$scope.$watch('form.WX_STATUS', function(newValue, oldValue) {
			if (newValue === '1') {
			 scope.wxShow = "True";
			}else{
			scope.wxShow = "False";	
			}
		}, true);
		
		/*监听支付宝支付是否启用状态值*/

		$scope.$watch('form.ALIPAY_STATUS', function(newValue, oldValue) {
			if (newValue === '1') {
			 scope.alipayShow = "True";
			}else{
			scope.alipayShow = "False";	
			}
		}, true);
		
		/*监听星Pos支付是否启用状态值*/

		$scope.$watch('form.POSPAY_STATUS', function(newValue, oldValue) {
			if (newValue === '1') {
			 scope.pospayShow = "True";
			}else{
			scope.pospayShow = "False";	
			}
		}, true);
		
		/*	//餐桌区域列表
		tables_qy = [ '大厅', '雅间', '小包间', '大包间']
		//区域初始化
		scope.form.TABLES_QY = tables_qy[1]
		////这一行是为了解决第一次点击下拉菜单后滑块默认是第一个的问题
		$("#qy_select").val(scope.form.TABLES_QY);*/
		//跳转
		scope.toHref = function(path) {
			var m2 = {
				"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
				"size" : "modal-lg",
				"contentName" : "content"
			}
			eventBusService.publish(controllerName, 'appPart.load.content', m2);
		}
		var $form = $("#form");
		$form.form();
		//保存
		scope.doSave = function(){
		/*	$form.validate(function(error) {
				if (!error) {*/
					//弹出保存询问
					var m2 = {
						"url" : "aps/content/SystemSetup/BasicSetting/pay/setting/config.json",
						"title" : "提示",
						"contentName" : "modal",
						"text" : "是否保存?"
					}
					eventBusService.publish(controllerName, 'appPart.load.modal', m2);
			/*	}
			})*/
		}
		
		// 弹窗确认事件
		eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
			//判断是修改还是添加
			if(scope.method == 'update'){
				url = config.updateURL;
			}else{
				url = config.insertURL;
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
						"toUrl" : "aps/content/SystemSetup/BasicSetting/pay/setting/config.json?fid="+params.fid
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
	
}


$(function() {
	
})
