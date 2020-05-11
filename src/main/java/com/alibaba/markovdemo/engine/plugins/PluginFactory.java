package com.alibaba.markovdemo.engine.plugins;

import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PluginFactory {


    private static PluginFactory instance;


    public PluginFactory(){

        PluginFactory.instance = this;

    }

    public static IPlugin getPlugin(JSONObject pluginObj, JSONObject stageJsonObj){

        if(pluginObj == null){
            return null;
        }

        String pluginType = pluginObj.getString(IPlugin.PluginTypeKey);
        if(pluginType == null){
            return null;
        }
        switch (pluginType.toUpperCase()){
            case "JAVA":
                return new JavaPlugin().build(pluginObj,stageJsonObj);
            case "PYTHON":
                return new PythonPlugin().build(pluginObj,stageJsonObj);
            case "SHELL":
                return new ShellPlugin().build(pluginObj,stageJsonObj);
            default:
                    throw new IllegalStateException(String.format("{0} isn't legal state.", pluginType));
        }
    }

}
