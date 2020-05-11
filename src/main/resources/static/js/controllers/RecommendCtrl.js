angular.module('app.controllers.recommendController', ['pascalprecht.translate', 'ngCookies','ngSanitize', 'ui.select'])
    .controller('recommendController', ['$scope','$location','$stateParams','UserService','ZhiziService','$compile','$window','$localStorage','$timeout',
    function($scope,$location,$stateParams,UserService,ZhiziService,$compile,$window,$localStorage,$timeout){

        $scope.type = $stateParams.type;
        $scope.appid = parseInt($stateParams.appid);
        $scope.sceneid = parseInt($stateParams.sceneid);
        $scope.features_src = [];

        $scope.featureList = {};

        $scope.formData = {
            scenarioId:$scope.sceneid,
            topN:3,
            caseDesc:"",
            featureList:[]
        };


        $scope.addCol = function(index){
            $scope.teslakv.push({"key":"","value":""});
            console($scope.teslakv)
        }
        $scope.rmCol = function(index){
            $scope.teslakv.splice(index,1);
        }




        $scope.initRecommend = function(){

            ZhiziService.getFeaturesList($scope.sceneid).then(function(res){

                $scope.features_src = res.data.data;

            })

        }

        $scope.addSelectFeatures = function(){
            angular.forEach($scope.featureList.src,function(a){
                $scope.features_src.splice($scope.features_src.indexOf(a),1);
                $scope.formData.featureList.push(a);
            })
        }

        $scope.removeSelectFeatures = function(){
            angular.forEach($scope.featureList.select,function(a){
                $scope.formData.featureList.splice($scope.formData.featureList.indexOf(a),1);
                $scope.features_src.push(a);
            })
        }

        $scope.recommend = function(){
            $scope.formData.caseDesc = $scope.caseDesc;
            ZhiziService.featuresMatch($scope.formData).then(function(res){
                $scope.renderCaseInfo(res);
            })

        }

        $scope.renderCaseInfo = function(res){
            $scope.caseList = res.data.data.caseList;
            $scope.caseCount = res.data.data.caseCount;
            $scope.interval = res.data.data.interval;
            $scope.recommenderName = res.data.data.recommenderName;

        }

        $scope.navigate = function(type = 'edit', params = null){
            var tmp = '';

            tmp += '&caseid=' + params + "&appid=" + $scope.appid + "&sceneid=" + $scope.sceneid;
            tmp = "#!/app/case?type=" + type + tmp;
            //$window.location.href = "#!/app/case?type=" + type + tmp;

            $window.open(tmp);

            //if(type == 'index'){
            //    $window.location.href = "#!/app/index?" + "appid=" + $scope.appid + "&sceneid=" + $scope.sceneid;
            //}
            //
            //else if (params){
            //    tmp += '&caseid=' + params + "&appid=" + $scope.appid + "&sceneid=" + $scope.sceneid;
            //    $window.location.href = "#!/app/case?type=" + type + tmp;
            //}
        }

        $scope.pickList = [];
        $scope.pickCase = function(caseId){
            var pos = $scope.pickList.indexOf(caseId);
            if (pos != -1){
                $scope.pickList.splice(pos, 1);
            }else{
                $scope.pickList.push(caseId);

            }
        }

        $scope.saveRecommendCase = function(){

            if($scope.pickList.length > 0){
                $scope.pickCaseList = [];
                //获取所有要保存的case信息
                for (var index in $scope.caseList){
                    if ($scope.pickList.includes($scope.caseList[index]["id"])){
                        $scope.pickCaseList.push($scope.caseList[index]);
                    }
                }
                var mutilInfo = {
                    "type": "recommend",
                    "caseTag": $scope.caseTag,
                    "caseDesc": $scope.caseDesc
                };
                //保存推荐用例,此时的用例描述和tesla都要进行修改.$scope.caseList,
                ZhiziService.saveMultiCase($scope.appid, $scope.sceneid, $scope.pickCaseList, mutilInfo);
            }
            //返回首页
            $window.location.href = "#!/app/index?" + "appid=" + $scope.appid + "&sceneid=" + $scope.sceneid;
        }
    }
]);