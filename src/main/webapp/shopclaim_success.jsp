<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
<title>添加商户成功</title>
<link rel="stylesheet" href="<%=basePath%>assets/weui/css/weui.css">
<link rel="stylesheet" href="<%=basePath%>assets/weui/css/weui2.css">
<link rel="stylesheet" href="<%=basePath%>assets/weui/css/weui3.css">
<script src="<%=path%>/js/weui/zepto.min.js"></script>
</head>
<body ontouchstart style="background-color: #f8f8f8;">

<div class="weui_msg" id="msg1">
        <div class="weui_icon_area"><i class="weui_icon_success weui_icon_msg"></i></div>
        <div class="weui_text_area">
            <h2 class="weui_msg_title">认领成功</h2>
            <p class="weui_msg_desc">点击返回按钮，返回首页</p>
        </div>
        <div class="weui_opr_area">
            <p class="weui_btn_area">
                <a href="index.jsp" class="weui_btn weui_btn_primary">返回首页</a>
            </p>
        </div>
        <div class="weui_extra_area">
        </div>
</div>

</body>
</html>