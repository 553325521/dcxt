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
            	//研究工作进展情况
            	UE.delEditor('work_progress');
                var ueprogress = UE.getEditor('work_progress'); 
            	//1－2项代表性成果简介
            	UE.delEditor('represent_results');
                var ueresults = UE.getEditor('represent_results');              
            	//初始化数据
                var findInfo=function(){
                	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
                		console.log(data.data);
                		$scope.form=data.data;
                		$scope.$apply();
                		findResultList();
    	            });
                }
            	//预期研究成果
            	var FK_ResultList=new Array();
            	var showResultList=new Array();
            	$scope.newResultList;
            	var findResultList=function(){
            		$httpService.post(config.findResultListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			for(var i=0;i<data.data.length;i++){	
            				var result={
            						"MEMBER_EXPECTING_PK":data.data[i].MEMBER_EXPECTING_PK,
                    				"TEACHER_ID":data.data[i].TEACHER_ID,
                    				"PEOPLE":data.data[i].TEACHER_NAME,
                    				"STAGE_START_TIME":data.data[i].STAGE_START_TIME,
                    				"STAGE_END_TIME":data.data[i].STAGE_END_TIME,
                    				"RESULT_FORM":data.data[i].RESULT_FORM,
                    				"RESULT_STAGE_NAME":data.data[i].RESULT_STAGE_NAME
                    				};
            				FK_ResultList.push(result);
            				showResultList.push(result); 	
            			}
            			$scope.newResultList=showResultList;
            			console.log($scope.newResultList);
                		$scope.$apply();	
    	            });
            	}
            	
            	//临时存放中期报告研究成果
            	var FK_AchieveList=new Array();
            	var showAchieveList=new Array();
            	$scope.newAchieveList;
            	$scope.addAchieve=function(){
            		
            		if ($scope.form.STAGE_NAME == null || $scope.STAGE_NAME == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写成果名称。"});
            			return;
    				}
            		
            		if ($scope.form.STAGE_TYPE == null || $scope.form.STAGE_TYPE == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写成果形式。"});
            			return;
    				}
            		
            		if ($scope.form.STAGE_AUTHER == null || $scope.form.STAGE_AUTHER == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写作者。"});
            			return;
    				}
            		
            		if ($scope.form.STAGE_DEP == null || $scope.form.STAGE_DEP == '' ) {
            			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写刊物年期、出版社和出版日期、使用单位。"});
            			return;
    				}
            		var achieve={
            				"STAGE_NAME":$scope.form.STAGE_NAME,
            				"STAGE_TYPE":$scope.form.STAGE_TYPE,
            				"STAGE_AUTHER":$scope.form.STAGE_AUTHER,
            				"STAGE_DEP":$scope.form.STAGE_DEP,
            				};	
            		showAchieveList.push(achieve);
            		$scope.form.STAGE_NAME="";
            		$scope.form.STAGE_TYPE="";		
            		$scope.form.STAGE_AUTHER="";
            		$scope.form.STAGE_DEP="";
            		$scope.newAchieveList=showAchieveList;
            		FK_AchieveList=showAchieveList;
            	}
            	
            	//移除预期研究成果
            	$scope.removeAchieve=function(achieveVo){
            		$scope.newAchieveList.remove(achieveVo);
            	}

            	//保存
        		$scope.save=function(){	
        			//校验表单
            		if(!$scope.validateForm()){
            			return;
            		}
        			$scope.form.FK_AchieveList=JSON.stringify(FK_AchieveList);
        			$scope.form.WORK_PROGRESS=ueprogress.getPlainTxt();
                    $scope.form.REPRESENT_RESULTS=ueresults.getPlainTxt();
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
        		//初始化表单校验
            	VALIDATE.iniValidate($scope);
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
