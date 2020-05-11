package com.alibaba.markovdemo.engine.plugins;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.engine.stages.RunData;
import com.alibaba.markovdemo.engine.stages.RunParams;
import com.alibaba.markovdemo.engine.stages.StageName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;

public class JavaPlugin implements IPlugin {

    private static final Logger Loger = LoggerFactory.getLogger(JavaPlugin.class);

    private String className;
    private String name;
    private String funcName;
    private String displayName;
    private RunData runData;
    private RunParams runParams = new RunParams();
    Class<?> clz = null;
    Method m;
    //反射类实例
    Object instance;
    private Map<StageName, Object> stageParamsObj;
    private JSONObject pipelineParamsObj;

    @Override
    public Map<StageName, Object> getStageParamsObj() {
        return stageParamsObj;
    }

    @Override
    public void setStageParamsObj(Map<StageName, Object> stageParamsObj) {
        this.stageParamsObj = stageParamsObj;
    }

    //能获取当前插件的所有信息,解决同一个插件对象调用多个func的情况
    @Override
    public void setPluginParams(JSONObject pluginParams) {
        funcName = pluginParams.containsKey("func") ? (String) pluginParams.get("func") : "exec";
    }

    @Override
    public void setRunData(RunData runData) {
        this.runData = runData;
    }

    @Override
    public RunData getRunData() {
        return runData;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;

    }


    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isJson(String content) {

        try {
            JSONObject jsonStr = JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void beforeExec() {

    }

    /**
     * 函数功能:java类插件执行
     */
    @Override
    public void exec() {
        //exec命令对象
        //params的格式为 Jsonstring,返回的结果为json串,会取得关键字
        runParams.setStageParamsObj(stageParamsObj);
        runParams.setPipelineParamsObj(pipelineParamsObj);
        //不需要控制时间
        try {
            m = clz.getMethod(funcName, RunData.class, RunParams.class);
            runData = (RunData) m.invoke(instance, this.runData, this.runParams);
            if (!funcName.equals("com.alimama.zhizi.engine.data.inputdata.DataPreparePlugin") && !funcName.equals("com.alimama.zhizi.engine.data.inputdata.DataPrepareOnlinePlugin")) {
            }

        } catch (Exception e) {
            runData.setResult(0);

        }

    }


    @Override
    public void afterExec() {
        ////插件返回结果赋值

    }

    /**
     * 函数功能:plugin插件初始化
     * @param obj
     * @param pipelineParamsObj
     * @return
     */
    @Override
    public IPlugin build(JSONObject obj, JSONObject pipelineParamsObj) {

        this.setName(obj.getString("plugin_name"));
        this.setDisplayName(obj.getString("display_name"));
        this.setClassName(obj.getString("class"));
        //默认使用exec的funcName
        this.setFuncName((obj.containsKey("func") ? obj.getString("func") : "exec"));
        Loger.info("插件名:" + this.getName() + ",调用类名:" + className + ",调用方法名:" + funcName + " ,插件类型:JAVA");

        //class
        try {
            clz = Class.forName(className);
            instance = clz.newInstance();

        } catch (Exception e) {
        }
        if (pipelineParamsObj.containsKey("params")) {
            this.pipelineParamsObj = (JSONObject) pipelineParamsObj.get("params");
        }
        return this;

    }




}
