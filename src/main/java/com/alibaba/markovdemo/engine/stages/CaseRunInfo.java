package com.alibaba.markovdemo.engine.stages;

import java.util.LinkedList;
import java.util.Map;

public class CaseRunInfo extends SerialCloneable {

    public String groupName;

    public LinkedList<RunningDataInfo> data;

    public Map<String,Boolean> selectCheckType;

    public Long scenarioId;
    public Long testcaseId;

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(Long testcaseId) {
        this.testcaseId = testcaseId;
    }

    public Map<String, Boolean> getSelectCheckType() {
        return selectCheckType;
    }

    public void setSelectCheckType(Map<String, Boolean> selectCheckType) {
        this.selectCheckType = selectCheckType;
    }


    public LinkedList<RunningDataInfo> getData() {
        return data;
    }

    public void setData(LinkedList<RunningDataInfo> data) {
        this.data = data;
    }
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


}
