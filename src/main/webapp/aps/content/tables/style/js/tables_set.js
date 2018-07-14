
		$('.switch_stop').click(function () {
	    	
	      	
	    })

	  	$('.switch_start').click(function () {
	      	
	      	
	    })

	 



	$('.dels').click(function(){
 			var that = this;
	        $.confirm("您确定要删除 1 大厅 (5桌) 吗？", "确认删除?", function() {
	        	 $(that).parents('.weui_cell').remove()
	            $.toast("删除成功!");
	        }, function() {
	          
	        });
});
	  
	  
      


	 
      
      
      