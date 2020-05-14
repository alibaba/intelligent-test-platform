package com.alibaba.markovdemo.engine;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.*;

import com.alibaba.markovdemo.engine.AI.FMM.GenerateDictionary;
import com.alibaba.markovdemo.engine.AI.FMM.Segmentation;
import com.alibaba.markovdemo.engine.util.Toolkit;
import com.alibaba.markovdemo.entity.GotFeaturesPool;
import com.alibaba.markovdemo.entity.GotTestCase;
import com.alibaba.markovdemo.service.GotFeaturesPoolService;
import com.alibaba.markovdemo.service.TestcaseService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


@Service
public class CaseFactoryProcessor {

    private static final Logger Logger = LoggerFactory.getLogger(CaseFactoryProcessor.class);

    @Autowired
    private GotFeaturesPoolService gotFeaturesPoolService;
    @Autowired
    private TestcaseService testcaseService;
    Gson gson = new Gson();
    Segmentation seg;


    /**
     * 函数功能:处理FMM处理后的特征
     * 过滤策略:单个字符不算特征,具体的词才算业务特征
     * @param input
     * @return
     */
    public List<String> getFMMFeatures(String input){

        List<String> featureList = new ArrayList<>();
        String str = seg.Fmm(input);
        String[] list = str.split("/");

        for (String split : list){

            if (split.length()>1){
                featureList.add(split);
            }
        }
        return featureList;

    }

    /**
     * 函数功能:处理query和数据源的特征
     * 过滤策略:数据源名 ,query用户可根据业务需求自行抽取,本次暂不实现
     * @param input
     * @return
     */
    public List<String> getQueryAndDataFeatures(String input){

        List<String> featureList = new ArrayList<>();

        try{

            JSONObject obj =  JSONObject.parseObject(input);
            List<JSONObject> list = (List<JSONObject>) obj.get("prepareData");

            for (JSONObject jObj : list){

                if (jObj.size()>0){
                    for (String dsName : jObj.keySet()){
                        List<JSONObject> dsList = (List<JSONObject>) jObj.get(dsName);
                        for (JSONObject dsObj : dsList){
                            String name = dsObj.getString("dsName");
                            if(!featureList.contains(name)){
                                featureList.add(name);
                            }
                        }

                    }
                }
            }

        }catch (Exception e){
            //格式异常,skip..
        }


        return featureList;

    }


    /**
     * 特征抽取策略:
     * 1.用例详细描述采用FMM分词.
     * 2.分组单独作为特征.
     * 3.数据源名为特征
     * 其他:其他维度特征用户可根据业务自行补充
     * @param gotTestCase
     * @return
     */
    public List<String>  extractFeatures(GotTestCase gotTestCase){

        //用例特征
        List<String> featureList = new ArrayList<>();
        //用例名进行FMM分词
        List<String>  descriptionFeatures = getFMMFeatures(gotTestCase.getDescription());
        //用例描述FMM分词
        List<String> longDescriptionFeatures = getFMMFeatures(gotTestCase.getLongDescription());
        //分组特征
        String caseGroupFeatures = gotTestCase.getCaseGroup();
        //数据源和query特征
        List<String> queryAndDataFeatures = getQueryAndDataFeatures(gotTestCase.getContent());

        if (caseGroupFeatures!=null && !"".equals(caseGroupFeatures)){
            featureList.add(caseGroupFeatures);
        }
        featureList.addAll(descriptionFeatures);
        featureList.addAll(longDescriptionFeatures);
        featureList.addAll(queryAndDataFeatures);

        return featureList;
    }

    /**
     * 函数功能:用例特征并入特征池
     * @param featurePool
     * @param featuresList
     */
    public void putInFeatures(Map<String,Integer>  featurePool,List<String> featuresList){

        if (featuresList.size()>0){
            for (String features :  featuresList){

                if (!featurePool.containsKey(features)){
                    featurePool.put(features,1);
                }
                else{
                    featurePool.put(features,featurePool.get(features)+1);
                }
            }
        }

    }


    /**
     * 函数功能:加载字典,分词使用
     * @throws IOException
     */
    public void loadDict() throws IOException {
        //加载词库
        String filename = System.getProperty("user.dir") + "/src/main/java/com/alibaba/markovdemo/engine/AI/FMM/doc/dict.txt";
        HashMap hm = new HashMap();
        HashMap len = new HashMap();
        GenerateDictionary genDic = new GenerateDictionary();
        genDic.genHashDic(filename, hm, len);
        seg = new Segmentation(hm, len);
    }



    /**
     * 函数功能:特征抽取
     * 1.使用FMM算法对用例描述进行分词(用户可根据需要自行采用其他分词算法,本篇采用了经典FMM分词算法)
     * 2.对数据源/发送query进行关键字段抽取
     * 3.用例维度/场景维度的特征入库
     * @param scenarioId
     */
    public void callFeaturesExtract(Long scenarioId) throws IOException {


        Map<String,Integer> featurePool = new HashMap<>();
        GotFeaturesPool  gotFeaturesPool = new GotFeaturesPool();

        //获取所有的用例的业务数据
        List<GotTestCase> gotTestCaseList = testcaseService.findByScenarioId(scenarioId);
        //加载词库
        loadDict();

        //遍历用例库
        for (GotTestCase  gotTestCase : gotTestCaseList){
            try{
                //抽取用例特征集
                List<String> featuresList = extractFeatures(gotTestCase);
                //特征池合并
                putInFeatures(featurePool,featuresList);
                //单个用例特征入库
                gotTestCase.setFeatures(gson.toJson(featurePool));
                testcaseService.update(gotTestCase);
                System.out.print("用例id:"+ gotTestCase.getId() +"特征抽取完成!\n");
            }
            catch (Exception e){
                System.out.print("用例id:"+ gotTestCase.getId() +"特征抽取过程出错,请注意!\n");
                e.printStackTrace();
            }

        }
        //特征池入库
        gotFeaturesPool.setId(scenarioId);
        gotFeaturesPool.setScenarioId(scenarioId);
        gotFeaturesPool.setFeatures(gson.toJson(featurePool));
        gotFeaturesPoolService.insert(gotFeaturesPool);
        System.out.print("特征池入库完成!\n");
    }


    /**
     * 函数功能:异步执行全量用例特征抽取
     * @param scenarioId
     */
    public void featuresExtract(Long scenarioId) throws IOException {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Logger.info("提交特征抽取任务");
                    callFeaturesExtract( scenarioId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    // 升序比较器
    Comparator<Map.Entry<String, Double>> valueComparator = new Comparator<Map.Entry<String,Double>>() {
        @Override
        public int compare(Map.Entry<String, Double> o1,
                           Map.Entry<String, Double> o2) {
            // TODO Auto-generated method stub
            return (int) (o1.getValue()-o2.getValue());
        }
    };

    // 升序比较器
    Comparator<Map.Entry<GotTestCase, Double>> valueCaseComparator = new Comparator<Map.Entry<GotTestCase,Double>>() {
        @Override
        public int compare(Map.Entry<GotTestCase, Double> o1,
                           Map.Entry<GotTestCase, Double> o2) {
            // TODO Auto-generated method stub
            return (int) (o1.getValue()-o2.getValue());
        }
    };


    /**
     * 函数功能:获取特征列表
     * 策略:按照特征的出现频率降序返回
     * @param scenarioId
     * @return
     */
    public List<String> getFeaturesList(Long scenarioId){

        List<String> featureList = new ArrayList<>();

        try{
            List<String> sortList = new ArrayList<>();
            GotFeaturesPool gotFeaturesPool = gotFeaturesPoolService.findByScenarioId(scenarioId);
            // map转换成list进行排序
            Map<String, Double> map = new HashMap<>();
            map = gson.fromJson(gotFeaturesPool.getFeatures(), map.getClass());

            List<Map.Entry<String, Double>> list = new ArrayList<>(map.entrySet());
            // 排序
            Collections.sort(list,valueComparator);
            for (Map.Entry<String, Double> entry : list) {
                sortList.add(entry.getKey());
            }
            return sortList;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return featureList;

    }


    /**
     * 函数功能:匹配特征命中数目
     * @param feature
     * @param featuresList
     * @return
     */
    public Integer getMatchFeatureNum(String feature, List<String> featuresList){

        JSONObject jList= JSONObject.parseObject(feature);
        List<String> list = new ArrayList<>();
        for (String str :jList.keySet()){
            list.add(str);
        }
        //交集处理
        list.retainAll(featuresList);

        return list.size();

    }

    /**
     * 函数功能:特征匹配
     * 返回相似度TopN用例集,此处采用简单的特征值命中策略,即交集特征越多,相似度越高
     * @param featureInfo
     * @return
     */
    public JSONObject featuresMatch(FeatureInfo featureInfo) throws IOException {

        Date start = new Date();
        JSONObject res = new JSONObject();
        Map<GotTestCase,Double> map = new HashMap<>();
        List<TestCaseInput> sortList = new ArrayList<>();

        //对业务描述进行分词,并合并入匹配特征列表
        if(featureInfo.getCaseDesc()!=null){
            loadDict();
            List<String>  descriptionFeatures = getFMMFeatures(featureInfo.getCaseDesc());
            featureInfo.getFeatureList().addAll(descriptionFeatures);
        }

        List<GotTestCase> gotTestCaseList = testcaseService.findByScenarioId(featureInfo.getScenarioId());
        //遍历用例库
        for (GotTestCase  gotTestCase : gotTestCaseList){
            try{
                //命中特征的数目
                Integer num = getMatchFeatureNum(gotTestCase.getFeatures(),featureInfo.getFeatureList());
                map.put(gotTestCase, Double.valueOf(num));
                System.out.print("用例id:"+ gotTestCase.getId() +"特征匹配完成!\n");
            }
            catch (Exception e){
                System.out.print("用例id:"+ gotTestCase.getId() +"特征匹配过程出错,请注意!\n");
                map.put(gotTestCase, (double) 0);
            }
        }

        List<Map.Entry<GotTestCase, Double>> list = new ArrayList<>(map.entrySet());
        // 降序排序
        Collections.sort(list,valueCaseComparator);
        int cnt = 0;
        for (Map.Entry<GotTestCase, Double> entry : list) {
            cnt++;
            if (cnt<=featureInfo.getTopN()){
                sortList.add(TestcaseService.formatTestCaseInput(entry.getKey()));
            }
        }
        Date end = new Date();

        res.put("caseList",sortList);
        res.put("caseCount",sortList.size());
        res.put("interval",end.getTime() - start.getTime());
        res.put("recommenderName","FMM算法");

        return res;
    }

    //====以下是用例膨胀===

    /**
     * 函数功能:获取可进行参数组合的Feature集
     * 注意:demo中针对请求串中的tesla参数进行用例膨胀,实际使用中,用户可以根据自己的业务自行改造.
     * @param testCaseInput
     * @return
     */
    public JSONArray getExpandFeatures( TestCaseInput testCaseInput){

        //对于符合query格式可进行用例膨胀
        try{
            //query
            String query = testCaseInput.getCaseRunStage().get(0).getData().get(0).getInput();
            JSONObject inputObj =  JSONObject.parseObject(query);
            JSONObject paramsObj = (JSONObject) inputObj.get("param_manager");
            JSONObject teslaObj = (JSONObject) paramsObj.get("expand_param");
            return (JSONArray) teslaObj.get("key_value_list");
        }
        catch (Exception e){
            return new JSONArray();
        }

    }


    /**
     * 函数功能:获取膨胀组合
     * 此处的膨胀组合是demo使用,真实场景用户可进行自定义,或者是自行训练出来.
     * @return
     */
    public JSONObject initFeatureCombination(){

        Map<String,String> featureCombination = new HashMap<>();
        //bool类型
        featureCombination.put("布尔类型","<true>,<false>");
        //Int32型组合
        featureCombination.put("Int32型组合","<0>,<-2147483648>,<2147483647>,<100>,<-100>");
        //Int型组合
        featureCombination.put("Int16型组合","<0>,<-32768>,<32767>,<100>,<-100>");
        //Long型组合,边界组合
        featureCombination.put("Long型组合","<0>,<-18446744073709551616>,<18446744073709551615>,<100>,<-100>");
        //字符串组合,乱码
        featureCombination.put("字符串组合","<nomalStr>,<?/@#%^^&&>");

        //以下用户可自行引入关于具体业务特征抽取算法
        featureCombination.put("线上抽取特征组合","<1/0/1>,<1/0/2>,<2/0/1>,<3/0/1>");
        featureCombination.put("FMM算法抽取特征组合","<猜你喜欢>,<直通车>,<定向>,<pid>");


        List<JSONObject> list = new ArrayList<>();
        for (String key : featureCombination.keySet()){

            JSONObject kv = new JSONObject();
            kv.put("key",key);
            kv.put("value",featureCombination.get(key));
            list.add(kv);
        }
        JSONObject res = new JSONObject();
        res.put("featureInitMap",featureCombination);
        res.put("featureInitList",list);

        return res;
    }

    /**
     * 函数功能:非递归计算所有膨胀组合
     * @param inputList 所有数组的列表
     * */
    public List<List<String>> calculateCombination(List<List<String>> inputList) {
        List<Integer> combination = new ArrayList<>();
        List<List<String>> outputList = new ArrayList<>();
        List<String> oneOutput ;
        int n=inputList.size();
        for (int i = 0; i < n; i++) {
            combination.add(0);
        }
        int i=0;
        boolean isContinue=false;
        do{
            oneOutput = new ArrayList<>();
            //添加一次循环生成的膨胀组合
            for (int j = 0; j < n; j++) {
                oneOutput.add(inputList.get(j).get(combination.get(j)));
            }
            outputList.add(oneOutput);

            i++;
            combination.set(n-1, i);
            for (int j = n-1; j >= 0; j--) {
                if (combination.get(j)>=inputList.get(j).size()) {
                    combination.set(j, 0);
                    i=0;
                    if (j-1>=0) {
                        combination.set(j-1, combination.get(j-1)+1);
                    }
                }
            }
            isContinue=false;
            for (Integer integer : combination) {
                if (integer != 0) {
                    isContinue=true;
                }
            }
        }while (isContinue);

        return outputList;
    }

    /**
     * 函数功能:将需要膨胀的kv集合进行切分
     * @param expandkvList
     * @return
     */
    List<List<String>> getExpandKvGroupList(List<Expandkv> expandkvList){

        //首先扫描,expandValue是分隔,如果为null,""
        List<List<String>> teslaCalList = new ArrayList<>();

        //转化为参数组
        for(Expandkv expandkv : expandkvList){
            String expandValue = expandkv.getExpandValue();
            String key = expandkv.getKey();
            List<String> teslaCal = new ArrayList<>();

            try{
                if("".equals(expandValue) || expandValue == null){
                    continue;
                }
                String[] valList = expandValue.split(">,<");
                for(String val : valList){
                    val = val.replace("<","");
                    val = val.replace(">","");
                    String kvStr = key + ":" + val;
                    teslaCal.add(kvStr);
                }
                teslaCalList.add(teslaCal);
            }catch (Exception e){
                //不符合格式的则忽略.
            }

        }

        return calculateCombination(teslaCalList);

    }
    /**
     * 函数功能:获取膨胀用例列表
     * 膨胀策略:demo中采用参数进行叉乘组合的方式进行膨胀,更多复杂的策略用户可根据业务进行添加
     * @return
     */
    public List<TestCaseInput> getExpandCases(ExpandInfo expandInfo){

        List<TestCaseInput> caseList = new ArrayList<>();
        TestCaseInput testCase = expandInfo.getTestCase();
        List<Expandkv> expandKvList = expandInfo.getExpandKvList();
        List<List<String>> expandKvGroupList = getExpandKvGroupList(expandKvList);
        //遍历膨胀参数列表
        for (List<String> oneExpandKvGroup : expandKvGroupList){
            TestCaseInput expandcase = (TestCaseInput) testCase.clone();
            //修改desc
            String caseDesc = expandcase.getDescription() + ".[用例膨胀组合]:" + Toolkit.implode("|",oneExpandKvGroup);
            expandcase.setDescription(caseDesc);
            //修改input
            String erpc = expandcase.getCaseRunStage().get(0).getData().get(0).getInput();
            JSONObject erpcObj = (JSONObject) JSONObject.parse(erpc);
            JSONObject paramsObj = (JSONObject) erpcObj.get("param_manager");
            JSONObject teslaObj = (JSONObject) paramsObj.get("expand_param");

            JSONArray kvObjListclone = new JSONArray();
            for(String onekv : oneExpandKvGroup){
                String[] kv = onekv.split(":");
                if(kv.length == 2){
                    JSONObject kvObj = new JSONObject();
                    kvObj.put("key",kv[0]);
                    kvObj.put("value",kv[1]);
                    kvObjListclone.add(kvObj);
                }
            }
            teslaObj.put("key_value_list",kvObjListclone);
            expandcase.getCaseRunStage().get(0).getData().get(0).setInput(erpcObj.toString());
            caseList.add(expandcase);
        }

        return caseList;
    }


    /**
     * 函数功能:在用户选择了批量膨胀的用例集后,可进行批量保存用例
     * @param data
     * @return
     */
    public AjaxResult saveCaseList( MultiCase data){
        try {

            int i = 0;

            //遍历待保存的用例集
            for(TestCaseInput testcase :data.getCaseList()){

                testcase.setId(null);

                //如果是用例膨胀,直接保存入库
                if("expand".equals(data.getMultiInfo().getType())){
                    testcaseService.saveTestCase(testcase);
                }
                //如果是推荐用例,可做改造后在保存
                if("recommend".equals(data.getMultiInfo().getType())){
                    //修改desc
                    String caseDesc = data.getMultiInfo().getCaseDesc();
                    i++;
                    caseDesc =  caseDesc + "[系统推荐" + i + "]";
                    testcase.setDescription(caseDesc);
                    testcase.setTag("系统推荐");

                    try{
                        //此处是根据可膨胀的字段进行mock,使用方可自行定义
                        String erpc = testcase.getCaseRunStage().get(0).getData().get(0).getInput();
                        JSONObject erpcObj = (JSONObject) JSONObject.parse(erpc);
                        JSONObject paramsObj = (JSONObject) erpcObj.get("param_manager");
                        JSONObject teslaObj = (JSONObject) paramsObj.get("expand_param");
                        JSONArray kvObjList = (JSONArray) teslaObj.get("key_value_list");
                        kvObjList.clear();

                        for(Expandkv onekv : data.getMultiInfo().getExpandkvist()){
                            JSONObject kvObj = new JSONObject();
                            if(onekv.getKey()!= null && onekv.getValue() != null){
                                kvObj.put("key",onekv.getKey());
                                kvObj.put("value",onekv.getValue());
                                kvObjList.add(kvObj);
                            }

                        }
                        testcase.getCaseRunStage().get(0).getData().get(0).setInput(erpcObj.toString());
                        testcaseService.saveTestCase(testcase);
                    }
                    catch (Exception e) {
                        //如果请求不符合规范,则不再改造请求串,直接入库保存.
                        testcaseService.saveTestCase(testcase);
                    }
                }

            }
            return AjaxResult.succResult("save ok");
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }

    }

}
