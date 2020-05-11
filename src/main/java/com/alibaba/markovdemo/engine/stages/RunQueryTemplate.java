package com.alibaba.markovdemo.engine.stages;

public class RunQueryTemplate {
    private String label;
    private String dataId;
    private String groupId;
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getDataId() {
        return dataId;
    }
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public RunQueryTemplate(String label, String dataId, String groupId) {
        super();
        this.label = label;
        this.dataId = dataId;
        this.groupId = groupId;
    }


    
}
