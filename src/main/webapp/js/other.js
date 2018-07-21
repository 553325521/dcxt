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

function hidemark() {
	$('.weui_mask').removeClass('weui_mask_visible');
	$('.weui-custom-pop').removeClass('weui-dialog-visible');
}

var comfrimTip = function() {

	if (arguments[0] != undefined && typeof arguments[0] == 'object') {
		var defaluts = {
			title : arguments[0].title || '提示',
			content : arguments[0].content || '',
			fn : arguments[0].fn != undefined ? arguments[0].fn : 'hidemark()'
		};
	} else {
		var defaluts = {
			title : '提示',
			content : '弹窗错误！',
			fn : 'hidemark()'
		};
	}

	var html = "";
	html += "<div class='weui_mask'></div>";
	html += "<div class='weui-custom-pop'>";
	html += "<div class='weui-custom-hd'>" + defaluts.title + "</div>";
	html += "<div class='weui-custom-bd'>" + defaluts.content + "</div>";
	html += "<div class='weui-custom-ft'>";
	html += "<a href='javascript:hidemark();'>取消</a>";
	html += "<a href='javascript:;' onclick='" + defaluts.fn + "'>确定</a></div></div>";
	$('body').append(html);
	$('.weui_mask').addClass('weui_mask_visible');
	$('.weui-custom-pop').addClass('weui-dialog-visible');
}