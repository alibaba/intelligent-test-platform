angular.module('app.controllers.RootCtrl', ['pascalprecht.translate', 'ngCookies','ngSanitize', 'ui.select'])
    .controller('RootCtrl', ['$rootScope', '$scope', '$translate', '$localStorage', '$window', '$state','$location', '$stateParams', '$timeout','GlobalStorage','UserService','ZhiziService','$compile',
        function ($rootScope, $scope, $translate, $localStorage, $window, $state,$location, $stateParams, $timeout,GlobalStorage,UserService,ZhiziService,$compile) {


            // add 'ie' classes to html
            var isIE = !!navigator.userAgent.match(/MSIE/i);
            isIE && angular.element($window.document.body).addClass('ie');

            $scope.currentLeftMenu = [];
            $scope.globalBuName = "";
            $scope.buInfoMap={};
            $scope.bunames=[];
            $scope.validBuAccess4User=[];
            $scope.userType = "";
            $scope.user={};
            $scope.user.isManager=false;
            $scope.jsList4Wel = ["http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/jquery.easing.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/jquery.magnific-popup.min.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/jquery.sliderPro.min.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/jquery.countTo.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/jquery.stellar.min.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/jquery.waypoints.min.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/jquery.nav.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/owl.carousel.min.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/modernizr.min.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/bootstrap.min.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/smooth-scroll.min.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/isotope.pkgd.min.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/wow.min.js",
                "http://dev.g.alicdn.com/alimama-engineering-fe/zhizi-fe/0.0.47/js/sqi-cdn/custom.js"];
            // config
            $scope.app = {
                name: 'Markov',
                pageTitle:"Markov-引擎测试平台",
                version: '0.0.1',
                color: {
                    primary: '#7266ba',
                    info: '#23b7e5',
                    success: '#27c24c',
                    warning: '#fad733',
                    danger: '#f05050',
                    light: '#e8eff0',
                    dark: '#3a3f51',
                    black: '#1c2b36'
                },
                settings: {
                    themeID: 4,
                    navbarHeaderWhite: 'bg-white',
                    navbarHeaderColor: 'bg-info',
                    navbarCollapseColor: 'bg-white-only',
                    asideColor: 'bg-black',
                    headerFixed: true,
                    asideFixed: false,
                    asideFolded: false
                }
            };
            var basePopSettings = {
                trigger:'hover',
                title:'',
                content:'',
                width:550,
                multi:false,
                closeable:false,
                style:'',
                padding:true
            };


            $scope.toTargetBu = function(){
                var bu = "markov-demo";

                $scope.globalBuName = bu;
                $.ajax({
                    url: 'api/getMenu',
                    dataType: 'json',
                    method: 'GET',
                    async:false,
                    success: function (data) {
                        if (data.success) {
                            var tmp = data.data.buinfo;
                            var res = UserService.getMenu(tmp);
                            if(res.flag=="succ"){
                                $scope.menuIdx = res.menuIndex;
                                $scope.applist = res.applist;
                                $scope.scenariolist = res.scenariolist;
                                $scope.menus = [];
                                $scope.appSecneMap = res.appSecneMap;
                                angular.forEach($scope.menuIdx,function(idx){
                                    angular.forEach(res.finalmenu,function(ms){
                                        if(idx == ms.topName){
                                            $scope.menus.push(ms);
                                        }
                                    })
                                });
                                angular.forEach(res.finalmenu,function(ms){
                                    if($.inArray(ms.topName,$scope.menuIdx) == -1){
                                        $scope.menus.push(ms);
                                    }
                                })

                                var productLineArr = GlobalStorage.getJSONObj("SELF_SELECT_MENU").productLine.split("-");
                                productLineArr.shift();
                                var productLine =productLineArr.join("-");
                                var bussinesid = "";
                                if (typeof($location.search().bussinesid)!="undefined"){
                                    bussinesid = $location.search().bussinesid;
                                    var bussinesidUrl = $location.search().bussinesid;
                                    for (var i in $scope.menus){
                                        if( $scope.menus[i].businessId == bussinesidUrl){
                                            productLine = $scope.menus[i].topName;
                                        }
                                    }
                                }else{
                                    for (var i in $scope.menus){
                                        if( $scope.menus[i].topName == productLine){
                                            $scope.currentLeftMenu = $scope.menus[i].leftMenus;
                                        }
                                    }
                                }

                                if($scope.currentLeftMenu.length == 0){
                                    $scope.currentLeftMenu = $scope.menus[0].leftMenus;
                                }


                            }else{
                                alert($scope.globalBuName+'内容结构为空，请联系管理员进行添加');
                            }
                        }
                    },
                    error: function (xhr) {
                        alert('error:' + JSON.stringify(xhr));
                    }
                });
            }


            //$scope.toTargetBu();

            $scope.displayDetailTip = function($event,data){
                var tipSettings = {
                    content:data
                };
                $($event.target).webuiPopover('destroy').webuiPopover($.extend({},basePopSettings,tipSettings));
            }
            $rootScope.$on('login-system',
                function (event, data) {
                    var host = "127.0.0.1,localhost,daily.taobao.net".indexOf(window.location.hostname) != -1?"login-test.alibaba-inc.com":"login.alibaba-inc.com";
                    var login = 'https://'+host+'/ssoLogin.htm?APP_Name=mmep_dev&BACK_URL='+encodeURIComponent(location.href)+"&CANCEL_CERT=true";
                    $window.location.href = login;
                });
            $scope.applist = {};
            $scope.scenariolist = {};
            if(GlobalStorage.getJSONObj("SELF_SELECT_MENU").product){
                $scope.productInfo = GlobalStorage.getJSONObj("SELF_SELECT_MENU");
            } else {
                $scope.productInfo = {product:"markov",productLine:"1-定向",productModule:"3-UTS",productSubModule:"10-单品"};
                GlobalStorage.setJSONObj("SELF_SELECT_MENU",$scope.productInfo);
            }
            $scope.switchProductLine = function(productLine){
                $scope.appid = $scope.$parent.$stateParams.appid;
                $scope.sceneid = $scope.$parent.$stateParams.sceneid;
                var len = $scope.menus.length;
                for (i=0;i<len;i++){
                    if ($scope.menus[i].topName == productLine){
                        $scope.productInfo.productLine = $scope.menus[i].businessId+"-"+$scope.menus[i].topName;
                        $scope.currentLeftMenu = [];
                        $scope.currentLeftMenu = $scope.menus[i].leftMenus;
                    }
                }
                $scope.currentLeftMenu = UserService.setLeftMenu($scope.currentLeftMenu, $scope.appid, $scope.sceneid);
            }

            if (document.body.getAttribute("origin")) {
                $scope.baseUrl = document.body.getAttribute("origin")
            } else {
                $scope.baseUrl = '';
            }





            // save settings to local storage
            if (angular.isDefined($localStorage.settings)) {
                $scope.app.settings = $localStorage.settings;
            } else {
                $localStorage.settings = $scope.app.settings;
            }
            $scope.$watch('app.settings', function () {
                $localStorage.settings = $scope.app.settings;
            }, true);

            $scope.toTargetPage = function(menu){
                $scope.switchProductLine(menu.topName);
                try{
                    $state.go("app.subpage",{appid:menu.route.split("=")[1]});
                } catch (e) {
                    $state.go("app.subpage",{appid:menu.appid});
                }
                //window.open(window.location.origin+"/"+menu.route);
            }

            $scope.getinBu = function(bu){
                $scope.toTargetBu({"bu":bu,"type":"welcome"});
            }



            $scope.changeLeftMenu = function(appname,scenename = null){
                GlobalStorage.setJSONObj("CurrentCasePage_" + $scope.sceneid, {page:0});
                for (var i in $scope.currentLeftMenu){
                    if ($scope.currentLeftMenu[i].name == appname){
                        $scope.appid = $scope.currentLeftMenu[i].appid;
                        $scope.currentLeftMenu[i].selected = true;
                        $scope.productInfo.productModule = $scope.appid+"-"+appname;
                        if (scenename == null){
                            continue;
                        }
                        for (var j in $scope.currentLeftMenu[i].son){
                            if ($scope.currentLeftMenu[i].son[j].name == scenename){
                                $scope.sceneid = $scope.currentLeftMenu[i].son[j].scenarioId;
                                $scope.currentLeftMenu[i].son[j].selected = true;
                                $scope.productInfo.productSubModule = $scope.sceneid+"-"+scenename;
                                GlobalStorage.setJSONObj("SELF_SELECT_MENU",$scope.productInfo);
                            } else {
                                $scope.currentLeftMenu[i].son[j].selected = false;
                            }
                        }
                    }else{
                        $scope.currentLeftMenu[i].selected = false;
                        for (var j in $scope.currentLeftMenu[i].son){
                            $scope.currentLeftMenu[i].son[j].selected = false;
                        }
                    }
                }
            }

            $scope.loadJs = function(url){
                var script = document.createElement ("script")
                script.type = "text/javascript";
                if (script.readyState){ //IE
                    script.onreadystatechange = function(){
                        if (script.readyState == "loaded" || script.readyState == "complete"){
                            script.onreadystatechange = null;
                            callback();
                        }
                    };
                } else { //Others
                    script.onload = function(){
                        callback();
                    };
                }
                script.src = url;
                document.getElementsByTagName("head")[0].appendChild(script);
            }

            $scope.loadJsList = function(){
                var _index = 0;
                var arr = $scope.jsList4Wel;
                if( _index < arr.length ) {
                    $scope.loadJs(arr[_index], function(){
                        _index ++;
                        $scope.loadJs(arr[_index]);
                    })
                }
            }
        }
    ]);