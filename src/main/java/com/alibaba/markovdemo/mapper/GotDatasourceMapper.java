package com.alibaba.markovdemo.mapper;

import com.alibaba.markovdemo.entity.GotDatasource;
import org.springframework.stereotype.Repository;


@Repository
public interface GotDatasourceMapper {
    GotDatasource getDataSource(Long scenarioId);

    void insert(GotDatasource datasource);


}


