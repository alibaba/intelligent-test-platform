package com.alibaba.markovdemo.service;

import com.alibaba.markovdemo.entity.GotReports;
import com.alibaba.markovdemo.mapper.GotReportsMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class GotReportsService {
    @Autowired
    GotReportsMapper gotReportsMapper;
    Gson gson = new Gson();


    public void insert(GotReports gotReports){
        gotReportsMapper.insert(gotReports);
    }


    public void update(GotReports gotReports){
        gotReportsMapper.update(gotReports);
    }


    public List<Long> getIdsByScenarioId(Long scenarioId){

        return gotReportsMapper.getIdsByScenarioId(scenarioId);
    }


    public List<GotReports> getByScenarioIdPage(Long scenarioId, Integer fromRow, Integer pageSize){

        HashMap map = new HashMap();
        map.put("scenarioId", scenarioId);
        map.put("fromRow", fromRow);
        map.put("pageSize", pageSize);

        return gotReportsMapper.getByScenarioIdPage(map);
    }

    public List<GotReports> getVisibleByScenarioIdPage(Long scenarioId, Integer fromRow, Integer pageSize){

        HashMap map = new HashMap();
        map.put("scenarioId", scenarioId);
        map.put("fromRow", fromRow);
        map.put("pageSize", pageSize);

        return gotReportsMapper.getByScenarioIdPage(map);
    }


    public GotReports findById(Long id){
        GotReports gotReports = gotReportsMapper.findById(id);
        return gotReports;

    }

}
