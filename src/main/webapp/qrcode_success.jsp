<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
String path = request.getContextPath(); 
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"; 
%>

<html>
<head>
<meta charset="UTF-8">
<title>智慧云点单</title>
<base href="<%=basePath %>" />
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="keywords" content="SSO">
<meta http-equiv="description" content="SSO">
<meta http-equiv="content-type" content="text/html">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=11; IE=9; IE=8">
<script src="<%=basePath%>js/jquery/1.11.3/jquery.min.js"></script>
<!-- <script type="text/javascript" src="http://webapi.amap.com/maps?v=1.4.6&key=042e114360bc65e3dabeb450bddab0f3"></script> 
<meta name="viewport" content="initial-scale=1.0, user-scalable=no">  -->
<!-- <script type="text/javascript">
 $(function(){
	 
	 function GetQueryString(name)
	    {
	         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	         var r = window.location.search.substr(1).match(reg);
	         if(r!=null)return  unescape(r[2]); return null;
	    }
	 
	 var map = new AMap.Map('container', {
		  resizeEnable: true
		})
		map.plugin('AMap.Geolocation', function() {
		  var geolocation = new AMap.Geolocation({
		    // 是否使用高精度定位，默认：true
		    enableHighAccuracy: true,
		    // 设置定位超时时间，默认：无穷大
		    timeout: 10000,
		    // 定位按钮的停靠位置的偏移量，默认：Pixel(10, 20)
		    buttonOffset: new AMap.Pixel(10, 20),
		    //  定位成功后调整地图视野范围使定位位置及精度范围视野内可见，默认：false
		    zoomToAccuracy: true,     
		    //  定位按钮的排放位置,  RB表示右下
		    buttonPosition: 'RB'
		  })

		  geolocation.getCurrentPosition()
		  AMap.event.addListener(geolocation, 'complete', onComplete)
		  AMap.event.addListener(geolocation, 'error', onError)

		  function onComplete (data) {
		    // data是具体的定位信息
		    console.log(data);
		    $.ajax({
				 url:"http://hygl.pdedu.sh.cn/TeacherEDUPhone/json/Qrcode_checkLngLat_data.json",
				 type:"get",
				 data:{"userPk":GetQueryString("userPk"),
					   "coursePk":GetQueryString("coursePk"),
					   "Lng":data.position.lng,
					   "Lat":data.position.lat},
				 success:function(data){
					 
				 }
			 })
		  }

		  function onError (data) {
		    // 定位出错
		    alert("您已拒绝定位签到");
		  }
		})
 })
 </script> -->
</head>
<body style="background: rgb(50,53,66);">

   <div style="text-align: center;">
	      <img alt="" src="assets/img/qrcode_success.png"  style="margin-top: 70px;width:128px;height:128px;">
	      
	</div>
	
	<div style="margin-top: 50px;text-align: center;color: #fff;">
		<h4>扫码成功，您的身份已更改！</h4>
	</div>
	
</body>
</html>