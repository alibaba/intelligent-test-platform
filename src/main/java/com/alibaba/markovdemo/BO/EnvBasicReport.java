package com.alibaba.markovdemo.BO;


public class EnvBasicReport {

    String envName;
    int caseNum;
    String runTime;
    int sucessCaseNum;
    int failCaseNum;

    //全量数据准备桶(数目/总时间)
    int dumpAllrecordNum;
    String dumpAllTime;

    //分层数据准备桶(总条数/实际执行条数/冗余度/节省时间sec/提速效率/总时间)
    int dumpTreeRecordNum;
    int dumpTreeActualRecordNum;
    String redundency;
    String dumpTreeSaveCost;
    String dumpTreeSpeedUpEfficiency;
    String dumpTreeTime;

    //快速用例运行桶(总串行数/实际串行数/总并行数/节省时间sec/提速效率/总时间)
    int runSerialNum;
    int runSerialActualNum;
    int runParallelNum;
    String runSaveCost;
    String runSpeedUpEfficiency;
    String runTreeTime;

    //失败桶(个数/总时间)
    int runFailNum;
    String runFailTime;
    int runActualFailNum;
    String runFailSavetime;
    String runFailSpeedUpEfficiency;

    public String getRunFailSpeedUpEfficiency() {
        return runFailSpeedUpEfficiency;
    }

    public void setRunFailSpeedUpEfficiency(String runFailSpeedUpEfficiency) {
        this.runFailSpeedUpEfficiency = runFailSpeedUpEfficiency;
    }

    public int getRunActualFailNum() {
        return runActualFailNum;
    }

    public void setRunActualFailNum(int runActualFailNum) {
        this.runActualFailNum = runActualFailNum;
    }

    public String getRunFailSavetime() {
        return runFailSavetime;
    }

    public void setRunFailSavetime(String runFailSavetime) {
        this.runFailSavetime = runFailSavetime;
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

    public int getDumpAllrecordNum() {
        return dumpAllrecordNum;
    }

    public void setDumpAllrecordNum(int dumpAllrecordNum) {
        this.dumpAllrecordNum = dumpAllrecordNum;
    }

    public String getDumpAllTime() {
        return dumpAllTime;
    }

    public void setDumpAllTime(String dumpAllTime) {
        this.dumpAllTime = dumpAllTime;
    }

    public int getDumpTreeRecordNum() {
        return dumpTreeRecordNum;
    }

    public void setDumpTreeRecordNum(int dumpTreeRecordNum) {
        this.dumpTreeRecordNum = dumpTreeRecordNum;
    }

    public int getDumpTreeActualRecordNum() {
        return dumpTreeActualRecordNum;
    }

    public void setDumpTreeActualRecordNum(int dumpTreeActualRecordNum) {
        this.dumpTreeActualRecordNum = dumpTreeActualRecordNum;
    }

    public String getRedundency() {
        return redundency;
    }

    public void setRedundency(String redundency) {
        this.redundency = redundency;
    }

    public String getDumpTreeSaveCost() {
        return dumpTreeSaveCost;
    }

    public void setDumpTreeSaveCost(String dumpTreeSaveCost) {
        this.dumpTreeSaveCost = dumpTreeSaveCost;
    }

    public String getDumpTreeSpeedUpEfficiency() {
        return dumpTreeSpeedUpEfficiency;
    }

    public void setDumpTreeSpeedUpEfficiency(String dumpTreeSpeedUpEfficiency) {
        this.dumpTreeSpeedUpEfficiency = dumpTreeSpeedUpEfficiency;
    }

    public String getDumpTreeTime() {
        return dumpTreeTime;
    }

    public void setDumpTreeTime(String dumpTreeTime) {
        this.dumpTreeTime = dumpTreeTime;
    }

    public int getRunSerialNum() {
        return runSerialNum;
    }

    public void setRunSerialNum(int runSerialNum) {
        this.runSerialNum = runSerialNum;
    }

    public int getRunSerialActualNum() {
        return runSerialActualNum;
    }

    public void setRunSerialActualNum(int runSerialActualNum) {
        this.runSerialActualNum = runSerialActualNum;
    }

    public int getRunParallelNum() {
        return runParallelNum;
    }

    public void setRunParallelNum(int runParallelNum) {
        this.runParallelNum = runParallelNum;
    }

    public String getRunSaveCost() {
        return runSaveCost;
    }

    public void setRunSaveCost(String runSaveCost) {
        this.runSaveCost = runSaveCost;
    }

    public String getRunSpeedUpEfficiency() {
        return runSpeedUpEfficiency;
    }

    public void setRunSpeedUpEfficiency(String runSpeedUpEfficiency) {
        this.runSpeedUpEfficiency = runSpeedUpEfficiency;
    }

    public String getRunTreeTime() {
        return runTreeTime;
    }

    public void setRunTreeTime(String runTreeTime) {
        this.runTreeTime = runTreeTime;
    }

    public int getRunFailNum() {
        return runFailNum;
    }

    public void setRunFailNum(int runFailNum) {
        this.runFailNum = runFailNum;
    }

    public String getRunFailTime() {
        return runFailTime;
    }

    public void setRunFailTime(String runFailTime) {
        this.runFailTime = runFailTime;
    }
}
