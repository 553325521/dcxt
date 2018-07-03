(function() {
    define(['ZeroClipboard','swfobject','uploadify','uploadauto'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$httpService.css("assets/css/uploadify.css");
            	//初始化 form 表单
            	$scope.form={};
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	/*var file_path="";
            	var file_id="";
            	var extname="";*/
            	var uploadfiletype ='.pdf,.docx,.ppt';
            	var uploadapp="jky";
            	var UserID= "";
            	var url=config.uploadurl;
            	//开题活动简况
            	UE.delEditor('activity_design');
                var ueactivity = UE.getEditor('activity_design');
            	//开题报告要点
            	UE.delEditor('report_point');
                var uepoint = UE.getEditor('report_point');               
                //重要变更
                UE.delEditor('important_change');
                var uechange= UE.getEditor('important_change'); 
                
                /*//在线预览
	           	 $scope.fileClick = function(){            		 
	           		if(file_id!=null&&file_id!=""&&file_id!=undefined){
	           			var m2 = {
	                  			  "url":"aps/content/EduResearch/ScienceReApply/viewswf/applyswf/config.json?FILE_ID="+file_id+"&FILE_PATH="+file_path+"&EXTNAME="+extname,
	                  		          text:"查看附件",
	                  		          size:"modal-lg",
	                  		          "contentName":"modal"
	                  		}   
	                  		eventBusService.publish(controllerName,'appPart.load.modal', m2);
	           		}
                	  	
	           	 }
	           	 //下载
	           	$scope.downLoadClick = function(){
	           		if(file_id!=null&&file_id!=""&&file_id!=undefined){
	           			var url = "http://rescenter.sjedu.cn/ResCenter/data";
	                    	var str  = file_path.split("/");
	                    	var path = "";
	                    	for(var i=2;i<str.length;i++){
	                    		path = path + "/" + str[i];
	                    	}
	                    	url = url + path + "/"+file_id+"." + extname;
	                    	console.log("url:"+url);
	                    	window.open(url);
	                    	funDownload(url,file_id+"."+extname);
	           		}
	           		
	              }
	           	 //下载链接地址
	           	var funDownload = function (content, filename) {
	                   // 创建隐藏的可下载链接
	                   var eleLink = document.createElement('a');
	                   eleLink.download = filename;
	                   eleLink.style.display = 'none';
	                   // 字符内容转变成blob地址
	                   var blob = new Blob([content]);
	                   eleLink.href = URL.createObjectURL(blob);
	                   // 触发点击
	                   document.body.appendChild(eleLink);
	                   eleLink.click();
	                   // 然后移除
	                   document.body.removeChild(eleLink);
	               };*/
                   
            	//初始化数据
                var findInfo=function(){
                	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
                		if(data.data.FILE_ID!=null){
                			/*file_path=data.data.FILE_PATH;
            				extname=data.data.EXT_NAME;
            				file_id=data.data.FILE_ID;*/
            				$('#'+controllerName+' .uploadfile').show();
                            $('#'+controllerName+' .uploadfile').html(data.data.FILE_NAME+"."+data.data.EXT_NAME);
            			}
                		ueactivity.ready( function() {
                			ueactivity.setContent(data.data.ACTIVITY_DESIGN);
                         } );
                		uepoint.ready( function() {
                			 uepoint.setContent(data.data.REPORT_POINT);
                          } );
                		 uechange.ready( function() {
                			 uechange.setContent(data.data.REPORT_IMPORTANT_CHANGE);
                          } );
                		$scope.form.EXPLORE_REPORT_PK=data.data.EXPLORE_REPORT_PK;
                		$scope.$apply();
                		projectInfo();
    	            });
                }
            	//申请书详情
            	var projectInfo=function(){
            		$httpService.post(config.findProjectInfoURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            			$scope.form.APPLY_CODE=data.data.APPLY_CODE;
            			$scope.form.PROJECT_TITLE=data.data.PROJECT_TITLE;
                		$scope.form.PROJECT_TYPE=data.data.PROJECT_TYPE;
                		$scope.form.PROJECT_LEVEL=data.data.PROJECT_LEVEL;
                		$scope.form.TEACHER_NAME=data.data.TEACHER_NAME;
                		$scope.form.TEACHER_SCHOOL_NAME=data.data.TEACHER_SCHOOL_NAME;
                		$scope.form.PRICE=data.data.PRICE;
                		$scope.form.APPLY_DATE=data.data.APPLY_DATE;
                		$scope.form.FINISH_DATE=data.data.FINISH_DATE;
                		$scope.form.EXPECT_RESULT=data.data.EXPECT_RESULT;
                		$scope.$apply();
    	            });
            	}
            	
            	//修改
            	$scope.save=function(){
            		$scope.form.ACTIVITY_DESIGN=ueactivity.getPlainTxt();
                    $scope.form.REPORT_POINT=uepoint.getPlainTxt();
                    $scope.form.REPORT_IMPORTANT_CHANGE=uechange.getPlainTxt();
            		$httpService.post(config.updateURL,$scope.form).success(function(data) {
                    	if(data.code != '0000'){
                    		loggingService.info(data.msg);
                    	}else{
                    		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"修改成功！"});
                    		eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});//发送更新事件，刷新数据
                    		$scope.goback();
                    	}
                     }).error(function(data) {
                         loggingService.info('修改开题报告出错！');
                     });

        		}
            	//附件上传（回调函数）
            	var callonComplete = function(event, queueID, fileObj, response, dataObj) {
              	    //转换为json对象
                	console.log(response);
                	var data = eval("("+response+")");
                	if(data.code == "4444"){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"不支持此类型文件的上传!"});
                		return;
                	}
                   if(data.code == "0000"){
                	   $scope.form.RES_FILE_LINK_PK=data.data.RES_FILE_LINK_PK;
                	   $scope.form.FILE_NAME=data.data.ORI_FILENAME;
                	   $scope.form.EXT_NAME=data.data.EXTNAME;
                	   $scope.form.FILE_PATH = data.data.FILE_PATH;
                	   $scope.form.FILE_ID=data.data.FILE_ID;
                	   $('#'+controllerName+' .uploadfile').show();
                       $('#'+controllerName+' .uploadresult').show();
                       $('#'+controllerName+' .uploadfile').html(data.data.ORI_FILENAME+"."+data.data.EXTNAME);
                   }
                    
                };
            	//初始化数据
            	var init = function(){
            		findInfo();
            		UPLOADAUTO.iniUploadauto($('#uploadifyfile'),uploadfiletype,uploadapp,"0",UserID,url,callonComplete);
            	}
            	init();
        		
            	//返回上一页
            	$scope.goback = function() { 
	         		var menu = {
	         		    "CONTROLLER_NAME": "ScienceReApply", 
	         		    "CREATE_BY": "SJAAAAX44858", 
	         		    "CREATE_TIME": 1508169600000, 
	         		    "MENU_CODE": "0000710104", 
	         		    "MENU_FATHER_PK": "a381053e77244085bdf5ead406cdb1b1", 
	         		    "MENU_IMG": "file", 
	         		    "MENU_LINK": "aps/content/EduResearch/ScienceReApply/list/config.json", 
	         		    "MENU_NAME": "区块管理", 
	         		    "MENU_PK": "58330ff069634d38829d41b25c9a4362", 
	         		    "MENU_STATUS": "0", 
	         		    "MENU_TYPE": "0"
	         		}
	         		var changeControllerData = {
		                  url:menu.MENU_LINK,
		                  contentName:"content",
		                  hasButton:"right",
		                  data:menu
		                }
	      	        return eventBusService.publish(controllerName,'appPart.load.content', changeControllerData);
	    		}
            	
            }
        ];
    });
}).call(this);
