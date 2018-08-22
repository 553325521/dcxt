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
				const MAX_PIC_SIZE = 2097152;//单张图片上传最大大小
				//显示菜单关闭
				scope.pageShow = "False"
				
				//初始化店铺类型
				scope.form.SHOP_TYPE_1=""
				scope.form.SHOP_TYPE_2=""
				
				//初始化店铺类型
				shop_type = {
					"中餐":["川菜", "鲁菜","湘菜"],
					"西餐":["西餐1","西餐2","西餐3"]
				}
				
				//初始化店铺类型
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
					$("#SHOP_TYPE_2").picker({
						title : "商铺类型",
						toolbarCloseText : '确定',
						cols : [
							{
								textAlign : 'center',
								values : shop_type[scope.form.SHOP_TYPE_1]
							}
						],
						onChange : function(e) {
							if (e != undefined && e.value[0] != undefined) {
								var value = e.value[0]
								scope.form.SHOP_TYPE_2 = value
							}
						}
					});
					scope.form.SHOP_TYPE_2 = shop_type[scope.form.SHOP_TYPE_1][0]
					$("#SHOP_TYPE_2").val(scope.form.SHOP_TYPE_2)
				} 
				
				flushShopType2()
				
				$scope.updateNum = function(){
					$scope.fenzi = $scope.form.SHOP_REMARK.length;
					//$scope.fenmu = 1000 - $scope.fenzi;
				}
				
				
				
				$scope.doSave = function(){
//					$scope.form.SHOP_TYPE = scope.form.SHOP_TYPE_1 + " " + scope.form.SHOP_TYPE_2;
					scope.form.SHOP_REMARK = $(".textarea")[0].innerHTML
					scope.form.SHOP_AREA = $("#ssx").val();
					/*if($scope.form.SHOP_REMARK != undefined && $scope.form.SHOP_REMARK.length > 1000){
						return;
					}*/
					$httpService.post(config.saveShopInfoURL, scope.form).success(function(data) {
						if (data.code === '0000') {
							$scope.toHref("welcome");
						} else {
							$scope.toHref("welcome");
						}
						$scope.$apply()
					}).error(function(data) {
						loggingService.info('获取测试信息出错');
					});
				}
				
				$scope.toHref = function(path) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fid=" + params.fid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				
			
				
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
								scope.form.SHOP_TYPE_1 = value
								if(shop_type[value].indexOf(scope.form.SHOP_TYPE_2) == -1){
									flushShopType2(shop_type[value]);
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
							scope.$apply();
							//初始化config信息
							initConfig(data.data["config"])
						} else {
							
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
						debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
					    appId: data.appId, // 必填，公众号的唯一标识
					    timestamp: data.timestamp,// 必填，生成签名的时间戳
					    nonceStr: data.nonceStr, // 必填，生成签名的随机串
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
				initEdit()
				
			}
		];
	});
}).call(this);

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
		        //添加图片
		    	   scope.form.IMG_LOGO = evt.target.result;
		       }else if(index == 'i2'){
		        //更换图片
		    	   scope.form.IMG_BODAY = evt.target.result;
		       }else if(index == 'i3'){
		        	scope.form.IMG_HEAD = evt.target.result;
		        }
		        scope.$apply();
	       };
	        reader.readAsDataURL(file.files[0]);
	   }
}	
//滑到底部
function scrollBottom(){
	window.scrollTo(0, document.documentElement.clientHeight);
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




