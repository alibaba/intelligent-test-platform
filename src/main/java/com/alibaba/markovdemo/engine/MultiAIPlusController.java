package com.alibaba.markovdemo.engine;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.*;
import com.alibaba.markovdemo.engine.AI.GACaseGenerator.CovMock;
import com.alibaba.markovdemo.engine.stages.*;
import com.alibaba.markovdemo.engine.util.Toolkit;
import com.alibaba.markovdemo.entity.*;
import com.alibaba.markovdemo.service.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;


@Service
public class MultiAIPlusController {

    private static final Logger logger = LoggerFactory.getLogger(MultiAIPlusController.class);

    Gson gson = new Gson();

    String runtimeGap;
    int sucessCaseNum;
    int envNum;

    //0:普通回归 1:智能回归
    String regressionType = "1";
    boolean openSmartRegress;
    int caseNum;
    Long scenarioId;
    Long appId;
    String caseType;
    List<String> caseGroup;
    String caseBranch;
    String caseIds;
    Map<String, FailCaseBucket> failCaseBuckets;
    Map<Long, String> conflictCaseId;
    boolean regAccuracy=false;

    //dump数据统计
    Map<String, RunDs> dumpDataStaticMap;
    //分层数据统计
    Map<String,Map<String, RunDs>> envRunDataStaticMap ;
    //各阶段耗时
    Map<String, Map<String,Long>> envBucketCostMap ;
    //回归分析报告
    Map<String, EnvBasicReport> envReportMap;

    //搜集用例集中用到的数据类型,仅供统计使用
    Map<String, String> dataTypeMap;
    //pipeline设置的数据类型,仅供统计使用
    Map<String, String> dataTypePipelineMap;
    Map<String,Integer> envRunBucketMap ;
    Map<String, GotTestcaseSnaps> gotTestcaseSnapMap;
    @Autowired
    TestcaseService gotTestcaseDao;
    @Autowired
    GotReportsService gotReportsDao;
    @Autowired
    private PipelineService pipelineService;
    @Autowired
    TestcaseSnapsService gotTestcaseSnapsDao;
    @Autowired
    CaseAccuracyService caseAccuracyService;

    GotReports gotReports;
    public List<TestCaseAI> getFailCaseAIList;
    List<GotTestcaseSnaps> gotTestcaseSnapList;

    int dumpDataRecoredNum;
    int failCaseNum;
    boolean stopFlag;
    Map<String, String> envMap;
    String envName;
    int parallerNum = 20;

    private String SERIAL = "serial";
    private static final String PARAMS = "params";

    private static final String JAVA = "JAVA";
    private static final String BLINK = "blink";

    private String ALL_DS = "allDs";
    private String SINGLE_DS = "singleDs";
    private String SINGLE_RECORD = "singleRecord";

    private String DATA_PREPARE = "dataPrepare";
    private String QUERY_CHECK = "queryCheck";


    //执行分桶
    private String DUMP_ALL_BUCKET = "dumpAllBucket";
    private String DUMP_TREE_BUCKET = "dumpTreeBucket";
    private String QUERY_CHECK_BUCKET = "queryCheckBucket";
    private String RUN_FAIL_BUCKET = "runFailBucket";

    //状态文案
    private String MULTI_RUN_STAGE_NAME = "回归阶段";
    private String DUMP_STAGE_NAME = "全量数据阶段";
    private String RUN_CASE_NAME = "运行用例阶段";
    private String ANALYSE_NAME = "报告生成阶段";
    private String TROUBLE_SHOOT = "智能排查阶段";
    //运行状态
    private String RUNNING = "RUNNING";
    private String SUCCESS = "SUCCESS";
    private String FAIL = "FAIL";
    private String EXCEPTION = "EXCEPTION";
    private String WAITING = "WAITING";
    private String STOP = "STOP";
    Gson gosn = new Gson();


    /**
     * 函数功能:功能回归初始化
     * 注意:此处为了方便demo展示就直接初始化,但在实战中需要改造,否则可能在多个回归任务并行时,全局参数会有相互覆盖的情况
     * @param multiAIInfo
     */
    public void initMultiParams(MultiAIInfo multiAIInfo){
        appId = multiAIInfo.getAppId();
        scenarioId = multiAIInfo.getScenarioId();
        //此处是为mock的环境名
        envName = multiAIInfo.getTaskName();
        this.openSmartRegress = multiAIInfo.isOpenSmartRegress();
        regAccuracy = multiAIInfo.getRegAccuracy(); //需要在每个case执行完成后，获取覆盖代码行
        caseBranch = "master";
        gotTestcaseSnapMap = new HashMap<>();
        failCaseBuckets = new HashMap<>();
        envReportMap = new HashMap<>();
        conflictCaseId = new HashMap<>();
        dataTypeMap = new HashMap<>();
        dataTypePipelineMap = new HashMap<>();
        gotTestcaseSnapMap = new HashMap<>();
        getFailCaseAIList = new ArrayList<>();
        failCaseBuckets = new HashMap<>();
        gotTestcaseSnapList = new ArrayList<>();
        dumpDataStaticMap =  new HashMap<>();
        envRunDataStaticMap = new HashMap<>();
        envBucketCostMap = new HashMap<>();
        envRunBucketMap = new HashMap<>();
        sucessCaseNum = 0;
        dumpDataRecoredNum = 0;
        envNum = 0;
        failCaseNum = 0;
        stopFlag = false;
        caseNum = 0;
        //环境信息
        envMap = new HashMap<>();


    }

    /**
     * 函数功能:回归测试执行
     * 1.普通回归:caseBycase,
     * 2.智能回归:用例重新编排,高效执行
     * @param multiAIInfo
     */
    public Long runIntelligent(MultiAIInfo multiAIInfo) {

        //初始化
        initMultiParams(multiAIInfo);
        String exector = "markov";
        String runType = multiAIInfo.getRunType();
        String caseType = multiAIInfo.getCaseType();
        Map<String, List<String>> envInfo = multiAIInfo.getEnvInfo();
        List<String> caseGroup = multiAIInfo.getCaseGroup();
        String caseIds = multiAIInfo.getCaseIds();
        final GotReports gotReports = new GotReports();
        gotReports.setUser(exector);
        gotReports.setAppId(appId);
        gotReports.setScenarioId(scenarioId);
        gotReports.setRunType(runType);
        gotReports.setReportName(Toolkit.implode(",", new ArrayList<>(envInfo.keySet())));
        gotReports.setStatus(ResultStatus.RUNNING.name());
        if(regAccuracy){
            gotReports.setIsVisible(1);
        }
        //测试报告入库,注:第一次save后,gotReports对象中就会拿到id
        insertReport(gotReports);

        // 启动功能回归线程,批量执行case
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("回归测试开启..");

                    batchRunInteligentTestCasesZK(appId, scenarioId, gotReports, envInfo, caseGroup, caseBranch, caseType, caseIds);

                } catch (Exception e) {
                    System.out.println("batch run case failed:" + e.getMessage());
                    gotReports.setStatus(ResultStatus.ERROR.name());
                    gotReports.setMessage("batch run case failed:" + e.getMessage());
                    saveReport(gotReports);
                }
            }
        });
        thread.start();
        return gotReports.getId();
    }

    /**
     * 获取分组用例集
     * @param scenarioId
     * @param caseGroupList
     * @return
     */
    public List<TestCaseInput> getCaseListByGroup(Long scenarioId, List<String> caseGroupList, String branch) {


        List<TestCaseInput> testcaseList = new ArrayList<>();
        if(branch==null || branch.equals("")){
            branch = "master";
        }

        List<TestCaseInput> testcaseListDb = gotTestcaseDao.getAllCaseByScenarioId(scenarioId);
        for (TestCaseInput oneCase : testcaseListDb) {

            String caseGroupValue = oneCase.getCaseGroup();
            if (caseGroupValue == null) {
                caseGroupValue = "默认分组";
            }
            if (caseGroupList.contains(caseGroupValue)) {
                testcaseList.add(oneCase);
            }
        }
        return testcaseList;
    }


    /**
     * 函数功能:获取回归用例集
     * @param envInfo
     * @return
     */
    public List<MultiRunCaseInfo> getMultiRunCaseInfoList(Map<String,List<String>> envInfo) {

        //用例集合
        List<TestCaseInput> testcaseList = new ArrayList<>();
        List<MultiRunCaseInfo> MultiRunCaseInfoList = new ArrayList<>();

        //获取用例集. 0:cases,1:scenarios
        if (scenarioId != null) {
            //多场景用例
            if (caseType != null && caseType.equals("1")) {
                Map<String,List<String>> scenidCaseGroup= new HashMap<>();
                for (String scenId : caseGroup) {
                    String[] multiStr = scenId.split("__");
                    String realScenId = multiStr[0];
                    String caseGroup = multiStr[2];
                    if (!scenidCaseGroup.containsKey(realScenId)){
                        scenidCaseGroup.put(realScenId,new ArrayList<>());
                    }
                    scenidCaseGroup.get(realScenId).add(caseGroup);
                }

                for (String realScenId : scenidCaseGroup.keySet()){
                    MultiRunCaseInfo MultiRunCaseInfo = new MultiRunCaseInfo();
                    MultiRunCaseInfo.setAppId(appId);
                    MultiRunCaseInfo.setScenarioId(scenarioId);
                    MultiRunCaseInfo.setRealAppId(appId);
                    MultiRunCaseInfo.setRealScenarioId(Long.valueOf(realScenId));
                    List<TestCaseInput> testCaseInputList =  getCaseListByGroup(Long.valueOf(realScenId), scenidCaseGroup.get(realScenId), caseBranch);
                    MultiRunCaseInfo.setTestcaseList(testCaseInputList);
                    caseNum += testCaseInputList.size();
                    MultiRunCaseInfoList.add(MultiRunCaseInfo);
                }

            }
            //单场景下所有用例
            else if (caseType.equals("0")) {
                MultiRunCaseInfo MultiRunCaseInfo = new MultiRunCaseInfo();
                MultiRunCaseInfo.setAppId(appId);
                MultiRunCaseInfo.setScenarioId(scenarioId);
                MultiRunCaseInfo.setRealAppId(appId);
                MultiRunCaseInfo.setRealScenarioId(scenarioId);
                if (caseGroup == null || caseGroup.size() == 0) {
                    if (caseIds != null ) {
                        //前端提交批量运行
                        String[] cases = caseIds.split(",");
                        for (String caseid : cases) {
                            TestCaseInput caseObj = gotTestcaseDao.getTestCaseById(Long.parseLong(caseid));
                            testcaseList.add(caseObj);
                        }
                        MultiRunCaseInfo.setTestcaseList(testcaseList);
                    } else {
                        if(caseBranch == null){
                            caseBranch = "master";
                        }
                        testcaseList = gotTestcaseDao.getAllCaseByScenarioId(scenarioId);

                        MultiRunCaseInfo.setTestcaseList(testcaseList);
                    }
                }
                //选中的特定分组用例
                else {
                    testcaseList = getCaseListByGroup(scenarioId, caseGroup, caseBranch);
                    MultiRunCaseInfo.setTestcaseList(testcaseList);
                }
                caseNum = testcaseList.size();
                MultiRunCaseInfoList.add(MultiRunCaseInfo);
            }

        } else {
            gotReports.setStatus(ResultStatus.FAILURE.name());
            saveReport(gotReports);

        }

        return MultiRunCaseInfoList;
    }


    /**
     * 函数功能:载入测试环境配置
     * 说明:用户可根据自身测试系统的需求去完善
     * @param envInfo
     * @param pipelineObj
     */
    public void loadEnvMapInfo(Map<String, List<String>> envInfo, JSONObject pipelineObj) {

        //todo: to be filled
        System.out.print("载入回归环境完成\n");
    }


    /**
     * 函数功能:
     * 判断是走哪种回归模式.0:caseBycase,1:智能回归
     * @param pipelineObj
     */
    public void getMultiRunType(JSONObject pipelineObj) {
        if (openSmartRegress){
            regressionType= "1";
        }
        else{
            regressionType= "0";
        }
        try{

            List<JSONObject> arr = (List<JSONObject>) pipelineObj.get("run-stage");
            JSONObject paramsObj = (JSONObject) arr.get(1).get("params");
            if (paramsObj.containsKey("regressionType")){
                regressionType = (String) paramsObj.get("regressionType");
            }
        }
        catch (Exception e){

        }


    }


    /**
     * 函数功能:中断回归测试流程
     * @param gotReports
     * @return
     */
    boolean isTaskStop(GotReports gotReports){

        //todo:待添加..
        return false;
    }

    /**
     * 功能: 在用例中保留一条测试数据
     * @param testCaseAI
     * @param groupNum
     * @param dataType
     * @param param1
     * @param param2
     * @param mode
     */
    public void addPrepareRecord(TestCaseAI testCaseAI, int groupNum, String dataType, Object param1, Object param2, String mode) {


        LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> prepareData = testCaseAI.getPrepareData();
        //添加分组数据
        if (prepareData.size() != groupNum + 1) {
            prepareData.add(new HashMap<>());
        }
        //取的该组疏忽
        HashMap<String, LinkedList<DetailDataInfo>> eachPrepareData = new HashMap<>();
        try {
            eachPrepareData = prepareData.get(prepareData.size()-1);
        }catch(Exception e){
            e.printStackTrace();
        }

        //添加type维度的所有ds
        if (mode.equals(ALL_DS)) {

            LinkedList<DetailDataInfo> allDsList = (LinkedList<DetailDataInfo>) param1;
            eachPrepareData.put(dataType, allDsList);
            for (DetailDataInfo detailDataInfo : allDsList) {
                dumpDataRecoredNum += detailDataInfo.getData().size();
            }
        }
        //添加ds
        else {
            //添加ds维度的数据
            if (!eachPrepareData.containsKey(dataType)) {
                eachPrepareData.put(dataType, new LinkedList<>());
            }
            LinkedList<DetailDataInfo> eachDsPrepareData = eachPrepareData.get(dataType);

            if (mode.equals(SINGLE_DS)) {

                DetailDataInfo detailDataInfo = (DetailDataInfo) param2;
                eachDsPrepareData.add(detailDataInfo);
                dumpDataRecoredNum += detailDataInfo.getData().size();

            }
            //添加ds下的每条记录维度
            else if (mode.equals(SINGLE_RECORD)) {

                boolean addFlag = false;
                String dsName = (String) param1;
                RecordInfo recordInfo = (RecordInfo) param2;
                testCaseAI.addConflictDs(dsName, recordInfo.getKey());

                //如果ds为空
                if (eachDsPrepareData.size() == 0 || eachDsPrepareData == null) {
                    DetailDataInfo detailDataInfo = new DetailDataInfo();
                    detailDataInfo.setDsName(dsName);
                    LinkedList<RecordInfo> recordList = new LinkedList<>();
                    recordList.add(recordInfo);
                    detailDataInfo.setData(recordList);
                    eachDsPrepareData.add(detailDataInfo);
                    dumpDataRecoredNum += 1;
                }
                //如果ds已存在
                else {
                    for (DetailDataInfo detailDataInfo : eachDsPrepareData) {
                        //找到ds
                        if (detailDataInfo.getDsName().equals(dsName)) {
                            if (detailDataInfo.getData().size() == 0 || detailDataInfo.getData() == null) {
                                detailDataInfo.setData(new LinkedList<>());
                            }
                            detailDataInfo.getData().add(recordInfo);
                            dumpDataRecoredNum += 1;
                            addFlag = true;
                            break;
                        }
                    }
                    //如果找不到ds,则新建
                    if (addFlag == false) {
                        DetailDataInfo detailDataInfo = new DetailDataInfo();
                        detailDataInfo.setDsName(dsName);
                        LinkedList<RecordInfo> recordList = new LinkedList<>();
                        recordList.add(recordInfo);
                        detailDataInfo.setData(recordList);
                        eachDsPrepareData.add(detailDataInfo);
                        dumpDataRecoredNum += 1;
                    }
                }
            }
        }

    }


    /**
     * 函数功能:
     * 1.整合数据准备,区分为全量数据 + 串行用例数据
     * 2.识别全部用例中的冲突数据,并进行打标
     * 3.执行全量数据准备
     * @param testcaseAIList
     * @param
     * @return
     */
    public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> getDumpData(List<TestCaseAI> testcaseAIList) {
        //注:有冲突的数据,即key一致时,value不一致为冲突,需要来标注用例为冲突,即并行.
        LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> allDumpData = new LinkedList<>();
        HashMap<String, LinkedList<DetailDataInfo>> dumpData = new HashMap<>();
        //此处我们默认TAIR可走全量,k-v形式的,当然用户可以自行决定全量数据准备的策略
        dataTypePipelineMap.put(Utils.PrepareDataType.Tair.name(), "use");

        //遍历用例集
        for (TestCaseAI testCaseAI : testcaseAIList) {
            //算法是扫描每个用例的每个ds,如果在里面的就放入,如果有冲突的就不放入,有没有用例是带数据,且能并行?那我们简单点,所有带数据的,都是要串行的.因为意味着可能有数据冲突.除非是不带,如果是全量数据,且有数据的,说明是冲突.

            if (testCaseAI.getPrepareDataSnap().size() > 0) {
                //遍历每组数据源集合
                for (int groupNum = 0; groupNum < testCaseAI.getPrepareDataSnap().size(); groupNum++) {

                    HashMap<String, LinkedList<DetailDataInfo>> groupData = testCaseAI.getPrepareDataSnap().get(groupNum);
                    //遍历每个数据源
                    for (String dataType : groupData.keySet()) {
                        dataTypeMap.put(dataType, "use");

                        LinkedList<DetailDataInfo> allDsList = groupData.get(dataType);

                        //如果非全量分组
                        if (!dataTypePipelineMap.containsKey(dataType)) {
                            //如果为非全量数据类型,则用例保留数据
                            addPrepareRecord(testCaseAI, groupNum, dataType, allDsList, null, ALL_DS);
                            continue;
                        }else {
                            if (allDsList.size() > 0) {
                                //遍历每个数据源的数据list
                                for (DetailDataInfo oneDsList : allDsList) {
                                    //遍历每条数据记录
                                    for (int recordNum = 0; recordNum < oneDsList.getData().size(); recordNum++) {
                                        RecordInfo recordInfo = oneDsList.getData().get(recordNum);
                                        //将数据加入全量,并删去原来的record,如果全量有冲突,则不删除
                                        boolean conflictFlag = addDumpData(recordInfo, dumpData, dataType, oneDsList.getDsName());
                                        //如果数据有冲突,则用例保留数据,不再记录全量桶
                                        if (conflictFlag == true) {
                                            addPrepareRecord(testCaseAI, groupNum, dataType, oneDsList.getDsName(), recordInfo, SINGLE_RECORD);
                                            conflictCaseId.put(testCaseAI.getId(), "use");
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        allDumpData.add(dumpData);
        //获取全量桶相关统计数据
        calDumpDataStatic(allDumpData);

        return allDumpData;
    }

    /**
     * 统计报告:搜集数据源名字
     * @param ds
     * @return
     */
    public String getTypeDs(String ds){

        String[] dasource = ds.split(":");
        if (dasource.length==3){
            String dsName = dasource[0];
            String groupName = dasource[1];
            return dsName + ":" +groupName;
        }
        else{
            return dasource[0];
        }

    }
    /**
     * 函数功能:对全量桶数据进行统计
     * 包含所有记录数目,冗余数据数目
     */
    public void calDumpDataStatic(LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> allDumpData){

        for (HashMap<String, LinkedList<DetailDataInfo>> typeInfo: allDumpData){

            for (String type : typeInfo.keySet()){
                LinkedList<DetailDataInfo> list = typeInfo.get(type);
                for (DetailDataInfo detailDataInfo : list){
                    String ds =detailDataInfo.getDsName();
                    String typeDs = type + ":" + getTypeDs(ds);
                    if (!dumpDataStaticMap.containsKey(typeDs)){
                        dumpDataStaticMap.put(typeDs,new RunDs());
                    }
                    dumpDataStaticMap.get(typeDs).setAllNum(dumpDataStaticMap.get(typeDs).getAllNum()+detailDataInfo.getData().size());
                    dumpDataStaticMap.get(typeDs).setRepeatNum(dumpDataStaticMap.get(typeDs).getRepeatNum() +detailDataInfo.getData().size());
                }
            }
        }
    }
    /**
     * 函数功能:将一条测试数据加入全量数据准备桶中
     * 如果数据存在全量桶冲突,则不在加入全量桶
     * @param recordInfo
     * @param dumpData
     * @param dsName
     * @return
     */
    boolean addDumpData(RecordInfo recordInfo, HashMap<String, LinkedList<DetailDataInfo>> dumpData, String dataType, String dsName) {


        boolean addFlag = false;
        boolean conflictFlag = false;

        //新建dataType
        if (!dumpData.containsKey(dataType)) {
            dumpData.put(dataType, new LinkedList<>());
        }

        LinkedList<DetailDataInfo> detailDataInfoList = dumpData.get(dataType);

        //如果ds为空
        if (detailDataInfoList.size() == 0 || detailDataInfoList == null) {
            DetailDataInfo detailDataInfo = new DetailDataInfo();
            detailDataInfo.setDsName(dsName);
            LinkedList<RecordInfo> recordList = new LinkedList<>();
            recordList.add(recordInfo);
            detailDataInfo.setData(recordList);
            detailDataInfoList.add(detailDataInfo);
        }
        //ds不为空
        else {
            //如果ds已存在
            for (DetailDataInfo detailDataInfo : detailDataInfoList) {
                //如果找到ds
                if (detailDataInfo.getDsName().equals(dsName)) {
                    if (detailDataInfo.getData().size() == 0 || detailDataInfo.getData() == null) {
                        detailDataInfo.setData(new LinkedList<>());
                    }
                    //遍历看是否会有冲突
                    for (RecordInfo eachRecord : detailDataInfo.getData()) {
                        //冲突
                        if (eachRecord.getKey().equals(recordInfo.getKey()) && !eachRecord.getValue().equals(recordInfo.getValue())) {
                            conflictFlag = true;
                            return conflictFlag;
                        }
                    }
                    //没冲突合并
                    detailDataInfo.getData().add(recordInfo);
                    addFlag = true;
                    break;
                }
            }
            //如果找不到ds,则新建
            if (addFlag == false) {
                DetailDataInfo detailDataInfo = new DetailDataInfo();
                detailDataInfo.setDsName(dsName);
                LinkedList<RecordInfo> recordList = new LinkedList<>();
                recordList.add(recordInfo);
                detailDataInfo.setData(recordList);
                detailDataInfoList.add(detailDataInfo);
            }
        }

        return conflictFlag;
    }
    /**
     * 函数功能:批量执行测试用例集主流程
     * @param appId
     * @param scenarioId
     * @param gotReports
     * @param envInfo
     * @param caseGroup
     * @param caseBranch
     * @param caseType
     * @param caseIds
     * @return
     * @throws Exception
     */
    public boolean batchRunInteligentTestCasesZK(final Long appId, final Long scenarioId, GotReports gotReports, Map<String, List<String>> envInfo, List<String> caseGroup,String caseBranch, String caseType, String caseIds) throws Exception {

        List<TestCaseAI> testcaseAllList = new ArrayList<>();
        try {
            //中断点
            if (isTaskStop(gotReports)) {
                return true;
            }
            //初始化
            this.gotReports = gotReports;
            this.scenarioId = scenarioId;
            this.appId = appId;
            this.caseGroup = caseGroup;
            this.caseBranch = caseBranch;
            this.caseType = caseType;
            this.caseIds = caseIds;
            Date start = new Date();
            gotReports.setStatus(ResultStatus.RUNNING.name());
            gotReports.setReportName(Toolkit.implode(",", new ArrayList<>(envInfo.keySet())));
            saveReport(gotReports);
            //环境信息
            envNum = envInfo.size();
            //todo:此处mock测试环境
            envNum =1;
            JSONObject pipelineObj = null;
            //获取回归用例集合
            List<MultiRunCaseInfo> MultiRunCaseInfoList = getMultiRunCaseInfoList( envInfo);
            gotReports.setCaseNum(caseNum);
            saveReport(gotReports);

            if (caseNum == 0) {
                System.out.print("回归用例为0,回归停止");
                gotReports.setStatus(ResultStatus.SUCCESS.name());
                gotReports.setMessage("回归用例集数目为零,回归完成");
                saveReport(gotReports);
                return true;
            }

            //多场景回归阶段:每个场景进行依次回归
            for (MultiRunCaseInfo multiRunCaseInfo : MultiRunCaseInfoList) {

                //中断点
                if (isTaskStop(gotReports)) {
                    return true;
                }

                System.out.print("批量执行场景用例,场景id:" + multiRunCaseInfo.getRealScenarioId() + ",用例数:" + multiRunCaseInfo.getTestcaseList().size());

                //获取单场景用例集合
                List<TestCaseInput> testcaseList = multiRunCaseInfo.getTestcaseList();
                if (testcaseList.size() == 0) {
                    continue;
                }
                //pipeline快照入库
                GotPipeline gotPipeline = pipelineService.getPipeline(multiRunCaseInfo.getRealScenarioId());
                pipelineObj = JSONObject.parseObject(gotPipeline.getPipeline());
                multiRunCaseInfo.setPipelineObj(pipelineObj);

                //载入环境配置
                loadEnvMapInfo(envInfo, pipelineObj);
                //fork出一份真正执行的用例列表,该用例集带有智能化的扩展信息,在智能回归模式可用到,但在普通回归使用较少.
                List<TestCaseAI> testcaseAIList = forkTestcaseAIList(testcaseList);
                testcaseAllList.addAll(testcaseAIList);
                //确定回归模式. regressionType=1:智能回归/ regressionType=1:普通回归
                getMultiRunType(pipelineObj);

                //回归模式:智能回归
                if (!regressionType.equals("0")) {

                    System.out.print("全量数据抽取中..\n");
                    //全量数据抽取,可一次性执行完成的测试数据才可放入全量数据准备桶中
                    LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> dumpData = getDumpData(testcaseAIList);
                    Map<String, List<TestCaseAI>> testcaseGroup = new HashMap<>();
                    testcaseGroup.put(SERIAL, testcaseAIList);

                    //多个测试环境时,并行执行
                    int i = 0;
                    MultiEnvTreeCaseThread multiEnvTreeCaseThread;
                    CountDownLatch countDownLatch = new CountDownLatch(envNum);
                    List<MultiEnvTreeCaseThread> threadsList = new ArrayList<>();
                    //初始化每套测试环境统计数据
                    if(!envRunBucketMap.containsKey(envName)){
                        envRunBucketMap.put(envName, 0);
                    }
                    if(!envRunDataStaticMap.containsKey(envName)){
                        envRunDataStaticMap.put(envName, new HashMap<>());
                    }
                    if(!envBucketCostMap.containsKey(envName)){
                        envBucketCostMap.put(envName, new HashMap<>());
                    }

                    //将测试数据集均匀分配给多个测试环境
                    Map<String, List<TestCaseAI>> testcaseEnvGroup = getTestcaseEnvGroup(testcaseGroup, envNum, i);
                    System.out.print("启动用例回归线程" + i);
                    multiEnvTreeCaseThread = new MultiEnvTreeCaseThread(testcaseEnvGroup.get(SERIAL), pipelineObj, parallerNum, gotReports, countDownLatch, envName, dumpData,multiRunCaseInfo.getRealScenarioId());
                    multiEnvTreeCaseThread.setDaemon(true);
                    multiEnvTreeCaseThread.start();
                    threadsList.add(multiEnvTreeCaseThread);
                    Thread.currentThread().sleep(3000);
                    i++;

                    //主进程阻塞,如果部署线程执行结束则往下
                    countDownLatch.await();
                    System.out.print("=========执行完成,开始记录失败用例========\n");

                    //搜集所有测试环境的失败用例,放入失败重试桶中
                    for (MultiEnvTreeCaseThread thread : threadsList) {
                        gotTestcaseSnapMap.putAll(thread.getTestcaseSnapMap());
                        failCaseBuckets.putAll(thread.getFailCaseBuckets());
                    }
                }
                //回归模式:caseBycase的普通回归
                else{
                    Map<String, List<TestCaseAI>> testcaseGroup = new HashMap<>();
                    for(TestCaseAI testCaseAI :testcaseAIList){
                        testCaseAI.setPrepareData(testCaseAI.getPrepareDataSnap());
                    }
                    //全部用例都设置为串行分组
                    testcaseGroup.put(SERIAL, testcaseAIList);
                    int i = 0;
                    //多环境则启动多个线程并行执行
                    MultiEnvSimpleCaseThread multiEnvSimpleCaseThread;
                    CountDownLatch countDownLatch = new CountDownLatch(envNum);
                    List<MultiEnvSimpleCaseThread> threadsList = new ArrayList<>();
                    //todo:此处demo暂时mock一个测试环境
                    Map<String, String> envMap = new HashMap<>();
                    envMap.put("env1","envName1");
                    envNum = envMap.size();
                    //多环境并发
                    for (String envName : envMap.keySet()) {
                        if(!envRunBucketMap.containsKey(envName)){
                            envRunBucketMap.put(envName, 0);
                        }
                        //将用例集分发到多个环境上
                        Map<String, List<TestCaseAI>> testcaseEnvGroup = getTestcaseEnvGroup(testcaseGroup, envNum, i);
                        System.out.print("启动用例回归线程" + i);
                        multiEnvSimpleCaseThread = new MultiEnvSimpleCaseThread(testcaseEnvGroup.get(SERIAL), pipelineObj, gotReports, countDownLatch, envName);
                        multiEnvSimpleCaseThread.setDaemon(true);
                        multiEnvSimpleCaseThread.start();
                        threadsList.add(multiEnvSimpleCaseThread);
                        Thread.currentThread().sleep(3000);
                        i++;
                    }
                    //主进程阻塞
                    countDownLatch.await();
                }
            }

            //中断点
            if (isTaskStop(gotReports)) {
                return true;
            }
            //失败重试桶
            reRunFailCaseBuckets(gotReports);


            //测试报告统计
            int failureCount = 0;
            for (GotTestcaseSnaps testRespone : gotTestcaseSnapMap.values()) {
                if (!testRespone.getStatus().equals(ResultStatus.SUCCESS.name())) {
                    failureCount++;
                }
            }

            if (failureCount == 0) {
                gotReports.setStatus(ResultStatus.SUCCESS.name());
            } else {
                gotReports.setStatus(ResultStatus.FAILURE.name());
            }
            Date end = new Date();
            runtimeGap = String.valueOf(end.getTime() - start.getTime());

            System.out.print("开始搜集回归数据...\n");
            //测试报告详细分析
            gotReports.setAnalysis(setAnalysisReportPlus());

            saveReport(gotReports);
            System.out.print("测试报告产出完成");


        } catch (Exception e) {
            System.out.print("回归异常:" + Toolkit.getErrorStackTrace(e));
            gotReports.setStatus(ResultStatus.FAILURE.name());
            gotReports.setMessage(e.getMessage());
            saveReport(gotReports);
        }

        return true;
    }

    /**
     * 函数功能:智能回归测试报告详情分析
     * @return
     */
    public String setAnalysisReportPlus(){

        //1.ds维度,总调用,实际调用,冗余度,平均耗时,节省耗时,
        //2.执行维度,总串行数,实际串行数,总并行数,节省耗时
        //3.总体信息:总用例,智能回归:打开,数据冗余度,执行冗余度,整体耗时,失败用例数,成功用例数,重跑用例数,重跑耗时,重跑成功,重跑失败,重跑通过率.

        AIReportPlusInfo aiReportInfo = new AIReportPlusInfo();
        //获取整体报告统计数据
        BasicReport allReport  = statisBasicReport(gotTestcaseSnapMap);
        System.out.print("[整体报告统计数据]搜集完成!\n");
        //获取分环境报告数据
        List<EnvBasicReport> envReportList = statisEnvReportList(gotTestcaseSnapMap);
        System.out.print("[分环境报告数据]搜集完成!\n");
        //获取ds维度的图标信息
        Map<String,ChartBasicInfo> treeDsChart = statisTreeChart(envRunDataStaticMap);
        System.out.print("[分层数据统计图标]搜集完成!\n");
        //获取全量ds维度的图标信息
        Map<String,ChartBasicInfo> dumpDsChart = statisDumpAllChart(dumpDataStaticMap);
        System.out.print("[全量数据统计图标]搜集完成!\n");

        aiReportInfo.setBasicReport(allReport);
        aiReportInfo.setEnvReportList(envReportList);
        aiReportInfo.setTreeDsChart(treeDsChart);
        aiReportInfo.setDumpDsChart(dumpDsChart);

        System.out.print("统计数据:\n"+gson.toJson(aiReportInfo));

        return gson.toJson(aiReportInfo);
    }


    /**
     * 函数功能:获取分环境报告数据
     * @param gotTestcaseSnapMap
     * @return
     */
    public List<EnvBasicReport>  statisEnvReportList(Map<String, GotTestcaseSnaps> gotTestcaseSnapMap){
        List<EnvBasicReport> envReportList = new ArrayList<>();
        try {
            int sucessCaseNum;
            int failCaseNum;
            int caseNum = 0;

            //计算all
            for (GotTestcaseSnaps gotTestcaseSnaps : gotTestcaseSnapMap.values()) {
                envReportMap.get(gotTestcaseSnaps.getEnvName()).setCaseNum(++caseNum);
                if (gotTestcaseSnaps.getStatus().equals(ResultStatus.SUCCESS.name())) {
                    sucessCaseNum = envReportMap.get(gotTestcaseSnaps.getEnvName()).getSucessCaseNum();
                    envReportMap.get(gotTestcaseSnaps.getEnvName()).setSucessCaseNum(++sucessCaseNum);
                } else {
                    failCaseNum = envReportMap.get(gotTestcaseSnaps.getEnvName()).getFailCaseNum();
                    envReportMap.get(gotTestcaseSnaps.getEnvName()).setFailCaseNum(++failCaseNum);
                }
            }
            for (String envName : envReportMap.keySet()) {
                EnvBasicReport envReport = envReportMap.get(envName);
                envReport.setEnvName(envName);
                envReport = calMoreEnvStatic(envReport);
                envReport.setEnvName(envName);
                envReportList.add(envReport);
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return envReportList;

    }

    /**
     * 补充各个环境阶段数据
     * @param envReport
     * @return
     */
    public EnvBasicReport calMoreEnvStatic(EnvBasicReport envReport){
        String envName = envReport.getEnvName();

        //智能回归统计
        if (regressionType.equals("1")){
            //补充dump全量阶段
            envReport = adDumpAllDataToEnvBasicReport(envReport,dumpDataStaticMap,envBucketCostMap.get(envName));
            //补充dump分层阶段
            envReport = addTreeDataToEnvBasicReport(envReport,envRunDataStaticMap.get(envName),envBucketCostMap.get(envName));
            //补充执行信息
            envReport = addRunToEnvBasicReport(envReport,envRunBucketMap.get(envName),envBucketCostMap.get(envName));
        }
        else{
            envReport = addRunToEnvBasicReport(envReport,envRunBucketMap.get(envName),envBucketCostMap.get(envName));
        }

        return envReport;
    }

    /**
     * 功能:补充快速执行统计信息
     * /快速用例运行桶(总串行数/实际串行数/总并行数/节省时间sec/提速效率/总时间)
     * @param envReport
     * @return
     */
    public EnvBasicReport addRunToEnvBasicReport(EnvBasicReport envReport, Integer runSerialActualNum, Map<String,Long> bucketCostMap){

        //串行用例数
        Integer runSerialNum = envReport.getCaseNum();
        //实际串行用例数:runSerialActualNum
        int poolNum = runSerialNum/runSerialActualNum;

        if(poolNum>10){
            runSerialActualNum = runSerialNum/10;
        }
        //并行用例数
        Integer runParallelNum = runSerialNum - runSerialActualNum;
        //总时间
        Integer cost = Integer.valueOf(String.valueOf(bucketCostMap.get(QUERY_CHECK_BUCKET)));
        //提速效率
        String speedUp=changePecentFormat(Long.valueOf(runSerialNum-runSerialActualNum),Long.valueOf(runSerialActualNum));
        //节省时间
        Integer saveCost = cost/runSerialActualNum * runParallelNum;

        envReport.setRunSerialNum( runSerialNum);
        envReport.setRunSerialActualNum(runSerialActualNum);
        envReport.setRunParallelNum(runParallelNum);
        envReport.setRunSaveCost(Toolkit.changeCostFormat(Long.valueOf(saveCost)));
        envReport.setRunSpeedUpEfficiency(speedUp);
        envReport.setRunTreeTime(Toolkit.changeCostFormat(Long.valueOf(cost)));

        return envReport;
    }


    /**
     * 函数功能:分数格式转换
     * @param a
     * @param b
     * @return
     */
    public String changePecentFormat(Long a,Long b){


        if (b==0|| a==0){
            return "0";
        }

        float bb = (float)a/(float)b;
        DecimalFormat decimalFormat=new DecimalFormat("0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(bb*100) + "%";
    }


    /**
     * 函数功能:补充分层数据统计信息
     * 分层数据准备桶(总条数/实际执行条数/冗余度/节省时间sec/提速效率/总时间)
     * @param envReport
     * @return
     */
    public EnvBasicReport addTreeDataToEnvBasicReport(EnvBasicReport envReport, Map<String, RunDs> runDataStaticMap, Map<String,Long> bucketCostMap){

        //用例依赖总数据量
        Integer allNum=0;
        //重复次数
        Integer repeatNum=0;
        //执行数据条数
        Integer actrualNum=0;
        //总耗时
        Integer cost = Integer.valueOf(String.valueOf(bucketCostMap.get(DUMP_TREE_BUCKET)));
        //预估普通回归耗时
        Integer predictCost=0;


        for (String ds :  runDataStaticMap.keySet()){
            RunDs runDsInfo = runDataStaticMap.get(ds);
            actrualNum += runDsInfo.getRecordNum();
            allNum += runDsInfo.getAllNum();
            predictCost += runDsInfo.getCost();
        }
        //重复条
        repeatNum = allNum - actrualNum;
        Float redundenPecent =(float)repeatNum/allNum;
        //冗余度
        String redundency=changePecentFormat(Long.valueOf(repeatNum),Long.valueOf(allNum));//format 返回的是字符串
        //节省时间
        Integer saveCost = (int)(cost*redundenPecent/(1-redundenPecent));
        //提速效率计算
        String speedUp=changePecentFormat(Long.valueOf(saveCost),Long.valueOf(cost));
        envReport.setDumpTreeRecordNum(allNum);
        envReport.setDumpTreeActualRecordNum(actrualNum);
        envReport.setDumpTreeSaveCost(Toolkit.changeCostFormat(Long.valueOf(saveCost)));
        envReport.setDumpTreeSpeedUpEfficiency(speedUp);
        envReport.setDumpTreeTime(Toolkit.changeCostFormat(Long.valueOf(cost)));
        envReport.setRedundency(redundency);

        return envReport;
    }


    /**
     * 函数功能:补充全量数据
     * @param envReport
     * @param runDataStaticMap
     * @param bucketCostMap
     * @return
     */
    public EnvBasicReport adDumpAllDataToEnvBasicReport(EnvBasicReport envReport, Map<String, RunDs> runDataStaticMap, Map<String,Long> bucketCostMap){

        //用例依赖总数据量
        Integer allNum=0;
        //执行数据条数
        Integer actrualNum=0;
        //总耗时
        Integer cost = Integer.valueOf(String.valueOf(bucketCostMap.get(DUMP_ALL_BUCKET)));


        for (String ds :  runDataStaticMap.keySet()){
            RunDs runDsInfo = runDataStaticMap.get(ds);
            actrualNum += runDsInfo.getRecordNum();
            allNum += runDsInfo.getAllNum();
        }

        envReport.setDumpAllrecordNum(actrualNum);
        envReport.setDumpAllTime(Toolkit.changeCostFormat(Long.valueOf(cost)));

        return envReport;
    }
    /**
     * 函数功能:获取分环境报告数据
     * @param
     * @return
     */
    public Map<String,ChartBasicInfo>  statisTreeChart(Map<String,Map<String, RunDs>> envRunDataStaticMap){


        Map<String,ChartBasicInfo> chartBasicInfo = new HashMap<>();
        for (String envName : envRunDataStaticMap.keySet()){
            for(String ds : envRunDataStaticMap.get(envName).keySet()){
                if (!chartBasicInfo.containsKey(ds)){
                    chartBasicInfo.put(ds, new ChartBasicInfo());
                }
                RunDs runDsInfo = envRunDataStaticMap.get(envName).get(ds);
                //智能回归
                if (openSmartRegress){
                    chartBasicInfo.get(ds).setAllNum(chartBasicInfo.get(ds).getAllNum() + runDsInfo.getAllNum());
                }
                else {
                    chartBasicInfo.get(ds).setAllNum(chartBasicInfo.get(ds).getActualNum() + runDsInfo.getRecordNum());
                }
                chartBasicInfo.get(ds).setActualNum(chartBasicInfo.get(ds).getActualNum() + runDsInfo.getRecordNum());

            }
        }
        return chartBasicInfo;

    }

    /**
     * 函数功能:获取分环境报告数据
     * @param
     * @return
     */
    public Map<String,ChartBasicInfo>  statisDumpAllChart(Map<String, RunDs> dumpDataStaticMap){

        Map<String,ChartBasicInfo> chartBasicInfo = new HashMap<>();
        for(String ds :dumpDataStaticMap.keySet()){
            if (!chartBasicInfo.containsKey(ds)){
                chartBasicInfo.put(ds, new ChartBasicInfo());
            }
            RunDs runDsInfo = dumpDataStaticMap.get(ds);
            chartBasicInfo.get(ds).setAllNum(chartBasicInfo.get(ds).getAllNum() + runDsInfo.getAllNum());
            chartBasicInfo.get(ds).setActualNum(chartBasicInfo.get(ds).getActualNum() + runDsInfo.getRecordNum());

        }

        return chartBasicInfo;

    }

    /**
     * 函数功能:获取整体报告统计数据
     * @param gotTestcaseSnapMap
     * @return
     */
    public BasicReport statisBasicReport(Map<String, GotTestcaseSnaps> gotTestcaseSnapMap){

        BasicReport allReport = new BasicReport();
        int caseNum = 0;
        //计算all
        for (GotTestcaseSnaps gotTestcaseSnaps : gotTestcaseSnapMap.values()){
            //计算总用例数
            caseNum++ ;
            //计算成功失败用例总数
            if(gotTestcaseSnaps.getStatus().equals(ResultStatus.SUCCESS.name())){
                sucessCaseNum++;
            }
            else{
                failCaseNum++;
            }
        }
        allReport.setCaseNum(caseNum);
        allReport.setFailCaseNum(failCaseNum);
        allReport.setSucessCaseNum(sucessCaseNum);
        allReport.setEnvNum(envNum);
        allReport.setRunTime(Toolkit.changeCostFormat(Long.valueOf(runtimeGap)));
        if(openSmartRegress){
            allReport.setRegressionType("智能回归");
        }
        else{
            allReport.setRegressionType("普通回归");
        }

        return allReport;
    }


    /**
     * 类功能:智能回归
     * 主要:对用例进行树分组,多个用例聚合并行执行,提升速率
     */
    class MultiEnvTreeCaseThread extends Thread {

        public JSONObject pipelineObj;
        public int parallelNum;
        public GotReports gotReports;
        public List<TestRespone> testResponeList;
        public CountDownLatch countDownLatch;
        public String envName;
        public List<TestCaseAI> testcaseList;
        public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> dumpData;
        public Queue<QueueNode> runQueue;
        public Map<String, String> resetDs;
        public Map<String, JSONObject> resetModuleInfo;
        public Map<String, GotTestcaseSnaps> gotTestcaseSnapMap;
        public List<TestCaseAI> getFailCaseAIList;
        public Map<String, FailCaseBucket> getFailCaseBuckets;
        public FailCaseBucket failCaseBucket;
        public Long scenarioId;


        public MultiEnvTreeCaseThread(List<TestCaseAI> testcaseList, JSONObject pipelineObj, int parallelNum, GotReports gotReports, CountDownLatch countDownLatch, String envName, LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> dumpData,Long scenarioId) {
            this.parallelNum = parallelNum;
            this.gotReports = gotReports;
            this.pipelineObj = pipelineObj;
            this.countDownLatch = countDownLatch;
            this.envName = envName;
            this.testcaseList = testcaseList;
            this.runQueue = new LinkedList<>();
            this.dumpData = dumpData;
            this.resetDs = new HashMap<>();
            this.resetModuleInfo = new HashMap<>();
            this.gotTestcaseSnapMap = new HashMap<>();
            this.getFailCaseAIList=new ArrayList<>();
            this.getFailCaseBuckets = new HashMap<>();
            this.gotReports = gotReports;
            this.pipelineObj = pipelineObj;
            this.scenarioId = scenarioId;
            this.failCaseBucket= new FailCaseBucket();

        }

        public Map<String, FailCaseBucket> getFailCaseBuckets(){

            failCaseBucket.setPipelineObj(pipelineObj);
            failCaseBucket.setGotReports(gotReports);
            failCaseBucket.setTestCaseAIList(getFailCaseAIList);
            getFailCaseBuckets.put(envName,failCaseBucket);

            return getFailCaseBuckets;
        }


        public Map<String, GotTestcaseSnaps> getTestcaseSnapMap(){
            return gotTestcaseSnapMap;
        }



        /**
         * 函数功能:run入口
         * 用例树的建立 => 全量数据准备 => 分层数据准备 => 快速并行执行
         */
        public void run() {

            Date begin = new Date();

            try {

                testResponeList = new ArrayList<>();

                if (testcaseList.size() > 0) {
                    if (isTaskStop(gotReports)) {
                        countDownLatch.countDown();
                        return;
                    }

                    //STEP1:用例执行树的建立
                    buildQueue(testcaseList,"");
                    //STEP2:执行队列重排,将需要重启链路放置最后
                    runQueue = reSortQueue();
//                    System.out.print("dumpData数据:\n" + gson.toJson(dumpData));
//                    System.out.print("生成树:\n" + gson.toJson(runQueue));

                    //STEP3:全量数据准备,去随机,主要是key-value这类明确的
                    Date start = new Date();
                    //STEP4:全量数据准备
                    dataPrepareRun(dumpData, envName, pipelineObj,DUMP_ALL_BUCKET);
                    Date end = new Date();
                    envBucketCostMap.get(envName).put(DUMP_ALL_BUCKET, end.getTime() - start.getTime());
                    if (isTaskStop(gotReports)) {
                        countDownLatch.countDown();
                        return;
                    }
                    //STEP5:分层数据准备 && STEP4:用例并行执行
                    multiLayerPrepareAndRun();

                }
            } catch (Exception e) {
                System.out.print("用例编排报错:原因:" + Toolkit.getErrorStackTrace(e));
            }

            Date end = new Date();
            if(!envReportMap.containsKey(envName)){
                envReportMap.put(envName, new EnvBasicReport());
            }

            envReportMap.get(envName).setRunTime(Toolkit.changeCostFormat(end.getTime()-begin.getTime()));
            countDownLatch.countDown();
        }

        /**
         * 函数功能:将传入的数据集进行一次准备
         * @return
         * @throws Exception
         */
        public JSONObject dataPrepareRun(LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> dumpData, String envName, JSONObject pipelineObj,String type) {

            //每个阶段需要透出的全局数据
            Map<StageName, Object> stageParams = new HashMap<>();

            try {
                Map<String, RunDs> runDsMap ;
                //全量数据桶
                if (type.equals(DUMP_ALL_BUCKET)){
                    runDsMap = dumpDataStaticMap;
                }
                //分层数据桶
                else{
                    runDsMap = envRunDataStaticMap.get(envName);
                }
                //占位符替换
                String dumpDataStr = placeholderReplace(dumpData);
                System.out.print("数据准备执行:占位符替换完成!");

                //加入部署数据
                stageParams.put(StageName.deploy, envName);

                PipelineImpl pipelineExec = new PipelineImpl();

                pipelineExec.build(pipelineObj);

                //获取prepareData阶段执行插件
                BaseStage bs = pipelineExec.flow.get(StageName.prepareData);
                //设置数据准备输入
                bs.setIputData(JSONArray.parse(dumpDataStr));
                bs.setScenarioId(scenarioId);
                //初始化RunData
                bs.setRunData();
                //设置stage之间传递的参数
                bs.setStageParams(stageParams);

                bs.beforeExec();
                bs.exec();
                bs.afterExec();

                System.out.print("数据准备执行:数据准备完成!\n");
                //填充返回结果
                List<RunData> runDataList = bs.getRunDataList();
                //统计每条记录的消耗时间
                for (RunData runData : runDataList) {

                    if (runData.getRunDsMap().size()==0){
                        continue;
                    }

                    for (String dsStr : runData.getRunDsMap().keySet()){
                        if(!runDsMap.containsKey(dsStr)){
                            runDsMap.put(dsStr,new RunDs());
                        }
                        runDsMap.get(dsStr).setRecordNum(runDsMap.get(dsStr).getRecordNum() + runData.getRunDsMap().get(dsStr).getRecordNum());
                        runDsMap.get(dsStr).setCost(runDsMap.get(dsStr).getCost() + runData.getRunDsMap().get(dsStr).getCost());
                    }

                }

            }
            //如有抛异常
            catch (Exception e) {
                logger.error("全量数据准备过程出现异常,中断全量准备,异常原因:\n" + Toolkit.getErrorStackTrace(e));
            }
            JSONObject res = new JSONObject();
            res.put("stageParams", stageParams);
            return res;
        }


        /**
         * 功能:整合为数据准备插件可识别的形式
         *
         * @param recordInfo
         * @param dataType
         * @param dsName
         * @return
         */
        public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> getPrepareDataFormat(RecordInfo recordInfo, String dataType, String dsName) {

            HashMap<String, LinkedList<DetailDataInfo>> DetailDataMap = new HashMap<>();
            LinkedList<DetailDataInfo> detailDataList = new LinkedList<>();
            LinkedList<RecordInfo> recordInfoList = new LinkedList<>();
            recordInfoList.add(recordInfo);
            DetailDataInfo detailDataInfo = new DetailDataInfo();
            detailDataInfo.setDsName(dsName);
            detailDataInfo.setData(recordInfoList);
            detailDataList.add(detailDataInfo);
            DetailDataMap.put(dataType, detailDataList);
            LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> groupRunData = new LinkedList<>();
            groupRunData.add(DetailDataMap);
            return groupRunData;
        }


        /**
         * 功能:处理为data和调用频率的矩阵
         *
         * @param testcaseList
         * @return
         */
        public HashMap<String, DataMatrixUnit> calDataMatrix(List<TestCaseAI> testcaseList) {


            HashMap<String, DataMatrixUnit> DataMatrix = new HashMap<>();
            final Base64.Encoder encoder = Base64.getEncoder();
            DataMatrixUnit dataMatrixUnit;

            for (TestCaseAI testCaseAI : testcaseList) {

                //遍历每组
                for (HashMap<String, LinkedList<DetailDataInfo>> group : testCaseAI.getPrepareData()) {

                    //遍历每个type
                    for (String dataType : group.keySet()) {
                        LinkedHashMap<String, Object> dataKeyMap = new LinkedHashMap<>();

                        //遍历每个ds
                        for (DetailDataInfo detailDataInfo : group.get(dataType)) {

                            Boolean reStartFlag = false;
                            String dsName = detailDataInfo.getDsName();
                            String ds =getTypeDs(dsName);


                            //遍历每个具体数据data
                            for (RecordInfo recordInfo : detailDataInfo.getData()) {
                                String key = recordInfo.getKey();
                                String value = recordInfo.getValue();
                                dataKeyMap.put("key", key);
                                dataKeyMap.put("value", value);
                                dataKeyMap.put("ds", ds);
                                String dataKey = new String(encoder.encode(gson.toJson(dataKeyMap).getBytes()));

                                //如果第一个
                                if (!DataMatrix.containsKey(dataKey)) {
                                    dataMatrixUnit = new DataMatrixUnit();
                                    dataMatrixUnit.addCaseid(testCaseAI.getId(), dataKey);
                                } else {
                                    dataMatrixUnit = DataMatrix.get(dataKey);
                                    dataMatrixUnit.addCaseid(testCaseAI.getId(), dataKey);
                                }
                                dataMatrixUnit.setRestartFlag(reStartFlag);
                                dataMatrixUnit.setName(dsName);
                                dataMatrixUnit.setPrepareData(getPrepareDataFormat(recordInfo, dataType, dsName));

                                DataMatrix.put(dataKey, dataMatrixUnit);
                            }
                        }
                    }
                }
            }

            return DataMatrix;
        }


        /**
         * 对比策略是重启+max > max
         *
         * @param dataMatrixUnit1
         * @param dataMatrixUnit2
         * @return
         */
        public DataMatrixUnit compareTo(DataMatrixUnit dataMatrixUnit1, DataMatrixUnit dataMatrixUnit2) {

            int score1 = 0;
            int score2 = 0;

            if (dataMatrixUnit1 == null) {
                return dataMatrixUnit2;
            } else if (dataMatrixUnit2 == null) {
                return dataMatrixUnit1;
            }


            if (dataMatrixUnit1.isRestartFlag()) {
                score1 += 2;
            }
            if (dataMatrixUnit2.isRestartFlag()) {
                score2 += 2;
            }
            if (dataMatrixUnit1.getCallNum() >= dataMatrixUnit2.getCallNum()) {
                score1 += 1;
            } else {
                score2 += 1;
            }
            if (score1 > score2) {
                return dataMatrixUnit1;
            } else {
                return dataMatrixUnit2;
            }

        }

        /**
         * 获取最大队列Node
         *
         * @param matrix
         * @return
         */
        public QueueNode getMaxAndRebootQueueNode(HashMap<String, DataMatrixUnit> matrix) {

            QueueNode queueNode = new QueueNode();
            DataMatrixUnit dataMatrixMaxUnit = null;

            for (String dataKey : matrix.keySet()) {
                DataMatrixUnit dataMatrixUnit = matrix.get(dataKey);
                dataMatrixMaxUnit = compareTo(dataMatrixMaxUnit, dataMatrixUnit);
            }
            queueNode.setRestartFlag(dataMatrixMaxUnit.isRestartFlag());
            queueNode.setCaseids(dataMatrixMaxUnit.getCaseids());
            queueNode.setPrepareData(dataMatrixMaxUnit.getPrepareData());
            queueNode.setCaseNum(dataMatrixMaxUnit.getCaseids().size());
            queueNode.setDataKeyLis(dataMatrixMaxUnit.getDataKeyList());
            queueNode.setDataType(DATA_PREPARE);
            return queueNode;
        }


        /**
         * 功能:对testCase进行剪枝,去除公共数据
         *
         * @param testCaseAI
         * @param dataKeyList
         * @return
         */
        public TestCaseAI cutCaseData(TestCaseAI testCaseAI, List<String> dataKeyList) {

            final Base64.Encoder encoder = Base64.getEncoder();

            TestCaseAI testCaseCutAI = (TestCaseAI) testCaseAI.clone();
            //遍历每组
            for (int groupNum = testCaseAI.getPrepareData().size() - 1; groupNum >= 0; groupNum--) {
                HashMap<String, LinkedList<DetailDataInfo>> group = testCaseAI.getPrepareData().get(groupNum);
                LinkedHashMap<String, Object> dataKeyMap = new LinkedHashMap<>();

                //遍历每个type
                for (String dataType : group.keySet()) {

                    //遍历每个ds
                    for (int detailNum = group.get(dataType).size() - 1; detailNum >= 0; detailNum--) {
                        DetailDataInfo detailDataInfo = group.get(dataType).get(detailNum);
                        String dsName = detailDataInfo.getDsName();
                        String ds = getTypeDs(dsName);

                        //遍历每个具体数据data
                        for (int recordNum = detailDataInfo.getData().size() - 1; recordNum >= 0; recordNum--) {
                            RecordInfo recordInfo = detailDataInfo.getData().get(recordNum);
                            String key = recordInfo.getKey();
                            String value = recordInfo.getValue();
                            dataKeyMap.put("key", key);
                            dataKeyMap.put("value", value);
                            dataKeyMap.put("ds", ds);
                            String dataKey = new String(encoder.encode(gson.toJson(dataKeyMap).getBytes()));
                            //剪枝
                            if (dataKeyList.contains(dataKey)) {
                                testCaseCutAI.getPrepareData().get(groupNum).get(dataType).get(detailNum).getData().remove(recordNum);
                                if (testCaseCutAI.getPrepareData().get(groupNum).get(dataType).get(detailNum).getData().size() == 0) {
                                    testCaseCutAI.getPrepareData().get(groupNum).get(dataType).remove(detailNum);
                                }
                                if (testCaseCutAI.getPrepareData().get(groupNum).get(dataType).size() == 0) {
                                    testCaseCutAI.getPrepareData().get(groupNum).remove(dataType);
                                }
                                if (testCaseCutAI.getPrepareData().get(groupNum).size() == 0) {
                                    testCaseCutAI.getPrepareData().remove(groupNum);
                                }
                            }

                        }
                    }
                }
            }

            return testCaseCutAI;
        }


        /**
         * 功能:将testcaselist进行剪枝,并进行分组
         *
         * @param queueNode
         * @param testcaseList
         * @return
         */
        public MatrixInfo cutTestcaseList(QueueNode queueNode, List<TestCaseAI> testcaseList) {

            List<TestCaseAI> testcaseLeftList = new ArrayList<>();
            List<TestCaseAI> testcaseRightList = new ArrayList<>();
            MatrixInfo matrixInfo = new MatrixInfo();
            for (TestCaseAI testCaseAI : testcaseList) {

                if (queueNode.getCaseids().contains(testCaseAI.getId())) {
                    testCaseAI = cutCaseData(testCaseAI, queueNode.getDataKeyLis());
                    testcaseLeftList.add(testCaseAI);
                } else {
                    testcaseRightList.add(testCaseAI);
                }
            }

            matrixInfo.setLeftCaseList(testcaseLeftList);
            matrixInfo.setRightCaseList(testcaseRightList);
            return matrixInfo;
        }

        /**
         * 函数功能:更新matrix并对列表进行剪枝处理
         * 1.每次能在传递进来的队列中算出:数据<=>依赖用例数目的矩阵.
         * 2.更新抽取data后的list.并划分左右子树.
         * @param testcaseList
         * @return
         */
        public MatrixInfo updataMatrixAndCutCases(List<TestCaseAI> testcaseList) {


            String leftTreeReStartFlag = "0";
            String rightTreeReStartFlag = "0";

            //数据分组优先级:重启数据源 > 最大公共数据源
            MatrixInfo matrixInfo = new MatrixInfo();
            //计算获取:数据<=>依赖用例的矩阵
            HashMap<String, DataMatrixUnit> matrix = calDataMatrix(testcaseList);
//            System.out.print("matrix:\n"+gson.toJson(matrix));
            //全叶子节点,不需要剪枝
            if (matrix.size() == 0) {
                matrixInfo.setMatrix(matrix);
                QueueNode queueNode = new QueueNode();
                queueNode.setDataType(QUERY_CHECK);
                queueNode.addCaseList(testcaseList);
                queueNode.setRestartFlag(false);
                matrixInfo.setLeftTreeReStartFlag(leftTreeReStartFlag);
                matrixInfo.setRightTreeReStartFlag(rightTreeReStartFlag);
                matrixInfo.setQueueUnit(queueNode);

                return matrixInfo;
            }
            //非叶子节点,需要剪枝,处理最大queueNode
            QueueNode queueNode = getMaxAndRebootQueueNode(matrix);
            //判断该节点是否有重启
            if(queueNode.restartFlag){
                leftTreeReStartFlag = "1";
            }
//            System.out.print("推出queueNode:\n"+ gson.toJson(queueNode));
            //剪枝
            MatrixInfo caselist = cutTestcaseList(queueNode, testcaseList);
            matrixInfo.setMatrix(matrix);
            matrixInfo.setQueueUnit(queueNode);
            matrixInfo.setLeftCaseList(caselist.getLeftCaseList());
            matrixInfo.setRightCaseList(caselist.getRightCaseList());
            matrixInfo.setLeftTreeReStartFlag(leftTreeReStartFlag);
            matrixInfo.setRightTreeReStartFlag(rightTreeReStartFlag);

            return matrixInfo;

        }


        /**
         * 函数功能:递归创建深度优先执行树
         * 树的执行顺序由队列表示
         * @param testcaseList
         */
        public void buildQueue(List<TestCaseAI> testcaseList,String restartPath) {

            if (testcaseList.size() == 0) {
                return;
            }
            //获取数据频率矩阵 && 剪枝caseList
            MatrixInfo matrixInfo = updataMatrixAndCutCases(testcaseList);
            HashMap<String, DataMatrixUnit> matrix = matrixInfo.getMatrix();

            //全叶子节点
            if (matrix.size() == 0) {
                QueueNode queueUnit = matrixInfo.getQueueUnit();
                queueUnit.setRestartPath(restartPath + queueUnit.getRestartPath());
                runQueue.offer(queueUnit);
                return;
            }
            //非全叶子节点
            List<TestCaseAI> leftCastList = matrixInfo.getLeftCaseList();
            List<TestCaseAI> rightCastList = matrixInfo.getRightCaseList();
            QueueNode queueUnit = matrixInfo.getQueueUnit();
            if(queueUnit.restartFlag){
                queueUnit.setRestartPath(restartPath + "1");
            }
            else {
                queueUnit.setRestartPath(restartPath + "0");
            }
            runQueue.offer(queueUnit);
            buildQueue(leftCastList,restartPath+matrixInfo.getLeftTreeReStartFlag());
            buildQueue(rightCastList,restartPath+matrixInfo.getRightTreeReStartFlag());
        }


        /**
         * 类功能:执行一次query和check过程
         */
        class MultiRunCaseThread extends Thread {

            public JSONObject pipelineObj;
            public GotReports gotReports;
            public CountDownLatch countDownLatch;
            public TestCaseAI testCaseAI;
            public Map<String, GotTestcaseSnaps> gotTestcaseSnapMap;
            public List<TestCaseAI> getFailCaseAIList;


            public MultiRunCaseThread(String envName, JSONObject pipelineObj, TestCaseAI testCaseAI, GotReports gotReports, CountDownLatch countDownLatch) {
                this.gotReports = gotReports;
                this.pipelineObj = pipelineObj;
                this.countDownLatch = countDownLatch;
                this.testCaseAI = testCaseAI;
                this.gotTestcaseSnapMap = new HashMap<>();
                this.getFailCaseAIList = new ArrayList<>();
            }

            public Map<String, GotTestcaseSnaps> getTestcaseSnapMap(){
                return gotTestcaseSnapMap;
            }

            public List<TestCaseAI> getFailCaseAIList(){
                return getFailCaseAIList;
            }

            public void run() {

                GotTestcaseSnaps gotTestcaseSnaps = new GotTestcaseSnaps();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    //每个阶段需要透出的全局数据
                    Map<StageName, Object> stageParams = new HashMap<>();
                    List<StageName> stageNameList = new ArrayList<>();
                    stageNameList.add(StageName.caseRunStage);

                    PipelineImpl pipelineExec = new PipelineImpl();
                    pipelineExec.build(pipelineObj);

                    //更新修改时间
                    saveReport(gotReports);
                    System.out.print("==========开始执行用例id:" + testCaseAI.getId() + ",时间" + df.format(new Date()) + "===========\n");
                    //任务中断
                    if (isTaskStop(gotReports)) {
                        countDownLatch.countDown();
                        return;
                    }

                    TestRespone testRespone = new TestRespone();
                    Date start = new Date();
                    //占位符替换
                    String testCaseStr = placeholderReplace(testCaseAI);
                    //build测试数据
                    TestCaseData testCaseDataObj = TestCaseData.build(JSONObject.parseObject(testCaseStr));
                    //加入部署数据
                    stageParams.put(StageName.deploy, envName);
                    //执行每阶段
                    for (StageName stageName : stageNameList) {

                        BaseStage bs = pipelineExec.flow.get(stageName);
                        //表示回归,传入exec中则不会执行重启判断逻辑
                        bs.setRunType("2");
                        //设置testcase输入
                        if (testCaseDataObj.testCaseInputMap.containsKey(stageName)) {
                            bs.setIputData(testCaseDataObj.testCaseInputMap.get(stageName));
                            bs.setScenarioId(Long.valueOf(testCaseAI.getScenarioId()));
                            bs.setTestcaseId(Long.valueOf(testCaseAI.getId()));
                            //初始化RunData
                            bs.setRunData();
                        }
                        //设置stage之间传递的参数
                        bs.setStageParams(stageParams);
                        int retryNum = getRetryNum(bs.getStageJsonObj());
                        int i = 0;
                        //判断是否需要重试,如果最终结果是
                        do {
                            i++;
                            if (i > 1) {
                                int n = i - 1;
                                System.out.println("[失败重试运行中],当前重试第" + n + "次");
                            }
                            bs.exec();
                            stageParams = bs.getStageParams();
                            //填充返回结果
                            testRespone = bs.setTestRespone(testRespone);
                        } while (i <= retryNum && !testRespone.getStatus().equals(ResultStatus.SUCCESS));
                        gotTestcaseSnaps.setRetryNum(i - 1);
                    }
                    Date end = new Date();
                    //用例快照保存
                    gotTestcaseSnaps.setEnvName(envName);
                    gotTestcaseSnaps.setRunTime((end.getTime() - start.getTime()) / 1000);
                    gotTestcaseSnaps.setRunTimeStr(formatTime((end.getTime() - start.getTime()) / 1000));
                    gotTestcaseSnaps.setStatus(testRespone.getStatus().name());
                    gotTestcaseSnaps.setAppId(testCaseAI.getAppId());
                    gotTestcaseSnaps.setScenarioId(testCaseAI.getScenarioId());
                    gotTestcaseSnaps.setDescription(testCaseAI.getDescription());
                    gotTestcaseSnaps.setLongDescription(testCaseAI.getLongDescription());
                    gotTestcaseSnaps.setName(testCaseAI.getName());
                    gotTestcaseSnaps.setTestcaseId(testCaseAI.getId());
                    gotTestcaseSnaps.setTestreportId(gotReports.getId());
                    gotTestcaseSnaps.setCaseGroup(testCaseAI.getCaseGroup());
                    gotTestcaseSnaps.setTag(testCaseAI.getTag());
                    gotTestcaseSnaps.setVersion(testCaseAI.getVersion());
                    gotTestcaseSnaps.setStatus(testRespone.getStatus().name());
                    gotTestcaseSnaps.setContent(pack(testCaseAI, testRespone));
                    gotTestcaseSnaps.setIsTrunkFlag(testCaseAI.getIsTrunkFlag());
                    testResponeList.add(testRespone);


                    if(regAccuracy){
                        //需要获取覆盖
                        try{
                            GotCaseAccuracy accuracy = new GotCaseAccuracy();
                            accuracy.setCaseId(testCaseAI.getId());
                            accuracy.setCollectType("single");
                            accuracy.setExeId(gotReports.getId());
                            accuracy.setCovLine(getCaseAccuracy(testCaseAI));

                            caseAccuracyService.save(accuracy);
                        }catch (Exception e){
                            System.out.println(e.getMessage());
                        }

                    }


                    try {
                        //记录已执行用例数量，计算执行进度
                        JSONObject anlysis = null;
                        JSONObject basicReport = null;
                        try {
                            anlysis = JSONObject.parseObject(gotReports.getAnalysis());
                            basicReport = anlysis.getJSONObject("basicReport");
                        }catch(Exception e){
                            anlysis = new JSONObject();
                            basicReport = new JSONObject();
                            basicReport.put("sucessCaseNum", 0);
                            basicReport.put("failCaseNum", 0);
                        }

                        Integer successNum = basicReport.getInteger("sucessCaseNum");
                        Integer failNum = basicReport.getInteger("failCaseNum");
                        if (testRespone.getStatus().name().equals(SUCCESS)) {
                            successNum++;
                        } else {
                            failNum++;
                        }

                        basicReport.put("caseNum", gotReports.getCaseNum());
                        basicReport.put("sucessCaseNum", successNum);
                        basicReport.put("failCaseNum", failNum);

                        anlysis.put("basicReport", basicReport);
                        gotReports.setAnalysis(anlysis.toJSONString());
                        saveReport(gotReports);
                    }catch(Exception e){
                        //异常则skip..
                    }


                } catch (Exception e) {
                    gotTestcaseSnaps.setStatus(ResultStatus.ERROR.name());
                }

                //记录成功的用例,失败的用例重试后如果还失败才会记录
                if (gotTestcaseSnaps.getStatus().equals(ResultStatus.SUCCESS.name())){
                    System.out.print("恭喜用例运行通过..."+ ",时间" +df.format(new Date()) + "\n");
                    saveGotTestcaseSnaps(gotTestcaseSnaps);
                }

                //失败桶记录
                if (gotTestcaseSnaps.getStatus().equals(ResultStatus.ERROR.name())){
                    gotTestcaseSnaps.setStatus("ERROR[已在失败桶等待重试]");
                    getFailCaseAIList.add(testCaseAI);
                    saveGotTestcaseSnaps(gotTestcaseSnaps);
                    System.out.print("===========用例运行失败,进入失败桶等待重试..."+ ",时间" +df.format(new Date())+"===========\n");
                }
                gotTestcaseSnapMap.put(String.valueOf(testCaseAI.getId()),gotTestcaseSnaps);

                countDownLatch.countDown();
            }
        }

        /**
         * 函数功能:并发执行用例query和check
         * @param caseIds
         */
        public void multiQueryAndQuery(List<Long> caseIds, JSONObject dataPrepareRunRes) throws InterruptedException {

            //并行桶
            List<TestCaseAI> runTestcaseListBucket = new ArrayList<>();
            //串行桶
            List<TestCaseAI> runTestcaseListSerialBucket = new ArrayList<>();

            //获取所有id
            for (TestCaseAI testCaseAI : testcaseList) {

                if (caseIds.contains(testCaseAI.getId())) {

                    //并行执行
                    if(testCaseAI.isParallel()){
                        runTestcaseListBucket.add(testCaseAI);
                    }
                    //串行执行
                    else{
                        runTestcaseListSerialBucket.add(testCaseAI);
                    }
                }
            }

            if (runTestcaseListBucket.size() == 0 && runTestcaseListSerialBucket.size()==0) {
                return ;
            }


            if(runTestcaseListSerialBucket.size()>0){
                System.out.print("开始执行串行用例集:" + runTestcaseListSerialBucket.size()+"\n");

                //串行执行
                for (TestCaseAI testCaseAI : runTestcaseListSerialBucket) {
                    envRunBucketMap.put(envName,envRunBucketMap.get(envName)+1);
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    MultiRunCaseThread multiRunCaseThread;
                    List<MultiRunCaseThread> threadsList = new ArrayList<>();
                    multiRunCaseThread = new MultiRunCaseThread(envName, pipelineObj, testCaseAI, gotReports, countDownLatch);
                    multiRunCaseThread.setDaemon(true);
                    multiRunCaseThread.start();
                    threadsList.add(multiRunCaseThread);
                    //主进程阻塞,如果部署线程执行结束则往下
                    countDownLatch.await();
                    //搜集所有的执行报告
                    for (MultiRunCaseThread thread: threadsList){
                        gotTestcaseSnapMap.putAll(thread.getTestcaseSnapMap());
                        getFailCaseAIList.addAll(thread.getFailCaseAIList());
                    }
                }
            }
            if(runTestcaseListBucket.size()>0){

                envRunBucketMap.put(envName,envRunBucketMap.get(envName)+1);
                //并行执行
                CountDownLatch countDownLatch = new CountDownLatch(runTestcaseListBucket.size());
                MultiRunCaseThread multiRunCaseThread;
                System.out.print("开始并行执行用例数:" + runTestcaseListBucket.size());
                List<MultiRunCaseThread> threadsList = new ArrayList<>();

                for (TestCaseAI testCaseAI : runTestcaseListBucket) {
                    multiRunCaseThread = new MultiRunCaseThread(envName, pipelineObj, testCaseAI, gotReports, countDownLatch);
                    multiRunCaseThread.setDaemon(true);
                    multiRunCaseThread.start();
                    threadsList.add(multiRunCaseThread);
                    Thread.currentThread().sleep(1000);
                }
                //主进程阻塞,如果部署线程执行结束则往下
                countDownLatch.await();
                //搜集所有的执行报告
                for (MultiRunCaseThread thread: threadsList){
                    gotTestcaseSnapMap.putAll(thread.getTestcaseSnapMap());
                    getFailCaseAIList.addAll(thread.getFailCaseAIList());
                }
            }

        }


        /**
         * 函数功能:统计数据准备的冗余度和总量
         * @param unit
         */
        public void calStaticRunData(QueueNode unit){

            Map<String, RunDs> runDataStaticMap = envRunDataStaticMap.get(envName);

            for(HashMap<String, LinkedList<DetailDataInfo>> typeInfo : unit.getPrepareData()){

                for (String type : typeInfo.keySet()){
                    LinkedList<DetailDataInfo> list = typeInfo.get(type);
                    for (DetailDataInfo detailDataInfo : list){
                        String ds =detailDataInfo.getDsName();
                        String typeDs = type + ":" + getTypeDs(ds);

                        if (!runDataStaticMap.containsKey(typeDs)){
                            runDataStaticMap.put(typeDs,new RunDs());
                        }
                        runDataStaticMap.get(typeDs).setAllNum(runDataStaticMap.get(typeDs).getAllNum()+unit.getCaseNum());
                        runDataStaticMap.get(typeDs).setRepeatNum(runDataStaticMap.get(typeDs).getRepeatNum() + unit.getCaseNum()-1);
                    }
                }
            }
        }

        /**
         * 函数功能:数据准备合并过程
         * @param dumpData
         * @param prepareData
         * @return
         */
        public LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> mergeRunData(LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> dumpData, LinkedList<HashMap<String, LinkedList<DetailDataInfo>>>  prepareData){


            //dump集合为空
            if (dumpData.size()==0){
                dumpData = prepareData;
            }
            else{
                for (int groupNum=0; groupNum < prepareData.size(); groupNum++){
                    HashMap<String, LinkedList<DetailDataInfo>> dumpDataMap= dumpData.get(groupNum);
                    HashMap<String, LinkedList<DetailDataInfo>> dataMap= prepareData.get(groupNum);

                    for (String dataType: dataMap.keySet()){
                        LinkedList<DetailDataInfo> dataList = dataMap.get(dataType);
                        LinkedList<DetailDataInfo> dumpDataList = dumpDataMap.get(dataType);
                        //如果全新的datatype
                        if (!dumpDataMap.containsKey(dataType)){
                            dumpDataMap.put(dataType,dataMap.get(dataType));
                        }
                        //如果已存在
                        else{
                            for (int listNum=0; listNum < dataList.size();  listNum++){
                                Boolean flag = true;

                                DetailDataInfo detailDataInfo = dataList.get(listNum);
                                //遍历dump
                                for (int listDumpNum=0; listDumpNum < dumpDataList.size();  listDumpNum++){
                                    DetailDataInfo dumpDetailDataInfo = dumpDataList.get(listDumpNum);
                                    //如果ds相等,则record直接合并
                                    if (dumpDetailDataInfo.getDsName().equals(detailDataInfo.getDsName())){
                                        flag = false;
                                        dumpDetailDataInfo.getData().addAll(detailDataInfo.getData());
                                    }
                                }
                                //如果不存在ds
                                if (flag){
                                    dumpDataList.add(detailDataInfo);
                                }
                            }
                        }
                    }
                }
            }

            return dumpData;
        }


        /**
         * 函数功能:
         * 按照深度优先方式执行数据准备 && 并行发送用例和校验结果
         */
        public void multiLayerPrepareAndRun() {

            LinkedList<HashMap<String, LinkedList<DetailDataInfo>>> dumpLayerData = new LinkedList<>();

            Long dumpTreeTime = Long.valueOf(0);
            Long runTreeTime = Long.valueOf(0);

//            System.out.print(gosn.toJson(runQueue));
            //队列执行,先进先出
            for (QueueNode unit : runQueue) {
                //如果节点是数据准备类型,则进行合并,后续在一次执行
                if (unit.getDataType().equals(DATA_PREPARE)) {
                    //合并
                    dumpLayerData = mergeRunData(dumpLayerData, unit.getPrepareData());
                    //统计数据冗余度
                    calStaticRunData(unit);
                }
                //如果节点是发送校验类型
                else {
                    //运行分层数据准备
                    Date start = new Date();
                    JSONObject dataPrepareRunRes = dataPrepareRun(dumpLayerData, envName, pipelineObj,DUMP_TREE_BUCKET);
                    Date end = new Date();
                    dumpTreeTime += end.getTime() - start.getTime();

                    //并发执行case
                    try {
                        start = new Date();
                        if (isTaskStop(gotReports)) {
                            return;
                        }
                        multiQueryAndQuery(unit.getCaseids(), dataPrepareRunRes);
                        end = new Date();
                        runTreeTime += end.getTime() - start.getTime();

                    } catch (Exception e) {
                        System.out.print("分层快速执行失败:原因:" + Toolkit.getErrorStackTrace(e)+"\n");
                    }

                    //清空分层数据集
                    dumpLayerData = new LinkedList<>();
                }
            }
            envBucketCostMap.get(envName).put(DUMP_TREE_BUCKET, dumpTreeTime);
            envBucketCostMap.get(envName).put(QUERY_CHECK_BUCKET, runTreeTime);
        }


        /**
         * 函数功能:clone队列
         * @param queue
         * @return
         */
        public Queue<QueueNode> cloneQueue(Queue<QueueNode> queue){
            Queue<QueueNode> cloneQueue = new LinkedList<>();
            for (QueueNode queueNode: queue){
                cloneQueue.offer((QueueNode) queueNode.clone());
            }
            return cloneQueue;
        }


        /**
         * 函数功能:队列重排
         * 将执将需要重启的链路放置尾部
         * @return
         */
        public Queue<QueueNode> reSortQueue(){
            Queue<QueueNode> eachQueue = new LinkedList<>();
            Queue<QueueNode> queueList1 = new LinkedList<>();
            Queue<QueueNode> queueList2 = new LinkedList<>();

            //扫描队列
            for (QueueNode queueNode : runQueue){

                eachQueue.offer(queueNode);

                if (queueNode.getDataType().equals(QUERY_CHECK) && queueNode.getRestartPath().contains("1")){
                    queueList1.addAll(cloneQueue(eachQueue));
                    eachQueue = new LinkedList<>();
                }

                if (queueNode.getDataType().equals(QUERY_CHECK) && !queueNode.getRestartPath().contains("1")){
                    queueList2.addAll(cloneQueue(eachQueue));
                    eachQueue = new LinkedList<>();
                }

            }

            //逆序重排队列
            queueList2.addAll(queueList1);
            return queueList2;
        }






    }
    public String getCaseAccuracy(TestCaseAI testCaseAI){
        //需要用户基于实际使用的覆盖统计方案，获取每个用例执行后的覆盖代码行
        //demo中通过mock数据，获取用例覆盖代码
        String res = "";
        try {
            JSONObject  caseQuery = JSONObject.parseObject(testCaseAI.getCaseRunStage().get(0).getData().get(0).input);
            List<String> covList = CovMock.getCaseCovMock(caseQuery);

            for(String line : covList){
                res += (line + "\n");
            }
            res = res.trim();
        }catch(Exception e){
            //
        }
        return res;

    }

    /**
     * 类功能:单环境失败用例集执行线程
     */
    class SingleEnvRunCaseThread extends Thread {
        public List<TestCaseAI> serialCaseList;
        public Map<String, String> deployMap;
        public JSONObject pipelineObj;
        public GotReports gotReports;
        public CountDownLatch countDownLatch;
        public String envName;


        public SingleEnvRunCaseThread(List<TestCaseAI> serialCaseList, Map<String, String> deployMap, JSONObject pipelineObj, GotReports gotReports, CountDownLatch countDownLatch, String envName) {
            this.serialCaseList = serialCaseList;
            this.deployMap = deployMap;
            this.pipelineObj = pipelineObj;
            this.gotReports = gotReports;
            this.countDownLatch = countDownLatch;
            this.envName = envName;

        }

        /**
         * 函数功能:失败用例重跑策略
         * 说明:用户可自定义,比如全部失败的用例只跑其中10%,小于某个阈值的用例集需要全部重跑
         * @param testCaseAIList
         * @return
         */
        public List<TestCaseAI>  selectReRunCaseList (List<TestCaseAI>  testCaseAIList ){

            int selectNum = 0;
            int limit = testCaseAIList.size();


            //策略:此处limit可限制重跑个数阈值
            if(testCaseAIList.size() > limit){
                selectNum = limit;
            }
            else{
                selectNum = testCaseAIList.size();
            }

            List<TestCaseAI>  selectCaseAIList = new ArrayList<>();

            for (int i=0;i< selectNum;i++){
                selectCaseAIList.add(testCaseAIList.get(i));
            }
            for(int i=selectNum;i< testCaseAIList.size();i++){
                TestCaseAI testCaseAI = testCaseAIList.get(i);
                GotTestcaseSnaps gotTestcaseSnaps = gotTestcaseSnapMap.get(String.valueOf(testCaseAI.getId()));
                gotTestcaseSnaps.setStatus(ResultStatus.ERROR.name() + "[未重试]");
                gotTestcaseSnaps.setIsTrunkFlag(testCaseAI.getIsTrunkFlag());
                gotTestcaseSnapMap.put(String.valueOf(testCaseAI.getId()), gotTestcaseSnaps);
                gotTestcaseSnapsDao.update(gotTestcaseSnaps);
            }
            return selectCaseAIList;
        }


        /**
         * 函数功能:重跑失败用例并进行最终状态设置
         * @param testCaseAIList
         * @param deployMap
         * @param pipelineObj
         * @param gotReports
         * @return
         * @throws IOException
         */
        public List<TestCaseAI>  runFailCaseList(List<TestCaseAI> testCaseAIList, Map<String, String>deployMap, JSONObject pipelineObj, GotReports gotReports) throws IOException {

            //每个阶段需要透出的全局数据
            Map<StageName, Object> stageParams = new HashMap<>();
            List<StageName> stageNameList = new ArrayList<>();
            stageNameList.add(StageName.prepareData);
            stageNameList.add(StageName.caseRunStage);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

            if (testCaseAIList.size() == 0) {
                return testCaseAIList;
            }
            PipelineImpl pipelineExec = new PipelineImpl();
            pipelineExec.build(pipelineObj);

            List<GotTestcaseSnaps> failTestcaseList = new ArrayList<>();
            int failCaseNum = 0;
            //重跑策略选取用例集
            testCaseAIList = selectReRunCaseList(testCaseAIList);

            //重跑失败桶用例集
            for (TestCaseAI testCaseAI : testCaseAIList) {

                try{


                    System.out.print( "==========执行失败桶用例id:" + testCaseAI.getId() + ",时间" + df.format(new Date()) + "===========\n");
                    appId = testCaseAI.getAppId();
                    scenarioId = testCaseAI.getScenarioId();

                    System.out.print( "失败重试和智能排查阶段:执行用例,id:" + testCaseAI.getId());
                    //任务中断
                    if (isTaskStop(gotReports)) {
                        return testCaseAIList;
                    }
                    TestRespone testRespone = new TestRespone();

                    //重试阶段需准备全部数据
//                    testCaseAI.setPrepareDataSnap(testCaseAI.getPrepareData());

                    //占位符替换
                    String testCaseStr = placeholderReplace(testCaseAI);
                    //build测试数据
                    TestCaseData testCaseDataObj = TestCaseData.build(JSONObject.parseObject(testCaseStr));
                    //加入部署数据
                    stageParams.put(StageName.deploy, deployMap);

                    //执行每阶段
                    for (StageName stageName : stageNameList) {

                        BaseStage bs = pipelineExec.flow.get(stageName);
                        //设置testcase输入
                        if (testCaseDataObj.testCaseInputMap.containsKey(stageName)) {
                            bs.setIputData(testCaseDataObj.testCaseInputMap.get(stageName));
                            bs.setScenarioId(Long.valueOf(testCaseAI.getScenarioId()));
                            bs.setTestcaseId(Long.valueOf(testCaseAI.getId()));
                            //初始化RunData
                            bs.setRunData();
                        }
                        //设置stage之间传递的参数
                        bs.setStageParams(stageParams);
                        int retryNum = getRetryNum(bs.getStageJsonObj());
                        int i = 0;
                        //判断是否需要重试,如果最终结果是
                        do {
                            i++;
                            if (i > 1) {
                                int n = i - 1;
                                logger.info("[失败重试运行中],当前重试第" + n + "次");

                            }
                            bs.exec();
                            stageParams = bs.getStageParams();
                            //填充返回结果
                            testRespone = bs.setTestRespone(testRespone);
                        } while (i <= retryNum && !testRespone.getStatus().equals(ResultStatus.SUCCESS));
                    }

                    GotTestcaseSnaps gotTestcaseSnaps = gotTestcaseSnapMap.get(String.valueOf(testCaseAI.getId()));
                    saveReport(gotReports);
                    //快照页中保存更多结果信息
                    gotTestcaseSnaps.setContent(pack(testCaseAI, testRespone));

                    //如果成功了,则是要修改最终的状态
                    if (testRespone.getStatus().name().equals(ResultStatus.SUCCESS.name())) {

                        gotTestcaseSnaps.setStatus(ResultStatus.SUCCESS.name());
                        gotTestcaseSnapMap.put(String.valueOf(testCaseAI.getId()), gotTestcaseSnaps);
                        //快照记录
                        gotTestcaseSnapsDao.update(gotTestcaseSnaps);

                    }
                    //如果失败了,更新智能排查和动态归因
                    else {
                        failCaseNum++;
                        gotTestcaseSnaps.setStatus(ResultStatus.ERROR.name());
                        gotTestcaseSnaps.setIsTrunkFlag(testCaseAI.getIsTrunkFlag());
                        failTestcaseList.add(gotTestcaseSnaps);
                        gotTestcaseSnapMap.put(String.valueOf(testCaseAI.getId()), gotTestcaseSnaps);

                    }
                }
                catch (Exception e){
                    System.out.print("[重试阶段]用例回归过程异常:"+ Toolkit.getErrorStackTrace(e));
                }

            }

            return testCaseAIList ;

        }

        public void run() {
            try {
                runFailCaseList(serialCaseList, deployMap, pipelineObj, gotReports);
            } catch (Exception e) {
                System.out.print( "重试桶线程异常:" + Toolkit.getErrorStackTrace(e));
            }
            countDownLatch.countDown();
        }
    }


    /**
     * 函数功能:失败用例重跑
     * @param gotReports
     * @return
     */
    public String reRunFailCaseBuckets(GotReports gotReports) {

        try {
            if (failCaseBuckets.size() > 0) {

                gotReports.setStatus("失败桶重跑中");
                saveReport(gotReports);
                CountDownLatch countDownLatch = new CountDownLatch(failCaseBuckets.size());
                SingleEnvRunCaseThread singleEnvRunCaseThread;
                List<SingleEnvRunCaseThread> threadsList = new ArrayList<>();

                Map<String,String> deployMap= null;
                //重跑
                for (String envName : failCaseBuckets.keySet()) {
                    FailCaseBucket failCaseBucket = failCaseBuckets.get(envName);
                    JSONObject pipelineObj = failCaseBucket.getPipelineObj();
                    List<TestCaseAI> testCaseAIList = failCaseBucket.getTestCaseAIList();
                    System.out.print(("执行失败用例集,用例数:" + testCaseAIList.size() + ",环境名为:" + envName));
                    singleEnvRunCaseThread = new SingleEnvRunCaseThread(testCaseAIList,deployMap, pipelineObj, gotReports,countDownLatch, envName);
                    singleEnvRunCaseThread.setDaemon(true);
                    singleEnvRunCaseThread.start();
                    threadsList.add(singleEnvRunCaseThread);
                    Thread.currentThread().sleep(2000);
                }
                countDownLatch.await();
            }
        } catch (Exception e) {
            System.out.print("失败桶运行过程出现异常,请检查!" + Toolkit.getErrorStackTrace(e));

        }


        return "";
    }


    /**
     * 函数功能:保存测试报告
     * @param gotReports
     */
    public void saveReport(GotReports gotReports) {
        gotReportsDao.update(gotReports);
    }

    /**
     * 函数功能:插入测试报告
     * @param gotReports
     */
    public void insertReport(GotReports gotReports) {
        gotReportsDao.insert(gotReports);
    }

    /**
     * 函数功能:保存测试用例快照
     * @param gotTestcaseSnaps
     * @return
     */
    public Long saveGotTestcaseSnaps(GotTestcaseSnaps gotTestcaseSnaps) {
        return Long.valueOf(gotTestcaseSnapsDao.insert(gotTestcaseSnaps));
    }


    /**
     * 函数功能:占位符替换
     * @param testCase
     * @return
     */
    public String placeholderReplace(Object testCase) {

        //将转化为json串,然后整体来进行替换
        Gson gosn = new Gson();
        String testCaseJsonStr = gosn.toJson(testCase);
        return Toolkit.placeholderReplace(testCaseJsonStr);
    }


    /**
     * 功能:预处理用例,将每个用例,给出用例的是否能并行,以及用例数据准备等信息.
     *
     * @param testcaseList
     * @return
     */
    public List<TestCaseAI> forkTestcaseAIList(List<TestCaseInput> testcaseList) {

        List<TestCaseAI> testCaseAIList = new ArrayList<>();

        for (TestCaseInput testCase : testcaseList) {

            testCaseAIList.add(TestCaseAI.packTestCaseAI(testCase));
        }

        return testCaseAIList;
    }


    /**
     * 类功能:多环境caseBycase执行线程
     */
    class MultiEnvSimpleCaseThread extends Thread {

        public List<TestCaseAI> serialCaseList;
        public JSONObject pipelineObj;
        public GotReports gotReports;
        public List<TestRespone> testResponeList;
        public CountDownLatch countDownLatch;
        public String envName;


        public MultiEnvSimpleCaseThread(List<TestCaseAI> serialCaseList, JSONObject pipelineObj, GotReports gotReports, CountDownLatch countDownLatch, String envName) {
            this.serialCaseList = serialCaseList;
            this.pipelineObj = pipelineObj;
            this.gotReports = gotReports;
            this.countDownLatch = countDownLatch;
            this.envName = envName;
        }

        public List<TestRespone> getResult(){
            return testResponeList;
        }


        /**
         * 函数功能:
         * @return
         * @throws Exception
         */
        public List<TestRespone> runCaseList(List<TestCaseAI>  serialCaseList , JSONObject pipelineObj, GotReports gotReports, String envName){

            try{
                Map<StageName,Object> stageParams = new HashMap<>();
                List<StageName> stageNameList = new ArrayList<>();
                stageNameList.add(StageName.prepareData);
                stageNameList.add(StageName.caseRunStage);
                List<TestRespone> testResponeList = new ArrayList<>();

                if(serialCaseList.size() == 0){
                    return testResponeList;
                }

                PipelineImpl pipelineExec = new PipelineImpl();
                pipelineExec.build(pipelineObj);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Integer failNum = 0;
                Integer successNum = 0;
                //遍历执行用例集
                for(TestCaseAI testCaseAI : serialCaseList){

                    envRunBucketMap.put(envName,envRunBucketMap.get(envName)+1);
                    System.out.print("==========开始执行用例id:" + testCaseAI.getId() + ",时间" +df.format(new Date()) + "===========\n");
                    //任务中断
                    if (isTaskStop(gotReports)){
                        return null;
                    }

                    TestRespone testRespone = new TestRespone();
                    GotTestcaseSnaps gotTestcaseSnaps = new GotTestcaseSnaps();
                    try {
                        Date start = new Date();
                        //占位符替换
                        String testCaseStr = placeholderReplace(testCaseAI);
                        //build测试数据
                        TestCaseData testCaseDataObj = TestCaseData.build(JSONObject.parseObject(testCaseStr));
                        Map<String, RunDs> runDsMap = envRunDataStaticMap.get(envName);

                        //执行每阶段
                        for (StageName stageName : stageNameList) {

                            BaseStage bs = pipelineExec.flow.get(stageName);
                            //设置testcase输入
                            if (testCaseDataObj.testCaseInputMap.containsKey(stageName)) {
                                bs.setIputData(testCaseDataObj.testCaseInputMap.get(stageName));
                                bs.setScenarioId(Long.valueOf(testCaseAI.getScenarioId()));
                                bs.setTestcaseId(Long.valueOf(testCaseAI.getId()));
                                //初始化RunData
                                bs.setRunData();
                            }
                            //设置stage之间传递的参数
                            bs.setStageParams(stageParams);
                            int retryNum = getRetryNum(bs.getStageJsonObj());
                            int i = 0;
                            //判断是否需要重试,如果最终结果是
                            do {
                                i++;
                                if (i > 1) {
                                    int n = i - 1;
                                    System.out.println("[失败重试运行中],当前重试第" + n + "次");
                                }
                                bs.beforeExec();
                                bs.exec();
                                bs.afterExec();
                                stageParams = bs.getStageParams();
                                //填充返回结果
                                testRespone = bs.setTestRespone(testRespone);
                            } while (i <= retryNum && !testRespone.getStatus().equals(ResultStatus.SUCCESS));
                            gotTestcaseSnaps.setRetryNum(i-1);

                            //统计每次数据准备花费的消耗
                            try {
                                if (stageName.equals(StageName.prepareData)) {
                                    List<RunData> runDataList = bs.getRunDataList();
                                    if (runDataList.size() > 0) {
                                        for (RunData runData : runDataList) {

                                            if (runData.getRunDsMap().size() == 0) {
                                                continue;
                                            }

                                            for (String dsStr : runData.getRunDsMap().keySet()) {
                                                if (!runDsMap.containsKey(dsStr)) {
                                                    runDsMap.put(dsStr, new RunDs());
                                                }
                                                runDsMap.get(dsStr).setRecordNum(runDsMap.get(dsStr).getRecordNum() + runData.getRunDsMap().get(dsStr).getRecordNum());
                                                runDsMap.get(dsStr).setCost(runDsMap.get(dsStr).getCost() + runData.getRunDsMap().get(dsStr).getCost());
                                            }

                                        }
                                    }
                                }
                            }catch(Exception e){
                                //
                            }
                        }
                        Date end = new Date();
                        if (testCaseAI.getConflictDs() != null) {
                            List<String> dsList = new ArrayList<>();
                            for (String ds : testCaseAI.getConflictDs().keySet()) {
                                dsList.add(ds + ":[" + Toolkit.implode(",", testCaseAI.getConflictDs().get(ds)) + "]");
                            }
                            gotTestcaseSnaps.setConflictDesc(Toolkit.implode(",", dsList));
                        }
                        //用例快照保存
                        gotTestcaseSnaps.setParallel(false);
                        gotTestcaseSnaps.setEnvName(envName);
                        gotTestcaseSnaps.setRunTime((end.getTime() - start.getTime()) / 1000);
                        gotTestcaseSnaps.setRunTimeStr(formatTime((end.getTime() - start.getTime()) / 1000));
                        gotTestcaseSnaps.setStatus(testRespone.getStatus().name());
                        gotTestcaseSnaps.setAppId(testCaseAI.getAppId());
                        gotTestcaseSnaps.setScenarioId(testCaseAI.getScenarioId());
                        gotTestcaseSnaps.setDescription(testCaseAI.getDescription());
                        gotTestcaseSnaps.setLongDescription(testCaseAI.getLongDescription());
                        gotTestcaseSnaps.setName(testCaseAI.getName());
                        gotTestcaseSnaps.setTestcaseId(testCaseAI.getId());
                        gotTestcaseSnaps.setTestreportId(gotReports.getId());
                        gotTestcaseSnaps.setCaseGroup(testCaseAI.getCaseGroup());
                        gotTestcaseSnaps.setTag(testCaseAI.getTag());
                        gotTestcaseSnaps.setVersion(testCaseAI.getVersion());
                        gotTestcaseSnaps.setStatus(testRespone.getStatus().name());
                        gotTestcaseSnaps.setContent(pack(testCaseAI, testRespone));
                        gotTestcaseSnaps.setIsTrunkFlag(testCaseAI.getIsTrunkFlag());
                        testResponeList.add(testRespone);

                        //记录已执行用例数量，计算执行进度
                        if(testRespone.getStatus().name().equals(SUCCESS)){
                            successNum++;
                        }else{
                            failNum++;
                        }


                        if(regAccuracy){
                            //需要获取覆盖
                            try{
                                GotCaseAccuracy accuracy = new GotCaseAccuracy();
                                accuracy.setCaseId(testCaseAI.getId());
                                accuracy.setCollectType("single");
                                accuracy.setExeId(gotReports.getId());
                                accuracy.setCovLine(getCaseAccuracy(testCaseAI));

                                caseAccuracyService.save(accuracy);
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }

                        }

                        JSONObject anlysis = new JSONObject();
                        JSONObject basicReport = new JSONObject();
                        basicReport.put("caseNum",gotReports.getCaseNum());
                        basicReport.put("sucessCaseNum", successNum);
                        basicReport.put("failCaseNum", failNum);
                        anlysis.put("basicReport", basicReport);
                        gotReports.setAnalysis(anlysis.toJSONString());
                        saveReport(gotReports);
                    }
                    catch (Exception e) {
                        logger.error("run case error: " + Toolkit.getErrorStackTrace(e));
                        gotTestcaseSnaps.setStatus(ResultStatus.ERROR.name());

                    }

                    //记录成功的用例,失败的用例重试后如果还失败才会记录
                    if (gotTestcaseSnaps.getStatus().equals(ResultStatus.SUCCESS.name())){
                        System.out.println( "恭喜用例运行通过..."+ ",时间" +df.format(new Date())+"\n" );
                        saveGotTestcaseSnaps(gotTestcaseSnaps);
                    }

                    //失败桶记录
                    if (gotTestcaseSnaps.getStatus().equals(ResultStatus.ERROR.name())){
                        gotTestcaseSnaps.setStatus("ERROR[已在失败桶等待重试]");
                        saveGotTestcaseSnaps(gotTestcaseSnaps);
                        logger.info("===========用例运行失败,进入失败桶等待重试..."+ ",时间" +df.format(new Date())+"===========");

                        if (!failCaseBuckets.containsKey(envName)){
                            testCaseAI.setCaseSnapId(gotTestcaseSnaps.getId());
                            failCaseBuckets.put(envName,new FailCaseBucket());
                            failCaseBuckets.get(envName).setPipelineObj(pipelineObj);
                            failCaseBuckets.get(envName).setGotReports(gotReports);
                            failCaseBuckets.get(envName).setTestCaseAIList(new ArrayList<>());
                            failCaseBuckets.get(envName).getTestCaseAIList().add(testCaseAI);
                        }
                        else {
                            failCaseBuckets.get(envName).getTestCaseAIList().add(testCaseAI);
                        }
                    }
                    gotTestcaseSnapMap.put(String.valueOf(testCaseAI.getId()),gotTestcaseSnaps);

                }

                return testResponeList;
            }
            catch (Exception e) {
                logger.error("运行过程出现异常,请检查!" + Toolkit.getErrorStackTrace(e));
                return null;
            }
        }

        public void run() {
            try {

                testResponeList = runCaseList(serialCaseList, pipelineObj, gotReports, envName);

            } catch (Exception e) {
                System.out.println("[普通回归]用例回归过程异常:"+ Toolkit.getErrorStackTrace(e));
            }
            countDownLatch.countDown();
        }
    }


    public int getRetryNum(JSONObject pipelineBsJson){
        int retryNum = 0;
        if (pipelineBsJson != null && pipelineBsJson.containsKey(PARAMS)){
            JSONObject paramsObj = (JSONObject)pipelineBsJson.get(PARAMS);
            retryNum = paramsObj.containsKey("retryNum") ? (int)paramsObj.get("retryNum") : 0;
        }
        return retryNum;
    }


    /**
     * 函数功能:将用例快照页信息进行聚合
     * @param testcase
     * @param testRespone
     * @return
     */
    public String pack(TestCaseAI testcase, TestRespone testRespone){
        JSONObject content = new JSONObject();

        testRespone.setTestCaseId(testcase.getId());
        content.put(StageName.prepareData.name(),testcase.getPrepareDataSnap());
        content.put(StageName.caseRunStage.name(),testcase.getCaseRunStage());

        //去掉reponse中的log
        try{
            for(String groupName : testRespone.getRunStage().keySet()){
                for(RunData runData : testRespone.getRunStage().get(groupName)){
                    runData.setLog("no log");
                }
            }
        }
        catch (Exception e){

        }

        content.put(StageName.response.name(),testRespone.getRunStage());
        return gson.toJson(content);
    }


    /**
     * 函数功能:对用例列表进行分组
     * @param parallelCaseList
     * @param parallelNum
     * @param i
     * @return
     */
    public List<TestCaseAI> testCaseSplit(List<TestCaseAI> parallelCaseList,int parallelNum, int i){

        List<TestCaseAI> calCaseList = new ArrayList<>();
        int divNum = parallelCaseList.size()/parallelNum;

        //返回为空
        if(parallelCaseList==null || parallelCaseList.size() == 0){
            return calCaseList;
        }
        //如果case数不大于并行数,每次只返回一个
        else if(divNum == 0 ){
            if( parallelCaseList.size() > i){
                calCaseList.add(parallelCaseList.get(i));
            }
        }
        //如果case数大于并行数,每次只返回一个
        else {
            int modNum = parallelCaseList.size()%parallelNum;
            int start = divNum * i;
            int end = parallelCaseList.size();
            //最后一组
            if(i+1 < parallelNum){
                end = start + divNum;
            }
            for(int n = start ; n< end ; n++){
                calCaseList.add(parallelCaseList.get(n));
            }
        }
        return calCaseList;
    }

    /**
     * 函数功能:对用例集根据环境数进行串行和并行分组
     * @param testcaseGroup
     * @param parallelNum
     * @param i
     * @return
     */
    public Map<String, List<TestCaseAI>>  getTestcaseEnvGroup(Map<String, List<TestCaseAI>>  testcaseGroup, int parallelNum,int i){

        Map<String, List<TestCaseAI>> testcaseEnvGroup = new HashMap<>();
        //caseBycase的模式下,并行桶切分就暂时注释
//        testcaseEnvGroup.put(PARALLEL, testCaseSplit(testcaseGroup.get(PARALLEL),parallelNum,i));
        testcaseEnvGroup.put(SERIAL, testCaseSplit(testcaseGroup.get(SERIAL),parallelNum,i));
        return testcaseEnvGroup;
    }



    /**
     * 函数功能:将运行时间格式化
     * @param seconds 01:15:33
     * @return
     */
    public String formatTime(long seconds){

        long temp;
        StringBuffer sb=new StringBuffer();
        temp = seconds/3600;
        sb.append((temp<10)?"0"+temp+":":""+temp+":");

        temp=seconds%3600/60;
        sb.append((temp<10)?"0"+temp+":":""+temp+":");

        temp=seconds%3600%60;
        sb.append((temp<10)?"0"+temp:""+temp);

        return sb.toString();
    }





    public static void main(String[] args)
            throws Exception {



    }
}
