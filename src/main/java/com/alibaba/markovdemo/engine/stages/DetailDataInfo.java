package com.alibaba.markovdemo.engine.stages;

import java.util.LinkedList;

public class DetailDataInfo extends SerialCloneable {
    public String dsName;
    // key,value,propety
    public LinkedList<RecordInfo> data = new LinkedList<>();

    // 0表示热加载,1表示重启加载
    public String restartFlag;

    public Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRestartFlag() {
        return restartFlag;
    }

    public void setRestartFlag(String restartFlag) {
        this.restartFlag = restartFlag;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public LinkedList<RecordInfo> getData() {
        return data;
    }

    public String getDataString(){
        String res = "";
        for(RecordInfo temp : data){
            res = res+"{";
            res = res + temp.getKey() + ": " + temp.getValue() + ": " + temp.getProperty();
            res = res + "}\n";
        }
        return res;
    }

    public void setData(LinkedList<RecordInfo> data) {
        this.data = data;
    }
}
