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

	app.filter('trustRole', function($sce) {
		return function(input) {
			if (input == '2') {
				input = '店长';
				return $sce.trustAsHtml(input)
			} else if (input == '3') {
				input = '收银'
				return $sce.trustAsHtml(input)
			} else if (input == '4') {
				input = '点菜'
				return $sce.trustAsHtml(input)
			} else if (input == '5') {
				input = '后厨'
				return $sce.trustAsHtml(input)
			} else if (input == '6') {
				input = '店员'
				return $sce.trustAsHtml(input)
			} else if (input == '7') {
				input = '代理商'
				return $sce.trustAsHtml(input)
			}
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

		$routeProvider
			.when('/', {
				templateUrl : 'html/main.html',
				controller : 'MainController'
			})
			.when('/ActingCustomerManagement/:LOCALPATH', {
				templateUrl : 'html/main.html',
				controller : 'MainController'
			})
			.when('/toOtherPage/:LOCALPATH/:btnToken', {
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