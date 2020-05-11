package com.alibaba.markovdemo.engine.AI.GACaseGenerator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.MultiAIInfo;
import com.alibaba.markovdemo.BO.TestCaseInput;
import com.alibaba.markovdemo.engine.MultiAIPlusController;
import com.alibaba.markovdemo.engine.SingleController;
import com.alibaba.markovdemo.engine.stages.ResultStatus;
import com.alibaba.markovdemo.entity.GotCaseAccuracy;
import com.alibaba.markovdemo.entity.GotCaseGenerateTask;
import com.alibaba.markovdemo.entity.GotReports;
import com.alibaba.markovdemo.entity.GotTestCase;
import com.alibaba.markovdemo.service.CaseAccuracyService;
import com.alibaba.markovdemo.service.CaseGenerateTaskService;
import com.alibaba.markovdemo.service.GotReportsService;
import com.alibaba.markovdemo.service.TestcaseService;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GenerateProcessor {
    @Autowired
    TestcaseService testcaseService;
    @Autowired
    GAOperator operator;
    @Autowired
    CaseAccuracyService caseAccuracyService;
    @Autowired
    MultiAIPlusController multiAIController;
    @Autowired
    GotReportsService gotReportsService;
    @Autowired
    CaseGenerateTaskService caseGenerateTaskService;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SingleController.class);


    public Long startTask(TestCaseInput seedCase, Long scenarioId, HashMap envInfo, String featureConf){
        try{
            //创建生成任务
            GotCaseGenerateTask aiTask = new GotCaseGenerateTask();

            JSONObject conf = JSONObject.parseObject(featureConf);
            JSONObject fields = conf.getJSONObject("field_setting");
            JSONArray ignore = conf.getJSONArray("neg_tesla_key");
            //构建基因库
            Map<String, List<Object>> geneBankMap = buildGeneBank(scenarioId, featureConf, fields, ignore);

            aiTask.setTaskStatus("created");
            aiTask.setSeedCaseList(seedCase.getId().toString());
            aiTask.setFeatureConf(featureConf);
            aiTask.setEnvInfo(envInfo.toString());
            aiTask.setGeneBankSnap(geneBankMap.toString());
            aiTask.setScenarioId(scenarioId);
            caseGenerateTaskService.save(aiTask);


            //启动新线程，开始执行任务
            GenetateTaskThread thread = new GenetateTaskThread(aiTask, geneBankMap, seedCase, ignore, (String)envInfo.get("name") );
            thread.start();

            //返回任务添加状态
            return aiTask.getId();
        }catch(Exception e){
            //
            e.printStackTrace();
            return -1L;
        }
    }

    public Map<String, Object> extractFeatures(TestCaseInput testcase, JSONArray ignoreConf){
        //从用例数据中抽取特征，需由业务定义 by 用户
        Map<String,Object> featureMap = new HashMap<>();
        JSONObject query = getCaseQuery(testcase);

        try{
            for(Object key : query.keySet()){
                if( !isIgnore(key, ignoreConf) ){
                    featureMap.put(key.toString(), query.get(key));
                }
            }
        }catch(Exception e){
            //
        }
        return featureMap;
    }

    public ArrayList<String> buildChromo(Map<String,Object> featureMap){
        ArrayList<String> res = new ArrayList<>();
        for(String key: featureMap.keySet()){
            res.add(key);
        }
        return res;
    }


    public void handleFeatureConf(){
    }

    public boolean isIgnore(Object key, JSONArray ignoreFields){
        //是否配置为需要忽略的特征
        //需要从task的配置字段中判断
        //todo
        if(ignoreFields.contains(key)){
            return true;
        }
        return false;
    }



    public boolean needTerminate(){
        return false;
    }


    public JSONObject getCaseQuery(TestCaseInput testcase){
        String query = testcase.getCaseRunStage().get(0).getData().get(0).input;
        JSONObject queryObject = JSONObject.parseObject(query);
        return queryObject;

    }

    /**
     * 构建基因库，demo中基于同场景的已有用例进行构建
     * @param scenarioId
     * @return
     */
    public Map<String, List<Object>> buildGeneBank(Long scenarioId, String featureConf ,JSONObject fields, JSONArray ignore){

        List<TestCaseInput> casesList = testcaseService.getAllCaseByScenarioId(scenarioId);
        Map<String, List<Object>> geneBankMap = new HashMap<>();


        for(TestCaseInput singleCase : casesList){
            Map<String, Object> featureMap = extractFeatures(singleCase,ignore);
            for(String featureKey : featureMap.keySet()){
                if(geneBankMap.keySet().contains(featureKey)){
                    List<Object> valueList = geneBankMap.get(featureKey);
                    Object value = featureMap.get(featureKey);
                    if(!valueList.contains(value)) {
                        geneBankMap.get(featureKey).add(featureMap.get(featureKey));
                    }
                }else{
                    List<Object> valueList = new ArrayList<>();
                    valueList.add(featureMap.get(featureKey));

                    geneBankMap.put(featureKey, valueList);
                }
            }
        }

        try {
            for (String key : fields.keySet()){
                if(geneBankMap.keySet().contains(key)){
                    List<Object> valueList = geneBankMap.get(key);
                    for(Object value : fields.getJSONArray(key)){
                        if(!valueList.contains(value)){
                            valueList.add(value);
                        }
                    }

                }else{
                    List<Object> valueList = new ArrayList<>();
                    for(Object value : fields.getJSONArray(key)){
                        if(!valueList.contains(value)){
                            valueList.add(value);
                        }
                    }
                    geneBankMap.put(key, valueList);
                }
            }
        }catch(Exception e){

        }

        return geneBankMap;

    }




    public class GenetateTaskThread extends Thread{
        GotCaseGenerateTask aiTask;
        Map<String, List<Object>> geneBankMap;
        Map<String, List<Object>> extralGeneMap = new HashMap<>();
        TestCaseInput seedCase;
        List<String> chromoSeq;
        List<Long> newCaseIdList = new ArrayList<>();
        List<Long> allCaseList = new ArrayList<>();
        Map<String, Integer> historyCov = new HashMap<>();
        List<String> diffLineList = new ArrayList<>();
        JSONArray ignoreConf;
        List<GotTestCase> relatedSeedCase = new ArrayList<>();
        String env;



        public GenetateTaskThread(GotCaseGenerateTask task, Map<String, List<Object>> geneMap, TestCaseInput seed, JSONArray ignore, String env){
            aiTask = task;
            geneBankMap = geneMap;
            seedCase = seed;
            ignoreConf = ignore;
            env = env;
        }

        @Override
        public void run(){
            String covReport = "";
            int covRate = 0;
            try {
                aiTask.setTaskStatus("executing");
                caseGenerateTaskService.update(aiTask);


                Map<String, Object> featureMap = extractFeatures(seedCase, ignoreConf);
                this.chromoSeq = buildChromo(featureMap);

                List<Long> originCases = searchRelateCase(seedCase);
                for(Long caseId : originCases){
                    relatedSeedCase.add(testcaseService.findById(caseId));
                }

                //运行种子及相关用例, todo
                diffLineList = CovMock.getDiffLines();
                runCasePopulation(relatedSeedCase, env);
                for (GotTestCase newCase : relatedSeedCase) {
                    Long caseId = newCase.getId();

                    GotCaseAccuracy accuracyObj = caseAccuracyService.getLastedByCaseId(caseId);
                    String[] caseCov = accuracyObj.getCovLine().split("\n");

                    for (String covLine : caseCov) {
                        if (historyCov.keySet().contains(covLine.trim())) {
                            historyCov.put(covLine, historyCov.get(covLine) + 1);
                        } else {
                            historyCov.put(covLine, 1);
                        }
                    }
                }

                covRate = calCovRate();
                covReport += ("种子及相关用例代码覆盖率:" +covRate +"%\n");
                covReport += ("当前共生成用例"+allCaseList.size()+"个\n");
                covReport += ("有效用例"+newCaseIdList.size()+"个\n");


                //step1：构建初始化种群
                List<Object[]> initPopulationChromo = buildInitPopulation(seedCase.getId());

                //step2：转化为初始种群测试用例，运行获取覆盖
                List<GotTestCase> initPopulationCase = new ArrayList<>();
                for (Object[] chromo : initPopulationChromo) {
                    initPopulationCase.add(decodeCase(chromo));
                }


                int lastPopulationValidNum = 1;
                List<GotTestCase> nextPopulationCase = initPopulationCase;
                List<Object[]> nextPopulation = initPopulationChromo;

                int invalidLoopCount = 0;
                int loopCount = 1;




                while (true) {

                    int validCaseNum = 0;

                    //todo 运行nextPopulationCase
                    runCasePopulation(nextPopulationCase, env);

                    //step3：计算初始种群用例适应度

                    List<Double> fitnessList = new ArrayList<>();
                    for (GotTestCase newCase : nextPopulationCase) {
                        allCaseList.add(newCase.getId());
                        Long caseId = newCase.getId();

                        GotCaseAccuracy accuracyObj = caseAccuracyService.getLastedByCaseId(caseId);
                        String[] caseCov = accuracyObj.getCovLine().split("\n");

                        for (String covLine : caseCov) {
                            if (historyCov.keySet().contains(covLine.trim())) {
                                historyCov.put(covLine, historyCov.get(covLine) + 1);
                            } else {
                                historyCov.put(covLine, 1);
                                if (!newCaseIdList.contains(caseId)) {
                                    newCaseIdList.add(caseId);
                                    validCaseNum += 1;
                                }
                            }
                        }
                    }

                    for(GotTestCase newCase : nextPopulationCase){
                        Long caseId = newCase.getId();
                        GotCaseAccuracy accuracyObj = caseAccuracyService.getLastedByCaseId(caseId);
                        String[] caseCov = accuracyObj.getCovLine().split("\n");

                        Double fitness = operator.fiteness(caseCov, historyCov);
                        fitnessList.add(fitness);
                    }

                    //step4：计算代码覆盖率
                    covReport += ("当前循环第"+loopCount+"次" + "\n");
                    loopCount++;
                    covRate = calCovRate();
                    covReport += ("代码覆盖率:" + covRate +"%\n");
                    covReport += ("当前共生成用例"+allCaseList.size()+"个\n");
                    covReport += ("有效用例"+newCaseIdList.size()+"个\n");

                    if (validCaseNum == 0) {
                        //break loop
                        if(invalidLoopCount >= 5) {
                            break;
                        }else{
                            invalidLoopCount++;
                        }
                    }else {
                        invalidLoopCount = 0;
                    }

                    lastPopulationValidNum = validCaseNum;

                    //step5：选择
                    nextPopulation = operator.selection(nextPopulation, fitnessList);

                    //step6：杂交
                    nextPopulation = operator.crossover(nextPopulation);
                    nextPopulation = operator.mutation(nextPopulation, geneBankMap, chromoSeq);

                    //step7：生成新一代种群用例
                    nextPopulationCase = new ArrayList<>();
                    for (Object[] chromo : nextPopulation) {
                        nextPopulationCase.add(decodeCase(chromo));
                    }

                }
                logger.info(covReport);
            }catch(Exception e){
                logger.info(e.getMessage());
            }

            JSONArray validCaseIds = new JSONArray();
            for(Long caseId : allCaseList){
                if(newCaseIdList.contains(caseId)){
                    validCaseIds.add(caseId);
                    continue;
                }else{
                    testcaseService.deleteTestCase(caseId);

                }
            }

            int count = 0;
            try {
                for (Long caseId : newCaseIdList) {
                    GotTestCase testCase = testcaseService.findById(caseId);
                    String desp = testCase.getDescription();
                    testCase.setDescription(desp + "--后代用例" + (count++));
                    testcaseService.update(testCase);
                }
            }catch(Exception e){
                System.out.print(e.getMessage());
            }

            JSONObject res = new JSONObject();
            res.put("all_num", allCaseList.size());
            res.put("valid_ids", validCaseIds);
            res.put("detail", covReport);
            res.put("cov_rate", covRate);


            try {
                aiTask.setTaskStatus("success");
                aiTask.setTaskResult(res.toJSONString());
                caseGenerateTaskService.update(aiTask);
            }catch(Exception e){
                System.out.print(e.getMessage());
            }


        }


        public List<Long> searchRelateCase(TestCaseInput seedCase){
            List<Long> caseRes = new ArrayList<>();
            List<TestCaseInput> allList = testcaseService.getAllCaseByScenarioId(seedCase.getScenarioId());

            for(TestCaseInput singleCase : allList){
                Map<String, Object> features = extractFeatures(singleCase, ignoreConf);

                int commonFeatureNum = 0;
                for(String key : features.keySet()){
                    if(chromoSeq.contains(key)){
                        commonFeatureNum++;
                    }
                }

                if((commonFeatureNum*1.0/ chromoSeq.size()) > 0.5){ // 如果有一半以上的相同特征，则属于相似关联用例
                    caseRes.add(singleCase.getId());
                }
            }
            return caseRes;
        }


        /**
         * 构建初始种群，根据库中已有的用例返回染色体列表
         * @param seedCaseId
         * @return
         */
        public List<Object[]> buildInitPopulation( Long seedCaseId){
            List<Object[]> population = new ArrayList<>();
            Object[] seedChromo = new Object[chromoSeq.size()];;

            //用例库中已有用例作为初始用例
            for(GotTestCase singleCase : relatedSeedCase){

                Map<String, Object> featureMap = extractFeatures(testcaseService.getTestCaseById(singleCase.getId()) , ignoreConf);
                Object[] chromo = new Object[chromoSeq.size()];
                for(int i = 0 ; i < chromoSeq.size(); i++){
                    Object value = "";
                    if(featureMap.get(chromoSeq.get(i)) != null){
                        value = featureMap.get(chromoSeq.get(i));
                    }
                    chromo[i] = value;
                }
                Long id = singleCase.getId();
                if(singleCase.getId().equals(seedCaseId)){
                    seedChromo = chromo.clone();
                }

                population.add(chromo);
            }

            //在种子用例基础上，修改字段为用户配置的参数值，构建初始用例
            //for(String geneKey : extralGeneMap.keySet()){
            for(String geneKey : geneBankMap.keySet()){
                int keyIndex = chromoSeq.indexOf(geneKey);
                for(Object geneValue : geneBankMap.get(geneKey)){
                    Object[] chromo = seedChromo.clone();

                    chromo[keyIndex] = geneValue;

                    population.add(chromo);
                }
            }

            return population;
        }

        /**
         * 从基因型解码为新用例
         * @param chromo
         * @return
         */
        public GotTestCase decodeCase(Object[] chromo){
            //解码基因型形成新case
            try{
                JSONObject seedQuery = getCaseQuery(seedCase);
                for(int i = 0; i < chromoSeq.size(); i++){
                    String key = chromoSeq.get(i);
                    Object value = chromo[i];

                    seedQuery.put(key, value);
                }

                TestCaseInput newCase = (TestCaseInput) SerializationUtils.clone(seedCase);

                newCase.getCaseRunStage().get(0).getData().get(0).input = seedQuery.toJSONString();

                newCase.setIsVisible(1);

                return testcaseService.addNewTestCase(newCase);
            }catch(Exception e){
                return null;
            }
        }

        /**
         * 构建用例运行任务，获取每个case覆盖的被测代码
         * @param casePopulation
         * @return
         */
        public void runCasePopulation(List<GotTestCase> casePopulation, String env){
            MultiAIInfo multiAIInfo = new MultiAIInfo();
            String caseIds = "";
            for(GotTestCase testCase : casePopulation){
                caseIds += (testCase.getId()+",");
            }
            if(caseIds.endsWith(",")){
                caseIds = caseIds.substring(0, caseIds.length()-1);
            }
            multiAIInfo.setCaseIds(caseIds);
            multiAIInfo.setTaskName("用例生成-运行任务");
            multiAIInfo.setTaskType(0);
            multiAIInfo.setCaseType("0");
            multiAIInfo.setExecId(""+(new Date()).getTime());
            multiAIInfo.setScenarioId(seedCase.getScenarioId());
            multiAIInfo.setRegAccuracy(true);
            multiAIInfo.setCreator("markov AI");
            multiAIInfo.setRunType("AI");
            multiAIInfo.setAppId(1L);
            multiAIInfo.setCaseTemplate("C++");
            multiAIInfo.setBranchName("master");
            multiAIInfo.setRegAccuracy(true);

            //mock

            Map<String, List<String>> envInfo = new HashMap<>();
            List<String> dockers = new ArrayList<>();
            dockers.add("mock docker");
            envInfo.put(env,dockers);
            multiAIInfo.setEnvInfo(envInfo);


            Long reportId = multiAIController.runIntelligent(multiAIInfo);
            while(true){
                GotReports report = gotReportsService.findById(reportId);
                if(report.getStatus().equals(ResultStatus.SUCCESS.name()) || report.getStatus().equals(ResultStatus.ERROR.name()) || report.getStatus().equals(ResultStatus.FAILURE.name())){
                    break;
                }else{
                    try {
                        Thread.sleep(1000);
                    }catch(Exception e){
                        continue;
                    }
                }
            }
        }


        public int calCovRate(){
            int covRate = 0; //百分比整数值
            try{
                int allLineNum = diffLineList.size();
                int covedNum = historyCov.keySet().size();

                covRate = (int)((covedNum*1.0)/allLineNum * 100);

            }catch(Exception e){
                //
            }
            return covRate;
        }

    }

}
