package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSONObject;


public class RunningStageFactory {

    // stage name key in pipeline json file
    static final String PrepareDataStageString = "prepareData-stage";
    static final String DeployStageString = "deploy-stage";
    static final String CaseRunStageString = "caseRun-stage";
    static final String SendStageString = "send-stage";
    static final String GetResponseStageString = "getResponse-stage";
    static final String CheckStageString = "check-stage";
    static final String LogStageString = "log-stage";

    public static BaseStage getRunningStage(String stageName, JSONObject stageJsonObj) {

        if (stageName == null || stageJsonObj == null) {
            return null;
        }
        StageName stage = convertStageStr2Enum(stageName);

        switch (stage) {
            case prepareData:
                return new PrepareDataStage().build(stageJsonObj);
            case caseRunStage:
                return new CaseRunStage().build(stageJsonObj);
            case sendStage:
                return new CaseRunStage.SendStage().build(stageJsonObj);
            case getResponseStage:
                return new CaseRunStage.GetResponseStage().build(stageJsonObj);
            case checkStage:
                return new CaseRunStage.CheckStage().build(stageJsonObj);
            default:
                return null;
        }

    }


    private static StageName convertStageStr2Enum(String stageName) {

        if (stageName.equalsIgnoreCase(PrepareDataStageString)) {
            return StageName.prepareData;
        } else if (stageName.equalsIgnoreCase(DeployStageString)) {
            return StageName.deploy;
        } else if (stageName.equalsIgnoreCase(CaseRunStageString)) {
            return StageName.caseRunStage;
        } else if (stageName.equalsIgnoreCase(SendStageString)) {
            return StageName.sendStage;
        } else if (stageName.equalsIgnoreCase(GetResponseStageString)) {
            return StageName.getResponseStage;
        } else if (stageName.equalsIgnoreCase(CheckStageString)) {
            return StageName.checkStage;
        } else if (stageName.equalsIgnoreCase(LogStageString)) {
            return StageName.logAppStage;
        } else {
            throw new IllegalArgumentException(String.format("{0} is invalid.", stageName));
        }

    }
}
