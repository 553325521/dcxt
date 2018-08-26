

//得到项目的路径
var urlstr = document.location.href;
var reg = /https?:\/\/[^\/]*/g;
var httpurl = reg.exec(urlstr) + "";
var str1 = urlstr.replace(/https?:\/\//g, "");
var reg = /[^\/]*\/[^\/]*/g;
var str2 = reg.exec(str1) + "";
var appname = str2.replace(/[^\/]*\//g, "");
if (appname == "dcxt" || appname == "weixin") {
	var realurl = httpurl + '/' + appname;
} else {
	var realurl = httpurl;
}
