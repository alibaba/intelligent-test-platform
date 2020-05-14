package com.alibaba.markovdemo.engine;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.TestCaseInput;
import com.alibaba.markovdemo.BO.TestRespone;
import com.alibaba.markovdemo.common.Layout;
import com.alibaba.markovdemo.engine.plugins.Pipeline;

import com.alibaba.markovdemo.engine.stages.*;
import com.alibaba.markovdemo.engine.util.Toolkit;
import com.alibaba.markovdemo.entity.GotDatasource;
import com.alibaba.markovdemo.entity.GotPipeline;
import com.alibaba.markovdemo.entity.PipelineImpl;
import com.alibaba.markovdemo.service.DatasourceService;
import com.alibaba.markovdemo.service.PipelineService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SingleController {

    private static final Logger logger = LoggerFactory.getLogger(SingleController.class);

    private static final String PARAMS = "params";


    @Autowired
    PipelineService pipelineService;
    @Autowired
    DatasourceService datasourceService;

    /**
     * 函数功能:根据pipeline获取动态布局
     * @param scenarioId
     */
    public Layout getLayout(final Long scenarioId) throws IOException {

        // 获取pipeline
        GotPipeline gotPipeline = pipelineService.getPipeline(scenarioId);
        JSONObject pipelineJsonObj = JSON.parseObject(gotPipeline.getPipeline());

        //获取datasource
        Layout layout = new Layout();
        layout.setCaseName("case名");
        layout.setCaseDese("case描述,展现在列表页的简单描述");
        layout.setCaseLongDese("case描述,展现在列表页的简单描述");

        Pipeline pipelineObj = new Pipeline();
        pipelineObj.build(pipelineJsonObj);
        //数据准备阶段
        PrepareDataStage prepareDataStage = (PrepareDataStage)pipelineObj.flow.get(StageName.prepareData);
        if (prepareDataStage != null){
            //生成pipeline阶段中的数据准备流程
            layout.getPipelineStage().put(StageName.prepareData, prepareDataStage.buildLayout());

        }
        //执行阶段
        CompositeBaseStage runStage = (CompositeBaseStage)pipelineObj.flow.get(StageName.caseRunStage);
        if (runStage!= null){
            //生成pipeline阶段中的run流程
            layout.getPipelineStage().put(StageName.caseRunStage,runStage.buildPipelineLayout());
            layout.setCaseRunStage(runStage.buildCaseRunLayout());
        }

        //todo:此处直接使用pipeline中的demo数据源配置,为了方便,但在实际中可以不这么做,而是走数据源动态化的方式
        try {
            layout.setDataPrepareStageNew(pipelineJsonObj.getJSONObject("layoutConf").getJSONObject("dataPrepareStageNew"));
        }catch(Exception e){
            e.printStackTrace();
        }
        return layout;
    }

    /**
     * 函数功能:占位符替换
     * @param testCase
     * @return
     */
    public String placeholderReplace(TestCaseInput testCase) {
        //将转化为json串后整体进行替换
        Gson gosn = new Gson();
        String testCaseJsonStr = gosn.toJson(testCase);
        return Toolkit.placeholderReplace(testCaseJsonStr);
    }


    /**
     * 函数功能:单次用例运行
     * @param testCase
     * @param pipelineJsonObj
     * @return
     */
    public TestRespone runCase(TestCaseInput testCase, JSONObject pipelineJsonObj){

        Map<StageName,Object> stageParams = new HashMap<>();
        TestRespone testRespone = new TestRespone();
        try{

            //环境初始化
            //todo:to be fill.. 用户可根据自己测试服务进行测试环境初始化工作,比如基于容器镜像/rpm的部署方式
            logger.info("加载环境为:"+testCase.getEnvName());
            //初始化pipeline
            PipelineImpl pipelineObj = new PipelineImpl();
            pipelineObj.build(pipelineJsonObj);

            testRespone.setCaseTemplate("C++");
            //加入部署数据
            stageParams.put(StageName.deploy,testCase.getEnvName());
            //用例占位符替换
            String testCaseStr = placeholderReplace(testCase);
            //初始化testcase
            TestCaseData testCaseDataObj= TestCaseData.build(JSONObject.parseObject(testCaseStr));
            Map<String,List<RunData>> stageRunDataMap = new HashMap<>();
            //执行每阶段:1.数据准备,2.用例执行
            for(StageName stageName :pipelineObj.flow.keySet()){

                logger.info("[运行开始],当前阶段:" + stageName.name());
                BaseStage bs = pipelineObj.flow.get(stageName);
                bs.setStageRunDataMap(stageRunDataMap);
                //设置testcase输入
                if (testCaseDataObj.testCaseInputMap.containsKey(stageName)){
                    bs.setIputData(testCaseDataObj.testCaseInputMap.get(stageName));
                    //初始化RunData,作为执行参数,将在各个插件间进行传递
                    bs.setRunData();
                }
                //设置stage之间传递的参数
                bs.setStageParams(stageParams);
                int retryNum = getRetryNum(bs.getStageJsonObj());
                int i = 0;
                //判断是否需要重试,如果最终结果是
                do{
                    i++;
                    if(i > 1){
                        int n = i-1;
                        logger.info("[失败重试运行中],当前重试第" + n + "次");
                    }
                    //执行before_exec, exec, after_exec
                    bs.beforeExec();
                    bs.exec();
                    bs.afterExec();
                    stageRunDataMap.put(stageName.name(),bs.getRunDataList());
                    stageParams = bs.getStageParams();
                    //填充返回结果
                    testRespone = bs.setTestRespone(testRespone);
                } while(i <= retryNum && !testRespone.getStatus().equals(ResultStatus.SUCCESS));

            }

        }
        //如有抛异常
        catch (Exception e) {
            logger.error("运行过程出现异常,请检查!");
            testRespone.setStatus(ResultStatus.ERROR);

        }
        return testRespone;

    }

    /**
     * 函数功能:获取重试次数
     * 可在pipeline中进行配置
     * @param pipelineBsJson
     * @return
     */
    public int getRetryNum(JSONObject pipelineBsJson){
        int retryNum = 0;
        if (pipelineBsJson != null && pipelineBsJson.containsKey(PARAMS)){
            JSONObject paramsObj = (JSONObject)pipelineBsJson.get(PARAMS);
            retryNum = paramsObj.containsKey("retryNum") ? (int)paramsObj.get("retryNum") : 0;
        }
        return retryNum;
    }





}
