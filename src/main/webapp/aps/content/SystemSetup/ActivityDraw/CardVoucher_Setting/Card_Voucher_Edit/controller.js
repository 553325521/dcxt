
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
					 
					// 定义页面标题
					scope.pageTitle = '卡券设置';	
					
					/*初始化是否启用*/
					scope.form.IS_USE = "1";
					
					/*初始化有效期限*/
					scope.form.EXPIRY_DATE = "DATE_TYPE_FIX_TIME_RANGE";
					
					scope.PICTURE_URL = [];
					
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
						
						console.info(scope.form.SHOPID);
						scope.form.code_type = $("#codeType_select").val();
						
						scope.form.INTRODUCE_STR = JSON.stringify(scope.introduceArray);
						
						scope.form.begin_time = $("#start_time").val();
						
						scope.form.end_time = $("#end_time").val();
						
						var flag = true;
						/*判断适用门店
						if(scope.form.SHOPID == ""){
							flag = false;
							scope.validateForm('.shopCell','.shopIcon','请选择适用门店');
						}else{
							flag = true;
							scope.returnForm('.shopCell','.shopIcon');
						}
						/*判断Logo
						if(scope.form.IMG_LOGO_STR == ""){
							flag = false;
							scope.validateForm('.logoCell','.logoIcon','请上传图片');
						}else{
							flag = true;
							scope.returnForm('.logoCell','.logoIcon');
						}*/
						if (flag) {
							//弹出保存询问
							var m2 = {
								"url" : "aps/content/SystemSetup/ActivityDraw/CardVoucher_Setting/Card_Voucher_Edit/config.json",
								"title" : "提示",
								"contentName" : "modal",
								"text" : "是否保存?"
							}
							eventBusService.publish(controllerName, 'appPart.load.modal', m2);
						}
					}
					
					// 弹窗确认事件
					eventBusService.subscribe(controllerName, controllerName + '.confirm', function(event, btn) {
						$httpService.post(config.updateCardURL,scope.form).success(function(data){
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
					//根据颜色名称查询颜色编码
					scope.findColorCodeByName = function(colorName){
						for(var i = 0;i < scope.colorArray.length;i++){
							if(scope.colorArray[i].colorName == colorName){
								return scope.colorArray[i].colorNumber
							}
						}
					}
					/*初始化卡券code类型*/
					scope.codeType_text = ['文 本','一维码','二维码 ','二维码无code显示','一维码无code显示',' 不显示code和条形码类型'];
					scope.codeType_value = ['CODE_TYPE_TEXT','CODE_TYPE_BARCODE','CODE_TYPE_QRCODE','CODE_TYPE_ONLY_QRCODE','CODE_TYPE_ONLY_BARCODE','CODE_TYPE_NONE'];
					/*初始化下拉框*/
					function comboboxCodeTypeInit() {
						$("#codeType_select").picker({
							title : "选择类型",
							toolbarCloseText : '确定',
							cols : [
								{
									textAlign : 'center',
									values :scope.codeType_value,
									displayValues : scope.codeType_text
								}
							]
						})
					}
					comboboxCodeTypeInit();
					var init = function(){
						$httpService.post(config.loadCardByIdURL,scope.form).success(function(data){
							if (data.code == '0000') {
								/*初始化有效期 当为期间的时候处理*/
								scope.form.EXPIRY_DATE = data.data.date_type;
								if(scope.form.EXPIRY_DATE == "DATE_TYPE_FIX_TIME_RANGE"){
									scope.yxQX = "True";
									scope.yjShow = "True";
									scope.form.begin_time = scope.formatDate(data.data.begin_timestamp);
									scope.form.end_time = scope.formatDate(data.data.end_timestamp);
								}else{
									scope.yxQX = "False";
									scope.yjShow = "False";
								}
								/*初始化card_id*/
								scope.form.card_id =data.data.card_id;
								/*初始化卡券类型*/
								scope.form.card_type =data.data.card_type;
								/*初始化卡券logo*/
								 scope.IMG_LOGO[0] = data.data.logo_url;
						    	 scope.form.IMG_LOGO_STR = JSON.stringify(scope.IMG_LOGO);
						    	 /*初始化卡券使用提醒*/
						    	 scope.form.notice = data.data.notice;
						    	 /*初始化卡券使用说明*/
						    	 scope.form.description = data.data.description;
						    	/*初始化共享类型*/
								scope.form.SHARE_TYPE = data.data.can_give_friend;
						    	 /*初始化卡券领取数量*/
						    	 scope.form.get_limit = data.data.get_limit;
						    	 /*初始化卡券是否可分享*/
						    	 scope.form.can_share = data.data.can_share;
						    	/*  初始化卡券颜色*/
						    	  scope.form.confirmColor = scope.findColorCodeByName(data.data.color)
						    	 /* 初始化卡券适用门店*/
						    	/*  scope.form.SHOP_NAME = data.data.shopName;*/
						    	  
						    	 /* 初始化卡券code类型*/
						    	  	scope.form.code_type = data.data.code_type;
						    	  $("#codeType_select").val(data.data.code_type);
						    	  
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
			       }
			        scope.$apply();
		       };
		        reader.readAsDataURL(file.files[0]);
		   }
	}
	$(function() {
	})