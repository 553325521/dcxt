(function() {
    define(['jqueryUiZh','ZeroClipboard','swfobject','uploadify','uploadauto'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	$httpService.css("assets/css/uploadify.css");
            	//初始化 form 表单
            	$scope.form={};
            	var file_path="";
            	var file_id="";
            	var extname="";
            	//接收保存按钮事件
            	$scope.form.SC_EXPLORE_PK=params.pk;
            	var uploadfiletype ='.pdf,.doc,.ppt';
            	var uploadapp="jky";
            	var UserID= "";
            	var url=config.uploadurl;  	
            	//研究方案
            	UE.delEditor('design_argument');
                var uedesign = UE.getEditor('design_argument');
            	//研究方案(匿名)
            	UE.delEditor('plans');
                var ueplans = UE.getEditor('plans');               
                //文献综述
                UE.delEditor('basis_theory');
                var uebasis= UE.getEditor('basis_theory');   
                //文献综述(匿名)
                UE.delEditor('information_review');
                var ueinformation = UE.getEditor('information_review');    
            	//完成项目可见分析
            	UE.delEditor('condition');
                var uecondition = UE.getEditor('condition');
            	//承担人
            	 $scope.peopleList=new Array();
            	 
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
                     	console.log("url:"+url);
                     	/*window.open(url);*/
                     	funDownload(url,file_id+"."+extname);
            		}
            		
                 }
            	
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
            	 
            	 
            	/*初始化数据*/
            	var findInfo=function(){
            		$httpService.post(config.findByIdURL,{"SC_EXPLORE_PK":params.pk}).success(function(data) {
            			if(data.data.FILE_ID!=null){
            				file_path=data.data.FILE_PATH;
            				extname=data.data.EXT_NAME;
            				file_id=data.data.FILE_ID;
            				$('#'+controllerName+' .uploadfile').show();
                            $('#'+controllerName+' .uploadfile').html(data.data.FILE_NAME+"."+data.data.EXT_NAME);
            			}
            			$scope.form=data.data;
                		 uedesign.ready( function() {
                          	uedesign.setContent(data.data.DESIGN_ARGUMENT);
                          } );
                 		 ueplans.ready( function() {
                 			 ueplans.setContent(data.data.PLANS);
                           } );
                 		 uebasis.ready( function() {
                 			 uebasis.setContent(data.data.BASIS_THEORY);
                           } );
                 		 ueinformation.ready( function() {
                 			 ueinformation.setContent(data.data.INFORMATION_REVIEW);
                           } );
                 		 uecondition.ready( function() {
                 			 uecondition.setContent(data.data.CONDITION);
                           } );
                		var user={"TEACHER_ID":data.data.FK_TEACHER,"TEACHER_NAME":$scope.form.TEACHER_NAME};
                		$scope.peopleList.push(user);
                		$scope.$apply();
                		findCode();
            			schoolTypeList();
            			findMemberList();
            			findTaskList();
            			findResultList();
    	            }).error(function(data) {
    	            	eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"查询数据出错！"});
                    });
            	}
            	
            	//查询当前时间跟申请编号
            	var findCode=function(){
            		$httpService.post(config.findCodeURL,{}).success(function(data) {
            			$scope.form.APPLY_DATE= data.data.APPLY_DATE;
            			$scope.form.APPLY_CODE=data.data.APPLY_CODE;
            			$scope.$apply();	
    	            });
            	}
            	
            	
            	//临时存放添加成员的数据
            	$scope.newTeacherList;
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
            				showTeacherList.push(mumber);
            				var user={"TEACHER_ID":data.data[i].FK_TEACHER,"TEACHER_NAME":data.data[i].TEACHER_NAME};
            				$scope.peopleList.push(user);
            			}
            			$scope.newTeacherList=showTeacherList;
            			FK_TeacherList=showTeacherList;
                		$scope.$apply();	
    	            }).error(function(data) {
    	            	eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"查询数据出错！"});
                    });
            		
            	}

            	//查找已有课题的数据
            	var FK_TaskList=new Array();
            	var showTaskList=new Array();
            	$scope.newTaskList;
            	var findTaskList=function(){
            		$httpService.post(config.findTaskListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			for(var i=0;i<data.data.length;i++){
            				var index=showTaskList.length;
            				var task={
            						"INDEX":index,
            						"MEMBER_PROJECT_PK":data.data[i].MEMBER_PROJECT_PK,
            						"PROJECT_NAME":data.data[i].PROJECT_NAME,
            						"PROJECT_KIND":data.data[i].PROJECT_TYPE,
                    				"START_DATE":data.data[i].APPLY_DATE,
                    				"COMPLETION":data.data[i].COMPLETION,
                    				"APPROVE_UNIT":data.data[i].APPROVE_UNIT
                    				};
            				showTaskList.push(task); 	
            			}
            			$scope.newTaskList=showTaskList;
            			FK_TaskList=showTaskList;
                		$scope.$apply();	
    	            }).error(function(data) {
    	            	eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"查询数据出错！"});
                    });
            	}
            	
            	//查找已有的研究成果
            	var FK_ResultList=new Array();
            	var showResultList=new Array();
            	$scope.newResultList;
            	var findResultList=function(){	
            		$httpService.post(config.findResultListURL,{"EXPLORE_PK":params.pk}).success(function(data) {
            			for(var i=0;i<data.data.length;i++){	
            				var index=showResultList.length;
            				var result={
            						"INDEX":index,
            						"MEMBER_EXPECTING_PK":data.data[i].MEMBER_EXPECTING_PK,
                    				"TEACHER_ID":data.data[i].TEACHER_ID,
                    				"PEOPLE":data.data[i].TEACHER_NAME,
                    				"STAGE_START_TIME":data.data[i].STAGE_START_TIME,
                    				"STAGE_END_TIME":data.data[i].STAGE_END_TIME,
                    				"RESULT_FORM":data.data[i].RESULT_FORM,
                    				"RESULT_STAGE_NAME":data.data[i].RESULT_STAGE_NAME
                    				};
            				showResultList.push(result); 	
            			}
            			$scope.newResultList=showResultList;
            			FK_ResultList=showResultList;
                		$scope.$apply();	
    	            }).error(function(data) {
    	            	eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"查询数据出错！"});
                    });
            	}
            	
            	//查询所有学校类别
            	var schoolTypeList=function(){
            		$httpService.post(config.findSchoolTypeURL).success(function(data) {
       	             	if(data.code != '0000'){
       	             		loggingService.info(data.msg);
       	             	}else{
	       	             	$scope.schoolTypeList = data.data;
		   	             	console.log($scope.schoolTypeList);
		   	             	$scope.$apply();
		   	             	$scope.schoolChange();
       	             	}
                	}).error(function(data) {
                        loggingService.info('获取初始化数据出错');
                    });

            	}
            	
            	//通过学校类别pk获取所有学校
            	$scope.schoolChange=function(){
            		console.log($scope.form.SCHOOL_TYPE_PK);
		        	if($scope.form.SCHOOL_TYPE_PK!= '' && $scope.form.SCHOOL_TYPE_PK!=null){
		        		var school_type_pk={"SCHOOL_TYPE_PK":$scope.form.SCHOOL_TYPE_PK};
			        	$httpService.post(config.findSchoolByTypeURL,school_type_pk).success(function(data) {
		                	if(data.code != '0000'){
		                		loggingService.info(data.msg);
		                	}else{
		                		$scope.unitList = data.data;
		   	         		    console.log( $scope.unitList);
		                		$scope.$apply();
		                		$scope.teacherChange();
		                	}
		
		                 }).error(function(data) {
		                     loggingService.info('获取学校出错');
		                 });
		        	}
            			
            	}

            	//通过学校PK获取所有老师
            	$scope.teacherChange=function(){
            		console.log($scope.form.UNIT_PK);
            		if($scope.form.UNIT_PK!= ''&&$scope.form.UNIT_PK!=null){
                    	var unit_pk={"UNIT_PK":$scope.form.UNIT_PK};
    		        	$httpService.post(config.findTeacherBySchoolURL,unit_pk).success(function(data) {
    	                	if(data.code != '0000'){
    	                		loggingService.info(data.msg);
    	                	}else{
    	                		$scope.teacherList = data.data;
    	                		$scope.$apply();
    	                		$scope.teacherInfo();
    	                		
    	                	}
    	
    	                 }).error(function(data) {
    	                     loggingService.info('获取老师出错');
    	                 });
            		}
            		
            	}
            	
            	//查询教师详细信息
            	$scope.teacherInfo=function(){
                    console.log($scope.form.TEACHER_PK);
            		if($scope.form.TEACHER_PK!= '' &&$scope.form.TEACHER_PK!=null){
            			var teacher_pk={"TEACHER_PK":$scope.form.TEACHER_PK};
    		        	$httpService.post(config.findTeacherInfoURL,teacher_pk).success(function(data) {
    	                	if(data.code != '0000'){
    	                		loggingService.info(data.msg);
    	                	}else{
    	                		 $scope.form.NAME=data.data.USER_NAME;
    	                		 $scope.form.BORN=data.data.DATE_BIRTH;
    	                		 $scope.form.GENDER= data.data.GENDER;
    	                		 $scope.form.TEACHER_EDUCATION= data.data.DEUCATION;
    	                		 $scope.form.TEACHER_DEGREE=data.data.EDU_DEGREE;
    	                		 $scope.form.SCHOOL_NAME=data.data.SCHOOL_NAME;
    	                		 console.log(data.data.DATE_BIRTH);
    	                		 $scope.$apply();
    	                		
    	                	}
    	
    	                 }).error(function(data) {
    	                     loggingService.info('获取老师出错');
    	                 });
            		}
            	}   	
        	//添加科研成员
        	$scope.addMember=function(){
        		if ($scope.form.TEACHER_NAME == null || $scope.form.TEACHER_NAME == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全姓名。"});
        			return;
				}
        		
        		if ($scope.form.TEACHER_GENDER == null || $scope.form.TEACHER_GENDER == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全性别。"});
        			return;
				}
        		
        		if ($scope.form.TEACHER_EDUCATION == null || $scope.form.TEACHER_EDUCATION == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全学历。"});
        			return;
				}
        		
        		if ($scope.form.TEACHER_DEGREE == null || $scope.form.TEACHER_DEGREE == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全学位。"});
        			return;
				}
        		
        		if ($scope.form.BORN == null || $scope.form.BORN == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全出生年月。"});
        			return;
				}
        		
        		if ($scope.form.JOB_TITLE == null || $scope.form.JOB_TITLE == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写职务。"});
        			return;
				}
        		
        		if ($scope.form.SPECIALTY == null || $scope.form.SPECIALTY == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写研究专长。"});
        			return;
				}
        		
        		if ($scope.form.TEACHER_SCHOOL_NAME == null || $scope.form.TEACHER_SCHOOL_NAME == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请去基础信息系统补全工作单位。"});
        			return;
				}
        		var pk="01";
        		var mumber={
        				"MEMBER_PK":pk,
        				"SCHOOL_PK":$scope.form.UNIT_PK,
        				"SCHOOL_NAME":$scope.form.SCHOOL_NAME,
        				"TEACHER_PK":$scope.form.TEACHER_PK,
        				"TEACHER_NAME":$scope.form.NAME,
        				"TEACHER_EDUCATION":$scope.form.TEACHER_EDUCATION,
        				"TEACHER_DEGREE":$scope.form.TEACHER_DEGREE,
        				"TEACHER_GENDER":$scope.form.GENDER,
        				"BORN":$scope.form.BORN,
        				"JOB_TITLE":$scope.form.JOB_TITLE,
        				"SPECIALTY":$scope.form.SPECIALTY
        				};
        		
        		if(showTeacherList.length==0){
    				showTeacherList.push(mumber);
    				var user={"TEACHER_ID":$scope.form.TEACHER_PK,"TEACHER_NAME":$scope.form.TEACHER_NAME};
    				$scope.peopleList.push(user);
    				$scope.form.TEACHER_PK="";
    				$scope.form.TEACHER_NAME="";
    				$scope.form.UNIT_PK="";
    				$scope.form.TEACHER_SCHOOL_NAME="";
    				$scope.form.TEACHER_EDUCATION="";
    				$scope.form.TEACHER_DEGREE="";
    				$scope.form.TEACHER_GENDER="";
    				$scope.form.JOB_TITLE="";
    				$scope.form.BORN="";
    				$scope.form.SPECIALTY="";
				}else{
					var flag=false;
        			for(var i=0;i<showTeacherList.length;i++){
    		        	if(mumber.TEACHER_PK===showTeacherList[i].TEACHER_PK){
    		        		flag=true;
    		        		alert("该老师已经存在课题组中！");		
    		        	}else{
    		        		flag=false;
    		        	}      	
    		        } 
        			if(flag==false){
        				FK_TeacherList.push(mumber);
		        		showTeacherList.push(mumber);
		        		var user={"TEACHER_ID":$scope.form.TEACHER_PK,"TEACHER_NAME":$scope.form.TEACHER_NAME};
		        		$scope.peopleList.push(user);
		        		$scope.form.TEACHER_PK="";
		        		$scope.form.TEACHER_NAME="";
		        		$scope.form.UNIT_PK="";
		        		$scope.form.TEACHER_SCHOOL_NAME="";
		        		$scope.form.TEACHER_EDUCATION="";
		        		$scope.form.TEACHER_DEGREE="";
		        		$scope.form.TEACHER_GENDER="";
		        		$scope.form.JOB_TITLE="";
		        		$scope.form.BORN="";
		        		$scope.form.SPECIALTY="";
        			}  		
        		$scope.newTeacherList=showTeacherList;
        		FK_TeacherList=showTeacherList;
        	}
        	//移除成员
        	$scope.removeMember=function(teacher){
        		var teacherPk=teacher.TEACHER_PK;
        		for(var i=0;i<showTeacherList.length;i++){
        				var pk=showTeacherList[i].TEACHER_PK;
        				if(teacherPk==pk){
        					showTeacherList.splice($.inArray(showTeacherList[i].TEACHER_PK,showTeacherList),1);
        					console.log(showTeacherList);
        					FK_TeacherList=showTeacherList;
        				}
        		}
        		for(var i=0;i<$scope.peopleList.length;i++){
    				var pk=$scope.peopleList[i].TEACHER_ID;
    				if(teacherPk==pk){
    					$scope.peopleList.splice($.inArray($scope.peopleList[i].TEACHER_ID,$scope.peopleList),1);
    					console.log($scope.peopleList);
    			}
    		}
        		
        	}
           
        	//添加课题
        	$scope.addTask=function(){
        		if ($scope.form.PROJECT_NAME == null || $scope.form.PROJECT_NAME == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写课题名称。"});
        			return;
				}
        		
        		if ($scope.form.PROJECT_KIND == null || $scope.form.PROJECT_KIND == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写课题类型。"});
        			return;
				}
        		
        		if ($scope.form.START_DATE == null || $scope.form.START_DATE == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写立项时间。"});
        			return;
				}
        		
        		if ($scope.form.COMPLETION == null || $scope.form.COMPLETION == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写完成情况。"});
        			return;
				}
        		
        		if ($scope.form.APPROVE_UNIT == null || $scope.form.APPROVE_UNIT == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写批准单位。"});
        			return;
				}   		
        		var pk="02";
        		var task={
						"MEMBER_PROJECT_PK":pk,
        				"PROJECT_NAME":$scope.form.PROJECT_NAME,
        				"PROJECT_KIND":$scope.form.PROJECT_KIND,
        				"START_DATE":$scope.form.START_DATE,
        				"COMPLETION":$scope.form.COMPLETION,
        				"APPROVE_UNIT":$scope.form.APPROVE_UNIT
        				};
        				showTaskList.push(task);       		
        		$scope.newTaskList=showTaskList;
        		FK_TaskList=showTaskList;
        	}
        	
        	//移除相关课题
        	$scope.removeTask=function(taskVo){
        		$scope.newTaskList.remove(taskVo);
        	}
        	
        
        	//预期研究成果
        	$scope.addResult=function(){
        		if ($scope.form.TEACHER_ID == null || $scope.form.TEACHER_ID == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写承担人。"});
        			return;
				}
        		
        		if ($scope.form.STAGE_START_TIME == null || $scope.form.STAGE_START_TIME == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"研究阶段开始时间。"});
        			return;
				}
        		
        		if ($scope.form.STAGE_END_TIME == null || $scope.form.STAGE_END_TIME == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"研究阶段结束时间。"});
        			return;
				}
        		
        		if ($scope.form.RESULT_FORM == null || $scope.form.RESULT_FORM == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写成果形式。"});
        			return;
				}
        		
        		if ($scope.form.RESULT_STAGE_NAME == null || $scope.form.RESULT_STAGE_NAME == '' ) {
        			eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"请填写阶段成果名称。"});
        			return;
				}
        		
        		var id=$scope.form.TEACHER_ID
        		for(var i=0;i<$scope.peopleList.length;i++){
        			if(id==$scope.peopleList[i].TEACHER_ID){
        				$scope.form.PEOPLE=$scope.peopleList[i].TEACHER_NAME;
        			}
        		} 	
        		var pk="03";
        		var result={
        				"MEMBER_EXPECTING_PK":pk,
        				"TEACHER_ID":$scope.form.TEACHER_ID,
        				"PEOPLE":$scope.form.PEOPLE,
        				"STAGE_START_TIME":$scope.form.STAGE_START_TIME,
        				"STAGE_END_TIME":$scope.form.STAGE_END_TIME,
        				"RESULT_FORM":$scope.form.RESULT_FORM,
        				"RESULT_STAGE_NAME":$scope.form.RESULT_STAGE_NAME
        				};
        				showResultList.push(result);   		
        		$scope.newResultList=showResultList;
        		FK_ResultList=showResultList;
        	}
        	//移除预期研究成果
        	$scope.removeResult=function(resultVo){
        		$scope.newResultList.remove(resultVo);
        	}
        	
    		//保存
    		$scope.save=function(){
    			var apply_date=$scope.form.APPLY_DATE;
    			var finish_date=$scope.form.FINISH_DATE;
    			if(apply_date==finish_date){
    				eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"申请日期跟完成日期不能是同一天。"});
        			return;
    			}
    			var length=FK_ResultList.length;
    			if(length<=0){
    				eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"至少要添加一项预计成果。"});
        			return;
    			}
    			$scope.form.FK_ResultList=JSON.stringify(FK_ResultList);
            	$scope.form.FK_TaskList=JSON.stringify(FK_TaskList);
            	$scope.form.FK_TeacherList = JSON.stringify(FK_TeacherList);
            	
    			//校验表单
        		if(!$scope.validateForm()){
        			return;
        		}
        		
        		$httpService.post(config.reapplyURL,$scope.form).success(function(data) {
                	if(data.code != '0000'){
                		loggingService.info(data.msg);
                	}else{
                		eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"申请成功！"});
                		eventBusService.publish(controllerName,'appPart.data.reload', {"scope":"site"});//发送更新事件，刷新数据
                		$scope.goback();
                	}
                 }).error(function(data) {
                     loggingService.info('申请科研申请书出错！');
                 });

    		}
    		//附件上传（回调函数）
        	var callonComplete = function(event, queueID, fileObj, response, dataObj) {
          	    //转换为json对象
            	/*console.log(response);*/
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
    		
            	//初始化表单校验
            	VALIDATE.iniValidate($scope);
            	
            	//初始化
            	$scope.form.START_DATE='  ';
            	$scope.form.STAGE_START_TIME='  ';
            	$scope.form.STAGE_END_TIME='  ';
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
