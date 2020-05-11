'use strict';

(function (win) {
    var baseUrl;
    if (document.body.getAttribute("origin")) {
        baseUrl = document.body.getAttribute("origin");
    } else {
        baseUrl = './';
    }
    var cssUrl;
    if (baseUrl === "./") {
        cssUrl = baseUrl + "../"
    } else {
        cssUrl = baseUrl;
    }

    require.config({
        waitSeconds: 200,
        map: {
            '*': {
                'css': baseUrl+'js/sqi-cdn/require.css.min.js'
            }
        },
        paths: {
            'angular': baseUrl+'js/sqi-cdn/angular.min',
            'angular-ui-router': baseUrl+'js/sqi-cdn/angular-ui-router.min',
            'angular-cookies': baseUrl+'js/sqi-cdn/angular-cookies.min',
            'angular-animate': baseUrl+'js/sqi-cdn/angular-animate.min',
            'angular-translate': baseUrl+'js/sqi-cdn/angular-translate.min',
            'angular-sanitize': baseUrl+'js/sqi-cdn/angular-sanitize.min',
            'ngStorage': baseUrl+'js/sqi-cdn/ngStorage.min',
            'bootstrap': baseUrl+'js/sqi-cdn/bootstrap.min',
            'jquery': baseUrl+'js/sqi-cdn/jquery.min',
            'jquery-ui': baseUrl+'js/sqi-cdn/jquery-ui.min',
            'ui-bootstrap': baseUrl+'js/sqi-cdn/ui-bootstrap-tpls.min',
            'ui-load': baseUrl+'js/sqi-cdn/ui-load.min',
            'ui-jq': baseUrl+'js/sqi-cdn/ui-jq.min',
            'ocLazyLoad': baseUrl+'js/sqi-cdn/ocLazyLoad.min',
            'moment': baseUrl+'js/sqi-cdn/moment.min',
            'directives': baseUrl+'js/sqi-cdn/common-directives.min',
            'screenfull': baseUrl+'js/sqi-cdn/screenfull.min',
            'highcharts-ng':baseUrl+'js/sqi-cdn/highcharts-ng',
            'highcharts':baseUrl+'js/sqi-cdn/highcharts',
            'ng-table': baseUrl+'js/angular-ngtable/ng-table.min',
            'ng-toaster': baseUrl+'js/angularjs-toaster/toaster',
            'webuipopover':baseUrl+'js/webui-popover/jquery.webui-popover',
            'app': baseUrl + 'js/app',
            'interceptor': baseUrl + 'js/interceptor',
            'filter': baseUrl + 'js/filters',
            'select': baseUrl + 'js/select2.min',
            'bootstrap-select': baseUrl + 'js/bootstrap-select.min',
            'diff-match-patch': baseUrl + 'js/diff_match_patch',
            'app.diff':baseUrl+'js/diffSmoke',
            'ui.codemirror':baseUrl+'js/angular-ui-codemirror/ui-codemirror',
            'ui-select': baseUrl + 'js/ui-select',
            'app.controllers.mainController':baseUrl+'js/controllers/MainCtrl',
            'app.controllers.pipelineController':baseUrl+'js/controllers/PipelineCtrl',
            'app.controllers.recommendController':baseUrl+'js/controllers/RecommendCtrl',
            'app.controllers.expandController':baseUrl+'js/controllers/ExpandCtrl',
            'app.controllers.RegController':baseUrl+'js/controllers/RegCtrl',
            'app.controllers.ReportController':baseUrl+'js/controllers/ReportCtrl',
            'app.controllers.RootCtrl':baseUrl+'js/controllers/RootCtrl',
            'app.controllers.AICaseGeneratorController':baseUrl+'js/controllers/AICaseGeneratorCtrl',
            'app.controllers.subController':baseUrl+'js/controllers/SubCtrl',
            'app.services.GlobalStorage':baseUrl+'js/services/GlobalStorage',
            'app.services.UserService':baseUrl+'js/services/UserService',
            'app.services.ZhiziService':baseUrl+'js/services/ZhiziService'
        },
        shim: {
            "angular": {
                exports: "angular"
            },
            'app': {
                deps: ['bootstrap', 'angular-ui-router', 'angular-cookies', 'angular-animate',
                    'angular-translate', 'angular-sanitize', 'ngStorage', 'ng-toaster','ng-table','ui-bootstrap', 'ocLazyLoad', 'ui-load', 'ui-jq',
                    'interceptor', 'webuipopover','app.diff','ui.codemirror', 'filter', 'directives', 'jquery-ui'
                    //,'highcharts'
                    ,'highcharts-ng'
                    //'services', 'controllers'
                    //,'robot-js','css!robot-css'
                    ,'app.controllers.mainController'
                    ,'app.controllers.AICaseGeneratorController'
                    ,'app.controllers.pipelineController'
                    ,'app.controllers.expandController'
                    ,'app.controllers.recommendController'
                    ,'app.controllers.RegController'
                    ,'app.controllers.ReportController'
                    ,'app.controllers.RootCtrl'
                    ,'app.controllers.subController',
                    'app.services.GlobalStorage',
                    'app.services.UserService',
                    'app.services.ZhiziService'
                    ,'css!' + cssUrl + 'css/test','css!' + cssUrl + 'css/app'
                    ,'css!' + cssUrl + 'css/select2.min'
                    ,'select','bootstrap-select','css!' + cssUrl + 'css/bootstrap-select.min'
                    ,'diff-match-patch', 'ui-select', 'css!' + cssUrl + 'css/ui-select'
                    ,'css!'+cssUrl+'js/angular-ngtable/ng-table.min'
                    ,'css!'+cssUrl+'css/diffview'
                    ,'css!'+baseUrl+'js/angularjs-toaster/toaster'
                    ,'css!'+baseUrl+'js/webui-popover/jquery.webui-popover'
                ]
            },
            'controllers': {
                deps: ['services']
            },
            'directives':{
                deps:['screenfull']
            }
        }
    });
    require(['app'],
        function () {
            angular.bootstrap(document, ['app']);
        });

})(window);


