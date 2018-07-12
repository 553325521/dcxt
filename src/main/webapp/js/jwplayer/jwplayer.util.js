/**
 * 
 */

/**
 * 
 */
var PLAYER = (function() {
	
	var thePlayer;  //保存当前播放器以便操作
	var thePlayerId = "jwplayerDiv"; //播放器ID
	var flashplayerpath = "http://cdn.sjedu.cn/js/jwplayer/jwplayer.flash.swf"; //jwplayer播放器所在路径
	var defaultAspectratio = "16:9"; //默认比例
	var playerWidth = 640; //播放器宽度
	
	
	//播放器宽度
	var playerTitle = "视频播放"; 
	
	//播放视频路径
	var playpath = "";
	
	//让层居中显示
	var center = function(obj) {
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;

		var popupHeight = $(obj).height();
		var popupWidth = $(obj).width();

		$(obj).css({
			"top" : (windowHeight - popupHeight ) / 2
					+ $(document).scrollTop() ,
			"left" : (windowWidth - popupWidth) / 2
		});
	}
	
	//让层居中隐藏
	var closeDiv = function(obj) {
		$(obj).hide();
		$(window).unbind();
	}
	
	//修改播放比例
	var updateshowbj = function(bl){
		thePlayer = jwplayer('container').setup({
   	        flashplayer: flashplayerpath,
   	        file: playpath,
   	        width: '100%',
   	        aspectratio:bl,//自适应宽高比例，如果设置宽高比，可设置宽度100%,高度不用设置
   	        autostart:true,
   	        dock: false
   	    });
		
	}
	
	//显示层并设置窗口变化事件
	var showDiv = function(obj) {
		$(obj).show().css({
			"zIndex" : "222",
			"position" : "absolute"
		});
		center(obj);
		$(window).scroll(function() {
			center(obj);
		});
		$(window).resize(function() {
			center(obj);
		});
	}
	
	//打开
	var startPlay = function(title,path) {
		playerTitle = title; 
		playpath = path;
		
		
		var bljz = playerWidth/2 - 100;
		
		if($("#"+thePlayerId).length==0){
			var title = "<span style='float:left;'><span class='glyphicon glyphicon-facetime-video'></span> "+playerTitle+"</span>";
			var bl = "<span style='float:right;'><a class='badge' style='padding: 2px 8px;line-height: 14px;' href='javascript:PLAYER.setAspectratio(\"4:3\")'>4 : 3</a>  <a class='badge' style='padding: 2px 8px;line-height: 14px;' href='javascript:PLAYER.setAspectratio(\"16:9\")'>16 : 9</a>";
			bl = bl + " <a href='javascript:PLAYER.close()' style='margin-left:"+bljz+"px;'>关闭</a></span>";
			$("body").append("<div id='"+thePlayerId+"' style='width:"+playerWidth+"px;height:auto;display:none;'><div class='panel panel-info' style='' ><div class='panel-heading' style='border-color:#d9edf7;padding-left: 10px;height: 25px;padding-top: 5px;' >"+title+bl+"</div> <div class='panel-body' style='padding: 5px;padding-bottom: 0px;background: #d9edf7;'><div id='container'>Loading the player...</div></div></div></div>");
		}

		
   	    thePlayer = jwplayer('container').setup({
   	        flashplayer: flashplayerpath,
   	        file: playpath,
   	        width: '100%',
   	        aspectratio:defaultAspectratio,//自适应宽高比例，如果设置宽高比，可设置宽度100%,高度不用设置
   	        autostart:true,
   	        dock: false
   	    });
   	    

   	 	thePlayer.onPlay(function(){
   	 		showDiv(document.getElementById(thePlayerId));
	   	 	$('#container').css({
				"background-color" : "transparent"
			});
	    })
		
	}
	
	//关闭
	var closeDivq = function() {
		thePlayer.stop();
		closeDiv(document.getElementById(thePlayerId));
	}
	
	
	
	//返回视频播放的地址
	//传入文件的路径
	var initPlayer = function (params){
		
		 thePlayerId = params.thePlayerId; //播放器ID
		 defaultAspectratio = params.defaultAspectratio; //默认比例
		 playerWidth = params.playerWidth; //播放器宽度
		
		return thePlayer;
	}
	
	return{  
		initPlayer:initPlayer,
		startPlay:startPlay,
		close:closeDivq,
		setAspectratio:updateshowbj
	}; 

})();
