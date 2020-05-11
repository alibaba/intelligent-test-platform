package com.alibaba.markovdemo.BO;

import java.util.List;


public class MultiInfo {

    String type;
    String caseDesc;
    String caseTag;
    List<Expandkv> expandkvist;

    public List<Expandkv> getExpandkvist() {
        return expandkvist;
    }

    public void setExpandkvist(List<Expandkv> expandkvist) {
        this.expandkvist = expandkvist;
    }

    public String getCaseDesc() {
        return caseDesc;
    }

    public void setCaseDesc(String caseDesc) {
        this.caseDesc = caseDesc;
    }

    public String getCaseTag() {
        return caseTag;
    }

    public void setCaseTag(String caseTag) {
        this.caseTag = caseTag;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

