package com.alibaba.markovdemo.BO;

import com.alibaba.markovdemo.engine.stages.SerialCloneable;
import org.springframework.stereotype.Service;

@Service
public class RunDs extends SerialCloneable {

    //用例依赖总数据量
    private Integer allNum;
    //重复次数
    private Integer repeatNum;
    //冗余度
    private Float redundency;
    //平均耗时
    private Integer costAvg;
    //执行数据条数
    private Integer recordNum;
    private Integer cost;

    public Integer getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(Integer recordNum) {
        this.recordNum = recordNum;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public RunDs(){
        recordNum = 0;
        cost = 0;
        allNum = 0;
        repeatNum = 0;
        costAvg = 0;
        redundency = Float.valueOf(0);
    }

    public Integer getAllNum() {
        return allNum;
    }

    public void setAllNum(Integer allNum) {
        this.allNum = allNum;
    }

    public Integer getRepeatNum() {
        return repeatNum;
    }

    public void setRepeatNum(Integer repeatNum) {
        this.repeatNum = repeatNum;
    }

    public Float getRedundency() {
        return redundency;
    }

    public void setRedundency(Float redundency) {
        this.redundency = redundency;
    }

    public Integer getCostAvg() {
        return costAvg;
    }

    public void setCostAvg(Integer costAvg) {
        this.costAvg = costAvg;
    }
}
