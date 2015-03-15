define(['jquery'], function($) {
	
	var $injector = angular.injector(['ticket']);
	var $config = $injector.get('config');
	var $ticket = $config.ticket;
	
	$ticket.factory('restAPIFactory', ['$httpBackend', 'config', function($httpBackend, $config) {

		mockup($config, $httpBackend);
		
		return {};
    }]);
	var number = 0;
	function mockup($config, $httpBackend) {
		
		// Mock
//		var $injector = angular.injector(['americano']);
//		var $httpBackend = $injector.get('$httpBackend');

		function matchURI(api, uri) {
			
			if ($config.apiPrefix + api == uri.split('?')[0]) {
				return true;
			}
			return false;
		}

		$httpBackend
		.whenGET(function(uri) { return matchURI('/ticket/push', uri); })
		.respond({
			"number"			: "3"
		});
		
	}
	
	return;
});