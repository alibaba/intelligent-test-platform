package com.alibaba.markovdemo.engine.plugins;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.engine.stages.BaseStage;
import com.alibaba.markovdemo.engine.stages.RunningStageFactory;
import com.alibaba.markovdemo.engine.stages.StageName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.LinkedHashMap;

public class Pipeline {
    private static final Logger logger = LoggerFactory.getLogger(Pipeline.class);

    Integer scenarioId;

    public LinkedHashMap<StageName, BaseStage> flow = new LinkedHashMap<>();


    public Integer getScenarioId() {
        return this.scenarioId;
    }

    public void setScenarioId(Integer id) {
        this.scenarioId = id;
    }

    public void build(JSONObject pipelineJsonObj) {

        logger.info("[初始化pipeline插件开始]");
        JSONArray runJsonObj = (JSONArray)pipelineJsonObj.get("run-stage");
        for (Object stageObj : runJsonObj) {
            // 由于pipeline JSONObject 对象的一级子目录有JSONObject和JSONArray两种，因此传递pipeline JSONObject
            JSONObject stageJsonObj = (JSONObject)stageObj;
            String stageName = (String)stageJsonObj.get("stageName");
            BaseStage baseStage = RunningStageFactory.getRunningStage(stageName, stageJsonObj);
            this.flow.put(baseStage.name, baseStage);
        }

    }


}
