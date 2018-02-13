use holmes;

# source_connection
CREATE TABLE IF NOT EXISTS source_connection
(
  connection_id   varchar(36) CHARACTER SET utf8mb4  NOT NULL DEFAULT '0' PRIMARY KEY
  COMMENT '数据源主键',
  connection_name VARCHAR(100)                       NOT NULL
  COMMENT '连接名称',
  source_type     VARCHAR(20)                        NOT NULL
  COMMENT '连接类型',
  connection_info VARCHAR(200)                       NOT NULL
  COMMENT '连接信息',
  author          VARCHAR(20) DEFAULT NULL           NULL
  COMMENT '操作人',
  created_at      DATETIME DEFAULT CURRENT_TIMESTAMP NULL
  COMMENT '创建时间',
  updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP NULL
  COMMENT '更新时间'
)
  COMMENT '数据源连接信息表';
ALTER TABLE source_connection ADD UNIQUE INDEX IN_CName (connection_name);

# source_table
CREATE TABLE IF NOT EXISTS source_table
(
  table_id              VARCHAR(36) CHARACTER SET utf8mb4  NOT NULL DEFAULT '0' PRIMARY KEY
  COMMENT '源表主键',
  table_name            VARCHAR(100)                       NOT NULL
  COMMENT '源表名',
  connection_id  VARCHAR(36)                        NOT NULL
  COMMENT '数据源连接ID',
  table_schema          TEXT                               NOT NULL
  COMMENT '表结构',
  author                VARCHAR(20) DEFAULT NULL           NULL
  COMMENT '操作人',
  created_at            DATETIME DEFAULT CURRENT_TIMESTAMP NULL
  COMMENT '创建时间',
  updated_at            DATETIME DEFAULT CURRENT_TIMESTAMP NULL
  COMMENT '更新时间'
)
  COMMENT '源表信息表';
ALTER TABLE source_table ADD UNIQUE INDEX IN_TName_CId (table_name, connection_id);

# rule
CREATE TABLE IF NOT EXISTS rule
(
  rule_id          VARCHAR(36) CHARACTER SET utf8mb4 NOT NULL DEFAULT '0'    PRIMARY KEY,
  rule_name        VARCHAR(100)                      NOT NULL
  COMMENT '规则名',
  rule_type        VARCHAR(30)                       NOT NULL
  COMMENT '规则类型',
  source_info      TEXT                              NOT NULL
  COMMENT '数据源表信息',
  rule_expression  TEXT                              NOT NULL
  COMMENT '规则表达式',
  rule_description TEXT                              NOT NULL
  COMMENT '规则描述',
  alarm_type      varchar(20)                        NULL
  COMMENT '警告类型',
  alarm_user      TEXT                               NOT NULL
  COMMENT '被警告用户',
  author          VARCHAR(20) DEFAULT NULL           NULL
  COMMENT '操作人',
  created_at      DATETIME DEFAULT CURRENT_TIMESTAMP NULL
  COMMENT '创建时间',
  updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP NULL
  COMMENT '更新时间'
)
  COMMENT '规则信息表';
ALTER TABLE rule ADD UNIQUE INDEX IN_RName (rule_name);


# table_rule_tmp
CREATE TABLE IF NOT EXISTS table_rule_tmp
(
  tmp_id          INT AUTO_INCREMENT                 PRIMARY KEY,
  table_id        VARCHAR(36)                        NOT NULL
  COMMENT '源表ID',
  rule_id         VARCHAR(36)                        NOT NULL
  COMMENT '规则表ID',
  author          VARCHAR(20) DEFAULT NULL           NULL
  COMMENT '操作人',
  created_at      DATETIME DEFAULT CURRENT_TIMESTAMP NULL
  COMMENT '创建时间',
  updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP NULL
  COMMENT '更新时间'

)
  COMMENT '规则信息表';
ALTER TABLE table_rule_tmp ADD UNIQUE INDEX IN_TId_RId (table_id, rule_id);

# execution
CREATE TABLE IF NOT EXISTS execution
(
  execution_id    INT AUTO_INCREMENT                    PRIMARY KEY,
  execution_name  VARCHAR(100)                          NOT NULL
  COMMENT '任务名',
  rule_id         VARCHAR(36)                           NOT NULL
  COMMENT '规则表ID',
  status          INT                                   NOT NULL
  COMMENT '状态',
  error_info      TEXT                                  NULL
  COMMENT '错误信息',
  created_at      DATETIME DEFAULT CURRENT_TIMESTAMP    NULL
  COMMENT '创建时间',
  updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP    NULL
  COMMENT '更新时间'
)
  COMMENT '任务信息表';


# alarm
CREATE TABLE IF NOT EXISTS alarm
(
  alarm_id        INT AUTO_INCREMENT                    PRIMARY KEY,
  execution_id    INT                                   NOT NULL
  COMMENT '任务ID',
  rule_id         VARCHAR(36)                           NOT NULL
  COMMENT '规则ID',
  alarm           TINYINT                               NOT NULL
  COMMENT '是否报警',
  alarm_info      TEXT                                  NULL
  COMMENT '报警信息',
  created_at      DATETIME DEFAULT CURRENT_TIMESTAMP    NULL
  COMMENT '创建时间',
  updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP    NULL
  COMMENT '更新时间'

)
  COMMENT '报警信息表';
ALTER TABLE alarm ADD CONSTRAINT FK_EId FOREIGN KEY (execution_id) REFERENCES execution(execution_id);

# connection_table
CREATE TABLE IF NOT EXISTS connection_rule_tmp
(
  connection_rule_tmp_id  INT AUTO_INCREMENT                    PRIMARY KEY,
  rule_id                 VARCHAR(36)                           NOT NULL
  COMMENT '规则ID',
  connection_id           VARCHAR(36)                           NOT NULL
  COMMENT '连接ID',
  author                  VARCHAR(20) DEFAULT NULL              NULL
  COMMENT '操作人',
  created_at              DATETIME DEFAULT CURRENT_TIMESTAMP    NULL
  COMMENT '创建时间',
  updated_at              DATETIME DEFAULT CURRENT_TIMESTAMP    NULL
  COMMENT '更新时间'
)
  COMMENT '数据源规则信息表';
ALTER TABLE connection_rule_tmp ADD UNIQUE INDEX IN_CId_RId (rule_id,connection_id);

# connection_table
CREATE TABLE IF NOT EXISTS table_volume_state_management
(
  table_volume_state_management_id    INT AUTO_INCREMENT       PRIMARY KEY,
  rule_id                VARCHAR(36)                           NOT NULL
  COMMENT '规则ID',
  table_names            TEXT                                  NOT NULL
  COMMENT '所有表名',
  version                INT                                   NOT NULL
  COMMENT '版本信息',
  created_at             DATETIME DEFAULT CURRENT_TIMESTAMP    NULL
  COMMENT '创建时间',
  updated_at             DATETIME DEFAULT CURRENT_TIMESTAMP    NULL
  COMMENT '更新时间'

)
  COMMENT '数据源状态信息表';
ALTER TABLE table_volume_state_management ADD UNIQUE INDEX IN_Version_RId (rule_id,version);

# connection_table
CREATE TABLE IF NOT EXISTS kafka_max_offset_management
(
  kafka_max_offset_management_id    INT AUTO_INCREMENT       PRIMARY KEY,
  rule_id                VARCHAR(36)                           NOT NULL
  COMMENT '规则ID',
  max_offset            BIGINT                                  NOT NULL
  COMMENT '所有partitions最大offset之和',
  version                INT                                   NOT NULL
  COMMENT '版本信息',
  created_at             DATETIME DEFAULT CURRENT_TIMESTAMP    NULL
  COMMENT '创建时间',
  updated_at             DATETIME DEFAULT CURRENT_TIMESTAMP    NULL
  COMMENT '更新时间'

)
  COMMENT 'Kafka max offset记录表';
ALTER TABLE kafka_max_offset_management ADD UNIQUE INDEX IN_Version_RId (rule_id,version);

# user_email
CREATE TABLE IF NOT EXISTS user_email
(
  user_email_id           VARCHAR(36)     PRIMARY KEY,
  user_chinese_name       VARCHAR(36)     NOT NULL
  COMMENT '中文名',
  user_english_name       VARCHAR(36)     NOT NULL
  COMMENT '英文名',
  email                   VARCHAR(40)     NOT NULL
  COMMENT 'email信息'
)
  COMMENT '用户邮箱信息表';
ALTER TABLE user_email ADD UNIQUE INDEX IN_CName_EName (user_chinese_name,user_english_name);

alter table source_connection convert to character set utf8;
alter table source_table convert to character set utf8;
alter table rule convert to character set utf8;
alter table execution convert to character set utf8;
alter table alarm convert to character set utf8;
alter table connection_rule_tmp convert to character set utf8;
alter table table_volume_state_management convert to character set utf8;
alter table user_email convert to character set utf8;

















