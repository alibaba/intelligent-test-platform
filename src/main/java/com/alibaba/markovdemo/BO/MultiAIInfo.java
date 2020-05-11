package com.alibaba.markovdemo.BO;

import java.util.List;
import java.util.Map;

/**
 * 功能:智能回归类
 */
public class MultiAIInfo {

    Map<String,List<String>> envInfo;
    List<String> caseGroup;
    String branchName;
    //0表示用例分组,1表示场景id
    String caseType;
    String execId;
    Long scenarioId;
    Long appId;
    String runType;
    String taskName;
    //0表示智能回归,1表示托管模式回归
    int taskType;
    //执行器脚本
    String script;
    String caseTemplate;
    String creator;
    boolean openAccuracy;
    Map<String, String> accuracyInfo;
    String caseIds;
    boolean openSmartRegress;
    //重跑失败/全部 ,0:仅失败,1.全部用例
    Long testReportId;
    String reRunType;
    Boolean regAccuracy;

    public Boolean getRegAccuracy() {
        return regAccuracy;
    }

    public void setRegAccuracy(Boolean regAccuracy) {
        this.regAccuracy = regAccuracy;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Long getTestReportId() {
        return testReportId;
    }

    public void setTestReportId(Long testReportId) {
        this.testReportId = testReportId;
    }

    public String getReRunType() {
        return reRunType;
    }

    public void setReRunType(String reRunType) {
        this.reRunType = reRunType;
    }

    public boolean isOpenSmartRegress() {
        return openSmartRegress;
    }

    public void setOpenSmartRegress(boolean openSmartRegress) {
        this.openSmartRegress = openSmartRegress;
    }

    public String getCaseIds() {
        return caseIds;
    }

    public void setCaseIds(String caseIds) {
        this.caseIds = caseIds;
    }

    public Map<String, String> getAccuracyInfo() {
        return accuracyInfo;
    }

    public void setAccuracyInfo(Map<String, String> accuracyInfo) {
        this.accuracyInfo = accuracyInfo;
    }

    public boolean isOpenAccuracy() {
        return openAccuracy;
    }

    public void setOpenAccuracy(boolean openAccuracy) {
        this.openAccuracy = openAccuracy;
    }

    public String getCaseTemplate() {
        return caseTemplate;
    }

    public void setCaseTemplate(String caseTemplate) {
        this.caseTemplate = caseTemplate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }



    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }



    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getRunType() {
        return runType;
    }

    public void setRunType(String runType) {
        this.runType = runType;
    }

    public Map<String, List<String>> getEnvInfo() {
        return envInfo;
    }

    public void setEnvInfo(Map<String, List<String>> envInfo) {
        this.envInfo = envInfo;
    }

    public List<String> getCaseGroup() {
        return caseGroup;
    }

    public void setCaseGroup(List<String> caseGroup) {
        this.caseGroup = caseGroup;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
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
