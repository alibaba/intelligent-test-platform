package com.alibaba.markovdemo.BO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.alibaba.markovdemo.engine.stages.*;
import com.alibaba.markovdemo.engine.util.Toolkit;
import com.google.gson.Gson;

import java.util.*;


public class TestCaseAI extends TestCaseInput {


    static Gson gson = new Gson();

    //数据准备区快照
    LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareDataSnap = new LinkedList<>() ;
    //是否能并行
    boolean isParallel;
    //每个用例的运行时长
    String caseRunTime;
    //历史记录id
    Long caseSnapId;

    public Long getCaseSnapId() {
        return caseSnapId;
    }

    public void setCaseSnapId(Long caseSnapId) {
        this.caseSnapId = caseSnapId;
    }

    //记录冲突的ds
    Map<String,List<String>> conflictDs;

    public Map<String, List<String>> getConflictDs() {
        return conflictDs;
    }

    public void setConflictDs(Map<String, List<String>> conflictDs) {
        this.conflictDs = conflictDs;
    }


    public void addConflictDs(String ds ,String key) {

        if(this.getConflictDs()==null){
            Map<String,List<String>> conflictDs = new HashMap<>();
            List<String> rList = new ArrayList<>();
            rList.add(key);
            conflictDs.put(ds,rList);
            this.setConflictDs(conflictDs);
        }
        else if(!this.getConflictDs().containsKey(ds)){
            List<String> rList = new ArrayList<>();
            rList.add(key);
            this.getConflictDs().put(ds,rList);
        }
        else{
            this.getConflictDs().get(ds).add(key);
        }
    }

    public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> getPrepareDataSnap() {
        return prepareDataSnap;
    }

    public void setPrepareDataSnap(LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareDataSnap) {
        this.prepareDataSnap = prepareDataSnap;
    }

    public boolean isParallel() {
        return isParallel;
    }

    public void setParallel(boolean parallel) {
        isParallel = parallel;
    }

    public String getCaseRunTime() {
        return caseRunTime;
    }

    public void setCaseRunTime(String caseRunTime) {
        this.caseRunTime = caseRunTime;
    }


    /**
     * 对数据准备进行占位符替换
     * @param testCaseInput
     * @return
     */
    public static  LinkedList<HashMap<String, LinkedList<DetailDataInfo>>>  replacePrepareData(TestCaseInput testCaseInput){

        if(testCaseInput.getPrepareData() == null){
            return null;
        }

        LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareResObj = new LinkedList<>();

        String pre = String.valueOf(testCaseInput.getAppId()) + String.valueOf(testCaseInput.getScenarioId()) + String.valueOf(testCaseInput.getId());
        //占位符替换
        String prepareDataStr = Toolkit.placeholderReplace(gson.toJson(testCaseInput.getPrepareData()), pre);

            JSONArray prepareDataArray = JSONArray.parseArray(prepareDataStr);
            //for (JSONObject prepareDataRecordJsonObj : prepareDataArray.toJavaList(JSONObject.class)) {
            for(int i=0; i<prepareDataArray.size(); i++) {
                JSONObject prepareDataRecordJsonObj = prepareDataArray.getJSONObject(i);
                HashMap<String, LinkedList<DetailDataInfo>> prepareDataRecord = new HashMap<>();

                for (Utils.PrepareDataType type : Utils.PrepareDataType.values()) {
                    if (prepareDataRecordJsonObj.containsKey(type.name())) {
                        TestCaseData.parsePrepareData(type, prepareDataRecordJsonObj, prepareDataRecord);
                    }
                }
                prepareResObj.add(prepareDataRecord);
            }

        return prepareResObj;
    }


    public static LinkedList<CaseRunInfo> replaceCaseRunData(TestCaseInput testCaseInput){

        if(testCaseInput.getCaseRunStage() == null){
            return null;
        }

        LinkedList<CaseRunInfo> caseRunInfoList= new LinkedList<>();

        String pre = String.valueOf(testCaseInput.getAppId()) +  String.valueOf(testCaseInput.getScenarioId()) + String.valueOf(testCaseInput.getId());

        String caseRunInfoListStr = Toolkit.placeholderReplace(gson.toJson(testCaseInput.getCaseRunStage()), pre);

        JSONArray groupJsonArray = JSONArray.parseArray(caseRunInfoListStr);

        for (int i = 0; i < groupJsonArray.size(); i++) {

            CaseRunInfo caseRunInfo = new  CaseRunInfo();
            JSONObject groupJson = groupJsonArray.getJSONObject(i);

            String groupName = groupJson.getString("group_name");

            LinkedList<RunningDataInfo> infoList = new LinkedList<RunningDataInfo>();

             caseRunInfo.setSelectCheckType((Map<String, Boolean>) groupJson.get("selectCheckType"));

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

                if (dataObj.containsKey("actual") && dataObj.get("actual")!=null) {
                    info.actual = dataObj.getString("actual");
                }
                if (dataObj.containsKey("caseAccuracy") && dataObj.get("caseAccuracy")!=null) {
                    info.accuInfo= dataObj.getString("caseAccuracy");
                }
                if (dataObj.containsKey("diffLine") && dataObj.get("diffLine")!=null) {
                    info.diffLine= dataObj.getString("diffLine");
                }
                if (dataObj.containsKey("servicename")) {
                    info.servicename = dataObj.getString("servicename");
                }
                if (dataObj.containsKey("functionname")) {
                    info.functionname = dataObj.getString("functionname");
                }

                infoList.add(info);
            }
            caseRunInfo.setGroupName(groupName);
            caseRunInfo.setData(infoList);
            caseRunInfoList.add(caseRunInfo);
        }

        return caseRunInfoList;
    }


    /**
     * 目的:将原必要信息赋值给TestCaseAI
     * @param testCaseInput
     * @return
     */
    public static TestCaseAI packTestCaseAI(TestCaseInput testCaseInput){

        //基本数据
        TestCaseAI testCaseAI= new TestCaseAI();
        testCaseAI.setCreateBy(testCaseInput.getCreateBy());
        testCaseAI.setDescription(testCaseInput.getDescription());
        testCaseAI.setLongDescription(testCaseInput.getLongDescription());
        testCaseAI.setAppId(testCaseInput.getAppId());
        testCaseAI.setName(testCaseInput.getName());
        testCaseAI.setModifiedBy(testCaseInput.getModifiedBy());
        testCaseAI.setCaseGroup(testCaseInput.getCaseGroup());
        testCaseAI.setId(testCaseInput.getId());
        testCaseAI.setScenarioId(testCaseInput.getScenarioId());
        testCaseAI.setTag(testCaseInput.getTag());
        testCaseAI.setVersion(testCaseInput.getVersion());
        testCaseAI.setType(testCaseInput.getType());
        testCaseAI.setCaseTemplate(testCaseInput.getCaseTemplate());
        testCaseAI.setIsTrunkFlag(testCaseInput.getIsTrunkFlag());


        LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> replacePrepareData = replacePrepareData(testCaseInput);
        LinkedList<CaseRunInfo> replaceCaseRunData  = replaceCaseRunData(testCaseInput);
        //复杂数据
//        testCaseAI.setPrepareDataSnap((LinkedList<HashMap<String, LinkedList<DetailDataInfo>>>) testCaseInput.getPrepareData().clone());
//        testCaseAI.setPrepareData(new LinkedList<>());
//        testCaseAI.setCaseRunStage((LinkedList<CaseRunInfo>) testCaseInput.getCaseRunStage().clone());
        testCaseAI.setPrepareDataSnap(replacePrepareData);
        testCaseAI.setPrepareData(new LinkedList<>());
        testCaseAI.setCaseRunStage(replaceCaseRunData);
        //额外数据,如果为null或者是只有ad校验,则默认可并行
        if (isParellel(testCaseAI)){
            testCaseAI.setParallel(true);
        }
        else{
            testCaseAI.setParallel(false);
        }

        return testCaseAI;
    }

    public static boolean isParellel(TestCaseAI testCaseAI){

        boolean flag = false;
        try{
            //智能回归时,默认可并行
            if(testCaseAI.getCaseRunStage().get(0).getSelectCheckType()==null){
                return true;
            }
            Map<String, Boolean> map = testCaseAI.getCaseRunStage().get(0).getSelectCheckType();
            //如果只有一个ad验证,则也可并行
            flag = true;
            for (String checkType : map.keySet()){
                if (!"ad".equals(checkType) && map.get(checkType)==true){
                    flag = false;
                }
            }
        }
        catch (Exception e){

        }
        return flag;
    }

}



