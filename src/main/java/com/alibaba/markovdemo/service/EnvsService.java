package com.alibaba.markovdemo.service;

import com.alibaba.markovdemo.entity.GotEnvs;
import com.alibaba.markovdemo.entity.GotTestCase;
import com.alibaba.markovdemo.mapper.GotEnvsMapper;
import com.alibaba.markovdemo.mapper.GotTestcaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class EnvsService {
    @Autowired
    GotEnvsMapper gotEnvsMapper;

    //获取环境列表
    public List<GotEnvs>  findByScenarioId(Long scenarioId){
        return gotEnvsMapper.findByScenarioId(scenarioId);
    }

    //创建环境
    public int insert(GotEnvs gotEnvs){
        return gotEnvsMapper.insert(gotEnvs);
    }

    //删除环境
    public void deleteById(Long id){
         gotEnvsMapper.deleteById(id);

    }

    //获取环境详情
    public GotEnvs findById(Long id){
        return gotEnvsMapper.findById(id);

    }
}
