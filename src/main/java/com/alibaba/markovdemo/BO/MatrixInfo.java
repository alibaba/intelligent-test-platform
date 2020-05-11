package com.alibaba.markovdemo.BO;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class MatrixInfo {

    public HashMap<String ,DataMatrixUnit> matrix;

    public QueueNode queueUnit;
    public String leftTreeReStartFlag;

    public String rightTreeReStartFlag;

    public List<TestCaseAI> leftCaseList;

    public List<TestCaseAI> rightCaseList;

    public HashMap<String, DataMatrixUnit> getMatrix() {
        return matrix;
    }

    public void setMatrix(HashMap<String, DataMatrixUnit> matrix) {
        this.matrix = matrix;
    }

    public QueueNode getQueueUnit() {
        return queueUnit;
    }

    public void setQueueUnit(QueueNode queueUnit) {
        this.queueUnit = queueUnit;
    }

    public List<TestCaseAI> getLeftCaseList() {
        return leftCaseList;
    }

    public void setLeftCaseList(List<TestCaseAI> leftCaseList) {
        this.leftCaseList = leftCaseList;
    }

    public List<TestCaseAI> getRightCaseList() {
        return rightCaseList;
    }

    public void setRightCaseList(List<TestCaseAI> rightCaseList) {
        this.rightCaseList = rightCaseList;
    }


    public MatrixInfo(){
        matrix = new HashMap<>();
        queueUnit = new QueueNode();
        leftCaseList = new ArrayList<>();
        rightCaseList = new ArrayList<>();
    }

    public String getLeftTreeReStartFlag() {
        return leftTreeReStartFlag;
    }

    public void setLeftTreeReStartFlag(String leftTreeReStartFlag) {
        this.leftTreeReStartFlag = leftTreeReStartFlag;
    }

    public String getRightTreeReStartFlag() {
        return rightTreeReStartFlag;
    }

    public void setRightTreeReStartFlag(String rightTreeReStartFlag) {
        this.rightTreeReStartFlag = rightTreeReStartFlag;
    }
}
