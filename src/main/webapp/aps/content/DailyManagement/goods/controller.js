
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
				
				scope.lastPage = [0];
				
				scope.form.GTYPE_PK = "";
				
				/*判断是否进入选择添加商品或者类别界面(返回按钮的时候使用)*/
				scope.methodType = 0;
				
				scope.toHref = function(path,GTYPE_PK) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid+"&fromUrl=" + config.currentUrl + "&GTYPE_PID=" + scope.form.GTYPE_PID+"&Last_Page="+scope.lastPage+"&GTYPE_PK="+GTYPE_PK+ "&gtype_id=" + scope.form.GTYPE_PID+"&goods_count=0",
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				/*删除按钮调用方法*/
				scope.deleteGoodsType = function(GTYPE_PK){
					console.info(GTYPE_PK);
					scope.form.GTYPE_PK  = GTYPE_PK;
					 var m2 = {
				        		"url":"aps/content/DailyManagement/goods/config.json",
				        		"title":"提示",
				        		"contentName":"modal",
				        		"text":"是否删除"
				        	 }
				    eventBusService.publish(controllerName,"appPart.load.modal",m2);
				}
				
				// 弹窗默认事件
				eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
					$httpService.post(config.deleteGoodsTypeUrl, $scope.form).success(function(data) {
							var m2 = {
								"title" : "提示",
								"contentName" : "modal",
								"text" : data.data,
								"toUrl" : "aps/content/DailyManagement/goods/config.json?fid=" + scope.form.fid
							}
						eventBusService.publish(controllerName, 'appPart.load.modal', m2);
					}).error(function(data) {
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
				
				/*初始化商品分类数据源*/
				scope.GoodsType_List = [];
				
				/*定义加载商品分类列表方法*/
				function loadGoodsTypeListByPID(){
					//发送post请求
					/*if(scope.methodType == 1){
						$(".showAddGoodsTypeBtn").css("display","block");
						$(".showGoodsType").css("display","block");
						$(".showSelectDiv").css("display","none");
					}*/
					$httpService.post(config.loadGoodsTypeListByPIDURL,scope.form).success(function(data) {
						if(data.code == '0000' && data.data !=null){
							scope.GoodsType_List = data.data;
							$scope.$apply();
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				/*调用加载商品分类列表方法*/
				loadGoodsTypeListByPID();
				/*定义返回按钮被点击时的方法*/
				scope.returnClick = function(){
					/*如果lastPage数组的长度为0且此时的PID为0，则当前为一级分类*/
					if(scope.lastPage.length == 1 && scope.form.GTYPE_PID == 0){
						scope.toHref("welcome");
					}
					if(scope.lastPage.length > 1){
						scope.lastPage.pop();
						scope.goodsTypeClick(scope.lastPage[scope.lastPage.length-1],"last");
					}
				}
				/*定义某一个商品分类被点击调用的方法*/
				scope.goodsTypeClick = function(GTYPE_PK,method){
					scope.form.GTYPE_PID = GTYPE_PK;
					/*若是往下点击往数组里边放*/
					if(method == "next"){
						scope.lastPage.push(GTYPE_PK);
					}
					$httpService.post(config.selectLastRecordCountByPIDURL,scope.form).success(function(data) {
						if(data.code == '0000' && data.data !=null){
							/*下面没有商品也没有商品分类*/
							if(data.data == '00'){
								/*隐藏商品分类数据，显示分类按钮*/
								$(".showAddGoodsTypeBtn").css("display","none");
								$(".showGoodsType").css("display","none");
								$(".showSelectDiv").css("display","block");
								/*下面没有商品有商品分类*/
							}else if(data.data == '10'){
								/*显示商品分类数据，隐藏分类按钮*/
								$(".showAddGoodsTypeBtn").css("display","block");
								$(".showGoodsType").css("display","block");
								$(".showSelectDiv").css("display","none");
								loadGoodsTypeListByPID();
								/*下面有商品没有商品分类*/
							}else if(data.data == '01'){
								
								scope.toHref("DailyManagement/goods/goods_show",GTYPE_PK);
								$scope.$apply();
								/*下面有商品有商品分类(不可能)*/
							}else{
								console.info("error");
							}
						}
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
			/*	若接收的数组存在*/
				if(params.Last_Page != undefined){
					console.info("主页接收的lastPage:"+params.Last_Page);
					scope.lastPage = params.Last_Page.split(",");
					console.info("主页接收的lastPage1:"+scope.lastPage);
					scope.goodsTypeClick(scope.lastPage[scope.lastPage.length-1],"last");
				}
			}
		];
	});
}).call(this);

$(function() {
})