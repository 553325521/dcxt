(function() {
    define(['jqueryUiZh'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	//初始化 form 表单
            	$scope.form={};
            	//接收保存按钮事件
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	/*初始化数据*/
            	$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            		/*console.log(data.data);*/
            		$scope.form=data.data;
            		$scope.$apply();
        			findCheckInfo();
        			findExpertCheckInfo();
        			findAchieveCheckInfo();
        			findExpertList();
        			findAlterList();
        			findDelayList();
	            });
            	//审核详情
                var findCheckInfo=function(){
                	$httpService.post(config.findCheckInfoURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			$scope.checkInfoList=data.data;
                		$scope.$apply();	
    	            });
                }
                
              //申请书专家审核详情
                var findExpertCheckInfo=function(){
                	$httpService.post(config.findExpertCheckURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			$scope.expertCheckList=data.data;
                		$scope.$apply();	
    	            });
                }
              //结题报告专家审核详情
                var findAchieveCheckInfo=function(){
                	$httpService.post(config.achieveCheckInfoURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			$scope.achieveCheckList=data.data;
                		$scope.$apply();	
    	            });
                }
              //专家鉴定组成员
                var findExpertList=function(){
                	$httpService.post(config.findExpertListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			$scope.newExpertList=data.data;
                		$scope.$apply();	
    	            });
                }
               //查询变更报告
                var showAlterList=new Array();
                var findAlterList=function(){
                	$httpService.post(config.findAlterListURL,{"EXPLORE_PK":params.pk}).success(function(data) {	
                		if(data.data.length>0){
                			for(var i=0;i<data.data.length;i++){
                    			var count=i+1;
                    			var name="变更报告"+count
                    			var alter={
                    					"name":name,
                    					"alter_pk":data.data[i].EXPLORE_ALTER_PK
                    			}
                    			showAlterList.push(alter);
                    		}
                    		$scope.alterList=showAlterList;
                    		
                		}
                		$scope.$apply();	
    	            });
                }
              //查询终止或延期报告
                var showDelayList=new Array();
                var findDelayList=function(){
                	$httpService.post(config.findDelayListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
                		if(data.data.length>0){
                			for(var i=0;i<data.data.length;i++){
                    			var count=i+1;
                    			var name="延期或终止报告"+count
                    			var delay={
                    					"name":name,
                    					"delay_pk":data.data[i].EXPLORE_DELAY_PK
                    			}
                    			showDelayList.push(delay);
                    		}
                    		$scope.delayList=showDelayList;
                    		/*console.log($scope.delayList);*/
                		}
                		$scope.$apply();	
    	            });
                }
                //延期或终止报告详情
                $scope.delayInfo= function(delay_pk) { 
                	var m2 = {
            				url:"aps/content/EduResearch/AreaLastCheck/delayinfo/config.json?pk="+delay_pk+"&explore_pk="+params.pk,
            				contentName:"content",
            				size:"modal-lg",
            				text:"延期或终止报告详情",
            				icon:"edit"
        				}
        			eventBusService.publish(controllerName, 'appPart.load.content', m2);
                }
                //变更报告详情
                $scope.alterInfo=function(alter_pk){
                	var m2 = {
            				url:"aps/content/EduResearch/AreaLastCheck/alterinfo/config.json?pk="+alter_pk+"&explore_pk="+params.pk,
            				contentName:"content",
            				size:"modal-lg",
            				text:"变更报告详情",
            				icon:"edit"
        				}
        			eventBusService.publish(controllerName, 'appPart.load.content', m2);
                }
        		//返回上一页
        		$scope.goback = function() { 
            		var m2 = {
            				url:"aps/content/EduResearch/AreaLastCheck/list/config.json",
            				contentName:"content",
            				size:"modal-lg",
            				text:"区级初审审核",
            				icon:"edit"
        				}
        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
        		}
  
            }
        ];
    });
}).call(this);
