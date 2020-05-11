package com.alibaba.markovdemo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.BO.MultiAIInfo;
import com.alibaba.markovdemo.BO.TestCaseInput;
import com.alibaba.markovdemo.common.AjaxResult;
import com.alibaba.markovdemo.engine.AI.GACaseGenerator.GenerateController;
import com.alibaba.markovdemo.engine.MultiAIPlusController;
import com.alibaba.markovdemo.engine.SingleController;
import com.alibaba.markovdemo.engine.stages.ResultStatus;
import com.alibaba.markovdemo.engine.util.Toolkit;
import com.alibaba.markovdemo.entity.*;
import com.alibaba.markovdemo.service.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;


/**
 * 说明:用例管理页
 * 1.包含了用例新建/查询/克隆/执行/回归等功能
 * 2.测试环境的选择此处采用mock方式,当然,真实测试场景下仍需要用户去实现真实环境的获取.
 */

@Controller
@RequestMapping(value = "/api")
public class TestcaseController {
    private static final Logger logger = LoggerFactory.getLogger(TestcaseController.class);

    @Autowired
    private GotReportsService gotReportsService;
    @Autowired
    private TestcaseService testcaseService;
    @Autowired
    private SingleController singleController;
    @Autowired
    private PipelineService pipelineService;
    @Autowired
    MultiAIPlusController multiAIController;
    @Autowired
    private TestcaseSnapsService testcaseSnapsService;
    @Autowired
    private GenerateController generateController;
    @Autowired
    private CaseGenerateTaskService caseGenerateTaskService;
    @Autowired
    private CaseAccuracyService caseAccuracyService;


    @Autowired
    private HttpServletRequest request;

    /**
     * 功能:获取测试场景下的分页用例列表
     * @param scenarioId
     * @param pageNo
     * @return
     */
    @RequestMapping(value = "/getTestcaseByScenario", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getTestcaseByScenario(Long scenarioId, Integer pageNo){
        try{
            if(pageNo==null || pageNo==0){
                pageNo=1;
            }

            List<GotTestCase> caseList = testcaseService.getVisibleTestcaseByScenario(scenarioId, pageNo);

            JSONObject res = new JSONObject();
            res.put("testCaseList", caseList);

            Integer allCaseNum = testcaseService.getAllCaseNum(scenarioId);
            res.put("allNumber", allCaseNum);

            return AjaxResult.succResult(res);

        }catch(Exception e){
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    /**
     * 功能:获取用例调试页的布局json
     * 该布局json描述了用例依赖的测试数据类型/测试流程等,与用例数据结合后形成完整的测试流程+测试数据.
     * @param scenarioId
     * @return
     */
    @RequestMapping(value = "/getLayoutJson", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getLayout(Long scenarioId) throws IOException {

        try {
            return AjaxResult.succResult(singleController.getLayout(scenarioId));
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    /**
     * 功能:获取测试用例数据
     * 测试用例数据与布局json结合后形成完整的测试流程+测试数据
     * @param testCaseId
     * @return
     */
    @RequestMapping(value = "/getTestCase", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getTestCase(Long testCaseId){
        try {
            return AjaxResult.succResult(testcaseService.getTestCaseById(testCaseId));
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    /**
     * 功能:
     * 获取指定分组scenarioId下的用例分组列表
     * 注:1.该接口可扩展为指定场景和用例分支,但在demo中我们暂不考虑用例分支的情况
     * 2.用例分组的在回归测试中使用,即用户可选择某类分组进行回归.
     * @param appId
     * @param scenarioId
     * @return
     */
    @RequestMapping(value = "/getCaseGroupListByBranch", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getTestCaseGroupList(Long appId, Long scenarioId){
        try {
            Map<String, Object> map = new HashMap();
            List<String> caseGroupList =testcaseService.getCaseGroupByScenarioId(scenarioId);
            int allNumber = caseGroupList.size();
            map.put("allNumber", allNumber);
            map.put("caseGroupList", caseGroupList);
            return AjaxResult.succResult(map);
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    /**
     * 功能:测试用例保存
     * @param testCase
     * @return
     */
    @RequestMapping(value = "/saveTestCase", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult saveTestCase(@RequestBody TestCaseInput testCase){
        try {
            return AjaxResult.succResult(testcaseService.saveTestCase(testCase));
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    /**
     * 功能:测试用例删除
     * 注意:此处为逻辑删
     * @param testCaseId
     * @return
     */
    @RequestMapping(value = "/deleteTestCase", method = RequestMethod.DELETE)
    @ResponseBody
    public AjaxResult deleteTestCase(Long testCaseId){
        try {
            testcaseService.deleteTestCase(testCaseId);
            return AjaxResult.succResultMessage("delete success.");
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }


    /**
     * 功能:测试用例执行
     * 根据前端传来的测试用例数据,后端初始化执行插件,按照预定义的执行流程依次运行,最终将结果返回给前端
     * 注意:
     * 此处demo采用同步执行机制,便于描述.但在实际中往往我们采用异步执行方式.后续开源如有需要,将逐步开放基于zk的异步执行调用方式
     * @param testCase
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/runSingleTestCase", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult runSingleTestCase(@RequestBody TestCaseInput testCase) throws Exception {
        try {
            GotPipeline gotPipeline = pipelineService.getPipeline(testCase.getScenarioId());
            JSONObject pipelineJsonObj = JSON.parseObject(gotPipeline.getPipeline());
            return AjaxResult.succResult(singleController.runCase(testCase,pipelineJsonObj));
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }

    }

    /**
     * 功能:功能回归测试
     * 注意:
     * 此处demo采用了最基本和最普适的caseBYcase回归机制,便于描述平台思想.后续开源将逐步开放智能回归的高效回归方式.
     * @param multiAIInfo
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/runMultiAITestCase", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult runMultiAITestCase(@RequestBody MultiAIInfo multiAIInfo) throws IOException {
        try {
            Gson gson = new Gson();
            System.out.print(gson.toJson(multiAIInfo));
            multiAIController.runIntelligent(multiAIInfo);
            return AjaxResult.succResult("智能回归测试任务提交成功,请进入执行历史页面查看!");
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage()) ;
        }
    }

    /**
     * 功能:获取测试报告列表
     * 说明:执行一次回归测试后会产生一份测试报告.
     * @param appId
     * @param scenarioId
     * @param pageId
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/getReportList", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getReportList(Long appId, Long scenarioId, Integer pageId, Integer pageSize){

        try {

            Map<String, Object> map = new HashMap();
            Integer allNumber = 0;
            List<GotReports> reportsList;
            Integer fromRow = (pageId - 1) * pageSize;
            allNumber = gotReportsService.getIdsByScenarioId(scenarioId).size();
            reportsList = gotReportsService.getVisibleByScenarioIdPage(scenarioId, fromRow, pageSize);
//            for (GotReports gotReports : reportsList){
//                gotReports.setGmtCreateStr(sdf.format(gotReports.getGmtCreate()));
//                gotReports.setGmtModifiedStr(sdf.format(gotReports.getGmtModified()));
//            }
            map.put("allNumber", allNumber);
            map.put("reportsList", reportsList);
            return AjaxResult.succResult(map);
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    /**
     * 功能:获取测试报告详情
     * 说明:详情包含了测试执行时间/用例快照列表/执行用例数/回归的交付物信息等..
     * @param testReportId
     * @param pageId
     * @param pageSize
     * @param status
     * @return
     */
    @RequestMapping(value = "/getReportTestCase", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getReportTestCase(Long testReportId, Integer pageId, Integer pageSize, String status){
        try {
            Map map = new HashMap();
            Integer runNumber;
            Integer sucessNumber= 0;
            Integer failureNumber = 0;
            Integer allStatusNumber = 0;
            //获取执行人,回归时间,回归名称等
            GotReports gotReports = gotReportsService.findById(testReportId);
            String reportName = gotReports.getReportName();
            String user = gotReports.getUser();
            Date gmtCreate = gotReports.getGmtCreate();
            Date gmtModified = gotReports.getGmtModified();
            int caseNum = gotReports.getCaseNum();
            double seconds = (double)(gmtModified.getTime()-gmtCreate.getTime());
            //统计用例数
            List<GotTestcaseSnaps> testcaseList = testcaseSnapsService.getReportTestcaseList(testReportId);
            runNumber = testcaseList.size();
            for (GotTestcaseSnaps testcase : testcaseList) {
                if (!testcase.getStatus().equals(ResultStatus.SUCCESS.name())) {
                    failureNumber++;
                }
                else{
                    sucessNumber++;
                }
            }
            Integer fromRow = (pageId - 1) * pageSize;
            List<GotTestcaseSnaps> testcaseListPage = testcaseSnapsService.getReportTestcaseListPage(testReportId, fromRow, pageSize,status);


            List<Long> caseids = new ArrayList<>();
            for (GotTestcaseSnaps gotTestcaseSnaps :testcaseListPage){
                caseids.add(gotTestcaseSnaps.getTestcaseId());
            }

            if(status == null){
                allStatusNumber = runNumber;
            }
            else if(status.contains("ERROR")){
                allStatusNumber = failureNumber;
            }
            else if(status.contains("SUCCESS")){
                allStatusNumber = runNumber - failureNumber;
            }
            else{
                allStatusNumber = 0;
            }

            map.put("status", gotReports.getStatus());
            map.put("allNumber", caseNum);
            map.put("runNumber", runNumber);
            map.put("sucessNumber", sucessNumber);
            map.put("allStatusNumber", allStatusNumber);
            map.put("failureNumber", failureNumber);
            map.put("testcaseList", testcaseListPage);
            map.put("gmtCreate", gmtCreate);
            map.put("gmtModified", gmtModified);
            map.put("reportName", reportName);
            map.put("user", user);
            map.put("timeGap", Toolkit.changeCostFormat((long) seconds));
            map.put("imageName",gotReports.getImageName());

            return AjaxResult.succResult(map);
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }

    @RequestMapping(value = "/getReport", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getReport( Long testReportId){

        try {
            GotReports gotReport = gotReportsService.findById(testReportId);

            JSONObject analysisObj = JSONObject.parseObject(gotReport.getAnalysis());
            return AjaxResult.succResult(analysisObj);
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }



    @RequestMapping(value = "/startAICaseGenerator", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult startAICaseGenerator( @RequestBody JSONObject params){

        try {
            String caseId = params.getString("caseId");
            Long scenarioId = params.getLong("scenarioId");
            String fieldConf = params.getString("fieldConf");
            HashMap<String, Object> envInfo = (HashMap<String, Object>)params.get("envInfo");
            JSONObject res = new JSONObject();
            res.put("taskId", generateController.startGenerateTask(Long.parseLong(caseId), scenarioId, fieldConf, envInfo));
            return AjaxResult.succResult(res);
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }


    @RequestMapping(value = "/getDefaultGeneConf", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getDefaultGeneConf( Long scenarioId){

        try {
            //just mock
            String defaultConf = "{\n" +
                    "\t\"neg_tesla_key\": [\n" +
                    "\t\t\"ad_id\"\n" +
                    "\t],\n" +
                    "\t\"field_setting\": {\n" +
                    "\t\t\"search_key\": [\"key1\", \"key2\", \"key3\", \"key4\", \"key5\", \"key6\", \"key7\"],\n" +
                    "\t\t\"match_level\": [1, 2, 3, 4, 5, 6],\n" +
                    "\t\t\"user_type\": [\"type1\", \"type2\", \"type3\", \"type4\", \"type5\"],\n" +
                    "\t\t\"top_num\": [10, 11, 12, 13, 14, 15],\n" +
                    "\t\t\"use_feature\": [true, false],\n" +
                    "\t\t\"other1\": [\"0\", \"1\", \"2\", \"3\"],\n" +
                    "\t\t\"other2\": [\"0\", \"1\", \"2\", \"3\"]\n" +
                    "\t}\n" +
                    "}";

            return AjaxResult.succResult(defaultConf);
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }


    @RequestMapping(value = "/getGeneratorTaskDetail", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getGeneratorTaskDetail( Long taskId){

        try {
            GotCaseGenerateTask task = caseGenerateTaskService.findTaskById(taskId);
            JSONObject res = new JSONObject();
            res.put("task", task);
            try {
                JSONArray cases = JSONObject.parseObject(task.getTaskResult()).getJSONArray("valid_ids");


                JSONArray caseArray = new JSONArray();
                for (Object caseId : cases) {
                    GotTestCase testcase = testcaseService.findById(Long.parseLong(caseId.toString()));
                    GotCaseAccuracy accuracy = caseAccuracyService.getLastedByCaseId(Long.parseLong(caseId.toString()));
                    JSONObject temp = new JSONObject();
                    temp.put("caseid", testcase.getId());
                    temp.put("caseName", testcase.getDescription());
                    temp.put("accuracy", accuracy.getCovLine());
                    caseArray.add(temp);
                }
                res.put("validCaseInfo", caseArray);
            }catch (Exception e){

            }

            return AjaxResult.succResult(res);
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }


    @RequestMapping(value = "/changeVisible", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult changeVisible( Long caseId){

        try {
            //just mock
            GotTestCase testCase = testcaseService.findById(caseId);
            testCase.setIsVisible(0);
            testcaseService.update(testCase);

            return AjaxResult.succResult(true);
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }


    @RequestMapping(value = "/getLastGenerateTask", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getLastGenerateTask( Long scenarioId){

        try {
            //just mock
            GotCaseGenerateTask task = caseGenerateTaskService.getLastGenerateTask(scenarioId);
            if(task!= null){
                return AjaxResult.succResult(task.getId());
            }else{
                return AjaxResult.errorResult(null);
            }
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }


}
