package com.alibaba.markovdemo.service;

import com.alibaba.markovdemo.entity.GotTestcaseSnaps;
import com.alibaba.markovdemo.mapper.GotTestcaseSnapsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestcaseSnapsService {
    @Autowired
    GotTestcaseSnapsMapper gotTestcaseSnapMapper;


    public Long insert(GotTestcaseSnaps gotTestCase){
        return gotTestcaseSnapMapper.insert(gotTestCase);
    }

    public Long update(GotTestcaseSnaps gotTestCase){
        return gotTestcaseSnapMapper.update(gotTestCase);
    }



    public List<GotTestcaseSnaps> getReportTestcaseList(Long testReportId){

        return gotTestcaseSnapMapper.getReportTestcaseList(testReportId);

    }

    public  List<GotTestcaseSnaps> getReportTestcaseListPage(Long testReportId, Integer fromRow, Integer pageSize, String status){

        Map<String,Object> map = new HashMap<>();
        map.put("testReportId", testReportId);
        map.put("fromRow", fromRow);
        map.put("pageSize", pageSize);
        map.put("status", status);
        return gotTestcaseSnapMapper.getReportTestcaseListPage(map);


    }
}
