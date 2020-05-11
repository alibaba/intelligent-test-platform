package com.alibaba.markovdemo.entity;

import com.alibaba.fastjson.JSONObject;


public interface IPipeline {

    Integer getScenarioId();

    void setScenarioId(Integer id);

    void build(JSONObject pipelineJsonStr);

}

