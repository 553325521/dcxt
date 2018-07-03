(function() {
    define(['flexpaper'], function() {
        return [
            '$scope','httpService','config','params','$routeParams','eventBusService','controllerName','loggingService', 
            function($scope,$httpService,config,params,$routeParams,eventBusService,controllerName,loggingService) {
            $scope.form = {};
            $scope.form.FILE_ID = params.FILE_ID;
            
            var initFlexpaper = function(swfpath){
        		var fp = new FlexPaperViewer(	  
        	         'js/FlexPaper/FlexPaperViewer', 
        	         'viewerPlaceHolder',{ config : {  
        	         SwfFile : escape(swfpath),  
        	         Scale : 1,  
        	         ZoomTransition : 'easeOut',  
        	         ZoomTime : 0.5,  
        	         ZoomInterval : 0.2,  
        	         FitPageOnLoad : true,  
        	         FitWidthOnLoad : true,  
        	         FullScreenAsMaxWindow : false,  
        	         ProgressiveLoading : true,  
        	         MinZoomSize : 0.2,  
        	         MaxZoomSize : 1,  
        	         SearchMatchAll : true,  
        	         InitViewMode : 'Portrait',  
        	         PrintPaperAsBitmap : false,  
        	         ViewModeToolsVisible : true,  
        	         ZoomToolsVisible : false,  
        	         NavToolsVisible : false,  
        	         CursorToolsVisible : true,  
        	         SearchToolsVisible : true,                          
        	         localeChain: 'zh_CN'  
        	           
        	         }});
        	}
            
            var init = function(){
            	initFlexpaper("http://rescenter.sjedu.cn/ResCenter/json/Preview_getView_getView.swf?FILE_ID="+params.FILE_ID);
            }
            init();
            
            var funDownload = function (content, filename) {
                // 创建隐藏的可下载链接
                var eleLink = document.createElement('a');
                eleLink.download = filename;
                eleLink.style.display = 'none';
                // 字符内容转变成blob地址
                var blob = new Blob([content]);
                eleLink.href = URL.createObjectURL(blob);
                // 触发点击
                document.body.appendChild(eleLink);
                eleLink.click();
                // 然后移除
                document.body.removeChild(eleLink);
            };
           	
            }
        ];
    });
}).call(this);
