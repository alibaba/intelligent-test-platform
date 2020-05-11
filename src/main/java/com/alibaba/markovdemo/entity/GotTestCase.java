package com.alibaba.markovdemo.entity;

import java.util.Date;

public class GotTestCase extends BaseEntity {
    private Long scenarioId;
    private String name;
    private String description;
    private String longDescription;
    private String content;
    private String caseGroup;
    private Integer isDeleted;
    private String caseTemplate;
    private String features;
    private Date gmtCreate;
    private Date gmtModified;
    private Integer isVisible;

    public Integer getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Integer isVisible) {
        this.isVisible = isVisible;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getCaseTemplate() {
        return caseTemplate;
    }

    public void setCaseTemplate(String caseTemplate) {
        this.caseTemplate = caseTemplate;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCaseGroup() {
        return caseGroup;
    }

    public void setCaseGroup(String caseGroup) {
        this.caseGroup = caseGroup;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
