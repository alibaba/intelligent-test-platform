angular.module('app.controllers.RegController', ['pascalprecht.translate', 'ngCookies','ngSanitize', 'ui.select'])

    .controller('RegController', ['$scope','$location','$stateParams','$state','$interval','$modal','NgTableParams','toaster','UserService','GlobalStorage','ZhiziService',
        function($scope,$location,$stateParams,$state,$interval,$modal,NgTableParams,toaster,UserService,GlobalStorage,ZhiziService){
            $scope.topos = []
            $scope.topolist = [];
            $scope.selTab = {tab:'runTest',autoRefresh:false};
            $scope.smartRreg = true;
            $scope.regAccuracy = true;
            $scope.hosts = [];
            $scope.caselist = {};
            $scope.existEnvs = [];
            $scope.selectAllModule = false;
            $scope.hostsBaseNumSelect = [];
            $scope.submitDisabled = false;
            $scope.historys = {};
            $scope.currentIdx = {};
            $scope.historyList = [];
            $scope.recentData = {taskInfo:{}, envs:[]};
            $scope.sceneid = $stateParams.sceneid;
            $scope.appid = $stateParams.appid;
            $scope.branchList = [];
            $scope.branchInfo = {};
            $scope.branchNameList = [];
            $scope.imageRelate = false;
            $scope.accuDefaultFlag = false;


            var scene = $scope.productInfo.productModule.split('-')[0];
            $scope.selectScenarioCase = 1;

            //$scope.selectScenarioCase = 0;
            //if (scene==18 || scene==64 || scene==70 || scene==72 ){
            //    $scope.selectScenarioCase=1;
            //}
            $scope.tmp_cases = [];
            $scope.tmp_scenarios = [];
            $scope.baseCodeMirrorConfig = {
                lineNumbers: true,
                lineWrapping: false,
                foldGutter: true,
                docEnd:false,
                lint: false,
                width:'100%',
                height:'200px',
                //theme:'erlang-dark',
                gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter","CodeMirror-lint-markers"],
                mode: {name: "javascript", globalVars: true},
            };
            $scope.pageRunType = {name:"",code:0};
            if($stateParams.ptype == 0){
                $scope.pageRunType.code = 0;
                $scope.pageRunType.name = "回归测试";
            } else if($stateParams.ptype == 1){
                $scope.pageRunType.code = 1;
                $scope.pageRunType.name = "托管模式";
            } else {
                $scope.pageRunType.code = 0;
                $scope.pageRunType.name = "回归测试";
            }
            $scope.formData = {
                taskType:$scope.pageRunType.code,
                mode:"multi",
                type:"new",
                script:"",
                envName:"",
                callbackHost:"",
                selectModels:{},
                params:{},
                caseType:0,
                cases:[],
                hosts:[],
                selectHosts:{},
                existEnvSelect:{},
                deploy:[],
                productInfo:$scope.productInfo
            };
            $scope.accuracyFormData = {
                caseType:"userCase",
                testBranch:"",
                newCommit:"",
                oldCommit:"",
                accuracyFlag:false,
                buildBase:false,
                queryRegFlag:false
            };

            $scope.canQueryReg = false;

            $scope.cases_src = [];
            var self = this;
            $scope.fltstatus = [{id:"SUCCESS",title:"SUCCESS"},{id:"FAIL",title:"FAIL"},{id:"EXCEPTION",title:"EXCEPTION"},{id:"RUNNING",title:"RUNNING"}];


            $scope.getBranchCaseGroups = function(branch){
                $scope.currentBranch = branch;
                ZhiziService.getBranchCaseGroups($scope.sceneid, branch).then(function (res) {
                    $scope.cases_src = res.data.data.caseGroupList;
                });
            }

            $scope.selectAccuracyRun = function(accuFlag){
                $scope.accuracyFormData.accuracyFlag = accuFlag;
            }

            $scope.selectSmartRreg = function(smartRreg){
                $scope.smartRreg = smartRreg;
            }
            $scope.selectRegAccuracy = function(regAccuracy){
                $scope.regAccuracy = regAccuracy;
            }

            $scope.selectRegBase = function(buildAccuBaseFlag){
                $scope.accuracyFormData.buildBase = buildAccuBaseFlag;
            }
            $scope.selectQueryReg = function(queryRegFlag){
                $scope.accuracyFormData.queryRegFlag = queryRegFlag;
            }

            $scope.model = {};
            $scope.model.caseType = 0;
            $scope.selectCaseType = function(type){
                $scope.cases_src = [];
                $scope.formData.cases = [];
                $scope.formData.caseType = type;
                if($scope.onlyTrunkRunFlag == true && type == 0){
                    type =2 ;
                    $scope.formData.caseType = 2;
                }
                if($scope.onlyTrunkRunFlag == true && type == 1){
                    type =3 ;
                    $scope.formData.caseType = 3;
                }

                if(type == 0 || type == 2){
                    $scope.cases_src = angular.copy($scope.tmp_cases);
                }
                $scope.showCaseType=type;
                if(type == 1 || type == 3){
                    angular.forEach($scope.menus,function(m){
                        if((m.businessId+"-"+m.topName) == $scope.productInfo.productLine){
                            angular.forEach(m.leftMenus,function(n){
                                if((n.appid+"-"+n.name) == $scope.productInfo.productModule){
                                    angular.forEach(n.son,function(s){

                                        var caseGroupList = null;
                                        ZhiziService.getBranchCaseGroups(s.scenarioId, $scope.currentBranch).then(function(res){
                                            if(res.data.success){
                                                caseGroupList = res.data.data.caseGroupList;
                                                if(caseGroupList!=null && caseGroupList.length > 0){
                                                    for (var i in caseGroupList){
                                                        var caseGroup = caseGroupList[i];
                                                        $scope.cases_src.push(s.scenarioId+"__"+s.scenarioName+ "__"+caseGroup);
                                                    }
                                                }
                                            }
                                        },function(error){
                                            alert(JSON.stringify(error));
                                        });

                                    })
                                }
                            })
                        }
                    })
                }
            }

            $scope.loadRecentPage = function(){
                if($scope.recentTimer){
                    $interval.cancel($scope.recentTimer);
                }

                $scope.selTab.tab='recent';
                $scope.loadRecent();
                if($scope.selTab.autoRefresh){
                    $scope.recentTimer = $interval(function(){
                        $scope.loadRecent();
                    },3000);
                }
            }

            $scope.loadRunTestPage = function(){
                if($scope.recentTimer){
                    $interval.cancel($scope.recentTimer);
                }
                $scope.selTab.tab='runTest';
            }

            $scope.loadRunAccuracyPage = function(){
                if($scope.recentTimer){
                    $interval.cancel($scope.recentTimer);
                }
                $scope.selTab.tab='runAccuracy';
            }


            $scope.loadHistory = function(){
                if($scope.recentTimer){
                    $interval.cancel($scope.recentTimer);
                }
                $scope.selTab.tab='history';
                $scope.historys = {};
                $scope.historyList = [];
                ZhiziService.getExecList($stateParams.sceneid).then(function(resdata){
                    toaster.clear();
                    if(resdata.data.success){
                        $scope.historyList = resdata.data.data;
                        self.historys = new NgTableParams({count:10,sorting: {id: "desc"}}, {counts: [10,30,50,100],paginationMaxBlocks: 10,paginationMinBlocks: 2,dataset: $scope.historyList});
                    } else {
                        toaster.pop("error","信息",resdata.data.message);
                    }
                },function(error){
                    alert(JSON.stringify(error));
                })
            }

            $scope.doSelectAllModule = function(){
                $scope.selectAllModule = !$scope.selectAllModule;
                angular.forEach($scope.topolist,function(f){
                    $scope.formData.selectModels[f.module] = $scope.selectAllModule;
                })
            }
            var ScriptInstanceCtrl = function ($scope, ZhiziService,$modalInstance,codeMirrorConfig,script) {
                codeMirrorConfig.theme = 'erlang-dark';
                codeMirrorConfig.height = '600px';
                $scope.scriptOptions = codeMirrorConfig;
                $scope.trace = {
                    modeName:"普通模式",
                    tcontent:script
                }
                $scope.changeMode = function(mode){
                    var jsmode = angular.copy(codeMirrorConfig);
                    jsmode.lint = mode == "application/json"?true:false;
                    jsmode.mode.name = mode;
                    $scope.scriptOptions = jsmode;
                    if(jsmode.lint == true){
                        $scope.trace.modeName = "JSON模式";
                    } else {
                        $scope.trace.modeName = "普通模式";
                    }
                }

                $scope.close = function () {
                    $modalInstance.dismiss('cancel');
                };
                $scope.completed = function () {
                    if(!$scope.trace.tcontent){
                        alert("脚本内容不能为空");
                        return;
                    }
                    $modalInstance.close($scope.trace.tcontent);
                };
            }
            $scope.openScriptModule = function (type) {
                var scriptInstance = $modal.open({
                    templateUrl: 'smoke_modal.html',
                    windowClass: 'app-modal-window',
                    controller:ScriptInstanceCtrl,
                    keyboard:false,
                    backdrop: 'static',
                    resolve: {
                        codeMirrorConfig:function(){
                            return angular.copy($scope.baseCodeMirrorConfig);
                        },
                        script:function(){
                            return type == 0?"":JSON.stringify(angular.fromJson($scope.recentData.taskInfo.configSnapshot), null, "\t");
                        }
                    }
                });
                scriptInstance.result.then(function(result){
                    if(type == 0){
                        $scope.formData.script = result;
                    }
                },function(reason){

                })
            }


            $scope.pickImage = function(currentImagePicked){
                $scope.currentImagePicked = currentImagePicked;

            }

            $scope.setImageInputType = function(imageInputType){
                $scope.imageInputType = imageInputType;
            }

            $scope.initCaseBank = function () {
                if($scope.currentBranch==undefined || $scope.currentBranch==""){
                    $scope.currentBranch = "master";
                }
                var data = new Date();
                $scope.currentTime = data.getTime();
            };

            $scope.initTopos = function(){
                $scope.smartRreg = true;
                $scope.buildModelDesc = {};
                $scope.existEnvs = [];
                $scope.currentIdx = {};
                //$scope.loadRecentPage();
                $scope.loadRunTestPage();
                $scope.initCaseBank();

                ZhiziService.getBranchCaseGroups($stateParams.sceneid).then(function(res){
                    if(res.data.success){
                        $scope.cases_src = res.data.data.caseGroupList;
                        $scope.tmp_cases = res.data.data.caseGroupList;
                    }
                },function(error){
                    alert(JSON.stringify(error));
                });

                ZhiziService.getPipelineData($stateParams.sceneid).then(function(resp){
                    var pipObj = angular.fromJson(resp.data.data);
                    console.log(pipObj);
                    if(pipObj["new-deploy-stage"]){
                        $scope.topos = pipObj["new-deploy-stage"]["topo"];
                        $scope.formData.callbackHost = pipObj["new-deploy-stage"]["callbackHost"]||"";
                        $scope.formData.deploy = $scope.topos;

                        angular.forEach($scope.topos,function(o){
                            var defaultDesc = "请输入"+o.module+"的参数1";
                            $scope.formData.params[o.module] = {};
                            $scope.formData.params[o.module][0] = {key:o.module,data:"",desc:defaultDesc};
                            if(o.buildDesc){
                                $scope.currentIdx[o.module] = o.buildDesc.length-1;
                                $scope.buildModelDesc[o.module] = o.buildDesc;
                                if(o.buildDesc.length > 0){
                                    for(var ii = 0;ii<o.buildDesc.length;ii++){
                                        $scope.formData.params[o.module][ii] = {
                                            key  : o.buildDesc[ii].key||(ii == 0?o.module:""),
                                            desc : o.buildDesc[ii].desc||"",
                                            data : o.buildDesc[ii].defaultValue||""
                                        }
                                    }
                                }
                            } else {
                                $scope.currentIdx[o.module] = 0;
                            }
                            $scope.topolist.push(o);
                        })
                        $scope.hosts = pipObj["new-deploy-stage"]["hostList"];
                        angular.forEach($scope.hosts,function(h){
                            var hn = {ip:h.ip,envNum:[]};
                            for(var i=0;i<h.maxEnvNum;i++){
                                hn.envNum.push(i+1);
                            }
                            $scope.hostsBaseNumSelect.push(hn);
                        });


                        var curDate = new Date();
                        var month = curDate.getMonth() + 1;
                        var time = curDate.getFullYear() + "-" + month + "-" + curDate.getDate() + "-" + curDate.getHours() + ":" + curDate.getMinutes() + ":" + curDate.getSeconds();
                        $scope.formData.envName =  "markov-" + $scope.productInfo.productSubModule + "-" + time;


                        ZhiziService.listGroupByHost($scope.sceneid).then(function(res) {
                            if (res.data.success) {
                                $scope.existEnvs = res.data.data;
                            }
                        });

                    } else {
                        alert("获取用户信息失败,"+res.data.message);
                    }


                },function(error){
                    alert("初始化pipeLine失败，"+JSON.stringify(error));
                });
            }
            $scope.addParams = function(tl,idx){
                $scope.currentIdx[tl.module] = parseInt(idx)+1;
                var defaultDesc = "请输入"+tl.module+"的参数"+$scope.currentIdx[tl.module];
                if($scope.buildModelDesc[tl.module]){
                    if($scope.buildModelDesc[tl.module].length > $scope.currentIdx[tl.module]){
                        $scope.formData.params[tl.module][$scope.currentIdx[tl.module]] = {key:$scope.buildModelDesc[tl.module][$scope.currentIdx[tl.module]].key||"",data:$scope.buildModelDesc[tl.module][$scope.currentIdx[tl.module]].defaultValue||"",desc:$scope.buildModelDesc[tl.module][$scope.currentIdx[tl.module]].desc||defaultDesc};
                    } else {
                        $scope.formData.params[tl.module][$scope.currentIdx[tl.module]] = {key:"",data:"",desc:defaultDesc};
                    }
                } else {
                    $scope.formData.params[tl.module][$scope.currentIdx[tl.module]] = {key:"",data:"",desc:defaultDesc};
                }
            }
            $scope.delParams = function(tl,idx){
                $scope.currentIdx[tl.module] = 0;
                delete $scope.formData.params[tl.module][idx];
                var pmobjs = angular.copy($scope.formData.params[tl.module]);
                $scope.formData.params[tl.module] = {};
                var ii =0 ;
                angular.forEach(pmobjs,function(v,k){
                    $scope.formData.params[tl.module][ii] = v;
                    ii++;
                });
                $scope.currentIdx[tl.module] = ii-1;
            }
            $scope.addSelectCase = function(){
                angular.forEach($scope.caselist.srcCase,function(a){
                    $scope.cases_src.splice($scope.cases_src.indexOf(a),1);
                    $scope.formData.cases.push(a);
                })
            }
            $scope.loadTaskDetail = function(env){
                window.open($state.href("app.envdetail",{envId:env.envId,module:env.moduleName,scenarioId:$scope.sceneid}),"_blank");
            }
            $scope.removeSelectCase = function(){
                angular.forEach($scope.caselist.selectCase,function(a){
                    $scope.formData.cases.splice($scope.formData.cases.indexOf(a),1);
                    $scope.cases_src.push(a);
                })
            }
            $scope.$on('$destroy',function(){
                if($scope.recentTimer)$interval.cancel($scope.recentTimer);
            });

            $scope.$watch("selTab.autoRefresh",function(newVal,oldVal){
                if(oldVal == newVal)return;
                if(newVal == true){
                    $scope.recentTimer = $interval(function(){
                        $scope.loadRecent();
                    },3000);
                } else {
                    if($scope.recentTimer){
                        $interval.cancel($scope.recentTimer);
                    }
                }
            });

            $scope.selectOnlyTrunkRunFlag = function(onlyTrunkRunFlag){
                $scope.onlyTrunkRunFlag = onlyTrunkRunFlag;

                if($scope.onlyTrunkRunFlag == true && $scope.formData.caseType==0){
                    $scope.formData.caseType=2;
                }
                if($scope.onlyTrunkRunFlag == true && $scope.formData.caseType==1){
                    $scope.formData.caseType=3;
                }

                if($scope.onlyTrunkRunFlag == false && $scope.formData.caseType==3){
                    $scope.formData.caseType = 1;
                }

                if($scope.onlyTrunkRunFlag == false && $scope.formData.caseType==2){
                    $scope.formData.caseType=0;
                }
            }


            $scope.saveForm = function(){
                var isSelectModels = false;
                var isSelectHosts = false;
                var isEnvSelect = false;

                if($scope.formData.type == 'old'){
                    $scope.accuracyFormData.oldCommit = "";
                    $scope.accuracyFormData.newCommit = "";
                }

                $scope.formData.scenarioId = $scope.sceneid;


                var runTaskParams = {
                    envInfo:{},
                    config:"",
                    callbackHost:$scope.formData.callbackHost,
                    script:$scope.formData.script,
                    taskType:$scope.formData.taskType,
                    taskName:$scope.formData.envName,
                    caseType:$scope.formData.caseType,//0:cases,1:scenarios,2,cases-trunk,3,scenarios-trunk
                    caseGroup:[],
                    execId:new Date().getTime()+"",
                    creator:$scope.user.nickName,
                    runType:"手动触发",
                    scenarioId:$stateParams.sceneid,
                    appId:$stateParams.appid,
                    openSmartRegress:$scope.smartRreg,
                    regAccuracy:$scope.regAccuracy,
                    branchName:$scope.currentBranch
                }


                var selectTypeName = "";
                if($scope.formData.caseType == 0 || $scope.formData.caseType == 2){
                    selectTypeName = "用例";
                    runTaskParams.caseGroup = $scope.formData.cases;
                }
                if($scope.formData.caseType == 1 || $scope.formData.caseType == 3){
                    selectTypeName = "场景";
                    angular.forEach($scope.formData.cases,function(cc){
                        runTaskParams.caseGroup.push(cc.split("__")[0]);
                    })
                }
                toaster.pop("wait","信息","正在执行操作...");
                if(runTaskParams.caseGroup.length == 0 && !$scope.accuracyFormData.accuracyFlag){
                    toaster.clear();
                    toaster.pop("error","信息","必须选择"+selectTypeName);
                    return;
                }else if(runTaskParams.caseGroup.length == 0 && $scope.accuracyFormData.accuracyFlag){
                    runTaskParams.caseGroup[0] = "accuracy recommend case"
                }
                //新建环境并执行回归
                if($scope.formData.type == "new"){
                    var hostIPs = {ipList:[]};
                    var _hosts = angular.copy($scope.hosts);
                    angular.forEach($scope.formData.selectModels,function(v,k){
                        if(v == true){
                            isSelectModels = true;
                        }
                    })
                    if(!isSelectModels){
                        toaster.clear();
                        toaster.pop("error","信息","必须选择模块");
                        return;
                    }
                    var i=0;
                    var selectIp = {"ip":""};
                    angular.forEach($scope.formData.selectHosts,function(v,k){
                        selectIp.ip = k;
                        $scope.formData.hostInfo = selectIp;
                        if(v.select == true){
                            isSelectHosts = true;
                            angular.forEach(_hosts,function(h){
                                if(k == h.ip){
                                    hostIPs.ipList.push(k);
                                    angular.forEach(v,function(v1,k1){
                                        h[k1]=v1;
                                    });
                                    var exsit =false;
                                    angular.forEach($scope.formData.hosts,function(l){
                                        if(l.hostname = h.hostname){
                                            exsit = true;
                                        }
                                    });
                                    if(exsit==false) {
                                        $scope.formData.hosts.push(h);
                                    }
                                }
                            })
                        }
                        i++;

                    });
                    if(!isSelectHosts){
                        toaster.clear();
                        toaster.pop("error","信息","必须选择宿主机");
                        return;
                    }

                    var dumpKeyStr = "";

                    angular.forEach($scope.formData.params,function(v, module){
                        if ($scope.formData.params[module]["0"] != undefined) {
                            if($scope.imageInputType == "choose"){
                                $scope.formData.params[module][0].data = $scope.currentImagePicked;
                            }
                        }
                    })

                    angular.forEach($scope.formData.selectModels,function(v5,k5){
                        if(v5 == true){
                            var dumpKey = [];

                            angular.forEach($scope.formData.params[k5],function(v3,module){
                                if($.inArray(v3.key,dumpKey)>0){
                                    dumpKeyStr += v5+" 模块中存在重复的参数key："+v3.key+"，请检查\n";
                                } else {
                                    dumpKey.push(v3.key);
                                }
                            });
                        }
                    })


                    if(dumpKeyStr != ""){
                        alert(dumpKeyStr);
                        return;
                    }
                    $scope.submitDisabled = true;

                    ZhiziService.addEnv($scope.formData).then(function (resdata) {
                        if (resdata.data.success) {
                            //执行回归
                            ZhiziService.runMultiCase(runTaskParams).then(function (data){
                                if(resdata.data.success){
                                    alert("回归任务提交完成,请去执行历史查看!");
                                    //toaster.pop("success","信息","回归任务提交完成");
                                    //$state.go("app.index", {appid: $scope.appid, sceneid: $scope.sceneid, testReportId: $scope.reportid});
                                } else {
                                    toaster.pop("error","提交失败",resdata.data.message);
                                }
                            });
                        }
                    }, function (error) {
                        alert("创建失败:" + JSON.stringify(error));
                    })
                }
                //使用老环境进行回归
                else {
                    var envIds = {idList:[]};
                    angular.forEach($scope.formData.existEnvSelect,function(v,k){
                        if(v.select == true){
                            isSelectHosts = true;
                            if(!v.envs || v.envs.length == 0){
                                alert(k+"必须选择至少一个环境");
                                isEnvSelect = false;
                                return;
                            } else {
                                isEnvSelect = true;
                                angular.forEach(v.envs,function(n){
                                    envIds.idList.push(n.id);
                                })
                            }
                        }
                    });

                    if(!isSelectHosts){
                        toaster.clear();
                        toaster.pop("error","信息","必须选择宿主机");
                        return;
                    }
                    if(!isEnvSelect){
                        return;
                    }
                    $scope.submitDisabled = true;

                    ZhiziService.runMultiCase(runTaskParams).then(function (resdata){
                        if(resdata.data.success){
                            //toaster.pop("success","信息","回归任务提交完成");
                            alert("回归任务提交完成,请去执行历史查看!");
                        } else {
                            toaster.pop("error","提交失败",resdata.data.message);
                        }
                    });

                }
                toaster.clear();
            }

        }
    ]);