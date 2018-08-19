
	(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					//初始化form表单
					scope.form = {};
					scope.pageShow = "False";
					goods_type = ['荤菜', '素菜', '豆类' ];
					goods_dw = ['份','盘','个'];
					goods_specification = ['大份','小份'];
					//初始化显示范围
//					scope.SHOW_RANGE = {'堂点': true, '外卖':false, '预订':false};
					scope.SHOW_RANGE = {}
					//新添规格的时候的初始值
					new_specification = {name:"大份",price:"+0.00"}
					//初始默认值
					scope.GOODS_SPECIFICATION = [];
					//做法初始化
					scope.GOODS_RECIPE = []
					//新添做法和口味的初始值
					new_recipe = {name:"",price:"+0.00"}
					//口味初始化
					scope.GOODS_TASTE = []
					//图片url
					scope.PICTURE_URL = []
					//获取传过来的类别id
					scope.form.GTYPE_ID = params.gtype_id;
					scope.form.GOODS_ID = params.goods_id;
					goods_count = Number(params.goods_count);
					scope.form.IS_USE = "1"
					scope.form.GOODS_TYPE = "2"
					var init = function(){
						//判断
						if(scope.form.GOODS_ID != undefined && scope.form.GOODS_ID != 'undefined' && scope.form.GOODS_ID != ''){
							//是修改
							// 定义页面标题
							scope.pageTitle = '修改商品';
							//初始化排序序号列表
							pxxh_select = Array(goods_count).fill().map((v,i) => i+1);//填充排序列表 1-n
							
							//发送post请求
							$httpService.post(config.findURL,scope.form).success(function(data) {
								if (data.code != '0000') {
									loggingService.info(data.data);
								} else {
									scope.pageShow = "True";
									scope.form = data.data;
									//字符串转json
									scope.SHOW_RANGE = angular.fromJson(scope.form.SHOW_RANGE);
									scope.GOODS_RECIPE = angular.fromJson(scope.form.GOODS_RECIPE);
									scope.GOODS_SPECIFICATION = angular.fromJson(scope.form.GOODS_SPECIFICATION);
									scope.GOODS_TASTE = angular.fromJson(scope.form.GOODS_TASTE);
									scope.picMap = angular.fromJson(scope.form.PICTURE_URL);
									//去掉map中的value组成list
									angular.forEach(scope.picMap, function(data,index,array){
										scope.PICTURE_URL.push(index)
									});
									
									//分转换成元
									scope.form.GOODS_PRICE = Number(scope.form.GOODS_PRICE)/100;
									if(scope.form.GOODS_TRUE_PRICE != ""){
										scope.form.GOODS_TRUE_PRICE = Number(scope.form.GOODS_TRUE_PRICE)/100;
									}
									//解决下拉选择框默认是第一个的问题
									$("#pxxh_select").val(scope.form.GOODS_PXXH);
									$("#unit_select").val(scope.form.GOODS_DW);
									
									$scope.$apply();
									comboboxInit()
								}
							}).error(function(data) {
								loggingService.info('获取测试信息出错');
							});
							
						}else{
							
							//发送post请求
							$httpService.post(config.initURL,scope.form).success(function(data) {
								if (data.code != '0000') {
									loggingService.info(data.data);
								} else {
									scope.form.GTYPE_NAME = data.data.GTYPE_NAME;//类别名字
									gtype_area = angular.fromJson(data.data.GTYPE_AREA);//该商品类别的范围
									angular.forEach(gtype_area, function(mapdata,index,array){
										scope.SHOW_RANGE[mapdata.name] = mapdata.checked;
									});
									
									scope.pageShow = "True";
									// 定义页面标题
									scope.pageTitle = '添加商品';
									//初始化排序序号列表
									pxxh_select = Array(goods_count+1).fill().map((v,i) => i+1);//填充排序列表 1-n
									
									//这几行是解决下拉框默认选择第一个的问题
//									$("#fs_select").val(goods_type[0]);
									$("#unit_select").val(goods_dw[0]);
									$("#pxxh_select").val(goods_count+1);
									//赋初值
//									scope.form.GTYPE_ID = goods_type[0];
									scope.form.GOODS_DW = goods_dw[0];
									scope.form.GOODS_PXXH = goods_count+1;
									
									$scope.$apply();
									comboboxInit();
								}
							}).error(function(data) {
								loggingService.info('获取测试信息出错');
							});
						}
					}
					
					
					init();
						
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fromUrl=" + config.currentUrl +"&GTYPE_PK="+params.gtype_id,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					var $form = $("#form");
					$form.form();
					//保存
					scope.doSave = function(){
						//由于下拉选择框的值不同步，所以要获取一下
						var specifications = angular.element('.size_select');
						angular.forEach(specifications, function(data,index,array){
							scope.GOODS_SPECIFICATION[index].name = data.value;
						});
//						scope.form.GTYPE_ID = $("#fs_select").val();
						scope.form.GOODS_DW = $("#unit_select").val();
						scope.form.GOODS_PXXH = $("#pxxh_select").val();
						
						//至少选择一个，不然return;
						a=false
						angular.forEach(scope.SHOW_RANGE, function(data,index,array){
							a = data || a
						});
						if(!a){
							$.toptips('至少选择一个显示范围')
							return;
						}
						
						//数组和Map都转换成json，不然会解析失败
						scope.form.SHOW_RANGE = JSON.stringify(scope.SHOW_RANGE);
						scope.form.GOODS_RECIPE = JSON.stringify(scope.GOODS_RECIPE);
						scope.form.GOODS_SPECIFICATION = JSON.stringify(scope.GOODS_SPECIFICATION);
						scope.form.GOODS_TASTE = JSON.stringify(scope.GOODS_TASTE);
						scope.form.PICTURE_URL = JSON.stringify(scope.PICTURE_URL);
						
						console.info(scope.form)
						$form.validate(function(error) {
						if (!error) {
							//获取排序序号
							scope.form.TABLES_AREA_PXXH = $("#pxxh_select").val();
							
							//弹出保存询问
							var m2 = {
								"url" : "aps/content/goods/general_goods_setting/config.json",
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
									"toUrl" : "aps/content/goods/goods_show/config.json"
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
					
					
					
					//添加
					scope.add = function(num){
						switch(num)
						{
						case 1:scope.GOODS_SPECIFICATION.push($.extend(true, {}, new_specification));//拷贝数组，防止改一个全改了
							break;
						case 2:scope.GOODS_RECIPE.push($.extend(true, {}, new_recipe));//拷贝数组，防止改一个全改了
							break;
						case 3:scope.GOODS_TASTE.push($.extend(true, {}, new_recipe));//拷贝数组，防止改一个全改了
							break;
						}
					}
					
					
					//删除
					scope.del = function(num,index){
						switch(num)
						{
						case 1:scope.GOODS_SPECIFICATION.splice(index,1); break;
						case 2:scope.GOODS_RECIPE.splice(index,1);break;
						case 3:scope.GOODS_TASTE.splice(index,1);break;
						}
						
					}
					
				
					scope.initsize_selects = function(index,value){
						$(".size_select:eq(" + index + ")").val(value);
						$(".size_select").picker({
							title : "选择份型",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values : goods_specification,
									displayValues : goods_specification
								}
							]
						});
					}
					
					function comboboxInit() {
						$("#pxxh_select").picker({
							title : "选择排序序号",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values : pxxh_select,
									displayValues : pxxh_select
								}
								]
						});
					/*	$("#fs_select").picker({
							title : "选择分类",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values : goods_type,
									displayValues : goods_type
								}
								]
						});*/
						$("#unit_select").picker({
							title : "选择单位",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values : goods_dw,
									displayValues : goods_dw
								}
							]
						});
					}
					
				}
			];
		});
	}).call(this);
	
	//添加或更换图片
	function previewImage(file,index) {
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