package com.alibaba.markovdemo.BO;


public class BasicReport {
    int caseNum;
    int sucessCaseNum;
    int failCaseNum;
    int envNum;
    String runTime;
    String regressionType;

    public String getRegressionType() {
        return regressionType;
    }

    public void setRegressionType(String regressionType) {
        this.regressionType = regressionType;
    }

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
