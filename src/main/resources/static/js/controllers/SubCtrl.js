angular.module('app.controllers.subController', ['pascalprecht.translate', 'ngCookies','ngSanitize', 'ui.select'])
    .controller('subController', ['$scope','$location','$interval','$modal','$state','$sce','UserService','GlobalStorage','ZhiziService','$compile','$window','$localStorage','$timeout','$stateParams','$uibModal',
        function($scope,$location,$interval,$modal,$state,$sce,UserService,GlobalStorage,ZhiziService,$compile,$window,$localStorage,$timeout,$stateParams,$uibModal) {

            $scope.sceneid = parseInt($stateParams.sceneid);
            $scope.showtype = $stateParams.showtype;
            $scope.testReportId = $stateParams.testReportId;
            $scope.selectenv = {};
            $scope.topos = [];
            $scope.scenarioEnvList = [];
            $scope.envGroups = [];
            $scope.caselist = {};
            $scope.reRunType = "0";

            $scope.currentPageSize = 15;


            $scope.textCodeConfig = {
                lineNumbers: true,
                lineWrapping: false,
                foldGutter: true,
                docEnd: false,
                lint: true,
                width: '100%',
                //theme :'erlang-dark',
                height: '300px',
                gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter", "CodeMirror-lint-markers"],
                //mode: {name: "application/json", globalVars: true},
            };


            if (isNaN($scope.appid)) {
                $scope.appid = 1;
            }

            if (isNaN($scope.appid)) {
                if ($scope.currentLeftMenu[0] != undefined) {
                    $scope.appid = $scope.currentLeftMenu[0].appid;
                }
            }


            if (!isNaN($scope.sceneid)) {

                $scope.currentLeftMenu = UserService.setLeftMenu($scope.currentLeftMenu, $scope.appid, $scope.sceneid);

                try {

                    ZhiziService.getPipelineDataDemo($scope.sceneid).then(function (res) {
                        if (res.data.success) {
                            $scope.pipeline = angular.fromJson(res.data.data);

                            //load   template
                            if ($scope.pipeline["new-deploy-stage"].hasOwnProperty("templateType") && $scope.pipeline["new-deploy-stage"]["templateType"]) {
                                $scope.templateType = $scope.pipeline["new-deploy-stage"]["templateType"];
                            }

                        } else {
                            console.error(res.data.message);
                        }
                    });
                }catch(e){

                }
            }


            if (!isNaN($scope.sceneid)) {
                if ($scope.type != 'quality') {
                    $scope.type = "scene";
                    $scope.sceneTab = "caseList";
                } else {
                    $scope.type = "qualityscene";
                }
                $scope.scenarioId = $scope.sceneid;
            } else {
                $scope.scenarioId = $scope.sceneid = "";
            }

            $scope.currentLeftMenu = UserService.setLeftMenu($scope.currentLeftMenu, $scope.appid, $scope.sceneid);

            $scope.sub = function (page) {
                var currentPageSize = 15;
                if ($scope.currentPageSize != null) {
                    currentPageSize = $scope.currentPageSize;
                }
                type = 'case';

                ZhiziService.getCaseListDemo($scope.scenarioId,page).then(function (res) {
                    $scope.allCasesNum = res.data.data.allNumber;

                    var pagecnt = Math.ceil($scope.allCasesNum * 1.0 / currentPageSize);
                    $scope.totalPage['case'] = pagecnt;
                    $scope.firstPage = 1;
                    $scope.lastPage = $scope.totalPage[type];
                    $scope.pageNum = 10;
                    if (page >= $scope.pageNum) {
                        $scope.firstPage = page - Math.floor($scope.pageNum / 2);
                    } else {
                        $scope.firstPage = 1;
                    }
                    if ($scope.firstPage > $scope.lastPage - $scope.pageNum) {
                        $scope.firstPage = $scope.lastPage - $scope.pageNum + 1;
                    }
                    $scope.currentPage[type] = page;
                    $scope.renderCaseInfo(res, page, $scope.firstPage);
                })

            }
            $scope.sub(0);
            $scope.toTargetBu();

            $scope.deleteCase = function (caseid) {
                ZhiziService.deleteCase(caseid).then(function (res) {
                    $scope.sub(0);
                });

            }

            $scope.doDel = function () {
                if (!$scope.mulDel) {
                    $scope.deleteCase($scope.delid);
                } else {
                    $scope.deleteMulCase($scope.delid);
                }
                $scope.mulDel = false;
                $scope.delid = "";
            }


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



            //=============================  markov demo (上面部分)======================


            $scope.case = {};
            $scope.deploy = {
                state: 0,
                isdocker: true
            };
            $scope.detailStatus = "";
            $scope.currentPage = {
                "case": 1,
                "result": 1,
                "detail": 1
            }
            $scope.totalPage = {
                "case": 0,
                "result": 0,
                "detail": 0
            }
            $scope.resBack = {
                "SUCCESS": "运行成功",
                "FAILURE": "校验失败，期望与实际不符",
                "ERROR": "运行失败",
                "RUNNING": "运行中……"
            }
            $scope.styleName = {
                "SUCCESS": "green",
                "FAILURE": "red",
                "ERROR": "red",
                "RUNNING": "blue"
            }

            $scope.mulrun = {};

            // 跳转到对应页面
            $scope.toCreate = function () {
                $window.location.href = "#!/app/case?appid=" + $scope.appid + "&sceneid=" + $scope.scenarioId + "&type=create";
            }
            $scope.toMulRun = function () {
                $scope.sceneTab = "mulrun";
            }
            $scope.toResult = function () {
                $scope.sceneTab = "result";
                $scope.getResultList($scope.scenarioId);
                if ($scope.smokeTimer) {
                    $interval.cancel($scope.smokeTimer);
                }
            }

            $scope.getFuncResultList = function () {
                $scope.type = "result";
                $scope.getResultList($scope.scenarioId);

            }


            $scope.toScene = function () {
                $scope.type = "scene";
                $scope.sceneTab = "caseList";
                // $window.location.href="#!/app/index?appid=" +$scope.appid + "&sceneid=" + $scope.sceneid+"&buid="+$scope.globalBuId;
            }

            $scope.toRecommend = function(){
                $scope.type = "recommend";
                $window.location.href="#!/app/recommend?appid=" + $scope.appid +"&sceneid=" +$scope.scenarioId + "&type=recommend";
            }


            $scope.toResultDetail = function (id) {
                $scope.sceneTab = "resultdetail";
                // todo: 回测的报告id单独取
                if (id == -99) {
                    id = $scope.mulrun.reportid;
                }
                $scope.reportId = id;
                $scope.getResultDetailList(id, $scope.currentPage['detail']);
            }


            $scope.toDeploy = function () {
                $scope.sceneTab = "deploy";
            }


            $scope.toModuleConf = function () {
                $scope.type = "moduleConf";
                $scope.confTab = "pipeline";

            }

            $scope.toDiamond = function () {
                $scope.confTab = "diamond";
            }
            $scope.toDataSource = function () {
                $scope.confTab = "datasource";
            }
            $scope.toProto = function () {
                $scope.confTab = "proto";
            }
            $scope.toPermi = function () {
                $scope.confTab = "permisson";
            }


            $scope.toEdit = function (caseid, actiontype, reportid = null, appid = null, scenarioId = null) {
                if (reportid != null) {
                    $scope.isTestReport = true;
                }
                else {
                    $scope.isTestReport = false;
                }

                if (appid == null) {
                    appid = $scope.appid;
                }

                if (scenarioId == null) {
                    scenarioId = $scope.scenarioId;
                }

                var tmp = "#!/app/case?appid=" + appid + "&sceneid=" + scenarioId + "&type=" + actiontype + "&caseid=" + caseid;

                if (actiontype == "singleResult") {
                    tmp += "&reportid=" + reportid;
                }
                if (actiontype == "edit" && reportid != null) {
                    tmp += "&testReportId=" + reportid;

                }
                if (actiontype == "expand") {
                    tmp = "#!/app/expand?appid=" + $scope.appid + "&sceneid=" + $scope.scenarioId + "&type=" + actiontype + "&caseid=" + caseid;
                }
                $window.open(tmp);
                // if(actiontype == "edit" && reportid==null){
                //     $window.location.href = tmp;
                // }else {
                //     $window.open(tmp);
                // }
            }


            $scope.pickList = [];
            $scope.pickCase = function (caseId) {
                $scope.pickList = [caseId];

            }

            // 全选方法
            $scope.allChecked = [];
            $scope.selectAll = function (check) {

                $scope.allCheckeFlag = check;
                $scope.runCaseNum = "全部";
                $scope.allChecked[$scope.currentPage['case']] = check;

                if ($scope.allChecked[$scope.currentPage['case']]) {
                    // 如果是选中状态,则将所有id加入进来
                    for (var i = 0; i < $scope.caseList.length; i++) {
                        $scope.pickCase($scope.caseList[i].id);
                    }
                } else {
                    // 如果全选框为未选中,则删除当前caselist全部id
                    for (var i = 0; i < $scope.caseList.length; i++) {
                        var caseId = $scope.caseList[i].id;
                        var pos = $scope.pickList.indexOf(caseId);
                        if (pos != -1) {
                            $scope.pickList.splice(pos, 1);
                        }
                    }
                }

            }


            // 获取模块数据
            $scope.getStats = function () {
                ZhiziService.getStats($scope.appid).then(function (res) {
                    var appname = $scope.tree[$scope.appid];
                    if (res.data.success) {
                        var tmp = res.data.data;
                        $scope.showAppStats = true;
                        $scope.statsApp = {
                            "caseNumber": 0,
                            "regressionNumber": 0,
                            "successNumber": 0
                        };
                        for (var business in tmp) {
                            for (var i in tmp[business]) {
                                if (tmp[business][i].appId == $scope.appid) {
                                    for (var option in $scope.statsApp) {
                                        $scope.statsApp[option] += tmp[business][i][option];
                                    }
                                }
                            }
                            if ($scope.statsApp["regressionNumber"] > 0) {
                                $scope.statsApp["successRate"] = $scope.statsApp["successNumber"] / $scope.statsApp["regressionNumber"] * 100;
                                $scope.statsApp["successRate"] = $scope.statsApp["successRate"].toFixed(2);
                                $scope.statsApp["successRate"] += '%';
                            } else {
                                $scope.statsApp["successRate"] = '0%';
                            }
                        }
                    } else {
                        $scope.showAppStats = false;
                    }

                })
            }
            //  获取用例列表


            $scope.renderCaseInfo = function (res, p, first = 1) {
                $scope.currentPage['case'] = p;
                $scope.caseList = res.data.data.testCaseList;
                $scope.caseNum = res.data.data.allNumber;
                var pagecnt = Math.ceil($scope.caseNum * 1.0 / $scope.currentPageSize);
                $scope.totalPage['case'] = pagecnt;

                var tmp = [];
                for (var i = 0; i < pagecnt; i++) {
                    tmp.push(i + 1);
                }

                var casepages = [];
                var page = pagecnt;
                if (page <= 10) {
                    for (var i = 1; i <= page; i++) {
                        casepages.push(i);
                    }
                }
                if (page > 10) {
                    for (var i = first; i < first + 10; i++) {
                        casepages.push(i);
                    }
                }
                $scope.casepages = casepages;
                $scope.lastPage = pagecnt;


                $scope.casepage = tmp;
            }
            $scope.prevpage = function (type) {
                if ($scope.currentPage[type] > 1) {
                    $scope.currentPage[type]--;
                    $scope.cursorto($scope.currentPage[type], type);
                }
            }
            $scope.nextpage = function (type) {
                if ($scope.currentPage[type] < $scope.totalPage[type]) {
                    $scope.currentPage[type]++;
                    $scope.cursorto($scope.currentPage[type], type);
                }
            }
            $scope.cursorto = function (p, type) {
                $scope.currentPage[type] = p;
                switch (type) {
                    case "case":
                        $scope.findMatchCase(p);
                        break;
                    case "result":
                        $scope.getResultList($scope.scenarioId, p);
                        break;
                    case "detail":
                        $scope.getResultDetailList($scope.reportId, p, 15, $scope.detailStatus);
                        break;
                    default:
                        break;
                }
                $scope.saveInfo = '';
            }





            // 获取结果列表
            $scope.resultTmpList = [];
            $scope.getResultList = function (sid, p = 1, psize = 15, status="") {
                ZhiziService.getResultList($scope.appid, sid, p, psize, status).then(function (res) {
                    $scope.currentPage['result'] = p;
                    if (sid == "") {
                        $scope.resultList = $scope.resultListApp = res.data.data.reportsList;
                    } else {
                        $scope.resultList = res.data.data.reportsList;
                    }

                    //add success num \fail num\ process rate
                    var i = 0;
                    angular.forEach($scope.resultList, function (o) {
                        $scope.resultList[i]['allNum'] = 0;
                        $scope.resultList[i]['successNum'] = 0;
                        $scope.resultList[i]['failNum'] = 0;
                        $scope.resultList[i]['processNum'] = 0;
                        $scope.resultList[i]['processRate'] = 20;
                        $scope.resultList[i]['progressType'] = 'info';
                        try {
                            var reportJson = angular.fromJson(o.analysis);
                            reportJson = reportJson.basicReport;
                            $scope.resultList[i]['allNum'] = reportJson.caseNum;
                            $scope.resultList[i]['successNum'] = reportJson.sucessCaseNum;
                            $scope.resultList[i]['failNum'] = reportJson.failCaseNum;
                            $scope.resultList[i]['processNum'] = $scope.resultList[i]['successNum'] + $scope.resultList[i]['failNum'];
                            $scope.resultList[i]['processRate'] = 20 + Math.ceil(($scope.resultList[i]['processNum']) / ($scope.resultList[i]['allNum']) * 80);
                            if ($scope.resultList[i]['status'] == 'FAILURE') {
                                $scope.resultList[i]['progressType'] = 'danger';
                            } else if ($scope.resultList[i]['status'] == 'SUCCESS') {
                                $scope.resultList[i]['progressType'] = 'success';
                            }
                        } catch (e) {
                            if ($scope.resultList[i]['status'] == 'FAILURE') {
                                $scope.resultList[i]['progressType'] = 'danger';
                                $scope.resultList[i]['processRate'] = 100;
                            } else if ($scope.resultList[i]['status'] == 'SUCCESS') {
                                $scope.resultList[i]['progressType'] = 'success';
                                $scope.resultList[i]['processRate'] = 100;
                            } else if ($scope.resultList[i]['status'] == 'RUNNING') {
                                $scope.resultList[i]['progressType'] = 'info';
                                $scope.resultList[i]['processRate'] = 20;
                            }

                        }
                        i++;

                    });


                    $scope.resultTmpList = res.data.data.reportsList;
                    $scope.resultNum = res.data.data.allNumber;
                    var pagecnt = Math.ceil($scope.resultNum * 1.0 / 15);
                    $scope.totalPage['result'] = pagecnt;
                    var tmp = [];
                    for (var i = 0; i < pagecnt; i++) {
                        tmp.push(i + 1);
                    }
                    $scope.resultPage = tmp;
                })
            }
            $scope.searchFilter = {keyword: ""};
            $scope.$watch("searchFilter.keyword", function (newVal, oldVal) {
                if (oldVal == newVal) return;
                $timeout(function () {
                    if ($scope.searchFilter.keyword) {
                        $scope.resultList = [];
                        angular.forEach($scope.resultTmpList, function (o) {
                            if (
                                (o.reportName && o.reportName.indexOf($scope.searchFilter.keyword) != -1) ||
                                o.user == $scope.searchFilter.keyword ||
                                o.status == $scope.searchFilter.keyword ||
                                o.id == $scope.searchFilter.keyword
                            ) {
                                $scope.resultList.push(o);
                            }
                        })
                    } else $scope.getResultList("");
                }, 300);
            });


            $scope.detailListStatusFilter = function (status) {
                $scope.detailStatus = status;
                $scope.getResultDetailList($scope.reportId, 1, 15, status);
            }

            $scope.loadCurrentPageSize = function (currentPageSize) {
                $scope.currentPageSize = currentPageSize;
                $scope.sub(1);
            }


            $scope.selectTroubleShootBox = function (troubleShootBox) {
                $scope.selectTroubleShootBox = troubleShootBox;
            }

            $('#modifyTroubleShootBox').on('show.bs.modal', function () {
                $scope.selectTroubleShootBox = troubleShootBox;
            })

            $scope.resetTroubleShootBox = function (troubleShootBox) {
                $scope.selectTroubleShootBox = {};
            }

            $scope.addManualFailAttribute = function (selectTroubleShootBox) {

                if ($scope.caseSelectType == 'multi') {
                    var params = {
                        "scenarioId": $scope.scenarioId,
                        "appId": $scope.appid,
                        "caseIds": $scope.pickList.join(","),
                        "testReportId": $scope.reportId,
                        "failAttribute": selectTroubleShootBox.failAttribute,
                        "content": selectTroubleShootBox.content
                    };
                }
                else {
                    var params = {
                        "scenarioId": $scope.scenarioId,
                        "appId": $scope.appid,
                        "caseIds": $scope.selectCaseId,
                        "failAttribute": selectTroubleShootBox.failAttribute,
                        "content": selectTroubleShootBox.content
                    };
                }

                ZhiziService.addManualFailAttribute(params).then(function (res) {
                    var data = res.data.data;
                    alert(data);
                    ZhiziService.getManualFailAttribute($scope.appid, $scope.sceneid, $scope.selectCaseId).then(function (res) {
                        var data = res.data.data;
                        $scope.troubleShootBoxList = data.troubleShootBoxList;
                        $scope.troubleShootBoxCnt = data.count;
                    })

                })
            }

            $scope.getResultDetailList = function (rid, p = 1, psize = 15, status = "") {

                ZhiziService.getResultDetailList($scope.appid, rid, p, psize, status).then(function (res) {
                    var res = res.data.data;
                    $scope.resultDetailList = res.testcaseList;
                    $scope.resultDetailNum = res.allNumber;
                    $scope.allStatusNumber = res.allStatusNumber;
                    $scope.resultDetailFail = res.failureNumber;
                    $scope.resultDetailrun = res.runNumber;
                    $scope.resultDetailsucess = res.sucessNumber;
                    $scope.hasAccu = res.hasAccu;

                    var user = res.user;
                    var timeGap = res.timeGap;
                    var reportName = res.reportName;
                    $scope.currentPage['detail'] = p;
                    var pagecnt = Math.ceil(res.allStatusNumber * 1.0 / 15);
                    $scope.totalPage['detail'] = pagecnt;
                    var tmp = [];
                    for (var i = 0; i < pagecnt; i++) {
                        tmp.push(i + 1);
                    }
                    $scope.resultDetailPage = tmp;
                    var color, resultTip;
                    if ($scope.resultDetailList.status == "SUCCESS") {
                        color = "alert alert-success";
                    } else if ($scope.resultDetailList.status == "FAILURE") {
                        color = "alert alert-danger";
                    } else {
                        color = "alert alert-info";
                    }
                    resultTip = $scope.statusInfo[$scope.resultDetailList.status];
                    if ($scope.resultDetailList.status == "ERROR") {
                        resultTip = "执行错误，错误提示：" + currentPage.message;
                    }

                    $scope.resultDetailSummary = {
                        "status": res.status,
                        "totalNum": $scope.resultDetailNum,
                        "failNum": $scope.resultDetailFail,
                        "runNum": $scope.resultDetailrun,
                        "sucessNum": $scope.resultDetailsucess,
                        "user": user,
                        "reportName": reportName,
                        "timeGap": timeGap,
                        "ngclass": color,
                        "resultTip": resultTip,
                        "reportId": rid,
                        "gmtCreate": res.gmtCreate,
                        "gmtModified": res.gmtModified,
                        "imageName": res.imageName
                    }

                })
            }

            $scope.isNum = function (s) {
                if (s != null && s != "") {
                    return !isNaN(s);
                }
                return false;
            }

            $scope.statusInfo = {
                "UNKNOWN": "未知",
                "PENDING": "待定...结果稍后刷新",
                "RUNNING": "运行中",
                "SUCCESS": "运行成功",
                "FAILURE": "运行失败",
                "ERROR": "有错误",
                "DEPLOYING": "部署中",
            }
            $scope.getPercent = function (rid) {
                ZhiziService.getResultDetailList($scope.appid, rid, 1, 15).then(function (res) {
                    var status = res.data.data.testReport.status;
                    $scope.mulrun.status = status;
                    $scope.mulrun.failcnt = res.data.data.failureNumber;
                    if (status == 'FAILURE' || status == 'SUCCESS') {
                        return;
                    } else {
                        $timeout(function () {
                            $scope.fetchRes(rid);
                        }, 10000);

                    }
                })

            }
            $scope.fetchRes = function (id) {
                $scope.getPercent(id);
            }

            $scope.deleteMulCase = function (caseid) {
                ZhiziService.deleteMulCase(caseid).then(function (res) {
                    $scope.getCaseList();
                    $scope.pickList = [];
                });

            }
            $scope.setDelId = function (caseId, type = 'single') {
                if (type == 'single') {
                    $scope.delid = caseId;
                    $scope.mulDel = false;
                } else {
                    $scope.delid = '';
                    for (var x in caseId) {
                        if ($scope.delid != '') {
                            $scope.delid += ',';
                        }
                        $scope.delid += caseId[x];
                    }
                    $scope.mulDel = true;
                }

            }

            $scope.pageRunType = {name: "", code: 0};
            $scope.formData = {
                taskType: $scope.pageRunType.code,
                mode: "multi",
                type: "new",
                script: "",
                envName: "",
                callbackHost: "",
                selectModels: {},
                params: {},
                caseType: 0,
                cases: [],
                hosts: [],
                selectHosts: {},
                existEnvSelect: {},
                deploy: [],
                productInfo: $scope.productInfo
            }


            $scope.setRunId = function (caseId, type = 'single') {
                $scope.userSelfEnv = GlobalStorage.getJSONObj("CurrentEnv_" + $scope.sceneid);
                if (type == 'single') {
                    //$scope.runid = caseId;
                    //$scope.mulRun = false;
                } else {
                    $scope.runIdNum = 0;
                    $scope.runId = '';
                    $scope.runCaseNum = 0;
                    angular.forEach(caseId, function (x) {
                        if ($scope.runId != '') {
                            $scope.runId += ',';
                        }
                        $scope.runId += x;
                        $scope.runCaseNum++;
                    });
                    if ($scope.allCheckeFlag) {
                        $scope.runCaseNum = "全部";
                    }
                    $scope.mulRun = true;
                    if ($scope.userSelfEnv == undefined || $scope.userSelfEnv.id == undefined) {
                        $scope.userSelfEnv = "你未设置默认环境";
                    }

                }
                console.log($scope.runId);

            }

            $scope.initEnvTableMock = function () {
                $scope.scenarioEnvList = [];
                $scope.envGroups = [];
                $scope.selfSetEnv = GlobalStorage.getJSONObj("CurrentEnv_" + $scope.sceneid);
                $scope.selectenv.current = $scope.selfSetEnv;
                //测试专用场景id
                //$scope.sceneid = 1;
                ZhiziService.listGroupByHost($scope.sceneid).then(function(res) {
                    if (res.data.success) {
                        $scope.scenarioEnvList = res.data.data;

                        if ($scope.scenarioEnvList.length>0){
                            var envs = $scope.scenarioEnvList[0].envs;
                            if (envs.length>0){
                                for (var i=0; i<envs.length;i++){
                                    if ($scope.selfSetEnv.id==envs[i].id){
                                        $scope.scenarioEnvList[0].envs[i].currentEnv=1;
                                    }
                                    else{
                                        $scope.scenarioEnvList[0].envs[i].currentEnv=0;
                                    }
                                    var ev= envs[i];
                                    $scope.envGroups.push({
                                        id: ev.id,
                                        name: ev.name,
                                        current: ev.currentEnv == 1,
                                    })


                                }
                            }
                        }
                    }
                });
            }



            $scope.setModifyId = function (caseId, type = 'single') {
                if (type == 'single') {
                    //$scope.runid = caseId;
                    //$scope.mulRun = false;
                } else {
                    $scope.modifyId = '';
                    for (var x in caseId) {
                        if ($scope.modifyId != '') {
                            $scope.modifyId += ',';
                        }
                        $scope.modifyId += caseId[x];
                    }
                    $scope.mulModify = true;
                }

            }

            $scope.setSeedCaseIds =  function(caseId){
                if(caseId.length == 0 ){
                    alert("请先选中生成任务种子用例，在用例列表的第一列进行勾选！");
                    $scope.showTaskModal = false;
                    return;
                }
                $scope.showTaskModal = true;
                $scope.seedCaseId = '';
                angular.forEach(caseId, function (x) {
                    $scope.seedCaseId += caseId;
                })
                $scope.userSelfEnv = GlobalStorage.getJSONObj("CurrentEnv_" + $scope.sceneid);
                ZhiziService.getDefaultGeneConf($scope.scenarioId).then(function (res) {
                    $scope.defaultGeneConf = res.data.data;
                })

                $scope.AICaseGenerateTask = true;
            }



            $scope.startAICaseGenerator =  function(){
                var params = {
                    caseId : $scope.seedCaseId,
                    scenarioId : $scope.scenarioId,
                    fieldConf : $scope.defaultGeneConf,
                    envInfo : $scope.userSelfEnv
                }

                ZhiziService.startAICaseGenerator(params).then(function (res) {
                    $window.location.href = "#!/app/AICaseGenerator?AITaskId="+res.data.data.taskId;
                })
            }


            $scope.toLastGenerateTask = function(){
                ZhiziService.getLastGenerateTask($scope.scenarioId).then(function (res) {
                    if(res.data.success) {
                        $window.location.href = "#!/app/AICaseGenerator?AITaskId=" + res.data.data;
                    }else{
                        alert("尚无智能生成任务记录！");
                    }
                })
            }



            $scope.syncExp = function () {
                $scope.case.caseExp = $scope.case.caseAct;
            }
            //$scope.getStats();


            // 获取app首页
            //$scope.getResultList("", 1, 5,"");

            $scope.sourceData = {};//前台实际使用的数据源结构
            $scope.datasourceList = {}; //后台返回的数据源列表

            $scope.scrollFunc = function (div1, div2) {
                var timeout;
                var str = "#" + div1 + ",#" + div2;
                $(str).on("scroll", function callback() {
                    clearTimeout(timeout);
                    var source = $(this),
                        target = $(source.is("#" + div1) ? '#' + div2 : '#' + div1);

                    // remove the callback from the other 'div' and set the 'scrollTop'
                    target.off("scroll").scrollTop(source.scrollTop());

                    // create a new 'timeout' and reassign 'scroll' event
                    // to other 'div' on 100ms after the last event call
                    timeout = setTimeout(function () {
                        target.on("scroll", callback);
                    }, 100);
                });
            }
            $scope.canedit = true;
            $scope.switchdiff = function () {
                $scope.canedit = !$scope.canedit;
                if ($scope.canedit) {
                    $scope.scrollFunc("s1", "s2");
                } else {
                    $scope.scrollFunc("s1", "prehtml")
                }
            }
            $scope.findMatchCase = function (p = 1, first=1) {
                if ($scope.keywords == undefined) {
                    $scope.keywords = "";
                }
                ZhiziService.listTestCaseByKeywords($scope.scenarioId, $scope.keywords, $scope.currentBranch, p).then(function (res) {
                    $scope.renderCaseInfo(res, p, first);
                })
            }
            $scope.$watch('type', function () {
                if ($scope.type == 'create') {
                    $(document).ready(function () {
                        $scope.scrollFunc("s1", "s2");
                    })
                }
            }, true);
        }
    ]);



