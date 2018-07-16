(function() {
    define([], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            	
            	
            	/*$('#J_Quantity').spinner({
                    input: '.J_Input',
                    add: '.J_Add',
                    minus: '.J_Del',
                    unit: function () {
                        return 1 + 2;
                    },
                    max: function () {
                        return (1 + 2 + 3 + 4 + 5) * 5;
                    },
                    callback: function (value, $ele) {
                        // $ele 当前文本框[jQuery对象]
                        // $ele.css('background', '#FF5E53');
                        console.log('值：' + value);
                    }
                });
            	 $(".show-toggle").change(
                    function() {
                           
                        $(".eyeryDayIn").toggle(100);
                            
                    });*/
            }
        ];
    });
}).call(this);
