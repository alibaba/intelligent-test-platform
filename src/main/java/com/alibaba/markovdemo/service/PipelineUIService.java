package com.alibaba.markovdemo.service;

import com.alibaba.markovdemo.entity.PipelineUI;
import com.alibaba.markovdemo.mapper.PipelineUIMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PipelineUIService {
    @Autowired
    PipelineUIMapper pipelineUIMapper;

    public PipelineUI getPipelineUI(Long scenarioId){
        return pipelineUIMapper.getPipelineUI(scenarioId);
    }


    public void  save(PipelineUI pipelineUI){
        pipelineUIMapper.insert(pipelineUI);
    }
}
