(function() {
    define(['jqueryUiZh','ZeroClipboard','swfobject','uploadify','uploadauto'], function() {
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
            	//研究工作概况
            	UE.delEditor('work_describe');
                var uedescribe = UE.getEditor('work_describe');
            	//结题报告
            	UE.delEditor('final_report');
                var uereport = UE.getEditor('final_report'); 
               
              /*//在线预览
             	 $scope.fileClick = function(){            		 
             		if(file_id!=null&&file_id!=""&&file_id!=undefined){
             			var m2 = {
                    			  "url":"aps/content/EduResearch/ScienceReApply/viewswf/reportswf/config.json?FILE_ID="+file_id+"&FILE_PATH="+file_path+"&EXTNAME="+extname,
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
                 };
               */
                
            	//初始化数据
                var findInfo=function(){
                	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
                		if(data.data.FILE_ID!=null){
                			file_path=data.data.FILE_PATH;
            				extname=data.data.EXT_NAME;
            				file_id=data.data.FILE_ID;
            				$('#'+controllerName+' .uploadfile').show();
                            $('#'+controllerName+' .uploadfile').html(data.data.FILE_NAME+"."+data.data.EXT_NAME);
            			}
                		uedescribe.ready( function() {
                			uedescribe.setContent(data.data.WORK_DESCRIBE);
                         } );
                		uereport.ready( function() {
                			uereport.setContent(data.data.FINAL_REPORT);
                          } );
                		$scope.form.USER_WORK=data.data.USER_WORK;
                		$scope.form.PUBLISH_TITLE=data.data.PUBLISH_TITLE;
                		$scope.form.PUBLISH_SORT=data.data.PUBLISH_SORT;
                		$scope.form.PERIODICALS_FLAG=data.data.PERIODICALS_FLAG;
                		//根据是否发表来显示
                		var option=$scope.form.PERIODICALS_FLAG;
            			if(option==1){
            				$("#"+controllerName+" #publish").show();
            				$("#"+controllerName+" #appear").show();
            			}else{
            				$("#"+controllerName+" #publish").hide();
            				$("#"+controllerName+" #appear").hide();
            			}
                		$scope.form.PERIODICALS_NAME=data.data.PERIODICALS_NAME;
                		$scope.form.PERIODICALS_DATE=data.data.PERIODICALS_DATE;
                		$scope.form.PERIODICALS_NUMBER=data.data.PERIODICALS_NUMBER;
                		$scope.form.EXPLORE_ACHIEVE_PK=data.data.EXPLORE_ACHIEVE_PK;
                		$scope.$apply();
                		projectInfo();
                		findMemberList();
                		findFileList();
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
            	
            	
            	$scope.newTeacherList;
            	//临时存放添加成员的数据
            	var FK_TeacherList=new Array();
            	var showTeacherList=new Array();
            	var findMemberList=function(){
            		$httpService.post(config.findMemberListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			for(var i=0;i<data.data.length;i++){
            				var mumber={
            						"MEMBER_PK":data.data[i].MEMBER_PK,
            						"SCHOOL_PK":data.data[i].FK_TEACHER_UNIT,
                    				"SCHOOL_NAME":data.data[i].UNIT_NAME,
                    				"TEACHER_PK":data.data[i].FK_TEACHER,
                    				"TEACHER_NAME":data.data[i].TEACHER_NAME,
                    				"TEACHER_WORK":data.data[i].TEACHER_WORK,
                    				"TEACHER_GENDER":data.data[i].GENDER,
                    				"JOB_TITLE":data.data[i].JOB_TITLE,
                    				"SPECIALTY":data.data[i].SPECIALTY
                    				};
            				showTeacherList.push(mumber);
            			}
            			$scope.newTeacherList=showTeacherList;
            			FK_TeacherList=showTeacherList;
            			$scope.$apply();	
            			for(var i=0;i<data.data.length;i++){
            				var work=data.data[i].TEACHER_WORK;
            				$("#"+controllerName+" .work"+data.data[i].MEMBER_PK).val(work);	
            			}
    	            });
            		
            	}
            	//已经有的文件
	           $scope.newFileList;
	           var FK_FileList=new Array();
	           var showFileList=new Array();
	           var findFileList=function(){
        	   $httpService.post(config.findFileListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
        	   		for(var i=0;i<data.data.length;i++){
        	   			var index=data.data.length;
                 		var file={
                 				"INDEX":index,
                 				"DOCUMENT_PK":data.data[i].DOCUMENT_PK,
                 				"DOCUMENT_SEQUENCE":data.data[i].DOCUMENT_SEQUENCE,
                 				"DOCUMENT_NAME":data.data[i].DOCUMENT_NAME,
                 				"DOCUMENT_NUMBER":data.data[i].DOCUMENT_NUMBER
                 				};	
                 		showFileList.push(file);
                 		var code=showFileList.length;
             	   		if(code>=0&&code<10){
             	   			code=code+1;
             	   			$scope.form.DOCUMENT_SEQUENCE="00"+code;
             	   		}else if(code>=10&&code<100){
             	   			code=code+1;
             	   			$scope.form.DOCUMENT_SEQUENCE="0"+code;
             	   		}
        	   		}
        	   		$scope.newFileList=showFileList;
             		FK_FileList=showFileList;
	           	   $scope.$apply();	
	            });
           }
      
         //是否发表
       	$scope.optionChange=function(){
       		if($scope.form.PERIODICALS_FLAG!= '' && $scope.form.PERIODICALS_FLAG!=null){
       			var option=$scope.form.PERIODICALS_FLAG;
       			if(option==1){
       				$("#"+controllerName+" #publish").show();
       				$("#"+controllerName+" #appear").show();
       			}else{
       				$("#"+controllerName+" #publish").hide();
       				$("#"+controllerName+" #appear").hide();
       			}
       		}
       	}
           
       	 //临时存放文件的数据
         $scope.addFile=function(){
     		if ($scope.form.DOCUMENT_NAME == null || $scope.form.DOCUMENT_NAME == '' ) {
     			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写材料分数。"});
     			return;
				}
     		
     		if ($scope.form.DOCUMENT_NUMBER == null || $scope.form.DOCUMENT_NUMBER == '' ) {
     			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写文件材料名称。"});
     			return;
				}
     		var pk="010";
         	var index=showFileList.length;
     		var file={
     				"INDEX":index,
     				"DOCUMENT_PK":pk,
     				"DOCUMENT_SEQUENCE":$scope.form.DOCUMENT_SEQUENCE,
     				"DOCUMENT_NAME":$scope.form.DOCUMENT_NAME,
     				"DOCUMENT_NUMBER":$scope.form.DOCUMENT_NUMBER
     				};	
     		showFileList.push(file);
     		var code=showFileList.length;
 	   		if(code>=0&&code<10){
 	   			code=code+1;
 	   			$scope.form.DOCUMENT_SEQUENCE="00"+code;
 	   		}else if(code>=10&&code<100){
 	   			code=code+1;
 	   			$scope.form.DOCUMENT_SEQUENCE="0"+code;
 	   		}
     		$scope.form.DOCUMENT_NUMBER="";
			$scope.form.DOCUMENT_NAME="";
     		$scope.newFileList=showFileList;
     		FK_FileList=showFileList;
         }
         //移除
         $scope.removeFile=function(index){
         	for(var i=0;i<showFileList.length;i++){
     			var sub=showFileList[i].INDEX
     			if(sub==index){
     				showFileList.splice($.inArray(showFileList[i].INDEX,showFileList),1);
     				FK_FileList=showFileList;
     				var code=showFileList.length;
         	   		if(code>=0&&code<10){
         	   			code=code+1;
         	   			$scope.form.DOCUMENT_SEQUENCE="00"+code;
         	   		}else if(code>=10&&code<100){
         	   			code=code+1;
         	   			$scope.form.DOCUMENT_SEQUENCE="0"+code;
         	   		}
     				console.log(showFileList);
     			}	
     		}
         }	
         //保存结题报告 
         var FK_UserList=new Array();
 		$scope.save=function(){
 			for(var i=0;i<showTeacherList.length;i++){
 				var pk=showTeacherList[i].MEMBER_PK;
 				var work=$("#"+controllerName+" .work"+showTeacherList[i].MEMBER_PK).val();
 				var user={
 						"MEMBER_PK":pk,
 						"TEACHER_WORK":work
 				}
 				FK_UserList.push(user);
 			}
 			$scope.form.WORK_DESCRIBE=uedescribe.getPlainTxt();
            $scope.form.FINAL_REPORT=uereport.getPlainTxt();
 			$scope.form.FK_FileList=JSON.stringify(FK_FileList);
 			$scope.form.FK_UserList=JSON.stringify(FK_UserList);
 			console.log($scope.form.FK_UserList);
 			//校验表单
     		if(!$scope.validateForm()){
     			return;
     		}
     		
     		$httpService.post(config.updateURL,$scope.form).success(function(data) {
             	if(data.code != '0000'){
             		loggingService.info(data.msg);
             	}else{
             		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"修改成功！"});
             		eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});//发送更新事件，刷新数据
             		$scope.goback();
             	}
              }).error(function(data) {
                  loggingService.info('添加结题报告出错！');
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
         	
         	$scope.form.PERIODICALS_DATE='  ';
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
