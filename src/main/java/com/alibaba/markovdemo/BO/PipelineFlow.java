package com.alibaba.markovdemo.BO;

import com.alibaba.fastjson.JSONObject;


public class PipelineFlow {

    Long scenarioId;

    Long appId;

    String tag;

    String version;

    String pipeline;

    String dataSource;

    String pipelineUI;

    JSONObject pipelineUIObj;

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
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public String getPipelineUI() {
        return pipelineUI;
    }

    public void setPipelineUI(String pipelineUI) {
        this.pipelineUI = pipelineUI;
    }

    public JSONObject getPipelineUIObj() {
        return pipelineUIObj;
    }

    public void setPipelineUIObj(JSONObject pipelineUIObj) {
        this.pipelineUIObj = pipelineUIObj;
    }

}
