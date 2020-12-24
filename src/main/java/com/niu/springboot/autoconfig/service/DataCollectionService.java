package com.niu.springboot.autoconfig.service;

import com.github.shyiko.mysql.binlog.event.Event;

import java.util.List;
import java.util.Map;

/**
 * 数据收集业务类
 *
 * @author [nza]
 * @version 1.0 [2020/12/21 11:13]
 * @createTime [2020/12/21 11:13]
 */
public interface DataCollectionService {

    /**
     * 收集增量数据
     *
     * @param event binlog 事件
     * @author nza
     * @createTime 2020/12/21 11:18
     */
    void collectionIncrementalData(Event event);

    /**
     * 获取数据库 数据表列的编码 -> 字段名 的Map
     *
     * @param schema    数据库名
     * @param tableName 表名
     * @return {@link java.util.Map<java.lang.Integer,java.lang.String>}
     * @author nza
     * @createTime 2020/12/21 13:41
     */
    Map<Integer, String> getDbPosMap(String schema, String tableName);

    /**
     * 获取数据库主键
     *
     * @param schema    数据库名
     * @param tableName 表名
     * @return {@link java.util.Map<java.lang.Integer,java.lang.String>}
     * @author nza
     * @createTime 2020/12/21 13:41
     */
    List<String> getPrimaryKeys(String schema, String tableName);
}
