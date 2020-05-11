package com.alibaba.markovdemo.mapper;

import com.alibaba.markovdemo.entity.GotTestCase;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface GotTestcaseMapper {
    List<GotTestCase>  getCaseByScenarioId(Map map);

    List<GotTestCase>  getVisibleTestcaseByScenario(Map map);

    List<GotTestCase>  getAllCaseByScenarioId(Map map);

    Integer getAllCaseNum(Long scenarioId);

    GotTestCase getTestCaseById(Long id);

    void insert(GotTestCase gotTestCase);

    void update(GotTestCase gotTestCase);


    void delete(Long id);

    List<GotTestCase> getCaseGroupByScenarioId(Long scenarioId);


}


