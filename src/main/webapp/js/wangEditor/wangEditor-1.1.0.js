(function (window, undefined) {
	//验证jQuery
    if (!window.jQuery) {
        return;
    }
    var document = window.document,
		$ = window.jQuery,
		supportRange = typeof document.createRange === 'function',
	    defaultHeight = 100,
	    menus,  //存储菜单配置
		currentRange,      //记录当前选中范围
        $txt = $('<div contenteditable="true" class="textarea" ></div>'),  //编辑区
        $btnContainer = $('<div class="btn-container"></div>'), //菜单容器
        $maskDiv = $('<div class="mask"></div>'),  //遮罩层
        $modalContainer = $('<div></div>'),  //modal容器
        $allMenusWithCommandName,

        commandHooks, 
        idPrefix = 'wangeditor_' + Math.random().toString().replace('.', '') + '_',
        id = 1,

        //基本配置
        basicConfig = {
            fontFamilyOptions: ['宋体', '黑体', '楷体', '隶书', '幼圆', '微软雅黑', 'Arial', 'Verdana', 'Georgia', 'Times New Roman', 'Trebuchet MS', 'Courier New', 'Impact', 'Comic Sans MS'],
            colorOptions: {
                '#880000': '暗红色',
                '#800080': '紫色',
                '#ff0000': '红色',
                '#ff00ff': '鲜粉色',
                '#000080': '深蓝色',
                '#0000ff': '蓝色',
                '#00ffff': '湖蓝色',
                '#008080': '蓝绿色',
                '#008000': '绿色',
                '#808000': '橄榄色',
                '#00ff00': '浅绿色',
                '#ffcc00': '橙黄色',
                '#808080': '灰色',
                '#c0c0c0': '银色',
                '#000000': '黑色'
            },
            fontsizeOptions: {
                1: '10px',
                2: '13px',
                3: '16px',
                4: '19px',
                5: '22px',
                6: '25px',
                7: '28px'
            }
        };

    //获取唯一ID
    function getUniqeId () {
        return idPrefix + '_' + (id++);
    }

	//selection range 相关事件
    function getCurrentRange() {
        var selection,
            range,
            parentElem,
            txt = $txt[0];
        //获取选中区域
        if(supportRange){
            //w3c
            selection = document.getSelection();
            if (selection.getRangeAt && selection.rangeCount) {
                range = document.getSelection().getRangeAt(0);
                parentElem = range.commonAncestorContainer;
            }
        }else{
            //IE8-
            range = document.selection.createRange();
            parentElem = range.parentElement();
        }
        //确定选中区域在$txt之内
        if( parentElem && (parentElem.id = txt.id || $.contains(txt, parentElem)) ){
            return range;
        }
    }
    function saveSelection() {
        currentRange = getCurrentRange();
    }
    function restoreSelection() {
        if(!currentRange){
            return;
        }
        var selection,
            range;
        if(supportRange){
            //w3c
            selection = document.getSelection();
            selection.removeAllRanges();
            selection.addRange(currentRange);
        }else{
            //IE8-
            range = document.selection.createRange();
            range.setEndPoint('EndToEnd', currentRange);
            if(currentRange.text.length === 0){
                range.collapse(false);
            }else{
                range.setEndPoint('StartToStart', currentRange);
            }
            range.select();
        }
    }

    //获取可以插入表格的元素，用于 commandHooks['insertHTML']
    function getElemForInsertTable($elem){
        if ($elem.parent().is('div[contenteditable="true"]')) {
            return $elem;
        }
        if ($elem[0].nodeName.toLowerCase() === 'body') {
            return $txt.children().last();
        }
        if ($elem.is('div[contenteditable="true"]')) {
            return $elem.children().last();
        } else {
            return getElemForInsertTable($elem.parent());
        }
    }
    //命令 hook
    commandHooks = {
        'insertHTML': function(commandName, commandValue){
            var parentElem,
                $elem;
            if(currentRange){
                if(supportRange){
                    parentElem = currentRange.commonAncestorContainer;
                }else{
                    parentElem = currentRange.parentElement();
                }
            }else{
                return;
            }
            $elem = getElemForInsertTable($(parentElem));
            if($elem.next().length === 0){
                commandValue += '<p>&nbsp;</p>';
            }
            $elem.after($(commandValue));
        }
    };
    //检验 command Enable
    function commandEnabled(commandName){
        var enabled;
        try{
            enabled = document.queryCommandEnabled(commandName);
        }catch(ex){
            enabled = false;
        }
        return enabled;
    }
    //执行命令
    function commonCommand (e, commandName, commandValue, callback) {
        var commandHook;

        //恢复选中区
        restoreSelection();
        if(!currentRange){
            e.preventDefault();
            return;
        }

        //执行
        if(commandEnabled(commandName) === true){
            document.execCommand(commandName, false, commandValue);
        }else{
            commandHook = commandHooks[commandName];
            if(commandHook){
                commandHook(commandName, commandValue);
            }
        }
        
        //执行回调函数
        if(callback){
            callback.call($txt);
        }

        //更新菜单样式
        updateMenuStyle();

        //关闭modal
        $modalContainer.find('.modal').hide();
        $maskDiv.hide();

        e.preventDefault();
    }

    //更新菜单样式
    function updateMenuStyle() {
        if(!$allMenusWithCommandName){
            $allMenusWithCommandName = $btnContainer.find('a[commandName]');
        }
        $allMenusWithCommandName.each(function(){
            var $btn = $(this),
                commandName = $.trim($btn.attr('commandName')).toLowerCase();
            if(commandName === 'insertunorderedlist' || commandName === 'insertorderedlist'){
                return;  //ff中，如果是刚刷新的页面，无选中文本的情况下，执行这两个的 queryCommandState 报 bug
            }
            if(document.queryCommandState(commandName)){
                $btn.addClass('btn-selected');
            }else{
                $btn.removeClass('btn-selected');
            }
        });
    }
    //菜单配置集
    menus = [
        {
            'type': 'dropMenu',
            'txt': 'fa fa-font',
            'command': 'fontName ',
            'dropMenu': (function(){
                var arr = [],
                    //注意，此处commandValue必填项，否则程序不会跟踪
                    temp = '<li><a href="#" commandValue="${value}" style="font-family:${family};">${txt}</a></li>',
                    $ul;

                $.each(basicConfig.fontFamilyOptions, function(key, value){
                    arr.push(
                        temp.replace('${value}', value)
                            .replace('${family}', value)
                            .replace('${txt}', value)
                    );
                });
                $ul = $('<ul>' + arr.join('') + '</ul>');
                return $ul; 
            })()
        },
        {
            'type': 'dropMenu',
            'txt': 'fa fa-text-height',
            'command': 'fontSize',
            'dropMenu': (function () {
                var arr = [],
                    //注意，此处commandValue必填项，否则程序不会跟踪
                    temp = '<li><a href="#" commandValue="${value}" style="font-size:${fontsize};">${txt}</a></li>',
                    $ul;

                $.each(basicConfig.fontsizeOptions, function(key, value){
                    arr.push(
                        temp.replace('${value}', key)
                            .replace('${fontsize}', value)
                            .replace('${txt}', value)
                    );
                });
                $ul = $('<ul>' + arr.join('') + '</ul>');
                return $ul; 
            })()
        },
        'split',
    	{
    		'type': 'btn',
    		'txt':'fa fa-bold',
    		'command': 'bold',
            'callback': function(){
                //alert('自定义callback函数');
            }
    	},
        {
            'type': 'btn',
            'txt':'fa fa-underline',
            'command': 'underline '
        },
        {
            'type': 'btn',
            'txt':'fa fa-italic',
            'command': 'italic '
        },
        'split',
        {
            'type': 'dropMenu',
            'txt': 'fa fa-pencil|color:#4a7db1',
            'command': 'foreColor ',
            'dropMenu': (function(){
                var arr = [],
                    //注意，此处commandValue必填项，否则程序不会跟踪
                    temp = '<li><a href="#" commandValue="${value}" style="color:${color};">${txt}</a></li>',
                    $ul;

                $.each(basicConfig.colorOptions, function(key, value){
                    arr.push(
                        temp.replace('${value}', key)
                            .replace('${color}', key)
                            .replace('${txt}', value)
                    );
                });
                $ul = $('<ul>' + arr.join('') + '</ul>');
                return $ul; 
            })()
        },
        'split',
        {
            'type': 'btn',
            'txt':'fa fa-align-left',
            'command': 'JustifyLeft '   
        },
        {
            'type': 'btn',
            'txt':'fa fa-align-center',
            'command': 'JustifyCenter'  
        },
        {
            'type': 'btn',
            'txt':'fa fa-align-right',
            'command': 'JustifyRight ' 
        },
        'split',
        {
            'type': 'modal',
            'txt': 'fa fa-image',
            'modal': (function () {
                    $modal = $(
                        `<div>
                    		<input type="file" accept="image/jpg,image/jpeg,image/png,image/gif" id="dcxtuploadpic">
                    	 </div>`
                    );
      
                return $modal;
            })()
        },
        'split',
        {
            'title': '撤销',
            'type': 'btn',
            'txt': 'fa fa-undo',
            'command': function(e){
                document.execCommand("undo");
                e.preventDefault();
            },
            'callback': function(){
                //alert('撤销操作');
            }
        }
 
    ];
    
 

    /*绑定jquery插件
	* customMenus: 自定义菜单
    */
    $.fn.wangEditor = function(customMenus){
    	$btnContainer = $('<div class="btn-container"></div>')
    	var height = this.height(),
    		initContent = this.html(),
            $dropMenuContainer = $('<div></div>'),
            $toolTipContainer = $('<div></div>'),
            $window = $(window);
    	
    	//加入自定义菜单
        if(customMenus){
            menus = $.extend(menus, customMenus);
        }

        
        
    	//渲染菜单（包括下拉菜单和弹出框）
        function createMenuElem(menu){
            if(menu.toString() === 'split'){
                //分割符
                return $('<div class="split"></div>');
            }
            var type = menu.type,
                txt = menu.txt,
                txtArr,
                title = menu.title,
                command = menu.command,  //函数或者字符串
                $dropMenu = menu.dropMenu,
                $modal = menu.modal,
                callback = menu.callback,
                $btn = $('<a class="btn btn-default" href="#"></a>');  //一定要有 herf='#'，否则无法监听blur事件
            if(typeof command === 'string'){
                command = $.trim(command);
            }

            //btn txt
            if(txt.indexOf('|') !== -1){
                txtArr = txt.split('|');
                txt = '<i class="' + txtArr[0] + '" style="' + txtArr[1] + '"></i>';
            }else{
                txt = '<i class="' + txt + '"></i>';
            }
            $btn.html(txt);

            //btn title
            if(title){
                $btn.attr('title', title);
            }

            //普通按钮
            if(type === 'btn'){
                //记录commandName
                if(typeof command === 'string'){
                    $btn.attr('commandName', command);
                }

                //基本命令（command是字符串）
                if(typeof command === 'string'){
                    $btn.click(function(e){
                        commonCommand(e, command, undefined, callback);
                    });
                }
                //自定义命令（command是函数）
                if(typeof command === 'function'){
                    $btn.click(function(e){
                        command(e);  //如果command是函数，则直接执行command
                    });
                }
            }
            //下拉菜单
            else if(type === 'dropMenu'){
                $btn.addClass('btn-drop');
                $btn.append($('<i class="fa fa-angle-down"></i>'));

                //渲染下拉菜单
                $dropMenu.attr('class', 'drop-menu');
                $dropMenuContainer.append($dropMenu);
                function hideDropMenu(){
                    $dropMenu.hide();
                }
                $btn.click(function(e){
                    var btnTop = $btn.offset().top,
                        btnLeft = $btn.offset().left,
                        btnHeight = $btn.height();
                    $dropMenu.css({
                        'top': (btnTop + btnHeight + 5) + 'px',
                        'left': btnLeft + 'px'
                    });
                    $dropMenu.show();
                    e.preventDefault();
                }).blur(function(e){
                    setTimeout(hideDropMenu, 100);  //先执行完，再隐藏
                });

                //命令（使用事件代理）
                $dropMenu.on('click', 'a[commandValue]', function(e){
                    var $this = $(this),
                        value = $this.attr('commandValue');
                    commonCommand(e, command, value);
                });
            }
            //弹出框
            else if(type === 'modal'){
            	 var urlTxtId = getUniqeId(),
                 btnId = getUniqeId();
                //渲染modal
                $modal.attr('class', 'modal modal-small');
                $modal.prepend($(
                    `<div class="header">
                      
                    </div>
                    <script>
                		function dcxtUploadPic(file){
                			
        	
                		}
                    </script>
                    `
                ));
                $modalContainer.append($modal);
                $btn.click(function(e){
                   console.info(5555)
                   
                  
                   $("#dcxtuploadpic").click();
                   $modal.find("#dcxtuploadpic").change(function(file){
           			for(var i=0;i<file.currentTarget.files.length;i++){
           				if (file.currentTarget.files && file.currentTarget.files[i]) {
               				var reader = new FileReader();
               				reader.onload = function (evt) {
       		        			console.info(evt.loaded)
       		        			if(evt.loaded > 3107152){
       		        					$.toptips('单张图片上传大小最大为3M')
       		        					return;
       		        			}
       		        			url = evt.target.result;
                   				if(!url){
       		                		url = document.getElementById(urlTxtId).value;
       		            		}
       		            		if(url !== ''){
       		            			console.info($("#false"))
       		            			$(".textarea")[0].innerHTML+='<img src="'+ url +'"><br>'
       		            		}
               				};
           					reader.readAsDataURL(file.currentTarget.files[i]);
           				}
       
           			}
           			$("#dcxtuploadpic")[0].value=""
           			
                   });
                   
                   
                });
            }


            return $btn;
        }
        $.each(menus, function(){
            var $menu = createMenuElem(this);
            $btnContainer.append($menu);
        });
        $btnContainer.append($('<div class="clear-both"></div>'))
                      .append($('<div class="line"></div>'));

    	//$txt光标发生变化时，保存selection，更新menu style
        $txt.on('focus click keyup', function(e){
            var keyForMoveCursor = false,
                kCodes = ' 33, 34, 35, 36, 37, 38, 39, 40, 8, 46 ';
            keyForMoveCursor = ( e.type === 'click' || e.type === 'focus' || (e.type === 'keyup') );
            if (!keyForMoveCursor) {
                return;  //只监听click,focus和[33, 34, 35, 36, 37, 38, 39, 40, 8, 46]这几个键，其他的不监听
            }
            saveSelection();
            updateMenuStyle();
        });

    	//插入 $menu 和 $txt  （$txt已经在开头定义）
        this.attr('class', 'wangEditor');
    	this.html('')
            .append($toolTipContainer)
            .append($maskDiv)
            .append($dropMenuContainer)
            .append($modalContainer)
    		.append($btnContainer)
    		.append($txt);
    	$txt.html(initContent);
    	height = height - $btnContainer.height() - 11;
    	height = height >= 50 ? height : defaultHeight;
    	$txt.height(height);

    	return $txt;
    };

})(window);