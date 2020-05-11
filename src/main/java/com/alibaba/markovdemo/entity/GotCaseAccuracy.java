/*******************************************************************************
 * Copyright (c) 2017 Alibaba Group limited Corp.
 * Building 5, 969 Wenyi West Road, Hangzhou, China.
 * All rights reserved.
 *
 * "[Description of code or deliverable as appropriate] is the copyrighted,
 * proprietary property of Alibaba Group limited Corp
 * which retain all right, title and interest therein."
 *
 *******************************************************************************
 *
 * Created on 2017-02-08
 * Coat refer to: http://sqi.alibaba-inc.com/alpha/detail.htm?id=278
 * @author SQI Alpha (http://sqi.alibaba-inc.com/alpha/index.htm)
 * @version 1.0.0
 * @since JDK 1.7
 *
 */

package com.alibaba.markovdemo.entity;

import java.util.Date;

/**
 * 保存执行配置快照
 */
public class GotCaseAccuracy  extends BaseEntity {
    private Date gmtCreate;
    private Date gmtModified;
    private Long caseId;
    private Long exeId;
    private String covLine;
    private String collectType;

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

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getExeId() {
        return exeId;
    }

    public void setExeId(Long exeId) {
        this.exeId = exeId;
    }

    public String getCovLine() {
        return covLine;
    }

    public void setCovLine(String covLine) {
        this.covLine = covLine;
    }

    public String getCollectType() {
        return collectType;
    }

    public void setCollectType(String collectType) {
        this.collectType = collectType;
    }
}