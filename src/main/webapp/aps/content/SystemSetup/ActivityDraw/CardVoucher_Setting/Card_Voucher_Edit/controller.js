
	(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					
					Array.prototype.indexOf = function(val) {
						for (var i = 0; i < this.length; i++) {
							if (this[i] == val)
								return i;
						}
						return -1;
					};
					Array.prototype.remove = function(val) {
						var index = this.indexOf(val);
						if (index > -1) {
							this.splice(index, 1);
						}
					};
					
					scope = $scope;
					//初始化form表单
					scope.form = {};
					scope.pageShow = "False";
					
					/*初始化card_id*/
					scope.form.card_id = params.card_id
					
					scope.form.fid = params.fid;
					
					/*初始化店铺LOGO*/
					 scope.IMG_LOGO = [];
					 
					 scope.form.IMG_LOGO_STR = "";
					 
					 /*初始化卡券形象图片*/
					 scope.IMG_BODAY = [];
					 
					 scope.form.IMG_BODAY_STR = "";
					 
					 /*初始化图文介绍字符串*/
					 scope.form.INTRODUCE_STR = "";
					 
					// 定义页面标题
					scope.pageTitle = '卡券设置';	
					
					/*初始化是否启用*/
					scope.form.IS_USE = "1";
					
					/*初始化有效期限*/
					scope.form.EXPIRY_DATE = "DATE_TYPE_FIX_TIME_RANGE";
					
					scope.yjShow = "True";
					scope.qjShow = "True";
					scope.tsShow = "False";
					
					scope.PICTURE_URL = [];
					
					/*初始化卡券类型*/
					scope.form.CARD_VOUCHER_TYPE = "CASH";
					
					scope.DKTYPE_ISSHOW = "False";
					scope.DJQ_ISSHOW = "True";
					scope.ZKQ_ISSHOW = "False";
					scope.DKQ_ISSHOW = "False";
					scope.DKQ_MONEY_ISSHOW = "False";
					
					
					/*初始化抵扣类型的值*/
					scope.form.DUDUCTION_TYPE = "0"; 
					
					/*初始化共享类型*/
					scope.form.SHARE_TYPE = "true";
					
					/*	初始化优惠共享*/
					scope.form.FAVOURABLE_SHARE = "不与其他优惠共享";
					
					/*初始化有限时段选择值*/
					scope.form.EFFECTIVE_TIME = "0";
					
					scope.time_week ="False";
					
					scope.time_time ="False";
					
					/*初始化图文介绍*/
					scope.introduceArray = [{"img":"","text":"","imgName":"","imgSize":""}];
					
					scope.form.timePeriod = "全天";
					
					/*初始化颜色数据源*/
					scope.colorArray = [
						{'colorName':'Color010','colorNumber':'	#63b359'},
						{'colorName':'Color020','colorNumber':'	#2c9f67'},
						{'colorName':'Color030','colorNumber':'	#509fc9'},
						{'colorName':'Color040','colorNumber':'	#5885cf'},
						{'colorName':'Color050','colorNumber':'	#9062c0'},
						{'colorName':'Color060','colorNumber':'	#d09a45'},
						{'colorName':'Color070','colorNumber':'	#e4b138'},
						{'colorName':'Color080','colorNumber':'	#ee903c'},
						{'colorName':'Color081','colorNumber':'	#f08500'},
						{'colorName':'Color082','colorNumber':'	#a9d92d'},
						{'colorName':'Color090','colorNumber':'	#dd6549'},
						{'colorName':'Color100','colorNumber':'	#cc463d'},
						{'colorName':'Color101','colorNumber':'	#cf3e36'},
						{'colorName':'Color102','colorNumber':'	#5E6671'},
					]
					
				
					/*初始化默认选择卡券颜色*/
					scope.form.confirmColor = "#63b359";
					
					scope.form.confirmName="Color010";
					
					/*选择颜色调用方法*/
					scope.selectColor = function(currentSelect){
						$("i").remove(".singleColor");
						$("div[user-attr='"+currentSelect.item.colorName+"']").after(`<i class="icon-caret-up singleColor" style="margin-left:15%"></i>`);
						scope.form.confirmColor = currentSelect.item.colorNumber;
						
						scope.form.confirmName=currentSelect.item.colorName;
					}
					
					//添加
					scope.add = function(){
						if(scope.introduceArray.length>=3){
							return;
						}
						scope.introduceArray.push({"img":"","text":"","imgName":"","imgSize":""});//拷贝数组，防止改一个全改了
					}
					//删除
					scope.del = function(index){
						scope.introduceArray.splice(index,1);
					}
					
					scope.SD_SHOW = "False";
					
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
							scope.SD_SHOW = "True";
							$scope.$watch('form.TIMEDUAN', function(newValue, oldValue) {
								if (newValue === '0') {
									scope.form.timePeriod ="全天";
									scope.time_time ="False";
								}else{
									scope.time_time ="True";
								}
							}, true);
						}
					}, true);
					
					// 初始化限星期数据源
					$scope.TIME_WEEK = [
						{
							value : 'MONDAY' ,
							name : '周一 ',
							checked: true
						},
						{
							value : 'TUESDAY' ,
							name : '周二',
							checked: false
						},
						{
							value : 'WEDNESDAY' ,
							name : '周三 ',
							checked: false
						},
						{
							value : 'THURSDAY' ,
							name : '周四 ',
							checked: false
						},
						{
							value : 'FRIDAY' ,
							name : '周五 ',
							checked: false
						},
						{
							value : 'SATURDAY' ,
							name : '周六 ',
							checked: false
						},
						{
							value : 'SUNDAY',
							name : '周日 ',
							checked: false
						}
					];
					/*初始化选择星期数组*/
					scope.form.weekArray = [{
						value : 'MONDAY' ,
						name : '周一 ',
						checked: true
					}];
					/*星期选择方法*/
					scope.selectWeek = function(item){
						var action = (item.checked ? 'add' : 'remove');
						if (action == "add") {
							scope.form.weekArray.push({value:item.value,name:item.name,checked:item.checked});
						/*	scope.form.timePeriod  = JSON.stringify(scope.form.GTYPE_AREA_Array);*/
						} else {
							scope.form.weekArray.remove({value:item.value,name:item.name,checked:item.checked});
							/*scope.form.GTYPE_AREA = JSON.stringify(scope.form.GTYPE_AREA_Array);*/
						}
					}
					
					scope.form.TIMEDUAN = '0';
					
					/*初始化限时段数据源*/
					scope.TIME_TIME = [
						{
							name : '上午',
							checked: false
						},
						{
							name : '中午',
							checked: false
						},
						{
							name : '下午',
							checked: false
						},
						{
							name : '晚上',
							checked: false
						}
					];
					
					scope.form.timeArray = [];
					
				/*	时段选择方法*/
					scope.selectTime = function(item){
						var action = (item.checked ? 'add' : 'remove');
							if (action == "add") {
								scope.form.timeArray.push({name:item.name,checked:item.checked});
								scope.form.timePeriod = scope.form.timeArray;
							} else {
								scope.form.timeArray.remove({name:item.name,checked:item.checked});
								scope.form.timePeriod = scope.form.timeArray;
							}
					}
					
					/*监听卡券类型选择值*/

					$scope.$watch('form.CARD_VOUCHER_TYPE', function(newValue, oldValue) {
						if (newValue === 'CASH') {
							scope.DKTYPE_ISSHOW = "False";
							scope.KQCELL_ISSHOW = "True";
							scope.DJQ_ISSHOW = "True";
							scope.DHQ_ISSHOW = "False";
							scope.ZKQ_ISSHOW = "False";
							scope.DKQ_ISSHOW = "False";
							scope.DKQ_MONEY_ISSHOW = "False";
						}else if(newValue === 'DISCOUNT'){
							scope.DKTYPE_ISSHOW = "False";
							scope.DJQ_ISSHOW = "False";
							scope.DHQ_ISSHOW = "False";
							scope.KQCELL_ISSHOW = "True";
							scope.ZKQ_ISSHOW = "True";
							scope.DKQ_ISSHOW = "False";
							scope.DKQ_MONEY_ISSHOW = "False";
						}else if(newValue === '2'){
							scope.DKTYPE_ISSHOW = "True";
							scope.DJQ_ISSHOW = "False";
							scope.DHQ_ISSHOW = "False";
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
							scope.KQCELL_ISSHOW = "True";
							scope.DHQ_ISSHOW = "True";
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
						}else if(newValue === 'DATE_TYPE_FIX_TIME_RANGE'){
							scope.yjShow = "True";
							scope.qjShow = "True";
							scope.tsShow = "False";
						}else{
							scope.yjShow = "True";
							scope.qjShow = "False";
							scope.tsShow = "True";
						}
					}, true);
					
					/*form表单验证每一项方法*/
					scope.validateForm = function(cellName,iconName,tip){
						$(cellName).css("color","#f43530");
						$(iconName).css("display","inline-block");
						/*$.toptips(tip);*/
					}
					scope.returnForm = function(cellName,iconName){
						$(cellName).css("color","#363636");
						$(iconName).css("display","none");
					}
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json?fid=" + scope.form.fid,
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					scope.form.SHOPID = "";
					
					//保存
					scope.doSave = function(){
						
						scope.form.INTRODUCE_STR = JSON.stringify(scope.introduceArray);
						
						scope.form.begin_time = $("#start_time").val();
						
						scope.form.end_time = $("#end_time").val();
						
						var flag = true;
						
						/*判断活动名称*/
						if(scope.form.title == undefined){
							flag = false;
							scope.validateForm('.titleCell','.titleIcon','请输入活动名称');
						}else{
							flag = true;
							scope.returnForm('.titleCell','.titleIcon');
						}
						/*期限开始时间判断*/
						if(scope.form.begin_time =="" && scope.form.EXPIRY_DATE == "DATE_TYPE_FIX_TIME_RANGE"){
							flag = false;
							scope.validateForm('.expiryCell','.expiryIcon','请选择开始时间');
						}else if(scope.form.begin_time !="" && scope.form.EXPIRY_DATE == "DATE_TYPE_FIX_TIME_RANGE"){
							flag = true;
							scope.returnForm('.expiryCell','.expiryIcon');
						}
						/*期限结束时间判断*/
						if(scope.form.end_time =="" && scope.form.EXPIRY_DATE == "DATE_TYPE_FIX_TIME_RANGE"){
							flag = false;
							scope.validateForm('.expiryCell','.expiryEndIcon','请选择结束时间');
						}else if(scope.form.end_time !="" && scope.form.EXPIRY_DATE == "DATE_TYPE_FIX_TIME_RANGE"){
							flag = true;
							scope.returnForm('.expiryCell','.expiryEndIcon');
						}
						/*选择天数的时候判断*/
						if(scope.form.fixed_begin_term ==undefined && scope.form.EXPIRY_DATE == "DATE_TYPE_FIX_TERM"){
							flag = false;
							scope.validateForm('.termCell','.termBeginIcon','请填写天数');
						}else if(scope.form.fixed_begin_term !=undefined && scope.form.EXPIRY_DATE == "DATE_TYPE_FIX_TERM"){
							flag = true;
							scope.returnForm('.termCell','.termBeginIcon');
						}
						if(scope.form.fixed_term ==undefined && scope.form.EXPIRY_DATE == "DATE_TYPE_FIX_TERM"){
							flag = false;
							scope.validateForm('.termCell','.termIcon','请填写天数');
						}else if(scope.form.fixed_term !=undefined && scope.form.EXPIRY_DATE == "DATE_TYPE_FIX_TERM"){
							flag = true;
							scope.returnForm('.termCell','.termIcon');
						}
						/*判断预发数量*/
						if(scope.form.PREVIEW_COUNT == undefined){
							flag = false;
							scope.validateForm('.priviewCountCell','.priviewCountIcon','请输入预发数量');
						}else{
							flag = true;
							scope.returnForm('.priviewCountCell','.priviewCountIcon');
						}
						/*判断提醒天数*/
						if(scope.form.REMIND_DAYS == undefined){
							flag = false;
							scope.validateForm('.priviewCountCell','.remindDaysIcon','请输入提醒天数');
						}else{
							flag = true;
							scope.returnForm('.priviewCountCell','.remindDaysIcon');
						}
						/*当选择代金券的时候判断*/
						if(scope.form.reduce_cost ==undefined && scope.form.CARD_VOUCHER_TYPE == "CASH"){
							flag = false;
							scope.validateForm('.djqCell','.reduceIcon','请填写金额');
						}else if(scope.form.reduce_cost !=undefined && scope.form.CARD_VOUCHER_TYPE == "CASH"){
							flag = true;
							scope.returnForm('.djqCell','.reduceIcon');
						}
						if(scope.form.least_cost ==undefined && scope.form.CARD_VOUCHER_TYPE == "CASH"){
							flag = false;
							scope.validateForm('.djqCell','.leastIcon','请填写金额');
						}else if(scope.form.least_cost !=undefined && scope.form.CARD_VOUCHER_TYPE == "CASH"){
							flag = true;
							scope.returnForm('.djqCell','.leastIcon');
						}
						/*判断适用门店*/
						if(scope.form.SHOPID == ""){
							flag = false;
							scope.validateForm('.shopCell','.shopIcon','请选择适用门店');
						}else{
							flag = true;
							scope.returnForm('.shopCell','.shopIcon');
						}
						/*判断Logo*/
						if(scope.form.IMG_LOGO_STR == ""){
							flag = false;
							scope.validateForm('.logoCell','.logoIcon','请上传图片');
						}else{
							flag = true;
							scope.returnForm('.logoCell','.logoIcon');
						}
						/*判断卡券形象*/
						if(scope.form.IMG_BODAY_STR == ""){
							flag = false;
							scope.validateForm('.bodyCell','.bodyIcon','请上传图片');
						}else{
							flag = true;
							scope.returnForm('.bodyCell','.bodyIcon');
						}
						if (flag) {
							//弹出保存询问
							var m2 = {
								"url" : "aps/content/SystemSetup/ActivityDraw/CardVoucher_Setting/Card_Voucher_Add/config.json",
								"title" : "提示",
								"contentName" : "modal",
								"text" : "是否保存?"
							}
							eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						}
					}
					
					// 弹窗确认事件
					eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
						$httpService.post(config.createCardURL,scope.form).success(function(data){
							if (data.code != '0000') {
								var m2 = {
									"title" : "提示",
									"contentName" : "modal",
									"text" : data.data,
								}
							} else {
								var m2 = {
									"title" : "提示",
									"contentName" : "modal",
									"text" : data.data,
									"toUrl" : "aps/content/SystemSetup/ActivityDraw/CardVoucher_Setting/config.json?fid=" + scope.form.fid
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
									/*scope.shopArray.push({title:'测试',value:'fdsf1123'});*/
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
							        	scope.form.SHOPID = d.values;
							        	scope.form.SHOP_NAME = d.titles;
							        	/*scope.$apply();*/
							        }
							      });
						    }).error(function(data){
						    	loggingService.info('获取测试信息出错');
						    });
					}
					comboboxInit();
					
					 // 第三种方式：日期函数处理(时间戳转日期格式yyyy-MM-dd)
					scope.formatDate = function timestampToTime(timestamp) {
				        var date = new Date(timestamp * 1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
				        var Y = date.getFullYear() + '-';
				        var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
				        var D = date.getDate();
				        return Y+M+D;
				    }
					var init = function(){
						$httpService.post(config.loadCardByIdURL,scope.form).success(function(data){
							if (data.code == '0000') {
								/*初始化卡券名称*/
								scope.form.title = data.data.title;
								/*初始化有效期 当为期间的时候处理*/
								scope.form.EXPIRY_DATE = data.data.date_type;
								if(scope.form.EXPIRY_DATE == "DATE_TYPE_FIX_TIME_RANGE"){
									scope.form.begin_time = scope.formatDate(data.data.begin_timestamp);
									scope.form.end_time = scope.formatDate(data.data.end_timestamp);
								}
								/*初始化预发数量*/
								scope.form.PREVIEW_COUNT = data.data.quantity;
								/*初始化卡券类型*/
								scope.form.CARD_VOUCHER_TYPE = data.data.card_type;
								/*当前为代金券的时候初始化数据*/
								if(scope.form.CARD_VOUCHER_TYPE == 'CASH'){
									scope.form.reduce_cost = data.data.reduce_cost;
									scope.form.least_cost = data.data.least_cost;
								}
								/*初始化卡券logo*/
								 scope.IMG_LOGO[0] = data.data.logo_url;
						    	 scope.form.IMG_LOGO_STR = JSON.stringify(scope.IMG_LOGO);
								/*初始化卡券形象图片*/
						    	  scope.IMG_BODAY[0] = data.data.icon_url_list;
						    	  scope.form.IMG_BODAY_STR = JSON.stringify(scope.IMG_BODAY);
						    	 /* 初始化图文介绍*/
								scope.$apply();
							}
						    }).error(function(data){
						    	loggingService.info('获取测试信息出错');
						 });
						$("#start_time").datetimePicker({title:"选择日期", toolbarCloseText : '确定',m:1});
						$("#end_time").datetimePicker({title:"选择日期",toolbarCloseText : '确定',m:1});
						scope.pageShow = "True";
					}
					init();
				}
			];
		});
	}).call(this);
	var MAX_PIC_SIZE = 2097152;//单张图片上传最大大小
	//店铺LOGO和形象图片更换方法
	function previewImage(file) {
		index = file.id
		 if (file.files && file.files[0]) {
		      var reader = new FileReader();
		      reader.onload = function (evt) {
			       if(evt.loaded > MAX_PIC_SIZE){
			        $.toptips('单张图片上传大小最大为'+Math.floor(MAX_PIC_SIZE/1000000)+'M')
			        return;
			       }
			       if(index == 'i1'){
			    	   scope.IMG_LOGO[0] = evt.target.result;
			    	   scope.IMG_LOGO[1] = file.files[0].name;
			    	   scope.IMG_LOGO[2] = evt.loaded;
			    	   scope.form.IMG_LOGO_STR = JSON.stringify(scope.IMG_LOGO);
			    	/*   scope.form.IMG_LOGO = evt.target.result;*/
			       }else if(index == 'i2'){
			    	   scope.IMG_BODAY[0] = evt.target.result;
			    	   scope.IMG_BODAY[1] = file.files[0].name;
			    	   scope.IMG_BODAY[2] = evt.loaded;
			    	   scope.form.IMG_BODAY_STR = JSON.stringify(scope.IMG_BODAY);
			       }
			        scope.$apply();
		       };
		        reader.readAsDataURL(file.files[0]);
		   }
	}
	//图文介绍图片更换方法
	function previewImage1(file) {
		index = file.id
		 if (file.files && file.files[0]) {
		      var reader = new FileReader();
		      reader.onload = function (evt) {
			       if(evt.loaded > MAX_PIC_SIZE){
			        $.toptips('单张图片上传大小最大为'+Math.floor(MAX_PIC_SIZE/1000000)+'M')
			        return;
			       }
			       	   scope.introduceArray[index].imgName = file.files[0].name;
			       	   scope.introduceArray[index].imgSize = evt.loaded;
			    	   scope.introduceArray[index].img = evt.target.result;
			    	   scope.$apply();
		       };
		        reader.readAsDataURL(file.files[0]);
		   }
	}
	$(function() {
	})