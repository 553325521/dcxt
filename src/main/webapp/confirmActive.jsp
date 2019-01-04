<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>激活会员</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<link rel="stylesheet" href="<%=basePath%>assets/weui/css/weuix.min.css">
<link rel="stylesheet" href="<%=basePath%>assets/weui/css/weui.css">
<link rel="stylesheet" href="<%=basePath%>assets/weui/css/weui2.css">
<link rel="stylesheet" href="<%=basePath%>assets/weui/css/weui3.css">
<%-- <link rel="stylesheet" href="<%=basePath%>assets/weui/css/icon.css">
	<link rel="stylesheet"
		href="<%=basePath%>assets/weui/css/jquery-weui.min.css"> --%>
</head>
<style>
.font-38 {
	font-size: 38px
}

.border-lf {
	border-top: 1px solid gray;
	border-bottom: 1px solid gray;
}
</style>
<body>
	<div class="weui_cells_title font-38" style="margin-top: 100px;">请确定一下信息</div>
	<div class="weui_cells weui_cells_form font-38 border-lf">
		<div class="weui_cell" style="width: 100%;height: 100px;">
			<div class="weui_cell_bd"
				style="color:lightgray;width: 16%;margin-left: 5%;">手机号</div>
			<div class="weui_cell_bd weui_cell_primary"><%=request.getAttribute("userPhone") %></div>
		</div>
	</div>
	<div class="weui_btn_area">
		<a class="weui_btn weui_btn_large weui_btn_primary" href="javascript:" style="font-size:28px;line-height:3.3333333" id="actice">激活</a>
	</div>
</body>
<script src="<%=basePath%>js/jquery/1.11.3/jquery.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>js/weixin/jweixin-1.2.0.js"></script>
<script type="text/javascript">
	 $(function(){
		 var jmCode = "<%=request.getAttribute("jmCode") %>";
		 var userPhone = "<%=request.getAttribute("userPhone") %>";
		 var card_id = "<%=request.getAttribute("card_id") %>";
		 $("#actice").click(function(){
			 $.post("https://m.ddera.com/json/activeVIPCard.json",{"jmCode":jmCode,"userPhone":userPhone,"card_id":card_id},function(data){
				 if(data.code == '0000'){
					 wx.closeWindow();
				 }
			 },"json");
		 })
	 })
</script>
</html>
