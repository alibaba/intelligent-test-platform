package com.alibaba.markovdemo.controller;

import com.alibaba.markovdemo.common.AjaxResult;
import com.alibaba.markovdemo.engine.EnvsProcessor;
import com.alibaba.markovdemo.entity.EnvsJsonBO;
import com.alibaba.markovdemo.entity.GotEnvs;
import com.alibaba.markovdemo.entity.GotTestCase;
import com.alibaba.markovdemo.service.EnvsService;
import com.alibaba.markovdemo.service.TestcaseService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 测试环境管理操作类
 * 说明:
 * 1.根据不同的环境部署动作由用户自定义,比如基于容器/RPM/java等的部署,此处就不在具体给出,由用户自定义.本次demo采用环境mock的方式
 * 2.每个ScenarioId场景可视为一个具体测试模块,而每个模块可起多套测试环境.
 */
@Controller
@RequestMapping(value = "/api/envs")
public class DeployEnvController {
    @Autowired
    private EnvsService envsService;
    @Autowired
    private EnvsProcessor envsProcessor;
    private Gson gson = new Gson();


    /**
     * 功能:测试环境列表的获取
     * 说明:scenarioId(可视为一个具体测试模块,称之为场景)
     * @param
     * @return
     */
    @RequestMapping(value = "/findByScenarioId", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult findByScenarioId(Long scenarioId){
        try{
            return AjaxResult.succResult(envsProcessor.findByScenarioId(scenarioId));
        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    /**
     * 功能:测试环境的创建
     * 说明:此处走mock,并非在一台真实的机器上部署.创建一个mock表,详情全部走mock的方式.
     * @param envsJsonBO
     * @return
     */
    @RequestMapping(value = "/addEnv", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult addEnv(@RequestBody EnvsJsonBO envsJsonBO ){
        try{

            GotEnvs gotEnvs = new GotEnvs();
            gotEnvs.setName(envsJsonBO.getEnvName());
            gotEnvs.setScenarioId(Long.valueOf(envsJsonBO.getScenarioId()));
            gotEnvs.setStatus("SUCCESS");
            gotEnvs.setHostIp((String) envsJsonBO.getHostInfo().get("ip"));
            gotEnvs.setEnvDetail(envsJsonBO.getDeployGiven());
            return AjaxResult.succResult(envsService.insert(gotEnvs));
        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }


    /**
     * 功能:测试环境的删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.DELETE)
    @ResponseBody
    public AjaxResult deleteById(Long id){
        try{
            envsService.deleteById(id);
            return AjaxResult.succResult("删除成功!");
        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    /**
     * 功能:测试环境详情页
     * @param id
     * @return
     */
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult findById(Long id){
        try{
            return AjaxResult.succResult(envsService.findById(id));
        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }
}
