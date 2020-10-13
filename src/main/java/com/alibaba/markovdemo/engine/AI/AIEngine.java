package com.alibaba.markovdemo.engine.AI;

import com.alibaba.markovdemo.BO.FeatureInfo;
import com.alibaba.markovdemo.entity.GotTestCase;

import java.util.List;

public interface AIEngine {

    /**
     * 获取当前引擎的名称
     *
     * @return 当前引擎名称
     */
    public String getEngineName();

    /**
     * 函数功能:处理FMM处理后的特征
     * 过滤策略:单个字符不算特征,具体的词才算业务特征
     *
     * @param input 要抽取特征的字符串
     * @return 特征列表
     */
    public List<String> getFeatures(String input);

    /**
     * 函数功能：得到测试用例和页面提交的 FeatureInfo 的相似度
     *
     * @param testCase    测试用例
     * @param featureInfo 页面提交的参数集合
     * @return 算法得到的相似度
     */
    public float sim(GotTestCase testCase, FeatureInfo featureInfo);

}
