<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.util.Map"%>
<%@page import="cn.wifiedu.core.util.HttpsRequestUtil"%>
<%@page import="cn.wifiedu.core.vo.ResultVo"%>
<%@page import="cn.wifiedu.core.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%

StringBuffer param = new StringBuffer(PropertiesUtil.get("APP_USER_AUTH_WEB_URL"));
StringBuffer redirect_uri = request.getRequestURL();
param.append("?redirect_uri=").append(java.net.URLEncoder.encode(redirect_uri.toString().replace("logout.jsp", ""),"UTF-8"));
param.append("&state=").append(session.getId());
param.append("&appid=").append(PropertiesUtil.get("APP_SSL_APPID"));
param.append("&scope=").append("web_logout");
session.invalidate();
response.sendRedirect(param.toString());

%>
