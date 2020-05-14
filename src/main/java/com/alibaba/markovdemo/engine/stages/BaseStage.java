package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSONObject;

import com.alibaba.markovdemo.BO.TestRespone;
import com.alibaba.markovdemo.engine.plugins.IPlugin;
import com.alibaba.markovdemo.engine.plugins.PluginFactory;
import com.alibaba.markovdemo.engine.plugins.PluginLayout;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import org.codehaus.groovy.util.ListHashMap;

import java.util.*;

@Service
public class BaseStage {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStage.class);

    public StageName name;
    public Long scenarioId;
    public Long testcaseId;
    static final String BeforeExecKey = "before_exec";
    static final String ExecKey = "exec";
    static final String AfterExecKey = "after_exec";
    static final String PLUGIN_TYPE_KEY = "plugin_type";
    static final String PLUGIN_NAME_KEY = "plugin_name";
    static final String DISPLAY_NAME_KEY = "display_name";

    Map<String,List<RunData>> stageRunDataMap = new HashMap<>();
    //输入对象
    Object inputObj;
    //stage参数
    Map<StageName,Object> stageParams;
    //plugin_name => IPlugin
    Map<String, IPlugin> pluginNameMap = new HashMap<>();

    //一个stage阶段可能执行多个数据组
    public String groupName;

    //1:单次 2:回归
    public String runType = "1";

    public LinkedList<JSONObject> pluginList = new LinkedList<>();


    public Map<String, LinkedList<JSONObject>> pluginMap = new HashMap<>();

    //中间结果收集
    public List<LogInfo> logCollection = new ArrayList<>();

    //存放stage的数据,包括输入/输出/期望/执行结果/校验内容
    public RunData runData = new RunData();

    //将所有存在信息都进行保存
    public List<RunData> runDataList = new ArrayList<>();

    public JSONObject stageJsonObj;

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(Long testcaseId) {
        this.testcaseId = testcaseId;
    }

    public Map<String, LinkedList<JSONObject>> getPluginMap() {
        return pluginMap;
    }

    public void setPluginMap(Map<String, LinkedList<JSONObject>> map) {
        pluginMap = map;
    }
    public JSONObject getStageJsonObj() {
        return stageJsonObj;
    }

    public void setStageJsonObj(JSONObject stageJsonObj) {
        this.stageJsonObj = stageJsonObj;
    }

    public Map<StageName, Object> getStageParams() {
        return stageParams;
    }

    public String getRunType() {
        return runType;
    }

    public void setRunType(String runType) {
        this.runType = runType;
    }
    public List<RunData> getRunDataList() {
        return runDataList;
    }

    public void setRunDataList(List<RunData> runDataList) {
        this.runDataList = runDataList;
    }

    public void setStageParams(
        Map<StageName, Object> stageParams) {
        this.stageParams = stageParams;
    }

    public LogInfo findByPluginName(String pluginName){

        if (logCollection.size() > 0) {
            for(LogInfo search : logCollection){
                if (search.getPluginName().equals(pluginName)){
                    return search;
                }
            }
        }
        return null;
    }
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public void init() {
        throw new NotImplementedException();
    }

    public StageName getName() {
        return name;
    }

    public void setName(StageName name) {
        this.name = name;
    }



    public void setRunData() {
        //转化为string OR JsonString
        this.runData = runData;
        runData.setResult(1);
        Gson gosn = new Gson();
        runData.setInput(gosn.toJson(inputObj));
        runData.setScenarioId(scenarioId);
        runData.setTestcaseId(testcaseId);
    }

    public void setRunDataCheckConf(String calFieldsConfig){
        this.runData.setCalFieldsConfig(calFieldsConfig);
    }

    public Map<String, List<RunData>> getStageRunDataMap() {
        return stageRunDataMap;
    }

    public void setStageRunDataMap(Map<String, List<RunData>> stageRunDataMap) {
        this.stageRunDataMap = stageRunDataMap;
    }

    public List<LogInfo> getLogCollection() {
        return logCollection;
    }

    public void setLogCollection(List<LogInfo> logCollection) {
        this.logCollection = logCollection;
    }

    public void setPrepareData(String data){
        runData.setInput(data);
    }


    public TestRespone setTestRespone(TestRespone testRespone){

        return testRespone;
    }


    /**
     * 函数功能:整合展示日志,对于check阶段,需要加入input/output/expect/result/log.其他阶段只有input/output/log
     * @param data
     * @param stageName
     * @return
     */
    public String logFormat(RunData data, StageName stageName){

        String showLog = "";

        showLog += "======================[INPUT]======================\n";

        showLog += "======================[OUTPUT]======================\n";

        showLog += "======================[LOG]======================\n";
        showLog +=  data.getLog() + "\n";

        return showLog;
    }

    public void setIputData(Object inputObj){
        this.inputObj = inputObj;
    }

    /**
     * 函数功能:收集中间结果Log,将本阶段所有log进行收集
     * @param data
     * @param pluginName
     */
    public void startLogCollect(RunData data, String pluginName){
        LogInfo logInfo = new LogInfo();
        logInfo.setStageName(name);
        logInfo.setGroupName(groupName);
        logInfo.setPluginName(pluginName);
        logInfo.setRunData(data);
        logInfo.setLog(logFormat(data, name));
        logCollection.add(logInfo);
    }

    public void beforeExec() {
        //TODO:在每个流程执行时,有些会有预先默认的系统插件,比如占位符替换
        LOGGER.debug(String.format("beforeExec start"));
        pluginExec(BeforeExecKey);
    }

    public void afterExec() {

        LOGGER.debug(String.format("afterExec start"));
        pluginExec(AfterExecKey);
    }

    //1.执行pluginList. 2.收集中间结果
    public void exec(){
        LOGGER.debug(String.format("exec start"));
        pluginExec(ExecKey);
    }


    /**
     * 函数功能:单个plugin插件执行
     * @param key
     */
    public void pluginExec(String key){

        LinkedList<JSONObject> plugins = pluginMap.get(key);
        IPlugin p;
        if("exec".equals(key)){
            runDataList = new ArrayList<>();
        }

        for (JSONObject pluginObj : plugins) {

            String pluginName = (String)pluginObj.get("plugin_name");
            LOGGER.info("[执行插件:" + pluginName + "]");

            p = pluginNameMap.get(pluginName);
            //第一个插件使用控制器传进来的,之后的插件获取上个插件的结果
            p.setPluginParams(pluginObj);
            p.setRunData(runData);


            p.setStageParamsObj(stageParams);

            p.beforeExec();

            LOGGER.debug(String.format("Plugin {0} executed beforeExec method.", p.getName()));

            p.exec();
            LOGGER.debug(String.format("Plugin {0} executed exec method.", p.getName()));
            //搜集插件结果集
            if("exec".equals(key)){
                runDataList.add((RunData) runData.clone());
            }
            p.afterExec();
            LOGGER.debug(String.format("Plugin {0} executed afterExec method.", p.getName()));

            //收集插件list的中间结果
            startLogCollect(runData,p.getName());

            //进行一次output设置为input
            runData.setInput(p.getRunData().getOutput());
        }
    }

    public void cleanup() {
        throw new NotImplementedException();
    }

    /**
     * 函数功能:插件添加
     * @param pluginObj
     */
    public void addPlugin(JSONObject pluginObj) {

        this.pluginList.add(pluginObj);
    }

    /**
     * 函数功能:插件初始化
     * @param stageName
     * @param stageJsonObj
     * @return
     */
    public BaseStage build(StageName stageName, JSONObject stageJsonObj) {

        this.name = stageName;
        this.stageJsonObj = stageJsonObj;
        String[] execList = {BeforeExecKey,ExecKey,AfterExecKey};
        //plugin_name => IPlugin
        pluginNameMap = new HashMap<>();
        for(String execName : execList){
            pluginList = new LinkedList<>();
            if (stageJsonObj.containsKey(execName) && stageJsonObj.get(execName) instanceof JSONObject){
                stageJsonObj = (JSONObject)stageJsonObj.get(execName);
                stageJsonObj.putAll(this.stageJsonObj);
                if (stageJsonObj.containsKey("pluginList")) {
                    for (Object obj : stageJsonObj.getJSONArray("pluginList")) {
                        JSONObject jsObj = (JSONObject) obj;
                        String pluginName = (String)jsObj.get("plugin_name");
                        if (!pluginNameMap.containsKey(pluginName)){
                            IPlugin plugin = PluginFactory.getPlugin(jsObj ,stageJsonObj);
                            pluginNameMap.put(pluginName,plugin);
                        }
                        this.addPlugin(jsObj);
                    }
                }
            }
            this.pluginMap.put(execName, pluginList);
        }

        return this;
    }

    /**
     * 函数功能:build出pipeline阶段中的布局对象,主要是插件信息,在prepareData/send/get/check阶段使用到
     * @return
     */
    public List<PluginLayout> buildLayout(){

        List<PluginLayout> pluginLayoutList = new ArrayList<>();
        String[] execList = {BeforeExecKey,ExecKey,AfterExecKey};
        for (String execKey : execList){
            if (this.stageJsonObj.containsKey(execKey) && stageJsonObj.get(execKey) instanceof JSONObject){

                JSONObject execJson = (JSONObject)stageJsonObj.get(execKey);

                if (execJson.containsKey("pluginList")) {
                    for (Object obj : execJson.getJSONArray("pluginList")) {
                        PluginLayout pluginLayout = new PluginLayout();
                        JSONObject jobj = (JSONObject)obj;
                        pluginLayout.setPluginName(jobj.containsKey(PLUGIN_NAME_KEY)? (String)jobj.get(PLUGIN_NAME_KEY) :"");
                        pluginLayout.setDisplayName(jobj.containsKey(DISPLAY_NAME_KEY)? (String)jobj.get(DISPLAY_NAME_KEY) :"");
                        pluginLayout.setPluginType(jobj.containsKey(PLUGIN_TYPE_KEY)? (String)jobj.get(PLUGIN_TYPE_KEY) :"");
                        pluginLayoutList.add(pluginLayout);
                    }
                }
            }
        }

        return pluginLayoutList;
    }

    public RunData getRunData() {
        return runData;
    }

    
    /**
     * 插件功能:/整合当前阶段所有的log,以map的格式返回,传递给testRespone
     * @param logCollection
     * @return
     */
    public HashMap<StageName,ListHashMap<String, String>> mergeResponeLog(List<LogInfo> logCollection){

        StageName stageName;
        String pluginName;
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

                key = pluginName;
                log = logInfo.getLog();
                logList.put(key,log);

            }
        }

        return allLogCollection;
    }
}


