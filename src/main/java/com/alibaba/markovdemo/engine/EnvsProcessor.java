package com.alibaba.markovdemo.engine;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.entity.GotEnvs;
import com.alibaba.markovdemo.service.EnvsService;
import com.alibaba.markovdemo.service.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EnvsProcessor {

    @Autowired
    private EnvsService envsService;
    @Autowired
    private PipelineService pipelineService;
    private String REMAINS = "remain";
    private String IP = "ip";
    private String MAX_ENV = "maxEnv";
    private String ENVS = "envs";
    private String HOST_INFO = "hostInfo";
    private String NEW_DEPLOY_STAGE = "new-deploy-stage";
    int maxEnv = 10;


    /**
     * 功能:获取指定ScenarioId下的测试环境list
     * @param ScenarioId
     * @return
     */
    public List<JSONObject> findByScenarioId(Long ScenarioId){

        //获取ScenarioId绑定的pipeline配置
        JSONObject pipelineObj = JSONObject.parseObject(pipelineService.getPipeline(ScenarioId).getPipeline());
        JSONObject deployObj = pipelineObj.getJSONObject(NEW_DEPLOY_STAGE);
        JSONObject hostObj = deployObj.getJSONObject(HOST_INFO);

        List<JSONObject> list = new ArrayList<>();
        JSONObject envUnit = new JSONObject();
        Map<String,List<GotEnvs>> envMap = new HashMap<>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<GotEnvs> gotEnvsList = envsService.findByScenarioId(ScenarioId);

        //pipeline可添加任意测试机器,与db中的测试环境进行map.
        for (GotEnvs gotEnvs : gotEnvsList){

            if (!envMap.containsKey(gotEnvs.getHostIp())){
                envMap.put(gotEnvs.getHostIp(),new ArrayList<>());
            }
            envMap.get(gotEnvs.getHostIp()).add(gotEnvs);
        }
        for (String ip : envMap.keySet()){
            List<GotEnvs> envList = envMap.get(ip);

            envUnit.put(MAX_ENV,maxEnv);
            int remains = maxEnv- envList.size();
            if (remains<0){
                remains = 0;
            }
            envUnit.put(REMAINS, remains);
            envUnit.put(IP, ip);
            envUnit.put(ENVS, envList);
            JSONObject hostInfo = new JSONObject();
            hostInfo.put(IP, ip);
            envUnit.put(HOST_INFO, hostInfo);
            list.add(envUnit);
        }

        if (list.size() == 0){
            envUnit.put(MAX_ENV,maxEnv);
            envUnit.put(REMAINS, maxEnv);
            envUnit.put(IP, hostObj.getString("ip"));
            envUnit.put(ENVS, new ArrayList<>());
            JSONObject hostInfo = new JSONObject();
            hostInfo.put(IP, hostObj.getString("ip"));
            envUnit.put(HOST_INFO, hostInfo);
            list.add(envUnit);
        }

        return list;
    }


}
