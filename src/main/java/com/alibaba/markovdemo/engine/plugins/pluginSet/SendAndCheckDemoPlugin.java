package com.alibaba.markovdemo.engine.plugins.pluginSet;

import com.alibaba.markovdemo.engine.stages.RunData;
import com.alibaba.markovdemo.engine.stages.RunParams;

import java.io.IOException;

/**
 * 类功能:发送和校验插件DEMO
 */
public class SendAndCheckDemoPlugin {

    private String log;

    /**
     * 函数功能:发送和校验动作具体执行
     *
     * @param data
     * @param runParams
     * @return
     * @throws IOException
     */
    public RunData exec(RunData data, RunParams runParams) throws IOException {
        /**
         * 将原先的实现删掉了，可以等这个功能真正开启时再重新打开。
         */
        throw new UnsupportedOperationException();
    }
}
