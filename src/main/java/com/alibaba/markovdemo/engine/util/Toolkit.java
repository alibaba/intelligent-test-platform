package com.alibaba.markovdemo.engine.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

public class Toolkit {
    private static final Logger logger = LoggerFactory.getLogger(Toolkit.class);


    private static String IGNORE_FIELDS = "ignoreFields";
    private static String PREGMATCH_FIELDS = "pregMatchFields";
    private static String NUMCHECKFIELDS = "numCheckFields";
    private static String CALL_FUNC_FIELDS = "callFuncFields";
    private static String NON_EXSIT_FIELDS = "nonExsitFields";

    private static String UNSORT_FIELDS = "unsortFields";
    private static String COUNT_FIELDS = "countFields";

    private static String ACTUAL = "actual";
    private static String EXPECT = "expect";
    private static String IS_SUCCESS = "isSuccess";
    private static String MESSAGE = "message";

    private static String SORT_TYPE = "sort";
    private static String REMOVE_TYPE = "remove";
    private static String COUNT_TYPE = "count";
    private static String NUM_CHECK_TYPE = "num_check";

    private static String PREG_MATCH_CHECK = "字段值正则匹配不相等!";
    private static String NON_EXSIT_CHECK = "字段不应该存在!";
    private static String FUNC_CHECK = "字段值函数化处理后不相等!";
    private static String VALUE_CHECK = "字段值不相等!";
    private static String KEY_NO_EXIST_CHECK = "字段在实际结果中不存在!!";
    private static String IGNORE_CAL = "实际返回已忽略字段，如下:";
    private static String SORT_CAL = "实际返回已将无序字段处理为有序，如下:";
    private static String PREG_MATCH_CAL = "实际返回已将字段转化为正则匹配校验，如下:";
    private static String NON_EXSIT_CAL = "实际返回已对字段进行存在与否校验，如下:";
    private static String COUNT_CAL = "实际返回已将字段值转为个数，如下:";
    private static String NUM_CHECK_CAL = "实际返回中对部分字段进行数量校验，如下:";
    private static String FUNC_CAL = "实际返回已将字段进行特殊化处理:";

    private static String RES = "res";
    private static String CAL = "cal";
    private static String PRE_SINGAL = "$$$$${{{{{";


    private static String MENU_GROUP_ID = "markov";
    private static String MENU_DATA_MENU_ID = "menu_reflection";
    private static String MENU_DATA_MENU_ID_4BU = "menu_reflection_4_bu";

    /**
     * @param elementSeprator List元素的连接符
     * @param list            待连接的List
     * @return
     */
    public static String implode(String elementSeprator, List<String> list) {

        String result = "";

        if (list.size() == 0) {
            return result;
        }

        int i;
        for (i = 0; i < list.size() - 1; i++) {
            result += list.get(i) + elementSeprator;
        }
        result += list.get(i);
        return result;
    }

    public static String getPre(String content) {

        String pre = "";
        try {
            JSONObject obj = JSON.parseObject(content);
            Integer appId = (Integer) obj.get("appId");
            Integer scenarioId = (Integer) obj.get("scenarioId");
            Integer caseId = 0;
            if (obj.containsKey("id")) {
                caseId = (Integer) obj.get("id");
            }
            pre = appId.toString() + scenarioId.toString() + caseId.toString();
            return pre;
        } catch (Exception e) {
            return pre;
        }
    }

    public static String placeholderReplace(String content, String pre) {

        String reg = "\\$\\{[a-zA-Z0-9,\\^ :\\-=_;]+\\}";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            //获取匹配
            String matchWord = matcher.group(0);
            //如果为时间变量
            if (matchWord.contains("datetime")) {
                content = content.replace(matchWord, dateTimeGet(matchWord));
            }
            //不可见separator
            else if (matchWord.contains("separator")) {
                content = content.replace(matchWord, genSeparator(matchWord));
            }
            //xml的属性匹配
            else if (matchWord.contains("property")) {
                content = content.replace(matchWord, genXmlProperty(matchWord));
            }
            //shardId变量
            else if (matchWord.contains("shardId")) {
                content = content.replace(matchWord, genShardId(genStrId(pre + matchWord, 8)));
            }
            //如果为普通变量
            else {
                content = content.replace(matchWord, genStrId(pre + matchWord, 8));
            }
        }
        return content;
    }

    public static String genShardId(String id) {

        String newId = null;
        for (int i = 1; i < 100; i++) {
            newId = i + id;
            if (getShardId(Long.valueOf(newId)) == 0) {
                return newId;
            }
        }
        return id;
    }

    public static int getShardId(Long id) {

        id = id & 65535;
        int shardCount = 2;
        int base = 65536 / shardCount;
        int remain = 65536 % shardCount;

        for (int shardId = 0; shardId < shardCount; ++shardId) {
            int start = base * shardId + (shardId < remain ? shardId : remain);
            int stop = base * (shardId + 1) + (shardId < remain ? shardId + 1 : remain);
            if (id >= start && id < stop) {
                return shardId;
            }
        }
        return -1;
    }

    /**
     * 占位符替换,目前支持id和时间变量
     *
     * @param content
     * @return
     */
    public static String placeholderReplace(String content) {

        String reg = "\\$\\{[a-zA-Z0-9,\\^ :\\-\\+=_;]+\\}";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(content);
        String pre = getPre(content);
        while (matcher.find()) {
            //获取匹配
            String matchWord = matcher.group(0);

            //如果为时间变量
            if (matchWord.contains("datetime")) {
                content = content.replace(matchWord, dateTimeGet(matchWord));
            }
            //不可见separator
            else if (matchWord.contains("separator")) {
                content = content.replace(matchWord, genSeparator(matchWord));
            }
            //xml的属性匹配
            else if (matchWord.contains("property")) {
                content = content.replace(matchWord, genXmlProperty(matchWord));
            }
            //shardId变量
            else if (matchWord.contains("shardId")) {
                content = content.replace(matchWord, genShardId(genStrId(pre + matchWord, 8)));
            } else if (matchWord.contains("skip")) {
                String matchWordNew = matchWord.replace("{skip,", PRE_SINGAL);
                content = content.replace(matchWord, matchWordNew);
            }
            //如果为普通变量
            else {
                content = content.replace(matchWord, genStrId(pre + matchWord, 8));
            }
        }
        content = content.replace(PRE_SINGAL, "{");

        return content;
    }


    public static String genXmlProperty(String content) {
        String[] arr = content.split(",");

        try {
            String propertyStr = arr[1].trim().replace("}", "");
            String[] propertyArr = propertyStr.split(";");
            String res = "";
            for (String property : propertyArr) {

                String[] kv = property.split("=");
                String k = kv[0].trim();
                String v = kv[1].trim();
                String p = " " + k + "='" + v + "'";
                res += p;
            }

            return res;

        } catch (Exception e) {
            logger.error("xml属性转化失败:" + e.getMessage());
            return content;
        }

    }

    public static String genSeparator(String content) {

        String[] arr = content.split(",");
        try {
            String pattern = arr[1].trim().replace("}", "");
            String kv;
            switch (pattern) {
                case "^A":
                    kv = "\u0001";
                    break;
                case "^B":
                    kv = "\u0002";
                    break;
                case "^C":
                    kv = "\u0003";
                    break;
                case "^D":
                    kv = "\u0004";
                    break;
                case "^E":
                    kv = "\u0005";
                    break;
                case "^F":
                    kv = "\u0006";
                    break;
                default:
                    kv = content;
            }

            return kv;

        } catch (Exception e) {
            System.out.println("不可见字符转化失败:" + e.getMessage());
            return content;
        }
    }

    /**
     * 将string转化为唯一id
     *
     * @param content
     * @return
     */
    public static String genStrId(String content, int num) {
        CRC32 crc32 = new CRC32();
        crc32.update(content.getBytes());
        Long crc32Value = crc32.getValue();
        String crc32ValueStr = crc32Value.toString();
        if (crc32ValueStr.length() < num) {
            return crc32ValueStr.substring(0, crc32ValueStr.length());
        }
        return crc32Value.toString().substring(0, num);
    }


    public static String genRepeatId(Integer start, Integer i, Integer repeatNum, Integer mutiple) {

        List<String> idList = new ArrayList<>();

        start = (start + i) * mutiple;
        for (int t = 0; t < repeatNum; t++) {
            idList.add(String.valueOf(start));
        }
        return implode(",", idList);
    }


    /**
     * //获取当前时间
     * //${datetime,now,yyyy-MM-dd HH:mm:ss}
     * //${datetime,-1 day,yyyy-MM-dd HH:mm:ss}
     * //${datetime,-1 hour,timestamp}
     * //${datetime,-1 minute,timestamp}
     *
     * @param datetimeStr
     * @return
     */
    public static String dateTimeGet(String datetimeStr) {

        String[] dateArr = datetimeStr.split(",");
        try {

            String curTime = dateArr[1].trim();
            String pattern = dateArr[2].trim().replace("}", "");

            Date date = new Date();//取时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            SimpleDateFormat formatter;
            //校验格式
            if (!"now".equals(curTime)) {
                String[] timeArr = curTime.split(" ");
                Integer offset = Integer.valueOf(timeArr[0]);
                String timeType = timeArr[1];
                if ("day".equals(timeType)) {
                    calendar.add(Calendar.DATE, offset);//把日期往前减少一天，若想把日期向后推一天则将负数改为正数
                }
                if ("hour".equals(timeType)) {
                    calendar.add(Calendar.HOUR, offset);//把日期往前减少一小时，若想把日期向后推一天则将负数改为正数
                }
                if ("minute".equals(timeType)) {
                    calendar.add(Calendar.MINUTE, offset);//把日期往前减少一分钟，若想把日期向后推一天则将负数改为正数
                }
            }

            date = calendar.getTime();

            //时间戳
            if ("timestamp".equals(pattern) || "timeStamp".equals(pattern)) {
                formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date dateUnix = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(formatter.format(date));
                long unixTimestamp = dateUnix.getTime() / 1000;
                return String.valueOf(unixTimestamp);
            } else {
                formatter = new SimpleDateFormat(pattern);
                return formatter.format(date);
            }

        } catch (Exception e) {
            logger.error("时间转化失败:" + e.getMessage());

            return datetimeStr;
        }
    }

    public static Boolean isJsonObjcet(String in) {
        if (in == null || in.isEmpty()) {
            return false;
        }
        try {
            JSONObject.parseObject(in);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static Boolean isJsonArray(String in) {
        if (in == null || in.isEmpty()) {
            return false;
        }
        try {
            JSONArray.parseArray(in);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否是需要正则匹配字段
     *
     * @return
     */
    public static boolean isPregMatch(String expectKey, List<String> pregMatchList) {


        if (pregMatchList.size() > 0) {

            expectKey = removeIndexFieldName(expectKey);
            if (pregMatchList.contains(expectKey)) {
                return true;
            }
        }

        return false;

    }


    /**
     * 去除序号相关
     *
     * @param fileName
     * @return
     */
    public static String removeIndexFieldName(String fileName) {


        String match = "\\[[0-9]+\\]";
        Pattern pattern = Pattern.compile(match);
        Matcher matcher = pattern.matcher(fileName);
        // 查找字符串中是否有匹配正则表达式的字符/字符串
        while (matcher.find()) {
            String matchWord = matcher.group(0);
            fileName = fileName.replace(matchWord, "");
        }
        return fileName;
    }


    /**
     * 判断是否是需要校验不存在的字段
     *
     * @return
     */
    public static boolean isNonExsitCheck(String actualKey, List<String> nonExsitList) {


        if (nonExsitList.size() > 0) {

            actualKey = removeIndexFieldName(actualKey);
            boolean isIn = false;
            for (String field : nonExsitList) {
                if (actualKey.equals(field)) {
                    isIn = true;
                } else if (actualKey.startsWith(field) && actualKey.charAt(field.length()) == '.') {
                    isIn = true;
                }
            }

            return isIn;
        }

        return false;

    }

    public static boolean isCallFunc(String expectKey, Map<String, String> pregMatchList) {


        if (pregMatchList.size() > 0) {

            expectKey = removeIndexFieldName(expectKey);
            if (pregMatchList.containsKey(expectKey)) {
                return true;
            }
        }

        return false;

    }


    /**
     * 获取匹配的list
     *
     * @return
     */
    public static List<String> calPregMatchList(String calFieldsConfig) {

        List<String> pregMatchList = new ArrayList<>();

        if (calFieldsConfig == null || !isJsonObjcet(calFieldsConfig)) {
            return pregMatchList;
        }

        JSONObject calFieldsObj = JSONObject.parseObject(calFieldsConfig);

        if (!calFieldsObj.containsKey(PREGMATCH_FIELDS)) {
            return pregMatchList;
        }
        pregMatchList = (List<String>) calFieldsObj.get(PREGMATCH_FIELDS);

        return pregMatchList;

    }


    /**
     * 获取匹配的list
     *
     * @return
     */
    public static List<String> calNonExsitList(String calFieldsConfig) {

        List<String> pregMatchList = new ArrayList<>();

        if (calFieldsConfig == null || !isJsonObjcet(calFieldsConfig)) {
            return pregMatchList;
        }

        JSONObject calFieldsObj = JSONObject.parseObject(calFieldsConfig);

        if (!calFieldsObj.containsKey(NON_EXSIT_FIELDS)) {
            return pregMatchList;
        }
        pregMatchList = (List<String>) calFieldsObj.get(NON_EXSIT_FIELDS);

        return pregMatchList;

    }

    public static List<String> getNumCheckList(String calFieldsConfig) {

        List<String> numCheckList = new ArrayList<>();

        if (calFieldsConfig == null || !isJsonObjcet(calFieldsConfig)) {
            return numCheckList;
        }

        JSONObject calFieldsObj = JSONObject.parseObject(calFieldsConfig);

        if (!calFieldsObj.containsKey(NUMCHECKFIELDS)) {
            return numCheckList;
        }
        numCheckList = (List<String>) calFieldsObj.get(NUMCHECKFIELDS);

        return numCheckList;

    }


    public static Map<String, String> getFuncList(String calFieldsConfig) {

        Map<String, String> matchList = new HashMap<>();

        if (calFieldsConfig == null || !isJsonObjcet(calFieldsConfig)) {
            return matchList;
        }

        JSONObject calFieldsObj = JSONObject.parseObject(calFieldsConfig);

        if (!calFieldsObj.containsKey(CALL_FUNC_FIELDS)) {
            return matchList;
        }
        matchList = (Map<String, String>) calFieldsObj.get(CALL_FUNC_FIELDS);

        return matchList;

    }

    /**
     * 实际结果预处理,可处理以下几类:1.忽略字段,2.字段正则匹配. 3.无序字段处理
     *
     * @param actual
     * @param calFieldsConfig
     * @return
     */
    public static JSONObject systemPreCal(String expect, String actual, String calFieldsConfig) {


        JSONObject allObj = new JSONObject();
        JSONObject resObj = new JSONObject();
        HashMap<String, List<String>> calMap = new HashMap<>();


        try {
            //会先进行处理
            if (calFieldsConfig == null || !isJsonObjcet(calFieldsConfig)) {
                resObj.put(ACTUAL, actual);
                resObj.put(EXPECT, expect);
                resObj.put(IS_SUCCESS, true);
                allObj.put(RES, resObj);
                allObj.put(CAL, calMap);
                return allObj;
            }
            JSONObject calFieldsObj = JSONObject.parseObject(calFieldsConfig);
            JSONObject actualObj = JSONObject.parseObject(actual);
            JSONObject expectObj = JSONObject.parseObject(expect);

            //处理忽略字段
            if (calFieldsObj.containsKey(IGNORE_FIELDS)) {

                List<String> ignoreFieldsList = (List<String>) calFieldsObj.get(IGNORE_FIELDS);

                if (ignoreFieldsList.size() > 0) {
                    if (!calMap.containsKey(IGNORE_CAL)) {
                        calMap.put(IGNORE_CAL, ignoreFieldsList);
                    }

                    for (String fieldName : ignoreFieldsList) {
                        List<String> fieldNameList = new ArrayList<>(Arrays.asList(fieldName.split("\\.")));
                        recursionCalJson(fieldNameList, expectObj, REMOVE_TYPE);
                        recursionCalJson(fieldNameList, actualObj, REMOVE_TYPE);
                    }
                }

            }

            //处理COUNT字段,只比较数目，不比较内容
            if (calFieldsObj.containsKey(COUNT_FIELDS)) {

                List<String> countFieldsList = (List<String>) calFieldsObj.get(COUNT_FIELDS);

                if (countFieldsList.size() > 0) {
                    if (!calMap.containsKey(COUNT_CAL)) {
                        calMap.put(COUNT_CAL, countFieldsList);
                    }

                    for (String fieldName : countFieldsList) {
                        List<String> fieldNameList = new ArrayList<>(Arrays.asList(fieldName.split("\\.")));
                        recursionCalJson(fieldNameList, expectObj, COUNT_TYPE);
                        recursionCalJson(fieldNameList, actualObj, COUNT_TYPE);
                    }
                }

            }


            //处理num check字段，既比较数目，又比较内容
            if (calFieldsObj.containsKey(NUMCHECKFIELDS)) {

                List<String> countFieldsList = (List<String>) calFieldsObj.get(NUMCHECKFIELDS);

                if (countFieldsList.size() > 0) {
                    if (!calMap.containsKey(NUM_CHECK_CAL)) {
                        calMap.put(NUM_CHECK_CAL, countFieldsList);
                    }

                    for (String fieldName : countFieldsList) {
                        List<String> fieldNameList = new ArrayList<>(Arrays.asList(fieldName.split("\\.")));
                        recursionCalJson(fieldNameList, expectObj, NUM_CHECK_TYPE);
                        recursionCalJson(fieldNameList, actualObj, NUM_CHECK_TYPE);
                    }
                }

            }

            //处理排序字段
            if (calFieldsObj.containsKey(UNSORT_FIELDS)) {


                List<String> sortFieldsList = (List<String>) calFieldsObj.get(UNSORT_FIELDS);

                if (sortFieldsList.size() > 0) {
                    if (!calMap.containsKey(SORT_CAL)) {
                        calMap.put(SORT_CAL, sortFieldsList);
                    }
                    for (String fieldName : sortFieldsList) {
                        List<String> fieldNameList = new ArrayList<>(Arrays.asList(fieldName.split("\\.")));
//                        recursionCalJson(fieldNameList, expectObj, SORT_TYPE);
                        recursionCalJson(fieldNameList, actualObj, SORT_TYPE);
                    }
                }
            }
            resObj.put(ACTUAL, JSONObject.toJSONString(actualObj));
            resObj.put(EXPECT, JSONObject.toJSONString(expectObj));
            resObj.put(IS_SUCCESS, true);
        } catch (Exception e) {
            resObj.put(ACTUAL, actual);
            resObj.put(EXPECT, expect);
            resObj.put(IS_SUCCESS, false);
            resObj.put(MESSAGE, e.getMessage());
        }

        allObj.put(RES, resObj);
        allObj.put(CAL, calMap);

        return allObj;

    }

    public static void recursionCalJson(List<String> fieldNameList, JSONObject obj, String type) {


        //只支持obj为JSON的情况
        //一层
        if (fieldNameList.size() == 1) {
            String layer1 = fieldNameList.get(0);
            //如果命中
            if (obj.containsKey(layer1)) {
                //处理
                calJsonLeaf(obj, layer1, type);
            }
        }

        //多层
        if (fieldNameList.size() > 1) {

            String layer1 = fieldNameList.get(0);
            String layer2 = fieldNameList.get(1);

            //命中第一层
            if (obj.containsKey(layer1)) {
                Object obj2 = obj.get(layer1);

                //如果是JSONObject
                if (obj2 instanceof JSONObject) {
                    JSONObject jobj2 = (JSONObject) obj2;
                    //如果命中
                    if (jobj2.containsKey(layer2)) {
                        fieldNameList.remove(0);
                        recursionCalJson(fieldNameList, jobj2, type);
                    }
                }
                //如果是jsonArr
                else if (obj2 instanceof JSONArray) {
                    JSONArray jsonArrayobj2 = (JSONArray) obj2;
                    for (Object unitObj : jsonArrayobj2) {
                        JSONObject jobj2 = (JSONObject) unitObj;
                        List<String> tmpfieldNameList = cloneList(fieldNameList);
                        //如果命中
                        if (jobj2.containsKey(layer2)) {
                            tmpfieldNameList.remove(0);
                            recursionCalJson(tmpfieldNameList, jobj2, type);
                        }
                    }

                }
            }

        }
    }

    public static void calJsonLeaf(JSONObject obj, String layer, String type) {

        if (type.equals(REMOVE_TYPE)) {
            obj.remove(layer);
        }

        if (type.equals(COUNT_TYPE)) {
            int count = 1;
            Object unitObj = obj.get(layer);
            if (unitObj instanceof JSONArray) {
                count = ((JSONArray) unitObj).size();
            }
            String countValue = "数目为" + count;
            obj.put(layer, countValue);
        }

        if (type.equals(NUM_CHECK_TYPE)) {
            int count = 1;
            Object unitObj = obj.get(layer);
            if (unitObj instanceof JSONArray) {
                count = ((JSONArray) unitObj).size();
            }
            String countValue = "数目为" + count;
            obj.put(layer + "_num", countValue);
        }

        if (type.equals(SORT_TYPE)) {
            Object unitObj = obj.get(layer);

            if (unitObj instanceof JSONArray) {
                JSONArray jObj = (JSONArray) unitObj;
                //对jObj进行排序
                if (jObj.size() > 1) {
                    List<String> tmpList = new ArrayList<>();
                    for (Object tmpObj : jObj) {
                        JSONObject tmpjObj = (JSONObject) tmpObj;
                        tmpList.add(tmpjObj.toString());
                    }
                    tmpList.sort(Comparator.reverseOrder());
                    JSONArray newJObj = new JSONArray();
                    for (String str : tmpList) {
                        newJObj.add(JSONObject.parseObject(str));
                    }
                    obj.put(layer, newJObj);
                }

            }
        }

    }

    public static List<String> cloneList(List<String> souceList) {

        List<String> desList = new ArrayList<>();
        for (String value : souceList) {
            desList.add(value);
        }
        return desList;
    }


    /**
     * 获取错误堆栈内容
     *
     * @param e
     * @return
     */
    public static String getErrorStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }

    public static String changeCostFormat(Long cost) {

        String time = "";
        //转换为秒
        if (cost == 0) {
            return "1s";
        }
//        long hourGap = 3600*1000;
        long minGap = 60 * 1000;
        long secGap = 1000;
        Long hour = 0L;
        Long min = 0L;
        Long second = 0L;


//        //转化为小时
//        if (cost > hourGap){
//            hour=cost/(hourGap);
//            cost=cost%(hourGap);
//        }


        //转化为分钟
        if (cost > minGap) {
            min = cost / (minGap);
            cost = cost % (minGap);
        }


        //转化为sec
        if (cost > secGap) {
            second = cost / (secGap);
        }
//
//        if (hour != 0){
//            time += hour +"hour";
//        }
        if (min != 0) {
            time += min + "min";
        }
        if (second == 0) {
            time += "1s";
        } else {
            time += second + "s";
        }

        return time;
    }

}

