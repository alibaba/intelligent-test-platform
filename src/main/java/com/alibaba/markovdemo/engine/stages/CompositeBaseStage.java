package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.alibaba.markovdemo.BO.TestRespone;
import com.alibaba.markovdemo.engine.plugins.IPlugin;
import com.alibaba.markovdemo.engine.plugins.PluginFactory;
import com.alibaba.markovdemo.engine.plugins.PluginLayout;
import com.google.gson.Gson;
import org.codehaus.groovy.util.ListHashMap;

import java.util.*;


/**
 * 功能:复杂流程处理
 * 说明:可处理用例执行阶段的发送请求=>获取结果=>校验结果的复杂流程
 */
public class CompositeBaseStage extends BaseStage {


    static final String GroupNameKey = "group_name";

    static final String PARAMS = "params";

    public HashMap<String, LinkedList<BaseStage>> RunningStageGroups = new HashMap<String, LinkedList<BaseStage>>();

    public HashMap<String, JSONArray> templates = new HashMap<>();

    public HashMap<String, JSONArray> expectTemplates = new HashMap<>();

    public String StageInput;

    //该变量是CompositeBaseStage阶段即使存储最新的runData
    public RunData stageRunData = new RunData();

    public JSONArray stageJsonArray;

    ListHashMap<String,List<String>> appLogList = new ListHashMap<>();

    //每组执行结果
    HashMap<String, List<RunData>> groupResultMap = new HashMap<>();


    public HashMap<String, JSONArray> getTemplates() {
        return templates;
    }

    public HashMap<String, JSONArray> getExpectTemplates() {
        return expectTemplates;
    }

    public void addSubStage() {

    }

    /**
     * 函数功能:复杂阶段,每个赋值都要在以下分阶段赋值
     * @param stageParams
     */
    @Override
    public void setStageParams(Map<StageName, Object> stageParams) {
        this.stageParams = stageParams;
        //初始化caseRun的子阶段runData
        if (this.RunningStageGroups.size() > 0) {
            for (String groupName : this.RunningStageGroups.keySet()) {
                for (BaseStage stage : this.RunningStageGroups.get(groupName)) {
                    stage.setStageParams(stageParams);
                }
            }
        }
    }

    @Override
    public void setIputData(Object inputObj){
        this.inputObj = inputObj;
    }

    /**
     * 函数功能:整合最终testRespone,判定是否通过
     * @param testRespone
     * @return
     */
    @Override
    public TestRespone setTestRespone(TestRespone testRespone){

        //获取当前阶段的log
        testRespone.getLogStage().putAll((Map<? extends StageName, ? extends ListHashMap<String, String>>) mergeResponeLog(getLogCollection()).clone());

        boolean resultFlag = true;
        //set的结果返回
        HashMap<String, List<RunData>> groupResultMapTmp =(HashMap<String, List<RunData>>) groupResultMap.clone();
        testRespone.setRunStage(groupResultMapTmp);

        //判断本次是否能通过
        for (String groupName : groupResultMapTmp.keySet()){
            for (RunData runData : groupResultMapTmp.get(groupName)){
                //如果判定某组data失败,则case fail
                if (runData.getResult() == 0){
                    resultFlag = false;
                    break;
                }
            }
        }
        //判断校验最终是否通过
        if (resultFlag == false){
            testRespone.setStatus(ResultStatus.ERROR);

        }
        else{
            testRespone.setStatus(ResultStatus.SUCCESS);
        }
        return testRespone;
    }


    /**
     * 函数功能:对所有子阶段的RunData都赋值
     * @param data
     * @param index
     */
    public void setRunData(RunningDataInfo data, int index){

        //初始化caseRun的runData
        runData.setResult(1);
        runData.setInput(data.getInput());
        runData.setExpect(data.getExpect());
        data.setCalFieldsConfig(this.runData.getCalFieldsConfig());

        //初始化caseRun的子阶段runData
        if (this.RunningStageGroups.size() > 0) {
            for (String groupName : this.RunningStageGroups.keySet()) {
                for (BaseStage stage : this.RunningStageGroups.get(groupName)) {
                    stage.getRunData().setInput(data.getInput());
                    stage.getRunData().setExpect(data.getExpect());
                    stage.getRunData().getParams().put("index",index);
                    stage.getRunData().setCalFieldsConfig(this.runData.getCalFieldsConfig());
                    stage.setGroupName(groupName);
                }
            }
        }
    }

    /**
     * 函数功能:收集caseRun阶段中每个子阶段的log
     * @return
     */
    @Override
    public List<LogInfo> getLogCollection(){

        // 非简单类型stage， 有group分组的sub stage
        if (this.RunningStageGroups.size() > 0) {
            //添加所有的log
            for (String groupName : this.RunningStageGroups.keySet()) {
                for (BaseStage stage : this.RunningStageGroups.get(groupName)) {
                    logCollection.addAll(stage.getLogCollection());
                }
            }
        } else {
            logCollection.addAll(super.getLogCollection());
        }

        return logCollection;
    }

    /**
     * 函数功能:对阶段性RunData进行赋值
     * @param runData
     */
    public void setStageRunData(RunData runData){


        if (runData.getInput() != null){
            stageRunData.setInput(runData.getInput());
        }
        if (runData.getOutput() != null){
            stageRunData.setOutput(runData.getOutput());
        }
        if (runData.getActual() != null){
            stageRunData.setActual(runData.getActual());
        }

        stageRunData.setResult(runData.getResult());

        if (runData.getExpect() != null){
            stageRunData.setExpect(runData.getExpect());
        }
        if (runData.getLog() != null){
            stageRunData.setLog(runData.getLog());
        }
        if (runData.getParams() != null){
            stageRunData.setParams(runData.getParams());
        }

    }


    /**
     * 函数功能:执行send=>get=>check的用例执行阶段
     */
    @Override
    public void exec() {

        LinkedHashMap<String, LinkedList<RunningDataInfo>> inputMap = (LinkedHashMap<String, LinkedList<RunningDataInfo>>)this.inputObj;
        RunData runCheckData = new RunData();

        //执行
        for (String groupName : inputMap.keySet()) {
            List<RunData> runCheckDataList = new ArrayList<>();
            int dataListIndex = 1;
            //每组中有多组input/expect
            for (RunningDataInfo runData : inputMap.get(groupName)) {
                //将输入期望传入
                setRunData(runData, dataListIndex);
                //执行run流程,这会带出所有的log和结果
                //设置初始输入
                StageInput = runData.getInput();
                //内部阶段输入
                String innerInput = runData.getInput();

                if (this.RunningStageGroups.containsKey(groupName)){
                    // 依次执行send->get->check三个阶段
                    for (BaseStage stage : this.RunningStageGroups.get(groupName)) {
                        //每个阶段的输出作为下个阶段的输入
                        stage.getRunData().setInput(innerInput);
                        //加这行的原因是,当某个阶段跳过时,默认输入输出是一样的
                        stage.getRunData().setOutput(innerInput);
                        stage.getRunData().setCalFieldsConfig(runData.getCalFieldsConfig());
                        stage.getRunData().setScenarioId(runData.getScenarioId());
                        stage.getRunData().setTestcaseId(runData.getTestcaseId());
                        stage.exec();
                        //获取当前最新的RunData
                        setStageRunData(stage.getRunData());

                        //获取group执行完的checkresult
                        if (StageName.checkStage.equals(stage.getName())){
                            RunData stageRunDataNew = new RunData();
                            stageRunDataNew.setActual(stageRunData.getActual());
                            stageRunDataNew.setExpect(stageRunData.getExpect());
                            stageRunDataNew.setLog(stageRunData.getLog());
                            stageRunDataNew.setInput(StageInput);
                            stageRunDataNew.setResult(stageRunData.getResult());
                            stageRunDataNew.setParams(stageRunData.getParams());
                            stageRunDataNew.setOutput(stageRunData.getOutput());
                            //判断checkList中是否有插件异常
                            if (stage.getRunDataList().size() > 0 ){
                                for (RunData runDatatmp : stage.getRunDataList()){
                                    if (runDatatmp.getResult() == 0){
                                        stageRunDataNew.setResult(0);
                                        break;
                                    }
                                }
                            }
                            runCheckDataList.add(stageRunDataNew);
                        }
                        //如果当前阶段无plugin,则input取再上个阶段的input
                        if (stage.getRunData().getOutput() != null) {
                            innerInput = stage.getRunData().getOutput();
                        }
                    }
                    if (runCheckDataList.size() > 0 ){
                        groupResultMap.put(groupName, runCheckDataList);
                    }
                    dataListIndex++;
                }
            }
        }
    }



    /**
     * 函数功能:获取check阶段的所有runData
     * @return
     */
    public RunData getResult() {

        Gson gosn = new Gson();
        RunData runData = new RunData();
        List<RunData> runDataList = null;
        // 非简单类型stage， 有group分组的sub stage
        if (this.RunningStageGroups.size() > 0) {
            runDataList = new ArrayList<>();
            for (String groupName : this.RunningStageGroups.keySet()) {

                for (BaseStage stage : this.RunningStageGroups.get(groupName)) {
                    //获取实际返回的结果
                    if (StageName.getResponseStage.equals(stage.getName())){
                        //拿到获取结果阶段的最终输出
                        runData.setActual(stage.getRunData().getOutput());
                    }
                    //获取check的结果
                    if (StageName.checkStage.equals(stage.getName())){
                        runData.setResult(stage.getRunData().getResult());
                        runData.setOutput(stage.getRunData().getOutput());

                    }
                }
            }
        }
        return runData;
    }


    /**
     * 函数功能:用例执行流程初始化
     * @param stageName
     * @param stageJson
     * @return
     */
    @Override
    public BaseStage build(StageName stageName, JSONObject stageJson) {

        this.name = stageName;

        stageJsonObj = stageJson;

        //1.初始化exec中的send,get,check的3个子阶段
        if (stageJson.containsKey(ExecKey)){
            this.stageJsonArray = (JSONArray)stageJson.get(ExecKey);
            //遍历每组
            for (Object obj: stageJsonArray){
                JSONObject jsObj = (JSONObject) obj;
                String groupName = jsObj.getString(GroupNameKey);
                JSONArray stageList = (JSONArray)jsObj.get(ExecKey);
                LinkedList<BaseStage> baseStageList = new LinkedList<BaseStage>();
                for (Object stageObj: stageList){
                    JSONObject stageJsonObj = (JSONObject)stageObj;
                    stageJsonObj.put("params",stageJson.get("params"));
                    String stagename = (String)stageJsonObj.get("stageName");
                    BaseStage bs = RunningStageFactory.getRunningStage(stagename, stageJsonObj);
                    baseStageList.add(bs);
                }
                this.RunningStageGroups.put(groupName, baseStageList);

                //templates
                JSONArray templateData = new JSONArray();
                if (jsObj.containsKey("templates")) {
                    templateData = jsObj.getJSONArray("templates");
                }
                this.templates.put(groupName, templateData);

                JSONArray expectTemplateData = new JSONArray();
                if (jsObj.containsKey("expectTemplates")) {
                    expectTemplateData = jsObj.getJSONArray("expectTemplates");
                }
                this.expectTemplates.put(groupName, expectTemplateData);
            }
        }

        //2.初始化before_exec和after_exec中的插件
        String[] execList = {BeforeExecKey,AfterExecKey};
        for(String execName : execList){
            PluginList = new LinkedList<>();
            if (stageJson.containsKey(execName) && stageJson.get(execName) instanceof JSONObject){
                JSONObject stageExecJson = (JSONObject)stageJson.get(execName);
                if (stageExecJson.containsKey("pluginList")) {
                    for (Object obj : stageExecJson.getJSONArray("pluginList")) {
                        JSONObject jsObj = (JSONObject) obj;
                        String pluginName = (String)jsObj.get("plugin_name");
                        if (!pluginNameMap.containsKey(pluginName)){
                            IPlugin plugin = PluginFactory.getPlugin(jsObj ,stageJson);
                            pluginNameMap.put(pluginName,plugin);
                        }
                        //插件
                        this.addPlugin(jsObj);
                    }
                }
            }
            this.PluginMap.put(execName, PluginList);
        }

        //3.初始化待收集的logList
        if(stageJson.containsKey("params")){
            JSONObject paramsObj = (JSONObject)stageJson.get("params");
            if (paramsObj.containsKey("logFile")){
                for (Object obj : (JSONArray)paramsObj.get("logFile")) {
                    JSONObject jsObj = (JSONObject)obj;
                    if (jsObj.containsKey("role") && jsObj.containsKey("log_files")){
                        String module = (String)jsObj.get("role");
                        List<String> fileList = (List<String>)jsObj.get("log_files");
                        if (fileList.size() > 0){
                            appLogList.put(module,fileList);
                        }
                    }
                }
            }
        }
        return this;
    }


    /**
     * 函数功能:标准化PluginLayout
     * @param pluginList
     * @return
     */
    public List<PluginLayout> getPluginListLayout(LinkedList<JSONObject> pluginList){

        List<PluginLayout> pluginLayoutList = new LinkedList<>();
        if (pluginList.size() > 0){
            for(JSONObject pluginJson : pluginList){
                PluginLayout pluginLayout = new PluginLayout();
                pluginLayout.setPluginType(pluginJson.containsKey(PLUGIN_TYPE_KEY)? (String)pluginJson.get(PLUGIN_TYPE_KEY) :"");
                pluginLayout.setDisplayName(pluginJson.containsKey(DISPLAY_NAME_KEY)? (String)pluginJson.get(DISPLAY_NAME_KEY) :"");
                pluginLayout.setPluginName(pluginJson.containsKey(PLUGIN_NAME_KEY)? (String)pluginJson.get(PLUGIN_NAME_KEY) :"");
                pluginLayoutList.add(pluginLayout);
            }
        }
        return pluginLayoutList;
    }

     /**
     * 函数功能:pipeline阶段的布局json
     * @return
     */
    public List<Map<Object, Object>> buildPipelineLayout(){

        Map<Object, Object> compositeLayout = new LinkedHashMap<>();
        List<Map<Object, Object>> compositeLayoutList = new ArrayList<>();

        //载入before阶段
        LinkedList<JSONObject> PluginList = PluginMap.get(BeforeExecKey);
        List<Map<Object, Object>> runLayoutList = new ArrayList<>();
        compositeLayout.put("beforeStage",getPluginListLayout(PluginList));
        compositeLayoutList.add(compositeLayout);

        //载入exec阶段
        Map<Object, Object>  composite = new LinkedHashMap<>();
        compositeLayout = new LinkedHashMap<>();
        for (Object obj : stageJsonArray) {
            JSONObject jsObj = (JSONObject) obj;
            //group_name是否为必须的，这里假设为必须的
            String groupName = jsObj.getString(GroupNameKey);
            composite.put("group_name",groupName);
            JSONArray execJsObj = (JSONArray)jsObj.get("exec");

            for (Object stageObj : execJsObj) {
                JSONObject stageJsObj = (JSONObject)stageObj;
                BaseStage bs = RunningStageFactory.getRunningStage((String)stageJsObj.get("stageName"), stageJsObj);
                composite.put(bs.getName(),bs.buildLayout());
            }
            runLayoutList.add(composite);
        }
        compositeLayout.put("execStage",runLayoutList);
        compositeLayoutList.add(compositeLayout);

        //载入after阶段
        compositeLayout = new LinkedHashMap<>();
        compositeLayout.put("afterStage",getPluginListLayout(PluginMap.get(AfterExecKey)));
        compositeLayoutList.add(compositeLayout);

        return compositeLayoutList;
    }


    /**
     * 函数功能:CaseRunLayout的布局json
     * @return
     */
    public List<Map<String, String>> buildCaseRunLayout(){

        List<Map<String, String>> caseRunLayoutList = new ArrayList<>();
        Map<String, String>  composite = new LinkedHashMap<>();
        for (Object obj : stageJsonArray) {
            JSONObject jsObj = (JSONObject) obj;
            //group_name是否为必须的，这里假设为必须的
            String groupName = jsObj.getString(GroupNameKey);
            composite.put("group_name",groupName);
            caseRunLayoutList.add(composite);
        }

        return caseRunLayoutList;
    }



    public static String getGroupNameKey() {
        return GroupNameKey;
    }

    public HashMap<String, LinkedList<BaseStage>> getRunningStageGroups() {
        return RunningStageGroups;
    }

    public void setRunningStageGroups(
        HashMap<String, LinkedList<BaseStage>> runningStageGroups) {
        RunningStageGroups = runningStageGroups;
    }


    /**
     * 函数功能:日志收集和标准化
     * @param data
     * @param stageName
     * @return
     */
    @Override
    public String logFormat(RunData data, StageName stageName){

        String showLog = "";

        //check阶段
        if (stageName.equals(StageName.checkStage)){

            showLog += "======================[INPUT]======================\n";
            showLog += "======================[OUTPUT]======================\n";


            showLog += "======================[EXPECT]======================\n";
            showLog +=  data.getExpect() + "\n";

            showLog += "======================[RESULT]======================\n";
            showLog +=  data.getResult() + "\n";
        }
        //appLog
        else if (stageName.equals((StageName.caseRunStage))){

            showLog += "======================[LOG]======================\n";
            showLog +=  data.getLog() + "\n";
        }
        //send,respone阶段
        else{
            showLog += "======================[INPUT]======================\n";

            showLog += "======================[OUTPUT]======================\n";

        }
        return showLog;
    }


    /**
     * 函数功能:整合当前阶段所有的log,以map的格式返回,传递给testRespone
     * @param logCollection
     * @return
     */
    @Override
    public HashMap<StageName,ListHashMap<String, String>> mergeResponeLog(List<LogInfo> logCollection){

        StageName stageName;
        String pluginName;
        int dataListIndex;
        String log;
        HashMap<StageName,ListHashMap<String, String>> allLogCollection = new HashMap<>();
        ListHashMap<String, String> logList;
        String key;

        if (logCollection.size() > 0){

            for (LogInfo logInfo : logCollection){

                stageName = logInfo.getStageName();

                if (!allLogCollection.containsKey(stageName)){
                    logList = new ListHashMap<>();
                    allLogCollection.put(stageName,logList );
                }
                else {
                    logList = allLogCollection.get(stageName);
                }

                pluginName = logInfo.getPluginName();

                //如果是收集插件,特征是params参数带有appLogs
                if (logInfo.getRunData().getParams().containsKey("appLogs")){
                    Map<String,String> appLogs = (Map<String, String>)logInfo.getRunData().getParams().get("appLogs");
                    for (String module : appLogs.keySet()){
                        log = appLogs.get(module);
                        logList.put(module,log);
                    }
                }
                //如果是执行类插件,特征是params参数中带有index
                if (logInfo.getRunData().getParams().containsKey("index")){
                    dataListIndex = (int)logInfo.getRunData().getParams().get("index");
                    log = logInfo.getLog();
                    key = pluginName + "." + dataListIndex;
                    if (logInfo.getGroupName() != null){
                        key +=  "." + logInfo.getGroupName() ;
                    }
                    logList.put(key,log);
                }
            }
        }

        return allLogCollection;
    }



}

