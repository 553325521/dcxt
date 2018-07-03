(function() {
    define([], function() {
        return [
            '$scope','$location','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$location,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	
            	
            	//初始化中间模块
            	if($location.search()[config.contentName]){
                    $scope.appPartSrc = $location.search()[config.contentName];
                } else{
                    $scope.appPartSrc = config.defaultAppPartSrc;
                }
            	
            	//移除不用的控制器里的接收器
            	eventBusService.subscribe(controllerName, 'appPart.terminate', function(event, eventBusUnloadData) {
            		if(null != eventBusUnloadData.appPartSrc && eventBusUnloadData.appPartSrc != undefined){
            			$httpService.post(eventBusUnloadData.appPartSrc, {}).success(function(data) {
                			eventBusService.unsubscribeAll(data.name);	
        	            });
            		}
            	});
            	
            	//接收模块加载事件
            	eventBusService.subscribe(controllerName, 'appPart.load.content', function(event, m2) {
              	  if (m2.contentName === config.contentName) {
              		if($scope.appPartSrc != m2.url){
          			  eventBusService.publish(controllerName,'appPart.terminate', {appPartSrc:$scope.appPartSrc});
              		}
              		eventBusService.publish(controllerName,'appPart.load.content.checkMenu', m2);
              		
              		//$location.search(m2.contentName, m2.url);
      	            return $scope.appPartSrc = m2.url;
      	          }
                });
            	
            	//接收模态窗口加载事件
            	eventBusService.subscribe(controllerName, 'appPart.load.modal', function(event, m2) {
            		 if (m2.contentName === config.modalName) {
                		  if($scope.appPartModalSrc != m2.url){
                			  eventBusService.publish(controllerName,'appPart.terminate', {appPartSrc:$scope.appPartModalSrc});
                    	  }
                		  
          	              $scope.modal = m2;
                		  $httpService.post(m2.url, {}).success(function(data) {
              	            $scope.buttonsData = data.config.menu.buttons;
                          }).error(function(data) {
                              eventBusService.publish(controllerName,'appPart.load.modal.alert', {"title":"操作提示","content":"打开窗口出错"});
                          });
                		  $('#mainModal').modal('show');
                		  var maxHeight = $(window).height()-160 + 'px';
                		  $('#mainModal .modal-body').css({"height":"auto"});
                		  if("modal-big" == m2.size){
                			  maxHeight = $(window).height()-123 + 'px';
                			  $('#mainModal .modal-body').css({"height":maxHeight});
                		  }
                		  $('#mainModal .modal-body').css({"max-height":maxHeight});
                		  $scope.appPartModalSrc = m2.url;
                		  $scope.$apply();
        	          }
                });
            	
            	//接收模态窗口关闭事件
            	eventBusService.subscribe(controllerName, 'appPart.load.modal.close', function(event, m2) {
	              	  if (m2.contentName === config.modalName) {
	              		  $('#mainModal').modal('hide');
	              		  $scope.modal = {};
	              		  $scope.appPartModalSrc = "";
	      	          }
            	});
            	
            	//接收弹出窗口加载事件
            	eventBusService.subscribe(controllerName, 'appPart.load.modal.alert', function(event, alertModel) {
            		
	            		$('#mainModalAlert .mainModalLabelAlertTitle').html(alertModel.title);
	            		$('#mainModalAlert .mainModalAlertContent').html(alertModel.content);
	            		$('#mainModalAlert').modal('show');
	            	
            	});
            	
            	//模态窗口下的按钮点击事件
            	$scope.clickButton = function(btn) {
	                eventBusService.publish(controllerName,btn.buttonLink, btn);
	            };
	            
	            $scope.closeAlert = function() {
	            	$('#mainModalAlert').modal('hide');
	            };
	            
	            
            	
            }
        ];
    });
}).call(this);
