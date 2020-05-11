/*******************************************************************************
 * Copyright (c) 2017 Alibaba Group limited Corp.
 * Building 5, 969 Wenyi West Road, Hangzhou, China.
 * All rights reserved.
 *
 * "[Description of code or deliverable as appropriate] is the copyrighted,
 * proprietary property of Alibaba Group limited Corp
 * which retain all right, title and interest therein."
 *
 *******************************************************************************
 *
 * Created on 2017-02-08
 * Coat refer to: http://sqi.alibaba-inc.com/alpha/detail.htm?id=278
 * @author SQI Alpha (http://sqi.alibaba-inc.com/alpha/index.htm)
 * @version 1.0.0
 * @since JDK 1.7
 *
 */

package com.alibaba.markovdemo.entity;

import java.util.Date;

/**
 * 保存执行配置快照
 */
public class GotCaseGenerateTask extends BaseEntity {
    private Date gmtCreate;
    private Date gmtModified;
    private String creator;
    private String seedCaseList;
    private Long scenarioId;
    private String envInfo;
    private String featureConf;
    private String taskName;
    private String taskSnap;
    private String taskResult;
    private String taskStatus; // created or executing or fail or success
    private String geneBankSnap;

    public String getGeneBankSnap() {
        return geneBankSnap;
    }

    public void setGeneBankSnap(String geneBankSnap) {
        this.geneBankSnap = geneBankSnap;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getSeedCaseList() {
        return seedCaseList;
    }

    public void setSeedCaseList(String seedCaseList) {
        this.seedCaseList = seedCaseList;
    }

    public String getEnvInfo() {
        return envInfo;
    }

    public void setEnvInfo(String envInfo) {
        this.envInfo = envInfo;
    }

    public String getFeatureConf() {
        return featureConf;
    }

    public void setFeatureConf(String featureConf) {
        this.featureConf = featureConf;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskSnap() {
        return taskSnap;
    }

    public void setTaskSnap(String taskSnap) {
        this.taskSnap = taskSnap;
    }

    public String getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(String taskResult) {
        this.taskResult = taskResult;
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

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }
}