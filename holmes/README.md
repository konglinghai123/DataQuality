## 子模块
- holmes-common     共用工具类
- holmes-dao        数据库管理
- holmes-core       任务管理，规则解析和执行
- holmes-web        业务模块，提供RESTful API

## source type
- mysql
- hive
- kafka

## rule type
- dataVolumn                    表数据量监控
- keyIndicator                  表列数据监控
- dataVolumeWithFixedWindow     数据量监控（Kafka、固定时间间隔内）
- tableVolume                   数据库表数量监控
- keyIndicatorWithDimension     多维度下列数据聚合值监控
- comparisonToTheSameTime       多维度下列数据聚合值同比监控

## alarm type
- wechat
- email
- both
- [报警人](doc/user.md)

## rule example
- [小时报](doc/xiaoshibao.md)
- [监控表](doc/monitoringTables.md)

## online
- [线上环境UI](http://172.31.2.219:8088/holmes/swagger-ui.html#/)
- [配置信息](holmes-web/src/main/resources/application.properties)

## 系统设计
- [架构图](doc/framework.png)
- [数据库表sql](sql/create_table.sql)