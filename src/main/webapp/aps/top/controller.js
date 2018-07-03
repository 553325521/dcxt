(function() {
    define([], function() {
        return [
            '$scope','$location','httpService','config', 'eventBusService','controllerName','loggingService', function($scope,$location,$httpService,config, eventBusService,controllerName,loggingService) {
            	
            	$httpService.post(config.getUserInfoURL, {}).success(function(data) {
            		$scope.userInfo = data.data;
        			$scope.$apply();
	            });
            	
            	//获取菜单数据列表
            	$httpService.post(config.menuListData, {"MENU_CODE":"000072"}).success(function(data) {
            		$scope.menuData = data.data;
            		$scope.$apply();
	            });
            	
            	$scope.searchMenuUp = function() {
            		if(null != $scope.searchMenu || "" != $scope.searchMenu){
            			$("#"+controllerName+" .searchMenuList").show();
            		}
            	}
            	
            	var currentMenu;	//当前菜单
            	$scope.clickMenu = function(menu) {
            		$("#"+controllerName+" .searchMenuList").hide();
            		
            		$scope.currentMenu = menu;
            		
            		//给当前菜单设置样式
                	$("#"+controllerName+" .menuPk-"+menu.MENU_FATHER_PK).addClass("active");
                	if(currentMenu != null && currentMenu.MENU_FATHER_PK != menu.MENU_FATHER_PK){
                    	$("#"+controllerName+" .menuPk-"+currentMenu.MENU_FATHER_PK).removeClass("active");
                	}
            		currentMenu = menu;
            		
            		//根据导航节点判断加载模块
            		var changeControllerData = {
          	                  url:menu.MENU_LINK,
          	                  contentName:"content",
          	                  hasButton:"right",
          	                  data:menu
          	                }
          	        return eventBusService.publish(controllerName,'appPart.load.content', changeControllerData);
	            };
	            
            	$scope.logout = function(){
            		window.location.href = "logout.jsp?rand="+Math.random();
            	}
            	
            	
            	
            }
        ];
    });
}).call(this);
