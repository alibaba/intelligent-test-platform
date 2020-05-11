angular.module('app.controllers.AICaseGeneratorController', ['pascalprecht.translate', 'ngCookies','ngSanitize', 'ui.select'])
    .controller('AICaseGeneratorController', ['$scope','$location','$interval','$modal','toaster','$state','$sce','UserService','GlobalStorage','ZhiziService','$compile','$window','$localStorage','$timeout','$stateParams','$uibModal',
    function($scope,$location,$interval,$modal,toaster,$state,$sce,UserService,GlobalStorage,ZhiziService,$compile,$window,$localStorage,$timeout,$stateParams,$uibModal){

        $scope.taskId = parseInt($stateParams.AITaskId);


        if (isNaN($scope.appid)){
            if ($scope.currentLeftMenu[0] !=undefined){
                $scope.appid = $scope.currentLeftMenu[0].appid;
            }
        }

        $scope.initTask = function (){
            ZhiziService.getGeneratorTaskDetail($scope.taskId).then(function(res) {
                if(res.data.success){
                    var task = res.data.data.task;
                    $scope.geneMap = task.geneBankSnap;
                    $scope.seedCase = task.seedCaseList;
                    
                    try {
                        $scope.taskResult = angular.fromJson(task.taskResult);
                        $scope.validCases = $scope.taskResult.valid_ids;
                        $scope.allCaseNum = $scope.taskResult.all_num;
                        $scope.detail = $scope.taskResult.detail;
                        $scope.covRate = $scope.taskResult.cov_rate;
                    }catch (e) {
                        
                    }
                    
                    $scope.taskStatus = task.taskStatus;
                    
                    $scope.envInfo = task.envInfo;

                    $scope.caseInfo = res.data.data.validCaseInfo;

                }
            });
        }


        $scope.setShowCode = function(codes){
            $scope.currentCovCode = codes;
        }

        $scope.changeVisible = function (caseid) {
            ZhiziService.changeVisible(caseid).then(function(res) {
                if(res.data.success){
                    alert("已加入用例列表！");
                }else{
                    alert("加入用例列表失败！");
                }
            });
        }



    }
    ]
    );