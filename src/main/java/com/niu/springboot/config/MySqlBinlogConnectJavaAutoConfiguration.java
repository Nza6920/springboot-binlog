package com.niu.springboot.config;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.niu.springboot.config.properties.MySqlBinlogConnectJavaProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * mysql-binlog-connector-java 配置类
 *
 * @author [nza]
 * @version 1.0 [2020/12/21 11:29]
 * @createTime [2020/12/21 11:29]
 */
@Configuration
@ConditionalOnProperty("mysql-binlog-connect-java.datasource")
@EnableConfigurationProperties({MySqlBinlogConnectJavaProperties.class})
@AllArgsConstructor
public class MySqlBinlogConnectJavaAutoConfiguration {

    private final MySqlBinlogConnectJavaProperties mySqlBinlogConnectJavaProperties;

    @PostConstruct
    public void init() throws IOException {
        String dbname = mySqlBinlogConnectJavaProperties.getDbname();
        Integer port = mySqlBinlogConnectJavaProperties.getPort();
        String username = mySqlBinlogConnectJavaProperties.getUsername();
        String password = mySqlBinlogConnectJavaProperties.getPassword();

        BinaryLogClient client = new BinaryLogClient(dbname, port, username, password);
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(
                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
        );
        client.setEventDeserializer(eventDeserializer);
        client.registerEventListener(event -> {
            System.out.println(event.toString());
        });

        client.connect();
    }
}
