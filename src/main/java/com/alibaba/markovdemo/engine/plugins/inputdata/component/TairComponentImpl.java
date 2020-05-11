package com.alibaba.markovdemo.engine.plugins.inputdata.component;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.List;


/**
 * 功能:tair数据准备
 */
public class TairComponentImpl{

    private static final Logger logger = LoggerFactory.getLogger(TairComponentImpl.class);
    private String log;

    /**
     * 构造函数:初始化tair
     */
    public TairComponentImpl(){
        log="";
        //todo:此处需要要用户自行定义
        try{
            logger.info("初始化tair完成");

        }
        catch (Exception e) {
            logger.error("tairManager初始化异常，请检查!");

        }
    }


    /**
     * 函数功能:数据批量插入
     * @param dataList
     * @return
     */
    public String insertBatch(List<JSONObject> dataList) throws IOException {

        String key ;
        String value;
        int insertNum = 0;

        if (dataList.size() > 0){
            //插入tair
            for (JSONObject data: dataList){
                insertNum++;
                key = (String)data.get("key");
                value = (String)data.get("value");
                log += "\n插入第" + insertNum + "条记录\nkey:" +  key + "\nvalue:"+ value + "\n";
            }

        }

        logger.info(log);
        return log;
    }


}
