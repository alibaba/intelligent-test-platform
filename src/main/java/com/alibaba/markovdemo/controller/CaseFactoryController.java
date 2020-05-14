package com.alibaba.markovdemo.controller;


import com.alibaba.markovdemo.BO.*;
import com.alibaba.markovdemo.common.AjaxResult;
import com.alibaba.markovdemo.engine.CaseFactoryProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * 包含了用例智能推荐/用例膨胀两部分功能接口
 */
@Controller
@RequestMapping(value = "api/caseFactory")
public class CaseFactoryController {

    @Autowired
    CaseFactoryProcessor caseFactoryProcessor;
    /**
     * 接口功能:全量特征抽取,执行后将所有用例进行统一特征抽取.
     * @param scenarioId
     * @return
     */
    @RequestMapping(value = "/featuresExtract", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult featuresExtract(Long scenarioId){
        try{
            caseFactoryProcessor.featuresExtract(scenarioId);
            return AjaxResult.succResult("特征抽取任务提交!");

        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }


    /**
     * 接口功能:特征列表获取
     * @param scenarioId
     * @return
     */
    @RequestMapping(value = "/getFeaturesList", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getFeaturesList(Long scenarioId){
        try{

            return AjaxResult.succResult(caseFactoryProcessor.getFeaturesList(scenarioId));

        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }


    /**
     * 接口功能:获取相似度最高的topN用例列表
     * @param featureInfo
     * @return
     */
    @RequestMapping(value = "/featuresMatch", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult featuresMatch(@RequestBody FeatureInfo featureInfo){
        try{

            return AjaxResult.succResult(caseFactoryProcessor.featuresMatch(featureInfo));

        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }


    /**
     * 接口功能:获取可进行用例膨胀的特征字段列表
     * @param testCaseInput
     * @return
     */
    @RequestMapping(value = "/getExpandFeatures", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult getExpandFeatures(@RequestBody TestCaseInput testCaseInput){
        try{
            return AjaxResult.succResult(caseFactoryProcessor.getExpandFeatures(testCaseInput));

        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    /**
     * 接口功能:获取膨胀组合集
     * 如:布尔组合,int型组合,Long型组合,字符串组合
     * @param
     * @return
     */
    @RequestMapping(value = "/getFeatureCombination", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getFeatureCombination(){
        try{
            return AjaxResult.succResult(caseFactoryProcessor.initFeatureCombination());
        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }


    /**
     * 接口功能:获取膨胀用例列表
     * @return
     */
    @RequestMapping(value = "/getExpandCases", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult getExpandCases(@RequestBody ExpandInfo expandInfo){
        try{
            return AjaxResult.succResult(caseFactoryProcessor.getExpandCases(expandInfo));
        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    /**
     * 接口功能:保存批量用例
     * @param data
     * @return
     */
    @RequestMapping(value = "/saveCaseList", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult saveCaseList(@RequestBody MultiCase data){
        try {

            return AjaxResult.succResult(caseFactoryProcessor.saveCaseList(data));
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }

    }

}
