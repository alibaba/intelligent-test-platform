package com.alibaba.markovdemo.mapper;

import com.alibaba.markovdemo.entity.GotFeaturesPool;
import com.alibaba.markovdemo.entity.GotPipeline;
import org.springframework.stereotype.Repository;


@Repository
public interface GotFeaturesPoolMapper {


    GotFeaturesPool findByScenarioId(long scenrioId);

    void insert(GotFeaturesPool gotFeaturesPool);


}


