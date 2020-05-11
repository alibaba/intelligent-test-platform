package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSONObject;

import org.springframework.stereotype.Service;

@Service
public class CaseRunStage extends CompositeBaseStage {

    public BaseStage build(JSONObject stageJsonObj) {

        return super.build(StageName.caseRunStage, stageJsonObj);

    }


    public static class SendStage extends BaseStage {

        public BaseStage build(JSONObject stageJsonObj) {

            return super.build(StageName.sendStage, stageJsonObj);

        }
    }

    public static class GetResponseStage extends BaseStage {

        public BaseStage build(JSONObject stageJsonObj) {

            return super.build(StageName.getResponseStage, stageJsonObj);

        }
    }

    public static class CheckStage extends BaseStage {

        public BaseStage build(JSONObject stageJsonObj) {

            return super.build(StageName.checkStage, stageJsonObj);

        }
    }

}
