angular.module('app.controllers.pipelineController', ['pascalprecht.translate', 'ngCookies','ngSanitize', 'ui.select'])
    .controller('pipelineController', ['$scope','$location','$stateParams','UserService','ZhiziService','$compile','$window','$localStorage','$timeout',
    function($scope,$location,$stateParams,UserService,ZhiziService,$compile,$window,$localStorage,$timeout){
        $scope.stage = $stateParams.stage;
        if ($scope.stage == undefined || $scope.stage == ''){
            $scope.stage = 'pipeline';
        }
        $scope.selTab = {"tab":$scope.stage};


        $scope.appId = parseInt($stateParams.appid);
        $scope.scenarioId = parseInt($stateParams.sceneid);
        $scope.pipeObj = {pipeline:"",dataSource:""};

        $scope.plugin = {
            "submitList" : []
        };
        $scope.pluginByType = {
            "submitListCustom": []
        };
        $scope.pluginListSelected = [];
        $scope.dockerDefault = {"ip":"","key":"","code":""};
        $scope.containerDefault = {
            "appName":"",
            "image":"",
            "command":""
        }
        $scope.pipelineCodeConfig = {
            lineNumbers: true,
            lineWrapping: false,
            foldGutter: true,
            docEnd:false,
            lint: true,
            width:'100%',
            //theme :'erlang-dark',
            height:'600px',
            gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter","CodeMirror-lint-markers"],
            mode: {name: "application/json", globalVars: true},
        };
        $scope.fixedEnvDefault = {
            "ip":"",
            "key":"",
            "code":"",
            "appName":"",
            "command":"",
            "installway":"pinstall"
        }
        $scope.dsDefault = {
            "type":"Tdbm",
            "group":"",
            "modules":"",
            "dataName":""
        }
        $scope.pluginInfoDefault = {
            "groupName":"",
            "availableCustom":"",
            "selectedCustom":"",
            "pluginSet":[]
        }

        $scope.stageList = ["docker", "fixedenv", "datasource", "pluginForDs", "pluginForUc", "save"]

        $scope.loadPipeline = function(){
            //if($scope.recentTimer){
            //    $interval.cancel($scope.recentTimer);
            //}

            $scope.pluginByType = {
                "submitListSystem":[],
                "submitListCustom":[
                    {
                        "groupName":"",
                        "pluginSet":[]
                    }
                ]
            }
            $scope.dockerList = [
                angular.copy($scope.dockerDefault)
            ]
            $scope.fixedEnvList = [
                angular.copy($scope.fixedEnvDefault)
            ]
            $scope.datasourceList = [
                angular.copy($scope.dsDefault)
            ]
            $scope.containerList = [
                angular.copy($scope.containerDefault)
            ]
            //$scope.getAllPluginList();
            $scope.getPipelineFlow();

            $scope.selTab.tab='pipeline';
        }
        $scope.loadDiamond = function(){

            $scope.selTab.tab='diamond';

            ZhiziService.getAllDiamondConfig($scope.scenarioId).then(function(res){
                if (res.data.success){
                    $scope.diamondInfo = res.data.data;
                }else{
                    $scope.saveInfo = '获取配置失败,请联系暔风排查,错误提示:' + res.data.message;
                    alert($scope.saveInfo);
                }
            })
        }

        $scope.openDiamondBox = function(type, desc, dataId, groupId,id){

            $scope.diamondUrl = "http://diamond.alibaba.net/diamond-ops/static/pages/config-edit/index.html?spm=0.0.0.0&dataId=" +dataId + "&group=" + groupId +"&serverId=daily&version=";
            ZhiziService.getDiamondConfig(dataId,groupId).then(function(res){
                if (res.data.success){
                    $scope.diamondContent = {
                        "id":id,
                        "content":res.data.data,
                        "dataId":dataId,
                        "groupId":groupId,
                        "desc":desc
                    };
                }else{
                    $scope.saveInfo = '获取配置失败,请去diamond查看是否异常,错误提示:' + res.data.message;
                    alert($scope.saveInfo);
                }
            })
        }

        $scope.saveDiamond = function(diamondUnit){

            diamondUnit.appId = $scope.appId;
            diamondUnit.scenarioId = $scope.scenarioId;

            ZhiziService.saveDiamondConfig(diamondUnit).then(function(res){


                if (res.data.success){
                    $scope.saveInfo = '保存成功';
                    ZhiziService.getAllDiamondConfig($scope.scenarioId).then(function(res){
                        if (res.data.success){
                            $scope.diamondInfo = res.data.data;
                        }else{
                            $scope.saveInfo = '获取配置失败,请联系暔风排查,错误提示:' + res.data.message;
                            alert($scope.saveInfo);
                        }
                    })
                    alert($scope.saveInfo);
                }else{
                    $scope.saveInfo = '获取配置失败,请去diamond查看是否异常,错误提示:' + res.data.message;
                    alert($scope.saveInfo);
                }
            })
        }


        $scope.transferDataSourceForCase = function(){

            ZhiziService.transferDataSourceForCase($scope.scenarioId).then(function(res){
                alert(res.data.message);
            })
        }



        $scope.addOne = function(type){
            switch (type){
                case 'docker':$scope.dockerList.push(angular.copy($scope.dockerDefault));break;
                case 'fixedenv':$scope.fixedEnvList.push(angular.copy($scope.fixedEnvDefault));break;
                case 'datasource':$scope.datasourceList.push(angular.copy($scope.dsDefault));break;
                case 'container':$scope.containerList.push(angular.copy($scope.containerDefault));break;
                case 'pluginForUc':$scope.pluginByType.submitListCustom.push(angular.copy($scope.pluginInfoDefault));break;
                default:break;
            }
        }

        $scope.rmOne = function(type, index, subtype = ""){
            switch (type + subtype){
                case 'dockermachine':$scope.dockerList.splice(index,1);break;
                case 'dockercontainer':$scope.containerList.splice(index,1);break;
                case 'datasource':$scope.datasourceList.splice(index,1);break;
                case 'fixedenv':$scope.fixedEnvList.splice(index,1);break;
                case 'pluginForUc':$scope.pluginByType.submitListCustom.splice(index,1);break;
                default:break;
            }
        }

        $scope.setTag = function(tag){
            $scope.tag = tag + "_defined";
        }
        $scope.selectPlugin = function(){
            var current = $scope.plugin.available[0];
            var tmp = {
                "pluginName": current.name,
                "params": current.pluginParams,
                "desc": current.pluginDesc,
                "id": current.id
            };
            if ($scope.indexOfAPlugin(tmp) == -1){
                $scope.plugin.submitList.push(tmp);
            }
        }

        $scope.unselectPlugin = function(p = null){
            var current;
            if (p != null) {
                current = p;
            }else{
                current = $scope.plugin.selected[0];
            }
            var pos = $scope.indexOfAPlugin(current);
            if (pos >= 0){
                $scope.plugin.submitList.splice(pos, 1);
            }
        }
        $scope.selectPluginByType = function(type = 'System', index = null){
            var current;
            if (type == 'System'){
                current = $scope.pluginByType['available' + type ][0];
            }else{
                current = $scope.pluginByType['submitListCustom'][index]['available' + type ][0];
            }
            var tmp = {
                "pluginName": current.name,
                "params": current.pluginParams,
                "desc": current.pluginDesc,
                "id": current.id
            };
            if ($scope.indexOfAPluginByType(tmp, type, index) == -1){
                if (type == 'System'){
                    $scope.pluginByType['submitList' + type].push(tmp);
                }else{
                    $scope.pluginByType['submitList' + type][index]['pluginSet'].push(tmp);
                }

            }
        }

        $scope.unselectPluginByType = function(type = 'System', index = null, p = null, subindex = null){
            var current;
            if (type == 'System'){
                current = p;
            } else{
                current = p;
            }
            var pos = $scope.indexOfAPluginByType(current, type, index, subindex);
            if (pos >= 0){
                if (type == 'System'){
                    $scope.pluginByType['submitList' + type].splice(pos, 1);
                }else{
                    $scope.pluginByType['submitList' + type][index]['pluginSet'].splice(pos, 1);
                }

            }
        }

        $scope.indexOfAPlugin = function(onePlugin){
            for (var x in $scope.plugin.submitList){
                if ($scope.plugin.submitList[x].id == onePlugin.id){
                    return x;
                }
            }
            return -1;
        }
        $scope.indexOfAPluginByType = function(onePlugin, type, index = null, subindex = null){
            if (type == 'System'){
                for (var x in $scope.pluginByType['submitList' + type]){
                    if ($scope.pluginByType['submitList' + type][x].id == onePlugin.id){
                        return x;
                    }
                }
            }else{
                for (var x in $scope.pluginByType['submitListCustom'][index]['pluginSet']){
                    if ($scope.pluginByType['submitListCustom'][index]['pluginSet'][x].id == onePlugin.id){
                        return x;
                    }
                }
            }

            return -1;
        }
        $scope.cursorTo = function(stage){
            $scope.stage = stage;
        }
        $scope.transferDockerInfo = function(){
            $scope.pipelineUI = {
                "savePath": "deploy.docker",
                "deploy":{
                    "docker":{
                        "hosts":$scope.dockerList,
                        "dockerContainers":$scope.containerList
                    }
                }
            }
        }
        $scope.transferFixedenvInfo = function(){
            $scope.pipelineUI = {
                "savePath": "deploy.fix",
                "deploy":{
                    "fix":{
                        "hosts":$scope.fixedEnvList
                    }
                }
            }
        }
        $scope.transferDatasource = function(){
            $scope.pipelineUI = {
                "savePath": "dataPrepare.datasource",
                "dataPrepare":{
                    "datasource":$scope.datasourceList
                }
            }
        }
        $scope.transferPluginForDs = function(){
            $scope.pipelineUI = {
                "savePath": "dataPrepare.pluginSet",
                "dataPrepare":{
                    "pluginSet":$scope.plugin.submitList
                }
            }
        }
        $scope.transferPluginForUc = function(){
            $scope.pipelineUI = {
                "savePath": "caseRun",
                "caseRun":{
                    "pluginSet":$scope.pluginByType.submitListCustom,
                    "systemPluginSet":$scope.pluginByType.submitListSystem
                }
            }
        }
        $scope.savePipelineFlow = function(){
            var data = {
                appId: $scope.appId,
                pipelineUIObj:$scope.pipelineUI,
                scenarioId:$scope.scenarioId,
                tag:"flow_defined",
                version:"default"
            }
            ZhiziService.savePipelineFlow(data).then(function(res){
                if (res.data.success){
                    var current = $scope.stageList.indexOf($scope.stage);
                    $scope.stage = $scope.stageList[++current];
                    if ($scope.pipelineUI.savePath == 'caseRun'){
                        $scope.pipeLineJson = JSON.stringify(res.data.data, null, "\t");
                    }
                    $scope.saveInfo = '';
                }else{
                    $scope.saveInfo = '保存失败,错误提示:' + res.data.message;
                }

            })
        }

        $scope.savePipelineJson = function(){
            var data = {
                appId: $scope.appId,
                pipeline:$scope.pipeObj.pipeline,
                dataSource:$scope.pipeObj.dataSource,
                scenarioId:$scope.scenarioId,
                tag:"user_defined",
                version:"default"
            }
            ZhiziService.savePipelineFlow(data).then(function(res){
                $scope.saveInfo = res.data.data;
                alert($scope.saveInfo);
                //$scope.pipeLine = JSON.stringify(res.data.data, null, "\t");
            })
        }
        $scope.dict = {
            "deploy.docker":'docker',
            "deploy.fix":'fixedenv',
            "dataPrepare.datasource":'datasource',
            "dataPrepare.pluginSet":'pluginForDs',
            "caseRun":'pluginForUc'
        }
        $scope.goBack = function(){
            history.back();
        }
        $scope.nextAndSave = function(){
            switch ($scope.stage){
                case 'docker':$scope.transferDockerInfo();break;
                case 'fixedenv':$scope.transferFixedenvInfo();break;
                case 'datasource':$scope.transferDatasource();break;
                case 'pluginForDs':$scope.transferPluginForDs();break;
                case 'pluginForUc':$scope.transferPluginForUc();break;
                default:break;
            }
            $scope.savePipelineFlow();


        }
        $scope.previous = function(){
            var current = $scope.stageList.indexOf($scope.stage);
            $scope.stage = $scope.stageList[--current];
        }
        $scope.getAllPluginList = function(){
            ZhiziService.getAllPluginList().then(function(res){
                $scope.pluginList = res.data.data;
                $scope.pluginListOfCaseRun =  $scope.pluginListOfDP = [];
                for (var x in $scope.pluginList){
                    var pluginStage = $scope.pluginList[x].pluginStage;
                    var tmpKey = "pluginListOf" + pluginStage;
                    if (tmpKey in $scope){
                        $scope[tmpKey].push($scope.pluginList[x]);
                    }else{
                        $scope[tmpKey] = [$scope.pluginList[x]];
                    }
                }
                $scope.pluginListByType = {
                    "system":[],
                    "custom":[]
                }
                for (var x in $scope.pluginListOfcaseRun){
                    var tmpType;
                    if ($scope.pluginListOfcaseRun[x].pluginType == "system"){
                        tmpType = "system";
                    }else{
                        tmpType = "custom";
                    }
                    $scope.pluginListByType[tmpType].push($scope.pluginListOfcaseRun[x]);
                }
            })
        }
        $scope.getPipelineFlow = function(){
            ZhiziService.getPipelineFlow($scope.scenarioId).then(function(res){
                if (res.data.success){
                    $scope.tag = res.data.data.tag;
                    if ($scope.tag == "flow_defined"){
                        var tmp = res.data.data.pipelineUIObj;
                        var step = tmp.savePath;
                        $scope.stage = $scope.dict[step];
                        $scope.dockerList = tmp["deploy"]["docker"]["hosts"];
                        $scope.containerList = tmp["deploy"]["docker"]["dockerContainers"];
                        $scope.fixedEnvList = tmp["deploy"]["fix"]["hosts"];
                        $scope.datasourceList = tmp["dataPrepare"]["datasource"];
                        $scope.plugin.submitList = tmp["dataPrepare"]["pluginSet"];
                        $scope.pluginByType.submitListSystem = tmp["caseRun"]["systemPluginSet"];
                        $scope.pluginByType.submitListCustom = tmp["caseRun"]["pluginSet"];
                        $scope.pipeLineJson = JSON.stringify(tmp, null, "\t");
                        // 后端没有传规范json 手动转一次
                        $scope.pipeObj.pipeline = JSON.stringify(angular.fromJson(res.data.data.pipeline), null, "\t");
                        $scope.pipeObj.dataSource = res.data.data.dataSource;
                    }else{
                        $scope.pipeObj.pipeline = JSON.stringify(angular.fromJson(res.data.data.pipeline), null, "\t");
                        $scope.pipeObj.dataSource = res.data.data.dataSource;


                        try {
                            var pipeline = angular.fromJson(res.data.data.pipeline);
                            if (pipeline["run-stage"][0]["params"].hasOwnProperty("datasourceNew")) {
                                $scope.isNewDataSource = true;
                            } else {
                                $scope.isNewDataSource = false;
                            }
                        }catch (e){
                            $scope.isNewDataSource = false;
                        }

                    }
                }else{
                    $scope.tag = "initial";
                }


            })
        }

        $scope.getDataTypeList = function(){
            ZhiziService.getDataTypeList().then(function(res){
                $scope.dataType = res.data.data;
            })
        }
        $scope.init = function(){


            if( $scope.stage == 'diamond'){
                $scope.loadDiamond();
            }
            else{

                $scope.loadPipeline();
                    //$scope.pluginByType = {
                    //    "submitListSystem":[],
                    //    "submitListCustom":[
                    //        {
                    //            "groupName":"",
                    //            "pluginSet":[]
                    //        }
                    //    ]
                    //}
                    //$scope.dockerList = [
                    //    angular.copy($scope.dockerDefault)
                    //]
                    //$scope.fixedEnvList = [
                    //    angular.copy($scope.fixedEnvDefault)
                    //]
                    //$scope.datasourceList = [
                    //    angular.copy($scope.dsDefault)
                    //]
                    //$scope.containerList = [
                    //    angular.copy($scope.containerDefault)
                    //]
                    ////$scope.getAllPluginList();
                    //$scope.getPipelineFlow();
                    ////$scope.getDataTypeList();
            }

        }

        $scope.init();
    }
]);