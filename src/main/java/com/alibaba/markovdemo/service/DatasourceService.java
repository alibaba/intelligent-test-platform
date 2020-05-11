package com.alibaba.markovdemo.service;

import com.alibaba.markovdemo.entity.GotDatasource;
import com.alibaba.markovdemo.mapper.GotDatasourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatasourceService {

    @Autowired
    GotDatasourceMapper gotDatasourceMapper;


    public GotDatasource getDatasource(Long scenarioId){
        return  gotDatasourceMapper.getDataSource(scenarioId);
    }

    public void save(GotDatasource datasource){
        gotDatasourceMapper.insert(datasource);
    }
}
