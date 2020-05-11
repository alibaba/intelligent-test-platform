angular.module('app.services.ZhiziService', []).factory('ZhiziService',['$http', '$timeout',function($http, $timeout){
    var api = {};


    api.getResultList = function(appid, scenarioid, pageId = 1, pageSize = 15,status){
        if (scenarioid == undefined)
            scenarioid = "";
        var url = 'api/getReportList?appId=' + appid + '&scenarioId=' + scenarioid + '&pageId=' + pageId + '&pageSize=' + pageSize+"&status="+status;
        return $http({
            method:'get',
            url:url,
            cache:false
        });
    }


//markov-demo
    api.getResultDetailList = function(appid,reportid, pageId = 1, pageSize = 15,status){
        var url = 'api/getReportTestCase?appId=' + appid + '&testReportId=' + reportid + '&pageId=' + pageId + '&pageSize=' + pageSize+(status?("&status="+status):"");
        return $http({
            method:'get',
            url:url,
            cache:false
        })
    }



    //markov-demo
    api.getCaseSnapsDetailList = function(caseid, pageId = 1, pageSize = 15,status){
        var url = 'api/getCaseSnapsDetailList?testcaseId=' + caseid  + '&pageId=' + pageId + '&pageSize=' + pageSize +(status?("&status="+status):"");
        return $http({
            method:'get',
            url:url,
            cache:false
        })
    }

    api.runCaseNew = function(appId,scenarioId,name,description,longDescription,prepareData,caseRunStage,envInfo,envName,prepare,caseId,caseTemplate){
        var url = 'api/runSingleTestCase';
        var data = {
            id:caseId,
            appId: appId,
            envName: envName,
            scenarioId: scenarioId,
            description: description,
            name: name,
            longDescription: longDescription,
            caseRunStage: caseRunStage,
            caseTemplate: caseTemplate,
            envInfo: envInfo
        }
        if (prepare){
            data.prepareData = prepareData;
        }
        return $http({
            method:'post',
            url:url,
            data:data,
            cache:true
        })
    };
        api.getBranchCaseGroups = function(scenarioId){
            var url = 'api/getCaseGroupListByBranch?scenarioId='+scenarioId;
            return $http({
                method:'get',
                url:url,
                cache:false
            })
        };




    //markov-demo
    api.getPipelineDataDemo = function(id){
        var url = 'api/getPipeline?scenarioId=' + id;
        return $http({
            method:'get',
            url:url,
            cache:false
        })
    }

    //markov-demo
    api.getCaseListDemo = function(scenarioId, pageId){
        var url = 'api/getTestcaseByScenario?scenarioId=' + scenarioId + "&pageNo=" + pageId ;
        return $http({
            method: 'get',
            url:url,
            cache:false
        });
    };

    //markov-demo
    api.savePipelineFlow = function(data){
        var url = 'api/savePipelineFlow';
        return $http({
            method:'post',
            url:url,
            data:data,
            cache:false
        })
    }

    //markov-demo
    api.getPipelineFlow = function(scenarioId){
        var url = 'api/getPipelineFlow?scenarioId=' + scenarioId ;
        return $http({
            method:'get',
            url:url,
            cache:false
        })
    }

    //markov-demo
    api.getPipelineData = function(id){
        var url = 'api/getPipeline?scenarioId=' + id;
        return $http({
            method:'get',
            url:url,
            cache:false
        })
    }

    //markov-demo
    api.getLayout = function(id){
        var url = 'api/getLayoutJson?scenarioId=' + id;
        return $http({
            method:'get',
            url:url,
            cache:false
        })
    }

    //markov-demo
    api.addCase = function(appId,scenarioId,name,description,longDescription,prepareData,caseRunStage,caseGroup,tag, caseTemplate,content, branch='master'){
        var url = 'api/saveTestCase';
        var data = {
            appId: appId,
            scenarioId: scenarioId,
            description: description,
            name: name,
            longDescription: longDescription,
            prepareData: prepareData,
            caseRunStage: caseRunStage,
            caseGroup: caseGroup,
            caseTemplate: caseTemplate,
            content: content,
            tag: tag,
            branchName: branch
        }
        return $http({
            method:'post',
            url:url,
            data:data,
            cache:true
        })
    }

    //markov-demo
    api.editCase = function(appId,scenarioId,name,description,longDescription,prepareData,caseRunStage,id,caseGroup,tag, caseTemplate,content, branch){
        var url = 'api/saveTestCase';
        var data = {
            id:id,
            appId: appId,
            scenarioId: scenarioId,
            description: description,
            name: name,
            longDescription: longDescription,
            prepareData: prepareData,
            caseRunStage: caseRunStage,
            caseGroup: caseGroup,
            caseTemplate: caseTemplate,
            content: content,
            tag: tag,
            branchName: branch
        }

        return $http({
            method:'post',
            url:url,
            data:data,
            cache:true
        })
    }


    //markov-demo
    api.getCase = function(appid, caseid,testReportId,snapId){
        var url = 'api/getTestCase?appId=' + appid + '&testCaseId=' + caseid+(testReportId?("&testReportId="+testReportId):"")+(snapId?("&snapId="+snapId):"");
        return $http({
            method:'get',
            url:url,
            cache:false
        })
    }

    //markov-demo
    api.listGroupByHost = function(scenarioId){
        var url = 'api/envs/findByScenarioId?scenarioId=' + scenarioId;
        return $http({
            method: 'get',
            url:url,
            cache:false
        });
    };
    //markov-demo
    api.addEnv = function(data){
        var url = 'api/envs/addEnv';
        return $http({
            method: 'post',
            data:data,
            url:url,
            cache:false
        });
    };

    //markov-demo
    api.deleteDeployEnv = function(id){
        var url = 'api/envs/deleteById?id=' + id;
        return $http({
            method: 'delete',
            url:url,
            cache:false
        });
    };
    //markov-demo
    api.deleteCase = function(caseid){
        var url = 'api/deleteTestCase?testCaseId=' + caseid;
        var data = {
            testCaseId: caseid
        };
        return $http({
            method:'delete',
            url:url,
            data:data,
            cache:true
        })
    }

    //markov-demo
    api.runMultiCase = function(data){
        var url = 'api/runMultiAITestCase';
        return $http({
            method: 'post',
            data:data,
            url:url,
            cache:false
        });
    };

//markov-demo
    api.getTestReportData = function(testReportId){
        var url = 'api/getReport?testReportId='+testReportId;
        return $http({
            method: 'GET',
            url: url
        });
    }

    //markov-demo
    api.getDefaultGeneConf = function(scenarioId) {
        var url = 'api/getDefaultGeneConf?scenarioId=' + scenarioId;
        return $http({
            method: 'GET',
            cache: false,
            url: url
        });
    }

//markov-demo
    api.getFeaturesList = function(scenarioId){
        var url = 'api/caseFactory/getFeaturesList?scenarioId='+scenarioId;
        return $http({
            method: 'GET',
            cache:false,
            url: url
        });
    }


    //markov-demo
    api.startAICaseGenerator = function(params){
        var url = 'api/startAICaseGenerator';
        return $http({
            method: 'post',
            data:params,
            url:url,
            cache:false
        });
    };

    //markov-demo
    api.getGeneratorTaskDetail = function(taskId){
        var url = 'api/getGeneratorTaskDetail?taskId='+taskId;
        return $http({
            method: 'GET',
            url: url
        });
    };

    //markov-demo
    api.changeVisible = function(caseId){
        var url = 'api/changeVisible?caseId='+caseId;
        return $http({
            method: 'GET',
            url: url
        });
    };

    //markov-demo
    api.getLastGenerateTask = function(scenarioId){
        var url = 'api/getLastGenerateTask?scenarioId='+scenarioId;
        return $http({
            method: 'GET',
            url: url
        });
    };

//markov-demo
    api.featuresMatch = function(data){
        var url = 'api/caseFactory/featuresMatch';
        return $http({
            method: 'post',
            cache:false,
            data:data,
            url: url
        });
    }


    api.saveMultiCase = function(appId,scenarioId,caseList,multiInfo){
        var url = 'api/caseFactory/saveCaseList';
        var data = {
            appId: appId,
            scenarioId: scenarioId,
            caseList: caseList,
            multiInfo: multiInfo
        }
        return $http({
            method:'post',
            url:url,
            data:data,
            cache:true
        })
    }

    api.getExpandFeatures = function(testCase){
        var url = 'api/caseFactory/getExpandFeatures';
        return $http({
            method:'post',
            url:url,
            data:testCase,
            cache:true
        })
    }

    api.getFeatureCombination = function(){
        var url = 'api/caseFactory/getFeatureCombination';
        return $http({
            method:'get',
            url:url,
            cache:true
        })
    }


    api.getExpandCaseList = function(appid, sceneid, testcase, expandKvList){
        var url = 'api/caseFactory/getExpandCases';

        var data ={
            appId: appid,
            scenarioId: sceneid,
            testCase: testcase,
            expandKvList: expandKvList
        }

        return $http({
            method:'post',
            url:url,
            data:data,
            cache:true
        })
    }

    return api;
}]);