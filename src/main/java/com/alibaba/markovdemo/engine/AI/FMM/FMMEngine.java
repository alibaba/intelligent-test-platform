package com.alibaba.markovdemo.engine.AI.FMM;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.FeatureInfo;
import com.alibaba.markovdemo.engine.AI.AIEngine;
import com.alibaba.markovdemo.entity.GotTestCase;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Service("FMMEngine")
@Lazy
public class FMMEngine implements AIEngine {

    private Segmentation seg;

    /**
     * 函数功能:加载字典,分词使用
     *
     * @throws IOException
     */
    @PostConstruct
    public void loadDict() throws IOException {
        HashMap hm = new HashMap();
        HashMap len = new HashMap();
        GenerateDictionary.genHashDic(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("/dict.txt")), hm, len);
        seg = new Segmentation(hm, len);
    }

    @Override
    public String getEngineName() {
        return "FMM算法";
    }

    @Override
    public List<String> getFeatures(String input) {
        List<String> featureList = new ArrayList<>();
        String str = seg.Fmm(Optional.ofNullable(input).orElse(""));
        String[] list = str.split("/");

        for (String split : list) {

            if (split.length() > 1) {
                featureList.add(split);
            }
        }
        return featureList;
    }

    @Override
    public float sim(GotTestCase testCase, FeatureInfo featureInfo) {
        JSONObject jList = JSONObject.parseObject(testCase.getFeatures());
        List<String> list = new ArrayList<>(jList.keySet());
        //交集处理
        list.retainAll(featureInfo.getFeatureList());
        return list.size();
    }


}
