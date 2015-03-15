define(['jquery', 'angular'], function($, angular) {
	
	var $injector = angular.injector(['ticket']);
	var $config = $injector.get('config');
	var $ticket = $config.ticket;
	var $v_element = null;

	$ticket.controller('MainController', ['$scope', '$element', 'restAPIFactory', '$http', function($scope, $element, $restAPIFactory, $http) {
		$v_element = $element;
		
		var requestNumber = null;
		$scope.numberButtonHandler = function(event, number) {
			console.log(number);
			$($v_element).find(".number").hide();
			$($v_element).find(".device").show();
		}
		
		$scope.syncButtonHandler = function(event) {
			$($v_element).find('.number').show();
			$($v_element).find('.device').hide();
			
			$('.loadingImg').show();
			$http({
	    		method: 'GET', //방식
	    		url: './json/ticket/push',
	    		headers: {'Content-Type': 'application/json; charset=utf-8'}
	    	})
	    	.success(function(data, status, headers, config) {
	    		setCurrentWatingNumber(data.number);
	    		setSuccessStatus();
	    		$('.loadingImg').hide();
	    		console.log(data);
	    	})
	    	.error(function(data, status, headers, config) {
	    		setFailStatus();
	    		$('.loadingImg').hide();
	    		console.log(status);
	    	});
		};
		
		
    }]);

	
	return {
		show : function() {
			console.log($v_element);
			//$($resource).show();
		}
	}
	
	function setCurrentWatingNumber(number) {
		$($v_element).find('.waiting_num').text(number);
	}
	
	function setSuccessStatus() {
		$($v_element).find('.result').text('Success !');
		statusTimeout();
	}
	
	function setFailStatus() {
		$($v_element).find('.result').text('fail !');
		statusTimeout();
	}
	
	function statusTimeout() {
		$($v_element).find('.result').show();
		setTimeout(function() {
			$($v_element).find('.result').hide();
		}, 2000)
	}
	
});