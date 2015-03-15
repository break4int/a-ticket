requirejs.config({
//	urlArgs: 'ts=' + (new Date()).getTime(),
	baseUrl : './js',
	paths : {
		//'text': '../asset/require/text',
		'jquery' : '../asset/jquery-1.11.1/jquery-1.11.1.min',
		'angular' : '../asset/angular-1.3.13/angular.min',
		'bootstrap' : '../asset/bootstrap-3.3.1/js/bootstrap.min',
		'ngResource' : '../asset/angular-1.3.13/angular-resource.min',
		'ngMockE2E' : '../asset/angular-1.3.13/angular-mocks',
		'mainController' : './main-controller',
		'restAPIFactory' : './factory/restapi-factory'
			

	},
	shim : {
		'angular':{
			deps : ['jquery'],
			exports:'angular'
		},
		'ngResource':{
			deps : ['angular'],
			exports:'ngResource'
		},
		'ngMockE2E':{
			deps : ['angular'],
			exports:'ngMockE2E'
		},
		'bootstrap' : {
			deps : ['jquery']
		}
	},
	waitSeconds : 3
});

requirejs(['jquery', 'angular', 'bootstrap', 'ngResource', 'ngMockE2E'], function($, angular, bootstrap) {
	
	var ticket = window.app = angular.module('ticket', ['ngResource', 'ngMockE2E']);
	
	ticket.constant('config', {
		ticket : ticket,
		apiPrefix : './json'
	});
	requirejs(['restAPIFactory', 'mainController'], function(restAPIFactory, controller) {
		
		angular.bootstrap(document, ['ticket']);
		controller.show();
	});
});