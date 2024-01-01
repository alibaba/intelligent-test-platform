package com.alibaba.markovdemo.common;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AjaxResult {
    private boolean isSuccess;
    private String message;
    private Object data;

    public AjaxResult() {
    }

    public AjaxResult(boolean isOk) {
        this.isSuccess = isOk;
    }

    public static AjaxResult succResult() {
        return new AjaxResult(true);
    }

    public static AjaxResult succResultMessage(String message) {
        AjaxResult ajaxResult = new AjaxResult(true);
        ajaxResult.setMessage(message);
        return ajaxResult;
    }

    public static AjaxResult succResult(Object data) {
        AjaxResult ajaxResult = new AjaxResult(true);
        ajaxResult.setData(data);
        ajaxResult.setMessage("success");
        return ajaxResult;
    }

    public static AjaxResult succPageResult(Object pager, List<?> list) {
        AjaxResult ajaxResult = new AjaxResult(true);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("pager", pager);
        data.put("list", list);
        ajaxResult.setData(data);
        return ajaxResult;
    }

    public static AjaxResult errorResult(String message) {
        AjaxResult result = new AjaxResult(false);
        result.setMessage(message);
        return result;
    }

    public static AjaxResult errorResult() {
        AjaxResult ajaxResult = new AjaxResult(false);
        ajaxResult.setMessage("system.error");
        return ajaxResult;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toSimpleJsonString() {
        return "{\"info\":{\"message\":\"" + getMessage() + "\",\"ok\":" + isSuccess + "},\"data\":{}}";
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
