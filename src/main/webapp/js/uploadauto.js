var UPLOADAUTO = (function() {
	
    var iniUploadauto = function(uploadobj,uploadfiletype,uploadapp,openattr,userID,url,callonComplete,index){
    	
    	uploadobj.uploadify({
            'uploader': 'js/jquery.uploadify-v2.1.0/uploadify.swf',
            'script':url,
            'scriptData': {'uploadfiletype':uploadfiletype,uploadapp:uploadapp,'openattr':openattr,'UserID':userID},
            'cancelImg': 'js/jquery.uploadify-v2.1.0/cancel.png',
            //'buttonText': '上传文件',
            //'hideButton':true,
            'buttonImg':'assets/img/upload.png',
            'fileDataName':'fileload',
            'fileExt':uploadfiletype,
            'simUploadLimit' : 1,
            'queueSizeLimit' : 1,
            'width':78,
            'auto':true,
            'multi': false,
            onComplete: callonComplete,
            onSelect:function(event,queueID,fileObj){
            	$('#uploadIndex').val(index);
            	$('#upload_tip').show();
            	
            },
            onError: function(event, queueID, fileObj) {
                alert("文件:" + fileObj.name + "上传失败");
            }
        });
    }
    
    return{
    	iniUploadauto:iniUploadauto  
    }; 
       
})();
