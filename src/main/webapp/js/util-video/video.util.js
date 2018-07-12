/**
 * 
 */
var VIDEO = (function() {
	
	var agreement = "rtmp";
	var ip = "livev.sjedu.cn";
	var port = "1935";
	var application = "dev";
	var filePath = "ResourceData/MP4/2011/11/16/076a9865941a17b6b2f6fa0a0f2c0d85.mp4";
	
	//判断是否Safari浏览器
	var initParam = function (serverIp,serverPort,serverApplication){
	 	ip = serverIp;
	 	port = serverPort;
	 	application = serverApplication;
	}
	
	function myBrowser(){
    	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	    var isOpera = userAgent.indexOf("Opera") > -1;
	    if (isOpera) {
	        return "Opera"
	    }; //判断是否Opera浏览器
	    if (userAgent.indexOf("Firefox") > -1) {
	        return "FF";
	    } //判断是否Firefox浏览器
	    if (userAgent.indexOf("Chrome") > -1){
		  return "Chrome";
		 }
	    if (userAgent.indexOf("Safari") > -1) {
	        return "Safari";
	    } //判断是否Safari浏览器
	    if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera) {
	        return "IE";
	    }; //判断是否IE浏览器
	}
	
	//判断是否Safari浏览器
	var isApple = function (){
		var mb = myBrowser();
		if ("Safari" == mb) {
			 return true;
		}else{
			return false;
		}
	}
	
	//判断是否Android
	var isAndroid = function (){
	    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	    var isAndroid = userAgent.indexOf('Android') > -1 || userAgent.indexOf('Adr') > -1; //android终端
	    if (isAndroid) {
	        return true;
	    }else{
	    	return false;
	    }
	}
	
	//返回视频播放的地址
	//传入文件的路径
	var returnURL = function (filePath){
		if(isApple()){
			agreement = "http";
			filePath = "mp4:"+filePath+"/playlist.m3u8";
		}else if(isAndroid()){
			agreement = "rtsp";
		}else{
			filePath = "mp4:"+filePath;
		}
		return agreement + "://" + ip + ":" + port + "/" + application + "/" + filePath;
	}
	
	return{  
		initParam:initParam,
		returnURL:returnURL
	}; 

})();
