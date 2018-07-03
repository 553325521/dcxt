<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.util.Map"%>
<%@page import="cn.wifiedu.core.util.HttpsRequestUtil"%>
<%@page import="cn.wifiedu.core.vo.ResultVo"%>
<%@page import="cn.wifiedu.core.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 
<%
Logger logger = Logger.getLogger("index.jsp");

Object userIno = session.getAttribute("userInfo");
if(userIno == null){
	StringBuffer param = new StringBuffer(PropertiesUtil.get("APP_USER_AUTH_WEB_URL"));
	StringBuffer redirect_uri = null;

	if(null != session.getAttribute("access_redirect_uri")){
		redirect_uri = (StringBuffer)session.getAttribute("access_redirect_uri");
	}else{
		redirect_uri = request.getRequestURL();
	}
	
	if(null != request.getQueryString()){
		redirect_uri.append("?"+request.getQueryString());
	}

	param.append("?redirect_uri=").append(java.net.URLEncoder.encode(redirect_uri.toString(),"UTF-8"));


	param.append("&state=").append(session.getId());
	param.append("&appid=").append(PropertiesUtil.get("APP_SSL_APPID"));
	param.append("&scope=").append("web_login");
	
	String code = request.getParameter("code");
	if(code == null){
		response.sendRedirect(param.toString());
	}else{
        String httpsUrl = PropertiesUtil.get("APP_USER_AUTH_API_URL")+"accessToken"; 
        String xmlStr = "appid="+PropertiesUtil.get("APP_SSL_APPID")+"&secret="+PropertiesUtil.get("APP_SSL_APPSECRET")+"&grant_type=authorization_code&code="+code;  
        ResultVo result = HttpsRequestUtil.post(httpsUrl, xmlStr);  
        if(null == result || null == result.getData()){
        	response.sendRedirect(param.toString());
      	}else if("0000".endsWith(result.getCode())){
        	Map<?, ?> TokenVo = (Map<?, ?>) result.getData();
        	Object access_token = TokenVo.get("access_token");
        	Object openid = TokenVo.get("openid");
        	
        	session.setAttribute("token", access_token);
        	session.setAttribute("openid", openid);
        	logger.info("access_token : " + access_token);
        	
        	httpsUrl = PropertiesUtil.get("APP_USER_AUTH_API_URL")+"userInfo";  
	        xmlStr = "access_token="+access_token+"&openid="+openid;  
	        session.setAttribute("userInfo", HttpsRequestUtil.post(httpsUrl, xmlStr).getData());
	        System.out.println(HttpsRequestUtil.post(httpsUrl, xmlStr).getData());
        }else{
        	logger.info(result.getCode() + " : " + result.getMsg());
        	response.sendRedirect(param.toString());
        }
		 
	}
}
%>

<%
	String path = request.getContextPath(); 
	String basePath = request.getScheme()+"://"+"cloudapps.sjedu.cn"+":"+request.getServerPort()+path+"/"; 
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
    <base href="<%=basePath %>" />
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
    <link href="https://cdn.sjedu.cn/js/bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet">
	<link href="<%=basePath %>/assets/css/style.css" rel="stylesheet">
	
	
	
	
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://cdn.sjedu.cn/js/html5/html5shiv.js"></script>
      <script src="https://cdn.sjedu.cn/js/html5/respond.min.js"></script>
    <![endif]-->
    
    <!--[if lt IE 8]>
	<script type="text/javascript" src="https://cdn.sjedu.cn/js/json3/json3.min.js"></script>
	<![endif]-->
  </head>
  <body id="appBody" ng-app="app" >
    <div class="ng-view" >
    	<div style="text-align: center;margin-left: auto;margin-right: auto;margin-top: 200px;">
    	<img alt="" src="https://cdn.sjedu.cn/img/loading_1.gif"><br/>
    	如果您等待时间过长，<a href="javascript:window.location.reload()" >请点击这里</a>
    	</div>
    </div>
  </body>
  
  <script type="text/javascript" src="https://cdn.sjedu.cn/js/jquery/1.11.3/jquery.min.js"></script> 
  <!-- <script type="text/javascript" src="https://cdn.sjedu.cn/js/bootstrap/3.3.4/js/bootstrap.min.js"></script> -->
  <script type="text/javascript" src="https://cdn.sjedu.cn/js/angular/1.2.0/angular.min.js"></script>
  <script type="text/javascript" src="https://cdn.sjedu.cn/js/angular/1.2.0/angular-route.min.js"></script>
  <script type="text/javascript" src="https://cdn.sjedu.cn/js/util.js?v=5.1"></script>
  <script type="text/javascript" src="<%=basePath %>/js/jquery.uploadify-v2.1.4/swfobject.js"></script>
  <script type="text/javascript" src="<%=basePath %>/js/jquery.uploadify-v2.1.4/jquery.uploadify.v2.1.4.min.js"></script>
  <script data-main="config/loader.js?v=7" src="https://cdn.sjedu.cn/js/require/require.min.js"></script>
  <script type="text/javascript" src="<%=basePath %>/js/ueditor/ueditor.config.js"></script>
  <script type="text/javascript" src="<%=basePath %>/js/ueditor/ueditor.all.js"></script>
  <script type="text/javascript" src="<%=basePath %>/js/ueditor/lang/zh-cn/zh-cn.js"></script>
  <script type="text/javascript" src="<%=basePath%>/config/app.js"></script>
  
</html>