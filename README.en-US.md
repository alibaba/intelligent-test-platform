# Overview
Markov(MOM Ali functional testing platform)is a new generation of functional testing platform self-developed in the context of test transformation. Compared with traditional functional testing frameworks, it has many advantages, such as visual use case writing management and distributed sandbox environment. and test data construction, test process pipeline management. Besides, many intelligent testing technologies have been derived based on the platform, such as use case recommendation based on Naive Bayes, use case recommendation based on parameter combination expansion filtering, intelligent regression technology base on use case orchestration algorithm, intelligent investigation system based on use case portrait. Accurate and intelligent testing, etc. We can think of Markov as a new generation of the functional testing framework. Compared with the traditional classic testing framework - a.g.: pytest + jenkins - the Markov model has a lower threshold for users, which makes it simple for students who do not understand test development and algorithms.
The self-service test has achieved the goal of making the world no difficult " to test".

<img src="https://mmbiz.qpic.cn/mmbiz_png/DWQ5ap0dyHMvWOVCYt8M5463BURIjtVD4x8ibtJxYRa0euRLC3ic47oHkRXltOg4SbEtECFwQVoOjlLqIWicpsPwg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1" width="70%" height="70%"></img>


# The main function

## `1.pipeline (management)`

- The pipeline is a configuration file, which is mainly related to functional testing. The first is related to the deployment of the test environment, mainly various parameters ( such as IP, deployment scripts, docker and other parameters) and the second is case execution-related parameters (including how to send and verify the module, what data source should be displayed on the page, etc.). It can be abstractly understood that the pipeline only sets the configuration parameters required by a specific module for the deployment page and the debugging page use case. Also, the pipeline is designed as a universal extension form, such as user-defined _contrast test/pressure test/integration test_

<img src="https://wx2.sinaimg.cn/mw690/ac0a3d36ly1gephe1brfij20n80pnnjg.jpg" width="30%" height="50%" ></img>


## 2. Use case management

- 可视化的用例管理中，Markov定义了一种面向功能测试的通用页面结构，包含了用例名/描述/业务分组/标签/测试数据/发送query/期望结果等元素，结合pipeline中的测试流程配置，实现了动态渲染用例编辑页的结果，让测试平台能接入更多的测试模块。


## 3.Test environment management


- 可视化的测试环境管理，Markov基于分布式容器部署技术，实现了在测试机上部署多容器能力，支持了镜像/rpm/基线等多种部署方式，让测试资源最大化利用，并支持页面化的环境部署/锁定/删除/异常检测等完善的管理能力。(本期只开放前端可视化，具体测试部署暂为开放)


## 4.Writing and executing use case


- 支持可视化的环境选择/测试数据修改后一键执行，透出实时日志和结果。后端执行引擎结合pipeline流程达到动态化load执行插件，以此调度，十分灵活。

## 5. Regression testing


- 支持页面化选取批量用例和测试环境，可选择多种回归模式(本期开放caseBycase的基本模式)，执行完成后可产出回归测试报告。


# Quickstart

Development environment: IntelliJ Idea

1. Get the repository

https://github.com/alibaba/intelligent-test-platform

1. IDE configuration
port: 8888
Java version java8

<img src="https://wx4.sinaimg.cn/mw690/ac0a3d36ly1gephe1g06wj21ux0u0kcv.jpg" ></img>


`2、搭建本地mysql环境`

在本地安装mysql后启动（mysql版本要求8.0及以上），设置用户名和密码

`3、创建数据表`

source markov/database.sql

`4、修改项目的mysql配置`

修改项目文件src/main/resources/application.properties，改为步骤2中设置的mysql用户名及密码


<img src="https://wx1.sinaimg.cn/mw690/ac0a3d36ly1gephe1uxltj221s0l21kx.jpg" ></img>


`5.配置pipeline流程配置`

启动服务后，点击 配置-pipeline，在pipeline编辑框中输入并保存demo中的pipeline内容(pipeline_demo)

`6.Demo试用吧！`



# DEMO演示

## `1.pipeline管理DEMO`

<img src="https://wx3.sinaimg.cn/mw690/ac0a3d36ly1gephe1ij0rj21n70u0qhc.jpg" width="70%" height="70%"></img>


## `2.用例管理DEMO`

<img src="https://wx3.sinaimg.cn/mw690/ac0a3d36ly1gephe1jka5j22260qmdt7.jpg" ></img>


## `3.测试环境管理DEMO`

- `测试环境管理页`


 <img src="https://wx3.sinaimg.cn/mw690/ac0a3d36ly1gephe1jxz0j224y0qi7lt.jpg" ></img>


 - `•测试环境部署页`

 <img src="https://wx3.sinaimg.cn/mw690/ac0a3d36ly1gephe1nci7j22080o8wst.jpg" ></img>


## `4.用例执行DEMO`


<img src="https://wx4.sinaimg.cn/mw690/ac0a3d36ly1gephe1qg2zj213j0u0qik.jpg"  width="70%" height="70%"></img>


## `5.回归测试DEMO`

- `回归测试任务编辑`


<img src="https://wx3.sinaimg.cn/mw690/ac0a3d36gy1gephef7fdrj22390u0dx4.jpg"  width="70%" height="70%" ></img>

- `回归任务进度`

<img src="https://wx4.sinaimg.cn/mw690/ac0a3d36gy1gephef5d6cj226g0pctmu.jpg" ></img>

- `测试报告详情页`

<img src="https://wx4.sinaimg.cn/mw690/ac0a3d36gy1gephefjwo2j21op0u0h5o.jpg"  width="70%" height="70%" ></img>

## `6、体验智能化feature`

点击查看[Intelligent.md](https://github.com/alibaba/intelligent-test-platform/blob/master/Intelligent.md)

# 2期开源计划

我们将按照计划进行开源，当然如果你有更感兴趣的方向，我们可以调整开源的优先级.

Markov Framework 未发布功能：

1.测试数据源管理。

2.分布式调度

3.智能排查。

4.用例分支管理。

5.基于容器化的测试环境部署

6.UT

7.功能AB-Test

8.用例画像系统

9.markov认证系统

10.定时调度系统

11.多bu权限管理系统

等..


# 联系我们
Markov由阿里集团-新零售智能引擎事业群-广告产品技术事业部-技术质量-引擎&基础测试及平台团队荣誉出品，markov-opensource@list.alibaba-inc.com和github issue联系和反馈。

钉钉沟通群：

<img src="https://mmbiz.qpic.cn/mmbiz_png/DWQ5ap0dyHMvWOVCYt8M5463BURIjtVDzMRia7ibevHW5q2icvb835Uq2kcqvBrAJFN7dQicBpLibA64kicfice62PxPg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1" width="40%" ></img>

# License

