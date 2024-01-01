package com.alibaba.markovdemo.BO;

import lombok.Data;

import java.util.List;

@Data
public class FeatureInfo {
    Long scenarioId;
    Integer topN;
    List<String> featureList;
    String caseDesc;
}
