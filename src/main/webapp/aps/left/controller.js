(function() {
    define(['jztree'], function() {
        return [
            '$scope','httpService','config', 'eventBusService','controllerName','loggingService', function($scope,$httpService,config, eventBusService,controllerName,loggingService) {
            	$httpService.post(config.getUserInfoURL, {}).success(function(data) {
        			$scope.userInfo = data.data;
        			$scope.$apply();
	            });
            	
            	$scope.goHist = function(classVo) {
            		$scope.currentClassVo = classVo;
            		$httpService.post(config.changeClassURL, classVo).success(function(data) {
            			$scope.userInfo = data.data;
            			$scope.$apply();
    	            });
            		
            		var changeControllerData = {
          	                  url:"aps/content/hist/home/config.json?CLASS_ID="+classVo.CLASS_PK,
          	                  contentName:"content",
          	                  data:{}
          	                }
          	        return eventBusService.publish(controllerName,'appPart.load.content', changeControllerData);
	            };
	            
            	$scope.logout = function(){
            		window.location.href = "logout.jsp?rand="+Math.random();
            	}
            	
            	//获取菜单数据列表
            	$httpService.post(config.menuListData, {}).success(function(data) {
            		$scope.menuData = data.data;
            		$scope.$apply();
            		
	            });
            	
            	$httpService.post(config.getUserHisClassURL, {}).success(function(data) {
            		$scope.classData = data.data;
            		$scope.currentClassVo = data.data[0];
            		$httpService.post(config.changeClassURL, data.data[0]).success(function(data) {
            			$scope.userInfo = data.data;
            			$scope.$apply();
    	            });
            		$scope.$apply();
            		
	            });
            	
            	var currentMenu;	//当前菜单
            	$scope.clickMenu = function(menu) {
            		//console.log(menu);
            		$scope.currentMenu = menu;
            		
            		//给当前菜单设置样式
                	$("#"+controllerName+" .menuPk-"+menu.MENU_PK).addClass("active");
                	if(currentMenu != null && currentMenu.MENU_PK != menu.MENU_PK){
                    	$("#"+controllerName+" .menuPk-"+currentMenu.MENU_PK).removeClass("active");
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
	            
	          
	        }
        ];
    });
}).call(this);
