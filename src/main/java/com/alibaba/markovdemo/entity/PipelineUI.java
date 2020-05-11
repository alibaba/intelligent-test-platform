package com.alibaba.markovdemo.entity;

public class PipelineUI extends BaseEntity {

    private Long scenarioId;

    private String content;

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
