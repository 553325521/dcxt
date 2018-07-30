/*! thunder SVN
 * Author: fet
 * Description: fet
 * Date: 2014-02-17 */
(function(angular) {
	var app = angular.module('app', [ 'ngRoute', 'commonDirectives' ]);

	app.filter('trustHtml', function($sce) {
		return function(input) {
			return $sce.trustAsHtml(input);
		}
	});
	
	app.controller('LoginController', function($scope, $route, $routeParams, $location) {
		$scope.$route = $route;
		$scope.$location = $location;
		$scope.$routeParams = $routeParams;
	});

	app.controller('MainController', function($scope, $route, $routeParams, $location) {
		$scope.$route = $route;
		$scope.$location = $location;
		$scope.$routeParams = $routeParams;
	});

	app.config(function($routeProvider, $locationProvider) {
		
		//console.info($routeParams);
		
		$routeProvider
			.when('/', {
				templateUrl : 'html/main.html',
				controller : 'MainController'
			})
			.when('/ActingCustomerManagement/:LOCALPATH', {
				templateUrl : 'html/main.html',
				controller : 'MainController'
			})
			.otherwise({
				redirectTo : '/'
			});
		// configure html5 to get links working on jsfiddle
		$locationProvider.html5Mode(true);
	});

	app.run(function($rootScope, $log) {
		$log.debug("app.js loaded");
	})

})(window.angular);