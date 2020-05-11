package com.alibaba.markovdemo.BO;


import com.alibaba.markovdemo.engine.stages.ResultStatus;
import com.alibaba.markovdemo.engine.stages.RunData;
import com.alibaba.markovdemo.engine.stages.StageName;
import org.codehaus.groovy.util.ListHashMap;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class TestRespone implements Serializable {

    private Long id;
    private Long testCaseId;
    private Long testReportId;
    private Date startTime;
    private Date endTime;
    private String caseTemplate;
    private String user;

    ResultStatus status;
    //ValidationResult validationResult;
    //结果返回
    HashMap<String, List<RunData>> runStage = new HashMap();
    //log透出
    HashMap<StageName,ListHashMap<String, String>> logStage = new HashMap();

    public String getCaseTemplate() {
        return caseTemplate;
    }

    public void setCaseTemplate(String caseTemplate) {
        this.caseTemplate = caseTemplate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(Long testCaseId) {
        this.testCaseId = testCaseId;
    }

    public Long getTestReportId() {
        return testReportId;
    }

    public void setTestReportId(Long testReportId) {
        this.testReportId = testReportId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }

    //public ValidationResult getValidationResult() {
    //    return validationResult;
    //}
    //
    //public void setValidationResult(ValidationResult validationResult) {
    //    this.validationResult = validationResult;
    //}


    public HashMap<StageName, ListHashMap<String, String>> getLogStage() {
        return logStage;
    }

    public void setLogStage(
        HashMap<StageName, ListHashMap<String, String>> logStage) {
        this.logStage = logStage;
    }
    public HashMap<String, List<RunData>> getRunStage() {
        return runStage;
    }

    public void setRunStage(
        HashMap<String, List<RunData>> runStage) {
        this.runStage = runStage;
    }


}



