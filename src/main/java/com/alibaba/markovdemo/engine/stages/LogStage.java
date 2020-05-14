package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.engine.plugins.PluginLayout;
import org.codehaus.groovy.util.ListHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogStage extends BaseStage {

    ListHashMap<String,List<String>> appLogList = new ListHashMap<>();

    //整合给前端日志
    //public HashMap<StageName,ListHashMap<String, String>> mergeResponeLog(List<LogInfo> logCollection){
    //
    //    StageName stageName;
    //    String pluginName;
    //    int dataListIndex;
    //    String log;
    //    HashMap<StageName,ListHashMap<String, String>> allLogCollection = new HashMap<>();
    //    ListHashMap<String, String> logList;
    //    String key;
    //
    //    if (logCollection.size() > 0){
    //        for (LogInfo logInfo : logCollection){
    //            stageName = logInfo.getStageName();
    //            if (!allLogCollection.containsKey(stageName)){
    //                logList = new ListHashMap<>();
    //                allLogCollection.put(stageName,logList );
    //            }
    //            else {
    //                logList = allLogCollection.get(stageName);
    //            }
    //            pluginName = logInfo.getPluginName();
    //            dataListIndex = logInfo.getDataListIndex();
    //            log = logInfo.getLog();
    //            key = pluginName + "." + dataListIndex;
    //            if (logInfo.getGroupName() != null){
    //                key +=  "." + logInfo.getGroupName() ;
    //            }
    //
    //            logList.put(key,log);
    //            }
    //        }
    //
    //    return allLogCollection;
    //}

    public BaseStage build(JSONArray stageJsonArr) {

        this.name = StageName.logAppStage;

        for (Object obj : stageJsonArr) {
            JSONObject jsObj = (JSONObject)obj;
            if (jsObj.containsKey("module") && jsObj.containsKey("log_files")){
                String module = (String)jsObj.get("module");
                List<String> fileList = (List<String>)jsObj.get("log_files");
                if (fileList.size() > 0){
                    appLogList.put(module,fileList);
                }
            }
        }

        return this;
    }

    /**
     * 收集applog
     * 1.收集start和end的序号,以及之间的LOG内容
     * @return
     * @throws InterruptedException
     */
    //public void appLogCollect(String type)
    //    throws InterruptedException {
    //
    //    List<String> logFileList ;
    //    RemoteShell remoteShell;
    //    String line ;
    //    long lastFileSize;
    //    long logGapSize;
    //    JSONObject moduleRemoteClassMap = new JSONObject();
    //
    //    //需要调用部署的接口
    //    ShellSut shellSut = new ShellSut("10.101.80.159");
    //    moduleRemoteClassMap.put("k2-uts",shellSut.getRemoteShell());
    //
    //    //TODO:调用方式修改为紫铜提供的
    //
    //    LogInfo logInfo;
    //    //遍历每个应用
    //    for (String module : appLogList.keySet()){
    //        logFileList = appLogList.get(module);
    //
    //        if (moduleRemoteClassMap.containsKey(module)){
    //
    //            remoteShell = (RemoteShell)moduleRemoteClassMap.get(module);
    //            //遍历应用的每个log文件
    //            for (String logPath: logFileList){
    //                line = remoteShell.runCmd("cat " + logPath + " | wc -l").getStdout().trim();
    //                lastFileSize = Long.parseLong(line);
    //                //module + fileName
    //                String pluginName = module + "." + logPath.split("/")[logPath.split("/").length - 1];
    //                //记录起始行号
    //                if ("START".equals(type)){
    //                    //获取起始
    //                    logInfo = new LogInfo();
    //                    logInfo.setStart(lastFileSize);
    //                    logInfo.setStageName(name);
    //                    logInfo.setPluginName(pluginName);
    //                    logCollection.add(logInfo);
    //                }
    //                //记录结束行号
    //                else{
    //                    logInfo = super.findByPluginName(pluginName);
    //                    logInfo.setEnd(lastFileSize);
    //                    //如果行号不变,暂停1s重新收集
    //                    if (logInfo.getStart() == logInfo.getEnd()){
    //                        System.out.println("启动暂停");
    //                        Thread.sleep(1000);
    //                        line = remoteShell.runCmd("cat " + logPath + " | wc -l").getStdout().trim();
    //                        lastFileSize = Long.parseLong(line);
    //                        logInfo.setEnd(lastFileSize);
    //                    }
    //                    //收集Log信息
    //                    logGapSize = logInfo.getEnd() - logInfo.getStart();
    //
    //                    logInfo.setLog(remoteShell.runCmd("tail -" + logGapSize + " " + logPath).getStdout());
    //                }
    //            }
    //        }
    //    }
    //}

    //设置前端LOG阶段展示
    @Override
    public List<PluginLayout> buildLayout() {
        PluginLayout pluginLayout;
        List<PluginLayout> pluginLayoutList = new ArrayList<>();
        Map<String,String> init = new HashMap<>();
        init.put("appStage","执行日志");
        init.put("PrepareData","数据准备阶段");
        init.put("SendStage","发送阶段");
        init.put("GetResponseStage","结果获取阶段");
        init.put("CheckStage","校验阶段");
        for(String stagename : init.keySet()){
            pluginLayout = new PluginLayout();
            pluginLayout.setPluginType(stagename);
            pluginLayout.setDisplayName(init.get(stagename));
            pluginLayoutList.add(pluginLayout);
        }
        return pluginLayoutList;
    }
}
