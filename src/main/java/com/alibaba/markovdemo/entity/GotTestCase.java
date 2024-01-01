package com.alibaba.markovdemo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GotTestCase extends BaseEntity {

    private Long scenarioId;
    private String name;
    private String description;
    private String longDescription;
    private String content;
    private String caseGroup;
    private Integer isDeleted;
    private String caseTemplate;
    private String features;
    private Date gmtCreate;
    private Date gmtModified;
    private Integer isVisible;

}
