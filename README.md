# springboot-binlog
基于 [mysql-binlog-connector-java](https://github.com/osheroff/mysql-binlog-connector-java.git) 实现数据库增量数据的收集  
详见: https://www.nnzzaa.cn/topics/GbQX8N3Ba

## 当前环境

- Spring Boot `3.5.10`
- Java `21`
- Maven Wrapper：优先使用 `./mvnw`

## 快速开始

1. 复制示例配置：
   - 将 `src/main/resources/application.yml.example` 复制为本地 `application.yml`
2. 按实际环境填写数据库和 binlog 配置：
   - `spring.datasource.*`
   - `mysql-binlog-connect-java.datasource.*`
3. 构建项目：
   - `./mvnw clean package`
4. 本地启动：
   - `./mvnw spring-boot:run`

## 配置说明

`mysql-binlog-connect-java.datasource` 需要至少配置以下字段：

- `hostname`
- `port`
- `schema`
- `username`
- `password`

业务侧需要自行提供以下 Bean，自动配置才会生效：

- `com.niu.springboot.autoconfig.service.DataCollectionService`
- `com.niu.springboot.autoconfig.service.BinlogInfoService`

项目会在启动后创建 `BinaryLogClient`，并从已保存的 binlog 文件名和位点继续消费。

## 说明

- 当前仓库已升级到 Spring Boot 3，自动配置入口已从 `spring.factories` 迁移为 `AutoConfiguration.imports`
- README 中保留了原始参考链接，具体实现请以当前仓库代码和配置示例为准
