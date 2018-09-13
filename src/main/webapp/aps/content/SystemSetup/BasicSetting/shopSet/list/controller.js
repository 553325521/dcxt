(function() {
	define(['jqueryweui','pickercity','wangEditor'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope
				$scope.pageTitle = config.pageTitle;
				$scope.form = {};
				$scope.fenzi = 0;
				$scope.fenmu = 1000;
				
				//显示菜单关闭
				scope.pageShow = "False"
				
				//初始化店铺类型
				scope.form.SHOP_TYPE_1=""
				scope.form.SHOP_TYPE_2=""
				
				//初始化店铺类型
				shop_type = {
					/*"中式":["大牌档", "云吞面店/粉面店","茶楼","酒楼","菜馆","斋菜馆","其他"],
					"西式":["咖啡室","西餐厅","其他"],
					"混合式":["冰室/饮冰室", "快餐厅", "茶餐厅", "美食广场", "食堂", "其他"]*/
					"美食":["粤菜","茶餐厅","川菜","湘菜","东北菜","西北菜","火锅","自助餐","小吃","快餐","日本料理","韩国料理","东南亚菜","西餐","面包甜点","咖啡厅","江浙菜","其他美食","外卖","其他"],
					"休闲娱乐":["冷饮","茶馆","茶楼","酒吧","其他"],
					"购物":["超市","便利店","水果超市","鲜花礼品","酒类","其他"]
				}
				
				//设施库
				scope.support_library = {
						"免费WIFI" : false,
						"免费停车" : false,
						"无烟区" : false,
						"包厢" : false,
						"儿童椅" : false,
						"免费游乐场" : false,
						"游泳池" : false,
						"健身房" : false,
						"可带宠物":false,
						"可外卖":false,
				}
				
				//已选中的设施
				scope.SUPPORT = [];
				
				//初始化店铺类型1
				shop_type_first = [];
				for(var key in shop_type){
					shop_type_first.push(key)
				}
				
				//初始化商铺类型下拉框
				scope.form.SHOP_TYPE_1 = shop_type_first[0]
				scope.form.SHOP_TYPE_2 = shop_type[scope.form.SHOP_TYPE_1][0]
				$("#SHOP_TYPE_2").val(scope.form.SHOP_TYPE_2)
				$("#SHOP_TYPE_1").val(scope.form.SHOP_TYPE_1)
				
				//初始化区域下拉框
				scope.form.SHOP_AREA = "北京 北京 东城"
				
				//刷新第二个下拉框
				var flushShopType2 = function(){
					//给第二个下拉框赋默认值
					scope.form.SHOP_TYPE_2 = shop_type[scope.form.SHOP_TYPE_1][0]
					$("#SHOP_TYPE_2").val(scope.form.SHOP_TYPE_2)
					
					//填充下拉框
					$("#SHOP_TYPE_2").picker({
						title : "商铺类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : shop_type[scope.form.SHOP_TYPE_1]
							}
						],
					});
				} 
				//刷新商铺类型第二个下拉框
				flushShopType2()
				
				$scope.updateNum = function(){
					$scope.fenzi = $scope.form.SHOP_REMARK.length;
					//$scope.fenmu = 1000 - $scope.fenzi;
				}
				
				//点击了设施
				scope.clickSupport = function(text){
					console.info(scope.SUPPORT)
					if(scope.support_library[text] != undefined){
						scope.support_library[text] = scope.support_library[text] ? false : true;
						if(scope.support_library[text]){
							scope.SUPPORT.push(text);
						}else{
							scope.SUPPORT.splice(scope.SUPPORT.indexOf(text), 1)
						}
					}
					console.info(scope.SUPPORT)
				}
				
				
				
				var $form = $("#form");
				$form.form();
				$scope.doSave = function(){
					scope.form.SHOP_TYPE_2 = $("#SHOP_TYPE_2").val()
					scope.form.SHOP_REMARK = $(".textarea")[0].innerHTML
					scope.form.SHOP_AREA = $("#ssx").val();
					scope.form.SUPPORT = JSON.stringify(scope.SUPPORT);
					console.info(scope.form)
					$form.validate(function(error) {
						if (!error) {
							//弹出保存询问
							var m2 = {
								"url" : "aps/content/SystemSetup/BasicSetting/shopSet/list/config.json",
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
					 $httpService.post(config.saveShopInfoURL,scope.form).success(function(data){
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
								"toUrl" : "aps/content/SystemSetup/BasicSetting/config.json?fid=" + params.fid
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
				
				
				
				$scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				
			
				//初始化商铺类型第一个下拉框
				var comboboxInit = function() {
					$("#SHOP_TYPE_1").picker({
						title : "商铺类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : shop_type_first
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								console.info(value)
								scope.form.SHOP_TYPE_1 = value
								
								if(shop_type[value].indexOf(scope.form.SHOP_TYPE_2) == -1){
									//刷新商铺类型第二个下拉框
									flushShopType2()
								}
									
							}
						}
					});
					

					$("#ssx").cityPicker({
				        title: "选择省市县",
				        toolbarCloseText : '完成'
				     });
				}
				comboboxInit();
				
				
				var init = function(){
					//获取用户商铺信息
					$httpService.post(config.getShopInfoURL, $scope.form).success(function(data) {
						if (data.code === '0000') {
							scope.pageShow = "True"
							scope.form = data.data["shopinfo"][0];
							var typeStr = scope.form.SHOP_TYPE.split(" ");
							scope.form.SHOP_TYPE_1 = typeStr[0];
							scope.form.SHOP_TYPE_2 = typeStr[1];
							console.log($scope.form);
							scope.SUPPORT = JSON.parse(scope.form.SUPPORT);
							angular.forEach(scope.SUPPORT,function(data,index,array){
								if(scope.support_library[data] !== undefined && scope.support_library[data] == false){
									 scope.support_library[data] = true
								}
							})
							
							scope.$apply();
							//初始化config信息
							initConfig(data.data["config"])
						} else {
							return;
						}
						
						if(scope.form.SHOP_REMARK !== undefined){
							$(".textarea")[0].innerHTML = scope.form.SHOP_REMARK;
						}else{
							$(".textarea")[0].innerHTML = "";
						}
						
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
					
				}
				init();
				
				
				//页面调用jssdk之前所必须的config配置
				var initConfig = function(data){
					wx.config({
//						debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
					    appId: data.appId, // 必填，公众号的唯一标识
					    timestamp: data.timestamp,// 必填，生成签名的时间戳
					    nonceStr: data.noncestr, // 必填，生成签名的随机串
					    signature: data.signature,// 必填，签名
					    jsApiList: ['getLocation'] // 必填，需要使用的JS接口列表
					});

					wx.error(function(res){
						console.info("config失败")
						console.info(res)
					});
				}
				
				//获取位置信息
				scope.getLocation = function(){
					wx.getLocation({
						type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
						success: function (res) {
							var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
							var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
							scope.form.SHOP_GPS = "经:"+latitude+"  纬:"+longitude;
							scope.$apply();
						}
					});
				}
				

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

				
				initEdit()
				
			}
		];
	});
}).call(this);

var MAX_PIC_SIZE = 2097152;//单张图片上传最大大小
//添加或更换图片
function previewImage(file) {
	index = file.id
	 if (file.files && file.files[0]) {
	      var reader = new FileReader();
	      reader.onload = function (evt) {
		       console.info(evt.loaded)
		       if(evt.loaded > MAX_PIC_SIZE){
		        $.toptips('单张图片上传大小最大为'+Math.floor(MAX_PIC_SIZE/1000000)+'M')
		        return;
		       }
		       if(index == 'i1'){
		        //添加店铺LOGO
		    	   scope.form.IMG_LOGO = evt.target.result;
		       }else if(index == 'i2'){
		        //添加店铺形象
		    	   scope.form.IMG_BODAY = evt.target.result;
		       }else if(index == 'i3'){
		        	scope.form.IMG_HEAD = evt.target.result;
		        }
		        scope.$apply();
	       };
	        reader.readAsDataURL(file.files[0]);
	   }
}



