package com.alibaba.markovdemo.engine.AI.hanlp;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.FeatureInfo;
import com.alibaba.markovdemo.engine.AI.AIEngine;
import com.alibaba.markovdemo.entity.GotTestCase;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("HanLPEngine")
@Lazy
public class HanLPEngine implements AIEngine {

    private static final Logger logger = LoggerFactory.getLogger(HanLPEngine.class);

    /**
     * 模型文件的位置，模型可以自行训练或者去 HanLP 的 github wiki 中下载
     */
    private static final String MODEL_FILE_NAME = "/usr/local/hanlp-wiki-vec-zh.txt";

    private WordVectorModel wordVectorModel;
    private DocVectorModel docVectorModel;

    @SneakyThrows
    public HanLPEngine() {
        logger.info("Loading HanLP WordVectorModel from {}", MODEL_FILE_NAME);
        wordVectorModel = new WordVectorModel(MODEL_FILE_NAME);
        docVectorModel = new DocVectorModel(wordVectorModel);
    }

    @Override
    public String getEngineName() {
        return "HanLP v1.x";
    }

    @Override
    public List<String> getFeatures(String input) {
        return HanLP.extractKeyword(Optional.ofNullable(input).orElse(""), 20);
    }

    @Override
    public float sim(GotTestCase testCase, FeatureInfo featureInfo) {
        try {
            JSONObject jList = JSONObject.parseObject(testCase.getFeatures());
            return docVectorModel.similarity(String.join(",", jList.keySet()), String.join(",", featureInfo.getFeatureList()));
        } catch (Exception e) {
            return -1f;
        }
    }
}
