package com.alibaba.markovdemo.mapper;

import com.alibaba.markovdemo.entity.GotTestCase;
import com.alibaba.markovdemo.entity.GotTestcaseSnaps;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface GotTestcaseSnapsMapper {


    Long insert(GotTestcaseSnaps gotTestcaseSnaps);

    Long update(GotTestcaseSnaps gotTestcaseSnaps);


    List<GotTestcaseSnaps> getReportTestcaseList(Long testReportId);

    List<GotTestcaseSnaps> getReportTestcaseListPage(Map<String,Object> map) ;
}


