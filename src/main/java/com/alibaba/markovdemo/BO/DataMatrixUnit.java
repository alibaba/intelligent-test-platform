package com.alibaba.markovdemo.BO;

import com.alibaba.markovdemo.engine.stages.DetailDataInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class DataMatrixUnit {

    private List<Long> caseids;

    private int callNum;

    private String name;

    private boolean restartFlag;

    //做数据准备用
//    public RunData runData;

    public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareData;

    public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> getPrepareData() {
        return prepareData;
    }

    public void setPrepareData(LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareData) {
        this.prepareData = prepareData;
    }

    //存ds的编码key
    public List<String> dataKeyList ;

    public List<String> getDataKeyList() {
        return dataKeyList;
    }

    public void setDataKeyList(List<String> dataKeyList) {
        this.dataKeyList = dataKeyList;
    }

    public DataMatrixUnit(){

        caseids = new ArrayList<>();
        dataKeyList= new ArrayList<>();
        callNum = 0;
        restartFlag = false;
    }



    public List<Long> getCaseids() {
        return caseids;
    }

    public void setCaseids(List<Long> caseids) {
        this.caseids = caseids;
    }

    public int getCallNum() {
        return callNum;
    }

    public void setCallNum(int callNum) {
        this.callNum = callNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRestartFlag() {
        return restartFlag;
    }

    public void setRestartFlag(boolean restartFlag) {
        this.restartFlag = restartFlag;
    }


    public void addCaseid(Long caseId,String dataKey){

        caseids.add(caseId);
        dataKeyList.add(dataKey);
        callNum ++ ;
    }
}
