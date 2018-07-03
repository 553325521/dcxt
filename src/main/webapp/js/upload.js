var UPLOAD = (function() {
	
    var iniUpload = function(uploadobj,uploadfiletype,uploadapp,openattr,userID,url,callonComplete){
    	
    	uploadobj.uploadify({
            'uploader': 'js/jquery.uploadify-v2.1.4/uploadify.swf',
            'script':url,
            'scriptData': {'uploadfiletype':uploadfiletype,uploadapp:uploadapp,'openattr':openattr,'UserID':userID},
            'cancelImg': 'js/jquery.uploadify-v2.1.4/cancel.png',
            'buttonText': '上传',
            'fileDataName':'fileload',
            'simUploadLimit' : 1,
            'queueSizeLimit' : 1,
            'auto':true,
            'multi': false,
            onComplete: function(event, queueID, fileObj, response, dataObj){
            	//uploadobj.uploadifySettings('buttonText','完成');
            	uploadobj.next().hide();
            	var data = eval("("+response+")");
            	uploadobj.parent().append(data.data.ORI_FILENAME + "." + data.data.EXTNAME);
            	return callonComplete(event, queueID, fileObj, data, dataObj);
            },
            onSelect:function(){
            	uploadobj.uploadifySettings('buttonText','正在处理..');
            },
            onError: function(event, queueID, fileObj) {
                alert("文件:" + fileObj.name + "上传失败");
            }
        });
    }
    
    return{
    	iniUpload:iniUpload  
    }; 
       
})();
