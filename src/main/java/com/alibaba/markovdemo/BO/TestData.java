package com.alibaba.markovdemo.BO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public abstract class TestData implements Serializable {
    Long id;
    String name;
    String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String serializeToString() {
        return value;
    }

    public void deserializeFromString(String s) {
        value = s;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
