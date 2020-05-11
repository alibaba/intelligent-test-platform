angular.module('app.interceptor', []).factory('interceptor', ['$q', '$location','$rootScope',
    function ($q, $location,$rootScope) {
        return {
            responseError: function (response) {
                if (response.status === 500 && response.config.url == "api/feuser.htm") {
                    $rootScope.$broadcast('login-system', '系统登录');
                }
                return $q.reject(response);
            },
            response: function (response) {

                return response;
            },
            request: function (config) {

                // //定义基础url，兼容cdn模式和本地模式
                // var baseUrl;
                //
                if (document.body.getAttribute("origin")) {
                    baseUrl = document.body.getAttribute("origin");
                } else {
                    baseUrl = '';
                }

                if (config.url.indexOf('views') >= 0) {
                    config.url = baseUrl + config.url;
                }
                return config;
            },
            requestError: function (config) {
                return $q.reject(config);
            }
        };
    }
]);