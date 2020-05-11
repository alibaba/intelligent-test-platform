package com.alibaba.markovdemo.engine.stages;

public class RecordInfo extends SerialCloneable {
    public String key;
    public String value;
    public String property;


    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}