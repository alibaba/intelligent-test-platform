package com.alibaba.markovdemo.BO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.engine.stages.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestCaseInput extends SerialCloneable {

    private static final long serialVersionUID = 1L;
    public static final String TESTDATA_PREPARE_DATA_KEY = StageName.prepareData.name();
    public static final String TESTDATA_RUN_DATA_KEY = StageName.caseRunStage.name();
    public static final String TESTDATA_RESPONSE_KEY = "response";


    public static final String TESTDATA_RUN_AGENT_DATA_KEY = StageName.caseRunAgentStage.name();

    Long id;
    Long appId;
    Long scenarioId;
    String description;
    String longDescription;
    String name;
    String createBy;
    String modifiedBy;
    String caseGroup;
    String tag;
    String version;
    String type;
    String regressionIds;
    String caseTemplate;
    boolean isDeploy;
    String caseIds;
    String content;
    private Long parentCaseId;
    Long branchId;
    String branchName;
    int isTrunkFlag;
    Integer isVisible;

    //接收前端传来的阶段数据
    LinkedList<CaseRunInfo> caseRunStage = new LinkedList<>() ;
    LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareData = new LinkedList<>() ;

    //agent形式运行区的组件信息[废弃]
    LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> componentInfo = new LinkedList<>() ;

    //单case执行页面传入的环境信息[废弃]
    HashMap<String, HashMap<String, List<String>>> deploy;

    //新环境管理传来的参数
    Map<String,List<String>> envInfo;
    String envName;

    //新环境管理传来,用来对比的基准环境
    Map<String,List<String>> envInfoBase;

    public Integer getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Integer isVisible) {
        this.isVisible = isVisible;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Long getParentCaseId() {
        return parentCaseId;
    }

    public void setParentCaseId(Long parentCaseId) {
        this.parentCaseId = parentCaseId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCaseIds() {
        return caseIds;
    }

    public void setCaseIds(String caseIds) {
        this.caseIds = caseIds;
    }

    public int getIsTrunkFlag() {
        return isTrunkFlag;
    }

    public void setIsTrunkFlag(int isTrunkFlag) {
        this.isTrunkFlag = isTrunkFlag;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public Map<String, List<String>> getEnvInfoBase() {
        return envInfoBase;
    }

    public void setEnvInfoBase(Map<String, List<String>> envInfoBase) {
        this.envInfoBase = envInfoBase;
    }

    public String getCaseTemplate() {
        return caseTemplate;
    }

    public void setCaseTemplate(String caseTemplate) {
        this.caseTemplate = caseTemplate;
    }
    public Map<String, List<String>> getEnvInfo() {
        return envInfo;
    }

    public void setEnvInfo(Map<String, List<String>> envInfo) {
        this.envInfo = envInfo;
    }

    public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> getComponentInfo() {
        return componentInfo;
    }

    public void setComponentInfo(LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> componentInfo) {
        this.componentInfo = componentInfo;
    }

    public String getRegressionIds() {
        return regressionIds;
    }

    public void setRegressionIds(String regressionIds) {
        this.regressionIds = regressionIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsDeploy() {
        return isDeploy;
    }

    public void setIsDeploy(boolean isDeploy) {
        this.isDeploy = isDeploy;
    }

    public HashMap<String, HashMap<String, List<String>>> getDeploy() {
        return deploy;
    }

    public void setDeploy(
        HashMap<String, HashMap<String, List<String>>> deploy) {
        this.deploy = deploy;
    }



    public LinkedList<CaseRunInfo> getCaseRunStage() {
        return caseRunStage;
    }

    public void setCaseRunStage(LinkedList<CaseRunInfo> caseRunStage) {
        this.caseRunStage = caseRunStage;
    }

    public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> getPrepareData() {
        return prepareData;
    }

    public void setPrepareData(
        LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareData) {
        this.prepareData = prepareData;
    }


    public static LinkedList<CaseRunInfo>  parseCaseRunStageRes(JSONObject testDataJsonObj) {

        LinkedList<CaseRunInfo> caseRunStage = new LinkedList();

        if (testDataJsonObj.containsKey(TESTDATA_RUN_DATA_KEY)) {


            JSONArray groupJsonArray = testDataJsonObj.getJSONArray(TESTDATA_RUN_DATA_KEY);
            JSONObject responseJsonObj = testDataJsonObj.getJSONObject(TESTDATA_RESPONSE_KEY);

            for (int i = 0; i < groupJsonArray.size(); i++) {

                CaseRunInfo caseRunInfo = new CaseRunInfo();

                JSONObject groupJson = groupJsonArray.getJSONObject(i);

                String groupName = groupJson.getString("group_name");
                caseRunInfo.setGroupName(groupName);
                JSONArray resArray = responseJsonObj.getJSONArray(groupName);

                LinkedList<RunningDataInfo> infoList = new LinkedList<RunningDataInfo>();

                caseRunInfo.setSelectCheckType((Map<String, Boolean>) groupJson.get("selectCheckType"));

                JSONArray dataArray = groupJson.getJSONArray("data");

                for (int j = 0; j < dataArray.size(); j++) {
                    JSONObject resObj = dataArray.getJSONObject(j);
                    JSONObject dataObj = resArray.getJSONObject(j);

                    RunningDataInfo info = new RunningDataInfo();
                    if (dataObj.containsKey("input")) {
                        info.input = dataObj.getString("input");
                    }

                    if (dataObj.containsKey("expect")) {
                        info.expect = dataObj.getString("expect");
                    }
                    if (dataObj.containsKey("output")) {
                        info.output = dataObj.getString("output");
                    }
                    if (dataObj.containsKey("actual")) {
                        info.actual = dataObj.getString("actual");
                    }
                    if (dataObj.containsKey("servicename")) {
                        info.servicename = dataObj.getString("servicename");
                    }
                    if (dataObj.containsKey("functionname")) {
                        info.functionname = dataObj.getString("functionname");
                    }

                    infoList.add(info);
                }
                caseRunInfo.setData(infoList);
                caseRunStage.add(caseRunInfo);
            }
        }
        return caseRunStage;
    }


    public static LinkedList<CaseRunInfo>  parseCaseRunStage(JSONObject testDataJsonObj, String caseTemplate) {


        if (caseTemplate==null){
            caseTemplate = "C++";
        }

        LinkedList<CaseRunInfo> caseRunStage = new LinkedList();

        if (testDataJsonObj.containsKey(TESTDATA_RUN_DATA_KEY)) {


            JSONArray groupJsonArray = testDataJsonObj.getJSONArray(TESTDATA_RUN_DATA_KEY);

            for (int i = 0; i < groupJsonArray.size(); i++) {

                CaseRunInfo caseRunInfo = new CaseRunInfo();

                JSONObject groupJson = groupJsonArray.getJSONObject(i);

                String groupName = groupJson.getString("group_name");
                caseRunInfo.setGroupName(groupName);

                caseRunInfo.setSelectCheckType((Map<String, Boolean>) groupJson.get("selectCheckType"));


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

                    if ("java".equals(caseTemplate)){
                        if (dataObj.containsKey("actual")) {
                            info.actual = dataObj.getString("actual");
                        }
                        if (dataObj.containsKey("servicename")) {
                            info.servicename = dataObj.getString("servicename");
                        }
                        if (dataObj.containsKey("functionname")) {
                            info.functionname = dataObj.getString("functionname");
                        }
                    }
                    try{
                        info.scenarioId = Long.valueOf(dataObj.getString("scenarioId"));
                        info.testcaseId = Long.valueOf(dataObj.getString("testcaseId"));
                    }
                    catch (Exception e){

                    }


                    try {
                        info.scenarioId = Long.valueOf(dataObj.getString("scenarioId"));
                        info.testcaseId = Long.valueOf(dataObj.getString("testcaseId"));
                    }catch(Exception e){
                        //do nothing
                    }

                    infoList.add(info);
                }
                caseRunInfo.setData(infoList);
                caseRunStage.add(caseRunInfo);
            }
        }
        return caseRunStage;
    }


    public static LinkedList<CaseRunInfo>  parseCaseRunStageJava(JSONObject testDataJsonObj) {

        LinkedList<CaseRunInfo> caseRunStage = new LinkedList();

        if (testDataJsonObj.containsKey(TESTDATA_RUN_DATA_KEY)) {


            JSONArray groupJsonArray = testDataJsonObj.getJSONArray(TESTDATA_RUN_DATA_KEY);

            for (int i = 0; i < groupJsonArray.size(); i++) {

                CaseRunInfo caseRunInfo = new CaseRunInfo();

                JSONObject groupJson = groupJsonArray.getJSONObject(i);

                String groupName = groupJson.getString("group_name");
                caseRunInfo.setGroupName(groupName);

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

                    if (dataObj.containsKey("actual")) {
                        info.actual = dataObj.getString("actual");
                    }
                    if (dataObj.containsKey("servicename")) {
                        info.servicename = dataObj.getString("servicename");
                    }
                    if (dataObj.containsKey("functionname")) {
                        info.functionname = dataObj.getString("functionname");
                    }

                    infoList.add(info);
                }
                caseRunInfo.setData(infoList);
                caseRunStage.add(caseRunInfo);
            }
        }
        return caseRunStage;
    }

    public static LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> parsePrepareDataList(JSONObject testDataJsonObj){

        LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareDataList = new LinkedList();

        if (testDataJsonObj.containsKey(TESTDATA_PREPARE_DATA_KEY)) {

            JSONArray prepareDataArray = testDataJsonObj.getJSONArray(TESTDATA_PREPARE_DATA_KEY);
            //for (JSONObject prepareDataRecordJsonObj : prepareDataArray.toJavaList(JSONObject.class)) {
            for (int i=0; i<prepareDataArray.size(); i++) {
                JSONObject prepareDataRecordJsonObj = prepareDataArray.getJSONObject(i);
                HashMap<String, LinkedList<DetailDataInfo>> prepareDataRecord = new HashMap<>();

                for (Utils.PrepareDataType type : Utils.PrepareDataType.values()) {
                    if (prepareDataRecordJsonObj.containsKey(type.name())) {
                        TestCaseData.parsePrepareData(type, prepareDataRecordJsonObj, prepareDataRecord);
                    }
                }

                prepareDataList.add(prepareDataRecord);
            }
        }
        return prepareDataList;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getCaseGroup() {
        return caseGroup;
    }

    public void setCaseGroup(String caseGroup) {
        this.caseGroup = caseGroup;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public static LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> parsePrepareDataListAddInfo(JSONObject testDataJsonObj){

        LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareDataList = new LinkedList();

        if (testDataJsonObj.containsKey(TESTDATA_PREPARE_DATA_KEY)) {

            JSONArray prepareDataArray = testDataJsonObj.getJSONArray(TESTDATA_PREPARE_DATA_KEY);
            //for (JSONObject prepareDataRecordJsonObj : prepareDataArray.toJavaList(JSONObject.class)) {
            for (int i=0; i<prepareDataArray.size(); i++) {
                JSONObject prepareDataRecordJsonObj = prepareDataArray.getJSONObject(i);
                HashMap<String, LinkedList<DetailDataInfo>> prepareDataRecord = new HashMap<>();

                for (Utils.PrepareDataType type : Utils.PrepareDataType.values()) {
                    if (prepareDataRecordJsonObj.containsKey(type.name())) {
                        TestCaseData.parsePrepareDataAddInfo(type, prepareDataRecordJsonObj, prepareDataRecord);
                    }
                }

                prepareDataList.add(prepareDataRecord);
            }
        }
        return prepareDataList;
    }


    public static LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> parseComponent(JSONObject testDataJsonObj){

        LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareDataList = new LinkedList();

        if (testDataJsonObj.containsKey(TESTDATA_RUN_AGENT_DATA_KEY)) {

            JSONArray prepareDataArray = testDataJsonObj.getJSONArray(TESTDATA_RUN_AGENT_DATA_KEY);
            for (int i=0; i<prepareDataArray.size(); i++) {
                JSONObject prepareDataRecordJsonObj = prepareDataArray.getJSONObject(i);
                HashMap<String, LinkedList<DetailDataInfo>> prepareDataRecord = new HashMap<>();

                for(String type : prepareDataRecordJsonObj.keySet()){
                    TestCaseData.parseComponentAdd(type, prepareDataRecordJsonObj, prepareDataRecord);
                }

                prepareDataList.add(prepareDataRecord);
            }
        }
        return prepareDataList;
    }

}
