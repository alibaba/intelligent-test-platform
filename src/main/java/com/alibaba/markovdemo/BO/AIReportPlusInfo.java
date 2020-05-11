package com.alibaba.markovdemo.BO;

import java.util.List;
import java.util.Map;

public class AIReportPlusInfo {

    //基本信息
    BasicReport basicReport;
    //分环境基本信息
    List<EnvBasicReport> envReportList;
    //树分层数据信息
    Map<String,ChartBasicInfo> treeDsChart;
    //全量数据信息
    Map<String,ChartBasicInfo> dumpDsChart;

    public BasicReport getBasicReport() {
        return basicReport;
    }

    public void setBasicReport(BasicReport basicReport) {
        this.basicReport = basicReport;
    }

    public List<EnvBasicReport> getEnvReportList() {
        return envReportList;
    }

    public void setEnvReportList(List<EnvBasicReport> envReportList) {
        this.envReportList = envReportList;
    }

    public Map<String, ChartBasicInfo> getTreeDsChart() {
        return treeDsChart;
    }

    public void setTreeDsChart(Map<String, ChartBasicInfo> treeDsChart) {
        this.treeDsChart = treeDsChart;
    }

    public Map<String, ChartBasicInfo> getDumpDsChart() {
        return dumpDsChart;
    }

    public void setDumpDsChart(Map<String, ChartBasicInfo> dumpDsChart) {
        this.dumpDsChart = dumpDsChart;
    }
}


