package com.alibaba.markovdemo.engine.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.codehaus.groovy.util.ListHashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonPathParser {

    private List<String> pathList;
    private String json;
    private Map<String, Object> pathMap;
    private JSONObject pathObj;


    public JsonPathParser(String json) {
        this.json = json;
        this.pathList = new ArrayList<String>();
        this.pathMap = new ListHashMap<String, Object>();
        this.pathObj = new JSONObject();
        setJsonPaths(json);
    }

    public JSONObject getPathJson() {
        return this.pathObj;
    }

    public String getPathJsonStr() {
        return this.pathObj.toString();
    }

    public List<String> getPathList() {
        return this.pathList;
    }

    public Map<String, Object> getPathMap() {
        return this.pathMap;
    }
    private void setJsonPaths(String json) {
        this.pathList = new ArrayList<String>();

        JSONObject object = JSONObject.fromObject(json);
        String jsonPath = "$";
        if(json != null) {
            readObject(object, jsonPath);
        }
    }

    private void readObject(JSONObject object, String jsonPath) {
        Iterator<String> keysItr = object.keys();
        String parentPath = jsonPath;
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            if ("$".equals(parentPath)){
                jsonPath = key;
            }
            else {
                jsonPath = parentPath + "." + key;
            }

            if(value instanceof JSONArray) {
                readArray((JSONArray) value, jsonPath);
            }
            else if(value instanceof JSONObject) {
                readObject((JSONObject) value, jsonPath);
            } else { // is a value
                this.pathList.add(jsonPath);
                this.pathMap.put(jsonPath,value);
                this.pathObj.put(jsonPath,value);

            }
        }
    }

    private void readArray(JSONArray array, String jsonPath) {
        String parentPath = jsonPath;
        for(int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            jsonPath = parentPath + "[" + i + "]";

            if(value instanceof JSONArray) {
                readArray((JSONArray) value, jsonPath);
            } else if(value instanceof JSONObject) {
                readObject((JSONObject) value, jsonPath);
            } else { // is a value
                this.pathList.add(jsonPath);
                this.pathObj.put(jsonPath,value);
                this.pathMap.put(jsonPath,value);
            }
        }
    }

}
