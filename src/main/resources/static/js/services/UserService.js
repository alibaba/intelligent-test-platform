angular.module('app.services.UserService', [])
    .factory('UserService', ['$http',function($http) {

    var me = this;
    function getUser(callback){
        if (me.currentUser){
            callback(me.currentUser);
        }
    }

    function setLeftMenu(menu, appid, sceneid){
        for (var i in menu){
            if (menu[i].appid == appid){
                menu[i].selected = true;
                for (var j in menu[i].son){
                    if (sceneid == "" || sceneid == undefined){
                        menu[i].son[j].selected = false;
                    }else if (menu[i].son[j].scenarioId == sceneid){
                        menu[i].son[j].selected = true;
                    }
                }
            } else {
                menu[i].selected = false;
                for (var j in menu[i].son){
                    menu[i].son[j].selected = false;
                }
            }
        }
        return menu;
    }

    // 后端MenuController.buildMenus也实现了此方法，之后可以直接访问后端接口拿到此数据
    function getMenu(res){
        var tmp = res["menu"];
        var curBuId = res["buid"];
        var finalmenu = [];
        var resOfapplist = {};
        var resOfscenariolist = {};
        var menuIndex = [];

        if(tmp=="[]"){
            return {
                "finalmenu":finalmenu,
                "applist":resOfapplist,
                "scenariolist":resOfscenariolist,
                "appSecneMap":appSecneMap,
                "menuIndex":menuIndex,
                "flag":"failed1"
            }
        }

        var appSecneMap = res["appSecneMap"];

        var len = tmp.length;
        for (x = 0; x < len; x++) {
            var current = {};
            current["topName"] = tmp[x]["businessName"];
            current["businessId"] = tmp[x]["businessId"];
            menuIndex.push(current["topName"]);
            var lmenu = [];
            var defapp = '';
            if (tmp[x]["appMenuList"]==null){
                return {
                    "finalmenu":finalmenu,
                    "applist":resOfapplist,
                    "scenariolist":resOfscenariolist,
                    "appSecneMap":appSecneMap,
                    "menuIndex":menuIndex,
                    "flag":"failed2"
                }
            }
            var leny = tmp[x]["appMenuList"].length;
            for (y = 0; y < leny; y++) {
                var sonlist = tmp[x]["appMenuList"][y]["scenarioMenuList"];
                var appid = tmp[x]["appMenuList"][y]["appId"];
                if (defapp == ''){
                    defapp = appid;
                }
                resOfapplist[appid] = tmp[x]["appMenuList"][y]["appName"];
                for (var z in sonlist){
                    sonlist[z]["route"] = "#!/app/index?appid=" + appid + "&sceneid=" + sonlist[z]["scenarioId"]+ "&buid=" + curBuId+"&bussinesid="+current["businessId"];
                    sonlist[z]["name"] = sonlist[z]["scenarioName"];
                    sonlist[z]["selected"] = false;
                    resOfscenariolist[sonlist[z]["scenarioId"]] = sonlist[z]["scenarioName"];
                }
                lmenu.push({"name":tmp[x]["appMenuList"][y]["appName"],
                    "route":"#!/app/index?appid=" + appid+"&sceneid="+appSecneMap[defapp]+ "&buid=" + curBuId+"&bussinesid="+current["businessId"],
                    "son":sonlist,
                    "selected":false,
                    "appid":appid
                })
            }
            current["route"] = "#!/app/index?appid=" + defapp+"&sceneid="+appSecneMap[defapp]+ "&buid=" + curBuId+"&bussinesid="+current["businessId"];
            current["appid"] = appid;
            current["leftMenus"] = lmenu;
            finalmenu.push(current);
        }
        return {
            "finalmenu":finalmenu,
            "applist":resOfapplist,
            "scenariolist":resOfscenariolist,
            "appSecneMap":appSecneMap,
            "menuIndex":menuIndex,
            "flag":"succ"
        }
    }

    function toTargetBu(params){
            var bu = params.bu;
            if($scope.user.userType!=1){
                var curUserBus = $scope.user.accessbu+$scope.user.accessbu4manager;
                if(curUserBus.search(bu)==-1){
                    alert("您没有该bu的访问权限，请选择您所属bu进行访问。");
                    return;
                }
            }

            $scope.globalBuName = bu;
            var type = params.type;
            $.ajax({
                url: 'api/getMenuByBuName.htm?buName='+$scope.globalBuName,
                dataType: 'json',
                method: 'GET',
                async:false,
                success: function (data) {
                    if (data.success) {
                        var tmp = data.data.buinfo;
                        var curBuId = tmp.buid;
                        $scope.globalBuId = curBuId;
                        $scope.globalBuName = tmp.buName;
                        $scope.dashboardHref = "#!/app/dashboard?showtype=quality&buid="+$scope.globalBuId;
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
                                $scope.currentLeftMenu = $scope.menus[0].leftMenus; //todo:暂时固定为精准
                            }
                            try{
                                if($scope.currentLeftMenu[0].appid == 33){
                                    $scope.defaultPath = "#!/app/quality?appid=" +  $scope.currentLeftMenu[0].appid +
                                        "&sceneid=" + $scope.currentLeftMenu[0].son[0].scenarioId+"&buid=" + curBuId+"&bussinesid="+bussinesid;

                                }else {
                                    $scope.defaultPath = "#!/app/index?appid=" + $scope.currentLeftMenu[0].appid +
                                        "&sceneid=" + $scope.currentLeftMenu[0].son[0].scenarioId+"&buid=" + curBuId+"&bussinesid="+bussinesid;
                                }
                            } catch (e){
                                console.log("catch a exception");
                            }

                            if(type=="SUBPAGE"){
                                $scope.switchProductLine(productLine);
                                $state.go("app.subpage",params);
                            }else if(type=="welcome"){
                                productLine = $scope.menuIdx[0];
                                $scope.switchProductLine(productLine);
                                $state.go("app.subpage",{"appid":$scope.currentLeftMenu[0].appid,"sceneid":$scope.currentLeftMenu[0].son[0].scenarioId,"buid":curBuId,"bussinesid":bussinesid});
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

    return {getUser: getUser,setLeftMenu:setLeftMenu,getMenu:getMenu,toTargetBu:toTargetBu}
}]);