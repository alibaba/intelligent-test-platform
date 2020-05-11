package com.alibaba.markovdemo.engine.plugins.pluginSet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.engine.stages.RunData;
import com.alibaba.markovdemo.engine.stages.RunParams;
import com.alibaba.markovdemo.engine.util.Toolkit;


import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 类功能:发送和校验插件DEMO
 */
public class SendAndCheckDemoPlugin {


    private String log;

    /**
     * 函数功能:发送和校验动作具体执行
     * @param data
     * @param runParams
     * @return
     * @throws IOException
     */
    public RunData exec(RunData data, RunParams runParams) throws IOException {

        log = "";

        //todo:此处用户可以自定义,比如发送真实http请求获取返回,此处走mock
        String result = "{\n" +
                "  \"result\": \"1\"\n" +
                "}";
        JSONObject actualObj = JSONObject.parseObject(result);


        //调用公共方法进行结果校验
        String actual = JSONObject.toJSONString(actualObj);
        String expect = data.getExpect();
        String compareResult = Toolkit.commonJsonCompare(expect, actual, data.getCalFieldsConfig());

//        //校验不通过
        if (compareResult.contains("校验失败")){
            data.setResult(0);
        }
        //校验通过
        else{
            data.setResult(1);
        }
        //actual: 实际返回
        data.setActual(actual);
        data.setOutput(compareResult);
        data.setLog(log);

        return data;
    }
}
