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

})(this);