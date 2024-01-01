package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.RunDs;

import java.util.HashMap;
import java.util.Map;

public class RunData extends SerialCloneable {

    Long scenarioId;
    Long testcaseId;
    String input;
    byte[] bInput;
    String output;
    String expect;
    String calFieldsConfig;
    //默认成功
    int result = 1;
    String log;
    String actual;
    Map<String, RunDs> runDsMap = new HashMap<>();

    public Map<String, RunDs> getRunDsMap() {
        return runDsMap;
    }

    public void setRunDsMap(Map<String, RunDs> runDsMap) {
        this.runDsMap = runDsMap;
    }

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

    //执行数据间的复杂参数传递
    JSONObject params = new JSONObject();

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }


    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getExpect() {
        return expect;
    }

    public void setExpect(String expect) {
        this.expect = expect;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }


    public byte[] getbInput() {
        return bInput;
    }

    public void setbInput(byte[] input2) {
        this.bInput = input2;
    }

    @Override
    public String toString() {
        String jsonString = JSON.toJSONString(this);
        return jsonString;
    }
}

