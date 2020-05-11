package com.alibaba.markovdemo.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class EnvsJsonBO extends BaseEntity {

    private Map hostInfo;

    private Map userInfo;

    private String envName;

    private String callbackHost;

    private Map selectModels;

    private Map params;

    private List deploy;

    private String mode;

    private List<Map> hosts;

    private Integer caseType;

    private Integer taskType;

    private List cases;

    private Integer scenarioId;
    private Integer appId;

    private boolean openAccuracy;
    private boolean openSmartRegress;
    private Map<String, String> accuracyInfo;
    private String branchName;
    private String deployGiven;

    public String getDeployGiven() {
        return deployGiven;
    }

    public void setDeployGiven(String deployGiven) {
        this.deployGiven = deployGiven;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public boolean isOpenSmartRegress() {
        return openSmartRegress;
    }

    public void setOpenSmartRegress(boolean openSmartRegress) {
        this.openSmartRegress = openSmartRegress;
    }



    public boolean isOpenAccuracy() {
        return openAccuracy;
    }

    public void setOpenAccuracy(boolean openAccuracy) {
        this.openAccuracy = openAccuracy;
    }

    public Map<String, String> getAccuracyInfo() {
        return accuracyInfo;
    }

    public void setAccuracyInfo(Map<String, String> accuracyInfo) {
        this.accuracyInfo = accuracyInfo;
    }



    public Integer getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Integer scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Map getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(Map hostInfo) {
        this.hostInfo = hostInfo;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getCallbackHost() {
        return callbackHost;
    }

    public void setCallbackHost(String callbackHost) {
        this.callbackHost = callbackHost;
    }

    public Map getSelectModels() {
        return selectModels;
    }

    public void setSelectModels(Map selectModels) {
        this.selectModels = selectModels;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public List getDeploy() {
        return deploy;
    }

    public void setDeploy(List deploy) {
        this.deploy = deploy;
    }

    public Map getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Map userInfo) {
        this.userInfo = userInfo;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<Map> getHosts() {
        return hosts;
    }

    public void setHosts(List<Map> hosts) {
        this.hosts = hosts;
    }

    public Integer getCaseType() {
        return caseType;
    }

    public void setCaseType(Integer caseType) {
        this.caseType = caseType;
    }

    public List getCases() {
        return cases;
    }

    public void setCases(List cases) {
        this.cases = cases;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}
