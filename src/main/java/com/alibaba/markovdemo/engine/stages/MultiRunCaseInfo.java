package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.TestCaseInput;

import java.util.List;

public class MultiRunCaseInfo {
    public Long appId;
    public Long scenarioId;
    public Long realAppId;
    public Long realScenarioId;
    public JSONObject pipelineObj;
    public List<TestCaseInput> testcaseList;

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

    public Long getRealAppId() {
        return realAppId;
    }

    public void setRealAppId(Long realAppId) {
        this.realAppId = realAppId;
    }

    public Long getRealScenarioId() {
        return realScenarioId;
    }

    public void setRealScenarioId(Long realScenarioId) {
        this.realScenarioId = realScenarioId;
    }

    public JSONObject getPipelineObj() {
        return pipelineObj;
    }

    public void setPipelineObj(JSONObject pipelineObj) {
        this.pipelineObj = pipelineObj;
    }

    public List<TestCaseInput> getTestcaseList() {
        return testcaseList;
    }

    public void setTestcaseList(List<TestCaseInput> testcaseList) {
        this.testcaseList = testcaseList;
    }
}
