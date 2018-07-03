(function() {
    define(['jqueryUiZh','ZeroClipboard','swfobject','uploadify','uploadauto'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	var file_path="";
            	var file_id="";
            	var extname="";
            	//承担人
            	 $scope.peopleList=new Array();
            	 
            	//在线预览
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
	                    	window.open(url);
	                    	/*	
	                    	console.log("url:"+url);*/
	                    	/*funDownload(url,file_id+"."+extname);*/
	           		}
	           		
	              }
	           	 //下载链接地址
	           	var funDownload = function (content,filename) {
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
            	 
            	/*初始化数据*/
            	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) { 		 
            		$scope.form=data.data;
            		if(data.data.FILE_ID!=null){
            			file_path=data.data.FILE_PATH;
        				extname=data.data.EXT_NAME;
        				file_id=data.data.FILE_ID;
        				$('#'+controllerName+' .uploadfile').show();
                        $('#'+controllerName+' .uploadfile').html(data.data.FILE_NAME+"."+data.data.EXT_NAME);
        			}
            		var user={"TEACHER_ID":data.data.FK_TEACHER,"TEACHER_NAME":$scope.form.TEACHER_NAME};
            		$scope.peopleList.push(user);
            		$scope.$apply();
        			findMemberList();
        			findTaskList();
        			findResultList();
        			findCheckInfo();
	            });
            	
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
                    				"TEACHER_EDUCATION":data.data[i].TEACHER_EDUCATION,
                    				"TEACHER_DEGREE":data.data[i].TEACHER_DEGREE,
                    				"TEACHER_GENDER":data.data[i].GENDER,
                    				"BORN":data.data[i].BORN,
                    				"JOB_TITLE":data.data[i].JOB_TITLE,
                    				"SPECIALTY":data.data[i].SPECIALTY
                    				};
            				FK_TeacherList.push(mumber);
            				showTeacherList.push(mumber);
            				var user={"TEACHER_ID":data.data[i].FK_TEACHER,"TEACHER_NAME":data.data[i].TEACHER_NAME};
            				$scope.peopleList.push(user);
            			}
            			$scope.newTeacherList=showTeacherList;
                		$scope.$apply();	
    	            });
            		
            	}

            	//临时存放课题的数据
            	var FK_TaskList=new Array();
            	var showTaskList=new Array();
            	$scope.newTaskList;
            	var findTaskList=function(){
            		$httpService.post(config.findTaskListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			for(var i=0;i<data.data.length;i++){	
            				var task={
            						"MEMBER_PROJECT_PK":data.data[i].MEMBER_PROJECT_PK,
            						"PROJECT_NAME":data.data[i].PROJECT_NAME,
            						"PROJECT_KIND":data.data[i].PROJECT_TYPE,
                    				"START_DATE":data.data[i].APPLY_DATE,
                    				"COMPLETION":data.data[i].COMPLETION,
                    				"APPROVE_UNIT":data.data[i].APPROVE_UNIT
                    				};
            				FK_TaskList.push(task);
            				showTaskList.push(task); 	
            			}
            			$scope.newTaskList=showTaskList;
                		$scope.$apply();	
    	            });
            	}
            	
            	//临时存放预期研究成果
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
                		$scope.$apply();	
    	            });
            	}
            	
            	//审核详情
                var findCheckInfo=function(){
                	$httpService.post(config.findCheckInfoURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			$scope.checkInfoList=data.data;
                		$scope.$apply();	
    	            });
                }
    		
    		$scope.savaApply=function(){
    			
    			//校验表单
        		if(!$scope.validateForm()){
        			return;
        		}
        		
        		$httpService.post(config.checkURL,$scope.form).success(function(data) {
                	if(data.code != '0000'){
                		loggingService.info(data.msg);
                	}else{
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"审核成功！"});
                		eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});//发送更新事件，刷新数据
                		eventBusService.publish(controllerName,'appPart.load.modal.close', {contentName:"modal"});	//关闭模态窗口
                	}
                 }).error(function(data) {
                     loggingService.info('校级审核申请书出错！');
                 });

    		}
    		
    		//初始化表单校验
        	VALIDATE.iniValidate($scope);
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
         		    "MENU_NAME": "我的教科研申请", 
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
