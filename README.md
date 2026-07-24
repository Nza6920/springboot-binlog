# springboot-binlog

[中文](#中文) | [English](#english)

## 中文

基于 [mysql-binlog-connector-java](https://github.com/osheroff/mysql-binlog-connector-java.git) 实现数据库增量数据的收集。

参考文章：<https://www.nnzzaa.cn/topics/GbQX8N3Ba>

### 当前环境

- Spring Boot `3.5.10`
- Java `21`
- Maven Wrapper：优先使用 `./mvnw`

### 项目结构

- `src/main/java/com/niu/springboot/autoconfig`：starter 风格的自动配置、配置属性和扩展接口。
- `src/main/java/com/niu/springboot/binlog`：应用启动类、DTO、枚举和业务服务实现。
- `src/main/resources/application.yml.example`：本地配置示例，请复制为未提交的 `application.yml` 后再按环境修改。
- `src/main/resources/test.sql`：示例表结构和测试数据脚本。

### 快速开始

1. 复制示例配置：
   - 将 `src/main/resources/application.yml.example` 复制为本地 `application.yml`。
2. 按实际环境填写数据库和 binlog 配置：
   - `spring.datasource.*`
   - `mysql-binlog-connect-java.datasource.*`
3. 构建项目：
   - `./mvnw clean package`
4. 本地启动：
   - `./mvnw spring-boot:run`

### 配置说明

`mysql-binlog-connect-java.datasource` 需要至少配置以下字段：

- `hostname`
- `port`
- `schema`
- `username`
- `password`

业务侧需要自行提供以下 Bean，自动配置才会生效：

- `com.niu.springboot.autoconfig.service.DataCollectionService`
- `com.niu.springboot.autoconfig.service.BinlogInfoService`

如果容器中存在 `com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer` Bean，自动配置会优先使用它来自定义事件反序列化策略。

项目会在启动后创建 `BinaryLogClient`，并从已保存的 binlog 文件名和位点继续消费。

### 开发与测试

- `./mvnw test`：运行测试套件。
- `./mvnw clean package`：编译并打包可运行 JAR。
- `./mvnw dependency:tree`：检查依赖树和潜在冲突。

新增测试时，请在 `src/test/java` 下按主代码包路径镜像放置，并将单元测试命名为 `*Test`、集成测试命名为 `*IT`。

### 说明

- 当前仓库已升级到 Spring Boot 3，自动配置入口已从 `spring.factories` 迁移为 `AutoConfiguration.imports`。
- 不要提交真实数据库凭据或 binlog 地址；新增必填配置时，请同步更新 `src/main/resources/application.yml.example`。
- README 中保留了原始参考链接，具体实现请以当前仓库代码和配置示例为准。

## English

A Spring Boot project for collecting incremental database changes with [mysql-binlog-connector-java](https://github.com/osheroff/mysql-binlog-connector-java.git).

Reference article: <https://www.nnzzaa.cn/topics/GbQX8N3Ba>

### Runtime Environment

- Spring Boot `3.5.10`
- Java `21`
- Maven Wrapper: prefer `./mvnw`

### Project Structure

- `src/main/java/com/niu/springboot/autoconfig`: starter-style auto-configuration, configuration properties, and extension interfaces.
- `src/main/java/com/niu/springboot/binlog`: application bootstrap, DTOs, enums, and business service implementations.
- `src/main/resources/application.yml.example`: local configuration example; copy it to an uncommitted `application.yml` before editing environment-specific values.
- `src/main/resources/test.sql`: sample schema and test data script.

### Quick Start

1. Copy the example configuration:
   - Copy `src/main/resources/application.yml.example` to a local `application.yml`.
2. Fill in the database and binlog settings for your environment:
   - `spring.datasource.*`
   - `mysql-binlog-connect-java.datasource.*`
3. Build the project:
   - `./mvnw clean package`
4. Run locally:
   - `./mvnw spring-boot:run`

### Configuration

`mysql-binlog-connect-java.datasource` must include at least the following fields:

- `hostname`
- `port`
- `schema`
- `username`
- `password`

The application side must provide the following Beans before auto-configuration is activated:

- `com.niu.springboot.autoconfig.service.DataCollectionService`
- `com.niu.springboot.autoconfig.service.BinlogInfoService`

If a `com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer` Bean exists in the container, the auto-configuration uses it first to customize event deserialization.

After startup, the project creates a `BinaryLogClient` and resumes consumption from the previously saved binlog filename and position.

### Development and Testing

- `./mvnw test`: run the test suite.
- `./mvnw clean package`: compile and package the runnable JAR.
- `./mvnw dependency:tree`: inspect the dependency tree and potential conflicts.

When adding tests, place them under `src/test/java` with a package path mirroring the main code, and name unit tests `*Test` and integration tests `*IT`.

### Notes

- This repository has been upgraded to Spring Boot 3, and the auto-configuration entry has moved from `spring.factories` to `AutoConfiguration.imports`.
- Do not commit real database credentials or binlog endpoints. When adding required properties, update `src/main/resources/application.yml.example` as well.
- The original reference link is kept in this README. Please treat the current repository code and configuration example as the source of truth for implementation details.
