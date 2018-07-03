(function() {
    define(['jqueryUiZh','ZeroClipboard','swfobject','uploadify','uploadauto'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$httpService.css("assets/css/uploadify.css");
            	//初始化 form 表单
            	$scope.form={};
            	//接收保存按钮事件
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	$scope.form.STATUS=params.status;
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
                		console.log(data.data);
                		$scope.form=data.data;	
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
    			$httpService.post(config.addURL,$scope.form).success(function(data) {
                	if(data.code != '0000'){
                		loggingService.info(data.msg);
                	}else{
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"新增成功！"});
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
