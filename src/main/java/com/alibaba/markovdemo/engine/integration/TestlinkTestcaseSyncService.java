package com.alibaba.markovdemo.engine.integration;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
import com.alibaba.markovdemo.BO.TestCaseInput;
import com.alibaba.markovdemo.service.TestcaseService;
import com.google.common.collect.Lists;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@ConfigurationProperties(prefix = "testlink")
public class TestlinkTestcaseSyncService {

    private static final Logger logger = LoggerFactory.getLogger(TestlinkTestcaseSyncService.class);

    private String api_endpoint;
    private String devKey;
    private String projectNames;

    public void setApi_endpoint(String api_endpoint) {
        this.api_endpoint = api_endpoint;
    }

    public void setDevKey(String devKey) {
        this.devKey = devKey;
    }

    public void setProjectNames(String projectNames) {
        this.projectNames = new String(Optional.ofNullable(projectNames).orElse("").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    @Autowired
    private TestcaseService testcaseService;

    public void sync() throws Exception {
        List<String> names = Lists.newArrayList(projectNames.split(","));
        TestLinkAPI testLinkAPI = new TestLinkAPI(new URL(api_endpoint), devKey);
        TestProject[] testProjects = testLinkAPI.getProjects();
        for (TestProject testProject : testProjects) {
            if (names.contains(testProject.getName())) {
                TestSuite[] testSuites = testLinkAPI.getFirstLevelTestSuitesForTestProject(testProject.getId());
                for (TestSuite testSuite : testSuites) {
                    fetchTestlinkCasesForSuite(testLinkAPI, testProject, testSuite.getId());
                }
            }
        }
    }

    private void fetchTestlinkCasesForSuite(TestLinkAPI testLinkAPI, TestProject testProject, Integer testSuiteId) {
        TestSuite[] testSuites = testLinkAPI.getTestSuitesForTestSuite(testSuiteId);
        for (TestSuite testSuite : testSuites) {
            TestCase[] cases = testLinkAPI.getTestCasesForTestSuite(testSuite.getId(), false, TestCaseDetails.FULL);
            logger.info(String.valueOf(cases.length));
            for (TestCase testCase : cases) {
                TestCaseInput testCaseInput = new TestCaseInput();
                testCaseInput.setIsVisible(0);
                testCaseInput.setScenarioId(1L);
                testCaseInput.setName(testProject.getPrefix() + "-" + testCase.getExternalId() + " : " + testCase.getName());
                testCaseInput.setLongDescription(Jsoup.parse(testCase.getSummary()).text());
                testCaseInput.setContent("{\"prepareData\":[],\"caseRunStage\":[{\"data\":[{\"input\":\"\",\"expect\":\"\"}]}]}");

                List<String> keywords = Optional.ofNullable(testCase.getKeywords()).orElse(Lists.newLinkedList());
                keywords.add("Version " + testCase.getVersionId());
                keywords.add(testProject.getPrefix());
                testCaseInput.setTag(String.join(",", keywords));

                testCaseInput.setCaseTemplate("java");
                testCaseInput.setCaseGroup(testSuite.getName());
                testcaseService.addNewTestCase(testCaseInput);
            }
            fetchTestlinkCasesForSuite(testLinkAPI, testProject, testSuite.getId());
        }
    }

}
