package com.alibaba.markovdemo.engine.plugins.inputdata.component;

import com.alibaba.fastjson.JSONObject;


import java.util.List;

public class ImockComponentImpl {

    private String log;

    /**
     * 构造函数,初始化
     */
    public ImockComponentImpl() {
        log="";
        //初始化,用户可以在外层传递初始配置
    }

    /**
     * 函数功能:数据批量插入
     * @param dataList
     * @return
     */
    public String insertBatch(List<JSONObject> dataList)  {

        log += "\n[ImockData插入]" + ",总记录数:" + dataList.size()  + "\n";

        String key =null;
        String value=null;
        String property;

        log += "插入imock数据完成:" ;
        log += "\n写入imock文件\n";
        return log;
    }




}
