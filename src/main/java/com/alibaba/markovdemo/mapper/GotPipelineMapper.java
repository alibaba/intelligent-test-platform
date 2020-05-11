package com.alibaba.markovdemo.mapper;

import com.alibaba.markovdemo.entity.GotPipeline;
import org.springframework.stereotype.Repository;


@Repository
public interface GotPipelineMapper {
    GotPipeline getPipeline(long scenrioId);

    void insert(GotPipeline pipeline);


}


