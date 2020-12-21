package com.niu.springboot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义 Mysql 配置类
 *
 * @author [nza]
 * @version 1.0 [2020/12/21 11:27]
 * @createTime [2020/12/21 11:27]
 */
@ConfigurationProperties(prefix = "mysql-binlog-connect-java.datasource")
@Data
public class MySqlBinlogConnectJavaProperties {

    /**
     * 数据库名称
     */
    private String dbname;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
