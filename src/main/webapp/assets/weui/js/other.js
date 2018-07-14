/* 页面顶部button 触摸变色事件 */

var friend = document.getElementById("attend1");
// 触摸
friend.ontouchstart = function() {
	// 背景变绿
	this.style.backgroundColor = "red";
};
// 停止触摸
friend.ontouchend = function() {
	// 还原白色
	this.style.backgroundColor = "white";
};

var friend = document.getElementById("attend2");
// 触摸
friend.ontouchstart = function() {
	// 背景变绿
	this.style.backgroundColor = "red";
};
// 停止触摸
friend.ontouchend = function() {
	// 还原白色
	this.style.backgroundColor = "white";
};