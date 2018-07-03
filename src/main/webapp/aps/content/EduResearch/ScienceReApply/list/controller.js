(function() {
    define([], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$scope.form = {};
            	
            	// 申请书区级复审过后添加开题报告
            	$scope.addReport=function(pk){
            		var m2 = {
	        				url:"aps/content/EduResearch/Report/add/config.json?pk="+pk,
	        				contentName:"content",
	        				size:"modal-lg",
	        				text:"添加开题报告",
	        				icon:"plus"
	        		}
	        		eventBusService.publish(controllerName, 'appPart.load.content', m2);
            	}
            	//开题报告区级复审过后添加中期报告
            	$scope.addProgress=function(pk){
            		var m2 = {
	        				url:"aps/content/EduResearch/Progress/add/config.json?pk="+pk,
	        				contentName:"content",
	        				size:"modal-lg",
	        				text:"添加中期报告",
	        				icon:"plus"
	        		}
	        		eventBusService.publish(controllerName, 'appPart.load.content', m2);
            	}
	
            	//中期报告区级复审过后添加结题报告
            	$scope.addFinal=function(pk){
            		var m2 = {
	        				url:"aps/content/EduResearch/Achieve/add/config.json?pk="+pk,
	        				contentName:"content",
	        				size:"modal-lg",
	        				text:"添加结题报告",
	        				icon:"plus"
	        		}
	        		eventBusService.publish(controllerName, 'appPart.load.content', m2);
            	}	
            	//添加申请书
            	eventBusService.subscribe(controllerName, controllerName+'.add', function(event, ojb) {
            		var m2 = {
	        				url:"aps/content/EduResearch/ScienceReApply/add/config.json",
	        				contentName:"content",
	        				size:"modal-lg",
	        				text:"添加申请书",
	        				icon:"plus"
	        		}
	        		eventBusService.publish(controllerName, 'appPart.load.content', m2);
        		});
            	
            	
            	//提交
            	eventBusService.subscribe(controllerName, controllerName+'.submit', function(event, ojb) {
            		var values = [];
                	$('#'+controllerName+' input[name="dataPk"]:checked').each(function(){ 
                		values.push($(this).val());
                	});
                	
                	if(values.length < 1){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请选择一条数据。"});
                	}else if(values.length > 1){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"不能同时删除多行数据，请选择一条数据。"});
                	}else{
                		$httpService.post(config.findStatusURL, {"SC_EXPLORE_PK":values[0]}).success(function(data) {
                			var apply=data.data.FK_EXPLORE_PETITION;//申请书PK
                			var report=data.data.FK_EXPLORE_REPORT;//开题报告PK
                			var progress=data.data.FK_EXPLORE_PROGRESS;//中期报告PK
                			var achieve=data.data.FK_PROJECT_ACHIEVE;//结题报告PK
                			var alter=data.data.FK_EXPLORE_ALTER;//变更报告PK
                			var delay=data.data.FK_EXPLORE_DELAY;//终止或延期报告PK
                			var status = data.data.STATUS;
                			//申请书提交
                			if(status==0){
                				explore_status=1;
                				if(apply!=null){
                					submit(values[0],explore_status);
                				}else{
                					eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"你还没有保存申请书！"});
                				}
                			//开题报告提交	
                			}else if(status==6){
                				explore_status=7;
                				if(report!=null){
                					submit(values[0],explore_status);
                				}else{
                					eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"你还没有保存申请书！"});
                				}
                			//中期报告提交		
                			}else if(status==10){
                				explore_status=11;
                				if(progress!=null){
                					submit(values[0],explore_status);
                				}else{
                					eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"你还没有保存中期报告！"});
                				}
                			//结题报告提交
                			}else if(status==14){
                				explore_status=15;
                				if(achieve!=null){
                					submit(values[0],explore_status);
                				}else{
                					eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"你还没有保存结题报告！"});
                				}
                				
                			}else if(status==21){
                				explore_status=22;
                				if(alter!=null){
                					submit(values[0],explore_status);
                				}else{
                					eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"你还没有保存变更报告！"});
                				}
                				
                			}else if(status==25){
                				explore_status=26;
                				if(delay!=null){	
                					submit(values[0],explore_status);
                				}else{
                					eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"你还没有保存终止或延期报告！"});
                				}
                			}else{
                				eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"不是未提交状态的数据不能提交！"});
                			}
                			
                			$scope.$apply();
        	            });
                			
                	}
        		
        		});
            	//提交
            	var submit=function(pk,status){
            		if(pk!=''&&pk!=null && status!=null&&status!=''){
            			$scope.form.EXPLORE_STATUS=status;
            			$scope.form.SC_EXPLORE_PK=pk;
            			$httpService.post(config.submitURL,$scope.form).success(function(data) {
                			if(data.code=="0000"){
                				$scope.find();
                				eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});
                                eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"提交成功!"});                   			
                			}
        	            }).error(function(data) {
        	            	eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"提交出错！"});
                        });
            			
            		}
            		
            		
            	}   
            	
            	
            	//修改
            	eventBusService.subscribe(controllerName, controllerName+'.update', function(event, ojb) {
            		var values = [];
                	$('#'+controllerName+' input[name="dataPk"]:checked').each(function(){ 
                		values.push($(this).val());
                	});
                	
                	if(values.length < 1){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请选择一条数据。"});
                	}else if(values.length > 1){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"不能同时删除多行数据，请选择一条数据。"});
                	}else{
                		checkStatus(values[0]);
                	}
        		})
            	
        		
        		//查询状态是否修改教育科研申请书
        		var checkStatus=function(pk){
            		$httpService.post(config.findStatusURL, {"SC_EXPLORE_PK":pk}).success(function(data) {
            			var status = data.data.STATUS;
            			if(status==0||status==5){
            				var m2 = {
        	        				url:"aps/content/EduResearch/ScienceReApply/update/config.json?pk="+pk,
        	        				contentName:"content",
        	        				size:"modal-lg",
        	        				text:"修改教育科研",
        	        				icon:"edit"
    	        				}
    	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            			}else{
            				eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"不是未提交状态跟驳回状态的数据不能修改！"});
            			}
            	
            			$scope.$apply();
            		});
            	}
        		
            	
              	//再次申请
            	eventBusService.subscribe(controllerName, controllerName+'.reapply', function(event, ojb) {
            		var values = [];
                	$('#'+controllerName+' input[name="dataPk"]:checked').each(function(){ 
                		values.push($(this).val());
                	});
                	
                	if(values.length < 1){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请选择一条数据。"});
                	}else if(values.length > 1){
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"不能同时删除多行数据，请选择一条数据。"});
                	}else{
                		findStatus(values[0]);
                	}
        		})
            	
        		
        		//查询状态是否符合再次申请教育科研申请书
        		var findStatus=function(pk){
            		$httpService.post(config.findStatusURL, {"SC_EXPLORE_PK":pk}).success(function(data) {
            			var status = data.data.STATUS;
            			if(status!=20){
            				var m2 = {
        	        				url:"aps/content/EduResearch/ScienceReApply/reapply/config.json?pk="+pk,
        	        				contentName:"content",
        	        				size:"modal-lg",
        	        				text:"再次申请",
        	        				icon:"edit"
    	        				}
    	        				eventBusService.publish(controllerName, 'appPart.load.content', m2);
            			}else{
            				eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"不是未提交状态跟驳回状态的数据不能修改！"});
            			}
            	
            			$scope.$apply();
            		});
            	}
        		
            	
        		//除了申请书以外的其它的修改
            	$scope.update=function(pk,status){
            		$httpService.post(config.findStatusURL, {"SC_EXPLORE_PK":pk}).success(function(data) {
                		$scope.form.apply=data.data.FK_EXPLORE_PETITION;//申请书PK
                		$scope.form.report=data.data.FK_EXPLORE_REPORT;//申请书PK
                		$scope.form.progress=data.data.FK_EXPLORE_PROGRESS;
                		$scope.form.achieve=data.data.FK_PROJECT_ACHIEVE;
                		$scope.form.alter=data.data.FK_EXPLORE_ALTER;//变更报告PK
                		$scope.form.delay=data.data.FK_EXPLORE_DELAY;//终止或延期报告PK
                		$scope.$apply();
            		});
            		if($scope.form.report!=null){
            			//只有报告提以及状态是未提交的状态才可以修改
            			if(status==6||status==9){
            				var m2 = {
        	        				url:"aps/content/EduResearch/Report/update/config.json?pk="+pk,
        	        				contentName:"content",
        	        				size:"modal-lg",
        	        				text:"修改开题报告",
        	        				icon:"edit"
        	        		}
        	        		eventBusService.publish(controllerName, 'appPart.load.content', m2);
            			}
            			
            		}
            		if($scope.form.progress!=null){
            			if(status==10||status==13){
            				var m2 = {
        	        				url:"aps/content/EduResearch/Progress/update/config.json?pk="+pk,
        	        				contentName:"content",
        	        				size:"modal-lg",
        	        				text:"修改中期报告",
        	        				icon:"edit"
        	        		}
        	        		eventBusService.publish(controllerName, 'appPart.load.content', m2);
            			}
            		}
            		
            		if($scope.form.achieve!=null){
            			if(status==14||status==19){
            				var m2 = {
        	        				url:"aps/content/EduResearch/Achieve/update/config.json?pk="+pk,
        	        				contentName:"content",
        	        				size:"modal-lg",
        	        				text:"修改结题报告",
        	        				icon:"edit"
        	        		}
        	        		eventBusService.publish(controllerName, 'appPart.load.content', m2);
            			}
            		}
            		if($scope.form.alter!=null){
            			if(status==21||status==24){
            				var m2 = {
        	        				url:"aps/content/EduResearch/Alter/update/config.json?pk="+pk,
        	        				contentName:"content",
        	        				size:"modal-lg",
        	        				text:"修改变更报告",
        	        				icon:"edit"
        	        		}
        	        		eventBusService.publish(controllerName, 'appPart.load.content', m2);
            			}
            		}
            		if($scope.form.delay!=null){
            			if(status==25||status==28){
            				var m2 = {
        	        				url:"aps/content/EduResearch/Delay/update/config.json?pk="+pk,
        	        				contentName:"content",
        	        				size:"modal-lg",
        	        				text:"修改延期或终止报告",
        	        				icon:"edit"
        	        		}
        	        		eventBusService.publish(controllerName, 'appPart.load.content', m2);
            			}
            		}
            	}
            	
            	//科研变更、延期或终止
            	$scope.change=function(pk,status){
            		 var m2 = {
 	        				url:"aps/content/EduResearch/ScienceReApply/changeoption/config.json?pk="+pk+"&status="+status,
 	        				contentName:"modal"		
       				}
       				eventBusService.publish(controllerName, 'appPart.load.modal', m2);
            	}
  
            	
            	//查看详情
  	           $scope.info=function(pk,petition_pk,report_pk,progress_pk,achieve_pk){
  	        	  var m2 = {
 	        				url:"aps/content/EduResearch/ScienceReApply/option/config.json?pk="
 	        					+pk+"&petition_pk="+petition_pk+"&report_pk="+report_pk+"&progress_pk="+
 	        					progress_pk+"&achieve_pk="+achieve_pk,
 	        				contentName:"modal"
 	        				
       				}
       				eventBusService.publish(controllerName, 'appPart.load.modal', m2);
  	           }

            	 $scope.find = function() { 
             		$scope.form.page = JSON.stringify($scope.page);
             		$httpService.post(config.findURL, $scope.form).success(function(data) {
                 		for( var i=0;i<data.data.length;i++){
                 			if(data.data[i].DELTA_TIME>=0){
                 				data.data[i].DELTA_TIME=data.data[i].DELTA_TIME;
                 			}else{
                 				data.data[i].DELTA_TIME="已超出";
                 			}
                 		}
             			$scope.dataList = data.data;
                        PAGE.buildPage($scope,data);
                        for(var i=0;i<$scope.dataList.length;i++){
                        	var status=$scope.dataList[i].STATUS;
         	            	var report=$scope.dataList[i].FK_EXPLORE_REPORT;
         	            	var progress=$scope.dataList[i].FK_EXPLORE_PROGRESS;
         	            	var achieve=$scope.dataList[i].FK_PROJECT_ACHIEVE;
         	            	var alter=$scope.dataList[i].FK_EXPLORE_ALTER;
         	            	var delay=$scope.dataList[i].FK_EXPLORE_DELAY;
         	            	if(status>5&&status!=20&&status!=29){
         	            		$("#"+controllerName+" #change"+$scope.dataList[i].SC_EXPLORE_PK).show();
         	            	}else{
         	            		$("#"+controllerName+" #change"+$scope.dataList[i].SC_EXPLORE_PK).hide();
         	            	}
         	            	
         	            	if(status==6){
         	            		if(report==null){
         	            			$("#"+controllerName+" #report"+$scope.dataList[i].SC_EXPLORE_PK).show();
             	            	}else{
             	      
             	            		$("#"+controllerName+" #report"+$scope.dataList[i].SC_EXPLORE_PK).hide();
             	            	}	
     	            		}else if(status==10){
     	            			if(progress==null){
     	            				$("#"+controllerName+" #progress"+$scope.dataList[i].SC_EXPLORE_PK).show();
     	            			}else{
     	            				$("#"+controllerName+" #progress"+$scope.dataList[i].SC_EXPLORE_PK).hide();
     	            			}
     	            		}else if(status==14){
     	            			if(achieve==null){
     	            				$("#"+controllerName+" #achieve"+$scope.dataList[i].SC_EXPLORE_PK).show();
     	            			}else{
     	            				$("#"+controllerName+" #achieve"+$scope.dataList[i].SC_EXPLORE_PK).hide();
     	            			}
     	            			
     	            		}else{
     	            			$("#"+controllerName+" #report"+$scope.dataList[i].SC_EXPLORE_PK).hide();
     	            			$("#"+controllerName+" #progress"+$scope.dataList[i].SC_EXPLORE_PK).hide();
     	            			$("#"+controllerName+" #achieve"+$scope.dataList[i].SC_EXPLORE_PK).hide();
     	            		}
         	            	
         	            	 if(status==6 || status==9){
         	            		if(report!=null){
         	            			$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).show();
             	            	}else{
             	      
             	            		$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).hide();
             	            	}	
         	            		
         	            	}else if(status==10 || status==13){
         	            		if(progress!=null){
     	            				$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).show();
     	            			}else{
     	            				$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).hide();
     	            			}
         	            	}else if(status==14 || status==19){
         	            		if(achieve!=null){
     	            				$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).show();
     	            			}else{
     	            				$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).hide();
     	            			}
         	            	}else if(status==21 || status==24){
         	            		if(alter!=null){
     	            				$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).show();
     	            			}else{
     	            				$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).hide();
     	            			}
         	            	}else if(status==25 || status==28){
         	            		if(delay!=null){
     	            				$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).show();
     	            			}else{
     	            				$("#"+controllerName+" #update"+$scope.dataList[i].SC_EXPLORE_PK).hide();
     	            			}
         	            	}
         	            	
         	            }
                       
     	            });
 	            };
 	            //查询
 	            $scope.select = function(){
 	            	$scope.page.current = 1;
 	            	$scope.find();
 	            }
 	            
 	            PAGE.iniPage($scope);
            	
            	//接收刷新事件
	            eventBusService.subscribe(controllerName, 'appPart.data.reload', function(event, data) {
	            	$scope.find();
	            });

            	
            }
        ];
    });
}).call(this);
