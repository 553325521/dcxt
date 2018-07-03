(function() {
    define(['ZeroClipboard','swfobject','uploadify','uploadauto'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$httpService.css("assets/css/uploadify.css");
            	//初始化 form 表单
            	$scope.form={};
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	var uploadfiletype ='.pdf,.doc,.ppt';
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
            	//初始化数据
                var findInfo=function(){
                	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
                		console.log(data.data);
                		$scope.form=data.data;
                		$scope.$apply();
    	            });
                }

            	//保存
            	$scope.form.SCHOOL_CHECK_RESULT='0';
            	$scope.form.AREA_CHECK_RESULT='0';
        		$scope.save=function(){
        			/*//校验表单
            		if(!$scope.validateForm()){
            			return;
            		}*/
        			$scope.form.ACTIVITY_DESIGN=ueactivity.getPlainTxt();
                    $scope.form.REPORT_POINT=uepoint.getPlainTxt();
                    $scope.form.REPORT_IMPORTANT_CHANGE=uechange.getPlainTxt();
            		$httpService.post(config.addURL,$scope.form).success(function(data) {
                    	if(data.code != '0000'){
                    		loggingService.info(data.msg);
                    	}else{
                    		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"保存成功！"});
                    		eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});//发送更新事件，刷新数据
                    		$scope.goback();
                    	}
                     }).error(function(data) {
                         loggingService.info('保存开题报告出错！');
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
