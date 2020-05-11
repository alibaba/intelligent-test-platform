package com.alibaba.markovdemo.BO;


public class AllReport {
    int caseNum;
    int envNum;
    int sucessCaseNum;
    int failCaseNum;
    String runTime;

    public int getCaseNum() {
        return caseNum;
    }

    public void setCaseNum(int caseNum) {
        this.caseNum = caseNum;
    }

    public int getEnvNum() {
        return envNum;
    }

    public void setEnvNum(int envNum) {
        this.envNum = envNum;
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

    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }
}
