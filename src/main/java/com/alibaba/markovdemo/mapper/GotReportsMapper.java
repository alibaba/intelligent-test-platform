package com.alibaba.markovdemo.mapper;

import com.alibaba.markovdemo.entity.GotReports;
import com.alibaba.markovdemo.entity.GotTestCase;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface GotReportsMapper {

    void insert(GotReports gotReports);

    void update(GotReports gotReports);

    List<Long> getIdsByScenarioId(Long scenarioId);

    List<GotReports> getByScenarioIdPage(Map map);

    GotReports findById(Long id);
}


