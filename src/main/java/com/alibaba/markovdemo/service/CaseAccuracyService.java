package com.alibaba.markovdemo.service;

import com.alibaba.markovdemo.entity.GotCaseAccuracy;
import com.alibaba.markovdemo.mapper.GotCaseAccuracyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaseAccuracyService {

    @Autowired
    GotCaseAccuracyMapper gotCaseAccuracyMapper;

    public void save(GotCaseAccuracy caseAccuracy){
        gotCaseAccuracyMapper.insert(caseAccuracy);
    }

    public GotCaseAccuracy getLastedByCaseId(Long caseId){
        return gotCaseAccuracyMapper.getLastedByCaseId(caseId);
    }
}
