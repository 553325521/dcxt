(function() {
    define(['jqueryUiZh'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	var file_path="";
            	var file_id="";
            	var extname="";
            	//在线预览
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
	                    	/*console.log("url:"+url);*/
	                    	window.open(url);
	                    	/*funDownload(url,file_id+"."+extname);*/
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
	               
            	//初始化数据
            	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            		if(data.data.FILE_ID!=null){
        				file_path=data.data.FILE_PATH;
        				extname=data.data.EXT_NAME;
        				file_id=data.data.FILE_ID;
        				$('#'+controllerName+' .uploadfile').show();
                        $('#'+controllerName+' .uploadfile').html(data.data.FILE_NAME+"."+data.data.EXT_NAME);
        			}
            		$scope.form.ACTIVITY_DESIGN=data.data.ACTIVITY_DESIGN;
            		$scope.form.REPORT_POINT=data.data.REPORT_POINT;
            		$scope.form.REPORT_IMPORTANT_CHANGE=data.data.REPORT_IMPORTANT_CHANGE;
            		$scope.form.EXPLORE_REPORT_PK=data.data.EXPLORE_REPORT_PK;
            		$scope.form.SCHOOL_CHECK_USER=data.data.SCHOOL_CHECK_USER;
            		$scope.form.SCHOOL_CHECK_TIME=data.data.SCHOOL_CHECK_TIME;
            		$scope.form.SCHOOL_CHECK_RESULT=data.data.SCHOOL_CHECK_RESULT;
            		$scope.form.SCHOOL_CHECK_OPINION=data.data.SCHOOL_CHECK_OPINION;
            		$scope.$apply();
            		projectInfo();
	            });
            	
            	//申请书详情
            	var projectInfo=function(){
            		$httpService.post(config.findProjectInfoURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            			$scope.form.PROJECT_TITLE=data.data.PROJECT_TITLE;
                		$scope.form.PROJECT_TYPE=data.data.PROJECT_TYPE;
                		$scope.form.PROJECT_LEVEL=data.data.PROJECT_LEVEL;
                		$scope.form.TEACHER_NAME=data.data.TEACHER_NAME;
                		$scope.form.TEACHER_SCHOOL_NAME=data.data.TEACHER_SCHOOL_NAME;
                		$scope.form.PRICE=data.data.PRICE;
                		$scope.form.APPLY_DATE=data.data.APPLY_DATE;
                		$scope.form.APPLY_CODE=data.data.APPLY_CODE;
                		$scope.form.FINISH_DATE=data.data.FINISH_DATE;
                		$scope.form.EXPECT_RESULT=data.data.EXPECT_RESULT;
                		$scope.$apply();
    	            });
            	}
            	
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
