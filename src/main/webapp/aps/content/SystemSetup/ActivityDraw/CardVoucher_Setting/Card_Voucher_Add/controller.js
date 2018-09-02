
	(function() {
		define(['picker','select','wangEditor'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					//初始化form表单
					scope.form = {};
					scope.pageShow = "False";
					
					scope.form.fid = params.fid;
					
					// 定义页面标题
					scope.pageTitle = '卡券设置';	
					
					/*初始化是否启用*/
					scope.form.IS_USE = "1";
					
					/*初始化有效期限*/
					scope.form.EXPIRY_DATE = "0";
					
					scope.yjShow = "False";
					scope.qjShow = "False";
					scope.tsShow = "False";
					
					scope.PICTURE_URL = [];
					
					/*初始化卡券类型*/
					scope.form.CARD_VOUCHER_TYPE = "0";
					
					scope.DKTYPE_ISSHOW = "False";
					scope.DJQ_ISSHOW = "True";
					scope.ZKQ_ISSHOW = "False";
					scope.DKQ_ISSHOW = "False";
					scope.DKQ_MONEY_ISSHOW = "False";
					
					/*初始化抵扣类型的值*/
					scope.form.DUDUCTION_TYPE = "0"; 
					
					/*初始化共享类型*/
					scope.form.SHARE_TYPE = "0";
					
					/*	初始化优惠共享*/
					scope.form.FAVOURABLE_SHARE = "0";
					
					/*初始化有限时段选择值*/
					scope.form.EFFECTIVE_TIME = "0";
					
					scope.time_week ="False";
					
					scope.time_time ="False";
					
					/*监听有效时段选择值*/
					$scope.$watch('form.EFFECTIVE_TIME', function(newValue, oldValue) {
						if (newValue === '0') {
							scope.time_week ="False";
							scope.time_time ="False";
						}else if(newValue === '1'){
							scope.time_week ="True";
							scope.time_time ="False";
						}else{
							scope.time_week ="False";
							scope.time_time ="True";
						}
					}, true);
					
					/*初始化文本编辑器*/
					function initEdit(){
					    //一句话，即可把一个div 变为一个富文本框！o(∩_∩)o 
					   
						var $editor = $('#txtDiv').wangEditor();
					    //显示 html / text
					    var $textarea = $('#textarea'),
					        $btnHtml = $('#btnHtml'),
					        $btnText = $('#btnText'),
					        $btnHide = $('#btnHide');
					    $textarea.hide();
					    $btnHtml.click(function(){
					        $textarea.show();
					        $textarea.val( $editor.html() );
					    });
					    $btnText.click(function(){
					        $textarea.show();
					        $textarea.val( $editor.text() );
					    });
					    $btnHide.click(function(){
					        $textarea.hide();
					    });
					};
					initEdit();
					
					// 初始化限星期数据源
					$scope.TIME_WEEK = [
						{
							value : 1,
							name : '周一 '
						},
						{
							value : 2,
							name : '周二'
						},
						{
							value : 3,
							name : '周三 '
						},
						{
							value : 4,
							name : '周四 '
						},
						{
							value : 5,
							name : '周五 '
						},
						{
							value : 6,
							name : '周六 '
						},
						{
							value : 7,
							name : '周日 '
						}
					];
					/*初始化限时段数据源*/
					$scope.TIME_TIME = [
						{
							value : 1,
							name : '上午 '
						},
						{
							value : 2,
							name : '中午'
						},
						{
							value : 3,
							name : '晚上 '
						}
					];
					
					/*监听卡券类型选择值*/

					$scope.$watch('form.CARD_VOUCHER_TYPE', function(newValue, oldValue) {
						if (newValue === '0') {
							scope.DKTYPE_ISSHOW = "False";
							scope.KQCELL_ISSHOW = "True";
							scope.DJQ_ISSHOW = "True";
							scope.ZKQ_ISSHOW = "False";
							scope.DKQ_ISSHOW = "False";
							scope.DKQ_MONEY_ISSHOW = "False";
						}else if(newValue === '1'){
							scope.DKTYPE_ISSHOW = "False";
							scope.DJQ_ISSHOW = "False";
							scope.KQCELL_ISSHOW = "True";
							scope.ZKQ_ISSHOW = "True";
							scope.DKQ_ISSHOW = "False";
							scope.DKQ_MONEY_ISSHOW = "False";
						}else if(newValue === '2'){
							scope.DKTYPE_ISSHOW = "True";
							scope.DJQ_ISSHOW = "False";
							scope.ZKQ_ISSHOW = "False";
							scope.KQCELL_ISSHOW = "True";
							scope.DKQ_ISSHOW = "True";
							scope.DKQ_MONEY_ISSHOW = "False";
							$scope.$watch('form.DUDUCTION_TYPE', function(newValue, oldValue) {
								if (newValue === '0') {
									scope.DKTYPE_ISSHOW = "True";
									scope.DJQ_ISSHOW = "False";
									scope.ZKQ_ISSHOW = "False";
									scope.KQCELL_ISSHOW = "True";
									scope.DKQ_ISSHOW = "True";
									scope.DKQ_MONEY_ISSHOW = "False";
								}else{
									scope.DKTYPE_ISSHOW = "True";
									scope.DJQ_ISSHOW = "False";
									scope.ZKQ_ISSHOW = "False";
									scope.DKQ_ISSHOW = "False";
									scope.KQCELL_ISSHOW = "True";
									scope.DKQ_MONEY_ISSHOW = "True";
								}
							}, true);
						}else{
							scope.DKTYPE_ISSHOW = "False";
							scope.KQCELL_ISSHOW = "False";
							scope.DJQ_ISSHOW = "False";
							scope.ZKQ_ISSHOW = "False";
							scope.DKQ_ISSHOW = "False";
							scope.DKQ_MONEY_ISSHOW = "False";
						}
					}, true);
					/*监听有效期限选择值*/
					$scope.$watch('form.EXPIRY_DATE', function(newValue, oldValue) {
						if (newValue === '0') {
							scope.yjShow = "False";
						}else if(newValue === '1'){
							scope.yjShow = "True";
							scope.qjShow = "True";
							scope.tsShow = "False";
						}else{
							scope.yjShow = "True";
							scope.qjShow = "False";
							scope.tsShow = "True";
						}
					}, true);
					
					var init = function(){
						$("#start_time").datetimePicker({title:"选择日期",m:1});
						$("#end_time").datetimePicker({title:"选择日期",m:1});
						scope.pageShow = "True";
					}
					init();
					
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fid=" + scope.form.fid,
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
							
							}
						})
					}
					
					// 弹窗确认事件
					eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
						//判断是修改还是添加
						if(scope.form.GOODS_ID == 'undefined' || scope.form.GOODS_ID == ''){
							url = config.saveURL;
						}else{
							url = config.updateURL;
						}
						 $httpService.post(url,scope.form).success(function(data){
							 
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
									"toUrl" : "aps/content/DailyManagement/goods/goods_show/config.json?GTYPE_PK="+params.gtype_id,
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
					/*初始化商铺选择数据源*/
					scope.shopArray = [];
					function comboboxInit() {
							$httpService.post(config.loadShopDataURL,scope.form).success(function(data){
								console.info(data.data);
								for(var i = 0;i < data.data.length;i++){
									scope.shopArray.push({title:data.data[i].SHOP_NAME,value:data.data[i].FK_SHOP});
								}
								scope.$apply();
								console.info("shopArray:"+scope.shopArray);
								$("#d3").select({
							        title: "选择门店",
							        multi: true,
							        split:',',
							        closeText:'完成',
							        items:scope.shopArray,
							        onChange: function(d) {
							          $.alert("你选择了"+d.values+d.titles);
							        }
							      });
						    }).error(function(data){
						    	loggingService.info('获取测试信息出错');
						    });
					}
					comboboxInit();
				}
			];
		});
	}).call(this);
	//添加或更换图片
	function previewImage(file) {
		index = angular.element(file).scope().$index  //onchange里边获取不到$index，只能这么获取了
	    for(var i=0;i<file.files.length;i++){
		    if (file.files && file.files[i]) {
		        var reader = new FileReader();
		        reader.onload = function (evt) {
		        	console.info(evt.loaded)
		        	if(evt.loaded > 2097152){
		        		$.toptips('单张图片上传大小最大为2M')
		        		return;
		        	}
		        	if(index == undefined || index == ''){
		        		//添加图片
		        		scope.PICTURE_URL.push(evt.target.result);
		        	}else{
		        		//更换图片
		        		scope.PICTURE_URL[index] = evt.target.result;
		        	}
		        	console.info(scope.PICTURE_URL)
		        	scope.$apply();
		        };
		        reader.readAsDataURL(file.files[i]);
		    }
	    
	    }
	}	
	$(function() {
	})