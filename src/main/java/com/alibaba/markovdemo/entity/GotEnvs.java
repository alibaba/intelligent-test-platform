package com.alibaba.markovdemo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class GotEnvs extends BaseEntity {
    private Long scenarioId;
    private String name;
    private Date gmtCreate;
    private Date gmtModified;
    private String status;
    private String hostIp;
    private String envDetail;
    //当前是否选中
    private String currentEnv;

    public String getCurrentEnv() {
        return currentEnv;
    }

    public void setCurrentEnv(String currentEnv) {
        this.currentEnv = currentEnv;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }


    public String getEnvDetail() {
        return envDetail;
    }

    public void setEnvDetail(String envDetail) {
        this.envDetail = envDetail;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT-5")
    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT-5")
    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
