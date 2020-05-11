package com.alibaba.markovdemo.service;

import com.alibaba.markovdemo.entity.GotCaseGenerateTask;
import com.alibaba.markovdemo.mapper.GotCaseGenerateTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaseGenerateTaskService {

    @Autowired
    GotCaseGenerateTaskMapper gotCaseGenerateTaskMapper;


    public GotCaseGenerateTask findTaskById(Long taskId){
        return gotCaseGenerateTaskMapper.findById(taskId);
    }


    public void save(GotCaseGenerateTask task){
        gotCaseGenerateTaskMapper.insert(task);
    }

    public void update(GotCaseGenerateTask task){
        gotCaseGenerateTaskMapper.update(task);
    }

    public GotCaseGenerateTask getLastGenerateTask(Long scenarioId){
        return gotCaseGenerateTaskMapper.getLastGenerateTask(scenarioId);
    }
}
