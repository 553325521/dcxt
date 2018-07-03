(function() {
    define(['jqueryUiZh','ZeroClipboard','swfobject','uploadify','uploadauto'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	$scope.project={};
            	$scope.project.EXPLORE_PK=params.explore_pk;
            	$scope.project.FK_EXPLORE_ALTER=params.pk;
            	$scope.form.SC_EXPLORE_PK=params.explore_pk;
            	
            	/*初始化数据*/
            	 var findInfo=function(){
            		 $httpService.post(config.findByIdURL,{"EXPLORE_ALTER_PK":params.pk}).success(function(data) {
            			if(data.data.FILE_ID!=null && data.data.FILE_ID!=undefined){
             				 $('#'+controllerName+' .uploadfile').show();
                             $('#'+controllerName+' .uploadfile').html(data.data.FILE_NAME+"."+data.data.EXT_NAME);
                             $("#"+controllerName+" #upload_show").show();
                 		}else{
                 			$("#"+controllerName+" #upload_show").hide();
             			}	
            			$scope.form=data.data;
                 		var resson=data.data.REASON;
                 		var ressonList=new Array();//数据库得到的数组
                 		var ressonArray=[];//转换成中文的原因数组
                		var ressonList= resson.split(",");
                		for(var i=0;i<ressonList.length;i++){
            				var reason=ressonList[i];
            				var cause="";
            				if(reason=='1'){
            					cause="负责人工作调动";
            					ressonArray.push(cause);
            				}else if(reason=='2'){
            					cause="学校（园）工作调整";
            					ressonArray.push(cause);
            				}else if(reason=='3'){
            					cause="其它";
            					ressonArray.push(cause);
            				}else{
            					
            				}
            			}
                		var a="";
                		for(var i=0;i<ressonArray.length;i++){
		    				if (i == ressonArray.length - 1) {
		    					a += ressonArray[i];
		    					} else {
		    						a += ressonArray[i];
		    						a += ",";
		    					}
		    			}
                		$scope.form.RESSON=a;	
                 		var content=data.data.ALTER_CONTENT;
                 		var contentList=new Array();
                 		var contentArray=[];//转换成中文的内容数组
                		var contentList= content.split(",");
                		for(var i=0;i<contentList.length;i++){
            				var content=contentList[i];
            				var title="";
            				if(content=='1'){
            					title="负责人";
            					contentArray.push(title);
            				}else if(content=='2'){
            					title="课题";
            					contentArray.push(title);
            				}else if(content=='3'){
            					title="主要成员";
            					contentArray.push(title);
            				}else if(content=='4'){
            					title="研究方案";
            					contentArray.push(title);
            				}else if(content=='5'){
            					title="研究内容";
            					contentArray.push(title);
            				}else if(content=='6'){
            					title="成果形式";
            					contentArray.push(title);
            				}else if(content=='7'){
            					title="其它";
            					contentArray.push(title);
            				}
            			}
                		var b="";
                		for(var i=0;i<contentArray.length;i++){
		    				if (i == contentArray.length - 1) {
		    					b += contentArray[i];
		    					} else {
		    						b += contentArray[i];
		    						b += ",";
		    					}
		    			}
                		$scope.form.ALTER_CONTENT=b;
                		/*console.log("$scope.form.ALTER_CONTENT");
                		console.log($scope.form.ALTER_CONTENT);*/
                		projectInfo();
                 		$scope.$apply();
                 		findAlterMemberList();
             			findMemberList();
             			findAlterTaskList();
             			findTaskList();
             			findAlterResultList();
             			findResultList();	
     	            });
            	 }
            	 
            	 
            	//申请书详情
             	var projectInfo=function(){
             		$httpService.post(config.findProjectInfoURL,{"SC_EXPLORE_PK":params.explore_pk}).success(function(data) {
             			$scope.project=data.data;
                 		$scope.$apply();
     	            });
             	}
            	
            	//查找变更后成员的数据
             	var findAlterMemberList=function(){
             		$httpService.post(config.findAlterMemberListURL,$scope.project).success(function(data) {  //{"EXPLORE_PK":params.explore_pk,"FK_EXPLORE_ALTER":params.pk}      		
            			$scope.alterMemberList=data.data;
                		$scope.$apply();	
    	            });
             	}
             	
             	//查找变更前成员的数据
            	var findMemberList=function(){
            		$httpService.post(config.findMemberListURL,$scope.project).success(function(data) {
            			if(data.data.length>0){
            				$scope.memberList=data.data;
            			}else{
            				$scope.memberList=$scope.alterMemberList;
            			}			
            			$scope.$apply();
    	            });
            		
            	}
            	
            	//查找变更后的数据
            	var findAlterTaskList=function(){
            		$httpService.post(config.findAlterTaskListURL,$scope.project).success(function(data) {		
            			$scope.alterTaskList=data.data;
                		$scope.$apply();	
    	            });
            	}
            	
            	//查找变更前的数据
            	var findTaskList=function(){
            		$httpService.post(config.findTaskListURL,$scope.project).success(function(data) {		
            			if(data.data.length>0){
            				$scope.taskList=data.data;
            			}else{
            				$scope.taskList=$scope.alterTaskList;
            			}	
                		$scope.$apply();	
    	            });
            	}
            	
            	//查找变更后的研究成果
            	var findAlterResultList=function(){
            		$httpService.post(config.findAlterResultListURL,$scope.project).success(function(data) {	
            			$scope.alterResultList=data.data;
                		$scope.$apply();	
    	            });
            	}
            	
            	//查找变更前的研究成果
            	var findResultList=function(){	
            		$httpService.post(config.findResultListURL,$scope.project).success(function(data) {
            			if(data.data.length>0){
            				$scope.resultList=data.data;
            			}else{
            				$scope.resultList=$scope.alterResultList;
            			}
                		$scope.$apply();	
    	            });
            	}

            	//初始化数据
            	var init = function(){
            		findInfo();
            		
            	}
            	init();
            	
            	//返回上一页
            	$scope.goback = function() { 
             		var menu = {
             		    "CONTROLLER_NAME": "AreaLastCheck", 
             		    "CREATE_BY": "SJAAAAX44858", 
             		    "CREATE_TIME": 1508169600000, 
             		    "MENU_CODE": "0000720401", 
             		    "MENU_FATHER_PK": "8f93b0e7a881469d8f3dd470c89f7644", 
             		    "MENU_IMG": "file", 
             		    "MENU_LINK": "aps/content/EduResearch/AreaLastCheck/list/config.json", 
             		    "MENU_NAME": "区级复审", 
             		    "MENU_PK": "a27a00e1eef04830b6e298aef76e6751", 
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
