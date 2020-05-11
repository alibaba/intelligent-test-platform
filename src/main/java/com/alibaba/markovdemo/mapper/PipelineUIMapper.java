package com.alibaba.markovdemo.mapper;

import com.alibaba.markovdemo.entity.PipelineUI;
import org.springframework.stereotype.Repository;


@Repository
public interface PipelineUIMapper {
    PipelineUI getPipelineUI(Long scenarioId);

    void insert(PipelineUI pipelineUI);


}


