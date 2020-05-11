package com.alibaba.markovdemo.BO;

import java.util.List;


public class MultiCase {

    Long appId;

    Long scenarioId;

    List<TestCaseInput> caseList;

    MultiInfo multiInfo;

    public List<TestCaseInput> getCaseList() {
        return caseList;
    }

    public void setCaseList(List<TestCaseInput> caseList) {
        this.caseList = caseList;
    }

    public MultiInfo getMultiInfo() {
        return multiInfo;
    }

    public void setMultiInfo(MultiInfo multiInfo) {
        this.multiInfo = multiInfo;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }
}
