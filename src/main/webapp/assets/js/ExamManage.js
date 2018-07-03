

function initJavaScriptMenuOne() {
	$(".menuone").click(function(){
		$(".menuone").each(function(){
			$(this).removeClass("active");
		})
		$(this).addClass("active");
		
	/*	$(this).hide(500);
		$(this).show(500);*/
	});
};




function initJavaScript() {
	$(".menutwo").click(function(){
		$(".menutwo").each(function(){
			$(this).parent().removeClass("active");
		})
		$(this).parent("li").addClass("active");
		
		/*$(this).hide(500);
		$(this).show(500);*/
	});
};



