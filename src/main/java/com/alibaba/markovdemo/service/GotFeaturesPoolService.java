package com.alibaba.markovdemo.service;

import com.alibaba.markovdemo.entity.GotFeaturesPool;
import com.alibaba.markovdemo.entity.GotReports;
import com.alibaba.markovdemo.mapper.GotFeaturesPoolMapper;
import com.alibaba.markovdemo.mapper.GotReportsMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class GotFeaturesPoolService {
    @Autowired
    GotFeaturesPoolMapper gotFeaturesPoolMapper;
    Gson gson = new Gson();


    public void insert(GotFeaturesPool gotFeaturesPool){
        gotFeaturesPoolMapper.insert(gotFeaturesPool);
    }


    public GotFeaturesPool findByScenarioId(Long scenarioId){

        return gotFeaturesPoolMapper.findByScenarioId(scenarioId);
    }


}
