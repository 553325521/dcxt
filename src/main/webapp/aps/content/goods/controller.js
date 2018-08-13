
(function() {
	define(['slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '商品分类';	
				
				scope.form = {};
				
				scope.form.GTYPE_PID = 0 ;
				
				console.info(params);
				
				scope.lastPage = [];
				
				
				/*判断是否进入选择添加商品或者类别界面(返回按钮的时候使用)*/
				scope.methodType = 0;
				
				scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fromUrl=" + config.currentUrl + "&GTYPE_PID=" + scope.form.GTYPE_PID+"&Last_Page="+scope.lastPage,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				/*初始化商品分类数据源*/
				scope.GoodsType_List = [];
				
				/*定义加载商品分类列表方法*/
				function loadGoodsTypeListByPID(method){
					console.info(scope.form.GTYPE_PID);
					//发送post请求
					if(method == "next"){
						scope.lastPage.push(scope.form.GTYPE_PID);
					}
					if(scope.methodType == 1){
						$(".showAddGoodsTypeBtn").css("display","block");
						$(".showGoodsType").css("display","block");
						$(".showSelectDiv").css("display","none");
					}
					$httpService.post(config.loadGoodsTypeListByPIDURL,scope.form).success(function(data) {
						if(data.code == '0000' && data.data !=null){
							scope.GoodsType_List = data.data;
							$scope.$apply();
						}else{
							scope.GoodsType_List = [];
							$scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				/*调用加载商品分类列表方法*/
				loadGoodsTypeListByPID("return");
				/*定义返回按钮被点击时的方法*/
				scope.returnClick = function(){
					if(scope.lastPage.length == 0 && scope.form.GTYPE_PID == 0){
						scope.toHref("welcome");
					}else if(scope.lastPage.length == 0 && scope.form.GTYPE_PID != 0){
						scope.form.GTYPE_PID = 0;
						loadGoodsTypeListByPID("return");
					}else{
						scope.lastPage.pop();
						if(scope.lastPage.length == 0){
							scope.form.GTYPE_PID = 0;
							loadGoodsTypeListByPID("return");
						}else{
							scope.form.GTYPE_PID = scope.lastPage[scope.lastPage.length-1];
							loadGoodsTypeListByPID("return");
						}
						
					}
				}
				/*定义某一个商品分类被点击调用的方法*/
				scope.goodsTypeClick = function(GTYPE_PK,method){
					scope.form.GTYPE_PID = GTYPE_PK;
					$httpService.post(config.selectLastRecordCountByPIDURL,scope.form).success(function(data) {
						if(data.code == '0000' && data.data !=null){
							/*下面没有商品也没有商品分类*/
							if(data.data == '00'){
								scope.methodType = 1;
								scope.lastPage.push(scope.form.GTYPE_PID);
								scope.lastPage.pop();
								$(".showAddGoodsTypeBtn").css("display","none");
								$(".showGoodsType").css("display","none");
								$(".showSelectDiv").css("display","block");
							/*	loadGoodsTypeListByPID();*/
								/*下面没有商品有商品分类*/
							}else if(data.data == '10'){
								loadGoodsTypeListByPID(method);
								/*下面有商品没有商品分类*/
							}else if(data.data == '01'){
								
								/*下面有商品有商品分类(不可能)*/
							}else{
								console.info("error");
							}
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				if(params!=null&&params.Last_Page!=null&&params.Last_Page!=""&&params.GTYPE_PID!=null){
					scope.lastPage = params.Last_Page.split(",");
					scope.lastPage.push(params.GTYPE_PID);
					scope.goodsTypeClick(scope.lastPage[scope.lastPage.length-1]);
					console.info("测试"+scope.lastPage);
				}else if(params.Last_Page == ""&&params.GTYPE_PID!=null){
					scope.goodsTypeClick(params.GTYPE_PID);
				}else{
					scope.form.GTYPE_PID = 0;
					loadGoodsTypeListByPID("return");
				}
				/*if(params!=null&&params.Last_Page!=null&&params.GTYPE_PID!=null){
					console.info("进来了");
					console.info("返回中传的param"+params.GTYPE_PID+"===="+params.Last_Page);
				}*/
				if(params!=null&&params.GTYPE_PID!=null){
					scope.form.GTYPE_PID = params.GTYPE_PID;
					/*scope.goodsTypeClick(params.GTYPE_PID,"return");*/
				}
				
				
			}
		];
	});
}).call(this);

$(function() {
})