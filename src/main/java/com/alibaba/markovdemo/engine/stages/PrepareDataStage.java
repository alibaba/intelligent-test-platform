package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.TestRespone;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrepareDataStage extends BaseStage {


    //type=>group=>modulelist
    private HashMap<String, Map<String, List<String>>> dataTypeConfig = new HashMap<>();

    //TODO: group->dsname

    public BaseStage build(JSONObject stageJsonObj) {

        if (stageJsonObj.containsKey("params")) {
            JSONObject paramsJson = (JSONObject)stageJsonObj.get("params");

            if (paramsJson.containsKey("datasource")){
                JSONObject innerJson = (JSONObject)paramsJson.get("datasource");
                //type
                for (String type: innerJson.keySet()) {
                    Map<String, List<String>> dataConfigMap = new HashMap<>();
                    //group
                    JSONObject groupjs = (JSONObject)innerJson.get(type);
                    for(String groupName : groupjs.keySet()){
                        List<String> moduleList = (List<String>)groupjs.get(groupName);
                        dataConfigMap.put(groupName,moduleList);

                    }
                    dataTypeConfig.put(type,dataConfigMap);
                }
            }
        }

        return super.build(StageName.prepareData, stageJsonObj);

    }


    HashMap<String,HashMap<String,List<String>>> datasourParse (String content){

        String[] dataList = content.split("\n");
        String[] recordInfo;
        String type;
        String groupName;
        String dsName;
        HashMap<String,HashMap<String,List<String>>> datasourceMap = new HashMap<>();
        HashMap<String,List<String>> groupMap;
        List<String> dsList;

        for (String record : dataList){
            //标准格式
            if (record.split(":").length == 3){
                recordInfo = record.split(":");
                type = recordInfo[0];
                groupName = recordInfo[1];
                dsName = recordInfo[2];
                //如果type存在
                if (datasourceMap.containsKey(type)){
                    groupMap = datasourceMap.get(type);
                    //如果包含groupName
                    if (groupMap.containsKey(groupName)){
                        dsList = groupMap.get(groupName);
                        dsList.add(dsName);
                    }
                    //如果不包含groupName
                    else{
                        dsList = new ArrayList<>();
                        dsList.add(dsName);
                        groupMap.put(groupName,dsList);
                    }
                }
                //如果type不存在
                else{
                    dsList = new ArrayList<>();
                    dsList.add(dsName);
                    groupMap = new HashMap<>();
                    groupMap.put(groupName,dsList);
                    datasourceMap.put(type,groupMap);
                }
            }
        }

        return datasourceMap;
    }


    @Override
    public TestRespone setTestRespone(TestRespone testRespone){

        //获取当前阶段的log
        testRespone.getLogStage().putAll(mergeResponeLog(getLogCollection()));

        return testRespone;
    }


}
