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
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="renderer" content="webkit">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">

<title>教科研平台</title>
<base href="<%=basePath%>" />
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
<link
	href="https://cdn.sjedu.cn/js/bootstrap/3.3.4/css/bootstrap.min.css"
	rel="stylesheet">
<link href="<%=basePath%>/assets/css/style.css" rel="stylesheet">

</head>
<body id="appBody" ng-app="app">
	<div class="ng-view">
		<div
			style="text-align: center;margin-left: auto;margin-right: auto;margin-top: 200px;">
			<img alt="" src="https://cdn.sjedu.cn/img/loading_1.gif"><br />
			如果您等待时间过长，<a href="javascript:window.location.reload()">请点击这里</a>
		</div>
	</div>
</body>

<script type="text/javascript"
	src="<%=basePath%>/js/jquery/1.11.3/jquery.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>/js/angular/1.2.0/angular.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>/js/angular/1.2.0/angular-route.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/util.js?v=5.1"></script>
<script type="text/javascript"
	src="<%=basePath%>/js/jquery.uploadify-v2.1.4/swfobject.js"></script>
<script type="text/javascript"
	src="<%=basePath%>/js/jquery.uploadify-v2.1.4/jquery.uploadify.v2.1.4.min.js"></script>
<script data-main="config/loader.js?v=7"
	src="<%=basePath%>/js/require/require.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/config/app.js"></script>

</html>