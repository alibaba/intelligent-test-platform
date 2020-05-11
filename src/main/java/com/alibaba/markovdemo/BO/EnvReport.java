package com.alibaba.markovdemo.BO;


public class EnvReport {

    String envName;
    int caseNum;
    String runTime;
    int parallelCaseNum;
    String parallelRunTime;
    int serialCaseNum;
    String serialRunTime;
    int dumpDataRecoredNum;
    String dumpRunTime;
    int sucessCaseNum;
    int failCaseNum;
    String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    EnvReport(){
        caseNum = 0;
        parallelCaseNum = 0;
        serialCaseNum = 0;
        dumpDataRecoredNum = 0;
        sucessCaseNum = 0;
        failCaseNum = 0;
    }


    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public int getCaseNum() {
        return caseNum;
    }

    public void setCaseNum(int caseNum) {
        this.caseNum = caseNum;
    }

    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public int getParallelCaseNum() {
        return parallelCaseNum;
    }

    public void setParallelCaseNum(int parallelCaseNum) {
        this.parallelCaseNum = parallelCaseNum;
    }

    public String getParallelRunTime() {
        return parallelRunTime;
    }

    public void setParallelRunTime(String parallelRunTime) {
        this.parallelRunTime = parallelRunTime;
    }

    public int getSerialCaseNum() {
        return serialCaseNum;
    }

    public void setSerialCaseNum(int serialCaseNum) {
        this.serialCaseNum = serialCaseNum;
    }

    public String getSerialRunTime() {
        return serialRunTime;
    }

    public void setSerialRunTime(String serialRunTime) {
        this.serialRunTime = serialRunTime;
    }


    public int getDumpDataRecoredNum() {
        return dumpDataRecoredNum;
    }

    public void setDumpDataRecoredNum(int dumpDataRecoredNum) {
        this.dumpDataRecoredNum = dumpDataRecoredNum;
    }

    public String getDumpRunTime() {
        return dumpRunTime;
    }

    public void setDumpRunTime(String dumpRunTime) {
        this.dumpRunTime = dumpRunTime;
    }

    public int getSucessCaseNum() {
        return sucessCaseNum;
    }

    public void setSucessCaseNum(int sucessCaseNum) {
        this.sucessCaseNum = sucessCaseNum;
    }

    public int getFailCaseNum() {
        return failCaseNum;
    }

    public void setFailCaseNum(int failCaseNum) {
        this.failCaseNum = failCaseNum;
    }
}


