package com.alibaba.markovdemo.engine.plugins;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.engine.stages.RunData;
import com.alibaba.markovdemo.engine.stages.StageName;


import java.util.Map;

public interface IPlugin {

    final String PluginTypeKey = "plugin_type";

    IPlugin build(JSONObject obj, JSONObject pipelineParamsObj);

    String getName();

    void setName(String name);

    String getDisplayName();

    void setDisplayName(String displayName);

    void beforeExec();

    void exec();

    void afterExec();

    void setRunData(RunData data);

    RunData getRunData();

    Map<StageName, Object> getStageParamsObj();

    void setStageParamsObj(Map<StageName, Object> stageParamsObj);

    void setPluginParams(JSONObject pluginParams);
}


