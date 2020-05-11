create database markov_demo

use markov_demo;

CREATE TABLE `got_testcase` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scenario_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'scenario id',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `name` longtext COMMENT 'name',
  `description` longtext COMMENT 'description',
  `long_description` longtext COMMENT '详细描述',
  `content` longtext COMMENT '存储case的阶段数据，比如数据准备阶段，数据执行阶段',
  `case_group` varchar(100) DEFAULT NULL COMMENT 'case分组',
  `is_deleted` int(11) DEFAULT '0' COMMENT '用例是否被删除。0-没有删除；1-已删除，此类case不会展示到页面上',
  `case_template` text COMMENT '用例模板 java/c++',
  `features` text COMMENT '业务特征',
  `is_visible` int DEFAULT '0' COMMENT '是否可见用例',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='testcase';

CREATE TABLE `got_pipeline` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键（id）',
  `pipeline` longtext COMMENT 'pipeline的json配置',
  `extend` text COMMENT '扩展字段',
  `tag` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '流程定义/自定义\n',
  `scenario_id` bigint(20) unsigned DEFAULT NULL COMMENT '场景id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='存储流程执行的pipeline配置文件';
CREATE TABLE `got_scenario` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT 'name',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='测试场景表';
CREATE TABLE `got_envs` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `host_ip` varchar(20)  DEFAULT NULL COMMENT 'host_ip',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  `name` varchar(200) DEFAULT NULL COMMENT '环境名称',
  `env_detail` text COMMENT '环境详情',
  `scenario_id` bigint(20) unsigned DEFAULT NULL COMMENT '场景id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6928 DEFAULT CHARSET=utf8 COMMENT='环境列表'
;
CREATE TABLE `pipeline_ui` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scenario_id` bigint(20) unsigned DEFAULT NULL COMMENT '场景id',
  `content` text COMMENT 'pipeline_ui的jsonString\n',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='pipeline_ui表';
CREATE TABLE `got_datasource` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键/场景id',
  `content` longtext COMMENT '数据源内容',
  `scenario_id` bigint(20) unsigned DEFAULT NULL COMMENT '场景id',
  PRIMARY KEY (`id`),
  KEY `idx_scenario_id` (`scenario_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='测试数据源表';
CREATE TABLE `got_menu` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` text COMMENT 'menu的jsonString\n',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='menu表';
#插入测试数据
insert into got_menu values(0, "{    \"buinfo\": {        \"buid\": 1,        \"appSecneMap\": {            \"1\": \"1\"        },        \"menu\": [{            \"businessId\": 1,            \"businessName\": \"markov-demo\",            \"appMenuList\": [{                \"appName\": \"测试模块\",                \"appId\": 1,                \"scenarioMenuList\": [{                    \"isMember\": true,                    \"scenarioId\": 1,                    \"scenarioName\": \"场景1\"                },{                    \"isMember\": true,                    \"scenarioId\": 2,                    \"scenarioName\": \"场景2\"                }]            }]        }, ],        \"buName\": \"markov-demo\"    }}");


CREATE TABLE `got_reports` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `user` text COMMENT '执行用户',
  `report_name` text COMMENT '报告名称',
  `status` varchar(100) DEFAULT NULL COMMENT '执行状态',
  `message` text COMMENT '信息',
  `app_id` bigint(20) unsigned DEFAULT NULL COMMENT 'appid',
  `scenario_id` bigint(20) unsigned DEFAULT NULL COMMENT '场景id',
  `run_type` text COMMENT '执行方式',
  `exec_id` text COMMENT '批次id',
  `analysis` text COMMENT '分析报告',
  `task_id` text COMMENT 'zk任务id',
  `zk_info` longtext COMMENT 'zk信息',
  `accuracy_report_id` bigint(20) unsigned DEFAULT NULL COMMENT '精准测试报告id',
  `case_num` int(10) unsigned DEFAULT '0' COMMENT '回归用例数',
  `image_name` text COMMENT '回归的镜像版本',
  `branch_name` text COMMENT '执行用例的分支',
  `git_branch` text COMMENT '测试源码的分支',
  `git_commit` text COMMENT '测试源码的commit版本',
  `cc_cov_rate` text COMMENT '增量代码覆盖率',
  `is_visible` int DEFAULT '0' COMMENT '是否可见报告',
  PRIMARY KEY (`id`),
  KEY `idx_scenarioid` (`scenario_id`),
  KEY `idx_appid` (`app_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7858 DEFAULT CHARSET=utf8mb4 COMMENT='回归测试报告表';


CREATE TABLE `got_testcase_snaps` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `scenario_id` bigint(20) unsigned DEFAULT NULL COMMENT '场景id',
  `app_id` bigint(20) unsigned DEFAULT NULL COMMENT 'appid',
  `name` varchar(100) DEFAULT NULL COMMENT '用例名',
  `description` text COMMENT '描述',
  `long_description` longtext COMMENT '详情',
  `content` longtext COMMENT '输入，输出，期望，数据准备',
  `status` varchar(100) DEFAULT NULL COMMENT '用例执行状态',
  `testreport_id` bigint(20) unsigned NOT NULL COMMENT '归属的报告id',
  `testcase_id` bigint(20) unsigned NOT NULL COMMENT '归属的用例id',
  `case_group` varchar(100) DEFAULT NULL COMMENT '测试用例分组',
  `tag` varchar(100) DEFAULT NULL COMMENT 'case标签，可有有多个值',
  `version` varchar(100) DEFAULT NULL COMMENT 'case版本号',
  `run_time` bigint(20) unsigned DEFAULT NULL COMMENT '执行时间',
  `run_time_str` text COMMENT '执行时间标准化',
  `retry_num` bigint(20) unsigned DEFAULT NULL COMMENT '重试次数',
  `constancy` text COMMENT '稳定性',
  `env_name` text COMMENT '环境名',
  `conflict_desc` text COMMENT '冲突用例描述',
  `is_parallel` tinyint(1) DEFAULT NULL COMMENT '是否串行',
  `trouble_shoot_box` longtext COMMENT '智能归因',
  PRIMARY KEY (`id`),
  KEY `idx_caseid` (`testcase_id`),
  KEY `idx_reportid` (`testreport_id`)
) ENGINE=InnoDB AUTO_INCREMENT=303953 DEFAULT CHARSET=utf8mb4 COMMENT='测试报告用例集快照'
;
CREATE TABLE `got_features_pool` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create` datetime NOT NULL COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL COMMENT '修改时间',
    `scenario_id` bigint unsigned NULL COMMENT 'scenario_id',
    `app_id` bigint unsigned NULL COMMENT 'app_id',
    `features` text NULL COMMENT '特征集',
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET=utf8mb4 COMMENT='特征池';


CREATE TABLE `got_case_generate_task` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `creator` text COMMENT '任务创建者',
  `seed_case_list` text COMMENT '种子用例id列表，以”,“分隔',
  `scenario_id` bigint unsigned DEFAULT NULL COMMENT '场景id',
  `env_info` text COMMENT '测试环境信息',
  `feature_conf` longtext COMMENT '任务相关配置，jsonObject',
  `task_name` text COMMENT '任务名',
  `task_snap` longtext COMMENT '任务生成信息',
  `task_result` longtext COMMENT '最终生成用例',
  `task_status` text COMMENT 'crate 、executing、success or fail ',
  `gene_bank_snap` longtext COMMENT 'json格式gene bank',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用例智能生成任务记录表';

CREATE TABLE `got_case_accuracy` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `case_id` bigint unsigned DEFAULT NULL COMMENT 'case id',
  `exe_id` bigint unsigned DEFAULT NULL COMMENT '回归执行id',
  `cov_line` longtext COMMENT 'case覆盖的代码行，json格式',
  `collect_type` text COMMENT 'single : 单case收集；total：任务整体收集',
  PRIMARY KEY (`id`),
  KEY `idx_caseid` (`case_id`),
  KEY `idx_exe_id` (`exe_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='case精准数据覆盖数据记录表';


INSERT INTO `got_testcase` (`gmt_create`,`gmt_modified`,`scenario_id`,`name`,`description`,`long_description`,`content`,`case_group`,`is_deleted`,`case_template`,`is_visible`) VALUES ('2020-04-28 19:12:55','2020-04-28 19:12:55',1,'case名','用例智能生成种子用例','种子用例','{"prepareData":[{"Tair":[{"dsName":"table.markovtair.test","data":[{"key":"testkey","value":"testvalue","property":""}]}]}],"caseRunStage":[{"group_name":"ERPC校验（第一组）","data":[{"input":"{\n  \"ad_id\": \"222\",\n  \"search_key\": \"key1\",\n  \"match_level\": 2,\n  \"user_type\": \"type1\",\n  \"top_num\": 10,\n  \"use_feature\": false,\n  \"other1\": \"1\",\n  \"other2\": \"0\"\n}","expect":"{\n  \"result\": \"1\" \n}","actual":"null"}]}]}','test',0,'c++',0);

