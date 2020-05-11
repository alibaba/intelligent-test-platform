package com.alibaba.markovdemo.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.engine.stages.StageName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//布局对象,前端使用
public class Layout {
    public String caseName;
    public String caseDese;
    public String caseLongDese;
    public Map<StageName,Object> pipelineStage =new HashMap<>();
    public List<DataprepareLayout> dataPrepareStage = new ArrayList<>();
    public JSONObject dataPrepareStageNew = new JSONObject();
    public List<ComponentLayout> componentStage = new ArrayList<>();
    public List<Map<String,String>> caseRunStage = new ArrayList<>();
    public Map<String,Map<String, Map<String, Map<String, Object>>>> caseRunStageTemplate = new HashMap<>();
    public Map<String,Map<String, Map<String, Map<String, Object>>>> caseRunStageExpectTemplate = new HashMap<>();
    public JSONArray dataGenStage;



    public class DataprepareLayout{
        String type;
        List<String> dsNameList;
        Map<String, List<Map<String, Object>>> templates = new HashMap<>();

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getDsNameList() {
            return dsNameList;
        }

        public void setDsNameList(List<String> dsNameList) {
            this.dsNameList = dsNameList;
        }

        public Map<String, List<Map<String, Object>>> getTemplates() {
            return templates;
        }

        public void setTemplates(Map<String, List<Map<String, Object>>> templates) {
            this.templates = templates;
        }


    }

    public class ComponentLayout {
        String key;
        String displayName;
        List<String> nameList;

        public ComponentLayout() {
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public List<String> getNameList() {
            return nameList;
        }

        public void setNameList(List<String> nameList) {
            this.nameList = nameList;
        }
    }





    public Map<String, Map<String, Map<String, Map<String, Object>>>> getCaseRunStageExpectTemplate() {
        return caseRunStageExpectTemplate;
    }

    public void setCaseRunStageExpectTemplate(Map<String, Map<String, Map<String, Map<String, Object>>>> caseRunStageExpectTemplate) {
        this.caseRunStageExpectTemplate = caseRunStageExpectTemplate;
    }

    public JSONObject getDataPrepareStageNew() {
        return dataPrepareStageNew;
    }

    public void setDataPrepareStageNew(JSONObject dataPrepareStageNew) {
        this.dataPrepareStageNew = dataPrepareStageNew;
    }

    public JSONObject restartMap = new JSONObject();

    public JSONArray mutilCheckConfig = new JSONArray();


    public JSONObject getRestartMap() {
        return restartMap;
    }

    public void setRestartMap(JSONObject restartMap) {
        this.restartMap = restartMap;
    }

    public JSONArray getDataGenStage() {
        return dataGenStage;
    }

    public void setDataGenStage(JSONArray dataGenStage) {
        this.dataGenStage = dataGenStage;
    }

    public List<ComponentLayout> getComponentStage() {
        return componentStage;
    }

    public void setComponentStage(List<ComponentLayout> componentStage) {
        this.componentStage = componentStage;
    }

    public List<Map<String, String>> getCaseRunStage() {
        return caseRunStage;
    }

    public void setCaseRunStage(List<Map<String, String>> caseRunStage) {
        this.caseRunStage = caseRunStage;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getCaseDese() {
        return caseDese;
    }

    public void setCaseDese(String caseDese) {
        this.caseDese = caseDese;
    }

    public String getCaseLongDese() {
        return caseLongDese;
    }

    public void setCaseLongDese(String caseLongDese) {
        this.caseLongDese = caseLongDese;
    }

    public Map<StageName, Object> getPipelineStage() {
        return pipelineStage;
    }

    public void setPipelineStage(
            Map<StageName, Object> pipelineStage) {
        this.pipelineStage = pipelineStage;
    }

    public List<DataprepareLayout> getDataPrepareStage() {
        return dataPrepareStage;
    }

    public void setDataPrepareStage(List<DataprepareLayout> dataPrepareStage) {
        this.dataPrepareStage = dataPrepareStage;
    }


    public void setCaseRunStageTemplate(Map<String,Map<String, Map<String, Map<String, Object>>>> caseRunStageTemplate) {
        this.caseRunStageTemplate = caseRunStageTemplate;
    }

    public JSONArray getMutilCheckConfig() {
        return mutilCheckConfig;
    }

    public void setMutilCheckConfig(JSONArray mutilCheckConfig) {
        this.mutilCheckConfig = mutilCheckConfig;
    }

}