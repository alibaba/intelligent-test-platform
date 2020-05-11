package com.alibaba.markovdemo.engine.plugins.inputdata.component;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;


/**
 * 功能:Tdbm类数据准备
 */
public class TdbmComponentImpl{

    private static final Logger logger = LoggerFactory.getLogger(TdbmComponentImpl.class);

    private String log;

    /**
     * 构造函数:初始化
     */
    public TdbmComponentImpl(){
        //todo:此处需用户自定义
        try {
            logger.info("TdbmComponent初始化完成!");
        }
        catch (Exception e){
            logger.error("TdbmComponent初始化异常，请检查!");

        }

    }

    /**
     * 函数功能:数据批量插入
     * @param dataList
     * @return
     */
    public String insertBatch(List<JSONObject> dataList){
        String key;
        String value;
        String property;
        int insertNum = 0;

        //将tdbm数据进行打包拼装
        for (JSONObject data : dataList) {
            insertNum++;
            key = (String) data.get("key");
            value = (String) data.get("value");
            log += "插入第" + insertNum + "条记录\nkey:" + key + "\nvalue:" + value + "\n";

        }

        logger.info(log);
        return log;
    }


}
