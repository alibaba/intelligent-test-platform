
# 概述
Markov本期开源了部分智能化技术（智能用例生成、智能回归、失败智能归因、用例推荐，用例膨胀），在功能测试全流程中，把测试方法论和智能化技术结合更进一步的提升测试效率。

<img src="https://mmbiz.qpic.cn/mmbiz_png/DWQ5ap0dyHMvWOVCYt8M5463BURIjtVDyg27odILUO2lI0XEu9iaOZQgoZWBBahROibwib4SkHjKykbfFib10gIFmQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1"></img>

# 智能化Feature

## `1.用例智能推荐`

- 基于朴素bayes算法和特征抽取算法，Markov提供了用例智能推荐功能；用户仅需输入少量的用例描述信息（分词算法如FMM，结巴分词等进行特征抽取)，或者直接选取特征池中已有的用例特征，系统就能自动抽取业务特征集并从千级别的用例库中匹配出相似度TopN的用例，然后结合模板生成用例推荐给用户。该技术创新性的解决了在大量用例中难以快速查询目标用例的痛点，极大的降低了功能测试过程中写用例的门槛。

<img src="https://mmbiz.qpic.cn/mmbiz_png/DWQ5ap0dyHMvWOVCYt8M5463BURIjtVD6rTTGmoHhENj0G3GLLLpNIR7arQS7u1EJJrAViamcu5pqzRMHdQ594Q/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1"></img>

- demo展示

<img width="800" src="https://wx3.sinaimg.cn/mw690/ac0a3d36gy1gephef20pmj210y0g543v.jpg"></img>

<img width="800" src="https://wx1.sinaimg.cn/mw690/ac0a3d36gy1gephef4b74j214i0m2ah7.jpg"></img>

## `2.用例膨胀`

- Markov提供了用例智能膨胀、管理、Filter功能；通过智能抽取种子用例特征，用户能对各个特征进行组合设置(用户可自定义特征值组合，或选取系统根据历史数据训练好的特征值组合)，系统将所有特征进行叉乘组合后产生批量用例集，系统过滤不合法用例后将批量用例集推荐给用户自行选取，以上就完成了一次大批量生成用例的过程。用例膨胀通过结合特征抽取，特征组合，特征值叉乘，用例模板合并等方式，创新性解决了用户一次性生成批量用例以达到覆盖测试多场景的高效结果。

<img src="https://mmbiz.qpic.cn/mmbiz_png/DWQ5ap0dyHMvWOVCYt8M5463BURIjtVDOkJgDWYUib9WdCAUwOesjgb7YrUpVPcPucU9P2qicPkP7lj6du8trvtA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1"></img>

- demo展示

<img width="800" src="https://wx1.sinaimg.cn/mw690/ac0a3d36gy1gephefa29aj210k0eg43v.jpg"></img>

<img width="800" src="https://wx4.sinaimg.cn/mw690/ac0a3d36gy1gepheflv97j21500gytfb.jpg"></img>

<img width="800" src="https://wx1.sinaimg.cn/mw690/ac0a3d36gy1gephf1e270j214y0kwk0t.jpg"></img>

## `3.基于遗传算法的智能用例生成`


- 基于遗传算法Markov提供了全自动化（自动生成、自动运行、自动筛选）的智能用例生成功能，利用它我们可以全自动化的生成覆盖变动代码的最全面高效的测试用例集
- 技术简介
  用例生成之前系统自动构建用例基因库，用户挑选用例做为表现型种子（phenotype seed），通过基因型编码（genotype encode）生成一系列基因的合集--染色体（chromosome）。通过定义遗传算法的几大核心算子：适应度计算、概率性选择算子、交叉算子、变异算子，进入核心用例培育流程。其中适应度计算是根据用例在增量代码上的覆盖表现来定义的，代表这个用例对测试代码的覆盖优异程度。遗传算法有一个核心假设：“适应度好的个体繁衍出来的后代适应度好的可能性更大”，这就保证了对测试代码有好的覆盖效果的参数组合进入后续的参数组织的可能性更大，而覆盖表现不好的组合将会快被种群遗弃，最终达到培育终止条件后，将基因型染色体解码成为有效用例集（注：遗传算法的理解可查阅算法类专业资料）
 - 技术效果
遗传算法智能生成与传统枚举的生成效率对比
传统枚举法需要对不同参数取值进行叉乘，逐一判断其有效性，导致运行大量用例效率较低。
遗传算法通过“择优选择，适者生存”的思想，只有代码覆盖效果好的用例才进入种群，对最优解极限逼近从而大幅提高效率。
实验数据中，逐一枚举法需要枚举40320种可能用例，而遗传算法平均只需要处理378个用例，最终都得到94%的极限增量覆盖率。
效率对比结论：实验数据同等效果的前提下，智能生成的效率对比传统枚举法的效率要超过100倍

<img src="https://mmbiz.qpic.cn/mmbiz_png/DWQ5ap0dyHMvWOVCYt8M5463BURIjtVDLs5n8AX8O2iaTndeRe0NIBUKjqgLvQPzeReCfdk6XCzVYOL2GKbJM5g/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1"></img>

- demo展示

feature体验demo操作如下，项目中已插入实验数据和相关mock，可直接按如下流程操作。
<img width="800" src="https://wx1.sinaimg.cn/mw690/ac0a3d36gy1gephf13adyj21lu0pc477.jpg"></img>
<img width="800" src="https://wx4.sinaimg.cn/mw690/ac0a3d36gy1gephf16x67j21iu0u0anf.jpg"></img>
<img width="800" src="https://wx2.sinaimg.cn/mw690/ac0a3d36gy1gephf18t7xj21mo0ei7aq.jpg"></img>
<img width="800" src="https://wx2.sinaimg.cn/mw690/ac0a3d36gy1gephf1jevxj21kj0u0dyl.jpg"></img>



## `4.基于动态编排算法的智能回归技术`

- 功能回归本质上其实是个调度问题，Markov运用动态用例编排算法技术实现极致的回归效率。将回归用例集进行初始排序，按照用户预定义的可全量数据类型及数据冲突检测方法将用例集预处理。整体大致会编排为全量数据准备桶/并行执行桶/高效串行桶/失败重试桶这四大阶段。

<img src="https://mmbiz.qpic.cn/mmbiz_png/DWQ5ap0dyHMvWOVCYt8M5463BURIjtVDB1uiakTnqGbkZQpcjnuGVCv9CVkZU16CoQnWEGlZdroU5gfTBQWL2Xw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1"></img>

- 回归提速的2个原则：

原则1:测试数据聚合冗余度r越小，整体回归效率越高。
数据聚合冗余度r指的是在用例集中的测试数据聚合后的重复率，举个例子：
100个用例都依赖了相同的测试数据data1，在caseByCase执行过程中要消耗100个t(data1)的时间单位，此时冗余度r为1。而如果通过某种调度方式对data1进行聚合处理，比如新调度过程在运行之前只需要抽取所有出data1统一准备，即新调度方式消耗1个t(data1)的时间单位即可，此时冗余度r为0.01，从而省下了99个t(data1)时间单位，因此整体回归效率变高。


原则2:当用例集不依赖于任何测试数据时，则用例集可并行执行。
举个例子, 典型不依赖测试数据的场景是线上冒烟场景，在线上域系统是基于生产数据的，即不再依赖任何测试数据，自然也不存在数据冲突等问题，因此线上冒烟场景能轻而易举做高并发执行冒烟检查返回。因此，在功能测试中，当用例集不依赖于测试数据时，则可直接做高并发执行。


在Markov实践中，动态编排算法相较于caseBycase的执行方式能有效提升回归效率约2-10倍，但在观察结果时，我们仍能发现用例编排后仍无可避免还有数据聚合冗余度，即终极优化的天花板就是数据聚合冗余度为0，后续仍有持续优化的空间。



<img width="800" src="https://mmbiz.qpic.cn/mmbiz_png/DWQ5ap0dyHMvWOVCYt8M5463BURIjtVDJ4Z176uQQ8d8lf6AKBniaqPiaH86x5uofQAiatniaQpMhkMm2Rq8vzcaiaA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1"></img>

- demo展示

<img width="800" src="https://wx4.sinaimg.cn/mw690/ac0a3d36gy1gephf1ewqfj211c0lw0xb.jpg"></img>


<img width="800" src="https://wx2.sinaimg.cn/mw690/ac0a3d36gy1gephfcjjnqj211m0ivq9p.jpg"></img>

<img width="800" src="https://wx3.sinaimg.cn/mw690/ac0a3d36gy1gephfckflyj21380hz44b.jpg"></img>

## `5.代码级缺陷智能定位技术`


- markov采用了一种概率分析方案，对每个失败用例建立特征向量，包括用例特征、覆盖代码特征等，并用一些加权策略计算相似度，找出测试目的上相似度最高的用例。相似用例中覆盖最频繁的改动代码行，其缺陷嫌疑度也越高。这样计算出测试未通过代码的缺陷嫌疑度，并将嫌疑度高的代码推送给用户作为缺陷定位的参考。

<img src="https://mmbiz.qpic.cn/mmbiz_png/DWQ5ap0dyHMvWOVCYt8M5463BURIjtVDRqm5P5kgR2BPBV6Zm1vnIeLQbHlibwt1OqdRR4OD7GDSsQBQpajTSicg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1"></img>
