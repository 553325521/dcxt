/* 页面顶部button 触摸变色事件 */
// 触摸
$(document).on('touchstart', 'div.bom-radio', function(e) {
	// 背景变绿
	this.style.backgroundColor = "red";
})
// 停止触摸
$(document).on('touchend', 'div.bom-radio', function(e) {
	// 还原白色
	this.style.backgroundColor = "white";
})

//得到项目的路径
var urlstr = document.location.href;
var reg = /https?:\/\/[^\/]*/g;
var httpurl = reg.exec(urlstr) + "";
var str1 = urlstr.replace(/https?:\/\//g, "");
var reg = /[^\/]*\/[^\/]*/g;
var str2 = reg.exec(str1) + "";
var appname = str2.replace(/[^\/]*\//g, "");
if (appname == "dcxt") {
	var realurl = httpurl + '/' + appname;
} else {
	var realurl = httpurl;
}
