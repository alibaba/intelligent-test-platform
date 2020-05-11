package com.alibaba.markovdemo.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.PipelineFlow;
import com.alibaba.markovdemo.common.AjaxResult;
import com.alibaba.markovdemo.entity.GotDatasource;
import com.alibaba.markovdemo.entity.GotPipeline;
import com.alibaba.markovdemo.entity.PipelineUI;
import com.alibaba.markovdemo.service.DatasourceService;
import com.alibaba.markovdemo.service.PipelineService;
import com.alibaba.markovdemo.service.PipelineUIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * pipeline配置管理
 * 1.pipeline可视为一个测试模块的所有测试流程配置,可以理解为一个配置json,包含了测试机器配置/数据准备插件/发送和校验插件/测试流程定义等
 * 2.此处的配置管理,在demo中我们仅用最简单的json来描述pipeline,demo进行简单的文本类配置增删改.如有需要,可升级为更好的可视化维护.
 */
@Controller
@RequestMapping(value = "/api")
public class PipelineController {

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private DatasourceService datasourceService;



    /**
     * 功能:pipeline的获取
     * @param scenarioId
     * @return
     */
    @RequestMapping(value = "/getPipeline", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getPipeline(Long scenarioId)  {
        try {
            GotPipeline gotPipeline = pipelineService.getPipeline(scenarioId);
            return AjaxResult.succResult(gotPipeline.getPipeline());
        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }


    /**
     * 功能:保存pipeline配置文件
     * @param pipelineFlow
     * @return
     */
    @RequestMapping(value = "savePipelineFlow", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult savePipelineFlow(@RequestBody PipelineFlow pipelineFlow)  {

        try {
            //1.datasource,即测试数据源保存
            GotDatasource datasource = new GotDatasource();
            datasource.setScenarioId(pipelineFlow.getScenarioId());
            datasource.setContent(pipelineFlow.getDataSource());
            datasourceService.save(datasource);
            //2.pipeline保存
            GotPipeline gotPipeline = new GotPipeline();
            gotPipeline.setScenarioId(pipelineFlow.getScenarioId());
            gotPipeline.setTag(pipelineFlow.getTag());
            gotPipeline.setPipeline(pipelineFlow.getPipeline());
            pipelineService.save(gotPipeline);
            return  AjaxResult.succResult("保存成功");

        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }

    }


    /**
     * 功能:查询pipelineFlow
     * @param scenarioId
     * @return
     */
    @RequestMapping(value = "/getPipelineFlow", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getPipelineFlow(Long scenarioId){
        //todo:此处是PipelineFlow的拓展,即可通过1.文本类json来描述pipeline,也可通过2.前端可视化UI来描述,此处仅实现1.
        try {
            //1.简单的文本类json方式来描述pipeline
            return AjaxResult.succResult(getUserPipelineFlow(scenarioId));
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    public PipelineFlow getUserPipelineFlow(Long scenarioId){

        PipelineFlow pipelineFlow = new PipelineFlow();
        //获取pipeline
        GotPipeline gotPipeline = pipelineService.getPipeline(scenarioId);
        //获取datasource
        GotDatasource gotDatasource = datasourceService.getDatasource(scenarioId);
        //设置pipelineFlow
        pipelineFlow.setPipeline(gotPipeline.getPipeline());
        pipelineFlow.setDataSource(gotDatasource.getContent());
        pipelineFlow.setScenarioId(scenarioId);
        pipelineFlow.setPipeline(pipelineService.getPipeline(scenarioId).getPipeline());
        return pipelineFlow;
    }

}
