(function() {
    define(['jqueryUiZh'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$httpService.css("assets/css/uploadify.css");
            	//初始化 form 表单
            	$scope.form={};
            	$scope.form.SC_EXPLORE_PK=params.explore_pk;
            	
            	//初始化数据
                var findInfo=function(){
                	$httpService.post(config.findByIdURL,{"EXPLORE_DELAY_PK":params.pk}).success(function(data) {	
                		if(data.data.FILE_ID!=null && data.data.FILE_ID!=undefined){
            				$('#'+controllerName+' .uploadfile').show();
                            $('#'+controllerName+' .uploadfile').html(data.data.FILE_NAME+"."+data.data.EXT_NAME);
                            $("#"+controllerName+" #upload_show").show();
                		}else{
                			$("#"+controllerName+" #upload_show").hide();
            			}	
                		$scope.form.EXPLORE_DELAY_PK=data.data.EXPLORE_DELAY_PK;
                		$scope.form.DELAY_SORT=data.data.DELAY_SORT;
                		$scope.form.PROGRESS_ITUATION=data.data.PROGRESS_ITUATION;
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
                		var ressonList=new Array();//数据库得到的数组
                		var breakArray=[];//转换成中文的终止原因数组
                		var delayArray=[];//转换成中文的延期原因数组 var values = [];  values.push($(this).val());
                		var ressonList= resson.split(",");
                		var result="";
                		/*console.log(option==1);*/
                		if(option==1){
                			for(var i=0;i<ressonList.length;i++){
                				var reason=ressonList[i];
                				var cause="";
                				if(reason=='1'){
                					cause="负责人变动";
                					breakArray.push(cause);
                				}else if(reason=='2'){
                					cause="方案变动";
                					breakArray.push(cause);
                				}else if(reason=='3'){
                					cause="主要成员变动";
                					breakArray.push(cause);
                				}else if(reason=='4'){
                					cause="经费问题";
                					breakArray.push(cause);
                				}else{
                					cause="其它";
                					breakArray.push(cause);
                				}
                			}
                		}else{
                			for(var i=0;i<ressonList.length;i++){
                				var reason=ressonList[i];
                				var delay="";
                				if(reason=='1'){
                					delay="负责人工作调动";
                					delayArray.push(delay);
                				}else if(reason=='2'){
                					delay="学校（园）工作调整";
                					delayArray.push(delay);
                				}else if(reason=='3'){
                					delay="经费问题";
                					delayArray.push(delay);
                				}else if(reason=='4'){
                					delay="其它";
                					delayArray.push(delay);
                				}else{
                					
                				}
                			}
                		}
                		var a="";
                		for(var i=0;i<breakArray.length;i++){
		    				if (i == breakArray.length - 1) {
		    					a += breakArray[i];
		    					} else {
		    						a += breakArray[i];
		    						a += ",";
		    					}
		    			}
                		$scope.form.BREAK_RESSON=a;	
                		var b="";
                		for(var i=0;i<delayArray.length;i++){
		    				if (i == delayArray.length - 1) {
		    					b += delayArray[i];
		    					} else {
		    						b += delayArray[i];
		    						b += ",";
		    					}
		    			}
                		$scope.form.DELAY_RESSON=b;
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
            		$httpService.post(config.findProjectInfoURL,{"SC_EXPLORE_PK":params.explore_pk}).success(function(data) {
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
 
                	//初始化数据
                	var init = function(){
                		findInfo();
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
