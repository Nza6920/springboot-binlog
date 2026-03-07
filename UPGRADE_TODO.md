# Spring Boot Upgrade TODO

目标：将项目从 `Spring Boot 2.1.7.RELEASE` 升级到当前稳定版 `Spring Boot 4.0.2`。  
建议路线：`2.1.7.RELEASE` → `2.7.x` → `3.5.x` → `4.0.2`

## 0. 基线确认
- [ ] 记录当前可运行状态：`mvn clean test`
- [ ] 记录当前启动方式：`mvn spring-boot:run`
- [ ] 确认本地 JDK 版本、Maven 版本、MySQL 版本
- [ ] 备份当前 `pom.xml`、`application.yml.example`、`spring.factories`
- [ ] 为升级过程创建独立分支

## 1. 运行时准备
- [x] 将 JDK 从 `1.8` 升级到 `21`
- [x] 将 Maven 升级到 `3.6.3+`
- [ ] 确认 CI/CD、IDE、部署环境均支持 Java 21
- [x] 保持业务代码不变，仅验证 Java 21 下可编译

## 2. 升级到 Spring Boot 2.7.x
- [x] 将 `spring-boot-starter-parent` 从 `2.1.7.RELEASE` 升到最新 `2.7.x`
- [x] 移除不必要的显式依赖版本，尽量交给 Spring Boot BOM 管理
- [x] 检查 `mysql-connector-java`、`druid-spring-boot-starter`、`lombok` 兼容性
- [x] 运行：`mvn clean test`
- [ ] 运行：`mvn spring-boot:run`
- [ ] 修复已废弃配置项和启动告警

## 3. 升级到 Spring Boot 3.5.x
- [ ] 将父版本从 `2.7.x` 升级到最新 `3.5.x`
- [ ] 处理 `javax.*` → `jakarta.*` 命名空间迁移
- [ ] 检查自动配置机制变化，评估 `src/main/resources/META-INF/spring.factories`
- [ ] 视需要迁移到 `AutoConfiguration.imports`
- [ ] 升级 Lombok 到支持 Java 21 / Boot 3 的版本
- [ ] 升级 Druid 或替换为兼容的数据源方案
- [ ] 运行编译、测试、启动验证

## 4. 升级到 Spring Boot 4.0.2
- [ ] 将父版本升级到 `4.0.2`
- [ ] 检查 Spring Framework 7 相关不兼容项
- [ ] 复查第三方依赖是否全部支持 Boot 4
- [ ] 修复配置绑定、日志、自动装配、反射相关问题
- [ ] 再次执行完整构建和启动验证

## 5. 仓库内重点检查项
- [ ] 验证 `src/main/java/com/niu/springboot/autoconfig` 下自动配置是否正常
- [ ] 验证 `src/main/resources/application.yml.example` 是否仍完整
- [ ] 验证 `src/main/resources/logback-spring.xml` 是否兼容
- [ ] 验证 binlog 连接、位点读取、字典查询逻辑
- [ ] 验证 `test.sql` 对本地联调仍可用

## 6. 测试与回归
- [ ] 为 `BinlogInfoServiceImpl`、配置绑定、自动配置补充基础测试
- [ ] 新增 `src/test/java` 测试目录（当前仓库暂无测试代码）
- [ ] 覆盖启动成功、配置加载、数据库连接、binlog 关键流程
- [ ] 记录升级前后行为差异

## 7. 文档与交付
- [ ] 更新 `README.md` 中的启动说明与环境要求
- [ ] 更新 `AGENTS.md` 中的版本和命令说明
- [ ] 在 PR 中附上升级路径、验证结果、风险点、回滚方案

## 建议优先处理的依赖
- `org.springframework.boot:spring-boot-starter-parent`
- `com.alibaba:druid-spring-boot-starter`
- `org.projectlombok:lombok`
- `mysql:mysql-connector-java`
- `com.github.shyiko:mysql-binlog-connector-java`

## 风险提示
- 从 `2.1.7` 直接升到 `4.0.2` 风险较高，不建议一步到位。
- 最大变更点通常是 Java 21 要求、`jakarta` 迁移、自动配置注册方式变化、旧三方库兼容性。
