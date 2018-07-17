/* 页面顶部button 触摸变色事件 */
// 触摸
$(document).on('ontouchstart', 'div.bom-radio', function () {
	// 背景变绿
	this.style.backgroundColor = "red";
})
// 停止触摸
$(document).on('ontouchstart', 'div.bom-radio', function () {
	// 还原白色
	this.style.backgroundColor = "white";
})
