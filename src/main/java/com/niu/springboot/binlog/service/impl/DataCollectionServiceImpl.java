package com.niu.springboot.binlog.service.impl;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.niu.springboot.binlog.service.DataCollectionService;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * 数据收集业务实现类
 *
 * @author [nza]
 * @version 1.0 [2020/12/21 11:14]
 * @createTime [2020/12/21 11:14]
 */
@Service
@AllArgsConstructor
public class DataCollectionServiceImpl implements DataCollectionService {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() throws IOException {

    }

    @Override
    public void collectionIncrementalData(Event event) {

    }
}
