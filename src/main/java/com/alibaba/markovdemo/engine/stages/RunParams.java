package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.util.Map;

public class RunParams {

    //各个阶段的参数,比如部署阶段的远程机器信息
     Map<StageName,Object> stageParamsObj;
    //pipeline设置的参数
     JSONObject pipelineParamsObj;

     public Map<StageName, Object> getStageParamsObj() {
         return stageParamsObj;
     }


    public void setStageParamsObj(
         Map<StageName, Object> stageParamsObj) {
         this.stageParamsObj = stageParamsObj;
     }

     public JSONObject getPipelineParamsObj() {
         return pipelineParamsObj;
     }

     public void setPipelineParamsObj(JSONObject pipelineParamsObj) {
         this.pipelineParamsObj = pipelineParamsObj;
     }

    @Override
    public String toString() {
        String jsonString = JSON.toJSONString(this);
        return jsonString;
    }

}
