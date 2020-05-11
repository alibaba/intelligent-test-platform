package com.alibaba.markovdemo.engine.stages;


public enum ResultStatus {
    UNKNOWN,
    PENDING,
    RUNNING,
    SUCCESS,
    FAILURE,
    ERROR,
    DEPLOYING;

    @Override
    public String toString() {
        return super.toString();
    }

    public static ResultStatus parse(String s){
        return Enum.valueOf(ResultStatus.class, s);
    }
}
