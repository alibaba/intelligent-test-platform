angular.module('app.controllers.mainController', ['pascalprecht.translate', 'ngCookies','ngSanitize', 'ui.select'])
    .controller('mainController', ['$scope','$location','$state','$stateParams','$modal','$log','toaster','UserService','GlobalStorage','ZhiziService','$compile','$window','$localStorage','$timeout',
        function($scope,$location,$state,$stateParams,$modal,$log,toaster,UserService,GlobalStorage,ZhiziService,$compile,$window,$localStorage,$timeout){

            $scope.funcList = ["appList", "sceneList", "case", "resultList", "deploy", "resultDetail", "mulrun", "tair"];
            $scope.caseFuncList = ["create", "edit", "singleResult"];
            $scope.selectenv = {};
            $scope.topos = [];
            $scope.scenarioEnvList = [];
            $scope.envGroups = [];
            $scope.caselist = {};
            $scope.bunames=[];
            $scope.curBuName = "请选择";
            $scope.curBuId=1;

            $scope.type="demo";
            $scope.multiCheckSelected = {};
            $scope.baseCodeMirrorConfig = {
                lineNumbers: true,
                lineWrapping: false,
                foldGutter: true,
                docEnd: false,
                lint: false,
                width: '100%',
                height: '600px',
                gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter", "CodeMirror-lint-markers"],
                mode: {name: "javascript", globalVars: true},
            };

            $scope.currentBranch = 'master';
            $scope.progressType="info";
            $scope.dynamic=0;
            $scope.showBottomLabel = false;
            $scope.isNewDataSource = false;

            var path = $location.path();
            path = path.substr(path.lastIndexOf("/") + 1);
            $scope.appid = parseInt($stateParams.appid);
            $scope.sceneid = parseInt($stateParams.sceneid);
            if( $scope.sceneid==undefined){
                $scope.sceneid =1;
            }
            $scope.envSelected = false;
            $scope.currentLeftMenu = UserService.setLeftMenu($scope.currentLeftMenu, $scope.appid, $scope.sceneid);
            $scope.queryMirrorConfig = angular.copy($scope.baseCodeMirrorConfig);
            $scope.queryMirrorConfig.height = "300px";
            $scope.queryMirrorConfig.lineWrapping = true;

            $scope.expectMirrorConfig = angular.copy($scope.baseCodeMirrorConfig);
            $scope.expectMirrorConfig.height = "300px";
            $scope.expectMirrorConfig.lineNumbers = false;

            $scope.serviceMirrorConfig = angular.copy($scope.baseCodeMirrorConfig);
            $scope.serviceMirrorConfig.height = "50px";
            $scope.serviceMirrorConfig.lineNumbers = false;

            $scope.mathodMirrorConfig = angular.copy($scope.baseCodeMirrorConfig);
            $scope.mathodMirrorConfig.height = "50px";
            $scope.mathodMirrorConfig.lineNumbers = false;

            $scope.paramsMirrorConfig = angular.copy($scope.baseCodeMirrorConfig);
            $scope.paramsMirrorConfig.height = "400px";
            $scope.paramsMirrorConfig.lineNumbers = false;

            $scope.currentEnv = {};
            $scope.currentEnvModules = [];
            $scope.datasourceMapId ={};

            $scope.pbEmptyUnit = {
                "pbFileName":"无",
                "content":"",
            };

            var type = $stateParams.type;
            if(type == 'create'){
                var branch = GlobalStorage.getJSONObj("CurrentBranch_" + $scope.sceneid);
                if (branch.branch) {
                    $scope.currentBranch = branch.branch;
                }
            }

            for (var i in $scope.menus){
                for(var j in $scope.menus[i].leftMenus){
                    if($scope.appid == $scope.menus[i].leftMenus[j].appid){
                        $scope.productInfo.productLine = $scope.menus[i].businessId+"-"+$scope.menus[i].topName;
                        $scope.currentLeftMenu = [];
                        $scope.currentLeftMenu = $scope.menus[i].leftMenus;
                        GlobalStorage.setJSONObj("SELF_SELECT_MENU",$scope.productInfo);
                    }
                }
            }



            $scope.deleteDeployEnv = function (id) {
                if (confirm("确定删除吗？")) {
                    toaster.pop("wait", "信息", "正在执行删除....");
                    ZhiziService.deleteDeployEnv(id).then(function (resdata) {
                        toaster.clear();
                        if (resdata.data.success) {
                            toaster.pop("success", "信息", "删除完成");
                            $scope.initEnvTableMock();
                        } else {
                            toaster.pop("error", "信息", resdata.data.info.message);
                        }
                    }, function (error) {
                        toaster.clear();
                        alert(JSON.stringify(error));
                    })
                }
            }

            $scope.getCaseCheckConf = function(){
                $scope.checkConfType='module';
                ZhiziService.getCaseCheckConf($scope.sceneid, $scope.case.caseId).then(function (res) {
                    if(res.data.success){
                        $scope.moduleCheckConf = res.data.data.moduleCheckConf;
                        $scope.caseCheckConf = res.data.data.caseCheckConf;
                        $scope.description = res.data.data.description;

                        $scope.kvModuleFields = res.data.data.moduleCallFuncFields;
                        $scope.kvCaseFields =  res.data.data.caseCallFuncFields;


                    }else{
                        alert("get conf error!");
                    }
                })
            }

            $scope.addCheckFields = function(confScope, fieldsType){
                if(confScope == 'module'){
                    if(fieldsType == 'callFuncFields'){
                        $scope.kvModuleFields[""] = "";
                    }else {
                        $scope.moduleCheckConf[fieldsType].push("");
                    }

                }else if(confScope == 'case'){

                    if(fieldsType == 'callFuncFields'){
                        $scope.kvCaseFields[""] = "";
                    }else {
                        $scope.caseCheckConf[fieldsType].push("");
                    }

                }

            }


            $scope.rmCheckFields = function(confScope, fieldsType, oneField){
                if(confScope == 'module'){
                    if(fieldsType != 'callFuncFields') {
                        $scope.moduleCheckConf[fieldsType].splice(oneField,1);
                    }else{
                        delete $scope.kvModuleFields[oneField];
                    }

                }else if(confScope == 'case'){
                    if(fieldsType != 'callFuncFields') {
                        $scope.caseCheckConf[fieldsType].splice(oneField,1);
                    }else{
                        delete $scope.kvCaseFields[oneField];
                    }

                }

            }


            $scope.saveCheckConf = function(moduleCheckConf,caseCheckConf, kvModuleFields, kvCaseFields){

                var params = {
                    'moduleConf' : moduleCheckConf,
                    'caseConf' : caseCheckConf,
                    'scenarioId' : $scope.sceneid,
                    'caseId' : $scope.case.caseId,
                    'kvModule': kvModuleFields,
                    'kvCase': kvCaseFields
                }
                ZhiziService.saveCheckConf(params).then(function (res) {
                    if(res.data.success){
                        alert("save success");
                        //$scope.getCaseCheckConf();
                    }else{
                        console.log(res.data.message);
                        alert("save failed!");
                    }
                });

            }

            $scope.getEnvCheckStatus = function(envObj){

                var params = {
                    'ip' : envObj.dockerIp,
                    'port' : envObj.dockerPort,
                    'deployCmd' : envObj.deployCmd,
                }
                $scope.currentEnvObj = envObj;
                toaster.pop("wait", "信息", "进行实时环境检测中，请稍后....");
                ZhiziService.getEnvCheckStatus(params).then(function (res) {
                    if(res.data.success){
                        ZhiziService.dockerImageLog($scope.appId,$scope.sceneid,envObj.dockerImg,envObj.dockerIp).then(function (dockeres) {
                            $scope.dockerLog = dockeres.data.data;
                            $scope.envCheckRes = 'SUCCESS';
                            $scope.envCheckDesc = '环境检测未发现异常。';

                            $scope.imageCheckStatus = 'SUCCESS';
                            if(($scope.dockerLog.indexOf("fail")<0 && $scope.dockerLog.indexOf("error")<0 )&& res.data.data.finalStatus=="SUCCESS"){
                                if("FAIL,TIMEOUT,EXCEPTION,ERROR".indexOf(envObj.status)>=0 || "FAIL,TIMEOUT,EXCEPTION,ERROR".indexOf(envObj.deployStatus)>=0){
                                    $scope.envCheckDesc = '环境检测未发现异常，请查看日志或登入容器进行排查！';
                                }
                            }else if($scope.dockerLog.indexOf("Image is up to date")<0 && "FAIL,TIMEOUT,EXCEPTION,ERROR".indexOf(envObj.status)>=0){
                                $scope.imageCheckStatus = 'ERROR';
                                $scope.envCheckRes = 'ERROR';
                                $scope.envCheckDesc = '镜像拉取异常';
                            }else if(res.data.data.finalStatus!="SUCCESS"){
                                $scope.envCheckRes = 'ERROR';
                                $scope.envCheckDesc = '环境部署状态异常，详见检测列表！';
                            }

                            toaster.clear();
                            toaster.pop("success", "信息", "环境检测成功");
                            $scope.envCheck = res.data.data;
                        })

                    }else{
                        console.log(res.data.message);
                        toaster.clear();
                        alert("环境检测失败");
                    }
                });

            }

            $scope.getDeployLog = function(fileName, startLine=-999){
                if($scope.totalNum>0 && startLine>=$scope.totalNum){
                    alert("已展示文件最后一行，无法向后翻页");
                    return;
                }
                if(startLine<-498 && startLine!=-999 ){
                    alert("已展示文件第一行，无法向前翻页");
                    return;
                }

                toaster.clear();
                toaster.pop("wait", "信息", "加载日志文件中...");

                var envObj = $scope.currentEnvObj;
                //默认展示最新500行
                var params = {
                    'ip' : envObj.dockerIp,
                    'port' : envObj.dockerPort,
                    'logFile' : fileName,
                    'startLine' : startLine
                };
                $scope.currentLogFile = fileName;
                ZhiziService.getDeployLog(params).then(function (res) {
                    if(res.data.success){
                        $scope.currentLogText = res.data.data.logText;
                        $scope.startLine = res.data.data.startLine;
                        $scope.endLine = res.data.data.endLine;
                        $scope.totalNum = res.data.data.totalNum;

                        toaster.clear();
                        toaster.pop("success", "信息", "加载成功");

                    }else{
                        toaster.clear();
                        alert("加载失败:"+res.data.message);

                    }
                });

            }


            $scope.showCurrentEnvDetail = function(currentEnv){
                console.log(currentEnv);
                if(currentEnv.id == undefined){
                    $scope.currentEnvIp = "";
                    $scope.currentEnvPort = "";
                    $scope.currentEnvName = "";
                    $scope.currentEnvIamge = "";
                    return;
                }
            }




            $scope.detailListStatusFilter = function(status=""){
                $scope.detailStatus = status;
                $scope.getResultDetailList($stateParams.testcaseid,1,15,status);
            }



            function  getResultDetailList(){
                $scope.getResultDetailList($stateParams.testcaseid,1,15,"");
            }
            $scope.getResultDetailList = function(testcaseid,p = 1, psize = 15,status = ""){

                ZhiziService.getCaseSnapsDetailList(testcaseid, p, psize,status).then(function(res) {
                    var res = res.data.data;

                    $scope.resultDetailList = res.testcaseList;

                    $scope.currentPage['detail'] = p;
                    var pagecnt = Math.ceil(res.allStatusNumber * 1.0 / 15);
                    $scope.totalPage['detail'] = pagecnt;
                    var tmp = [];
                    for (var i=0; i<pagecnt;i++){
                        tmp.push(i+1);
                    }
                    $scope.resultDetailPage = tmp;

                    //失败归因
                    $scope.failAttributeMap = {};

                    for (var i=0; i<$scope.resultDetailList.length;i++){

                        var attributeUnit = {};

                        if ($scope.resultDetailList[i].troubleShootBox!=null){
                            attributeUnit = angular.fromJson($scope.resultDetailList[i].troubleShootBox);
                            $scope.resultDetailList[i].attribute = attributeUnit["attribute"];
                        }
                        else{
                            $scope.resultDetailList[i].attribute = "自检无异常";
                            attributeUnit["attribute"] = "自检无异常";
                        }
                        var testcaseSnapId = $scope.resultDetailList[i].id;
                        $scope.failAttributeMap[testcaseSnapId] = attributeUnit;
                    }

                })
            }

            function getCaseModifyList(){
                $scope.getCaseModifyList($stateParams.testcaseid,1,15,"");
            }

            $scope.getCaseModifyList = function(testcaseid,p = 1, psize = 15){

                ZhiziService.getCaseModifyList(testcaseid, p, psize).then(function(res) {
                    var res = res.data.data;

                    $scope.caseModifyList = res.testcaseList;

                    $scope.currentPageM['detail'] = p;
                    var pagecnt = Math.ceil(res.allStatusNumber * 1.0 / 15);
                    $scope.totalPageM['detail'] = pagecnt;
                    var tmp = [];
                    for (var i=0; i<pagecnt;i++){
                        tmp.push(i+1);
                    }
                    $scope.caseModifyListPage = tmp;

                })
            }

            $scope.toGetFailAttribute = function(testcaseId){
                $scope.failAttributeUnit = $scope.failAttributeMap[testcaseId];
            }

            $scope.prevpage = function(type){
                if ($scope.currentPage[type] > 1){
                    $scope.currentPage[type]--;
                    $scope.cursorto($scope.currentPage[type], type);
                }
            }
            $scope.nextpage = function(type){
                if ($scope.currentPage[type] < $scope.totalPage[type]){
                    $scope.currentPage[type]++;
                    $scope.cursorto($scope.currentPage[type], type);
                }
            }
            $scope.cursorto = function(p, type){
                $scope.currentPage[type] = p;
                switch (type){
                    case "detail":$scope.getResultDetailList($stateParams.testcaseid,p,15,$scope.detailStatus);break;
                    default:break;
                }
                $scope.saveInfo = '';
            }

            ////===修改用例列M表的快照==
            $scope.prevpageM = function(type){
                if ($scope.currentPageM[type] > 1){
                    $scope.currentPageM[type]--;
                    $scope.cursortoM($scope.currentPageM[type], type);
                }
            }
            $scope.nextpageM = function(type){
                if ($scope.currentPageM[type] < $scope.totalPageM[type]){
                    $scope.currentPageM[type]++;
                    $scope.cursortoM($scope.currentPageM[type], type);
                }
            }
            $scope.cursortoM = function(p, type){
                $scope.currentPageM[type] = p;
                switch (type){
                    case "detail":$scope.getCaseModifyList($stateParams.testcaseid,p,15);break;
                    default:break;
                }
                $scope.saveInfo = '';
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
                                        //break;
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

            $scope.initEnvTable = function () {
                if(!isNaN($scope.sceneid)){
                    ZhiziService.getPipelineData($scope.sceneid).then(function(res) {
                        if (res.data.success) {
                            $scope.pipeline = angular.fromJson(res.data.data);

                            try {
                                if ($scope.pipeline["new-deploy-stage"]["topo"][0].hasOwnProperty("topoConf")) {
                                    $scope.imageRelate = true;
                                } else {
                                    $scope.imageRelate = false;
                                }
                            }catch (e){

                            }
                        }
                    });
                }

                $scope.scenarioEnvList = [];
                $scope.envGroups = [];
                $scope.selfSetEnv = GlobalStorage.getJSONObj("CurrentEnv_" + $scope.sceneid);
                $scope.selfSetBaseEnv = GlobalStorage.getJSONObj("BaseEnv_" + $scope.sceneid);
                $scope.selectenv.current = $scope.selfSetEnv;
                $scope.selectenv.base = $scope.selfSetBaseEnv;

                $scope.curBuName="";
                $scope.loadBuInfo();


                ZhiziService.getIsProductFlag().then(function(res){
                    $scope.isSyncFlag= res.data.data;
                });

                userPromise.then(function () {
                    if(GlobalStorage.getJSONObj("SELF_SELECT_MENU").product){
                        $scope.productInfo = GlobalStorage.getJSONObj("SELF_SELECT_MENU");
                    } else {
                        $scope.productInfo = {product:"markov",productLine:"1-定向",productModule:"3-UTS",productSubModule:"10-单品"};
                        GlobalStorage.setJSONObj("SELF_SELECT_MENU",$scope.productInfo);
                    }

                    ZhiziService.getPipelineData($scope.sceneid).then(function (resp) {
                        var pipObj = angular.fromJson(resp.data.data);
                        //console.log(pipObj);
                        if (pipObj["new-deploy-stage"]) {
                            $scope.topos = pipObj["new-deploy-stage"]["topo"];
                            var hosts = pipObj["new-deploy-stage"]["hostList"];
                            $scope.pan = hosts;



                            FengChaoService.listGroupByHost({
                                productInfo: $scope.productInfo,
                                userInfo: {userId: $scope.user.code, nickName: $scope.user.nickName}
                            }).then(function (resdata) {
                                angular.forEach(hosts, function (h) {

                                    var hostFlag = false;
                                    if (resdata.data.data.hostEnvList.length > 0) {
                                        angular.forEach(resdata.data.data.hostEnvList, function (s) {
                                            if (s.ip == h.ip) {
                                                hostFlag = true;
                                                var hostType = 0;
                                                if(h.hostType != undefined){
                                                    hostType = h.hostType;
                                                }
                                                else if(s.hostType != undefined){
                                                    hostType = s.hostType;
                                                }

                                                //反转顺序
                                                for (var i=0; i<s.envs.length;i++){
                                                    var index = s.envs.length-1-i;
                                                    var ev = s.envs[index];

                                                    if ($scope.selfSetEnv.id) {
                                                        if (ev.id == $scope.selfSetEnv.id) {
                                                            ev.currentEnv = 1;
                                                        } else {
                                                            ev.currentEnv = 0;
                                                        }
                                                    }
                                                    $scope.envGroups.push({
                                                        id: ev.id,
                                                        name: ev.name,
                                                        current: ev.currentEnv == 1,
                                                        host: s.ip
                                                    })
                                                }



                                                $scope.scenarioEnvList.push({
                                                    ip: s.ip,
                                                    hostType: hostType,
                                                    maxEnv: s.maxEnvNum,
                                                    remain: s.remain,
                                                    hostInfo: h,
                                                    envs: s.envs
                                                });
                                                //console.log($scope.scenarioEnvList);
                                            }
                                        })
                                    }
                                    if (!hostFlag) {
                                        $scope.scenarioEnvList.push({
                                            ip: h.ip,
                                            hostType: h.hostType || 0,
                                            maxEnv: 15,//h.maxEnvNum,
                                            remain: 15,//h.maxEnvNum,
                                            hostInfo: h,
                                            envs: []
                                        })
                                    }
                                });

                                if ($scope.scenarioEnvList.length == 0) {
                                    angular.forEach(hosts, function (h) {
                                        $scope.scenarioEnvList.push({
                                            ip: h.ip,
                                            hostType: h.hostType || 0,
                                            maxEnv: 15,//h.maxEnvNum,
                                            remain: 15,//h.maxEnvNum,
                                            hostInfo: h,
                                            envs: []
                                        })
                                    })
                                }
                            });
                        }
                    }, function (error) {
                        alert(JSON.stringify(error));
                    })
                })
            }




            $scope.getDeployLog = function (cmd) {

                console.log(cmd);
                //ZhiziService.getDeployLog($scope.appId,$scope.sceneid,cmd).then(function (res) {
                //    $scope.dockerLog = res.data.data;
                //})
            }


            $scope.$watch("selectenv.current", function (newVal, oldVal) {
                if (oldVal == newVal) return;
                GlobalStorage.setJSONObj("CurrentEnv_" + $scope.sceneid, {id: newVal.id, name: newVal.name});
            });

            $scope.$watch("selectenv.base", function (newVal, oldVal) {
                if (oldVal == newVal) return;
                GlobalStorage.setJSONObj("BaseEnv_" + $scope.sceneid, {id: newVal.id, name: newVal.name});
            });


            $scope.setCurrentEnv = function (env) {
                GlobalStorage.setJSONObj("CurrentEnv_" + $scope.sceneid, {id: env.id, name: env.name});
                /*
                 FengChaoService.setCurrentEnv(id).then(function(resdata){
                 console.log(resdata);
                 },function(error){
                 alert("设置失败，"+JSON.stringify(error));
                 })
                 */
            }

            $scope.setBaseEnv = function (env) {
                GlobalStorage.setJSONObj("BaseEnv_" + $scope.sceneid, {id: env.id, name: env.name});
                /*
                 FengChaoService.setCurrentEnv(id).then(function(resdata){
                 console.log(resdata);
                 },function(error){
                 alert("设置失败，"+JSON.stringify(error));
                 })
                 */
            }
            var createEnvInstanceCtrl = function ($scope, $modalInstance, remain, fields, hostInfo, userInfo, productInfo, envObj,imageRelate, sceneid) {
                $scope.topolist = [];
                $scope.buildModelDesc = {};
                $scope.remain = remain;
                $scope.envObj = {};
                $scope.currentIdx = {};
                $scope.scenarioId = sceneid;
                $scope.imageRelate = imageRelate;


                if (hostInfo) {
                    hostInfo.createEnvNum = 1;
                    hostInfo.removeOldEnv = true;
                    hostInfo.newCreateModule = false;
                }
                if (envObj) {
                    $scope.envObj = envObj;
                    if (hostInfo) {
                        hostInfo.newCreateModule = true;
                    }
                }
                $scope.currentProduct = productInfo;
                var curDate = new Date();
                var month = curDate.getMonth() + 1;
                var time = curDate.getFullYear() + "-" + month + "-" + curDate.getDate() + "-" + curDate.getHours() + ":" + curDate.getMinutes() + ":" + curDate.getSeconds();


                $scope.pickImage = function(currentImagePicked){
                    $scope.currentImagePicked = currentImagePicked;

                }


                $scope.formData = {
                    mode: "normal",
                    productInfo: productInfo,
                    hostInfo: hostInfo,
                    userInfo: userInfo,
                    envName: $scope.envObj.name ? $scope.envObj.name : ( "markov-" + productInfo.productSubModule + "-" + time),
                    selectModels: {},
                    params: {},
                    deploy: fields,
                    scenarioId:sceneid,
                    deployGiven:$scope.deployGiven

                }

                var _fields = angular.copy(fields);
                angular.forEach(_fields, function (o) {
                    $scope.formData.params[o.module] = {};
                    $scope.formData.params[o.module][0] = {key: o.module, data: "", desc: "请输入" + o.module + "的参数1"};
                    if (o.buildDesc) {
                        $scope.currentIdx[o.module] = o.buildDesc.length - 1;
                        $scope.buildModelDesc[o.module] = o.buildDesc;
                        if (o.buildDesc.length > 0) {
                            for (var ii = 0; ii < o.buildDesc.length; ii++) {
                                $scope.formData.params[o.module][ii] = {
                                    key: o.buildDesc[ii].key || (ii == 0 ? o.module : ""),
                                    desc: o.buildDesc[ii].desc || "",
                                    data: o.buildDesc[ii].defaultValue || ""
                                }
                            }
                        }
                    } else {
                        $scope.currentIdx[o.module] = 0;
                    }
                    $scope.topolist.push(o);
                })

                $scope.addParams = function (tl, idx) {
                    $scope.currentIdx[tl.module] = parseInt(idx) + 1;
                    var defaultDesc = "请输入" + tl.module + "的参数" + $scope.currentIdx[tl.module];
                    if ($scope.buildModelDesc[tl.module]) {
                        if ($scope.buildModelDesc[tl.module].length > $scope.currentIdx[tl.module]) {
                            $scope.formData.params[tl.module][$scope.currentIdx[tl.module]] = {
                                key: $scope.buildModelDesc[tl.module][$scope.currentIdx[tl.module]].key || "",
                                data: $scope.buildModelDesc[tl.module][$scope.currentIdx[tl.module]].defaultValue || "",
                                desc: $scope.buildModelDesc[tl.module][$scope.currentIdx[tl.module]].desc || defaultDesc
                            };
                        } else {
                            $scope.formData.params[tl.module][$scope.currentIdx[tl.module]] = {
                                key: "",
                                data: "",
                                desc: defaultDesc
                            };
                        }
                    } else {
                        $scope.formData.params[tl.module][$scope.currentIdx[tl.module]] = {
                            key: "",
                            data: "",
                            desc: defaultDesc
                        };
                    }
                }
                $scope.delParams = function (tl, idx) {
                    $scope.currentIdx[tl.module] = 0;
                    delete $scope.formData.params[tl.module][idx];
                    var pmobjs = angular.copy($scope.formData.params[tl.module]);
                    $scope.formData.params[tl.module] = {};
                    var ii = 0;
                    angular.forEach(pmobjs, function (v, k) {
                        $scope.formData.params[tl.module][ii] = v;
                        ii++;
                    });
                    $scope.currentIdx[tl.module] = ii - 1;
                }
                $scope.close = function () {
                    $modalInstance.dismiss('cancel');
                };

                $scope.setImageInputType = function(imageInputType){
                    $scope.imageInputType = imageInputType;
                }


                $scope.completed = function () {
                    //toaster.clear();
                    //toaster.pop("wait", "信息", "正在创建....");

                    ZhiziService.addEnv($scope.formData).then(function (resdata) {
                        if (resdata.data.success) {
                            //toaster.clear();
                            toaster.pop("success", "信息", "创建完成");
                            //$modalInstance.close("ok");
                        } else {
                            toaster.clear();
                            toaster.pop("success", "信息", resdata.data.info.message);
                        }
                    }, function (error) {
                        alert("创建失败:" + JSON.stringify(error));
                    })
                };


            };

            $scope.openCreateEnvModule = function (type,hostInfo, remain, env) {

                if(type=='new' && remain<=0){
                    alert("亲~剩余环境数目为0!请手动去清理无用容器,以免部署失败哦!");
                    return;
                }

                var envCreateInstance = $modal.open({
                    templateUrl: 'env_create.html',
                    windowClass: 'app-modal-window',
                    controller: createEnvInstanceCtrl,
                    keyboard: true,
                    backdrop: 'static',
                    resolve: {
                        remain: function () {
                            return remain;
                        },
                        fields: function () {
                            return $scope.topos;
                        },
                        hostInfo: function () {
                            return hostInfo;
                        },
                        userInfo: function () {
                            return {userId: $scope.user.code, nickName: $scope.user.nickName};
                        },
                        productInfo: function () {
                            return $scope.productInfo;
                        },
                        envObj: function () {
                            return env;
                        },
                        imageRelate: function () {
                            return $scope.imageRelate;
                        },
                        sceneid: function () {
                            return $scope.sceneid;
                        }
                    }
                });
                envCreateInstance.result.then(function (result) {
                    if (result == "ok") {
                        $scope.initEnvTable();
                    }
                }, function (reason) {
                    console.log(reason);
                })
            }


            $scope.envShareModule = function (env) {
                $modal.open({
                    templateUrl: 'env_share.html',
                    windowClass: 'app-modal-window',
                    controller: EnvShareIns,
                    resolve: {
                        fields: function () {
                            return env;
                        },
                        currentUser: function () {
                            return $scope.user;
                        }
                    }
                });
            }

            var ScriptInstanceCtrl = function ($scope, $modalInstance, fields, codeMirrorConfig) {
                $scope.scriptOptions = codeMirrorConfig;
                $scope.scriptOptions.theme = 'erlang-dark';
                $scope.trace = {
                    modeName: "普通模式",
                    tcontent: fields
                }
                $scope.changeMode = function (mode) {
                    var jsmode = angular.copy(codeMirrorConfig);
                    jsmode.lint = mode == "application/json" ? true : false;
                    jsmode.mode.name = mode;
                    $scope.scriptOptions = jsmode;
                    if (jsmode.lint == true) {
                        $scope.trace.modeName = "JSON模式";
                    } else {
                        $scope.trace.modeName = "普通模式";
                    }
                }
                $scope.close = function () {
                    $modalInstance.dismiss('cancel');
                };
                $scope.completed = function () {

                    $modalInstance.close($scope.trace.tcontent);
                };
            };

            $scope.openModuleEdit = function (trace,index,type) {

                $("#templateEditBox").hide();
                var scriptInstance = $modal.open({
                    templateUrl: 'trace.html',
                    windowClass: 'app-modal-window',
                    controller: ScriptInstanceEditCtrl,
                    keyboard: false,
                    backdrop: 'static',
                    resolve: {
                        fields: function () {
                            try {
                                return JSON.stringify(angular.fromJson(trace), undefined, 2);
                            } catch (e) {
                                return trace;
                            }
                        },
                        codeMirrorConfig: function () {
                            return angular.copy($scope.baseCodeMirrorConfig);


                        }
                    }
                });

                scriptInstance.result.then(function (result) {

                        if (type == 0) {
                            $scope.currentEditContent = result;
                            $scope.confirmEditContent();

                        } else if (type == 1) {
                            $scope.oneTemplateInfo.config[index].configValue = result;
                        }
                        else if (type == 2) {
                            $scope.oneDataSource.configDefault[index].configValue = result;
                        }
                        else if (type == 3) {
                            $scope.oneDataSource.configModule[index].configValue = result;
                        }
                        else if (type == 4) {
                            $scope.fixFeature.dat = result;
                        }
                        else if (type == 5) {
                            $scope.fixFeature.fmt = result;
                        }
                        else if (type == 6) {
                            $scope.fixFeature.initData = result;
                        }
                        else if (type == 7) {
                            $scope.currentPbUnit.content = result;
                        }
                        else if (type == 8) {
                            $scope.oneDataSource.template = result;
                        }
                        else if (type == 9) {
                            $scope.oneDataSource.demo = result;
                        }
                        else if (type == 10) {
                            $scope.oneProto.content = result;
                        }
                        else if (type == 11) {
                            $scope.case.caseLongDesc = result;
                        }else if(type == 12){
                            $scope.fixFeature.initData = result;
                        }
                    }, function (reason) {
                        if (type == 0) {
                            $scope.exitEditMode();
                        }
                    }
                )
            }

            var ScriptInstanceEditCtrl = function ($scope, $modalInstance, fields, codeMirrorConfig) {
                $scope.scriptOptions = codeMirrorConfig;
                $scope.scriptOptions.theme = 'erlang-dark';
                $scope.trace = {
                    modeName: "普通模式",
                    tcontent: fields
                }
                $scope.changeMode = function (mode) {
                    var jsmode = angular.copy(codeMirrorConfig);
                    jsmode.lint = mode == "application/json" ? true : false;
                    jsmode.mode.name = mode;
                    $scope.scriptOptions = jsmode;
                    if (jsmode.lint == true) {
                        $scope.trace.modeName = "JSON模式";
                    } else {
                        $scope.trace.modeName = "普通模式";
                    }
                }
                $scope.close = function () {
                    $("#templateEditBox").show();
                    $modalInstance.dismiss('cancel');

                };
                $scope.completed = function () {
                    $("#templateEditBox").show();
                    $modalInstance.close($scope.trace.tcontent);

                };
            };

            $scope.openModule = function (trace, type, idx0, idx1, dsType, currentDs) {
                var scriptInstance = $modal.open({
                    templateUrl: 'trace.html',
                    windowClass: 'app-modal-window',
                    controller: ScriptInstanceCtrl,
                    keyboard: true,
                    backdrop: 'static',
                    resolve: {
                        fields: function () {
                            try {
                                return JSON.stringify(angular.fromJson(trace), undefined, 2);
                            } catch (e) {
                                return trace;
                            }
                        },
                        codeMirrorConfig: function () {
                            return angular.copy($scope.baseCodeMirrorConfig);
                        }
                    }
                });
                scriptInstance.result.then(function (result) {
                    if (type == 0) {
                        $scope.currentEditContent = result;
                        $scope.confirmEditContent();
                    } else if (type == 1) {
                        $scope.caseRunStage[idx0].data[idx1].input = result;
                    } else if (type == 2) {
                        $scope.caseRunStage[idx0].data[idx1].expect = result;
                    } else if (type == 3) {
                        currentDs[dsType][idx0].data[idx1].key = result;
                    } else if (type == 4) {
                        currentDs[dsType][idx0].data[idx1].property = result;
                    }else if (type == 5) {
                        $scope.caseRunStage[idx0].data[idx1].actual = result;
                    }
                    else if (type == 6) {
                        $scope.case.content = result;
                    }
                    else if (type == 7) {
                        $scope.case.caseLongDesc = result;
                    }


                }, function (reason) {
                    if (type == 0) {
                        $scope.exitEditMode();
                    }
                })
            }


            $scope.initEnvListData = function (selectedEnvName){

                $scope.selectedServiceMethodMap = {};
                $scope.serviceList = [];
                $scope.selfSetEnv = selectedEnvName;
                for (var serviceName in $scope.serviceMethodMap) {
                    var info = $scope.serviceMethodMap[serviceName];
                    if (info.hasOwnProperty(selectedEnvName.name)){
                        $scope.serviceList.push(serviceName);
                        $scope.selectedServiceMethodMap[serviceName] ={};
                        $scope.selectedServiceMethodMap[serviceName][selectedEnvName.name]=info[selectedEnvName.name];
                    }
                }
                $scope.selectedServiceName = $scope.serviceList[0];
                $scope.selectedMethodInfo = $scope.selectedServiceMethodMap[$scope.selectedServiceName][$scope.selfSetEnv.name][0];
                $scope.methodNameList = $scope.selectedServiceMethodMap[$scope.selectedServiceName][$scope.selfSetEnv.name];
                $scope.selectedMethodInfo = $scope.selectedServiceMethodMap[$scope.selectedServiceName][$scope.selfSetEnv.name][0];

            }

            $scope.initMethodListData = function (selectedServiceName){
                $scope.selectedServiceName = selectedServiceName;
                $scope.caseRunStage[0].data[0].input = selectedServiceName;
                $scope.methodNameList = $scope.selectedServiceMethodMap[selectedServiceName][$scope.selfSetEnv.name];
                $scope.selectedMethodInfo = $scope.selectedServiceMethodMap[selectedServiceName][$scope.selfSetEnv.name][0];

                $scope.caseRunStage[0].data[0].expect = $scope.selectedMethodInfo.data;
            }

            $scope.initMethodData = function (selectedMethodInfo){

                try{
                    $scope.caseRunStage[0].data[0].expect = selectedMethodInfo.data;
                }
                catch (e){
                    //$scope.caseRunStage[0].data[0].expect = "";
                }

            }

            $scope.initParamsDescData = function (selectedParamsInfoInfo){
                $scope.selectedParamsInfoInfo = selectedParamsInfoInfo;
                $scope.selectedParamsList = [];
                for(var key in $scope.selectedParamsInfoInfo.params){
                    var kv = {"key":key,"value": $scope.selectedParamsInfoInfo.params[key]};
                    $scope.selectedParamsList.push(kv);
                }
            }


            $scope.analysisMultiCheck = function(){
                if($scope.mutilCheckConfig != null && $scope.mutilCheckConfig != undefined){
                    var newMultiCheckConfig = angular.copy($scope.mutilCheckConfig);
                    $scope.newMultiCheckConfig = {};
                    angular.forEach(newMultiCheckConfig, function(conf){
                        var checkType = conf.checkType;
                        if(checkType == null || checkType==undefined){
                            checkType = "默认检验";
                        }
                        if($scope.newMultiCheckConfig[checkType]!= null || $scope.newMultiCheckConfig[checkType]!= undefined){
                            $scope.newMultiCheckConfig[checkType].push(conf);

                        }else{
                            $scope.newMultiCheckConfig[checkType] = [];
                            $scope.newMultiCheckConfig[checkType].push(conf);
                        }
                    });
                }

            }
            $scope.getVersion = function () {
                ZhiziService.getVersion($scope.appid, $scope.sceneid).then(function (res) {
                    $scope.deployVersion = res.data.data;
                    $scope.clusterList = [];
                    $scope.hostList = {};
                    $scope.envSelected = false;
                    for (var i in $scope.deployVersion) {
                        $scope.clusterList.push(i);
                        var hosttmp = {};
                        for (var j in $scope.deployVersion[i]) {
                            var tmp = {"hostName": j, "selected": false, "roleList": []};
                            for (var z in $scope.deployVersion[i][j]) {
                                tmp.roleList.push({
                                    "roleName": $scope.deployVersion[i][j][z],
                                    "selected": false
                                })
                            }
                            hosttmp[j] = tmp;
                        }
                        $scope.hostList[i] = hosttmp;
                    }
                    for (var i in $localStorage.settings.userVersion) {
                        $scope.envSelected = true;
                        $scope.selectedVersion.cluster = i;
                        for (var j in $localStorage.settings.userVersion[i]) {
                            $scope.selectedVersion.host.push(j);
                            for (var k in $localStorage.settings.userVersion[i][j]) {
                                if (j in $scope.selectedVersion.machine) {
                                    $scope.selectedVersion.machine[j].push($localStorage.settings.userVersion[i][j][k]);
                                } else {
                                    $scope.selectedVersion.machine[j] = [$localStorage.settings.userVersion[i][j][k]];
                                }

                            }
                        }
                    }
                })
            }
            if (path == 'case' || path == 'caseJava' || path == 'caseBlink' ||  path == 'caseStfp') {
                $scope.casePath = path;
                $scope.caseid = $stateParams.caseid;
                $scope.reportid = $stateParams.reportid;
                $scope.typeForCase = $stateParams.type;
                $scope.case = {};
                $scope.case.currentLog = "";
                $scope.queryTemplates = {};
                $scope.expectTemplates = {};



                ZhiziService.getLayout($scope.sceneid).then(function (res) {
                    $scope.debug = res.data;
                    $scope.pipeline = $scope.debug.data;

                    if ($scope.pipeline == undefined || $scope.pipeline == null) {
                        alert("pipeline为空或无法获取，case相关功能均无法使用，请检查pipeline内容后再继续");
                        return;
                    }

                    $scope.useNewDataSource();
                    //console.log($scope.pipeline);
                    $scope.dataSource = []; // 数据源列表
                    $scope.dsList = {}; // 各数据源具体原地址列表
                    $scope.ds = []; // 当前case数据源信息

                    $scope.addPrepareData(); // 初始化第一组数据源信息
                    $scope.getQueryTemplates();
                    $scope.getExpectTemplates();
                    $scope.caseRunGroupList = $scope.pipeline.caseRunStage;
                    $scope.caseRunStage = $scope.pipeline.caseRunStage;
                    $scope.showDataGenStage = false;
                    $scope.restartMap = $scope.pipeline.restartMap;

                    $scope.mutilCheckConfig = $scope.pipeline.mutilCheckConfig;
                    $scope.analysisMultiCheck();

                    $scope.dataGenStage = $scope.pipeline.dataGenStage;
                    if ($scope.dataGenStage != null) {
                        $scope.oneDataGenInfo = $scope.dataGenStage[0];
                        $scope.showDataGenStage = true;
                    }

                    for (var i in $scope.caseRunStage) {
                        $scope.caseRunStage[i].data = [{"input": "", "expect": ""}];
                    }
                    if ($scope.typeForCase == "edit") {
                        ZhiziService.getCase($scope.appid, $scope.caseid, $stateParams.testReportId, $stateParams.snapId).then(function (res) {
                            var tmp = res.data.data;
                            $scope.getCaseInfo(tmp);
                            if($stateParams.testReportId!=null){
                                $scope.isTestReport = true;
                            }
                            else{
                                $scope.isTestReport = false;
                            }
                        })
                    }
                    if ($scope.typeForCase == "run") {
                        ZhiziService.getCase($scope.appid, $scope.caseid, $stateParams.snapId).then(function (res) {
                            var tmp = res.data.data;
                            $scope.getCaseInfo(tmp);
                            $scope.runCase();
                        })
                    }
                    if ($scope.typeForCase == "clone") {
                        ZhiziService.getCase($scope.appid, $scope.caseid).then(function (res) {
                            var tmp = res.data.data;
                            delete tmp.id;
                            $scope.getCaseInfo(tmp);
                            $scope.typeForCase = 'create';

                        })
                    }

                    if ($scope.typeForCase == "create") {
                        if ($stateParams.rd) {
                            var localCaseData = GlobalStorage.getJSONObj("diffCase");//LocalDataService.caseData;
                            var caseTemp = {
                                name: localCaseData.description,
                                description: localCaseData.description,
                                longDescription: localCaseData.longDescription,
                                id: null,
                                caseGroup: localCaseData.caseGroup,
                                tag: localCaseData.tag,
                                caseRunStage: localCaseData.caseRunStage
                            }
                            $scope.getCaseInfo(caseTemp, null);
                        } else {
                            $scope.getCaseInfo(undefined, 1);
                        }
                    }

                    if ($scope.typeForCase == "expand") {

                        $scope.getCaseInfo($localStorage["expandCaseList"][$scope.caseid]);
                        $scope.typeForCase = 'create';
                    }
                    if ($scope.typeForCase == "singleResult") {
                        ZhiziService.getSingleResult($scope.caseid, $scope.reportid).then(function (res) {
                            var tmp = res.data.data;
                            //console.log(tmp);
                            $scope.getCaseInfo(tmp);
                            $scope.typeForCase = 'singleResult';
                        })
                    }
                    // todo: run的情况
                })


            } else if (path == 'deploy') {
                $scope.typeForDeploy = $stateParams.type;
                $scope.deployRefreshcnt = 0;
                $scope.deploy = {
                    state: 0,
                    isdocker: true
                };
                $scope.selectedVersion = {
                    "cluster": "",
                    "host": [],
                    "machine": {}
                };
                ZhiziService.getCurrentParamters($scope.appid, $scope.sceneid).then(function (res) {
                    $scope.deployParams = res.data.data;
                    $scope.cluster = [];
                    $scope.host = {};
                    $scope.role = [];

                    for (var i in $scope.deployParams) {
                        $scope.cluster.push(i);
                        var hosttmp = [];
                        for (var j in $scope.deployParams[i]) {
                            hosttmp.push(j);
                        }
                        $scope.host[i] = hosttmp;
                    }
                    $scope.clusterSel = $scope.cluster[0];
                    $scope.setHostAndRole();
                })
                $scope.getVersion();

            } else if (path == "manager") {
                $scope.typeForManager = $stateParams.type;
                $scope.manager = {
                    "title": {
                        "datasource": "数据源管理",
                        "pipeline": "pipeline管理",
                        "menu": "菜单管理"
                    },
                    "content": ""
                }
                if ($scope.typeForManager == "datasource") {
                    ZhiziService.getDataSource($scope.sceneid).then(function (res) {
                        if (res.data.success) {
                            $scope.manager.content = res.data.data.content;
                        } else {
                            //
                        }
                    })
                }
                else if ($scope.typeForManager == "pipeline") {
                    ZhiziService.getPipelineFlow($scope.sceneid).then(function (res) {
                        if (res.data.success) {
                            //$scope.manager.content = angular.toJson(res.data.data);
                            $scope.manager.content = JSON.stringify(res.data.data, null, "\t");
                        } else {
                            //
                        }
                    })
                }

            } else if (path == "plugin") {
                $scope.content = "plugin";
            }

            $scope.setActionType = function (entity, actionType) {
                $scope.entity = entity;
                $scope.actionType = actionType;
            }
            $scope.setContent = function (content) {
                $scope.content = {};
                if ("type" in content) {
                    $scope.entity = content.type;
                }
                if ("id" in content) {
                    $scope.content.id = content.id;
                }
                if ("appId" in content) {
                    $scope.content.appId = content.appId;
                }
                if ("productId" in content) {
                    $scope.content.productId = content.productId;
                }
                // if ("buId" in content) {
                //     $scope.content.buId = content.buId;
                // }
                if ("delete" in content) {
                    $scope.content.delete = true;
                } else {
                    $scope.content.delete = false;
                }
                if ("edit" in content) {
                    $scope.content.edit = true;
                    $scope.content.name = content.originalName;
                } else {
                    $scope.content.edit = false;
                }
            }
            $scope.toPipeLine = function (sceneid) {
                $window.location.href = "#!/app/pipeline?stage=pipeline" + "&sceneid=" + sceneid;
            }

            $scope.toDiamond = function () {
                var tmp = "#!/app/pipeline?stage=diamond" + "&sceneid=" + $scope.sceneid;
                window.open(tmp);
            }

            $scope.editMenu = function () {
                var obj = {};
                if ($scope.content.edit) {
                    obj.id = $scope.content.id;
                }
                obj.type = $scope.entity;
                obj.name = $scope.content.name;
                switch ($scope.entity) {
                    case 'bu':
                        break;
                    case 'product':
                        obj.buId = $scope.curBuId;
                        break;
                    case 'app':
                        obj.productId = $scope.content.productId;
                        break;
                    case 'scenario':
                        obj.appId = $scope.content.appId;
                        obj.productId = $scope.content.productId;
                        break;
                    default:
                        break;
                }
                if ($scope.content.delete != true) {
                    ZhiziService.editMenu(obj).then(function (res) {
                        if (res.data.success) {
                            $scope.loadBuInfo();
                        } else {
                            alert("修改失败，错误信息为:" + res.data.message);
                        }
                    })
                } else {
                    ZhiziService.deleteMenu($scope.entity, $scope.content.id).then(function (res) {
                        if (res.data.success) {
                            $scope.loadBuInfo();
                        } else {
                            alert("删除失败，错误信息为:" + res.data.message);
                        }
                    })
                }
            }

            $scope.prepareDataGenJava = function (oneDataGenInfo) {

                //dataGenerator
                ZhiziService.dataGenerator($scope.appid, $scope.sceneid, $scope.caseid, oneDataGenInfo.title, null, null, "JAVA").then(function (data) {
                    data = data.data.data;

                    if (data.success) {
                        $scope.dataGenMsg = "用例生成完成!"
                        var caseRunStageMap = data.data.caseRunStage;
                        $scope.caseRunStage[0] = caseRunStageMap;

                        try{
                            $scope.caseRunStage[0].data[0].actual = JSON.stringify(angular.fromJson($scope.caseRunStage[0].data[0].actual), undefined, 2);
                        }
                        catch (e){
                        }

                    }
                    else {
                        $scope.dataGenMsg = "用例生成失败!请联系管理员查看配置!失败原因如下:\n" + data.message;
                    }
                })
            }

            //prepareDataGen
            $scope.prepareDataGen = function (oneDataGenInfo) {

                if ($scope.typeForCase == "create") {
                    $scope.dataGenMsg = "用例生成失败,请保存用例后再重试哦!"
                    return;
                }

                var adTypeMap = {};
                var switchTypeMap = {};
                if (angular.isDefined(oneDataGenInfo.num)) {
                    for (var i in oneDataGenInfo.num) {
                        adTypeMap[oneDataGenInfo.num[i].var] = oneDataGenInfo.num[i].defaultValue;
                    }
                }
                if (angular.isDefined(oneDataGenInfo.switch)) {
                    for (var i in oneDataGenInfo.switch) {
                        if (oneDataGenInfo.switch[i].defaultValue == true) {
                            switchTypeMap[oneDataGenInfo.switch[i].var] = "on";
                        }
                        else if (oneDataGenInfo.switch[i].defaultValue == false) {
                            switchTypeMap[oneDataGenInfo.switch[i].var] = "off";
                        }
                        else {
                            switchTypeMap[oneDataGenInfo.switch[i].var] = oneDataGenInfo.switch[i].defaultValue;
                        }
                    }
                }

                //dataGenerator
                ZhiziService.dataGenerator($scope.appid, $scope.sceneid, $scope.caseid, oneDataGenInfo.title, adTypeMap, switchTypeMap, "C++").then(function (data) {
                    data = data.data.data;

                    if (data.success) {
                        var dataMap = data.data.dataPrepareStage;
                        for (var type in dataMap) {
                            $scope.ds[0][type] = dataMap[type];
                            $scope.dataSource[0][type] = {"selected": true, "type": type};
                        }
                        $scope.dataGenMsg = "用例生成完成!"
                        //var caseRunStageMap = data.data.caseRunStage;
                        //$scope.caseRunStage[0] = caseRunStageMap;
                        //try{
                        //    if($scope.caseRunStage[0].data[0].input != undefined){
                        //        $scope.caseRunStage[0].data[0].input = JSON.stringify(angular.fromJson($scope.caseRunStage[0].data[0].input), undefined, 2);
                        //    }
                        //}
                        //catch (e){
                        //}
                        //try{
                        //    if($scope.caseRunStage[0].data[0].expect != undefined){
                        //        $scope.caseRunStage[0].data[0].expect = JSON.stringify(angular.fromJson($scope.caseRunStage[0].data[0].expect), undefined, 2);
                        //    }
                        //}
                        //catch (e){
                        //}
                        //try{
                        //    if($scope.caseRunStage[0].data[0].actual != undefined){
                        //        $scope.caseRunStage[0].data[0].actual = JSON.stringify(angular.fromJson($scope.caseRunStage[0].data[0].actual), undefined, 2);
                        //    }
                        //}
                        //catch (e){
                        //}
                    }
                    else {
                        $scope.dataGenMsg = "用例生成失败!请联系管理员查看配置!失败原因如下:\n" + data.message;
                    }
                })
            }

            $scope.fetchMenu = function () {
                $scope.applist = {};
                $scope.scenariolist = {};

                ZhiziService.fetchMenu().then(function (data) {
                    data = data.data;
                    if (data.success) {
                        var tmp = data.data;
                        var res = UserService.getMenu(tmp);
                        $scope.applist = res.applist;
                        $scope.scenariolist = res.scenariolist;
                        $scope.menus = res.finalmenu;
                        $scope.currentLeftMenu = $scope.menus[0].leftMenus; //todo:暂时固定为精准
                        $scope.switchProductLine();
                        $scope.appSecneMap = res.appSecneMap;
                    }
                })
            }

            $scope.saveManager = function () {
                switch ($scope.typeForManager) {
                    case 'datasource':
                        $scope.saveDataSource();
                        break;
                    case 'pipeline':
                        $scope.savePipelineFlow();
                        break;
                    default:
                        break;
                }
            }
            $scope.saveDataSource = function () {
                ZhiziService.saveDataSource($scope.appid, $scope.sceneid, $scope.manager.content).then(function (res) {
                    if (res.data.success) {
                        alert("保存成功");
                    } else {
                        alert("保存失败，" + res.data.message);
                    }
                })
            }

            $scope.displayDetailTip = function($event,data){
                //var tipSettings = {
                //    content:data
                //};
                //$($event.target).webuiPopover('destroy').webuiPopover($.extend({},basePopSettings,tipSettings));
                console.log("call displayDetailTip ...")
            }

            $scope.savePipelineFlow = function () {
                try {
                    var content = angular.fromJson($scope.manager.content);
                } catch (e) {
                    alert("pipeline内容需要是合法的json，请检查输入内容,错误提示如下: " + e.message);
                    return;
                }
                var data = {
                    appId: $scope.appId,
                    pipeline: $scope.manager.content,
                    scenarioId: $scope.scenarioId,
                    tag: "user_defined",
                    version: "default"
                }
                ZhiziService.savePipelineFlow(data).then(function (res) {
                    if (res.data.success) {
                        alert("保存成功");
                    } else {
                        alert("保存失败，" + res.data.message);
                    }
                })
            }

            $scope.getDataSource = function () {
                ZhiziService.getDataSource($scope.sceneid).then(function (res) {
                    if (res.data.success) {
                        $scope.dataSourceManager = res.data.data;
                    } else {
                        //
                    }
                })
            }
            $scope.addRole = function () {
                $scope.machinelist.role.push({
                    "roleName": $scope.deployParams[$scope.clusterSel][$scope.hostSel][0],
                    "build": ""
                });
            }
            $scope.rmRole = function (i) {
                if ($scope.machinelist.role.length == 1) {
                    alert("最少需要一个role才能部署，请返回重试");
                    return;
                }
                $scope.machinelist.role.splice(i, 1);
            }
            $scope.setHostAndRole = function () {
                $scope.hostSel = $scope.host[$scope.clusterSel][0];
                $scope.setRole();
            }
            $scope.setRole = function () {
                $scope.machinelist = {
                    "cluster": $scope.clusterSel,
                    "host": $scope.hostSel,
                    "role": [
                        {
                            "roleName": $scope.deployParams[$scope.clusterSel][$scope.hostSel][0],
                            "build": ""
                        }
                    ]
                };
            }
            $scope.addDsname = function (index, type) {
                $scope.ds[index][type].push({
                    "type": type,
                    "dsName": "",
                    "data": [{
                        "key": "",
                        "value": "",
                        "property": ""
                    }]
                });
            }
            $scope.rmDsname = function (index, type, subindex) {
                $scope.ds[index][type].splice(subindex, 1);
            }

            $scope.findDsNameBycase = function (dsName, type) {

                $scope.curKvp = {};

                var params = {
                    "scenarioId":$scope.sceneid,
                    "name":dsName,
                    "dataType":type
                }

                ZhiziService.findDsNameBycase(params).then(function (res) {
                    //console.log(res.data.data);
                    try{
                        var key = JSON.stringify(angular.fromJson(res.data.data.key), null, "\t");

                    }catch (e){
                        var key = res.data.data.key;
                    }
                    try{
                        var value = JSON.stringify(angular.fromJson(res.data.data.value), null, "\t");
                    }catch (e){
                        var value = res.data.data.value;
                    }
                    try{
                        var property = JSON.stringify(angular.fromJson(res.data.data.property), null, "\t");
                    }catch (e){
                        var property = res.data.data.property;
                    }
                    $scope.curKvp.key = key;
                    $scope.curKvp.value = value;
                    $scope.curKvp.property = property;
                })
            }


            $scope.addCol = function (index, type, subindex) {
                $scope.ds[index][type][subindex].data.push({
                    "key": "",
                    "value": "",
                    "property": ""
                });
            }
            $scope.rmCol = function (index, type, subindex, deleteindex) {
                $scope.ds[index][type][subindex].data.splice(deleteindex, 1);
            }

            $scope.setEditContent = function (data, a, b, c, d) {
                try {
                    var tmp = JSON.parse(data);
                    $scope.currentEditContent = JSON.stringify(tmp, null, "\t");
                } catch (e) {
                    $scope.currentEditContent = data;
                }

                $scope.editIndex = {
                    "a": a,
                    "b": b,
                    "c": c,
                    "d": d
                }
            }
            $scope.confirmEditContent = function () {
                var a = $scope.editIndex.a;
                var b = $scope.editIndex.b;
                var c = $scope.editIndex.c;
                var d = $scope.editIndex.d;
                try {
                    // 去除制表符等特殊格式
                    $scope.ds[a][b][c].data[d].value = JSON.stringify(JSON.parse($scope.currentEditContent));
                } catch (e) {
                    $scope.ds[a][b][c].data[d].value = $scope.currentEditContent;
                }

                $scope.exitEditMode();
            }
            $scope.exitEditMode = function () {
                $scope.currentEditContent = '';
                $scope.editIndex = {};
            }

            $scope.selectDsChoice = function (selectDs, type, idx, indexForDs) {
                var ds_name;
                if (selectDs.dsName.indexOf(":") != 1) {
                    ds_name = selectDs.dsName.substring(0, selectDs.dsName.lastIndexOf(":"));
                } else {
                    ds_name = selectDs.dsName;
                }
                selectDs.data = $scope.getTemplateData(type, ds_name);

                selectDs.restartFlag = $scope.getRestartFlag(type, ds_name);


            }

            $scope.synAndSaveDatasourceToContainner = function(oneDataSource){

                oneDataSource.syncPage = $scope.syncPage;

                if ($scope.action == "add"){
                    oneDataSource.id=null;
                }
                oneDataSource.scenarioId = parseInt($scope.sceneid);
                oneDataSource.module = $scope.moduleSelect;
                try{
                    oneDataSource.content = angular.toJson(oneDataSource.configModule);
                    oneDataSource.fixFeature = angular.toJson($scope.fixFeature);
                }
                catch (e){
                    oneDataSource.content = "";
                }

                oneDataSource.gmtCreate = null;
                oneDataSource.gmtModified = null;

                oneDataSource.dataType = $scope.dataTypeSelect;
                oneDataSource.module = $scope.moduleSelect;

                //获取环境
                var userSelfEnv = GlobalStorage.getJSONObj("CurrentEnv_" + $scope.sceneid);
                if (!userSelfEnv || !userSelfEnv.id) {
                    alert("你未设置默认环境，请在部署环境页面选择一个环境最为默认环境");
                    $scope.formInfo = "请选择测试环境";
                    return;
                }
                FengChaoService.getEnvDockersByEnvs({id: userSelfEnv.id}).then(function (resdata) {
                    if (resdata.data.info.ok) {
                        var envInfo = resdata.data.data.dockerNames;
                        oneDataSource.envInfo = envInfo;

                    } else {
                        alert("环境服务不稳定,请刷新..");
                    }
                    ZhiziService.synAndSaveDatasource(oneDataSource).then(function (res) {
                        alert(res.data.message);
                        ZhiziService.getDataSourceList($scope.sceneid).then(function (res) {
                            $scope.dataSourceListInfo = res.data.data;
                            $scope.initProtoListForDs();
                        })
                    })
                }, function (error) {
                    alert(JSON.stringify(error));
                })



            }


            $scope.synAndSaveDatasource = function(oneDataSource){

                oneDataSource.syncPage = $scope.syncPage;

                if ($scope.action == "add"){
                    oneDataSource.id=null;
                }
                oneDataSource.scenarioId = parseInt($scope.sceneid);
                oneDataSource.module = $scope.moduleSelect;
                try{
                    oneDataSource.content = angular.toJson(oneDataSource.configModule);
                    oneDataSource.fixFeature = angular.toJson($scope.fixFeature);
                }
                catch (e){
                    oneDataSource.content = "";
                }
                //保存pbName
                oneDataSource.protoFileName = $scope.currentPbUnit.pbFileName;
                oneDataSource.protoContent = $scope.currentPbUnit.content;

                oneDataSource.gmtCreate = null;
                oneDataSource.gmtModified = null;

                oneDataSource.dataType = $scope.dataTypeSelect;
                oneDataSource.module = $scope.moduleSelect;

                ZhiziService.synAndSaveDatasource(oneDataSource).then(function (res) {
                    alert(res.data.message);
                    ZhiziService.getDataSourceList($scope.sceneid).then(function (res) {
                        $scope.dataSourceListInfo = res.data.data;
                        $scope.initProtoListForDs();
                    })
                })
            }


            $scope.addDsModuleConifgItem = function(){

                var defaultds = {
                    "configName":"",
                    "configValue":"",

                };
                $scope.oneDataSource.configModule.push(defaultds);
            }

            $scope.getDatasourceNew = function(id){
                $scope.action = "put";
                ZhiziService.getDatasource(id).then(function (res) {
                    $scope.oneDataSource = res.data.data;
                    $scope.dataTypeSelect = $scope.oneDataSource.dataType;
                    $scope.moduleSelect = $scope.oneDataSource["module"];
                    $scope.curId =  $scope.oneDataSource.id;
                    $scope.dataType =  $scope.oneDataSource.dataType;
                    $scope.fixFeature = angular.fromJson($scope.oneDataSource.fixFeature);

                    var protoFileName = $scope.oneDataSource.protoFileName;

                    if (protoFileName=="无" || protoFileName==""  || protoFileName==null || protoFileName==undefined ){
                        $scope.isUseProtoFlag = false;
                    }
                    else{
                        $scope.isUseProtoFlag = true;
                    }

                    if ($scope.oneDataSource.isUseTemplate =="无"){
                        $scope.isUseTemplateFlag = false;
                    }
                    else{
                        $scope.isUseTemplateFlag = true;
                    }

                    if ($scope.oneDataSource.configDefault.length == 0){
                        $scope.isConfigDefaultFlag = false;
                    }
                    else{
                        $scope.isConfigDefaultFlag = true;
                    }

                    if ($scope.protoMap.hasOwnProperty(protoFileName)){
                        $scope.currentPbUnit = {
                            "pbFileName":protoFileName,
                            "content":$scope.protoMap[protoFileName],
                        };
                    }
                    else{
                        $scope.currentPbUnit =$scope.pbEmptyUnit;
                    }

                })
            }

            $scope.selectDsId = function (dsName) {
                $scope.datasourceId = $scope.datasourceMapId[dsName];
                $scope.getDatasourceNew($scope.datasourceId);
            }

            $scope.saveDataSource = function(oneDataSource){

                if ($scope.action == "add"){
                    oneDataSource.id=null;
                }

                oneDataSource.scenarioId = parseInt($scope.sceneid);
                oneDataSource.module = $scope.moduleSelect;
                try{
                    oneDataSource.content = angular.toJson(oneDataSource.configModule);
                    oneDataSource.fixFeature = angular.toJson($scope.fixFeature);
                }
                catch (e){
                    oneDataSource.content = "";
                }

                oneDataSource.gmtCreate = null;
                oneDataSource.gmtModified = null;

                oneDataSource.dataType = $scope.dataTypeSelect;
                oneDataSource.module = $scope.moduleSelect;

                ZhiziService.addDatasource(oneDataSource).then(function (res) {
                    alert(res.data.message);
                    ZhiziService.getDataSourceList($scope.sceneid).then(function (res) {
                        $scope.dataSourceListInfo = res.data.data;
                    })
                })
            }

            //$scope.synAndSaveDatasource = function(oneDataSource){
            //
            //    //oneDataSource.syncPage = $scope.syncPage;
            //    ZhiziService.synAndSaveDatasource(oneDataSource).then(function (res) {
            //        alert(res.data.message);
            //    })
            //}

            $scope.initProtoListForDs = function(){
                ZhiziService.getProtoList($scope.sceneid).then(function (res) {
                    var protoList = res.data.data;
                    $scope.protoList = [];
                    $scope.protoMap = {};

                    $scope.protoList.push($scope.pbEmptyUnit.pbFileName);

                    var pbFileName = "无";
                    var content = "";
                    $scope.protoMap[pbFileName] = content;

                    for (var index in protoList) {

                        pbFileName = protoList[index].pbFileName;
                        content = protoList[index].content;
                        unit = {
                            "pbFileName":pbFileName,
                            "content":content,
                        };
                        $scope.protoList.push(unit.pbFileName);
                        $scope.protoMap[pbFileName] = content;

                    }
                })
            }


            $scope.selectPb = function(pbFileName){

                $scope.currentPbUnit ={
                    "pbFileName":pbFileName,
                    "content":$scope.protoMap[pbFileName]
                };
            }

            Array.prototype.indexOf = function(val) {
                for (var i = 0; i < this.length; i++) {
                    if (this[i] == val) return i;
                }
                return -1;
            };

            Array.prototype.remove = function(val) {
                var index = this.indexOf(val);
                if (index > -1) {
                    this.splice(index, 1);
                }
            };


            $scope.getConfList = function () {
                //获取数据源列表
                ZhiziService.getDataSourceList($scope.sceneid).then(function (res) {

                    var confList = [];
                    $scope.confList= [];

                    if(res.data.data.hasOwnProperty("其他数据源")){
                        //console.log(res.data.data["其他数据源"]);
                        confList = res.data.data["其他数据源"];
                    }

                    for(var index in confList){
                        try{
                            var fixFeature = angular.fromJson(confList[index].fixFeature);
                            if(fixFeature.allowCaseShow){
                                $scope.confList.push(confList[index]);
                            }
                        }
                        catch (e){

                        }

                    }


                    if($scope.confList.length>0){
                        $scope.showConfig=true;
                    }
                    else{
                        $scope.showConfig=false;
                    }
                })
            }

            $scope.useNewDataSource = function () {

                try{
                    if($scope.pipeline["dataPrepareStageNew"]["dataPrepareLayoutList"] == undefined){
                        $scope.isNewDataSource = false;
                    }
                    else{
                        $scope.pipeline.dataPrepareStage = $scope.pipeline["dataPrepareStageNew"]["dataPrepareLayoutList"];
                        $scope.datasourceMapId =  $scope.pipeline["dataPrepareStageNew"]["mapId"];
                        $scope.isNewDataSource = true;

                        //初始化
                        ZhiziService.getDataTypeList().then(function (res) {
                            var list = res.data.data;
                            //用例编辑页禁用
                            list.remove("其他数据源");
                            $scope.dataTypeList =list;
                        })
                        ZhiziService.getModuleList($scope.sceneid).then(function (res) {
                            $scope.moduleList = res.data.data;
                        })
                        //获取所有proto内容
                        $scope.initProtoListForDs();
                        $scope.getConfList();
                    }

                }
                catch (e){
                    $scope.isNewDataSource = false;

                }
                ////如果用新数据源
                //if($scope.isNewDataSource){
                //    $scope.pipeline.dataPrepareStage = $scope.pipeline["dataPrepareStageNew"]["dataPrepareLayoutList"];
                //    $scope.datasourceMapId =  $scope.pipeline["dataPrepareStageNew"]["mapId"];
                //}
            }

            $scope.addPrepareData = function () {
                var defaultds = {};
                var currentDataSource = {};

                for (var i in $scope.pipeline.dataPrepareStage) {
                    var current = $scope.pipeline.dataPrepareStage[i];
                    var currentType = current.type;

                    if(currentType=="其他数据源" || currentType==undefined){
                        continue;
                    }

                    currentDataSource[currentType] = {type: currentType, selected: false};
                    $scope.dsList[currentType] = [];
                    for (var j in current.dsNameList) {
                        $scope.dsList[currentType].push(current.dsNameList[j]);
                    }

                    defaultds[currentType] = [{
                        "type": currentType,
                        "dsName": "",
                        "data": [{
                            "key": "",
                            "value": "",
                            "property": ""
                        }]
                    }];
                }
                $scope.dataSource.push(currentDataSource);
                $scope.ds.push(defaultds);
            }

            $scope.rmPrepareData = function (i) {
                $scope.ds.splice(i, 1);
                $scope.dataSource.splice(i, 1);
            }

            $scope.getRestartFlag = function (type, dsName) {

                if ($scope.restartMap.hasOwnProperty(dsName)) {
                    return "1";
                }

                return "0";
            }

            $scope.getTemplateData = function (type, dsName) {

                var data = [];
                for (var i in $scope.pipeline.dataPrepareStage) {
                    var current = $scope.pipeline.dataPrepareStage[i];
                    var currentType = current.type;
                    if (currentType == type) {
                        var templates = current.templates;
                        angular.forEach(templates, function (v, k) {
                            if (k == dsName) {
                                angular.forEach(v, function (vl) {
                                    data.push({
                                        key: vl.KEY || "",
                                        value: vl.VALUE || "",
                                        property: vl.PROPERTY || ""
                                    })
                                })
                                return data;
                            }
                        })
                    }
                }
                if (data.length == 0) {
                    data.push({
                        key: "",
                        value: "",
                        property: ""
                    })
                }
                return data;
            }
            $scope.getQueryTemplates = function () {
                if (!$scope.pipeline || !$scope.pipeline["caseRunStageTemplate"]) {
                    return;
                }
                angular.forEach($scope.pipeline["caseRunStageTemplate"], function (v, k) {
                    $scope.queryTemplates[k] = [];
                    angular.forEach(v, function (v1, k1) {
                        angular.forEach(v1, function (v2, k2) {
                            $scope.queryTemplates[k].push({name: k1, key: k2, val: v2.VALUE});
                        })
                    })
                })
            }
            $scope.getExpectTemplates = function () {
                if (!$scope.pipeline || !$scope.pipeline["caseRunStageExpectTemplate"]) {
                    return;
                }
                angular.forEach($scope.pipeline["caseRunStageExpectTemplate"], function (v, k) {
                    $scope.expectTemplates[k] = [];
                    angular.forEach(v, function (v1, k1) {
                        angular.forEach(v1, function (v2, k2) {
                            $scope.expectTemplates[k].push({name: k1, key: k2, val: v2.VALUE});
                        })
                    })
                })
            }

            $scope.getCaseInfo = function (tmp, replace = null) {
                if (replace == null) {
                    if (tmp) {
                        $scope.case.caseName = tmp.name;
                        $scope.case.caseDesc = tmp.description;
                        $scope.case.caseLongDesc = tmp.longDescription;
                        $scope.case.caseId = tmp.id;
                        $scope.case.caseGroup = tmp.caseGroup;
                        $scope.case.tag = tmp.tag;
                        $scope.case.content = tmp.content;
                        $scope.currentBranch = tmp.branchName;
                    }
                } else {
                    if (tmp) {
                        $scope.case = tmp;
                    } else {
                        //新建case逻辑
                        $scope.case = {
                            caseName: $scope.pipeline.caseName,
                            caseDesc: $scope.pipeline.caseDese,
                            caseLongDesc: $scope.pipeline.caseLongDese,
                            tag: $scope.pipeline.tag,
                            caseGroup: $scope.pipeline.caseGroup
                        }
                    }
                }



                if (tmp) {
                    $scope.caseRunStage = tmp.caseRunStage;

                    try{

                        var selectCheckType = $scope.caseRunStage[0].selectCheckType;
                        if (selectCheckType!=null){
                            for (var index in $scope.mutilCheckConfig) {
                                var type = $scope.mutilCheckConfig[index].value;

                                if($scope.mutilCheckConfig[index].default!=null){
                                    $scope.multiCheckSelected[type] = true;
                                }

                                if(selectCheckType[type]!=null) {
                                    if (!($scope.mutilCheckConfig[index].isSelect == true)) {

                                        $scope.mutilCheckConfig[index].isSelect = selectCheckType[type];
                                        $scope.multiCheckSelected[type] = selectCheckType[type];
                                    }else{
                                        //需要选上ad
                                        //$scope.caseRunStage[0].selectCheckType[type] = true;
                                        //$scope.multiCheckSelected[type] = true;
                                    }

                                }
                                else{
                                    $scope.mutilCheckConfig[index].isSelect = false;
                                    $scope.multiCheckSelected[type] = false;
                                }
                            }
                        }
                    }
                    catch (e){
                    }

                    try{
                        $scope.caseRunStage[0].data[0].input = JSON.stringify(angular.fromJson($scope.caseRunStage[0].data[0].input), undefined, 2);
                    }
                    catch (e){
                    }
                    try{
                        $scope.caseRunStage[0].data[0].expect = JSON.stringify(angular.fromJson($scope.caseRunStage[0].data[0].expect), undefined, 2);
                    }
                    catch (e){
                    }
                    try{
                        $scope.caseRunStage[0].data[0].actual = JSON.stringify(angular.fromJson($scope.caseRunStage[0].data[0].actual), undefined, 2);
                    }
                    catch (e){
                    }
                    tmp.prepareData = angular.copy(tmp.prepareData) ;
                    // 因为后端仅返回有内容字段 而前端渲染需要完整字段结构 所以单独对指定数据源赋值
                    for (var i in tmp.prepareData) {
                        if ($scope.ds.length <= i) {
                            $scope.addPrepareData();
                        }
                        for (var j in tmp.prepareData[i]) {
                            $scope.ds[i][j] = tmp.prepareData[i][j];
                            $scope.dataSource[i][j].selected = true;
                        }
                    }

                }else{

                }

                var checkSelected = false;
                angular.forEach($scope.multiCheckSelected, function(checkType , isChecked){
                    if(isChecked==true){
                        checkSelected = true;
                    }
                });
                if(checkSelected == false){
                    //默认选上ad
                    $scope.multiCheckSelected.ad = true;
                };

            }
            $scope.isShowForDeploy = [true, false];

            $scope.switchDeploy = function (id) {
                $scope.isShowForDeploy[id] = true;
                $scope.isShowForDeploy[1 - id] = false;
            }
            $scope.selectHost = function (x, xx) {
                if (xx in $scope.choose.host) {
                    delete $scope.choose.host[xx];
                } else {
                    $scope.choose.host[xx] = [];
                }
            }
            $scope.selectMachine = function (xx, yyy) {
                if ($.inArray(yyy, $scope.choose.host[xx])) {
                    $scope.choose.host[xx].splice($.inArray(yyy, $scope.choose.host[xx]), 1);
                } else {
                    $scope.choose.host[xx].push(yyy);
                }
            }


            $scope.getEnvStruct = function () {
                var res = {};
                var selectedHost = [];
                var selectedMachine = {};
                res[$scope.choose.cluster] = {};
                for (var i in $scope.hostList[$scope.choose.cluster]) {
                    var current = $scope.hostList[$scope.choose.cluster][i];
                    if (current.selected) {
                        var currentHost = current.hostName;
                        selectedHost.push(currentHost);
                        res[$scope.choose.cluster][currentHost] = [];
                        for (var k in current.roleList) {
                            var tmp = current.roleList[k];
                            if (tmp.selected) {
                                res[$scope.choose.cluster][currentHost].push(tmp.roleName);
                                if (currentHost in selectedMachine) {
                                    selectedMachine[currentHost].push(tmp.roleName);
                                } else {
                                    selectedMachine[currentHost] = [tmp.roleName];
                                }
                            }
                        }
                    }
                }
                return {
                    "selectedContent": res,
                    "selectedHost": selectedHost,
                    "selectedMachine": selectedMachine
                }
            }
            $scope.applyChoose = function () {
                alert("选择成功");
                $scope.envSelected = true;
                var envStruct = $scope.getEnvStruct();
                $localStorage.settings.userVersion = envStruct["selectedContent"];
                $scope.selectedVersion = {
                    "cluster": $scope.choose.cluster,
                    "host": envStruct["selectedHost"],
                    "machine": envStruct["selectedMachine"]
                }
            }
            $scope.applyChooseForDel = function () {
                var envStruct = $scope.getEnvStruct();
                $scope.selectedForDel = envStruct["selectedContent"];
            }

            $scope.choose = {
                cluster: "",
                host: {}
            };

            $scope.switchCluster = function (x) {
                if ($scope.choose.cluster != x) {
                    $scope.choose.cluster = x;
                    for (var i in $scope.hostList) {
                        if (i == x)
                            continue;
                        for (var j in $scope.hostList[i]) {
                            $scope.hostList[i][j].selected = false;
                            for (var k in $scope.hostList[i][j].roleList) {
                                $scope.hostList[i][j].roleList[k].selected = false;
                            }
                        }
                    }
                }
            }
            $scope.deploy = function (type = null) {
                $scope.deploy.state = 1;
                var buildmap = {};
                var currentCluster = $scope.clusterSel;
                var currentHost = $scope.hostSel;
                buildmap[currentCluster] = {};
                buildmap[currentCluster][currentHost] = {};
                for (var i in $scope.machinelist.role) {
                    var tmp = $scope.machinelist.role[i];
                    buildmap[currentCluster][currentHost][tmp.roleName] = tmp.build;
                }
                $scope.buildmap = buildmap;

                ZhiziService.deploy($scope.appid, $scope.sceneid, buildmap).then(function (res) {
                    var tmp = res.data;
                    if (tmp.success == false) {
                        $scope.deploy.state = 4;
                        $scope.deploy.failinfo = tmp.message;
                    } else {
                        var id = tmp.data;
                        $scope.deployRefreshcnt = 0;
                        $scope.getDeployRes(id, type);
                    }

                })
            }

            $scope.getDeployRes = function (id, type) {
                ZhiziService.getDeployStatus(id)
                    .then(function (res) {
                        $scope.deployRefreshcnt++;
                        var tmp = res.data;
                        var status = tmp.success;
                        var msg = tmp.message;
                        if (status) {
                            $scope.deploy.state = (msg == 'deploy success') ? 2 : 1;
                            if ($scope.deploy.state == 1) {
                                $timeout(function () {
                                    $scope.getDeployRes(id, type);
                                }, 10000);
                            } else {
                                if (type == "deployAndRun") {
                                    res = $scope.buildmap;
                                    $scope.runMulCase($scope.buildmap, type);
                                }
                                $scope.getVersion();
                            }
                        } else {
                            $scope.deploy.state = 4;
                            $scope.deploy.failinfo = msg;
                        }
                    })
            }
            $scope.addCase = function () {
                var caseName = $scope.case.caseName;
                var caseDesc = $scope.case.caseDesc;
                var caseLongDesc = $scope.case.caseLongDesc;
                var prepareData = $scope.getPrepareData();
                var caseRunStage = $scope.caseRunStage;
                var caseGroup = $scope.case.caseGroup;
                var tag = $scope.case.tag;
                var content = $scope.case.content;

                tempCaseRunStage =  angular.copy(caseRunStage);

                if ($scope.casePath == "caseJava" ){
                    var caseTemplate = "java";
                }
                else if ($scope.casePath == "caseBlink" ){
                    var caseTemplate = "blink";
                    tempCaseRunStage[0].data[0].actual = null;

                }
                else if ($scope.casePath == "caseStfp" ){
                    var caseTemplate = "stfp";
                }
                else{
                    var caseTemplate = "c++";
                    tempCaseRunStage[0].data[0].actual = null;

                }

                tempCaseRunStage[0].data[0].output = null;
                tempCaseRunStage[0].data[0].log = null;

                ZhiziService.addCase($scope.appid, $scope.sceneid, caseName, caseDesc, caseLongDesc, prepareData, tempCaseRunStage, caseGroup, tag, caseTemplate,content, $scope.currentBranch)
                    .then(function (res) {
                        if (res.data.success == true) {
                            $scope.formInfo = '保存成功';
                            $scope.case.caseId = res.data.data.id;
                            $scope.caseid = $scope.case.caseId;
                            // todo：change to edit mode
                            $scope.typeForCase = 'edit';

                            $scope.showBottomLabel = true;
                        } else {
                            $scope.formInfo = '保存失败，原因为:' + res.data.message;
                            $scope.showBottomLabel = true;
                        }
                    })
            }

            $scope.generateExpect = function(){
                if($scope.caseRunStage[0].selectCheckType == null || $scope.caseRunStage[0].selectCheckType == undefined ){
                    $scope.caseRunStage[0].selectCheckType = {
                        "ad":true
                    };
                }
                tempCaseRunStage = angular.copy($scope.caseRunStage);
                tempCaseRunStage[0].data[0].actual = null;
                tempCaseRunStage[0].data[0].output = null;
                tempCaseRunStage[0].data[0].log = null;

                ZhiziService.generateExpect(tempCaseRunStage)
                    .then(function (res) {
                        if (res.data.success == true) {
                            $scope.caseRunStage[0].data[0].expect = res.data.data;
                            alert("生成成功，请查看期望结果展示框." +
                                "\n您也可以直接运行用例，获取实际返回后，点击'<<'一键将实际结果转化为期望结果 ");
                        } else {
                            alert("生成失败")
                        }
                    })
            }

            $scope.editCase = function () {
                var caseName = $scope.case.caseName;
                var caseDesc = $scope.case.caseDesc;
                var caseLongDesc = $scope.case.caseLongDesc;
                var prepareData = $scope.getPrepareData();
                var caseRunStage = $scope.caseRunStage;
                var caseId = $scope.caseid;
                var caseGroup = $scope.case.caseGroup;
                var tag = $scope.case.tag;
                var content = $scope.case.content;
                tempCaseRunStage = angular.copy(caseRunStage);

                if ($scope.casePath == "caseJava" ){
                    var caseTemplate = "java";
                }
                else if ($scope.casePath == "caseBlink" ){
                    var caseTemplate = "blink";
                    tempCaseRunStage[0].data[0].actual = null;
                }
                else if ($scope.casePath == "caseStfp" ){
                    var caseTemplate = "stfp";
                    tempCaseRunStage[0].data[0].actual = null;
                }
                else{
                    var caseTemplate = "c++";
                }

                tempCaseRunStage[0].data[0].output = null;
                tempCaseRunStage[0].data[0].log = null;

                ZhiziService.editCase($scope.appid, $scope.sceneid, caseName, caseDesc, caseLongDesc, prepareData, tempCaseRunStage, caseId, caseGroup, tag, caseTemplate,content, $scope.currentBranch)
                    .then(function (res) {
                        if (res.data.success == true) {
                            $scope.formInfo = '保存成功';
                            $scope.case.caseId = res.data.data.id;
                            $scope.showBottomLabel = true;
                        } else {
                            $scope.formInfo = '保存失败，原因为:' + res.data.message;
                            $scope.showBottomLabel = true;
                        }
                    })
            }

            $scope.findRunStage = function (groupname) {
                for (var i in $scope.caseRunStage) {
                    if ($scope.caseRunStage[i].group_name == groupname) {
                        return i;
                    }
                }
                return -1;
            }
            $scope.runCase = function (prepare = true) {
                var caseName = $scope.case.caseName;
                var caseDesc = $scope.case.caseDesc;
                var caseLongDesc = $scope.case.caseLongDesc;
                var prepareData = $scope.getPrepareData();
                ;
                var caseRunStage = $scope.caseRunStage;
                var deploy = $localStorage.settings.userVersion;
                $scope.formInfo = "执行中";

                var userSelfEnv = GlobalStorage.getJSONObj("CurrentEnv_" + $scope.sceneid);

                if (!userSelfEnv || !userSelfEnv.id) {
                    alert("你未设置默认环境，请在部署环境页面选择一个环境最为默认环境");
                    $scope.formInfo = "请选择测试环境";
                    return;
                }

                $scope.showBottomLabel = false;
                $scope.progressType="info";
                $scope.dynamic=20;
                tempCaseRunStage = angular.copy(caseRunStage);
                tempCaseRunStage[0].data[0].actual = null;
                tempCaseRunStage[0].data[0].output = null;
                tempCaseRunStage[0].data[0].log = null;


                var envName = userSelfEnv.name;
                envInfo = null;
                ZhiziService.runCaseNew($scope.appid, $scope.sceneid, caseName, caseDesc, caseLongDesc, prepareData, tempCaseRunStage, envInfo,envName, prepare, $scope.case.caseId)
                    .then(function (res) {
                        if (res.data.success == true) {
                            var tmp = res.data.data;
                            $scope.formInfo = '运行结束,' + tmp.status;
                            $scope.case.logStage = tmp.logStage;
                            $scope.case.runStage = tmp.runStage;

                            if($scope.formInfo.indexOf("ERROR") >= 0){
                                $scope.progressType="danger";
                                $scope.dynamic=100;
                            }
                            if($scope.formInfo.indexOf("SUCCESS") >= 0){
                                $scope.progressType="success";
                                $scope.dynamic=100;
                            }

                            for (var i in $scope.case.runStage) {
                                for (var j in $scope.case.runStage[i]) {
                                    var indexRunStage = $scope.findRunStage(i);
                                    $scope.caseRunStage[indexRunStage].data[j] = $scope.case.runStage[i][j];
                                }
                            }

                            $scope.showLog($scope.pipeline.logStage[0].pluginType);
                        } else {
                            $scope.formInfo = '运行出错，错误提示:' + res.data.message;
                            if($scope.formInfo.indexOf("Permission Denied")>=0){
                                alert("请联系应用owner申请权限");
                            }else{
                                alert("运行出错："+res.data.message);
                            }
                            $scope.progressType="danger";
                            $scope.dynamic=100;
                        }
                    })

            }
            $scope.sleep = function (d) {
                var t = Date.now();
                while (Date.now() - t <= d) ;
            }




            $scope.sleep = function (d) {
                var t = Date.now();
                while (Date.now() - t <= d) ;
            }


            $scope.processBarHander = function(){

                if($scope.formInfo != $scope.lastBarText){
                    $scope.lastBarText = $scope.formInfo;
                    if($scope.formInfo.indexOf("运行结果")>=0 || $scope.formInfo.indexOf("运行结束")>=0){
                        //终态
                        if($scope.formInfo.indexOf("SUCCESS")>=0){
                            $scope.progressType="success";
                            $scope.dynamic=100;
                        }else if($scope.formInfo.indexOf("ERROR")>=0){
                            $scope.progressType="danger";
                            $scope.dynamic=100;
                        }
                    }else if($scope.formInfo.indexOf("运行出错")>=0){
                        //终态
                        $scope.progressType="danger";
                        $scope.dynamic=100;
                    }
                    else {
                        if($scope.dynamic < 80) {
                            $scope.dynamic = $scope.dynamic + 20;
                            $scope.progressType="info";
                        }
                    }
                }

            }

            $scope.getToubleShootFlag = function(){

                try{

                    if ($scope.troubleShootBoxStr.includes('"status":"ERROR"')){
                        $scope.troubleShootFlag = false;
                    }
                    else{
                        $scope.troubleShootFlag = true;
                    }
                }
                catch (e){
                    $scope.troubleShootFlag = true;
                }
            }

            $scope.getZKResCircle = function (taskId) {

                ZhiziService.getZkRes($scope.appid, $scope.sceneid, taskId)
                    .then(function (res) {
                        var data = res.data.data;
                        if (res.data.success == true) {
                            $scope.statusProgress = data.statusProgress;
                            $scope.zkLog = data.zkLog;
                            $scope.troubleShootBoxStr = data.troubleShootBox;
                            $scope.troubleShootBox = angular.fromJson(data.troubleShootBox);
                            $scope.getToubleShootFlag();
                            if ($scope.runCycleFlag && data.outParams == null) {
                                $scope.formInfo = '进度:' + data.status;
                                $timeout(function () {
                                    $scope.getZKResCircle(taskId);
                                }, 500);
                            }
                            //完成
                            else {
                                if (!$scope.runCycleFlag){
                                    $scope.formInfo = '运行结果:停止';
                                }
                                else{
                                    var outParams = angular.fromJson(data.outParams);
                                    $scope.formInfo = '运行结果:' + outParams.status;
                                    $scope.case.logStage = outParams.logStage;
                                    $scope.case.runStage = outParams.runStage;
                                    for (var i in $scope.case.runStage) {
                                        for (var j in $scope.case.runStage[i]) {
                                            var indexRunStage = $scope.findRunStage(i);
                                            $scope.caseRunStage[indexRunStage].data[j] = $scope.case.runStage[i][j];

                                            try {
                                                var actual = JSON.parse($scope.case.runStage[i][j]["actual"]);
                                                $scope.caseRunStage[indexRunStage].data[j]["actual"] = JSON.stringify(actual, null, "\t");
                                            } catch (e) {
                                            }
                                        }
                                    }


                                    if ($scope.casePath  == 'caseJava'){
                                        var inputObj = angular.fromJson($scope.caseRunStage[0].data[0].input);
                                        $scope.caseRunStage[0].data[0].input = inputObj["serviceName"];
                                        try {
                                            var output = JSON.parse($scope.caseRunStage[0].data[0].output);
                                            $scope.caseRunStage[0].data[0].output = JSON.stringify(output, null, "\t");
                                        } catch (e) {
                                        }
                                    }
                                    $scope.showLog($scope.pipeline.logStage[0].pluginType);
                                }


                            }
                        } else {
                            $scope.formInfo = '运行出错，错误提示:' + res.data.message;
                        }
                        $scope.processBarHander();
                    })
            }


            $scope.selectCheckType = function (key,value) {

                if (value == undefined){
                    value = false;
                }
                $scope.multiCheckSelected[key] = value;
                $scope.caseRunStage[0]["selectCheckType"] = $scope.multiCheckSelected;

                for (var i in $scope.mutilCheckConfig){
                    if($scope.mutilCheckConfig[i].value==key){
                        $scope.mutilCheckConfig[i].isSelect=value;
                    }
                }
            }


            $scope.openTraceModule = function (trace) {
                var scriptInstance = $modal.open({
                    templateUrl: 'trace.html',
                    windowClass: 'app-modal-window',
                    controller: ScriptInstanceCtrl,
                    keyboard: true,
                    backdrop: 'static'
                });

            }

            $scope.openTroubleShootBox = function (type, desc, detail) {

                $("#troubleShootBox").hide();

                var scriptInstance = $modal.open({
                    templateUrl: 'troubleShootBox.html',
                    windowClass: 'app-modal-window',
                    controller:troubleShootInstanceCtrl,
                    keyboard: false,
                    backdrop: 'static',
                    resolve: {

                        appId: function(){
                            return $scope.detail;
                        },
                        scenarioId: function() {
                            return $scope.sceneid;
                        },
                        type: function () {
                            return type;
                        },
                        desc: function () {
                            return desc;
                        },
                        detail:function(){
                            return detail;
                        }
                    }
                });
            }


            var troubleShootInstanceCtrl = function ($scope, ZhiziService, $modalInstance, appId, scenarioId, type, desc, detail) {

                $scope.viewDiffResult = false;
                $scope.checkInfoBox = {};

                $scope.diffConfig = {
                    leftTitle: "基准环境",
                    rightTitle: "测试环境",
                    leftData: "",
                    rightData: ""
                }
                $scope.init = function () {

                    $scope.viewDiffResult = false;

                    if (type=='[环境对比]检测'){
                        $scope.viewDiffResult = true;
                        var detailobj = angular.fromJson(detail);
                        $scope.diffConfig.leftData = detailobj.envBase;
                        $scope.diffConfig.rightData = detailobj.envDes;
                        $scope.checkInfoBox.desc = desc;
                    }
                    else{
                        $scope.viewDiffResult = false;
                        $scope.checkInfoBox.desc = desc;
                        $scope.checkInfoBox.detail = detail;
                    }
                }

                $scope.close = function () {
                    $("#troubleShootBox").show();

                    $modalInstance.dismiss('cancel');
                };
            };



            $scope.runAgentCase = function (type) {
                var caseName = $scope.case.caseName;
                var caseDesc = $scope.case.caseDesc;
                var caseLongDesc = $scope.case.caseLongDesc;
                var prepareData = $scope.getPrepareData();
                var caseRunStage = $scope.caseRunStage;
                var deploy = $localStorage.settings.userVersion;

                $scope.showBottomLabel = false;
                $scope.progressType="info";
                $scope.dynamic=20;

                $scope.url;
                $scope.refreshcnt = 0;
                $scope.cycleNum = "轮询次数:" + $scope.refreshcnt;
                $scope.formInfo = "进度:执行任务已发送,运行中..";
                ZhiziService.runCaseAgent($scope.appid, $scope.sceneid, caseName, caseDesc, caseLongDesc, prepareData, caseRunStage, deploy, type)
                    .then(function (res) {
                        if (res.data.success == true) {
                            var data = res.data.data;
                            $scope.url = data.url;
                            //5s后开始轮询
                            $scope.sleep(5000); //当前方法暂停5秒
                            $scope.refreshcnt++;
                            $scope.cycleNum = "轮询次数:" + $scope.refreshcnt;
                            //轮询查询请求
                            ZhiziService.getAgentRes($scope.appid, $scope.sceneid, $scope.url, deploy)
                                .then(function (res) {
                                    //如果完成
                                    var data = res.data.data;
                                    if (res.data.success == true) {
                                        $scope.formInfo = '进度:' + res.data.message;
                                        $scope.getAgentResCircle(deploy);
                                    }
                                    else {
                                        $scope.formInfo = '运行出错，错误提示:' + res.data.message;
                                        if($scope.formInfo.indexOf("Permission Denied")>=0){
                                            alert("请联系应用owner申请权限");
                                        }else{
                                            alert("运行出错："+res.data.message);
                                        }
                                        $scope.progressType="danger";
                                        $scope.dynamic=100;
                                    }
                                })
                        } else {
                            $scope.formInfo = '运行出错，错误提示:' + res.data.message;
                        }
                    })
            }




            $scope.getAgentResCircle = function (deploy) {

                ZhiziService.getAgentRes($scope.appid, $scope.sceneid, $scope.url, deploy)
                    .then(function (res) {
                        $scope.refreshcnt++;
                        $scope.cycleNum = "轮询次数:" + $scope.refreshcnt;
                        var data = res.data.data;
                        if (res.data.success == true) {
                            if (res.data.message != "finish") {
                                $scope.formInfo = '进度:' + res.data.message;
                                $timeout(function () {
                                    $scope.getAgentResCircle(deploy);
                                }, 3000);
                            }
                            //完成
                            else {
                                $scope.formInfo = '进度:' + res.data.message;
                                $scope.case.logStage = data.logStage;
                                $scope.case.runStage = data.runStage;
                                for (var i in $scope.case.runStage) {
                                    for (var j in $scope.case.runStage[i]) {
                                        var indexRunStage = $scope.findRunStage(i);
                                        $scope.caseRunStage[indexRunStage].data[j] = $scope.case.runStage[i][j];
                                    }
                                }
                                $scope.showLog($scope.pipeline.logStage[0].pluginType);
                            }
                        } else {
                            $scope.formInfo = '运行出错，错误提示:' + res.data.message;
                        }
                    })
            }

            $scope.replaceStatus = false;
            $scope.replacePlaceholder = function (prepare = true) {
                var caseName = $scope.case.caseName;
                var caseDesc = $scope.case.caseDesc;
                var caseLongDesc = $scope.case.caseLongDesc;
                var prepareData = $scope.getPrepareData();
                $scope.case.prepareData = prepareData;
                var caseRunStage = $scope.caseRunStage;
                $scope.case.caseRunStage = caseRunStage;
                var deploy = $localStorage.settings.userVersion;
                var caseId = $scope.case.caseId;
                $scope.bakup = {
                    "case": $scope.case,
                    "caseRunStage": $scope.caseRunStage,
                    "tag": $scope.case.tag,
                    "caseGroup": $scope.case.caseGroup,
                    "caseId": caseId
                }

                ZhiziService.replacePlaceholder(caseId, $scope.appid, $scope.sceneid, caseName, caseDesc, caseLongDesc, prepareData, caseRunStage, deploy, prepare)
                    .then(function (res) {
                        if (res.data.success == true) {
                            var tmp = res.data.data;

                            $scope.getCaseInfo(tmp);
                            $scope.case.tag = $scope.bakup.tag;
                            $scope.case.caseGroup = $scope.bakup.caseGroup;
                            $scope.case.caseId = $scope.bakup.caseId;
                            $scope.replaceStatus = true;
                            //$(".container :input").not('.alwaysEnable').attr("disabled", true);
                            $scope.formInfo = '占位符已替换，页面当前禁止编辑，显示内容即真实运行数据，若要返回编辑，点击取消占位符按钮即可。';

                        } else {

                        }
                    })
            }

            $scope.cancelReplace = function () {
                //$(".container :input").not('.alwaysEnable').attr("disabled", false);
                $scope.getCaseInfo($scope.bakup.case, 1);
                $scope.case.tag = $scope.bakup.tag;
                $scope.case.caseGroup = $scope.bakup.caseGroup;
                $scope.replaceStatus = false;
                $scope.formInfo = '';
            }

            $scope.exchangeFromActualToExpect = function (i, ii) {
                //$(".container :input").not('.alwaysEnable').attr("disabled", false);
                var exchangeParams = {
                    scenarioId: $scope.sceneid,
                    actual: $scope.caseRunStage[i].data[ii].actual
                }
                ZhiziService.exchangeFromActualToExpect(exchangeParams).then(function (res) {
                    var tmp = res.data;
                    if (tmp.success) {
                        $scope.caseRunStage[i].data[ii].expect = tmp.data;
                    } else {
                        alert("运行失败，错误信息为:" + tmp.message);
                    }
                })
            }

            $scope.runMulCase = function (deploy, type) {
                ZhiziService.runMulCase($scope.appid, $scope.sceneid, deploy, type == 'deployAndRun').then(function (res) {
                    var tmp = res.data;
                    if (tmp.success) {
                        alert(tmp.data);
                    } else {
                        alert("运行失败，错误信息为:" + tmp.message);
                    }
                })
            }

            $scope.runMulCaseAgent = function (deploy, type) {
                ZhiziService.runMulCase($scope.appid, $scope.sceneid, deploy, type == 'deployAndRun').then(function (res) {
                    var tmp = res.data;
                    if (tmp.success) {
                        alert(tmp.data);
                    } else {
                        alert("运行失败，错误信息为:" + tmp.message);
                    }
                })
            }

            $scope.regression = function (type) {
                if (type == 'run') {
                    var deploy = $localStorage.settings.userVersion;
                    $scope.runMulCase(deploy, type);
                } else {
                    $scope.deploy(type);
                }
            }

            $scope.regressionAgent = function (type) {
                if (type == 'run') {
                    var deploy = $localStorage.settings.userVersion;
                    $scope.runMulCaseAgent(deploy, type);
                } else {
                    $scope.deploy(type);
                }
            }


            $scope.showLog = function (name) {
                $scope.case.currentLog = $scope.case.logStage[name];
                $scope.case.currentLogType = [];
                for (var i in $scope.case.currentLog) {
                    $scope.case.currentLogType.push(i);
                    try{
                        $scope.case.currentLog[i] = JSON.stringify(angular.fromJson($scope.case.currentLog[i]), undefined, 2);
                    }
                    catch (e){

                    }
                }
                $scope.logType = $scope.case.currentLogType[0];
            }
            $scope.getPrepareData = function () {
                var res = [];
                for (var index in $scope.ds) {
                    var resobj = {};
                    var list = [];
                    for (var type in $scope.ds[index]) {
                        var content = $scope.ds[index][type];
                        resobj[type] = [];

                        for (var j in content) {
                            var tmpobj = {};
                            if (content[j].dsName == "" || content[j].dsName == undefined) {
                                continue;
                            }
                            tmpobj.dsName = content[j].dsName;
                            tmpobj.data = content[j].data;
                            tmpobj.restartFlag = content[j].restartFlag;
                            resobj[type].push(tmpobj);
                        }
                        if (resobj[type].length == 0) {
                            delete resobj[type];
                        }
                        else{
                            list.push(type);
                        }

                    }
                    if(list.length==0){
                        continue;
                    }

                    res.push(resobj);
                }

                return res;
            }

            /*$scope.getCaseRunStage = function(){
             $scope.caseRunStage = [{
             "group_name": "ERPC校验（第一组）",
             "data": [{
             "input": "version: 0\nuser_info {\n  user_id {\n    acookie: \"liantiao_uts_xalgo_7\"\n    acookie11: \"liantiao_uts_xalgo_7\"\n    nickname: \"liantiao_uts_xalgo_7\"\n  }\n}\ndevice_info {\n  traffic_type: PC\n  ip: \"10.232.131.80\"\n}\ncontext_info {\n  site {\n    content {\n      itemid: \"23123458\"\n    }\n  }\n}\nadz_info {\n  pid: \"prepare_uts_xalgo_8\"\n  base_cpc_price: 5\n  bidding_type: BIDDIG_TYPE_BID\n  settle_type: SETTLE_TYPE_CPC\n  banner {\n    size: \"\"\n  }\n  ad_bid_count: 4\n  max_ad_per_member: 2\n  refpid: \"\"\n  adchannel: 1\n  element_id: 1\n  adzone_type: 1\n  image_count: 1\n  ismobile: 1\n}\nsession_id: \"c18cda0a5e009940599e9c3700000057\"\nbiding_type: BT_CPC\nparam_manager {\n  tesla_param: \"\\n\\035\\n\\010BucketID\\022\\02121788718,41678530\\n\\023\\n\\016add_least_cate\\022\\0010\\n\\030\\n\\020ecpm_sort_enable\\022\\004true\\n\\021\\n\\tis_fusion\\022\\004true\\n\\030\\n\\017match_name_list\\022\\005xalgo\\n\\034\\n\\016qscore_version\\022\\ninnerScore\\n,\\n\\024strategy_flow.ad_bid\\022\\024EcpmScorer,GSPBidder\\n\/\\n\\032strategy_flow.ad_calibrate\\022\\021AutobidCalibrater\\n9\\n\\032strategy_flow.ad_diversity\\022\\033EcpmRandomDiversitySelector\\n)\\n\\027strategy_flow.ad_filter\\022\\016CategoryFilter\\n(\\n\\026strategy_flow.ad_score\\022\\016RealtimeScorer\\n`\\n\\027strategy_flow.ad_select\\022EAdvDiversitySelector,TargetPrioritySelector,CategoryDiversitySelector\\n$\\n\\nxalgo_list\\022\\026xalgo_etrecom_tongkuan\"\n  conf_param {\n    key: \"itemid\"\n    value: \"23123458\"\n  }\n  conf_param {\n    key: \"itemlist\"\n    value: \"23123458\"\n  }\n  conf_param {\n    key: \"count\"\n    value: \"4\"\n  }\n  conf_param {\n    key: \"bought_filter_level\"\n    value: \"1\"\n  }\n  conf_param {\n    key: \"bought_filter_days\"\n    value: \"19\"\n  }\n  conf_param {\n    key: \"elementid\"\n    value: \"1\"\n  }\n}\nrandom: false\ntesla:\nmatch_name_list=xalgo\nadd_least_cate=0\nxalgo_list=xalgo_etrecom_tongkuan\nis_fusion=true",
             "expect": "version: 1\nres_code: RC_SUCCESS\nuts_host_name: \"e010101080159.zmf\"\nuser_target {\n  target_info {\n    target_id: 0\n    match_type: 110\n    match_level: 1\n    query_type: 6\n    score: 75000000\n    rank_info: \"100,100,100\"\n    key_type: 0\n    target_unit {\n      value: \"8978961\"\n      score: 100\n    }\n    target_unit {\n      value: \"8978962\"\n      score: 100\n    }\n    target_unit {\n      value: \"8978963\"\n      score: 100\n    }\n  }\n}\nuser_info {\n  user_id {\n    acookie: \"liantiao_uts_xalgo_7\"\n    acookie11: \"liantiao_uts_xalgo_7\"\n    nickname: \"liantiao_uts_xalgo_7\"\n    aid: \"aid001\"\n  }\n}\nsessoin_id: \"c18cda0a5e009940599e9c3700000057\"\n\n"
             }]
             }]
             }*/

            $scope.addQuery = function (i) {
                $scope.caseRunStage[i].data.push({"input": "", "expect": ""});
            }

            $scope.rmQuery = function (i, ii) {
                var total = $scope.caseRunStage[i].data.length;
                for (var j = ii; j < total - 1; j++) {
                    $scope.caseRunStage[i].data[j] = $scope.caseRunStage[i].data[j + 1];
                }
                $scope.caseRunStage[i].data.pop();
            }
            $scope.toScene = function () {
                if ($scope.appid == 33) {
                    $window.location.href = "#!/app/quality?appid=" + $scope.appid + "&sceneid=" + $scope.sceneid;
                } else {
                    $window.location.href = "#!/app/index?appid=" + $scope.appid + "&sceneid=" + $scope.sceneid;
                }
            }
            $scope.toResultDetail = function () {
                $state.go("app.index", {appid: $scope.appid, sceneid: $scope.sceneid, testReportId: $scope.reportid});
            }

            $scope.cases_src = [];
            $scope.tmp_cases = [];
            $scope.caseFormData = {
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
                deploy: []
            }
            $scope.initcasetransfer = function () {
                ZhiziService.getBranchCaseGroups($scope.sceneid, 'master').then(function (res) {
                    if (res.data.success) {
                        $scope.cases_src = res.data.data.caseGroupList;
                        $scope.tmp_cases = res.data.data.caseGroupList;
                    }
                }, function (error) {
                    alert(JSON.stringify(error));
                });


                $scope.targetProduct = [];
                $scope.targetModule = [];
                $scope.targetScene = [];

                ZhiziService.loadBuInfo($scope.globalBuName).then(function (res) {

                    // ZhiziService.getAllScenes().then(function (res) {
                    if (res.data.success) {
                        angular.forEach(res.data.data.buinfo.menu, function (o) {
                            $scope.targetProduct.push(o.businessName);
                            $scope.targetProduct[o.businessName] = [];

                            angular.forEach(o.appMenuList, function (s) {
                                $scope.targetProduct[o.businessName].push(s.appName);
                                $scope.targetProduct[o.businessName][s.appName] = [];

                                angular.forEach(s.scenarioMenuList, function (t) {
                                    $scope.targetProduct[o.businessName][s.appName].push(t.scenarioId + " : "+ t.scenarioName);
                                });
                            });
                        });
                    } else {
                        alert("加载场景列表失败");
                    }
                });
            }


            $scope.loadModule = function () {
                if ($scope.targetProduct[$scope.selectTargetProduct]) {
                    $scope.targetModule = $scope.targetProduct[$scope.selectTargetProduct];
                } else {
                    $scope.targetModule = [];
                }
            }

            $scope.loadScene = function () {
                if ($scope.targetModule[$scope.selectTargetModule]) {
                    $scope.targetScene = $scope.targetModule[$scope.selectTargetModule];
                } else {
                    $scope.targetScene = [];
                }
            }




            $scope.addSelectCase = function () {
                angular.forEach($scope.caselist.srcCase, function (a) {
                    $scope.cases_src.splice($scope.cases_src.indexOf(a), 1);
                    $scope.caseFormData.cases.push(a);
                })
            }
            $scope.removeSelectCase = function () {
                angular.forEach($scope.caselist.selectCase, function (a) {
                    $scope.caseFormData.cases.splice($scope.caseFormData.cases.indexOf(a), 1);
                    $scope.cases_src.push(a);
                })
            }

            $scope.conflictCases = [];
            $scope.pickList = [];
            $scope.submitTransfer = function (){
                var transferParams = {
                    "exportSceneId" : $scope.sceneid,
                    "importSceneName" : $scope.selectTargetScene,
                    "caseGroupList" : $scope.caseFormData.cases
                }
                $scope.pickList = []
                var flatData = function (data) {
                    var result = [];
                    data.forEach(function (item) {
                        $scope.exConflictCaseIds.push(item.excaseid);
                        item.conflictCase.forEach(function (caseItem, index, arr) {
                            caseItem.groupFirstRow = !index;
                            caseItem.groupRowCount = arr.length;
                            caseItem.excaseid = index > 0 ? '' : item.excaseid;
                            caseItem.excasename = index > 0 ? '' : item.excasename;
                            caseItem.excasesceid = index > 0 ? '' : item.excasesceid;
                            caseItem.excaseappid = index > 0 ? '' : item.excaseappid;
                            result.push(caseItem);
                        });
                    });
                    return result;
                };

                toaster.pop("wait", "信息", "正在进行用例迁移....");
                ZhiziService.transferCases(transferParams).then(function (res) {
                    if (res.data.success) {
                        toaster.clear();
                        var successnum = res.data.data.transfernum;
                        var confictnum = res.data.data.conflictnum;
                        if(confictnum == 0){
                            toaster.pop("success", "信息","全部迁移成功，迁移成功用例数："+successnum);
                            $scope.conflictCases = [];
                        }else{
                            toaster.pop("success", "信息","迁移成功用例数："+successnum+", 冲突用例个数："+confictnum+", 请查看页尾冲突用例列表，确认后继续操作");
                            $scope.conflictCases = flatData(res.data.data.conflictinfo);
                        }
                    }else{
                        toaster.clear();
                        toaster.pop("error", "信息", "迁移失败，"+ res.data.message);
                    }
                }, function (error) {
                    alert("迁移失败："+res.data.message);
                });
            }

            $scope.exConflictCaseIds = [];
            $scope.checkCaseConflict = function () {
                $scope.pickList = []
                toaster.pop("wait", "信息", "正在进行冲突检测....");
                var transferParams = {
                    "exportSceneId" : $scope.sceneid,
                    "importSceneName" : $scope.selectTargetScene,
                    "caseGroupList" : $scope.caseFormData.cases
                }

                var flatData = function (data) {
                    var result = [];
                    data.forEach(function (item) {
                        $scope.exConflictCaseIds.push(item.excaseid);
                        item.conflictCase.forEach(function (caseItem, index, arr) {
                            caseItem.groupFirstRow = !index;
                            caseItem.groupRowCount = arr.length;
                            caseItem.excaseid = index > 0 ? '' : item.excaseid;
                            caseItem.excasename = index > 0 ? '' : item.excasename;
                            caseItem.excasesceid = index > 0 ? '' : item.excasesceid;
                            caseItem.excaseappid = index > 0 ? '' : item.excaseappid;
                            result.push(caseItem);
                        });
                    });
                    return result;
                };

                ZhiziService.checkCaseConflict(transferParams).then(function (res) {
                    if (res.data.success) {
                        toaster.clear();
                        if(res.data.data.length>0){
                            toaster.pop("success", "信息", "冲突检测成功，请查看页尾");
                            $scope.conflictCases = flatData(res.data.data);
                        }else{
                            toaster.pop("success", "信息", "冲突检测成功，不存在用例冲突");
                        }

                    }else {
                        toaster.clear();
                        toaster.pop("error", "信息", "冲突检测失败，"+ res.data.message);
                    }
                }, function (error) {
                    toaster.clear();
                    toaster.pop("error", "信息", "冲突检测失败，"+ res.data.message);
                });
            }

            $scope.toSnapEdit = function(caseid, actiontype, reportid = null){
                if( reportid!=null){
                    $scope.isTestReport = true;
                }
                else{
                    $scope.isTestReport = false;
                }

                if($scope.templateType == "JAVA-Alimama-template"){
                    //var tmp = "#!/app/caseJava?appid=" + $scope.appid +"&sceneid=" +$scope.scenarioId + "&type=" + actiontype + "&caseid=" + caseid;
                    var tmp = "#!/app/caseJava?appid=" + $scope.appid +"&sceneid=" +$scope.sceneid + "&type="+actiontype  + "&caseid=" + caseid;

                }
                else if($scope.templateType == "Blink-Alimama-template"){
                    var tmp = "#!/app/caseBlink?appid=" + $scope.appid +"&sceneid=" +$scope.sceneid + "&type=" + actiontype + "&caseid=" + caseid;
                }
                else if($scope.templateType == "stfp-Alimama-template"){
                    var tmp = "#!/app/caseStfp?appid=" + $scope.appid +"&sceneid=" +$scope.sceneid + "&type=" + actiontype + "&caseid=" + caseid;
                }
                else{
                    var tmp = "#!/app/case?appid=" + $scope.appid +"&sceneid=" +$scope.sceneid + "&type=" + actiontype + "&caseid=" + caseid;
                }

                if (actiontype == "singleResult"){
                    tmp += "&reportid=" + reportid;
                }
                if (actiontype == "edit" && reportid!=null){
                    tmp += "&testReportId=" + reportid;

                }
                if(actiontype == "expand"){
                    tmp = "#!/app/expand?appid=" + $scope.appid +"&sceneid=" +$scope.sceneid + "&type=" + actiontype + "&caseid=" + caseid;
                }
                if(actiontype == "edit" && reportid==null){
                    $window.location.href = tmp;
                }else {
                    $window.open(tmp);
                }
            }

            $scope.toEdit = function(caseid, appid, sceid, actiontype,casetype){
                var tmp = "#!/app/case?appid=" + appid +"&sceneid=" + sceid + "&type=" + actiontype + "&caseid=" + caseid;

                if(actiontype == "expand"){
                    tmp = "#!/app/expand?appid=" + appid +"&sceneid=" + sceid + "&type=" + actiontype + "&caseid=" + caseid;
                    $window.location.href = tmp;
                }
                else if(actiontype == "snapsToEdit" && casetype== "java"){
                    tmp = "#!/app/caseJava?appid=" + appid +"&sceneid=" + sceid + "&type=edit"  + "&caseid=" + caseid;
                    $window.open(tmp);
                }
                else if(actiontype == "snapsToEdit" && casetype== "blink"){
                    tmp = "#!/app/caseBlink?appid=" + appid +"&sceneid=" + sceid + "&type=edit"  + "&caseid=" + caseid;
                    $window.open(tmp);
                }
                else if(actiontype == "snapsToEdit"){
                    tmp = "#!/app/case?appid=" + appid +"&sceneid=" + sceid + "&type=edit"  + "&caseid=" + caseid;
                    $window.open(tmp);
                }
                else{
                    $window.location.href = tmp;
                }

            }


            $scope.toModifySnapEdit = function(caseid, actiontype, snapId ){


                if($scope.templateType == "JAVA-Alimama-template"){
                    //var tmp = "#!/app/caseJava?appid=" + $scope.appid +"&sceneid=" +$scope.scenarioId + "&type=" + actiontype + "&caseid=" + caseid;
                    var tmp = "#!/app/caseJava?appid=" + $scope.appid +"&sceneid=" +$scope.sceneid + "&type="+actiontype  + "&caseid=" + caseid+ "&snapId=" + snapId;

                }
                else if($scope.templateType == "Blink-Alimama-template"){
                    var tmp = "#!/app/caseBlink?appid=" + $scope.appid +"&sceneid=" +$scope.sceneid + "&type=" + actiontype + "&caseid=" + caseid+ "&snapId=" + snapId;
                }
                else if($scope.templateType == "stfp-Alimama-template"){
                    var tmp = "#!/app/caseStfp?appid=" + $scope.appid +"&sceneid=" +$scope.sceneid + "&type=" + actiontype + "&caseid=" + caseid+ "&snapId=" + snapId;
                }
                else{
                    var tmp = "#!/app/case?appid=" + $scope.appid +"&sceneid=" +$scope.sceneid + "&type=" + actiontype + "&caseid=" + caseid+ "&snapId=" + snapId;
                }

                $window.open(tmp);

            }



            $scope.pickCase = function(excaseId){
                var pos = $scope.pickList.indexOf(excaseId);
                if (pos != -1){
                    $scope.pickList.splice(pos, 1);
                }else{
                    $scope.pickList.push(excaseId);
                }
            }





            $scope.transferConflict = function(pickList, transfertype){
                var transferParams = {
                    "caseIds" : pickList,
                    "importSceneName" : $scope.selectTargetScene,
                    "transferType" : transfertype
                }
                toaster.pop("wait", "信息", "正在进行用例迁移....");
                ZhiziService.transferCasesById(transferParams).then(function (res) {
                    if (res.data.success) {
                        toaster.clear();
                        var successnum = res.data.data.transfernum;
                        var overridenum = res.data.data.overridenum;
                        if(overridenum == 0){
                            toaster.pop("success", "信息","迁移成功用例数："+successnum);
                            //$scope.conflictCases = [];
                        }else{
                            toaster.pop("success", "信息","迁移成功用例数："+successnum+", 覆盖用例个数："+overridenum);
                            //$scope.conflictCases = flatData(res.data.data.conflictinfo);
                            $scope.pickList = []
                        }
                    }else{
                        toaster.clear();
                        toaster.pop("error", "信息", "迁移失败，"+ res.data.message);
                        $scope.pickList = []
                    }
                }, function (error) {
                    alert("迁移失败："+res.data.message);
                });
            }
            $scope.initBusInfo = function(){
                ZhiziService.initBusInfo().then(function (res) {
                    $scope.bunames=res.data.data.bunames;
                }, function (error) {
                    alert("获取bu列表失败："+res.data.message);
                });
            }

            $scope.loadBuInfo = function(){
                var name = $scope.curBuName;
                ZhiziService.loadBuInfo(name).then(function (result) {
                    var data = result.data;
                    if (data.success) {
                        var tmp = data.data.buinfo;
                        var res = UserService.getMenu(tmp);
                        $scope.curBuId=tmp.buid;
                        $scope.applist = res.applist;
                        $scope.scenariolist = res.scenariolist;
                        $scope.menus = res.finalmenu;
                        $scope.appSecneMap = res.appSecneMap;
                        $scope.bunames = data.data.busnames.bunames;
                    }
                }, function (error) {
                    alert("获取bu列表失败："+res.data.message);
                });
            }

        }]);

