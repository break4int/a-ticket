(function(window, undefined) {
	
	/**
	 * private static 영역
	 */
	var console = window.console;
	var history = window.history;
	var location = window.location;
	
	var instance = null;
	
	var currentHistoryIndex = -1;
	var historyList = null // Array;
//	{
//		'forward' : undefined,
//		'backward' : undefined
//	}
    var browserForwardBlock = false;
	
    var TYPEOF_FUNCTION = typeof(function() {});
    function isFunction(func) {
    	return typeof(func) === TYPEOF_FUNCTION;
    }
    
    function go(idx) {
    	history.go(idx);
    }
    
    function forward() {
    	history.forward();
    }
    
    function back() {
    	history.back();
    }
    
    window.addEventListener("popstate", function(popstateEvent) {
    	var oldStateIndex = -1;
    	var newStateIndex = -1;
    	
    	oldStateIndex = currentHistoryIndex;
    	newStateIndex = popstateEvent.state;
    	
    	if (newStateIndex != undefined && newStateIndex != null ) {
    		currentHistoryIndex = newStateIndex;
    		
    		console.debug('[Paradox] currentHistoryIndex : ' + currentHistoryIndex);
    		console.debug(historyList);
    		
    		var callback = null;
    		var data = null;
    		if ( oldStateIndex < currentHistoryIndex ) {
        		/** 앞으로 진입할때는 진입한 페이지의 forward 이벤트를 적용받음 */
    			callback = historyList[currentHistoryIndex].forward;
    			data = historyList[currentHistoryIndex].data;
    			isFunction(callback) && callback.call(instance, {'index': currentHistoryIndex, 'data' : data}); 
    		} else if ( oldStateIndex > currentHistoryIndex ){
        		/** 뒤로 일때도 도착한 페이지의 backward 이벤트를 적용받음 */
    			callback = historyList[currentHistoryIndex].backward;
    			data = historyList[currentHistoryIndex].data;
    			isFunction(callback) && callback.call(instance, {'index': currentHistoryIndex, 'data' : data});
    			
        		if (browserForwardBlock) {
        			historyList[oldStateIndex].data = null;
        			historyList[oldStateIndex].forward = back;
    	    		historyList[oldStateIndex].backward = back;
    	    		historyList[currentHistoryIndex].backward = null;
    	    	}
        	}
    	} else {
    		// replaceState를 해서 그런지 초기에 state == null로 이벤트 콜백이 한번 발생함
    		//console.error('등록되지 않은 state index입니다.');
    	}
	}, false);
    
	/**
	 * Class Paradox
	 */
	var Paradox = function() {};

    Paradox.prototype = {
    		// History의 Index를 반환
    		getCurrentHistoryIndex : function() {
    			return currentHistoryIndex;
    		},
    		setData : function(index, newData) {
    			if (historyList.length > index) {
    				historyList[index].data = newData;
    			}
    		},
    		// 해당 index에 새로운 forword callback을 지정합니다.
    		setForwardCallback : function(index, newCallback) {
    			if (historyList.length > index) {
    				historyList[index].forward = newCallback;
    			}
    		},
    		// 해당 index에 새로운 backword callback을 지정합니다.
    		setBackwardCallback : function(index, newCallback) {
    			if (historyList.length > index) {
    				historyList[index].backward = newCallback;
	    		}
    		},
			// Paradox의 시작메서드
			ready : function(data, callback) {
				
    			historyList = [{
    				'data' : data,
    				"forward": null,
    				"backward": null
    			}];
    			
				currentHistoryIndex = 0;
				history.replaceState(0, null, location.href);
				
				isFunction(callback) && callback.call(this, {'index' : 0, 'data' : data});
			},
			// History 추가
			push : function(data, forwardCallback, backwardCallback) {
				var newHistoryIndex = currentHistoryIndex + 1;
    			
    			if (newHistoryIndex > 0) {
    				
    				if (historyList.length > currentHistoryIndex) {
    					historyList.splice(newHistoryIndex, historyList.length - newHistoryIndex);
        			}
        			
    				historyList[currentHistoryIndex].backward = backwardCallback;
    				historyList[newHistoryIndex] = {
    						'data' : data,
        					"forward": forwardCallback,
        					"backward": null
        			};
        			
        			currentHistoryIndex = newHistoryIndex;
        			history.pushState(currentHistoryIndex, null, location.href);
        			
        			isFunction(forwardCallback) && forwardCallback.call(this, {'index': currentHistoryIndex, 'data' : data});
    			} else {
    				console.error("please execute Paradox.ready");
    			}
			},
			 // 해당 index의 앞 뒤 History를 연결 (설정된 callback제거)
		    link : function(index) {
		    	var result = false;
    			if (index >= 0 && this.getCurrentHistoryIndex() != index) {
    				// data 지우기
    				this.setForwardCallback(index, this.forward);
    				this.setBackwardCallback(index, this.back);
	    			result = true;
    			}
    			
    			return result;
		    },
			// 해당 index 이후의 모든 History 제거 (설정된 callback제거)
			pop : function() {
				var index = this.getCurrentHistoryIndex();
    			
    			if (index > 0) {
    				// data 지우기
    				this.setForwardCallback(index, this.back);
    				this.setBackwardCallback(index, this.back);
    				this.back();
    			}
			},
			go : go,
			// History를 강제로 한 단계 진입
		    forward : forward,
		    // History를 강제로 한 단계 후퇴
		    back : back,
    		/**
    		 * default false
    		 * true 적용시 브라우저의 앞으로 가기를 금지 시킬 수 있습니다.
    		 */
    		setBrowserForwardBlock : function(value) {
    			/** true / false */
    			browserForwardBlock = value;
    		},
    		replace : function(newData, newForwardCallback, newBackwardCallback) {
    			
    			historyList[currentHistoryIndex].data = (newData == undefined) ? historyList[currentHistoryIndex].data : newData;
    			historyList[currentHistoryIndex].forward = (newForwardCallback == undefined) ? historyList[currentHistoryIndex].forward : newForwardCallback;
    			historyList[currentHistoryIndex].backward = (newBackwardCallback == undefined) ? historyList[currentHistoryIndex].backward : newBackwardCallback; 
    		}
    };
	
	/**
	 * Singleton
	 */
	var Singleton = new function() {
		this.getInstance = function() {
			if (instance == null) {
				instance = new Paradox();
				//instance.constructor = null;
			}
			return instance;
		};
	};
	
	/**
	 * Export
	 */
	window.Paradox = Singleton;
	
	return Singleton;
}(this));