angular.module('app.controllers.expandController', ['pascalprecht.translate', 'ngCookies','ngSanitize', 'ui.select'])
    .controller('expandController', ['$scope','$location','$stateParams','UserService','ZhiziService','$compile','$window','$localStorage','$timeout',
    function($scope,$location,$stateParams,UserService,ZhiziService,$compile,$window,$localStorage,$timeout){

        $scope.appid = parseInt($stateParams.appid);
        $scope.sceneid = parseInt($stateParams.sceneid);
        $scope.caseid = $stateParams.caseid;
        $scope.type = $stateParams.type;
        $scope.case = {}
        $scope.featureInitMap = {};
        $scope.featureInitList =[];
        $scope.featureSelect = {} ;


        $scope.init = function(){

            ZhiziService.getCase($scope.appid, $scope.caseid).then(function(res){
                $scope.case = res.data.data;
                ZhiziService.getExpandFeatures($scope.case).then(function(res){
                    $scope.expandKvList = res.data.data;
                })

                ZhiziService.getFeatureCombination().then(function(res){
                    $scope.featureInitMap = res.data.data.featureInitMap;
                    $scope.featureInitList = res.data.data.featureInitList;
                })
            });

        }


        $scope.selectFeatureMap = function(index){

            $scope.expandKvList[index].expandValue = $scope.featureSelect[index].value;
        }


        $scope.init();


        $scope.expandCase = function(){

            $scope.expandcase = []

            var dateBegin = new Date();
            ZhiziService.getExpandCaseList($scope.appid, $scope.sceneid, $scope.case, $scope.expandKvList).then(function(res){
                $scope.expandcase = res.data.data;
                $scope.expandcaseCnt = $scope.expandcase.length;
                $localStorage["expandCaseList"] = $scope.expandcase;
                var dateEnd = new Date();
                $scope.dateDiff = dateEnd.getTime() - dateBegin.getTime()
            })

        }

        $scope.toEdit = function(caseid, actiontype){
            var tmp = "#!/app/case?appid=" + $scope.appid +"&sceneid=" +$scope.sceneid + "&type=" + actiontype + "&caseid=" + caseid;
            //$window.location.href = tmp;
            $window.open(tmp);
        }



        $scope.pickList = [];
        //获取case下标
        $scope.pickCase = function(indexId){
            var pos = $scope.pickList.indexOf(indexId);
            if (pos != -1){
                $scope.pickList.splice(pos, 1);
            }else{
                $scope.pickList.push(indexId);

            }
        }

        $scope.saveExpandCase = function(){

            if($scope.pickList.length > 0){
                $scope.pickCaseList = [];
                //获取所有要保存的case信息
                for (var index in $scope.expandcase){
                    if ($scope.pickList.includes(parseInt(index))){
                        $scope.pickCaseList.push($scope.expandcase[index]);
                    }
                }
                var mutilInfo = {
                    "type": "expand"
                }
                //保存推荐用例,此时的用例描述和tesla都要进行修改.$scope.caseList,
                ZhiziService.saveMultiCase($scope.appid, $scope.sceneid, $scope.pickCaseList, mutilInfo);
            }
            //返回首页
            $window.location.href = "#!/app/index?" + "appid=" + $scope.appid + "&sceneid=" + $scope.sceneid;

        }

    }
]);