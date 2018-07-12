/*! thunder SVN
 * Author: fet
 * Description: fet
 * Date: 2014-02-17 */
(function(angular) {
    	var versionStr = 1.0;
    	var initConfig = {};
    	initConfig.logConfig = {"systemName":"cdn"};
    	
        var commonDirectives,
            appendUrlParams,
            cookie_CTID = "_ctid",
            cookie_clientIP = '_ip',
            cookie_platinfo = '_platinfo',
            cookie_LUR = '_objectid',
            eventType = {
                'LOAD_APPART_CONFIG': 'LOAD_APPART_CONFIG',
                'LOADED_APPART_CONFIG': 'LOADED_APPART_CONFIG',
                'LOAD_APPART_CONTROLLER': 'LOAD_APPART_CONTROLLER',
                'LOADED_APPART_CONTROLLER': 'LOADED_APPART_CONTROLLER',
                'LOAD_APPART_VIEW': 'LOAD_APPART_VIEW',
                'LOADED_APPART_VIEW': 'LOADED_APPART_VIEW',
                'LOADED_APPART_VIEW_FROM_CACHE': 'LOADED_APPART_VIEW_FROM_CACHE',
                'REQUEST_AJAX': 'REQUEST_AJAX',
                'REQUESTED_AJAX': 'REQUESTED_AJAX'
            };

        /**
         * @ngdoc property
         * @name angular.version
         * @description
         * An object that contains information about the current Thunder version. This object has the
         * following properties:
         *
         * - `full` 鈥� `{string}` 鈥� Full version string, such as "0.9.18".
         * - `major` 鈥� `{number}` 鈥� Major version number, such as "0".
         * - `minor` 鈥� `{number}` 鈥� Minor version number, such as "9".
         * - `dot` 鈥� `{number}` 鈥� Dot version number, such as "18".
         * - `codeName` 鈥� `{string}` 鈥� Code name of the release, such as "jiggling-armfat".
         */
        var version = {
            full: '1.0.1',
            major: 1,
            minor: 0,
            dot: 1,
            codeName: 'thunder'
        };

        /**
         * append param to url if the oriUrl already content "?" then concat them with "&", otherwise with "?"
         * the param (second one) should be like "v=123"
         * @private
         * @param {string, string}
         * @return string
         */
        appendUrlParams = function(oriUrl, param) {
            if (!oriUrl) {
                return "";
            }
            if (!param) {
                return oriUrl;
            }
            return (oriUrl + ((oriUrl.indexOf("?") > -1) ? "&" : "?") + param);
        };

        // for ie8
        if (!Array.prototype.indexOf) {
            Array.prototype.indexOf = function(elt /*, from*/ ) {
                var len = this.length >>> 0;
                var from = Number(arguments[1]) || 0;
                from = (from < 0) ? Math.ceil(from) : Math.floor(from);
                if (from < 0) {
                    from += len;
                }

                for (; from < len; from++) {
                    if (from in this && this[from] === elt) {
                        return from;
                    }
                }
                return -1;
            };
        }

        if (!Array.prototype.remove) {
            Array.prototype.remove = function(value) {
                var idx = this.indexOf(value);
                if (idx != -1) {
                    return this.splice(idx, 1); // The second parameter is the number of elements to remove.
                }
                return false;
            }
        }


        Date.prototype.format = function(fmt) {
            var o = {
                "M+": this.getMonth() + 1,
                "d+": this.getDate(),
                "h+": this.getHours(),
                "m+": this.getMinutes(),
                "s+": this.getSeconds(),
                "q+": Math.floor((this.getMonth() + 3) / 3),
                "S": this.getMilliseconds()
            };
            if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
            for (var k in o)
                if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            return fmt;
        }

        

        var commonServices,
            __hasProp = {}.hasOwnProperty,
            __slice = [].slice;

        commonServices = angular.module('commonServices', []);

        

        commonServices.factory('cacheService', [
            '$cacheFactory',
            function($cacheFactory) {
                return {
                    create: function(id, size) {
                        return $cacheFactory(id, {
                            capacity: size
                        });
                    }
                };
            }
        ]);

        commonServices.factory('taskService', [
            '$timeout',
            function($timeout) {
                var recur;

                recur = function() {
                    var p, _recur;

                    p = void 0;
                    return _recur = function(delay, task) {
                        p = $timeout(task, delay);
                        p.then(function() {
                            return _recur(delay, task);
                        });
                        return function() {
                            return $timeout.cancel(p);
                        };
                    };
                };
                return {
                    recur: recur(),
                    once: function(delay, task) {
                        var p;

                        p = $timeout(task, delay);
                        return function() {
                            return $timeout.cancel(p);
                        };
                    }
                };
            }
        ]);

        commonServices.factory('metricsService', [
            'taskService', 'loggingService',
            function(taskService, loggingService) {
                var counters, meters, p, reportCounter, reportMeter, startTime;

                counters = {};
                meters = {};
                startTime = void 0;
                p = void 0;
                reportCounter = function(id) {
                    return loggingService.info('metrics', "counter " + id + ": " + counters[id]);
                };
                reportMeter = function(id, interval) {
                    var i, intervals, m, perInterval, time, total, _i, _ref;

                    time = new Date().getTime();
                    intervals = parseInt((time - startTime) / 1000 / interval);
                    total = meters[id];
                    perInterval = parseInt(total / intervals);
                    m = [];
                    m.push("meter " + id + ": " + total);
                    for (i = _i = 1, _ref = Math.min(intervals, 5); 1 <= _ref ? _i <= _ref : _i >= _ref; i = 1 <= _ref ? ++_i : --_i) {
                        m.push(" " + (perInterval * i) + "/" + (i * interval) + "s");
                    }
                    return loggingService.info('metrics', m.join());
                };
                return {
                    counter: function(id) {
                        counters[id] = 0;
                        return {
                            inc: function() {
                                return counters[id]++;
                            },
                            dec: function() {
                                return counters[id]--;
                            }
                        };
                    },
                    meter: function(id) {
                        meters[id] = 0;
                        return {
                            mark: function() {
                                return meters[id]++;
                            }
                        };
                    },
                    start: function(interval) {
                        startTime = new Date().getTime();
                        return p = taskService.recur(interval * 1000, function() {
                            var id, _results;

                            for (id in counters) {
                                reportCounter(id);
                            }
                            _results = [];
                            for (id in meters) {
                                _results.push(reportMeter(id, interval));
                            }
                            return _results;
                        });
                    },
                    stop: function() {
                        return p();
                    },
                    reset: function() {
                        counters = {};
                        return meters = {};
                    }
                };
            }
        ]);

        commonServices.factory('circularListService', [

            function() {
                return {
                    create: function(capacity) {
                        var index, list;

                        list = [];
                        index = 0;
                        return {
                            add: function(obj) {
                                list[index] = obj;
                                return index = (index + 1) % capacity;
                            },
                            getAll: function() {
                                var l, _ref, _ref1;

                                l = list.slice(index);
                                [].splice.apply(l, [(_ref = l.length), 9e9].concat(_ref1 = list.slice(0, index))), _ref1;
                                return l;
                            },
                            removeAll: function() {
                                list = [];
                                index = 0;
                                return list;
                            }
                        };
                    }
                };
            }
        ]);

        commonServices.provider('cookieService', [

            function() {
                return {
                    $get: [

                        function() {
                            return {
                                get: function(cname) {
                                    try {
                                        var name = cname + "=";

                                        var ca = document.cookie.split(';');
                                        if (ca != null && ca != "" && ca != "undefined" && !(typeof ca == 'undefined')) {
                                            for (var i = 0; i < ca.length; i++) {
                                                if (ca[i] != null && ca[i] != "undefined") {
                                                    var c = jQuery.trim(ca[i]);
                                                    if (c.indexOf(name) == 0) {
                                                        return c.substring(name.length, c.length);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (e) {
                                        // loggingService.info("get:" + e);
                                    }
                                    return "";
                                },
                                del: function(name) {
                                    try {
                                    	var exp = new Date(); 
                                	    exp.setTime(exp.getTime() - 1); 
                                	    var cval=getCookie(name); 
                                	    if(cval!=null) 
                                	        document.cookie= name + "="+cval+";expires="+exp.toGMTString(); 
                                    } catch (e) {
                                        // loggingService.info("del:" + e);
                                    }
                                    return "";
                                },
                                getFromJSON: function(cname) {
                                    try {
                                        var o = this.get(cname);
                                        if (o != null && o != "" && o != "undefined" && !(typeof o == 'undefined')) {
                                            return JSON.parse(o);
                                        }
                                    } catch (e) {
                                        // loggingService.info("getFromJSON:" + e);
                                    }
                                    return "";
                                },
                                set: function(cname, cvalue, exdays) {
                                    try {
                                        var expires = '';
                                        if (exdays) {
                                            var d = new Date();
                                            d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
                                            expires = "expires=" + d.toGMTString();
                                        }
                                        document.cookie = cname + "=" + cvalue + "; " + expires;
                                    } catch (e) {
                                        // loggingService.info("set:" + e);
                                    }
                                },
                                setToJSON: function(cname, cvalue, exdays) {
                                    try {
                                        this.set(cname, JSON.stringify(cvalue), exdays);
                                    } catch (e) {
                                        // loggingService.info("setToJSON:" + e);
                                    }
                                },
                                getAll: function() {
                                    var cookies = {}, pairs;
                                    try {
                                        pairs = document.cookie.split(";");

                                        for (var i = 0; i < pairs.length; i++) {
                                            var pair = pairs[i].split("=");
                                            if (pair[0] != "" && pair[0] != "undefined") {
                                                cookies[pair[0]] = unescape(pair[1]);
                                            }
                                        }
                                    } catch (e) {
                                        // loggingService.info("getAll:" + e);
                                    } finally {
                                        pair = null;
                                    }
                                    return cookies;
                                }
                            }
                        }
                    ]
                };
            }
        ]);

        commonServices.provider('platformService', [

            function() {
                return {
                    $get: [

                        function() {
                            return {
                                get: function() {

                                    var browserInfo;

                                    var unknown = 'Unknown';

                                    // screen
                                    var screenSize = '';
                                    if (screen.width) {
                                        width = (screen.width) ? screen.width : '';
                                        height = (screen.height) ? screen.height : '';
                                        screenSize += '' + width + " x " + height;
                                    }

                                    //browser
                                    var nVer = navigator.appVersion;
                                    var nAgt = navigator.userAgent;
                                    var browser = navigator.appName;
                                    var version = '' + parseFloat(navigator.appVersion);
                                    var majorVersion = parseInt(navigator.appVersion, 10);
                                    var nameOffset, verOffset, ix;

                                    // Opera
                                    if ((verOffset = nAgt.indexOf('Opera')) != -1) {
                                        browser = 'Opera';
                                        version = nAgt.substring(verOffset + 6);
                                        if ((verOffset = nAgt.indexOf('Version')) != -1) {
                                            version = nAgt.substring(verOffset + 8);
                                        }
                                    }
                                    // MSIE
                                    else if ((verOffset = nAgt.indexOf('MSIE')) != -1) {
                                        browser = 'Microsoft Internet Explorer';
                                        version = nAgt.substring(verOffset + 5);
                                    }

                                    //IE 11 no longer identifies itself as MS IE, so trap it
                                    //http://stackoverflow.com/questions/17907445/how-to-detect-ie11
                                    else if ((browser == 'Netscape') && (nAgt.indexOf('Trident/') != -1)) {

                                        browser = 'Microsoft Internet Explorer';
                                        version = nAgt.substring(verOffset + 5);
                                        if ((verOffset = nAgt.indexOf('rv:')) != -1) {
                                            version = nAgt.substring(verOffset + 3);
                                        }

                                    }

                                    // Chrome
                                    else if ((verOffset = nAgt.indexOf('Chrome')) != -1) {
                                        browser = 'Chrome';
                                        version = nAgt.substring(verOffset + 7);
                                    }
                                    // Safari
                                    else if ((verOffset = nAgt.indexOf('Safari')) != -1) {
                                        browser = 'Safari';
                                        version = nAgt.substring(verOffset + 7);
                                        if ((verOffset = nAgt.indexOf('Version')) != -1) {
                                            version = nAgt.substring(verOffset + 8);
                                        }

                                        // Chrome on iPad identifies itself as Safari. Actual results do not match what Google claims
                                        //  at: https://developers.google.com/chrome/mobile/docs/user-agent?hl=ja
                                        //  No mention of chrome in the user agent string. However it does mention CriOS, which presumably
                                        //  can be keyed on to detect it.
                                        if (nAgt.indexOf('CriOS') != -1) {
                                            //Chrome on iPad spoofing Safari...correct it.
                                            browser = 'Chrome';
                                            //Don't believe there is a way to grab the accurate version number, so leaving that for now.
                                        }
                                    }
                                    // Firefox
                                    else if ((verOffset = nAgt.indexOf('Firefox')) != -1) {
                                        browser = 'Firefox';
                                        version = nAgt.substring(verOffset + 8);
                                    }
                                    // Other browsers
                                    else if ((nameOffset = nAgt.lastIndexOf(' ') + 1) < (verOffset = nAgt.lastIndexOf('/'))) {
                                        browser = nAgt.substring(nameOffset, verOffset);
                                        version = nAgt.substring(verOffset + 1);
                                        if (browser.toLowerCase() == browser.toUpperCase()) {
                                            browser = navigator.appName;
                                        }
                                    }
                                    // trim the version string
                                    if ((ix = version.indexOf(';')) != -1) version = version.substring(0, ix);
                                    if ((ix = version.indexOf(' ')) != -1) version = version.substring(0, ix);
                                    if ((ix = version.indexOf(')')) != -1) version = version.substring(0, ix);

                                    majorVersion = parseInt('' + version, 10);
                                    if (isNaN(majorVersion)) {
                                        version = '' + parseFloat(navigator.appVersion);
                                        majorVersion = parseInt(navigator.appVersion, 10);
                                    }

                                    // mobile version
                                    var mobile = /Mobile|mini|Fennec|Android|iP(ad|od|hone)/.test(nVer);

                                    // cookie
                                    var cookieEnabled = (navigator.cookieEnabled) ? true : false;

                                    if (typeof navigator.cookieEnabled == 'undefined' && !cookieEnabled) {
                                        document.cookie = 'testcookie';
                                        cookieEnabled = (document.cookie.indexOf('testcookie') != -1) ? true : false;
                                    }

                                    // system
                                    var os = unknown;
                                    var clientStrings = [{
                                        s: 'Windows 3.11',
                                        r: /Win16/
                                    }, {
                                        s: 'Windows 95',
                                        r: /(Windows 95|Win95|Windows_95)/
                                    }, {
                                        s: 'Windows ME',
                                        r: /(Win 9x 4.90|Windows ME)/
                                    }, {
                                        s: 'Windows 98',
                                        r: /(Windows 98|Win98)/
                                    }, {
                                        s: 'Windows CE',
                                        r: /Windows CE/
                                    }, {
                                        s: 'Windows 2000',
                                        r: /(Windows NT 5.0|Windows 2000)/
                                    }, {
                                        s: 'Windows XP',
                                        r: /(Windows NT 5.1|Windows XP)/
                                    }, {
                                        s: 'Windows Server 2003',
                                        r: /Windows NT 5.2/
                                    }, {
                                        s: 'Windows Vista',
                                        r: /Windows NT 6.0/
                                    }, {
                                        s: 'Windows 7',
                                        r: /(Windows 7|Windows NT 6.1)/
                                    }, {
                                        s: 'Windows 8.1',
                                        r: /(Windows 8.1|Windows NT 6.3)/
                                    }, {
                                        s: 'Windows 8',
                                        r: /(Windows 8|Windows NT 6.2)/
                                    }, {
                                        s: 'Windows NT 4.0',
                                        r: /(Windows NT 4.0|WinNT4.0|WinNT|Windows NT)/
                                    }, {
                                        s: 'Windows ME',
                                        r: /Windows ME/
                                    }, {
                                        s: 'Android',
                                        r: /Android/
                                    }, {
                                        s: 'Open BSD',
                                        r: /OpenBSD/
                                    }, {
                                        s: 'Sun OS',
                                        r: /SunOS/
                                    }, {
                                        s: 'Linux',
                                        r: /(Linux|X11)/
                                    }, {
                                        s: 'iOS',
                                        r: /(iPhone|iPad|iPod)/
                                    }, {
                                        s: 'Mac OS X',
                                        r: /Mac OS X/
                                    }, {
                                        s: 'Mac OS',
                                        r: /(MacPPC|MacIntel|Mac_PowerPC|Macintosh)/
                                    }, {
                                        s: 'QNX',
                                        r: /QNX/
                                    }, {
                                        s: 'UNIX',
                                        r: /UNIX/
                                    }, {
                                        s: 'BeOS',
                                        r: /BeOS/
                                    }, {
                                        s: 'OS/2',
                                        r: /OS\/2/
                                    }, {
                                        s: 'Search Bot',
                                        r: /(nuhk|Googlebot|Yammybot|Openbot|Slurp|MSNBot|Ask Jeeves\/Teoma|ia_archiver)/
                                    }];


                                    for (var id in clientStrings) {
                                        var cs = clientStrings[id];
                                        if (cs.r.test(nAgt)) {
                                            os = cs.s;
                                            break;
                                        }
                                    }

                                    var osVersion = unknown;

                                    if (/Windows/.test(os)) {
                                        osVersion = /Windows (.*)/.exec(os)[1];
                                        os = 'Windows';
                                    }

                                    switch (os) {
                                        case 'Mac OS X':
                                            osVersion = /Mac OS X (10[\.\_\d]+)/.exec(nAgt)[1];
                                            break;

                                        case 'Android':
                                            osVersion = /Android ([\.\_\d]+)/.exec(nAgt)[1];
                                            break;

                                        case 'iOS':
                                            osVersion = /OS (\d+)_(\d+)_?(\d+)?/.exec(nVer);
                                            osVersion = osVersion[1] + '.' + osVersion[2] + '.' + (osVersion[3] | 0);
                                            break;

                                    }

                                    var obj = {
                                        'screen': screenSize,
                                        'browser': browser,
                                        'browserVersion': version,
                                        'mobile': mobile,
                                        'os': os,
                                        'osVersion': osVersion,
                                        'cookies': cookieEnabled
                                    };

                                    return obj;
                                }
                            }
                        }
                    ]
                };
            }
        ]);

        commonServices.provider('geoLocationService', [

            function() {
                return {
                    $get: [

                        function() {
                            return {
                                get: function() {
                                    var latitude = null,
                                        longitude = null,
                                        msg = null;
                                    if (navigator.geolocation) {
                                        navigator.geolocation.getCurrentPosition(showPosition, showError);
                                    } else {
                                        msg = "Geolocation is not supported by this browser.";
                                    }

                                    function showPosition(position) {
                                        latitude = position.coords.latitude;
                                        longitude = position.coords.longitude;
                                    }

                                    function showError(error) {
                                        switch (error.code) {
                                            case error.PERMISSION_DENIED:
                                                msg = "User denied the request for Geolocation.";
                                                break;
                                            case error.POSITION_UNAVAILABLE:
                                                msg = "Location information is unavailable.";
                                                break;
                                            case error.TIMEOUT:
                                                msg = "The request to get user location timed out.";
                                                break;
                                            case error.UNKNOWN_ERROR:
                                                msg = "An unknown error occurred.";
                                                break;
                                        }
                                    }
                                    return {
                                        "latitude": latitude,
                                        "longitude": longitude,
                                        "msg": msg
                                    };
                                }
                            }
                        }
                    ]
                };
            }
        ]);

        commonServices.provider('ctidService', [

            function() {
                return {
                    $get: ['cookieService',

                        function(cookieService) {
                            return {
                                create: function() {
                                    var s = [];
                                    var hexDigits = "0123456789abcdef";
                                    for (var i = 0; i < 36; i++) {
                                        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
                                    }
                                    s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
                                    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
                                    s[8] = s[13] = s[18] = s[23] = "-";
                                    return s.join("");
                                },
                                getFromCookie: function() {
                                    return cookieService.get(cookie_CTID);
                                },
                                get: function() {
                                    var uuid = this.getFromCookie();
                                    if (uuid != null && uuid != '' && uuid != 'undefined') {
                                        return uuid;
                                    } else {
                                        uuid = this.create();
                                        cookieService.set(cookie_CTID, uuid, 30);
                                        return uuid;
                                    }
                                }
                            }
                        }
                    ]
                };
            }
        ]);

        commonServices.provider('loggingService', [

            function() {

                var appenders, level, logLevel,
//                    logAggregatorHost = "http://flip.fareastone.com.tw",
//                    logInfoActionUrl = "/CLA/ClientInfoServlet",
//                    logReceiverActionUrl = "/CLA/CLAServlet",
                	logServletUrl = "ClientLogServlet",
                	systemContextPath = "XYZ",
                    _maxLogs = 100,
                    logBuffer = [],
                    consoleAppenderStatus = "OFF",
                    logCenterAppenderStatus = "ON",
                    logAnalyzeLevel = 0,
                    clientIP,
                    getClientIP;


                appenders = [];
                logLevel = 0;
                level = function(l) {
                    switch (l) {
                        case 'DEBUG':
                            return 0;
                        case 'INFO':
                            return 1;
                        case 'WARNING':
                            return 2;
                        case 'ERROR':
                            return 3;
                        case 'ANALYSIS':
                            return 4;
                        case 'PDP':
                            return 5;
                        default:
                            return 0;
                    }
                };

                if (initConfig.logConfig.consoleAppenderStatus != null && initConfig.logConfig.consoleAppenderStatus != "" && initConfig.logConfig.consoleAppenderStatus != "undefined" || !(typeof initConfig.logConfig.consoleAppenderStatus == 'undefined')) {
                    consoleAppenderStatus = initConfig.logConfig.consoleAppenderStatus;
                }

                if (initConfig.logConfig.logLevel != null && initConfig.logConfig.logLevel != "" && initConfig.logConfig.logLevel != "undefined" || !(typeof initConfig.logConfig.logLevel == 'undefined')) {
                    logLevel = level(initConfig.logConfig.logLevel);
                }

                if (initConfig.logConfig.logCenterAppenderStatus != null && initConfig.logConfig.logCenterAppenderStatus != "" && initConfig.logConfig.logCenterAppenderStatus != "undefined" || !(typeof initConfig.logConfig.logCenterAppenderStatus == 'undefined')) {
                    logCenterAppenderStatus = initConfig.logConfig.logCenterAppenderStatus;
                }

                if (initConfig.logConfig.logAnalyzeLevel != null && initConfig.logConfig.logAnalyzeLevel != "" && initConfig.logConfig.logAnalyzeLevel != "undefined" || !(typeof initConfig.logConfig.logAnalyzeLevel == 'undefined')) {
                    logAnalyzeLevel = level(initConfig.logConfig.logAnalyzeLevel);
                }

                if (initConfig.logConfig.logDumpSize != null && initConfig.logConfig.logDumpSize != "" && initConfig.logConfig.logDumpSize != "undefined" || !(typeof initConfig.logConfig.logDumpSize == 'undefined')) {
                    if (initConfig.logConfig.logDumpSize > 0 && initConfig.logConfig.logDumpSize <= _maxLogs) {
                        _maxLogs = initConfig.logConfig.logDumpSize;
                    }
                }

//                if (initConfig.logConfig.logAggregatorHost != null && initConfig.logConfig.logAggregatorHost != "" && initConfig.logConfig.logAggregatorHost != "undefined" || !(typeof initConfig.logConfig.logAggregatorHost == 'undefined')) {
//                    logAggregatorHost = initConfig.logConfig.logAggregatorHost;
//                }

                
                if (initConfig.logConfig.systemContextPath != null && initConfig.logConfig.systemContextPath != "" && initConfig.logConfig.systemContextPath != "undefined" || !(typeof initConfig.logConfig.systemContextPath == 'undefined')) {
                	systemContextPath = initConfig.logConfig.systemContextPath;
                }

                getClientIP = function(cookieService) {
                    clientIP = cookieService.get(cookie_clientIP);
                    if (clientIP == null || clientIP == "" || clientIP == "undefined" || (typeof clientIP == 'undefined')) {
//                        var logInfoAction = logAggregatorHost + logInfoActionUrl;
                    	var logInfoAction = "/" + systemContextPath + "/" + logServletUrl;
                    	/*
                        jQuery.get(logInfoAction).success(function(data, status, headers, config) {
                            clientIP = data.ip;
                            cookieService.set(cookie_clientIP, clientIP);
                            return clientIP;
                        });*/
                    }
                };

                return {
                    addAppender: function(appender) {
                        return appenders.push.apply(appenders, appender);
                    },
                    setLevel: function(l) {
                        return logLevel = level(l);
                    },
                    setLogAnalyzeLevel: function(l) {
                        return logAnalyzeLevel = level(l);
                    },
                    setLogAggregatorHost: function(targeturl) {
                        return logAggregatorHost = targeturl;
                    },
                    setLogDumpSize: function(s) {
                        if (s > 0 && s <= _maxLogs) {
                            return _maxLogs = s;
                        }
                    },
                    setConsoleAppenderStatus: function(status) {
                        return consoleAppenderStatus = status;
                    },
                    setLogCenterAppenderStatus: function(status) {
                        return logCenterAppenderStatus = status;
                    },
                    $get: [
                        'cookieService', 'platformService', 'ctidService',
                        function(cookieService, platformService, ctidService) {

                            getClientIP(cookieService);
                            ctidService.get();

                            if (consoleAppenderStatus === "ON") {
                                // Adding Console Log Appenders
                                appenders.push(
                                    (function() {
                                        //no console (ie8)
                                        if ((typeof console === "undefined") || (typeof loggingService.info === "undefined")) {
                                            return function() {};
                                        }

                                        var getConsoleLog, getChromeColor;

                                        getConsoleLog = function(level) {
                                            var level_l = level.toLowerCase() === "warning" ? "warn" : level.toLowerCase(),
                                                logging = console[level_l] || loggingService.info;
                                            return logging;
                                        };
                                        return function(l, source, message) {

                                            if (level(l) >= logLevel) {
                                                var _log_msg = l + ": " + source + " -> " + message;
                                                try {
                                                    return getConsoleLog(l)(_log_msg);
                                                } catch (e) {
                                                    return getConsoleLog(l).call(console, _log_msg);
                                                }
                                            }
                                        };
                                    })()
                                );
                            }

                            if (logCenterAppenderStatus === "ON") {
                                // Adding Log Center Appenders
                                appenders.push(
                                    (function() {

                                        var getCookies, getBrowserInfo, getSysTime,
                                            getScreenInfo, getClientIP, getLUR;

                                        getCookies = function() {
                                            return cookieService.getAll();
                                        }

                                        getBrowserInfo = function() {
                                            var platinfo = cookieService.getFromJSON(cookie_platinfo);
                                            if (platinfo == null || platinfo == "" || platinfo == "undefined" || (typeof platinfo == 'undefined')) {
                                                platinfo = platformService.get();
                                                cookieService.setToJSON(cookie_platinfo, platinfo);
                                            }

                                            var screeninfo = getScreenInfo();

                                            var clientprinfo = {
                                                "clientip": clientIP,
                                                "browserinfo": {
                                                    "availablewidth": screeninfo.availWidth,
                                                    "screenwidth": screeninfo.width,
                                                    "browsernm": platinfo.browser,
                                                    "screenheight": screeninfo.height,
                                                    "osinfo": {
                                                        'osname': platinfo.os,
                                                        'osversion': platinfo.osVersion
                                                    },
                                                    "version": platinfo.browserVersion,
                                                    "availableheight": screeninfo.availHeight
                                                }
                                            };

                                            return clientprinfo;
                                        };

                                        getScreenInfo = function() {
                                            var width, height, availWidth, availHeight;

                                            if (screen.width) {
                                                width = (screen.width) ? screen.width : '';
                                                height = (screen.height) ? screen.height : '';
                                                availWidth = (screen.availWidth) ? screen.availWidth : '';
                                                availHeight = (screen.availHeight) ? screen.availHeight : '';
                                            }

                                            return {
                                                'width': width,
                                                'height': height,
                                                'availWidth': availWidth,
                                                'availHeight': availHeight
                                            };
                                        }

                                        getSysTime = function() {
                                            return new Date().format("yyyy-MM-dd hh:mm:ss.S");
                                        }

                                        getLUR = function() {
                                            return cookieService.get(cookie_LUR);
                                        }

                                        return function(l, source, message) {
                                            if (level(l) >= logAnalyzeLevel) {

                                                var _message = {};

                                                if (level(l) <= level('WARNING')) {
                                                    _message = {
                                                        "msg": {
                                                            "debuginfo": {
                                                                "debugmsg": source + " -> " + message
                                                            }
                                                        }
                                                    };
                                                } else if (level(l) == level('ERROR')) {
                                                    _message = {
                                                        "msg": {
                                                            "errorinfo": {
                                                                "errorcode": source,
                                                                "errormsg": message,
                                                                "stacktrace": ""
                                                            }
                                                        }
                                                    };
                                                } else if (level(l) >= level('ANALYSIS')) {

                                                    if (message == null || message == "" || message == "undefined" || (typeof message == 'undefined')) {
                                                        message = {};
                                                    }

                                                    // special handle for Analysisinfo object.
                                                    if (message.msg != null && message.msg.analysisinfo != null) {
                                                        message.msg.analysisinfo["eventinfo"] = source;
                                                    } else {
                                                        _message = {
                                                            "msg": {
                                                                "analysisinfo": {
                                                                    "eventinfo": source
                                                                }
                                                            }
                                                        };
                                                    }

                                                    jQuery.extend(_message, message);
                                                }

                                                // special handle for LAP object.
                                                if (_message.lap != null) {
                                                    if (_message.lap.clientapinfo != null) {
                                                        _message.lap.clientapinfo["sysid"] = initConfig.logConfig.systemName;
                                                        _message.lap.clientapinfo["appname"] = "thunder";
                                                        _message.lap.clientapinfo["appver"] = version.full;
                                                    }
                                                } else {
                                                    var _tmp_lap = {
                                                        "lap": {
                                                            "clientapinfo": {
                                                                "sysid": initConfig.logConfig.systemName,
                                                                "appname": "thunder",
                                                                "appver": version.full
                                                            }
                                                        }
                                                    }
                                                    jQuery.extend(_message, _tmp_lap);
                                                }

                                                var _log_msg = {
                                                    "lpr": {
                                                        "clientprinfo": getBrowserInfo()
                                                    },
                                                    "ver": "1.0",
                                                    "ctid": ctidService.get(),
                                                    "lvl": l,
                                                    "lct": getSysTime(),
                                                    "lur": {
                                                        "objectid": getLUR()
                                                    }
                                                };

                                                jQuery.extend(_log_msg, _message);
                                                // loggingService.info(_log_msg);
                                                logBuffer.push(_log_msg);

                                                // Send to Log Server
                                                if (logBuffer.length >= _maxLogs) {
                                                    var tttmp = {
                                                        "logmsg": JSON.stringify(logBuffer)
                                                    };
                                                    var logmsg = jQuery.param(tttmp);
                                                    try {
//                                                        var logReceiverAction = logAggregatorHost + logReceiverActionUrl;
                                                        var logReceiverAction = "/" + systemContextPath + "/" + logServletUrl;
                                                        // jQuery.post(logReceiverAction, logmsg, {
                                                        //     'headers': {
                                                        //         'Content-Type': 'application/x-www-form-urlencoded'
                                                        //     },
                                                        //     'timeout': 600,
                                                        //     'crossDomain': true
                                                        // }).done(function(data) {
                                                        //     loggingService.info("success");
                                                        // }).fail(function(data) {
                                                        //     loggingService.info(data);
                                                        // });

//                                                        jQuery.support.cors = true;
//                                                        jQuery.ajax({
//                                                            "type": "POST",
//                                                            "async": true,
//                                                            "url": logReceiverAction,
//                                                            "cache": false,
//                                                            "contentType": "application/x-www-form-urlencoded",
//                                                            // "contentType": "text/plain",
//                                                            "dataType:": "json",
//                                                            "crossDomain": true,
//                                                            "timeout": 600,
//                                                            "data": logmsg
//                                                        }).done(function(data) {
//                                                            // loggingService.info("success");
//                                                        }).fail(function(xhr, textStatus, error) {
//                                                            // loggingService.info(xhr.statusText);
//                                                            // loggingService.info(textStatus);
//                                                            // loggingService.info(error);
//                                                        });

                                                    } catch (e) {
                                                        // save logBuffer to ????
                                                    } finally {
                                                        logmsg = null, tttmp = null, _log_msg = null, _message = null, message = null;
                                                    }
                                                    logBuffer = [];
                                                }
                                            }
                                        };
                                    })());
                            }

                            var log;

                            log = function(l, source, message) {
                                // if (level(l) >= logLevel) {
                                return angular.forEach(appenders, function(appender) {
                                    return appender(l, source, message);
                                });
                                // }
                            };
                            return {
                                debug: function(source, message) {
                                    return log('DEBUG', source, message);
                                },
                                info: function(source, message) {
                                    // loggingService.info(arguments.callee.caller);
                                    return log('INFO', source, message);
                                },
                                warning: function(source, message) {
                                    return log('WARNING', source, message);
                                },
                                error: function(source, message) {
                                    return log('ERROR', source, message);
                                },
                                analysis: function(source, message) {
                                    return log('ANALYSIS', source, message);
                                },
                                pdp: function(source, message) {
                                    return log('PDP', source, message);
                                }
                            };
                        }
                    ]
                };
            }
        ]);

        commonServices.provider('eventBusService', [

            function() {
                var _maxEvents;

                _maxEvents = 10;
                return {
                    maxEvent: function(n) {
                        return _maxEvents = n;
                    },
                    $get: [
                        '$rootScope', 'circularListService',
                        function($rootScope, circularListService) {
                            var events, subs, unsubscribe;

                            events = circularListService.create(_maxEvents);
                            subs = {};
                            unsubscribe = function(subscriber, name) {
                                if (subs[subscriber] && subs[subscriber][name]) {
                                    subs[subscriber][name]();
                                    delete subs[subscriber][name];
                                }
                                if (subs[subscriber] === {}) {
                                    return delete subs[subscriber];
                                }
                            };
                            return {
                                publish: function(publisher, name, args) {
                                    events.add({
                                        publisher: publisher,
                                        name: name,
                                        args: args
                                    });
                                	
                                    return $rootScope.$broadcast(name, args);
                                },
                                subscribe: function(subscriber, name, listener) {
                                    var sub;
                                    sub = subs[subscriber] || {};
                                    if (typeof sub[name] === "function") {
                                        sub[name]();
                                    }
                                    sub[name] = $rootScope.$on(name, listener);
                                    return subs[subscriber] = sub;
                                },
                                unsubscribe: unsubscribe,
                                unsubscribeAll: function(subscriber) {
                                    var n, _ref, _results;

                                    _ref = subs[subscriber];
                                    _results = [];
                                    for (n in _ref) {
                                        if (!__hasProp.call(_ref, n)) continue;
                                        _results.push(unsubscribe(subscriber, n));
                                    }
                                    return _results;
                                },
                                events: function() {
                                    return events.getAll();
                                },
                                removeeventAll: function() {
                                    return events.removeAll();
                                },
                                subscribers: function() {
                                    var n, s, ss, _ref;

                                    ss = [];
                                    for (s in subs) {
                                        if (!__hasProp.call(subs, s)) continue;
                                        _ref = subs[s];
                                        for (n in _ref) {
                                            if (!__hasProp.call(_ref, n)) continue;
                                            ss.push({
                                                subscriber: s,
                                                name: n
                                            });
                                        }
                                    }
                                    return ss;
                                }
                            };
                        }
                    ]
                };
            }
        ]);

        commonServices.provider('controllerLoader', [
            '$controllerProvider',
            function($controllerProvider) {
                return {
                    $get: [
                        '$q', '$rootScope', '$controller',
                        function($q, $rootScope, $controller) {
                            return {
                                load: function(name, url) {
                                    var defer;

                                    defer = $q.defer();
                                    require([url], function(controller) {
                                        var ctrl, index;

                                        if (angular.isArray(controller)) {
                                            index = controller.indexOf('$scope');
                                            if (index !== -1) {
                                                ctrl = controller[controller.length - 1];
                                                controller[controller.length - 1] = function() {
                                                    var args, scope;

                                                    args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
                                                    scope = args[index];
                                                    scope._controllerName = name;
                                                    scope.controllerName = name;
                                                    return ctrl.apply(null, args);
                                                };
                                            }
                                        }
                                        $controllerProvider.register(name, controller);
                                        defer.resolve();
                                        return $rootScope.$apply();
                                    });
                                    return defer.promise;
                                }
                            };
                        }
                    ]
                };
            }
        ]);

        commonServices.provider('httpService', [

            function() {
                var config, _timeout;

                _timeout = 60000;
                config = function(method, url, options, loggingService, ctidService) {
                    var _tmp_data = null;
                    if (options != null && options["data"] != null) {
                        _tmp_data = options["data"];
                    }
                    var _msg = {
                        "msg": {
                            "requestinfo": {
                                "reqparam": _tmp_data,
                                "requesturl": url
                            }
                        }
                    };

                    loggingService.analysis(eventType.REQUEST_AJAX + ' ' + url, _msg);
                    _msg = null;
                    _tmp_data = null;

                    var c;

                    c = {
                        'method': method,
                        'url': url,
                        'timeout': _timeout
                    };

                    // put ctid into request header
                    
                        options = {};
                    

                    return jQuery.extend(c, options);
                };
                return {
                    timeout: function(n) {
                        return _timeout = n;
                    },
                    $get: [
                        '$http', 'loggingService', 'ctidService', '$location',
                        function($http, loggingService, ctidService, $location) {
                            return {
                            	css: function(url) {
                                    if(!url || url.length === 0){
                    					throw new Error('argument "path" is required !');
                    				}
                    				var head = document.getElementsByTagName('head')[0];
                    		        var link = document.createElement('link');
                    		        link.href = url;
                    		        link.rel = 'stylesheet';
                    		        link.type = 'text/css';
                    		        console.log("Loaded CSS "+url);
                                    return head.appendChild(link);;
                                },
                                get: function(url, options) {
                                    if (options == null) {
                                        options = {};
                                    }
                                    return $http(config('GET', url, options, loggingService, ctidService)).success(function(data){
                                    	if (data.redirect) {
 		                				   window.location.href = data.redirect;
 		                				}
                             			if(data == '4006'){
                             				var url = window.location.href;
                             				window.location.href = data.data+"?redirect_uri="+encodeURIComponent(url);
                             			}else if(data.code == '4009'){
                             				var url = window.location.href;
                             				window.location.href = data.data+"?redirect_uri="+encodeURIComponent(url);
                                     	}
                                    });
                                },
                                put: function(url, options) {
                                    if (options == null) {
                                        options = {};
                                    }
                                    return $http(config('PUT', url, options, loggingService, ctidService));
                                },
                                post: function(url, options) {
                                    if (options == null) {
                                        options = {};
                                    }
                                    
                                    //options.token = $location.search()["token"];
                                    
                                    var random = '1'+Math.random();
                                    random = random.replace('.','');
                                    
                                    return $.ajax( {    
                            		    url:url+"?random="+random,
                            		    data:options,    
                            		    type:'POST', 
                            		    contentType:'application/x-www-form-urlencoded',
                            		    timeout:60000,
                            		    dataType:'json'
                            		}).success(function(data){
                            			
                            			if (data.redirect) {
		                				   window.location.href = data.redirect;
		                				}
                            			if(data == '4006'){
                            				/*
                            				var login = function(){
                            					var url = window.location.href;
                                				window.location.href = data.data+"?redirect_uri="+encodeURIComponent(url);
                            				}
                            				setTimeout("login()",1000);
                            				*/
                            				$("body").html("褰撳墠鐧诲綍宸辫秴鏃讹紝姝ｅ湪杩斿洖鐧诲綍椤甸潰...")
                            				var url = window.location.href;
                            				window.location.href = data.data+"?redirect_uri="+encodeURIComponent(url);
                            			}else if(data.code == '4009'){
                            				/*
                                    		var url = window.location.href;
                                    		var getUrlValue = function(name) {
                                			   var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
                                			   var r = window.location.search.substr(1).match(reg);
                                			   if (r!=null) return (r[2]); return null;
                                			}
                                    		var removeStr = "code="+getUrlValue("code");
                                    		url = url.replace(removeStr,"");
                                    		url = url.replace("code","old");
                                    		var userType = "teacher";
                                    		
                                    		var isCompany = url.indexOf("CompanyPortal");
                                    		if(isCompany > -1){
                                    			userType = "company";
                                    		}
                                    		
                                    		window.location.href = "https://passport.sjedu.cn/app/"+userType+"?redirect_uri="+encodeURIComponent(url);
                                    		*/
                                    	}
                                    });
                                    
                                    //return $http(config('POST', url+"?random="+random, options, loggingService, ctidService));
                                },
                                postForm: function(url, formId) {
                                    var forParam = $(formId).serialize();
                                	
                                    var random = '1'+Math.random();
                                    random = random.replace('.','');
                                    
                                    return $.ajax( {    
                            		    url:url+"?random="+random,
                            		    data:forParam,    
                            		    type:'post',    
                            		    cache:false,    
                            		    dataType:'json'
                            		});
                                    
                                    //return $http(config('POST', url+"?random="+random, options, loggingService, ctidService));
                                },
                                jsonp: function(url, options) {
                                    if (options == null) {
                                        options = {};
                                    }
                                    options['params'] = {
                                        'callback': 'JSON_CALLBACK'
                                    };
                                    return $http(config('JSONP', url, options, loggingService, ctidService));
                                }
                            };
                        }
                    ]
                };
            }
        ]);

        commonServices.provider('appPartService', [
            '$provide',
            function($provide) {
                var appParts, configs, params;

                appParts = {};
                configs = {};
                params = {};
                $provide.decorator('$controller', [
                    '$delegate',
                    function($delegate) {
                        return function(controller, locals) {
                            if (typeof controller === 'string') {
                                locals.controllerName = controller;
                            }
                            if (configs[controller]) {
                                locals.config = configs[controller];
                            }
                            if (params[controller]) {
                                locals.params = params[controller];
                            }
                            return $delegate(controller, locals);
                        };
                    }
                ]);
                return {
                    $get: [
                        'httpService', 'controllerLoader', 'eventBusService', 'loggingService',
                        function(httpService, controllerLoader, eventBusService, loggingService) {
                            return {
                                load: function(url, param, randomValue, reload) {
                                    if (param == null) {
                                        param = {};
                                    }
                                    if (reload == null) {
                                        reload = false;
                                    }
                                    if (appParts[url] && !reload) {
                                        params[appParts[url].name] = param;
                                        return appParts[url];
                                    } else {

                                        var msg = {
                                            "lap": {
                                                "clientapinfo": {
                                                    "sysid": initConfig.logConfig.systemName,
                                                    "appartconfig": url
                                                }
                                            }
                                        };
                                        //EVENT
                                        loggingService.analysis(eventType.LOAD_APPART_CONFIG, msg);
                                        msg = null;

                                        return appParts[url] = httpService.get(url).then(function(resp) {

                                            var msg = {
                                                "lap": {
                                                    "clientapinfo": {
                                                        "sysid": initConfig.logConfig.systemName,
                                                        "appartnm": resp.data.name,
                                                        "appartconfig": url
                                                    }
                                                }
                                            };
                                            //EVENT
                                            loggingService.analysis(eventType.LOADED_APPART_CONFIG + ' ' + resp.data.name, msg);
                                            msg = null;

                                            var _controllerUrl = resp.data.controllerUrl;
                                            if (resp.data.config) {
                                                configs[resp.data.name] = resp.data.config;
                                            }
                                            params[resp.data.name] = param;
                                            _controllerUrl = (randomValue ? appendUrlParams(_controllerUrl, randomValue) : _controllerUrl);
                                            loggingService.debug('[appPart] _controllerUrl: ', _controllerUrl);

                                            var msg = {
                                                "lap": {
                                                    "clientapinfo": {
                                                        "sysid": initConfig.logConfig.systemName,
                                                        "appartnm": resp.data.name,
                                                        "appartconfig": url,
                                                        "appartcontroller": _controllerUrl

                                                    }
                                                }
                                            };
                                            //EVENT
                                            loggingService.analysis(eventType.LOAD_APPART_CONTROLLER + ' ' + resp.data.name, msg);
                                            msg = null;

                                            return controllerLoader.load(resp.data.name, _controllerUrl).then(function() {


                                                var msg = {
                                                    "lap": {
                                                        "clientapinfo": {
                                                            "sysid": initConfig.logConfig.systemName,
                                                            "appartnm": resp.data.name,
                                                            "appartconfig": url,
                                                            "appartcontroller": _controllerUrl

                                                        }
                                                    }
                                                };
                                                
                                                
                                                //EVENT
                                                loggingService.analysis(eventType.LOADED_APPART_CONTROLLER + ' ' + resp.data.name, msg);
                                                msg = null;

                                                eventBusService.publish('httpService', 'appPart.loaded', resp.data.name);
                                                return resp.data;
                                            });
                                        }, function(error) {
                                            loggingService.error('url: ' + url, error.message);
                                            return '';
                                        });
                                    }
                                }
                            };
                        }
                    ]
                };
            }
        ]);

        commonServices.provider('authenticationService', [

            function() {
                var userInfo;

                userInfo = void 0;
                return {
                    $get: [
                        'httpService',
                        function(httpService) {
                            return {
                                register: function(url) {
                                    return httpService.get(url);
                                },
                                login: function(url, id, password, success, failure) {
                                    return httpService.get(url, {
                                        params: {
                                            id: id,
                                            password: password
                                        }
                                    }).then((function(resp) {
                                        userInfo = resp.data.userInfo ? resp.data.userInfo : void 0;
                                        return success(userInfo);
                                    }), (function(resp) {
                                        return failure(resp);
                                    }));
                                },
                                logout: function(url) {
                                    return userInfo = void 0;
                                },
                                userInfo: function() {
                                    return userInfo;
                                }
                            };
                        }
                    ]
                };
            }
        ]);

        commonServices.provider('utilService', [

            function() {
                return {
                    $get: [

                        function() {
                            return {
                                appendUrlParams: function(oriUrl, param) {
                                    return appendUrlParams(oriUrl, param);
                                }
                            };
                        }
                    ]
                };
            }
        ]);
        
        
        commonDirectives = angular.module('commonDirectives', ['commonServices']);

        commonDirectives.directive('tBlock', function() {
            return {
                restrict: 'CA',
                link: function(scope, element, attributes) {
                    var isBlock;
                    scope.$watch(attributes.tBlock, function(value) {
                        isBlock = value;
                        if ((isBlock === 'true') || (isBlock === true)) {
                            element.block({
                                message: '<div><img id="displayBox" src="assets/img/loading.gif"></div>',
                                css: {
                                    width: '100%',
                                    top: ($(window).height() - 400) / 2 + 'px',
                                    left: ($(window).width() - 400) / 2 + 'px',
                                    right: '10px',
                                    border: 'none',
                                    padding: '5px',
                                    backgroundColor: '#fff',
                                    opacity: 0.7,
                                    color: '#fff'
                                },
                                overlayCSS: {
                                    backgroundColor: '#fff'
                                }
                            });
                        } else {
                            element.unblock();
                        }
                    });
                }
            };
        });
        
        commonDirectives.directive('starlevel', ['$compile', function($compile){
            return {
                restrict: 'A',
                link: function(scope, ele, attr) {
                	var level = attr['starlevel'];
                	var star = "";
                	for(var i=0;i<level;i++){
                		star += '<span style="color:#f0ad4e" class="glyphicon glyphicon-star"></span>';
                	}
                	for(var i=0;i<5-level;i++){
                		star += '<span style="color:#777" class="glyphicon glyphicon-star"></span>';
                	}
                	ele[0].innerHTML = star;
                }
            };
        }]);
        
        commonDirectives.directive('placeholder', ['$compile', function($compile){
            return {
                restrict: 'A',
                link: function(scope, ele, attr) {
                	
                    var input = document.createElement('input');
                    var isSupportPlaceholder = 'placeholder' in input;
                    if (!isSupportPlaceholder && "password" != ele[0].type) {
                    	//var userName = cookieService.get("userName");
                    	if("none"==attr['ieshow']){
                    		ele.on('focus', function(){
                    			if(ele[0].value == ""){
                            		ele[0].value = attr['placeholder'];
                            	}
                            }).on('blur', function(){
                            	if(ele[0].value == ""){
                            		ele[0].value = attr['placeholder'];
                            	}
                            }).on('click', function(){
                            	if(ele[0].value == attr['placeholder']){
                            		ele[0].value = "";
                            	}
                            });
                    		ele.focus();
                    	}else{
                    		var fakePlaceholder = angular.element('<span class="placeholder">' + attr['placeholder'] + '</span>');
                            fakePlaceholder.on('click', function(e){
                                e.stopPropagation();
                                ele.focus();
                            });
                            ele.before(fakePlaceholder);
                            $compile(fakePlaceholder)(scope);
                    	}
                       
                    }
                }
            };
        }]);

        commonDirectives.directive('tAcc', ['$document', '$location', '$window',
            function($document, $location, $window) {
                return {
                    restrict: 'CA',
                    link: function(scope, element, attrs) {
                        element.bind('click', function(event) {
                            var last = jQuery('li.open', element.parent().parent()),
                                sub = element.siblings('.sub');

                            jQuery('.arrow', last).removeClass("open");
                            jQuery('.sub', last).slideUp(200, function() {
                                last.removeClass('active');
                            });

                            if (sub.is(":visible")) {
                                jQuery('.arrow', element).removeClass("open");
                                element.parent().removeClass("open");
                                sub.slideUp(200, function() {
                                    element.parent().removeClass('active');
                                });
                            } else {
                                jQuery('.arrow', element).addClass("open");
                                element.parent().addClass("open");
                                sub.slideDown(200, function() {
                                    element.parent().addClass('active');
                                });
                            }
                        });
                    }
                };
            }
        ]);

        commonDirectives.directive('tSortable', function() {
            return {
                restrict: 'CA',
                link: function(scope, element, attrs) {
                    element.sortable({
                        connectWith: '.sortable',
                        iframeFix: false,
                        items: 'div.widget',
                        opacity: 0.8,
                        helper: 'original',
                        //revert: true,
                        forceHelperSize: true,
                        placeholder: 'sortable-box-placeholder round-all',
                        forcePlaceholderSize: true,
                        tolerance: 'pointer',

                        handle: '.handle',
                        revert: 100
                    });
                }
            };
        });

        commonDirectives.directive('tWidget', [

            function() {
                return {
                    restrict: 'A',
                    link: function(scope, element, attrs) {
                        var $tools = element.find('.tools');
                        $tools.find('.fa-remove').bind('click', function() {
                            element.parent().remove();
                        });

                        $tools.find('.fa-chevron-up, .fa-chevron-down').bind('click', function() {
                            var el = element.find('.widget-body');
                            var $this = jQuery(this);
                            if ($this.hasClass('fa-chevron-up')) {
                                $this.removeClass('fa-chevron-up').addClass('fa-chevron-down');
                                el.slideUp(100);
                            } else {
                                $this.removeClass('fa-chevron-down').addClass('fa-chevron-up');
                                el.slideDown(100);
                            }
                        });
                        
                        scope.$watch(attrs.isClose,function(newVal,oldVal){
	                        if(newVal == undefined){
	                    		return;
	                    	}
	                    	if(newVal == oldVal){
	                    		return;
	                    	}else{
	                            var el = element.find('.widget-body');
	                            var $this = $tools.find('.fa-chevron-up, .fa-chevron-down');
	                            if(attrs.isClose){
		                            if ($this.hasClass('fa-chevron-up')) {
		                                $this.removeClass('fa-chevron-up').addClass('fa-chevron-down');
		                                el.slideUp(100);
		                            } else {
		                                $this.removeClass('fa-chevron-down').addClass('fa-chevron-up');
		                                el.slideDown(100);
		                            }
	                            }
	                    	}
	                    });
                    }
                };
            }
        ]);

        commonDirectives.directive('accordion', [

            function() {
                return {
                    restrict: 'C',
                    link: function(scope, element, attrs) {
                        element.on('show hide', function(event) {
                            $(event.target).siblings(".accordion-heading").find(".accordion-toggle i").toggleClass("icon-chevron-up icon-chevron-down");
                        });
                    }
                };
            }
        ]);

        commonDirectives.directive('appPart', [
            '$templateCache', 'httpService', 'appPartService', 'loggingService',
            function($templateCache, httpService, appPartService, loggingService) {
                var params;

                params = function(url) {
                    var decode, match, param, plus, query, urlParams;
                    urlParams = {};
                    if (url) {
                        plus = /\+/g;
                        param = /([^&=]+)=?([^&]*)/g;
                        decode = function(s) {
                            return decodeURIComponent(s.replace(plus, " "));
                        };
                        query = url.split("?")[1];

                        if (query !== null) {
                            while (match = param.exec(query)) {
                                urlParams[decode(match[1])] = decode(match[2]);
                            }
                        }
                    }
                    return urlParams;
                };
                return {
                    restrict: 'A',
                    scope: {
                        src: '=',
                        random: '@',
                        loadingeffect: '@'
                    },
                    template: '<div ng-include src="viewUrl"></div>',
                    link: function(scope, element, attributes) {
                        return scope.$watch('src', function(src) {

                            // Default Scope Loading effect Setting
                            if ((typeof scope.loadingeffect == 'undefined')) {
                                scope.loadingeffect = true;
                                loggingService.debug('Use default Scope Loading effect Setting:', scope.loadingeffect);
                            } else {
                                loggingService.debug('Use Config Scope Loading effect Setting:', scope.loadingeffect);
                            }

                            var _src = src,
                                random = (scope.random === "true"),
                                randomValue;
                            if (random) {
                                _src = appendUrlParams(_src, randomValue = "r=" + Math.random());
                            }

                            if (!_src) {
                                scope.loadingeffect = false;
                                loggingService.warning('[appPart] _configUrl is empty! ', '_configUrl: ' + _src);
                                return null;
                            } else {
                                loggingService.debug('[appPart] _configUrl: ', _src);
                            }

                            return appPartService.load(_src, params(_src), randomValue).then(function(appPart) {
                                var _viewUrl = appendUrlParams(appPart.viewUrl, initConfig.versionStr);
                                if (random) {
                                    _viewUrl = appendUrlParams(_viewUrl, randomValue);
                                }

                                if ((typeof _viewUrl != 'undefined') && _viewUrl) {
                                    loggingService.debug('[appPart] _viewUrl: ', _viewUrl);

                                    var msg = {
                                        "lap": {
                                            "clientapinfo": {
                                                "sysid": initConfig.logConfig.systemName,
                                                "appartnm": appPart.name,
                                                "appartview": _viewUrl
                                            }
                                        }
                                    };
                                    //EVENT
                                    loggingService.analysis(eventType.LOAD_APPART_VIEW + ' ' + appPart.name, msg);
                                    msg = null;

                                    // performance issue                                    
                                    if (typeof($templateCache.get('_arrViewUrl_')) != 'undefined' && $templateCache.get('_arrViewUrl_')) {
                                        var arrViewUrl = [];
                                        arrViewUrl = $templateCache.get('_arrViewUrl_');
                                        // loggingService.debug("arrViewUrl size from templateCache:", arrViewUrl.length);
                                        for (index = 0; index < arrViewUrl.length; ++index) {
                                            $templateCache.remove(arrViewUrl[index]);
                                            // loggingService.debug('remove viewUrl: [' + index + '] ', arrViewUrl[index]);
                                        }
                                        arrViewUrl = null;
                                    }

                                    //performance enhance
                                    if (typeof($templateCache.get(_viewUrl)) != 'undefined' && $templateCache.get(_viewUrl)) {
                                        // loggingService.debug('Getting viewUrl from TemplateCache.', _viewUrl);
                                        // loggingService.debug('[' + _viewUrl + '] templateCache Info:', JSON.stringify($templateCache.info()));

                                        var msg = {
                                            "lap": {
                                                "clientapinfo": {
                                                    "sysid": initConfig.logConfig.systemName,
                                                    "appartnm": appPart.name,
                                                    "appartview": _viewUrl
                                                }
                                            }
                                        };
                                        //EVENT
                                        loggingService.analysis(eventType.LOADED_APPART_VIEW_FROM_CACHE + ' ' + appPart.name, msg);
                                        msg = null;

                                        scope.loadingeffect = false;
                                        return scope.viewUrl = _viewUrl;
                                    } else {
                                        return httpService.get(_viewUrl).then(function(resp) {

                                            var msg = {
                                                "lap": {
                                                    "clientapinfo": {
                                                        "sysid": initConfig.logConfig.systemName,
                                                        "appartnm": appPart.name,
                                                        "appartview": _viewUrl
                                                    }
                                                }
                                            };
                                            //EVENT
                                            loggingService.analysis(eventType.LOADED_APPART_VIEW + ' ' + appPart.name, msg);
                                            msg = null;

                                            $templateCache.put(_viewUrl, "<div id='" + appPart.name + "' ng-controller='" + appPart.name + "'>" + resp.data + "</div>");

                                            // performance issue
                                            var _arrViewUrl = [_viewUrl];
                                            // loggingService.debug("arrViewUrl size:", _arrViewUrl.length);
                                            $templateCache.put('_arrViewUrl_', _arrViewUrl);
                                            _arrViewUrl = null;

                                            // loggingService.debug('[' + _viewUrl + '] templateCache Info:', JSON.stringify($templateCache.info()));
                                            // loggingService.debug('[' + _viewUrl + '] loadingeffect:', scope.loadingeffect);

                                            scope.loadingeffect = false;
                                            return scope.viewUrl = _viewUrl;
                                        }, function(error) {
                                            loggingService.error('_viewUrl: ' + _viewUrl, error.message);
                                            scope.loadingeffect = false;
                                            return '';
                                        });
                                    }
                                } else {
                                    scope.loadingeffect = false;
                                    // loggingService.warning('[appPart] viewUrl is empty! ', '_viewUrl: ' + _viewUrl);
                                }
                            });
                        });
                    }
                };
            }
        ]);

        commonDirectives.directive('cid', [

            function() {
                return {
                    require: 'ngModel',
                    restrict: 'A',
                    link: function(scope, element, attribute, ngModel) {
                        var validate;

                        validate = function(value) {
                            return value.length === 10;
                        };
                        return ngModel.$parsers.unshift(function(value) {
                            ngModel.$setValidity('cid', validate(value));
                            return value;
                        });
                    }
                };
            }
        ]);

        commonDirectives.directive('rocid', [

            function() {
                return {
                    require: 'ngModel',
                    restrict: 'A',
                    link: function(scope, element, attribute, ngModel) {
                        var calculate, mapping, multipliers, validate;

                        mapping = {
                            A: '10',
                            B: '11',
                            C: '12',
                            D: '13',
                            E: '14',
                            F: '15',
                            G: '16',
                            H: '17',
                            I: '34',
                            J: '18',
                            K: '19',
                            L: '20',
                            M: '21',
                            N: '22',
                            O: '35',
                            P: '23',
                            Q: '24',
                            R: '25',
                            S: '26',
                            T: '27',
                            U: '28',
                            V: '29',
                            W: '32',
                            X: '30',
                            Z: '33'
                        };
                        multipliers = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
                        calculate = function(value) {
                            var d, i, sum, _i, _len;

                            sum = 0;
                            for (i = _i = 0, _len = value.length; _i < _len; i = ++_i) {
                                d = value[i];
                                sum += parseInt(d) * multipliers[i];
                            }
                            return sum % 10 === 0;
                        };
                        validate = function(value) {
                            return value.length === 10 && mapping[value[0]] && calculate(mapping[value[0]] + value.substring(1));
                        };
                        return ngModel.$parsers.unshift(function(value) {
                            ngModel.$setValidity('rocid', validate(value));
                            return value;
                        });
                    }
                };
            }
        ]);

        commonDirectives.directive('passwd', [

            function() {
                return {
                    require: 'ngModel',
                    restrict: 'A',
                    link: function(scope, element, attributes, ngModel) {
                        var validate;

                        validate = function(value) {
                            return value && value.length >= 5 && (/[A-z]/.test(value) || /\d/.test(value));
                        };
                        return ngModel.$parsers.unshift(function(value) {
                            ngModel.$setValidity('passwd', validate(value));
                            return value;
                        });
                    }
                };
            }
        ]);

        commonDirectives.directive('regex', [

            function() {
                return {
                    require: 'ngModel',
                    restrict: 'A',
                    link: function(scope, element, attributes, ngModel) {
                        var regex;

                        regex = new RegExp(attributes.regex, attributes.regexFlags || '');
                        return ngModel.$parsers.unshift(function(value) {
                            ngModel.$setValidity('regex', regex.test(value));
                            return value;
                        });
                    }
                };
            }
        ]);

        commonDirectives.directive('validator', [

            function() {
                return {
                    require: 'ngModel',
                    restrict: 'A',
                    link: function(scope, element, attributes, ngModel) {
                        return ngModel.$parsers.unshift(function(value) {
                            var valid;

                            valid = scope[attributes.validator](value);
                            ngModel.$setValidity((attributes != null ? attributes.validatorName : void 0) || 'validator', valid);
                            return value;
                        });
                    }
                };
            }
        ]);

        commonDirectives.directive('formatter', [

            function() {
                return {
                    require: 'ngModel',
                    restrict: 'A',
                    link: function(scope, element, attributes, ngModel) {
                        return ngModel.$formatters.unshift(function(value) {
                            var v;

                            v = scope[attributes.formatter](value);
                            return v;
                        });
                    }
                };
            }
        ]);
        

})(window.angular);