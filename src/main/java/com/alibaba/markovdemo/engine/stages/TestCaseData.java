package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.entity.IPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class TestCaseData extends SerialCloneable {

    private static final Logger logger = LoggerFactory.getLogger(SerialCloneable.class);

    public static final String TESTDATA_DESCRIPTION_KEY = "description";
    public static final String TESTDATA_LONG_DESCRIPTION_KEY = "longDescription";
    public static final String TESTDATA_PREPARE_DATA_KEY = "prepareData";
    public static final String TESTDATA_RUN_DATA_KEY = "caseRunStage";


    IPipeline pipeline;

    LinkedHashMap<String, LinkedList<RunningDataInfo>> runDataMap = new LinkedHashMap<>();

    LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> preparDataList = new LinkedList<>();

    //控制器中使用
    public Map<StageName,Object> testCaseInputMap = new HashMap<>();

    public static TestCaseData build(JSONObject testDataJsonObj) {

        logger.info("[载入用例数据]");

        TestCaseData testCaseData = new TestCaseData();

        //JSONObject testDataJsonObj = JSON.parseObject(jsonString);

        //// 必填
        //testCaseData.setId(testDataJsonObj.containsKey(TESTDATA_ID_KEY)? Long.valueOf((Integer)testDataJsonObj.get(TESTDATA_ID_KEY)) :null);
        //testCaseData.setScenarioId(testDataJsonObj.containsKey(TESTDATA_SCENARIO_ID_KEY)?  Long.valueOf((Integer)testDataJsonObj.get(TESTDATA_SCENARIO_ID_KEY)) :null);
        //testCaseData.setAppId(testDataJsonObj.containsKey(TESTDATA_APP_ID_KEY)?  Long.valueOf((Integer)testDataJsonObj.get(TESTDATA_APP_ID_KEY)) :null);
        //testCaseData.setName(testDataJsonObj.getString(TESTDATA_NAME_KEY));
        //
        //// 选填
        //if (testDataJsonObj.containsKey(TESTDATA_DESCRIPTION_KEY)) {
        //    testCaseData.setDescription(testDataJsonObj.getString(TESTDATA_DESCRIPTION_KEY));
        //}
        //
        //if (testDataJsonObj.containsKey(TESTDATA_LONG_DESCRIPTION_KEY)) {
        //    testCaseData.setLongDescription(testDataJsonObj.getString(TESTDATA_LONG_DESCRIPTION_KEY));
        //}

        //假设由 scenarioId+appId 决定使用哪个pipeline
        // IPipeline pipeline =PipelineFactory.getPipeline(String key);
        testCaseData.setPipeline(null);

        parseRunData(testCaseData, testDataJsonObj);
        //parsePrepareDataList(testCaseData, testDataJsonObj);

        testCaseData.testCaseInputMap.put(StageName.prepareData,testCaseData.preparDataList);
        testCaseData.testCaseInputMap.put(StageName.caseRunStage,testCaseData.runDataMap);

        if (testDataJsonObj.containsKey(TESTDATA_PREPARE_DATA_KEY)) {

            JSONArray prepareDataArray = testDataJsonObj.getJSONArray(TESTDATA_PREPARE_DATA_KEY);
            //for (JSONObject prepareDataRecordJsonObj : prepareDataArray.toJavaList(JSONObject.class)) {
            for(int i=0; i<prepareDataArray.size(); i++) {
                JSONObject prepareDataRecordJsonObj = prepareDataArray.getJSONObject(i);
                HashMap<String, LinkedList<DetailDataInfo>> prepareDataRecord = new HashMap<>();

                for (Utils.PrepareDataType type : Utils.PrepareDataType.values()) {
                    if (prepareDataRecordJsonObj.containsKey(type.name())) {
                        parsePrepareData(type, prepareDataRecordJsonObj, prepareDataRecord);
                    }
                }

                testCaseData.preparDataList.add(prepareDataRecord);
            }
        }
        return testCaseData;
    }


    public static void parsePrepareData(Utils.PrepareDataType dataType, JSONObject prepareDataRecordJsonObj, HashMap<String, LinkedList<DetailDataInfo>> prepareDataRecord) {

        //String dataTypeString = Utils.convertDataType2String(dataType);

        if (!prepareDataRecord.containsKey(dataType)) {
            prepareDataRecord.put(dataType.name(), new LinkedList<DetailDataInfo>());
        }

        JSONArray tairDetailJsonArray = prepareDataRecordJsonObj.getJSONArray(dataType.name());

        for (int i=0; i< tairDetailJsonArray.size(); i++) {
            JSONObject tairDetailJson = tairDetailJsonArray.getJSONObject(i);
            DetailDataInfo detailData = new DetailDataInfo();

            detailData.dsName = tairDetailJson.getString("dsName");

            if (tairDetailJson.containsKey("data")) {
                JSONArray tairDataArray = tairDetailJson.getJSONArray("data");
                //for (JSONObject jsonDetail : tairDataArray.toJavaList(JSONObject.class)) {
                for(int j=0; j< tairDataArray.size(); j++) {
                    JSONObject jsonDetail = tairDataArray.getJSONObject(j);
                    RecordInfo recordInfo = new RecordInfo();
                    recordInfo.setKey(Utils.getValueOrEmptyString(jsonDetail, "key"));
                    recordInfo.setValue(Utils.getValueOrEmptyString(jsonDetail, "value"));
                    recordInfo.setProperty(Utils.getValueOrEmptyString(jsonDetail, "property"));
                    detailData.data.add(recordInfo);
                }
            }

            prepareDataRecord.get(dataType.name()).add(detailData);
        }
    }




    public static void parseComponentAdd(String dataType, JSONObject prepareDataRecordJsonObj, HashMap<String, LinkedList<DetailDataInfo>> prepareDataRecord) {

        //String dataTypeString = Utils.convertDataType2String(dataType);

        if (!prepareDataRecord.containsKey(dataType)) {
            prepareDataRecord.put(dataType, new LinkedList<DetailDataInfo>());
        }

        JSONArray tairDetailJsonArray = prepareDataRecordJsonObj.getJSONArray(dataType);
        //for (JSONObject tairDetailJson : tairDetailJsonArray.toJavaList(JSONObject.class)) {
        for (int i=0; i< tairDetailJsonArray.size(); i++) {
            JSONObject tairDetailJson = tairDetailJsonArray.getJSONObject(i);
            DetailDataInfo detailData = new DetailDataInfo();

            detailData.dsName = tairDetailJson.getString("dsName");

            if (tairDetailJson.containsKey("data")) {
                JSONArray tairDataArray = tairDetailJson.getJSONArray("data");
                //for (JSONObject jsonDetail : tairDataArray.toJavaList(JSONObject.class)) {
                for(int j=0; j< tairDataArray.size(); j++) {
                    JSONObject jsonDetail = tairDataArray.getJSONObject(j);
                    RecordInfo recordInfo = new RecordInfo();
                    recordInfo.setKey(Utils.getValueOrEmptyString(jsonDetail, "key"));
                    recordInfo.setValue(Utils.getValueOrEmptyString(jsonDetail, "value"));
                    recordInfo.setProperty(Utils.getValueOrEmptyString(jsonDetail, "property"));
                    detailData.data.add(recordInfo);
                }
            }

            prepareDataRecord.get(dataType).add(detailData);
        }
    }
    public static void parsePrepareDataAddInfo(Utils.PrepareDataType dataType, JSONObject prepareDataRecordJsonObj, HashMap<String, LinkedList<DetailDataInfo>> prepareDataRecord) {

        //String dataTypeString = Utils.convertDataType2String(dataType);

        if (!prepareDataRecord.containsKey(dataType)) {
            prepareDataRecord.put(dataType.name(), new LinkedList<DetailDataInfo>());
        }

        JSONArray tairDetailJsonArray = prepareDataRecordJsonObj.getJSONArray(dataType.name());

        //for (JSONObject tairDetailJson : tairDetailJsonArray.toJavaList(JSONObject.class)) {
        for (int i=0; i< tairDetailJsonArray.size(); i++) {
            JSONObject tairDetailJson = tairDetailJsonArray.getJSONObject(i);
            DetailDataInfo detailData = new DetailDataInfo();
            detailData.dsName = tairDetailJson.getString("dsName");

            if (tairDetailJson.containsKey("data")) {
                JSONArray tairDataArray = tairDetailJson.getJSONArray("data");
                //for (JSONObject jsonDetail : tairDataArray.toJavaList(JSONObject.class)) {
                for(int j=0; j< tairDataArray.size(); j++) {
                    JSONObject jsonDetail = tairDataArray.getJSONObject(j);
                    RecordInfo recordInfo = new RecordInfo();
                    recordInfo.setKey(Utils.getValueOrEmptyString(jsonDetail, "key"));
                    recordInfo.setValue(Utils.getValueOrEmptyString(jsonDetail, "value"));
                    recordInfo.setProperty(Utils.getValueOrEmptyString(jsonDetail, "property"));
                    detailData.data.add(recordInfo);
                }
            }

            prepareDataRecord.get(dataType.name()).add(detailData);
        }
    }

    public static void parseRunData(TestCaseData testCaseData, JSONObject testDataJsonObj) {

        if (testDataJsonObj.containsKey(TESTDATA_RUN_DATA_KEY)) {

            JSONArray groupJsonArray = testDataJsonObj.getJSONArray(TESTDATA_RUN_DATA_KEY);
//            System.out.println(groupJsonArray);

            for (int i = 0; i < groupJsonArray.size(); i++) {
                JSONObject groupJson = groupJsonArray.getJSONObject(i);

                String groupName = groupJson.getString("group_name");

                LinkedList<RunningDataInfo> infoList = new LinkedList<RunningDataInfo>();

                JSONArray dataArray = groupJson.getJSONArray("data");
                for (int j = 0; j < dataArray.size(); j++) {
                    JSONObject dataObj = dataArray.getJSONObject(j);
                    RunningDataInfo info = new RunningDataInfo();
                    if (dataObj.containsKey("input")) {
                        info.input = dataObj.getString("input");
                    }

                    if (dataObj.containsKey("expect")) {
                        info.expect = dataObj.getString("expect");
                    }
                    if (groupJson.containsKey("selectCheckType")) {
                        info.selectCheckType = (Map<String, Boolean>) groupJson.get("selectCheckType");
                    }
                    info.setScenarioId(Long.valueOf(String.valueOf(testDataJsonObj.get("scenarioId"))));
                    try {
                        info.setTestcaseId(Long.valueOf(String.valueOf(testDataJsonObj.get("id"))));
                    }catch(Exception e){
                        info.setTestcaseId(99999999L);
                    }

                    infoList.add(info);
                }


                testCaseData.runDataMap.put(groupName, infoList);
            }

        }
    }


    public IPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(IPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public LinkedHashMap<String, LinkedList<RunningDataInfo>> getRunDataMap() {
        return runDataMap;
    }

    public void setRunDataMap(
        LinkedHashMap<String, LinkedList<RunningDataInfo>> runDataMap) {
        this.runDataMap = runDataMap;
    }

    public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> getPreparDataList() {
        return preparDataList;
    }

    public void setPreparDataList(
        LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> preparDataList) {
        this.preparDataList = preparDataList;
    }

}

