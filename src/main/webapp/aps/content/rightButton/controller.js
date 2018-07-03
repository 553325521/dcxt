(function() {
    define([], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	console.log(controllerName,"loaded");
            	
            	$("#"+controllerName).hide();
            	
        		$scope.currentMenu = {};
        		$scope.currentMenu.MENU_PK = 'none';
        		
        		//获取按钮数据列表
            	$httpService.post(config.menuButtonListData, {}).success(function(data) {
            		$scope.buttonData = data.data;
            		$scope.$apply();
	            });
            	
            	//获取菜单数据列表
            	$httpService.post(config.menuListData, {}).success(function(data) {
            		$scope.menuData = data.data;
            		$scope.$apply();
            		
	            });
            	
            	//权限按钮点击事件
	            $scope.clickButton = function(btn) {
	            	console.log(btn.BUTTON_LINK);
	                eventBusService.publish(controllerName,btn.BUTTON_LINK, btn);
	            };
	           
	            $scope.goHome = function() {
            		var changeControllerData = {
          	                  url:"aps/content/welcome/config.json",
          	                  contentName:"content",
          	                  data:{}
          	                }
          	        return eventBusService.publish(controllerName,'appPart.load.content', changeControllerData);
	            };
	            
	            var currentMenu;	//当前菜单
            	$scope.clickMenu = function(menu) {
            		console.log(menu)
            		$scope.currentMenu = menu;
            		
            		//根据导航节点判断加载模块
            		var changeControllerData = {
          	                  url:menu.MENU_LINK,
          	                  contentName:"content",
          	                  hasButton:"right",
          	                  data:menu
          	                }
          	        return eventBusService.publish(controllerName,'appPart.load.content', changeControllerData);
	            };
	            
	            //接收选中的菜单
            	eventBusService.subscribe(controllerName, 'appPart.load.content.checkMenu', function(event, m2) {
              		if(m2.hasButton == "right"){
              			$("#res_admin_right_button").show();
              			$scope.currentMenu = m2.data;
              		}else{
              			$("#res_admin_right_button").hide();
              		}
                });
            }
        ];
    });
}).call(this);
