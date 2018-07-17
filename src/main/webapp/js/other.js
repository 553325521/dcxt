/* 页面顶部button 触摸变色事件 */
// 触摸
$(document).on('touchstart', 'div.bom-radio', function (e) {
	// 背景变绿
	this.style.backgroundColor = "red";
})
// 停止触摸
$(document).on('touchend', 'div.bom-radio', function (e) {
	// 还原白色
	this.style.backgroundColor = "white";
})
