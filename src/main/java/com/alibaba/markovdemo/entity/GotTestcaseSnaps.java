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

public class GotTestcaseSnaps extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Date gmtCreate;
    private Date gmtModified;
    private Long scenarioId;
    private Long appId;
    private Long testreportId;
    private Long testcaseId;
    private String name;
    private String description;
    private String longDescription;
    private String content;
    private String status;
    private String caseGroup;
    private String tag;
    private String version;
    //运行时间
    private long runTime;
    //运行时间
    private String runTimeStr;
    //重试次数
    private int retryNum;
    //稳定性
    private String constancy;
    //执行环境
    private String envName;
    //冲突的数据说明
    private String conflictDesc;
    //是否在并行队列
    private boolean isParallel;
    private int  isTrunkFlag;

    //智能归因
    private String troubleShootBox;
    //人工排查失败原因
    private String troubleShootManual;

    public String getTroubleShootManual() {
        return troubleShootManual;
    }

    public void setTroubleShootManual(String troubleShootManual) {
        this.troubleShootManual = troubleShootManual;
    }

    public int getIsTrunkFlag() {
        return isTrunkFlag;
    }

    public void setIsTrunkFlag(int isTrunkFlag) {
        this.isTrunkFlag = isTrunkFlag;
    }

    public String getTroubleShootBox() {
        return troubleShootBox;
    }

    public void setTroubleShootBox(String troubleShootBox) {
        this.troubleShootBox = troubleShootBox;
    }

    public int getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(int retryNum) {
        this.retryNum = retryNum;
    }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }
    public String getRunTimeStr() {
        return runTimeStr;
    }

    public void setRunTimeStr(String runTimeStr) {
        this.runTimeStr = runTimeStr;
    }


    public String getConstancy() {
        return constancy;
    }

    public void setConstancy(String constancy) {
        this.constancy = constancy;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getConflictDesc() {
        return conflictDesc;
    }

    public void setConflictDesc(String conflictDesc) {
        this.conflictDesc = conflictDesc;
    }

    public boolean isParallel() {
        return isParallel;
    }

    public void setParallel(boolean parallel) {
        isParallel = parallel;
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

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getTestreportId() {
        return testreportId;
    }

    public void setTestreportId(Long testreportId) {
        this.testreportId = testreportId;
    }

    public Long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(Long testcaseId) {
        this.testcaseId = testcaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCaseGroup() {
        return caseGroup;
    }

    public void setCaseGroup(String caseGroup) {
        this.caseGroup = caseGroup;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}