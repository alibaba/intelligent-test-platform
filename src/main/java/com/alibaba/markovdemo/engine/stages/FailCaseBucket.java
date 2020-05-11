package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.TestCaseAI;
import com.alibaba.markovdemo.entity.GotReports;


import java.util.List;
import java.util.Map;

/**
 * 功能:失败桶
 */
public class FailCaseBucket {

    public List<TestCaseAI> testCaseAIList;
    public JSONObject pipelineObj;
    public GotReports gotReports;
    public Map<String, String> deployMap;

    public Map<String, String> getDeployMap() {
        return deployMap;
    }

    public void setDeployMap(Map<String, String> deployMap) {
        this.deployMap = deployMap;
    }

    public List<TestCaseAI> getTestCaseAIList() {
        return testCaseAIList;
    }

    public void setTestCaseAIList(List<TestCaseAI> testCaseAIList) {
        this.testCaseAIList = testCaseAIList;
    }

    public JSONObject getPipelineObj() {
        return pipelineObj;
    }

    public void setPipelineObj(JSONObject pipelineObj) {
        this.pipelineObj = pipelineObj;
    }

    public GotReports getGotReports() {
        return gotReports;
    }

    public void setGotReports(GotReports gotReports) {
        this.gotReports = gotReports;
    }

}
