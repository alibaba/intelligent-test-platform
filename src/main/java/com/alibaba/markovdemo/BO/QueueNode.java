package com.alibaba.markovdemo.BO;


import com.alibaba.markovdemo.engine.stages.DetailDataInfo;
import com.alibaba.markovdemo.engine.stages.SerialCloneable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class QueueNode extends SerialCloneable {

    public List<Long> caseids;

    public int caseNum;

    public String cost;

    public String saveTime;

    public String next;

    public String dataType;

    public String restartPath= "";

    public boolean restartFlag;

//    public RunData runData;

    public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareData;

    public List<String> dataKeyList;

    public List<String> getDataKeyList() {
        return dataKeyList;
    }

    public void setDataKeyList(List<String> dataKeyList) {
        this.dataKeyList = dataKeyList;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isRestartFlag() {
        return restartFlag;
    }

    public void setRestartFlag(boolean restartFlag) {
        this.restartFlag = restartFlag;
    }

    public List<Long> getCaseids() {
        return caseids;
    }

    public void setCaseids(List<Long> caseids) {
        this.caseids = caseids;
    }

    public int getCaseNum() {
        return caseNum;
    }

    public void setCaseNum(int caseNum) {
        this.caseNum = caseNum;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> getPrepareData() {
        return prepareData;
    }

    public void setPrepareData(LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareData) {
        this.prepareData = prepareData;
    }


    public QueueNode(){
        caseids = new ArrayList<>();
    }

    public void addCaseList(List<TestCaseAI>  testcaseList){
        for (TestCaseAI testCaseAI : testcaseList){
            caseids.add(testCaseAI.getId());
        }
        caseNum = testcaseList.size();
    }

    public String getRestartPath() {
        return restartPath;
    }

    public void setRestartPath(String restartPath) {
        this.restartPath = restartPath;
    }

}
