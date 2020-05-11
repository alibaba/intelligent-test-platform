angular.module('app.controllers.ReportController', ['pascalprecht.translate', 'ngCookies','ngSanitize', 'ui.select'])
    .controller('ReportController', ['$scope','$location','$stateParams','$state','$interval','$modal','NgTableParams','toaster','UserService','ZhiziService',
    function($scope,$location,$stateParams,$state,$interval,$modal,NgTableParams,toaster,UserService,ZhiziService){
        $scope.testReportId = $stateParams.testReportId;
        $scope.reportData = {};
        $scope.caseHistory = {};
        $scope.caseHistoryList = [];
        $scope.caseHistoryChart = [];
        $scope.sceneid = $stateParams.sceneid;
        $scope.appid = $stateParams.appid;
        $scope.tableNameMap = {"caseStatusDis":"用例状态分布","runTimeDis":"运行时长分布","retryNumDis":"重试次数分布"}
        var self = this;

        $scope.initReportData = function(){
            ZhiziService.getTestReportData($scope.testReportId).then(function(resdata){
                if(resdata.data.success){
                    $scope.reportData = resdata.data.data;

                    var treeDsChart = $scope.reportData.treeDsChart;
                    var dumpDsChart = $scope.reportData.dumpDsChart;

                    var treeDsList = [];
                    var treeActualDataList = [];
                    var treeNoDataList = [];

                    angular.forEach(treeDsChart,function(info,ds){

                        treeDsList.push(ds);
                        var allNum = info["allNum"];
                        var actualNum = info["actualNum"];
                        var noNum = allNum-actualNum;
                        treeActualDataList.push(actualNum);
                        treeNoDataList.push(noNum);
                    })



                    Highcharts.chart('treeDsChart', {
                        chart: {
                            type: 'column'
                        },
                        title: {
                            text: '数据冗余度统计'
                        },
                        xAxis: {
                            categories: treeDsList
                        },
                        yAxis: {
                            min: 0,
                            title: {
                                text: '数据条数'
                            },
                            stackLabels: {  // 堆叠数据标签
                                enabled: true,
                                style: {
                                    fontWeight: 'bold',
                                    color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                                }
                            }
                        },
                        legend: {
                            align: 'right',
                            x: -30,
                            verticalAlign: 'top',
                            y: 25,
                            floating: true,
                            backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
                            borderColor: '#CCC',
                            borderWidth: 1,
                            shadow: false
                        },
                        tooltip: {
                            formatter: function () {
                                return '<b>' + this.x + '</b><br/>' +
                                    this.series.name + ': ' + this.y + '<br/>' +
                                    '总量: ' + this.point.stackTotal;
                            }
                        },
                        plotOptions: {
                            column: {
                                stacking: 'normal',
                                dataLabels: {
                                    enabled: true,
                                    color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
                                    style: {
                                        // 如果不需要数据标签阴影，可以将 textOutline 设置为 'none'
                                        textOutline: '1px 1px black'
                                    }
                                }
                            }
                        },
                        series: [{
                            name: '实际数据执行条数',
                            data: treeActualDataList
                        },  {
                            name: '冗余数据执行条数',
                            data: treeNoDataList
                        }]
                    });


                    angular.forEach(dumpDsChart,function(info,ds){

                        treeDsList.push(ds);
                        var allNum = info["allNum"];
                        var actualNum = info["actualNum"];
                        treeActualDataList.push(actualNum);
                    })

                   Highcharts.chart('dumpDsChart',{
                        chart: {
                            type: 'column'
                        },
                        title: {
                            text: '全量数据准备队列:数据统计'
                        },
                        subtitle: {
                            text: '数据来源: Markov'
                        },
                        xAxis: {
                            categories:treeDsList,
                            crosshair: true
                        },
                        yAxis: {
                            min: 0,
                            title: {
                                text: '数据条数'
                            }
                        },
                        tooltip: {
                            // head + 每个 point + footer 拼接成完整的 table
                            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                            //pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                            //'<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
                            footerFormat: '</table>',
                            shared: true,
                            useHTML: true
                        },
                        plotOptions: {
                            column: {
                                borderWidth: 0
                            }
                        },
                        series: [{
                            name: '数据量',
                            data: treeActualDataList
                        }]
                    });

                } else {
                    toaster.clear();
                    toaster.pop("error","信息",resdata.data.message);
                }
            },function(error){
                alert(JSON.stringify(error));
            })
        }
        $scope.loadCaseHistory = function(url){
            $scope.caseHistory = {};
            $scope.caseHistoryList = [];
            toaster.pop("wait","信息","正在加载数据...");
            ZhiziService.getTestCaseDetailData(url).then(function(resdata){
                toaster.clear();
                if(resdata.data.success){
                    $scope.caseHistoryList = resdata.data.data.caseList;
                    self.caseHistory = new NgTableParams({count:10,sorting: {id: "desc"}}, {counts: [10,30,50,100],paginationMaxBlocks: 10,paginationMinBlocks: 2,dataset: $scope.caseHistoryList});
                } else {
                    toaster.pop("error","信息",resdata.data.message);
                }
            },function(error){
                alert(JSON.stringify(error));
            })
        }
    }
]);