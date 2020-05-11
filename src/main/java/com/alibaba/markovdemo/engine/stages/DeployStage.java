package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSONObject;

public class DeployStage extends BaseStage {

    static final String HostKey = "host";
    /* TODO:需要定义特定值 */
    private String host;

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public BaseStage build(JSONObject stageJsonObj) {

        String hostValue = stageJsonObj.getString(HostKey);
        if (hostValue != null) {
            this.setHost(hostValue);
        }

        return super.build(StageName.deploy, stageJsonObj);

    }
}
