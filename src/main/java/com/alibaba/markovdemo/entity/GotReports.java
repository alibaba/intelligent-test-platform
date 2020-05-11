/*******************************************************************************
 * Copyright (c) 2017 Alibaba Group limited Corp.
 * Building 5, 969 Wenyi West Road, Hangzhou, China.
 * All rights reserved.
 * <p>
 * "[Description of code or deliverable as appropriate] is the copyrighted,
 * proprietary property of Alibaba Group limited Corp
 * which retain all right, title and interest therein."
 * <p>
 * ******************************************************************************
 * <p>
 * Created on 2017-02-08
 * Coat refer to: http://sqi.alibaba-inc.com/alpha/detail.htm?id=278
 *
 * @author SQI Alpha (http://sqi.alibaba-inc.com/alpha/index.htm)
 * @version 1.0.0
 * @since JDK 1.7
 */

package com.alibaba.markovdemo.entity;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class GotReports extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long appId;
    private Long scenarioId;
    private Date gmtCreate;
    private Date gmtModified;
    private String user;
    private String reportName;
    private String status;
    private String message;
    private String caseGroup;
    private String tag;
    private String version;
    private int caseNum;
    //触发方式
    private String runType;
    //和前端执行id进行绑定
    private String execId;
    private String analysis;
    //zk信息,实时信息保存
    private String taskId;
    private String zkInfo;
    private Long accuracyReportId;
    private String imageName;
    private String branchName;
    private String gitBranch;
    private String gitCommit;
    private String ccCovRate;
    private Integer isVisible;

    public Integer getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Integer isVisible) {
        this.isVisible = isVisible;
    }

    public String getCcCovRate() {
        return ccCovRate;
    }

    public void setCcCovRate(String ccCovRate) {
        this.ccCovRate = ccCovRate;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getGitCommit() {
        return gitCommit;
    }

    public void setGitCommit(String gitCommit) {
        this.gitCommit = gitCommit;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getCaseNum() {
        return caseNum;
    }

    public void setCaseNum(int caseNum) {
        this.caseNum = caseNum;
    }

    public Long getAccuracyReportId() {
        return accuracyReportId;
    }

    public void setAccuracyReportId(Long accuracyReportId) {
        this.accuracyReportId = accuracyReportId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getZkInfo() {
        return zkInfo;
    }

    public void setZkInfo(String zkInfo) {
        this.zkInfo = zkInfo;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public String getRunType() {
        return runType;
    }

    public void setRunType(String runType) {
        this.runType = runType;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT-5")
    public Date getGmtCreate() {
        return gmtCreate;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT-5")
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT-5")
    public Date getGmtModified() {
        return gmtModified;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT-5")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getCaseGroup() {
        return caseGroup;
    }

    public void setCaseGroup(String caseGroup) {
        this.caseGroup = caseGroup;
    }

    public String getTag() {
        return tag;
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
}