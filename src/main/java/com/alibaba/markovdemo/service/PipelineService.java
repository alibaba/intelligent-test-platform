package com.alibaba.markovdemo.service;

import com.alibaba.markovdemo.entity.GotPipeline;
import com.alibaba.markovdemo.mapper.GotPipelineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PipelineService {

    @Autowired
    GotPipelineMapper gotPipelineMapper;

    public GotPipeline getPipeline(Long scenarioId){
        return  gotPipelineMapper.getPipeline(scenarioId);
    }


    public void save(GotPipeline pipeline){
        gotPipelineMapper.insert(pipeline);
    }
}
