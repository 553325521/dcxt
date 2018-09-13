
	(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					//初始化form表单
					scope.form = {};
					scope.pageShow = "False";
					scope.form.VCARD_PK = params.vcard_id
					scope.twjs = [];
					//使用须知，特权说明，最大字数限制
					scope.MAX_WORDS_COUNT_1 = 1000
					//初始化使用须知和特权说明
					scope.form.VCARD_SYXZ = "";
					scope.form.VCARD_TQSM = "";
					
					var init = function(){
						$httpService.post(config.initURL).success(function(data) {
							if (data.code != '0000') {
								loggingService.info(data.data);
							} else {
								item = data.data
								 $("#d3").select({
								        title: "选择门店",
								        multi: true,
								        split:',',
								        closeText:'完成',
								        items: item,
								        onChange: function(d) {
								          scope.form.USE_SHOP = d.values
								          scope.SHOP = d.titles
								        }
								   });
								init2();
							}
						}).error(function(data) {
							loggingService.info('获取测试信息出错');
						});
						
					}
					
					var init2 = function(){
						//判断是修改还是添加
						if(scope.form.VCARD_PK !== 'undefined' && scope.form.VCARD_PK !== ''){
							scope.pageTitle = "修改会员卡";
							//发送post请求查询会员卡信息
							$httpService.post(config.findURL,scope.form).success(function(data) {
								if (data.code != '0000') {
									loggingService.info(data.data);
								} else {
									scope.pageShow = "True";
									scope.form = data.data
									scope.form.START_MONEY = Number(scope.form.START_MONEY)/100;
									scope.twjs = angular.fromJson(scope.form.VCARD_TWJS);
									if(scope.twjs === undefined || scope.twjs === 'undefined' || scope.twjs === '""'){
										scope.twjs = []
									}
									//根据商铺的id得到商铺的名字
									scope.USE_SHOP_NAMES = scope.form.USE_SHOP
									angular.forEach(item,function(d,index,array){
										scope.USE_SHOP_NAMES = scope.USE_SHOP_NAMES.replace(d.value, d.title)	
									});
									//分割日期
									if(scope.form.ALLOTTED_TIME == 2){
										scope.START_TIME = scope.form.ALLOTTED_TIME_PERIOD.split(' ')[0]
										scope.END_TIME = scope.form.ALLOTTED_TIME_PERIOD.split(' ')[1]
									}
									scope.$apply()
								}
							}).error(function(data) {
								loggingService.info('获取测试信息出错');
							});
							
						}else{
							scope.pageShow = "True";
							scope.pageTitle = "添加会员卡";
							scope.form.IS_USE = 1;
							scope.form.ALLOTTED_TIME = 1;
							scope.form.VCARD_ZKXS = '100.00';
							scope.form.VCARD_JFXS = '100.00';
							scope.form.START_MONEY = '0.00';
							scope.form.START_JF = '0.00';
						}
					}
					
					init();
					
					scope.addTWJS = function(){
						scope.twjs.push({'img':'','text':''});
					}
					
					scope.toHref = function(path) {
						$('.close-picker').click()//如果选择框还没关闭，关闭它
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					
					//监听使用须知数据
					$scope.$watch('form.VCARD_SYXZ', function(newValue, oldValue) {
						if(newValue.length > scope.MAX_WORDS_COUNT_1){
							scope.form.VCARD_SYXZ = scope.form.VCARD_SYXZ.slice(0, scope.MAX_WORDS_COUNT_1);
						}
					});
					
					$scope.$watch('form.VCARD_TQSM', function(newValue, oldValue) {
						if(newValue.length > scope.MAX_WORDS_COUNT_1){
							scope.form.VCARD_TQSM = scope.form.VCARD_TQSM.slice(0, scope.MAX_WORDS_COUNT_1);
						}
					});
					
					var $form = $("#form");
					$form.form();
					//保存
					scope.doSave = function(){
						$('.close-picker').click()//如果选择框还没关闭，关闭它
						scope.form.VCARD_TWJS = JSON.stringify(scope.twjs)
						console.info(scope.form)
						if(scope.form.VCARD_LOGO === undefined || scope.form.VCARD_LOGO === ""){
							 $.toptips("添加一个LOGO");
							 return;
						}
						if(scope.form.BACKGROUND_IMAGE === undefined || scope.form.BACKGROUND_IMAGE === ""){
							$.toptips("添加一个会员卡背景图片");
							 return;
						}
						//期间转换
						if(scope.form.ALLOTTED_TIME == 2){
							startTime = $('#start_time').val()
							endTime = $('#end_time').val()
							if(startTime == ""){
								$.toptips("请选择一个开始日期");
								return;
							}
							if(endTime == ""){
								$.toptips("请选择一个结束日期");
								return;
							}
							a = startTime.replace(/-/g,'')
							if(Number(startTime.replace(/-/g,'')) > Number(endTime.replace(/-/g,''))){
								$.toptips("开始日期不允许大于结束日期");
								return;
							}
							
							scope.form.ALLOTTED_TIME_PERIOD = startTime + " " + endTime
						}
						
						
						$form.validate(function(error) {
							if (!error) {
								//弹出保存询问
								var m2 = {
									"url" : "aps/content/SystemSetup/AdvancedSetting/vipCardSetting/setting/config.json",
									"title" : "提示",
									"contentName" : "modal",
									"text" : "是否保存?"
								}
								eventBusService.publish(controllerName, 'appPart.load.modal', m2);
							}
						});
					}
					
					// 弹窗确认事件
					eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
						$httpService.post(config.insertOrUpdate,scope.form).success(function(data) {
							 if(data.code != "0000"){
								 console.info(data.data)
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
									"toUrl" : "aps/content/SystemSetup/AdvancedSetting/vipCardSetting/config.json?fid="+params.fid
								 }
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
					});
					/*初始化商铺选择数据源*/
					scope.shopArray = [];
					function comboboxInit() {
						 $("#start_time").datetimePicker({
						     title: '选择开始日期',m:1
						      });
						 $("#end_time").datetimePicker({
							 title: '选择结束日期',m:1
						      });
					}
					comboboxInit();
				}
			];
		});
	}).call(this);
	//添加或更换图片
	function previewImage(file) {
//		index = angular.element(file).scope().$index  //onchange里边获取不到$index，只能这么获取了
		id = file.id
		index = angular.element(file).scope().$index
	    for(var i=0;i<file.files.length;i++){
		    if (file.files && file.files[i]) {
		        var reader = new FileReader();
		        reader.onload = function (evt) {
		        	console.info(evt.loaded)
		        	if(evt.loaded > 2097152){
		        		$.toptips('单张图片上传大小最大为2M')
		        		return;
		        	}
		        	if(id == 'i1'){
		        		//添加商铺logo
		        		scope.form.VCARD_LOGO = evt.target.result;
		        	}else if(id == 'i2'){
		        		scope.form.BACKGROUND_IMAGE = evt.target.result;
		        	}else{
		        		scope.twjs[index]['img'] = evt.target.result;
		        	}
		        	
		        	scope.$apply();
		        };
		        reader.readAsDataURL(file.files[i]);
		    }
	    
	    }
	}	
	$(function() {
	})