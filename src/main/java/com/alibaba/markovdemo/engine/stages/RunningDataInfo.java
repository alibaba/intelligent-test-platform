package com.alibaba.markovdemo.engine.stages;

import java.util.Map;

public class RunningDataInfo extends SerialCloneable {
    public Long scenarioId ;
    public Long testcaseId ;
    public String input ;
    public String expect;
    public String actual;
    public String output;
    public String servicename;

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getFunctionname() {
        return functionname;
    }

    public void setFunctionname(String functionname) {
        this.functionname = functionname;
    }

    public String functionname;
    public String accuInfo;
    public String diffLine;
    public Map<String, Boolean> selectCheckType;
    public String calFieldsConfig;

    public String getCalFieldsConfig() {
        return calFieldsConfig;
    }

    public void setCalFieldsConfig(String calFieldsConfig) {
        this.calFieldsConfig = calFieldsConfig;
    }

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

    public String getDiffLine() {
        return diffLine;
    }

    public void setDiffLine(String diffLine) {
        this.diffLine = diffLine;
    }

    public String getAccuInfo() {
        return accuInfo;
    }

    public void setAccuInfo(String accuInfo) {
        this.accuInfo = accuInfo;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getExpect() {
        return expect;
    }

    public void setExpect(String expect) {
        this.expect = expect;
    }
}
