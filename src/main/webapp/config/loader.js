(function() {
	require.config({
		baseUrl : './',
		urlArgs : "v=7",
		paths : {
			jquery : 'js/jquery/1.11.3/jquery.min',
			jqueryUi : 'js/jqueryUi/js/jquery-ui-1.9.2.custom.min',
			jqueryUiZh : 'js/jqueryUi/jqueryUi_ZH',
			jqueryForm : 'js/jqueryForm/jquery.form',

			bootstrap : 'js/bootstrap/3.3.4/js/bootstrap.min',

			jztree : 'js/zTree/js/jquery.ztree.all-3.5.min',
			smartMenu : 'js/jquerySmartMenu/js/jquery-smartMenu-min',
			jqueryUpload : 'js/jqueryUpload/jquery.upload',

			ueditorConfig : 'js/ueditor/ueditor.config',
			ueditorMin : 'js/ueditor/ueditor',
			ueditorLang : 'js/ueditor/lang/zh-cn/zh-cn',

			jwplayer : "js/jwplayer/jwplayer",
			flexpaper : "js/FlexPaper/flexpaper_flash",

			highcharts : 'js/highcharts/highcharts',
			highchartsGridLight : 'js/highcharts/themes/grid-light',
			highchartsMore : 'js/highcharts/highcharts-more',
			highcharts3D : 'js/highcharts/highcharts-3d',

			date : 'js/bootstrap-daterangepicker/date',
			daterangepicker : 'js/bootstrap-daterangepicker/daterangepicker',
			bootstrapDatetimepicker : 'js/bootstrap-datetimepicker/js/bootstrap-datetimepicker',
			bootstrapDatetimepickerZhCn : 'js/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN',

			//validateController : 'js/validate',

			ZeroClipboard : "js/ueditor/third-party/zeroclipboard/ZeroClipboard",
			upload : "assets/js/upload",
			uploadify : "js/jquery.uploadify-v2.1.4/jquery.uploadify.v2.1.4.min",
			swfobject : "js/jquery.uploadify-v2.1.4/swfobject",
			uploadauto : "js/uploadauto",
<<<<<<< HEAD
			zepto : "assets/js/zepto.min",
=======

>>>>>>> 8483eccde127202c10edb53cd765d98ecde8c1d2
			jqueryweui : "js/weui/jquery-weui.min",
			slideleft : 'js/weui/slideleft2',
		},
		shim : {
			highchartsGridLight : {
				deps : [ 'highcharts' ],
				exports : 'highchartsGridLight'
			},
			highchartsMore : {
				deps : [ 'highcharts', 'highchartsGridLight' ],
				exports : 'highchartsMore'
			},
			bootstrap : {
				deps : [ 'jqueryForm' ],
				exports : 'bootstrap'
			},
			daterangepicker : {
				deps : [ 'date' ],
				exports : 'daterangepicker'
			},
			ueditorMin : {
				deps : [ 'ueditorConfig' ],
				exports : 'ueditorMin'
			},
			jqueryUiZh : {
				deps : [ 'jqueryUi' ],
				exports : 'jqueryUiZh'
			},
			bootstrapDatetimepicker : {
				deps : [ 'jqueryUi' ],
				exports : 'bootstrapDatetimepicker'
			},
			bootstrapDatetimepickerZhCn : {
				deps : [ 'bootstrapDatetimepicker' ],
				exports : 'bootstrapDatetimepickerZhCn'
			},
			ueditorLang : {
				deps : [ 'ueditorMin' ],
				exports : 'ueditorLang'
			}
		}
	});

<<<<<<< HEAD

	require(['zepto'], function() {
		if (!window.console) {
			console = (function() {
				var instance = null;
				function Constructor() {
					this.div = document.createElement("console");
					this.div.id = "console";
					this.div.style.cssText = "filter:alpha(opacity=80);position:absolute;top:100px;right:0px;width:30%;border:1px solid #ccc;background:#eee;display:none";
					document.body.appendChild(this.div);
				//this.div = document.getElementById("console");
				}
				Constructor.prototype = {
					log : function(str) {
						var p = document.createElement("p");
						p.innerHTML = "LOG: " + str;
						this.div.appendChild(p);
					},
					debug : function(str) {
						var p = document.createElement("p");
						p.innerHTML = "DEBUG: " + str;
						p.style.color = "blue";
						this.div.appendChild(p);
					},
					error : function(str) {
						var p = document.createElement("p");
						p.innerHTML = "ERROR: " + str;
						p.style.color = "red";
						this.div.appendChild(p);
					}
				}
				function getInstance() {
					if (instance == null) {
						instance = new Constructor();
					}
					return instance;
				}
				return getInstance();
			})()
		}
	});

=======
>>>>>>> 8483eccde127202c10edb53cd765d98ecde8c1d2
})(this);