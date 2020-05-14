package com.alibaba.markovdemo.engine.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.text.ParseException;
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

    public static String strtotime(String str, String seprator) {

        if (str.indexOf("timeStamp" +
                "" +
                "(") > 0 || str.indexOf("date(") > 0) {
            List<String> strList = new ArrayList<String>();
            Calendar calendar = Calendar.getInstance();
            int date;
            String[] timeList = str.split(seprator + "\\(");
            for (String elem : timeList) {
                String[] tmp = elem.split("\\)");
                if (tmp.length == 2) {
                    String timeStamp = "";
                    if (tmp[0].contains(" days")) {
                        tmp[0] = tmp[0].replace(" days", "");

                        date = Integer.parseInt(tmp[0]);
                        calendar.add(Calendar.DAY_OF_YEAR, date);
                    }
                    if (tmp[0].contains(" hours")) {
                        tmp[0] = tmp[0].replace(" hours", "");
                        date = Integer.parseInt(tmp[0]);
                        calendar.add(Calendar.HOUR_OF_DAY, date);
                    }
                    if ("timeStamp".equals(seprator)) {
                        timeStamp = String.valueOf(calendar.getTimeInMillis() / 1000);
                    } else {
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
                        timeStamp = (format.format(calendar.getTime()));
                    }

                    strList.add(timeStamp);
                    strList.add(tmp[1]);
                }
                if (tmp.length == 1) {
                    strList.add(tmp[0]);
                }
            }
            str = implode("", strList);
        }
        return str;
    }

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

    public static String implode(String elementSeprator, Set<Long> set) {

        List list = new ArrayList(set);
        String result = "";
        int i;

        if (list.size() == 0) {
            return result;
        }

        for (i = 0; i < list.size() - 1; i++) {
            result += list.get(i) + elementSeprator;
        }
        result += list.get(i);
        return result;
    }


    /**
     * 函数功能
     * 将String按照分隔符切分,写入map中
     *
     * @param str
     * @param elementSeprator
     * @param kvSeprator
     * @return
     */
    public static Map<String, String> explode2Map(String str, String elementSeprator, String kvSeprator) {

        Map<String, String> map = new HashMap<String, String>();
        String[] arr = str.split(elementSeprator);
        List<String> tmp;
        String key = null;
        String value = null;
        for (int i = 0; i < arr.length; i++) {
            tmp = new ArrayList<String>(Arrays.asList(arr[i].split(kvSeprator)));
            if (tmp.size() == 2) {
                key = tmp.get(0);
                value = tmp.get(1);
            } else if (tmp.size() > 2) {
                key = tmp.get(0);
                tmp.remove(0);
                value = implode(kvSeprator, tmp);
            }
            map.put(key, value);
        }
        return map;
    }



    public static String explode2Json(String str, String elementSeprator, String kvSeprator) {

        JSONObject map = new JSONObject();
        String[] arr = str.split(elementSeprator);
        List<String> tmp;
        String key = null;
        String value = null;
        for (int i = 0; i < arr.length; i++) {
            tmp = new ArrayList<String>(Arrays.asList(arr[i].split(kvSeprator)));
            if (tmp.size() == 2) {
                key = tmp.get(0);
                value = tmp.get(1);
            } else if (tmp.size() > 2) {
                key = tmp.get(0);
                tmp.remove(0);
                value = implode(kvSeprator, tmp);
                //System.out.println(key + ":" + value);
            } else if (tmp.size() == 1) {
                key = tmp.get(0);
                value = "";
            } else {
                continue;
            }
            key = key.trim();
            value = value.trim();
            map.put(key, value);
        }
        return map.toString();
    }

    public static String getType(Object o) {
        return o.getClass().toString();
    }

    /**
     * 函数功能,进行map的连接
     */
    public static String implodeMapObject(String kvSeprator, String elementSeprator, JSONObject mapObject) {

        //拼接成msg
        List<String> list = new ArrayList<String>();
        Object value;
        String element;
        String pre = "'";

        for (String key : mapObject.keySet()) {
            value = mapObject.get(key);
            if (getType(value).contains("com.alibaba.fastjson.JSONArray")) {
                for (Object fieldValue : (JSONArray) value) {
                    element = key + kvSeprator + fieldValue;
                    list.add(element);
                }
            } else if (getType(value).contains("java.lang.String") && !"ubs_action".equals(key)) {
                element = key + kvSeprator + pre + value + pre;
                list.add(element);
            } else {
                element = key + kvSeprator + value;
                list.add(element);
            }

        }

        return implode(elementSeprator, list);
    }

    /**
     * 函数功能,进行map的连接
     */
    public static String implodeMap(String kvSeprator, String elementSeprator, JSONObject mapObject) {

        //拼接成msg
        List<String> list = new ArrayList<String>();
        Object value;
        String element;
        String pre = "";

        for (String key : mapObject.keySet()) {
            value = mapObject.get(key);
            if (getType(value).contains("com.alibaba.fastjson.JSONArray")) {
                System.out.println(value);
                for (Object fieldValue : (JSONArray) value) {
                    element = key + kvSeprator + fieldValue;
                    list.add(element);
                }
            } else if (getType(value).contains("java.lang.String") && !"ubs_action".equals(key)) {
                element = key + kvSeprator + pre + value + pre;
                list.add(element);
            } else {
                element = key + kvSeprator + value;
                list.add(element);
            }

        }

        return implode(elementSeprator, list);
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


    public static String commonJsonCompare(String expect, String actual, String calFieldsConfig) {

        String result = "";
        //将两个json进行转换,进行比较
        try {

            if (expect == null || "".equals(expect)) {
                expect = "{}";
            }
            if (actual == null || "".equals(actual)) {
                actual = "{}";
            }
            if (calFieldsConfig == null || "".equals(calFieldsConfig)) {
                calFieldsConfig = "{}";
            }


            String reExpect = expect;
            String reActual = actual;
            Field valueFieldOfString = String.class.getDeclaredField("value");
            valueFieldOfString.setAccessible(true);

            if (isJsonArray(actual)) {
                JSONObject obj = new JSONObject();
                obj.put("JSONArray", JSONArray.parse(actual));
                actual = obj.toString();
            }
            if (isJsonArray(expect)) {
                JSONObject obj = new JSONObject();
                obj.put("JSONArray", JSONArray.parse(expect));
                expect = obj.toString();
            }

            //系统预处理=> 系统提供公共的处理,比如设置 忽略字段 or 字段有序无序 or 正则匹配
            JSONObject allObj = systemPreCal(expect, actual, calFieldsConfig);

            JSONObject resObj = (JSONObject) allObj.get(RES);
            HashMap<String, List<String>> calMapPre = (HashMap<String, List<String>>) allObj.get(CAL);

            expect = (String) resObj.get(EXPECT);
            actual = (String) resObj.get(ACTUAL);
            //反射机制,将实际和期望修改后的值带出去
            valueFieldOfString.set(reExpect, expect.toCharArray());
            valueFieldOfString.set(reActual, actual.toCharArray());

            HashMap<String, List<String>> errorMap = new HashMap<>();
            HashMap<String, List<String>> calMap = new HashMap<>();
            calMap.putAll(calMapPre);

            JsonPathParser expectParser = new JsonPathParser(expect);
            JsonPathParser actualParser = new JsonPathParser(actual);
            JSONObject expectObj = JSONObject.parseObject(expectParser.getPathJsonStr());
            JSONObject actualObj = JSONObject.parseObject(actualParser.getPathJsonStr());
            String expectValue;
            String actualValue;


            List<String> pregMatchList = calPregMatchList(calFieldsConfig);
            Map<String, String> callFuncMap = getFuncList(calFieldsConfig);
            List<String> numCheckList = getNumCheckList(calFieldsConfig);
            List<String> nonExsitList = calNonExsitList(calFieldsConfig);

            if (("{}".equals(expect) || "{}".equals(actual)) && !expect.equals(actual)) {
                if (!errorMap.containsKey(VALUE_CHECK)) {
                    errorMap.put(VALUE_CHECK, new ArrayList<>());
                }
                errorMap.get(VALUE_CHECK).add("1.实际和期望不相等!");
            }

            //校验应该不存在的key
            for (String actualKey : actualObj.keySet()) {
                if (isNonExsitCheck(actualKey, nonExsitList)) {
                    //if exsit
                    actualValue = String.valueOf(actualObj.get(actualKey));
                    if (!errorMap.containsKey(NON_EXSIT_CHECK)) {
                        errorMap.put(NON_EXSIT_CHECK, new ArrayList<>());
                    }
                    if (!calMap.containsKey(NON_EXSIT_CAL)) {
                        calMap.put(NON_EXSIT_CAL, new ArrayList<>());
                    }

                    errorMap.get(NON_EXSIT_CHECK).add(".字段名:" + actualKey + ",期望: 不出现" + " VS 实际:" + actualValue);
                    calMap.get(NON_EXSIT_CAL).add(".字段名:" + actualKey);
                }
            }

            for (String expectKey : expectObj.keySet()) {

                //存在相同key,进行对比
                if (actualObj.containsKey(expectKey)) {
                    expectValue = String.valueOf(expectObj.get(expectKey));
                    actualValue = String.valueOf(actualObj.get(expectKey));
                    //如果不相等
                    if (!expectValue.equals(actualValue)) {
                        //判断是否正则匹配
                        if (isPregMatch(expectKey, pregMatchList)) {
                            //执行正则
                            Pattern pattern = Pattern.compile(expectValue);
                            Matcher matcher = pattern.matcher(actualValue);
                            // 查找字符串中是否有匹配正则表达式的字符/字符串
                            if (!matcher.find()) {
                                if (!errorMap.containsKey(PREG_MATCH_CHECK)) {
                                    errorMap.put(PREG_MATCH_CHECK, new ArrayList<>());
                                }
                                if (!calMap.containsKey(PREG_MATCH_CAL)) {
                                    calMap.put(PREG_MATCH_CAL, new ArrayList<>());
                                }

                                errorMap.get(PREG_MATCH_CHECK).add(".字段名:" + expectKey + ",期望:" + expectValue + " VS 实际:" + actualValue);
                                calMap.get(PREG_MATCH_CAL).add(".字段名:" + expectKey);
                            }

                        }//判断是否动态调用处理函数
                        else if (isCallFunc(expectKey, callFuncMap)) {
                            String expectCalKey = removeIndexFieldName(expectKey);
                            String funcName = callFuncMap.get(expectCalKey);

                            String[] funcNameList = funcName.split("\\[");
                            String funcDesc = "";
                            String func = FUNC_CAL;
                            if (funcNameList.length == 2) {
                                funcName = funcNameList[0];
                                funcDesc = funcNameList[1];
                                func += funcDesc;
                            }

                            Class<?> threadClazz = Class.forName("com.alimama.zhizi.engine.util.Toolkit");
                            Method method = threadClazz.getMethod(funcName, String.class, String.class);
                            boolean res = (boolean) method.invoke(null, expectValue, actualValue);

                            if (!calMap.containsKey(func)) {
                                calMap.put(func, new ArrayList<>());
                            }

                            if (!res) {
                                if (!errorMap.containsKey(FUNC_CHECK)) {
                                    errorMap.put(FUNC_CHECK, new ArrayList<>());
                                }
                                errorMap.get(FUNC_CHECK).add(".字段名:" + expectKey + ",特殊处理函数:" + funcName + ",期望:" + expectValue + " VS 实际:" + actualValue);
                                calMap.get(func).add(".字段名:" + expectKey);
                            }

                        } else {
                            if (!errorMap.containsKey(VALUE_CHECK)) {
                                errorMap.put(VALUE_CHECK, new ArrayList<>());
                            }
                            errorMap.get(VALUE_CHECK).add(".字段名:" + expectKey + ",期望:" + expectValue + " VS 实际:" + actualValue);
                        }
                    }

                }
                //key不存在
                else {
                    if (!errorMap.containsKey(KEY_NO_EXIST_CHECK)) {
                        errorMap.put(KEY_NO_EXIST_CHECK, new ArrayList<>());
                    }
                    errorMap.get(KEY_NO_EXIST_CHECK).add("字段名:" + expectKey);
                }
            }

            if (errorMap.size() == 0) {
                result = "校验通过!";

                if (calMap.size() > 0) {
                    result += "[存在特殊处理字段] 详情如下:\n";
                    for (String calType : calMap.keySet()) {

                        String calStr = "======" + calType + "======\n";

                        List<String> calList = calMap.get(calType);

                        for (int i = 1; i <= calList.size(); i++) {
                            calStr += i + "." + calList.get(i - 1) + "\n";
                        }
                        result += calStr;
                    }
                }
            } else {
//                resList.sort(Comparator.reverseOrder());
//                result = "校验失败,失败详情如下:\n";
//                result += Toolkit.implode("\n",resList);
                result = "[校验失败] 失败详情如下:\n";
                //失败原因
                for (String errorType : errorMap.keySet()) {

                    String errorStr = "======" + errorType + "======\n";

                    List<String> errorList = errorMap.get(errorType);

                    for (int i = 1; i <= errorList.size(); i++) {
                        errorStr += i + errorList.get(i - 1) + "\n";
                    }
                    result += errorStr;
                }

                //处理
                if (calMap.size() > 0) {
                    result += "[存在特殊处理字段] 详情如下:\n";

                    for (String calType : calMap.keySet()) {

                        String calStr = "======" + calType + "======\n";

                        List<String> calList = calMap.get(calType);

                        for (int i = 1; i <= calList.size(); i++) {
                            calStr += i + "." + calList.get(i - 1) + "\n";
                        }
                        result += calStr;
                    }
                }
            }

        } catch (Exception e) {
            result = "校验失败,对比异常原因:" + e.getMessage();
        }

        return result;
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

