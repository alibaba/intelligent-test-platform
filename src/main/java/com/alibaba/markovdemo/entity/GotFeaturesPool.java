/*******************************************************************************
 * Copyright (c) 2017 Alibaba Group limited Corp.
 * Building 5, 969 Wenyi West Road, Hangzhou, China.
 * All rights reserved.
 * <p>
 * "[Description of code or deliverable as appropriate] is the copyrighted,
 * proprietary property of Alibaba Group limited Corp
 * which retain all right, title and interest therein."
 * <p>
 * ******************************************************************************
 * <p>
 * Created on 2017-02-08
 * Coat refer to: http://sqi.alibaba-inc.com/alpha/detail.htm?id=278
 *
 * @author SQI Alpha (http://sqi.alibaba-inc.com/alpha/index.htm)
 * @version 1.0.0
 * @since JDK 1.7
 */

package com.alibaba.markovdemo.entity;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class GotFeaturesPool extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long appId;
    private Long scenarioId;
    private Date gmtCreate;
    private Date gmtModified;
    private String features;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }
}