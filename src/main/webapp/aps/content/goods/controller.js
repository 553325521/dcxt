
(function() {
	define(['slideleft'], function() {
		return [
			'$scope', 'httpService', 'config', 'params', '$routeParams', 'eventBusService', 'controllerName', 'loggingService',
			function($scope, $httpService, config, params, $routeParams, eventBusService, controllerName, loggingService) {
				scope = $scope;
				// 定义页面标题
				scope.pageTitle = '商品分类';	
				
				scope.toHref = function(path,shopid) {
					var m2 = {
						"url" : "aps/content/" + path + "/config.json?fromUrl=" + config.currentUrl + "&shopid=" + shopid,
						"size" : "modal-lg",
						"contentName" : "content"
					}
					eventBusService.publish(controllerName, 'appPart.load.content', m2);
				}
				
				scope.type_list = [{
					order:1,
					name:'荤菜',
					isStart:'启用',
					area:'堂点'
				},{
					order:2,
					name:'素菜',
					isStart:'启用',
					area:'外卖'
				}];
			}
		];
	});
}).call(this);

$(function() {
	
})