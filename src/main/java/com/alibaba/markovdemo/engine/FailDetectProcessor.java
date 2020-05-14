package com.alibaba.markovdemo.engine;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.engine.AI.GACaseGenerator.CovMock;
import com.alibaba.markovdemo.entity.GotCaseAccuracy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FailDetectProcessor {

    /**
     * 失败用例代码级定位
     * 用例的代码覆盖和增量代码需要根据需求自行实现
     * @param scenarioId
     * @return
     */
    public JSONObject analysisCodeDetectAccuracy(Long scenarioId, List<GotCaseAccuracy> caseAccuracyList){
        try{

            List<String> diffLineList = CovMock.getDiffLines();


            JSONArray detailres = new JSONArray();
            JSONArray funcDetailRes = new JSONArray();

            Integer covLineNum = 0;
            Integer ignoreLineNum = 0;
            Integer allLineNum = 0;

            HashMap<String,JSONArray> covMap = new HashMap<>();
            HashMap<String,JSONArray> funcCovMap = new HashMap<>();
            for(String line : diffLineList){

                boolean isignore = false;
                String[] pieces = line.split(":");

                String content = "";
                if(pieces.length == 2){
                    content = pieces[1];
                }else {
                    if(pieces.length>2){
                        for(int i = 1; i < pieces.length-1; i++){
                            content += (pieces[i]+":");
                        }
                        content += pieces[pieces.length-1];
                    }

                }

                if(!content.isEmpty()) {
                    allLineNum++;
                    String fileAndLine = pieces[0].trim();


                    if (content.trim().startsWith("//") || content.trim().startsWith("*") || content.trim().startsWith("/*") || content.trim().startsWith("*/") || "{"
                        .equals(content.trim()) || "}".equals(content.trim())) {
                        isignore = true;
                    }

                    if(isignore){
                        ignoreLineNum++;
                    }

                    try {
                        String[] temp = fileAndLine.split("@");
                        String fileName = temp[0];
                        String fileLine = temp[1];
                        boolean isCoved = false;

                        ArrayList<HashMap<String, String>> caseList = null;
                        ArrayList<HashMap<String, String>> bCaseList = null;


                        JSONObject lineObj = new JSONObject();
                        if(caseList!=null && caseList.size()>0){
                            lineObj.put("caseList",caseList);
                            lineObj.put("covCaseNum",caseList.size());
                            lineObj.put("isCaseFail", true);
                        }else{
                            lineObj.put("covCaseNum",0);
                            lineObj.put("isCaseFail", false);
                        }

                        lineObj.put("lineNo", fileLine);

                        lineObj.put("isFuncLine", !isignore);
                        lineObj.put("isCompCov",false);
                        lineObj.put("isFuncCov",isCoved);
                        lineObj.put("content",content);
                        lineObj.put("isCoved",isCoved);



                        JSONArray fileArray = new JSONArray();
                        JSONArray funcFileArray = new JSONArray();
                        if(covMap.keySet().contains(fileName)){
                            fileArray = covMap.get(fileName);
                        }
                        if(funcCovMap.keySet().contains(fileName)){
                            funcFileArray = funcCovMap.get(fileName);
                        }
                        if(!isignore) {
                            JSONObject tempObj =new JSONObject();
                            tempObj.put("lineNo", fileLine);
                            tempObj.put("isFuncLine", !isignore);
                            tempObj.put("isCompCov",false);
                            tempObj.put("isFuncCov",isCoved);
                            tempObj.put("content",content);
                            tempObj.put("isCoved",isCoved);
                            if(caseList!=null && caseList.size()>0){
                                tempObj.put("caseList",bCaseList);
                                tempObj.put("covCaseNum",caseList.size());
                                tempObj.put("isCaseFail", true);
                            }else{
                                tempObj.put("covCaseNum",0);
                                tempObj.put("isCaseFail", false);
                            }

                            funcFileArray.add(tempObj);
                            funcCovMap.put(fileName, funcFileArray);
                        }
                        fileArray.add(lineObj);
                        covMap.put(fileName, fileArray);

                    }catch (Exception e){
                        continue;
                    }
                }
            }

            for(String fileName : covMap.keySet()){
                JSONObject fileObj = new JSONObject();
                fileObj.put("fileName", fileName);
                fileObj.put("lineList", covMap.get(fileName));

                detailres.add(fileObj);
            }

            for(String fileName : funcCovMap.keySet()){
                JSONObject fileObj = new JSONObject();
                fileObj.put("fileName", fileName);
                fileObj.put("lineList", funcCovMap.get(fileName));

                funcDetailRes.add(fileObj);
            }

            float funcCCRate = 0.0f;
            String funcCCRateStr = "";
            if((allLineNum - ignoreLineNum) == 0){
                funcCCRate = 1.0f;
                funcCCRateStr = "无增量代码";
            }else {
                funcCCRate = ((float) (covLineNum) )/ ((float) (allLineNum - ignoreLineNum));
                funcCCRateStr = String.format("%1.2f", funcCCRate*100) + "%";
            }

            String diffCCRateStr =  funcCCRateStr;

            JSONObject res = new JSONObject();

            res.put("detail",detailres);
            res.put("diffLineNum",diffLineList.size());
            res.put("funcDiffLineNum",allLineNum - ignoreLineNum);
            res.put("diffCovRate",diffCCRateStr);
            res.put("funcCovRate",funcCCRateStr);
            res.put("covLineNum",covLineNum);
            res.put("totalCovLineNum",covLineNum);


            return res;


        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
