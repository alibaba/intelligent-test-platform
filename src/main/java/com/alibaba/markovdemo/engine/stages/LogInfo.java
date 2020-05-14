package com.alibaba.markovdemo.engine.stages;

public class LogInfo {

    public StageName stageName;

    public String groupName;

    public String pluginName;

    //public int dataListIndex = 1;

    public String log;

    public RunData runData ;



    public RunData getRunData() {
        return runData;
    }

    public void setRunData(RunData runData) {
        this.runData = runData;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public StageName getStageName() {
        return stageName;
    }

    public void setStageName(StageName stageName) {
        this.stageName = stageName;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
