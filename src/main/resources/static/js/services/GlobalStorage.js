angular.module('app.services.GlobalStorage', []).factory('GlobalStorage', ['$window', function ($window) {
    return {
        set: function(key, value) {
            $window.localStorage[key] = value;
        },
        get: function(key, defaultValue) {
            return $window.localStorage[key] || defaultValue;
        },
        setJSONObj: function(key, value) {
            $window.localStorage[key] = angular.toJson(value);
        },
        getJSONObj: function(key) {
            return angular.fromJson($window.localStorage[key] || '{}');
        },
        pushObject: function(key,obj){
            var objarr = [];
            try{
                objarr = angular.fromJson($window.localStorage[key]) || [];
                var ishave = false;
                angular.forEach(objarr,function(b){
                    if(b.id == obj.id){
                        ishave = true;
                        return;
                    }
                })
                if(!ishave)objarr.push(obj);
            }catch(e){
                objarr.push(obj);
            }
            $window.localStorage[key] = angular.toJson(objarr);
        },
        pullObject: function(key){
            return angular.fromJson($window.localStorage[key] || '[]');
        },
        clearObject:function(key){
            $window.localStorage.removeItem(key);
        },
        removeObject:function(key,obj){
            var objarr = angular.fromJson($window.localStorage[key] || '[]');
            angular.forEach(objarr,function(a){
                if(a.id == obj.id){
                    objarr.splice(objarr.indexOf(a),1);
                }
            })
            $window.localStorage[key] = angular.toJson(objarr);
        }
    }
}])