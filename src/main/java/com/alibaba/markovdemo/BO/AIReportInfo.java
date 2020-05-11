package com.alibaba.markovdemo.BO;

import java.util.List;
import java.util.Map;


public class AIReportInfo {
    AllReport allReport;
    List<EnvReport> envReportList;
    List<CaseAnalysis> caseAnalysisList;
    Map<String,List<ChartInfo>> allChart;

    public AllReport getAllReport() {
        return allReport;
    }

    public void setAllReport(AllReport allReport) {
        this.allReport = allReport;
    }

    public List<EnvReport> getEnvReportList() {
        return envReportList;
    }

    public void setEnvReportList(List<EnvReport> envReportList) {
        this.envReportList = envReportList;
    }

    public List<CaseAnalysis> getCaseAnalysisList() {
        return caseAnalysisList;
    }

    public void setCaseAnalysisList(List<CaseAnalysis> caseAnalysisList) {
        this.caseAnalysisList = caseAnalysisList;
    }

    public Map<String, List<ChartInfo>> getAllChart() {
        return allChart;
    }

    public void setAllChart(Map<String, List<ChartInfo>> allChart) {
        this.allChart = allChart;
    }
}
