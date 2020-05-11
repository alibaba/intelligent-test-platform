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

package com.alibaba.markovdemo.mapper;

import com.alibaba.markovdemo.entity.GotCaseGenerateTask;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GotCaseGenerateTaskMapper{

    List<GotCaseGenerateTask> getByScenarioId(Long scenarioId);

    //创建任务
    int insert(GotCaseGenerateTask generateTask);

    //获取任务
    GotCaseGenerateTask findById(Long id);

    //更新任务
    void update(GotCaseGenerateTask generateTask);

    GotCaseGenerateTask getLastGenerateTask(Long scenarioId);


}