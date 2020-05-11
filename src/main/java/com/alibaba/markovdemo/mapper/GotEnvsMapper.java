package com.alibaba.markovdemo.mapper;

import com.alibaba.markovdemo.entity.GotEnvs;
import com.alibaba.markovdemo.entity.GotTestCase;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface GotEnvsMapper {

    //获取环境列表
    List<GotEnvs>  findByScenarioId(Long scenarioId);

    //创建环境
    int insert(GotEnvs gotEnvs);

    //删除环境
    void deleteById(Long id);

    //获取环境详情
    GotEnvs findById(Long id);


    void updateCurrentEnv(Long id);

}


