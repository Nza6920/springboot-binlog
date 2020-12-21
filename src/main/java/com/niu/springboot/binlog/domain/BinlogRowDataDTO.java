package com.niu.springboot.binlog.domain;

import com.github.shyiko.mysql.binlog.event.EventType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * Binlog 数据
 *
 * @author [nza]
 * @version 1.0 [2020/12/21 11:21]
 * @createTime [2020/12/21 11:21]
 */
@Data
@Accessors(chain = true)
public class BinlogRowDataDTO {

    /**
     * 数据库名
     */
    private String schemaName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 事件类型
     */
    private EventType eventType;

    /**
     * 更新之后的数据，对于删除类型来说，即为空
     */
    private List<Map<String, String>> after;

    /**
     * 更新之前的数据，对于插入类型来说，即为空
     */
    private List<Map<String, String>> before;
}
