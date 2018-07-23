
	(function() {
		define(['jqueryweui'], function() {
			return [
				'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
				function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
					scope = $scope;
					
					
					scope.pageTitle = config.pageTitle;
					
					scope.form = {}
					
					
					// 购买期限数据源，单位/月 
					buying_select = ['1','2','3','6','12','24','60','120'];
					
					buying_select_transform = []
					
					angular.forEach(buying_select,function(data,index,array){
						if(data % 12 == 0){
							buying_select_transform.push(data/12 + '年');
						}else{
							buying_select_transform.push(data + '月')
						}
						
					});
					
					
					scope.form.BS_BUYINGPERIOD = buying_select_transform[0];
					
					
					
					
					
					scope.toHref = function(path) {
						var m2 = {
							"url" : "aps/content/" + path + "/config.json",
							"size" : "modal-lg",
							"contentName" : "content"
						}
						eventBusService.publish(controllerName, 'appPart.load.content', m2);
					}
					
					
					//测试，可以删
					
					scope.addOrChange = function(a){
						console.info(a);
					}
			
					
					
					comboboxInit();
					
				}
			];
		});
	}).call(this);
	
	
	function comboboxInit(){
		
	
		$("#buying_select").picker({
			title : "购买期限",
			toolbarCloseText : '确定',
			cols : [
				{
					textAlign : 'center',
					values : buying_select_transform,
					displayValues : buying_select_transform
				}
			]
		});
		
		$(".dcxt-shopselect").on('click',function(){
			$(this).addClass("dcxt-shopselect-on");
			$(this).siblings().removeClass("dcxt-shopselect-on");
			return false;
		})
		
		
	}


	$(function() {
		
	})