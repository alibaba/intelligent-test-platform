package com.alibaba.markovdemo.engine.AI.GACaseGenerator;

import com.alibaba.markovdemo.BO.TestCaseInput;
import com.alibaba.markovdemo.mapper.GotTestcaseMapper;
import com.alibaba.markovdemo.service.TestcaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class GenerateController {
    @Autowired
    GenerateProcessor generateProcessor;
    @Autowired
    TestcaseService testcaseService;
    @Autowired
    GotTestcaseMapper gotTestcaseMapper;



    public Long startGenerateTask(Long seedCaseId, Long scenarioId, String featureConf, HashMap envInfo){
        try {

            TestCaseInput seedCase = testcaseService.getTestCaseById(seedCaseId);

            return generateProcessor.startTask(seedCase,scenarioId,envInfo, featureConf);

        }catch (Exception e){
            //处理异常
        }
        return -1L;
    }

}
