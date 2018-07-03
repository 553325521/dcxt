(function() {
    define(['jqueryUiZh','ZeroClipboard','swfobject','uploadify','uploadauto'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$httpService.css("assets/css/uploadify.css");
            	//初始化 form 表单
            	$scope.form={};
            	//接收保存按钮事件
            	//接收保存按钮事件
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	var uploadfiletype ='.pdf,.doc,.ppt';
            	var uploadapp="jky";
            	var UserID= "";
            	var url=config.uploadurl;
            	//主要研究内容及研究进展情况
            	UE.delEditor('progress_ituation');
                var ueituation = UE.getEditor('progress_ituation');       
            	//初始化数据
                var findInfo=function(){
                	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
                		if(data.data.FILE_ID!=null){
                    		var path=data.data.FILE_PATH;
                    		var newPath="/data";
                    		var file_path=path.substring(10,1)+newPath+path.substring(9+1,path.length);
                    		file_path="http://rescenter.sjedu.cn/"+file_path+"/"+data.data.FILE_ID+"."+data.data.EXT_NAME;
            				$('#'+controllerName+' .uploadfile').show();
                            $('#'+controllerName+' .uploadfile').html(data.data.FILE_NAME+"."+data.data.EXT_NAME);
            			}
                		ueituation.ready( function() {
                			ueituation.setContent(data.data.PROGRESS_ITUATION);
                         } );
                		$scope.form.EXPLORE_DELAY_PK=data.data.EXPLORE_DELAY_PK;
                		$scope.form.DELAY_SORT=data.data.DELAY_SORT;
                		//根据变更类型显示或隐藏
                		var option=$scope.form.DELAY_SORT;
                		if(option==1){
                			$("#"+controllerName+" #break").show();
                			$("#"+controllerName+" #delay").hide();
                		}else if(option==2){	
                			$("#"+controllerName+" #delay").show();
                			$("#"+controllerName+" #break").hide();
                		}	
                		var resson=data.data.DELAY_RESSON;
                		var ressonList=new Array();
                		var ressonList= resson.split(",");
                		if(option==1){
                			for(var i=0;i<ressonList.length;i++){
                				var reason=ressonList[i];
                				if(reason=='1'){
                					$("#"+controllerName+" #break1").prop("checked",true);
                				}else if(reason=='2'){
                					$("#"+controllerName+" #break2").prop("checked",true);
                				}else if(reason=='3'){
                					$("#"+controllerName+" #break3").prop("checked",true);
                				}else if(reason=='4'){
                					$("#"+controllerName+" #break4").prop("checked",true);
                				}else{
                					$("#"+controllerName+" #break5").prop("checked",true);
                				}
                			}
                		}else{
                			for(var i=0;i<ressonList.length;i++){
                				var reason=ressonList[i];
                				if(reason=='1'){
                					$("#"+controllerName+" #delay1").prop("checked",true);
                				}else if(reason=='2'){
                					$("#"+controllerName+" #delay2").prop("checked",true);
                				}else if(reason=='3'){
                					$("#"+controllerName+" #delay3").prop("checked",true);
                				}else if(reason=='4'){
                					$("#"+controllerName+" #delay4").prop("checked",true);
                				}else{
                					
                				}
                			}
                		} 		
                		if(option==1){
                			$scope.form.DELAY_DATE='';
                		}else{
                			$scope.form.DELAY_DATE=data.data.DELAY_DATE;
                		}	
                		$scope.form.OLD_DATE=data.data.OLD_DATE;
                		$scope.form.OLD_STATUS=data.data.OLD_STATUS;
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
                		$scope.form.TEACHER_JOB=data.data.TEACHER_JOB;
                		$scope.form.TEACHER_SKILL=data.data.TEACHER_SKILL;
                		$scope.form.TEACHER_GENDER=data.data.TEACHER_GENDER;
                		$scope.$apply();
    	            });
            	}
            	
            	//变更类型
            	$scope.delayChange=function(){
            		var option=$scope.form.DELAY_SORT;
            		if(option==''){
            			$("#"+controllerName+" #break").hide();
            			$("#"+controllerName+" #delay").hide();
            		}else if(option==1){
            			$("#"+controllerName+" #break").show();
            			$("#"+controllerName+" #delay").hide();
            		}else if(option==2){	
            			$("#"+controllerName+" #delay").show();
            			$("#"+controllerName+" #break").hide();
            		}
            	}
            	
            	
                //保存延期或终止变更报告 
        		$scope.save=function(){
        			$scope.form.PROGRESS_ITUATION=ueituation.getPlainTxt();
        			if($scope.form.DELAY_SORT=='1'){
        				$scope.form.DELAY_DATE='';
        			}
        			
        			if($scope.form.DELAY_SORT=='2'){
        				if ($scope.form.DELAY_DATE == null || $scope.form.DELAY_DATE == '' ) {
                			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写延期时间。"});
                			return;
        				}	
        				
        			}
        			
        			//校验表单
            		if(!$scope.validateForm()){
            			return;
            		}
            		var values = [];
        			if($scope.form.DELAY_SORT==1){
        				$('#'+controllerName+' input[name="discontinue"]:checked').each(function(){ 
                    		values.push($(this).val());
                    	});
        			}else if($scope.form.DELAY_SORT==2){
        				$('#'+controllerName+' input[name="extension"]:checked').each(function(){ 
                    		values.push($(this).val());
                    	});
        			}
        			var resson="";
        			for(var i=0;i<values.length;i++){
        				if (i == values.length - 1) {
        					resson += values[i];
        					} else {
        						resson += values[i];
        						resson += ",";
        					}
        			}
        			$scope.form.DELAY_RESSON=resson;
        			$httpService.post(config.updateURL,$scope.form).success(function(data) {
                    	if(data.code != '0000'){
                    		loggingService.info(data.msg);
                    	}else{
                    		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"修改成功！"});
                    		eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});//发送更新事件，刷新数据
                    		$scope.goback();
                    	}
                     }).error(function(data) {
                         loggingService.info('修改延期或终止报告出错！');
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
                	
                	//初始化
                	$scope.form.DELAY_DATE='  ';
                	//时间控件样式
                    $httpService.css("http://cdn.sjedu.cn/js/jqueryUi/css/custom-theme/jquery-ui-1.9.2.custom.css");
                	//设置时间控件
                	$('#'+controllerName+' .datepicker').datepicker(
                			{	onSelect: function(dateText, inst) 
                				{
                					eval("$scope." + $(this).attr('ng-model') + "='"+$(this).val()+"'");
                                }
                			}
                	);
                	$('#'+controllerName+' .datepicker').datepicker('option', 'dateFormat','yy-mm-dd');
                	
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
