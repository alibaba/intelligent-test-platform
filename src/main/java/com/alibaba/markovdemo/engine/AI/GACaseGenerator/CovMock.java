package com.alibaba.markovdemo.engine.AI.GACaseGenerator;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CovMock {

    /**
     * 此方法mock不同的case在被测程序中的覆盖代码行，返回的数据格式如"path/file@line"
     * 实际使用时需要根据被测程序的语言，选择其他工具（如Jacoco）获取覆盖代码行
     */
    public static List<String> getCaseCovMock(JSONObject caseQuery){
        List<String> codeCovs = new ArrayList<>();
        try{
            String sourceCodeFile = "testapp/process/Test.java";

            String searchKey = caseQuery.getString("search_key");
            Integer topNum = caseQuery.getInteger("top_num");
            Integer matchLevel = caseQuery.getInteger("match_level");
            String userType =caseQuery.getString("user_type");
            Boolean useFeature = caseQuery.getBoolean("use_feature");

            if(searchKey!=null){
                if("key1".equals(searchKey)){
                    codeCovs.add(sourceCodeFile+"@line1");
                }else if("key2".equals(searchKey) || "key3".equals(searchKey) || "key4".equals(searchKey)){
                    codeCovs.add(sourceCodeFile+"@line2");
                }else{
                    codeCovs.add(sourceCodeFile+"@line48");
                }
            }

            if(topNum!=0){
                codeCovs.add(sourceCodeFile+"@line3");
            }

            if(matchLevel!=0 && userType!=null){
                if(matchLevel==1 && ("type1".equals(userType) || "type2".equals(userType)) && useFeature==false){
                    codeCovs.add(sourceCodeFile+"@line4");
                    codeCovs.add(sourceCodeFile+"@line5");
                    codeCovs.add(sourceCodeFile+"@line6");
                    codeCovs.add(sourceCodeFile+"@line7");
                    codeCovs.add(sourceCodeFile+"@line8");
                }else if(matchLevel==1 && "type3".equals(userType) && useFeature==false){
                    codeCovs.add(sourceCodeFile+"@line9");
                    codeCovs.add(sourceCodeFile+"@line10");
                    codeCovs.add(sourceCodeFile+"@line11");
                    codeCovs.add(sourceCodeFile+"@line12");
                    codeCovs.add(sourceCodeFile+"@line13");
                    codeCovs.add(sourceCodeFile+"@line14");
                }else if(matchLevel==1 && ("type1".equals(userType) || "type2".equals(userType)) && useFeature==true){
                    codeCovs.add(sourceCodeFile+"@line15");
                    codeCovs.add(sourceCodeFile+"@line16");
                    codeCovs.add(sourceCodeFile+"@line17");
                    codeCovs.add(sourceCodeFile+"@line18");
                    codeCovs.add(sourceCodeFile+"@line19");
                    codeCovs.add(sourceCodeFile+"@line20");
                }else if(matchLevel==1 && "type3".equals(userType) && useFeature==true){
                    codeCovs.add(sourceCodeFile+"@line21");
                    codeCovs.add(sourceCodeFile+"@line22");
                    codeCovs.add(sourceCodeFile+"@line23");
                    codeCovs.add(sourceCodeFile+"@line24");
                    codeCovs.add(sourceCodeFile+"@line25");
                    codeCovs.add(sourceCodeFile+"@line26");
                    codeCovs.add(sourceCodeFile+"@line27");
                }else if(matchLevel==2 && ("type1".equals(userType) || "type2".equals(userType)) && useFeature==false){
                    codeCovs.add(sourceCodeFile+"@line28");
                    codeCovs.add(sourceCodeFile+"@line29");
                    codeCovs.add(sourceCodeFile+"@line30");
                    codeCovs.add(sourceCodeFile+"@line31");
                }else if(matchLevel==2 && ("type1".equals(userType) || "type2".equals(userType)) && useFeature==true){
                    codeCovs.add(sourceCodeFile+"@line32");
                    codeCovs.add(sourceCodeFile+"@line33");
                    codeCovs.add(sourceCodeFile+"@line34");
                    codeCovs.add(sourceCodeFile+"@line35");
                    codeCovs.add(sourceCodeFile+"@line36");
                }else if(matchLevel==2 && "type3".equals(userType) && useFeature==true){
                    codeCovs.add(sourceCodeFile+"@line37");
                    codeCovs.add(sourceCodeFile+"@line38");
                    codeCovs.add(sourceCodeFile+"@line39");
                    codeCovs.add(sourceCodeFile+"@line40");
                    codeCovs.add(sourceCodeFile+"@line41");
                    codeCovs.add(sourceCodeFile+"@line42");
                }else if(matchLevel==2 && "type3".equals(userType) && useFeature==false){
                    codeCovs.add(sourceCodeFile+"@line43");
                    codeCovs.add(sourceCodeFile+"@line44");
                    codeCovs.add(sourceCodeFile+"@line45");
                    codeCovs.add(sourceCodeFile+"@line46");
                    codeCovs.add(sourceCodeFile+"@line47");
                }else{
                    codeCovs.add(sourceCodeFile+"@line49");
                    codeCovs.add(sourceCodeFile+"@line50");
                    codeCovs.add(sourceCodeFile+"@line51");
                    codeCovs.add(sourceCodeFile+"@line52");
                }
            }

        }catch(Exception e){
            //handle exception
        }
        return codeCovs;
    }


    /**
     * 此方法mock返回被测代码的变动代码行
     * 实际使用时，需要获取被测代码库的代码变动进行解析，推荐使用 git diff
     * 返回格式如 "path/file@line: line_content"
     */
    public static List<String> getDiffLines(){
        List<String> diffLines  = new ArrayList<>();

        String sourceCodeFile = "testapp/process/Test.java";
        for(int i=1; i<=55; i++){
            diffLines.add(sourceCodeFile + "@line" + i + ": mock line content " + i);
        }

        return diffLines;
    }
}
