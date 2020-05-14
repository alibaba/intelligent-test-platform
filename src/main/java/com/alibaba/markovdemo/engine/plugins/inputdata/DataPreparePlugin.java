package com.alibaba.markovdemo.engine.plugins.inputdata;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.alibaba.markovdemo.BO.RunDs;
import com.alibaba.markovdemo.engine.stages.RunData;
import com.alibaba.markovdemo.engine.stages.RunParams;
import com.alibaba.markovdemo.engine.stages.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 功能:数据准备通用插件
 */
public class DataPreparePlugin {
    private static final Logger Logger = LoggerFactory.getLogger(DataPreparePlugin.class);

    private String log;
    private long TIMEOUT = 12000L;
    private String classPath = "com.alibaba.markovdemo.engine.plugins.inputdata.component";
    private Map<String, RunDs> runDsMap = new HashMap<>();

    /**
     * 函数功能:插件执行func
     */
    public RunData exec(RunData data , RunParams runParams) throws IOException {

        log = "";
        //获取数据准备
        String inputObj =  data.getInput();
        JSONArray inputList = (JSONArray)JSONArray.parse(inputObj);

        //如果有输入
        if (inputList.size() > 0){
            //插入每组的数据,数据组之间是有顺序关系的
            for (Object group : inputList){
                insertGroupData((JSONObject)group);
            }
        }
        data.setLog(log);
        data.setRunDsMap(runDsMap);

        return data;
    }

    /**
     * 函数功能:支持的数据类型,动态获取
     * @return
     */
    Map<String, String> getTypeClassMap(){
        Map<String,String> typeClassMap = new HashMap<>();
        typeClassMap.put(Utils.PrepareDataType.Tair.name(),"TairComponentImpl");
        typeClassMap.put(Utils.PrepareDataType.Tdbm.name(),"TdbmComponentImpl");
        typeClassMap.put(Utils.PrepareDataType.Imock.name(),"ImockComponentImpl");
        return typeClassMap;
    }
    public boolean isNotEmpty(String content){

        if (content == null){
            return false;
        }
        if ("".equals(content)){
            return false;
        }
        if ("无".equals(content)){
            return false;
        }
        return true;
    }

    /**
     * 函数功能:准备每组数据,进行插入
     * 通过反射机制动态支持数据准备插件
     * @param
     * @param group
     */
    public void insertGroupData(JSONObject group) throws IOException {

        List<JSONObject> dsDataList;
        String dsName;

        String typeClassName;
        Map<String,String> typeClassMap = getTypeClassMap();
        List<JSONObject> recordList;
        JSONObject dsConf;
        Class<?> clz = null;
        Object instance = null;
        Method m;

        //插入每个类型
        for(String type : group.keySet()){
            dsDataList = (List<JSONObject>)group.get(type);
            Logger.info("开始执行数据插入,TYPE:" + type);
            for (JSONObject dsDataUnit : dsDataList){
                Date start = new Date();

                //进行每个ds的插入
                dsName = (String)dsDataUnit.get("dsName");
                String match = type + ":" +dsName;
                dsName = (String)dsDataUnit.get("dsName");
                recordList = (List<JSONObject>)dsDataUnit.get("data");
                if (!runDsMap.containsKey(match)){
                    runDsMap.put(match,new RunDs());
                }
                typeClassName = classPath + "." + typeClassMap.get(type);
                try {
                    //初始化
                    clz = Class.forName(typeClassName);
                    Constructor constructor = clz.getConstructor();
                    instance = constructor.newInstance();
                    m = clz.getDeclaredMethod("insertBatch", List.class);
                    String tmpLog = (String)m.invoke(instance, recordList);
                    log += tmpLog;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Date end = new Date();
                runDsMap.get(match).setRecordNum(runDsMap.get(match).getRecordNum() + recordList.size());

            }
        }
    }


    public static void main(String[] args) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(new Date()));

    }


}
