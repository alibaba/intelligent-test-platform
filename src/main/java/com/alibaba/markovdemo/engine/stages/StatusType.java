package com.alibaba.markovdemo.engine.stages;


public enum StatusType {
    //调度状态
    LOCK, //当一个host对任务进行处理时,必须加锁,处理完后,进行解锁.
    UNLOCK,
    WAIT,
    STOP,
    SKIP,
    //结果状态
    ERROR,
    SUCCESS,
    WARN,
    //任务状态
    CREATE,
    START,
    RUNNING,
    COMPLETE,//当运行区的task的状态为COMPLETE时,就会转移到完成区
    TO_BE_DESTROY,
    //任务所在区域
    WAIT_AREA,
    RUN_AREA,
    COMPLETE_AREA,
    //任务类型
    SINGLE_RUN,
    TROUBLE_SHOOT,
    FUN_DIFF,
    REGRESSION,
    PERF_DIFF,
    PIPELINE,
    //异步任务zk池文件名
    pipelineTasks,
    regressionTasks,
    perfDiffTasks,
    funcDiffTasks,
    singleRunTasks,
    troubleShootTasks,
    //智能检测归因分类
    TEST_ENV_CHECKITEM,//测试环境检测
    COREDUMP_CHECKITEM,//coredump检测
    CASERUN_CHECKITEM,//执行过程检测
    LOG_CHECKITEM,//错误日志检测
    DIFF_CHECKITEM,//基准环境对比检测
    OUTPUT_CHECKITEM //校验结果对比检测

}
