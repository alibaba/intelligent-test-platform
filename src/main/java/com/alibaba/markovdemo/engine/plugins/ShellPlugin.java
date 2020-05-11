package com.alibaba.markovdemo.engine.plugins;

import com.alibaba.fastjson.JSONObject;

import com.alibaba.markovdemo.engine.stages.RunData;
import com.alibaba.markovdemo.engine.stages.StageName;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

public class ShellPlugin implements IPlugin {

    private String name;
    private String displayName;
    private String shellScript;

    @Override
    public IPlugin build(JSONObject obj ,JSONObject pipelineParamsObj) {

        //TODO:build object
        return this;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;

    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;

    }

    public String getPythonScript() {
        return this.shellScript;
    }

    public void setPythonScript(String pythonScript) {
        this.shellScript = shellScript;

    }

    @Override
    public void beforeExec() {

    }

    @Override
    public void exec() {
        throw new NotImplementedException();

    }

    @Override
    public void afterExec() {

    }

    @Override
    public void setRunData(RunData data) {

    }

    @Override
    public RunData getRunData() {
        return null;
    }

    @Override
    public Map<StageName, Object> getStageParamsObj() {
        return null;
    }

    @Override
    public void setStageParamsObj(Map<StageName, Object> stageParamsObj) {

    }

    @Override
    public void setPluginParams(JSONObject pluginParams) {

    }
}
