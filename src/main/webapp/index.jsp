<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.util.Map"%>
<%@page import="cn.wifiedu.core.util.HttpsRequestUtil"%>
<%@page import="cn.wifiedu.core.vo.ResultVo"%>
<%@page import="cn.wifiedu.core.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE html>
<html lang="zh-CN" class="ios hairline" style="font-size: 42.75px;">
<head>
<meta charset="utf-8">
<meta content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0" name="viewport">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta content="black" name="apple-mobile-web-app-status-bar-style">
<meta content="telephone=no" name="format-detection">
<title>点餐系统</title>
<base href="<%=basePath%>" />
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
<link rel="stylesheet" href="js/ydui/css/ydui.css">
<script src="js/ydui/js/jquery.min.js"> </script>
<script src="js/ydui/js/ydui.js"> </script>

</head>
<body id="appBody" ng-app="app" >
	<div class="ng-view">
		<div
			style="text-align: center;margin-left: auto;margin-right: auto;margin-top: 200px;">
			<img alt="" src="https://cdn.sjedu.cn/img/loading_1.gif"><br />
			如果您等待时间过长，<a href="javascript:window.location.reload()">请点击这里</a>
		</div>
	</div>
</body>

<script type="text/javascript" src="<%=basePath%>/js/angular/1.2.0/angular.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/angular/1.2.0/angular-route.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/util.js?v=5.1"></script>
<script data-main="config/loader.js?v=7" src="<%=basePath%>/js/require/require.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/config/app.js"></script>

</html>