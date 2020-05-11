package com.alibaba.markovdemo.BO;


import java.util.List;

public class ExpandInfo {

    Long appId;
    Long scenarioId;
    String tag;
    String description;
    TestCaseInput testCase;
    List<Expandkv> expandKvList;

    public List<Expandkv> getExpandKvList() {
        return expandKvList;
    }

    public void setExpandKvList(List<Expandkv> expandKvList) {
        this.expandKvList = expandKvList;
    }

    public TestCaseInput getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCaseInput testCase) {
        this.testCase = testCase;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
