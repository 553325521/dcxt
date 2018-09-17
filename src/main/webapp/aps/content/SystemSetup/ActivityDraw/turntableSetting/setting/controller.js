
	(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					//初始化form表单
					scope.form = {};
					scope.pageShow = "False";
					scope.TURNTABLE_PRIZE = []
					scope.form.TURNTABLE_PK = params.turntable_id
					scope.bzsm = [];
					scope.prizename_show = false;
					//奖品下拉框
					cate_list = ['自定义','卡券','积分','储值'];
					prize_list = ['20元优惠券','40元优惠券']
					//初始化使用须知和特权说明
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
						
						if(params.turntable_id != undefined && params.turntable_id != 'undefined' && params.turntable_id != ''){
							scope.pageTitle = "修改转盘";
							//发送post请求查询会员卡信息
							$httpService.post(config.findURL,scope.form).success(function(data) {
								if (data.code != '0000') {
									loggingService.info(data.data);
								} else {
									scope.pageShow = "True";
									scope.form = data.data
									scope.TURNTABLE_PRIZE = angular.fromJson(scope.form.TURNTABLE_PRIZE);
									scope.bzsm = angular.fromJson(scope.form.TURNTABLE_BZSM);
									if(scope.TURNTABLE_PRIZE == undefined || scope.TURNTABLE_PRIZE == 'undefined' || scope.TURNTABLE_PRIZE == '""' || scope.TURNTABLE_PRIZE == '[]'){
										scope.TURNTABLE_PRIZE = []
									}
									if(scope.bzsm === undefined || scope.bzsm === 'undefined' || scope.bzsm === '""'){
										scope.bzsm = []
									}
									//根据商铺的id得到商铺的名字
									scope.USE_SHOP_NAMES = scope.form.USE_SHOP
									angular.forEach(item,function(d,index,array){
										scope.USE_SHOP_NAMES = scope.USE_SHOP_NAMES.replace(d.value, d.title)	
									});
									//分割日期
									if(scope.form.TURNTABLE_YXQX != undefined && scope.form.TURNTABLE_YXQX !=''){
										scope.START_TIME = scope.form.TURNTABLE_YXQX.split(' ')[0]
										scope.END_TIME = scope.form.TURNTABLE_YXQX.split(' ')[1]
									}
									scope.$apply();
								}
							}).error(function(data) {
								loggingService.info('获取测试信息出错');
							});
							
						}else{
							scope.pageShow = "True";
							scope.pageTitle = "添加转盘";
							scope.form.IS_USE = 1;
							scope.form.TURNTABLE_CYDX = 1;
							scope.form.PARTICIPATION_WAY = 1;//参与方式初始化
							scope.form.PARTICIPATION_KCJF = 0;
							scope.$apply();
						}
					}
					
					init();
					scope.addBZSM = function(){
						scope.bzsm.push({'img':'','text':''});
					}
					scope.delBZSM = function(index){
						console.info(index)
						scope.bzsm.splice(index,1);
					}
					
					scope.addJPSZ = function(){
						scope.TURNTABLE_PRIZE.push({'cate':{'name':'自定义','id':''},'award_name':'','awards':{'name':'','id':''},'true_num':'','num':''})
					}
					
					scope.delJPSZ = function(index){
						scope.TURNTABLE_PRIZE.splice(index,1);
						console.info(index)
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
					
					
					scope.initprize_selects = function(index,value){
						$(".cate_select:eq(" + index + ")").val(value);
						$(".cate_select:eq(" + index + ")").picker({
							title : "选择类别",
							cols : [
								{
									textAlign : 'center',
									values : cate_list
								}
							],
							onChange : function(e){
								ind = e.params.input.dataset.id
								scope.TURNTABLE_PRIZE[ind].cate.name = e.value[0];
								if("卡券" == e.value[0]){
									$(".prize_select:eq(" + ind + ")").picker({
										title : "选择奖品名",
										cols : [
											{
												textAlign : 'center',
												values : prize_list,
												displayValues : prize_list
											}
										],
										onChange : function(e){
											prize_index = e.params.input.dataset.id
											scope.TURNTABLE_PRIZE[prize_index].awards.name= e.value[0];
											scope.TURNTABLE_PRIZE[prize_index].awards.id= e.value[0];
											scope.$apply()
										}
									});
									scope.TURNTABLE_PRIZE[ind].awards.name= prize_list[0];
								}else{
									$(".prize_select:eq(" + e.params.input.dataset.id + ")")[0] = "";	
									console.info($(".prize_select:eq(" + e.params.input.dataset.id + ")"))
								}
								
								scope.$apply()
								
							}
						});
					}
					
					
					var $form = $("#form");
					$form.form();
					//保存
					scope.doSave = function(){
						$('.close-picker').click()//如果选择框还没关闭，关闭它
						scope.form.TURNTABLE_BZSM = JSON.stringify(scope.bzsm)
						scope.form.TURNTABLE_PRIZE = JSON.stringify(scope.TURNTABLE_PRIZE)
						
						console.info(scope.form)
						//期间转换
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
						
						scope.form.TURNTABLE_YXQX = startTime + " " + endTime
						
						
						$form.validate(function(error) {
							if (!error) {
								//弹出保存询问
								var m2 = {
									"url" : "aps/content/SystemSetup/ActivityDraw/turntableSetting/setting/config.json",
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
									"toUrl" : "aps/content/SystemSetup/ActivityDraw/turntableSetting/config.json?fid="+params.fid
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
		        	scope.bzsm[index]['img'] = evt.target.result;
		        	scope.$apply();
		        };
		        reader.readAsDataURL(file.files[i]);
		    }
	    
	    }
	}	
	$(function() {
	})