
		$('.switch_stop').click(function () {
	    	
	      	
	    })

	  	$('.switch_start').click(function () {
	      	
	      	
	    })

	 


	  
      
 $(document).on("click", "#delete-table-cate", function() {
 	var that = this
        $.confirm("您确定要删除吗?", "确认删除?", function() {
          $(that).parents(".weui_cell").remove();
          $.toast("删除成功!");
        }, function() {
          //取消操作
        });
  });




	 
      
   $(document).on("click", "#delete-table-list", function() {
 	var that = this
        $.confirm("您确定要删除吗?", "确认删除?", function() {
          $(that).parents(".weui_cell").remove();
          $.toast("删除成功!");
        }, function() {
          //取消操作
        });
  });

      