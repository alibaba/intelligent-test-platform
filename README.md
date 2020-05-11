# 概述
Markov(阿里妈妈功能测试平台)是在测试转型大背景下自研的新一代功能测试平台，相较于传统的功能测试框架具有着诸多的优点，比如可视化用例编写管理、分布式的沙盒环境和测试数据构建、测试流程pipeline管理。此外，基于该平台还衍生出了许多智能化测试技术，如基于朴素贝叶斯的用例推荐、参数组合膨胀过滤的用例推荐、基于用例编排算法的智能回归技术、基于用例画像的智能排查系统、精准智能测试等。我们可将Markov视为新一代的功能测试框架，相对于传统经典的测试框架(如pytest)+jenkins的模式，Markov模式对于使用者的门槛更低，能让不懂测试的开发和算法同学简单的进行自助测试，达到了让天下没有难做的"测试"的目标。

<img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-1.png" width="70%" height="70%"></img>

# 主要功能

## `1.pipeline管理`

- pipeline即一份配置文件，与功能测试而言主要的两部分，第一个是测试环境部署相关的，主要是各种参数(比如ip，部署脚本，docker等参数)，第二个是case执行相关的参数（包括了该模块的该如何发送，校验，页面上该展示何种数据源等）。可以抽象理解成，pipeline仅为部署页和用例调试页设定了特定模块所需配置参数。此外，pipeline设计为通用的扩展形式，比如用户可自定义对比测试/压测/集成测试等。

<img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-2.png" width="30%" height="50%" ></img>


## `2.用例管理`

- 可视化的用例管理中，Markov定义了一种面向功能测试的通用页面结构，包含了用例名/描述/业务分组/标签/测试数据/发送query/期望结果等元素，结合pipeline中的测试流程配置，实现了动态渲染用例编辑页的结果，让测试平台能接入更多的测试模块。


## `3.测试环境管理`


- 可视化的测试环境管理，Markov基于分布式容器部署技术，实现了在测试机上部署多容器能力，支持了镜像/rpm/基线等多种部署方式，让测试资源最大化利用，并支持页面化的环境部署/锁定/删除/异常检测等完善的管理能力。(本期只开放前端可视化，具体测试部署暂为开放)


## `4.用例编写和执行`


- 支持可视化的环境选择/测试数据修改后一键执行，透出实时日志和结果。后端执行引擎结合pipeline流程达到动态化load执行插件，以此调度，十分灵活。

## `5.回归测试`


- 支持页面化选取批量用例和测试环境，可选择多种回归模式(本期开放caseBycase的基本模式)，执行完成后可产出回归测试报告。


# 快速开始

开发环境:IDEA

`1.拉取git代码库`

http://gitlab.alibaba-inc.com/engine-test-platform/markov_open

IDE配置端口为8888

<img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-3.png" ></img>


`2、搭建本地mysql环境`

在本地安装mysql后启动，设置用户名和密码

`3、创建数据表`

source markov/database.sql

`4、修改项目的mysql配置`
修改项目文件src/main/resources/application.properties，改为步骤2中设置的mysql用户名及密码


<img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-4.png" ></img>


`5.配置pipeline流程配置`

启动服务后，点击 配置-pipeline，在pipeline编辑框中输入并保存demo中的pipeline内容(pipeline_demo)

`6.Demo试用吧！`



# DEMO演示

## `1.pipeline管理DEMO`

<img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-5.png" width="70%" height="70%"></img>


## `2.用例管理DEMO`

<img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-6.png" ></img>


## `3.测试环境管理DEMO`

- `测试环境管理页`


 <img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-7.png" ></img>


 - `•测试环境部署页`

 <img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-8.png" ></img>


## `4.用例执行DEMO`


<img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-9.png"  width="70%" height="70%"></img>


## `5.回归测试DEMO`

- `回归测试任务编辑`


<img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-10.png"  width="70%" height="70%" ></img>

- `回归任务进度`

<img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-11.png" ></img>

- `测试报告详情页`

<img src="https://github.com/alibaba/intelligent-test-platform/raw/master/src/main/resources/static/img/read-12.png"  width="70%" height="70%" ></img>

# 2期开源计划

我们将按照计划进行开源，当然如果你有更感兴趣的方向，我们可以调整开源的优先级.

Markov Framework 未发布功能：

1.数据源管理。【新数据源上的动态数据渲染】

2.分布式调度

3.智能排查。

4.分支管理。



# 联系我们
Markov由阿里集团-新零售智能引擎事业群-广告产品技术事业部-技术质量-引擎&基础测试及平台团队荣誉出品，markov-opensource@list.alibaba-inc.com和github issue联系和反馈。


# License

