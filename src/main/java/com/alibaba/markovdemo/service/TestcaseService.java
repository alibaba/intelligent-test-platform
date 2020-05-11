package com.alibaba.markovdemo.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.TestCaseInput;
import com.alibaba.markovdemo.engine.stages.StageName;
import com.alibaba.markovdemo.entity.GotTestCase;
import com.alibaba.markovdemo.mapper.GotTestcaseMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TestcaseService {
    private static final Logger logger = LoggerFactory.getLogger(TestcaseService.class);

    @Autowired
    GotTestcaseMapper gotTestcaseMapper;
    Gson gson = new Gson();

    private static Integer PAGESIZE = 15;

    public List<GotTestCase> getTestcaseByScenario(Long scenarioId, Integer pageNo){
        Integer fromRow = (pageNo-1)*PAGESIZE;

        HashMap map = new HashMap();
        map.put("scenarioId", scenarioId);
        map.put("fromRow", fromRow);
        map.put("pageSize", PAGESIZE);

        return gotTestcaseMapper.getCaseByScenarioId(map);
    }


    public List<GotTestCase> getVisibleTestcaseByScenario(Long scenarioId, Integer pageNo){
        Integer fromRow = (pageNo-1)*PAGESIZE;

        HashMap map = new HashMap();
        map.put("scenarioId", scenarioId);
        map.put("fromRow", fromRow);
        map.put("pageSize", PAGESIZE);

        return gotTestcaseMapper.getVisibleTestcaseByScenario(map);
    }


    public List<GotTestCase> findByScenarioId(Long scenarioId){
        HashMap map = new HashMap();
        map.put("scenarioId", scenarioId);
        return gotTestcaseMapper.getAllCaseByScenarioId(map);
    }


    public List<TestCaseInput> getAllCaseByScenarioId(Long scenarioId){

        HashMap map = new HashMap();
        map.put("scenarioId", scenarioId);
        List<TestCaseInput> list = new ArrayList<>();

        List<GotTestCase> gotTestCaseList = gotTestcaseMapper.getAllCaseByScenarioId(map);
        if (gotTestCaseList.size()>0){
            for (GotTestCase  gotTestCase : gotTestCaseList){
                list.add(FormatTestCaseInput(gotTestCase));
            }
        }
        return list;
    }



    public List<String> getCaseGroupByScenarioId(Long scenarioId){


        List<GotTestCase> caselist =  gotTestcaseMapper.getCaseGroupByScenarioId(scenarioId);
        List<String> caseGroupList = new ArrayList<>();
        for (GotTestCase gotTestCase : caselist){
            try{
                caseGroupList.add(gotTestCase.getCaseGroup());
            }
            catch (Exception e){
                //do nothing
            }
        }
        return caseGroupList;
    }

    public void insert(GotTestCase gotTestCase){
       gotTestcaseMapper.insert(gotTestCase);
    }
    public void update(GotTestCase gotTestCase){
        gotTestcaseMapper.update(gotTestCase);
    }

    public void deleteTestCase(Long testcaseId){
        gotTestcaseMapper.delete(testcaseId);
    }


    public static TestCaseInput FormatTestCaseInput(GotTestCase gotTestcase){
        TestCaseInput testCase = new TestCaseInput();
        testCase.setId(gotTestcase.getId());
        testCase.setScenarioId(gotTestcase.getScenarioId());
        testCase.setName(gotTestcase.getName());
        testCase.setDescription(gotTestcase.getDescription());
        testCase.setLongDescription(gotTestcase.getLongDescription());
        testCase.setCaseTemplate(gotTestcase.getCaseTemplate());
        testCase.setCaseGroup(gotTestcase.getCaseGroup());
        testCase.setIsVisible(gotTestcase.getIsVisible());

        try {
            JSONObject contentObj = JSONObject.parseObject(gotTestcase.getContent());
            testCase.setCaseRunStage(TestCaseInput.parseCaseRunStage(contentObj, gotTestcase.getCaseTemplate()));
            testCase.setPrepareData(TestCaseInput.parsePrepareDataListAddInfo(contentObj));
            testCase.setComponentInfo(TestCaseInput.parseComponent(contentObj));
        }catch(Exception e){

        }
        return testCase;
    }

    public TestCaseInput getTestCaseById(Long caseId){


        GotTestCase gotTestcase = gotTestcaseMapper.getTestCaseById(caseId);

        if (gotTestcase == null) {
            return null;
        }

        return FormatTestCaseInput(gotTestcase);

    }

    public GotTestCase findById(Long caseId){


        GotTestCase gotTestcase = gotTestcaseMapper.getTestCaseById(caseId);

        return gotTestcase;

    }


    public GotTestCase saveTestCase(TestCaseInput testCase) {

        GotTestCase gotTestcase = new GotTestCase();
        JSONObject stageData = new JSONObject();
        gotTestcase.setId(testCase.getId());
        gotTestcase.setScenarioId(testCase.getScenarioId());
        gotTestcase.setName(testCase.getName());
        gotTestcase.setDescription(testCase.getDescription());
        gotTestcase.setCaseTemplate(testCase.getCaseTemplate());
        gotTestcase.setLongDescription(testCase.getLongDescription());
        gotTestcase.setCaseGroup(testCase.getCaseGroup());
        gotTestcase.setIsVisible(testCase.getIsVisible());

        stageData.put(StageName.prepareData.name(), testCase.getPrepareData());
        if (testCase.getComponentInfo() != null && testCase.getComponentInfo().size() > 0) {
            stageData.put(StageName.caseRunAgentStage.name(), testCase.getComponentInfo());
        }
        stageData.put(StageName.caseRunStage.name(), testCase.getCaseRunStage());
        gotTestcase.setContent(gson.toJson(stageData));

        System.out.println("[save-testcase-test-log] gotTestCase.getContent: " + gotTestcase.getContent());
        //replace的插入方式
        this.insert(gotTestcase);



        return gotTestcase;
    }


    public GotTestCase addNewTestCase(TestCaseInput testCase) {

        GotTestCase gotTestcase = new GotTestCase();
        JSONObject stageData = new JSONObject();
        gotTestcase.setScenarioId(testCase.getScenarioId());
        gotTestcase.setName(testCase.getName());
        gotTestcase.setDescription(testCase.getDescription());
        gotTestcase.setCaseTemplate(testCase.getCaseTemplate());
        gotTestcase.setLongDescription(testCase.getLongDescription());
        gotTestcase.setCaseGroup(testCase.getCaseGroup());
        gotTestcase.setIsVisible(testCase.getIsVisible());

        stageData.put(StageName.prepareData.name(), testCase.getPrepareData());
        if (testCase.getComponentInfo() != null && testCase.getComponentInfo().size() > 0) {
            stageData.put(StageName.caseRunAgentStage.name(), testCase.getComponentInfo());
        }
        stageData.put(StageName.caseRunStage.name(), testCase.getCaseRunStage());
        gotTestcase.setContent(gson.toJson(stageData));

        System.out.println("[save-testcase-test-log] gotTestCase.getContent: " + gotTestcase.getContent());
        //replace的插入方式
        this.insert(gotTestcase);

        return gotTestcase;
    }


    public Integer getAllCaseNum(Long scenarioId){
        return gotTestcaseMapper.getAllCaseNum(scenarioId);
    }




}
