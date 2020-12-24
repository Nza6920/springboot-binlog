package com.niu.springboot.autoconfig;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.niu.springboot.autoconfig.service.BinlogInfoService;
import com.niu.springboot.autoconfig.properties.MySqlBinlogConnectJavaProperties;
import com.niu.springboot.autoconfig.service.DataCollectionService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
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
@EnableConfigurationProperties({MySqlBinlogConnectJavaProperties.class})
@ConditionalOnBean({DataCollectionService.class, BinlogInfoService.class})
@ConditionalOnProperty("mysql-binlog-connect-java.datasource.hostname")
@AllArgsConstructor
public class MySqlBinlogConnectJavaAutoConfiguration {

    private final MySqlBinlogConnectJavaProperties mySqlBinlogConnectJavaProperties;

    private final DataCollectionService dataCollectionService;

    private final ApplicationContext context;

    private final BinlogInfoService binlogInfoService;

    @PostConstruct
    public void init() throws IOException {

        String hostname = mySqlBinlogConnectJavaProperties.getHostname();
        String schema = mySqlBinlogConnectJavaProperties.getSchema();
        Integer port = mySqlBinlogConnectJavaProperties.getPort();
        String username = mySqlBinlogConnectJavaProperties.getUsername();
        String password = mySqlBinlogConnectJavaProperties.getPassword();

        // 配置客户端连接
        BinaryLogClient client = new BinaryLogClient(hostname, port, schema, username, password);

        // 配置事件序列化策略
        EventDeserializer eventDeserializer = getEventDeserializer();
        if (eventDeserializer != null) {
            client.setEventDeserializer(eventDeserializer);
        }

        // 添加事件处理器
        client.registerEventListener(dataCollectionService::collectionIncrementalData);

        Long position = binlogInfoService.getBinlogNextPosition();
        String filename = binlogInfoService.getBinlogFileName();
        if (position != null) {
            // 设置 Binlog 起始位置
            client.setBinlogPosition(position);
        }
        if (StringUtils.isNotEmpty(filename)) {
            // 设置 Binlog 文件名
            client.setBinlogFilename(filename);
        }

        client.connect();
    }

    /**
     * 获取事件序列化规则
     *
     * @return {@link com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer}
     * @author nza
     * @createTime 2020/12/21 13:28
     */
    private EventDeserializer getEventDeserializer() {
        EventDeserializer eventDeserializer;
        try {
            // 先从容器中获取
            eventDeserializer = context.getBean(EventDeserializer.class);
        } catch (NoSuchBeanDefinitionException e) {
            eventDeserializer = null;
            // 设置字段序列化规则
//            eventDeserializer = new EventDeserializer();
//            eventDeserializer.setCompatibilityMode(
//                    EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
//                    EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
//            );
        }
        return eventDeserializer;
    }
}
