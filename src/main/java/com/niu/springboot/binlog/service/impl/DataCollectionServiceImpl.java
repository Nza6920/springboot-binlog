package com.niu.springboot.binlog.service.impl;

import com.github.shyiko.mysql.binlog.event.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.niu.springboot.autoconfig.service.DataCollectionService;
import com.niu.springboot.binlog.domain.dto.BinlogRowDataDTO;
import com.niu.springboot.binlog.domain.enums.SysDictionaryEnum;
import com.niu.springboot.binlog.service.SysDictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数据收集业务实现类
 *
 * @author [nza]
 * @version 1.0 [2020/12/21 11:14]
 * @createTime [2020/12/21 11:14]
 */
@Service
@Slf4j
public class DataCollectionServiceImpl implements DataCollectionService {

    /**
     * 可以直接转字符串的类型
     */
    private static final List<Class<?>> NORMAL_TYPE;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SysDictionaryService sysDictionaryService;

    /**
     * 查询表信息
     */
    private static final String SQL_SCHEMA;

    /**
     * 查询表主键
     */
    private static final String SQL_PRIMARY_SCHEMA;

    /**
     * 允许收集的数据类型
     */
    private static final List<EventType> ALLOW_COLLECTION_TYPES;

    /**
     * 允许收集的数据库
     */
    private static final List<String> ALLOW_COLLECTION_SCHEMAS;

    /**
     * 允许收集的数据库表
     */
    private static final List<String> ALLOW_COLLECTION_TABLES;

    /**
     * 表名
     */
    private String tableName = null;

    /**
     * 数据库名
     */
    private String dbName = null;

    /**
     * 数据库名
     */
    private Boolean isBinlogChanged = false;

    static {
        SQL_SCHEMA = "select table_schema, table_name, column_name, ordinal_position from information_schema.columns where table_schema = ? and table_name = ?";
        SQL_PRIMARY_SCHEMA = "select column_name, column_key from information_schema.columns where table_schema = ? and table_name = ? and column_key = 'PRI'";
        ALLOW_COLLECTION_TYPES = Lists.newArrayList(EventType.EXT_UPDATE_ROWS, EventType.EXT_WRITE_ROWS, EventType.EXT_DELETE_ROWS);
        ALLOW_COLLECTION_SCHEMAS = Lists.newArrayList("test_log");
        ALLOW_COLLECTION_TABLES = Lists.newArrayList("sys_config");
        NORMAL_TYPE = Lists.newArrayList(byte.class, Byte.class,
                short.class, Short.class,
                int.class, Integer.class,
                long.class, Long.class,
                float.class, Float.class,
                double.class, Double.class,
                char.class, Character.class,
                String.class,
                BigDecimal.class);
    }

    @Override
    public void collectionIncrementalData(Event event) {

        // 获取到事件类型
        EventType type = event.getHeader().getEventType();

        // 切换了 binlog 文件
        if (handleBinlogFileChange(type, event)) {
            return;
        }

        // 设置表信息
        optionTableInfo(event, type);

        // 判断是否可以收集
        if (!canCollection(type)) {
            return;
        }

        // 执行收集逻辑
        doCollection(event, type);
    }

    /**
     * 处理 binlog 文件切换事件
     *
     * @param type  事件类型
     * @param event binlog事件
     * @return boolean
     * @author nza
     * @createTime 2020/12/22 14:13
     */
    private boolean handleBinlogFileChange(EventType type, Event event) {
        if (EventType.ROTATE.equals(type)) {
            // 更新 binlog 文件相关记录配置
            String originalFile = sysDictionaryService.getValByKey(SysDictionaryEnum.BIN_LOG_FILE_NAME);
            String binlogFilename = ((RotateEventData) event.getData()).getBinlogFilename();
            // 如果文件未变化忽略即可
            if (StringUtils.equals(binlogFilename, originalFile)) {
                return true;
            }

            isBinlogChanged = true;
            sysDictionaryService.updateByKey(SysDictionaryEnum.BIN_LOG_FILE_NAME, binlogFilename);
            return true;
        }
        if (EventType.FORMAT_DESCRIPTION.equals(type) && isBinlogChanged) {
            // 更新 binlog 开始位置记录配置
            long nextPosition = ((EventHeaderV4) event.getHeader()).getNextPosition();
            sysDictionaryService.updateByKey(SysDictionaryEnum.BIN_LOG_NEXT_POSITION, String.valueOf(nextPosition));
            isBinlogChanged = false;
            return true;
        }
        return false;
    }

    /**
     * 执行收集逻辑
     *
     * @param event binlog 事件
     * @param type  事件类型
     * @throws {@link Exception} 收集失败抛出
     * @author nza
     * @createTime 2020/12/21 14:36
     */
    private void doCollection(Event event, EventType type) {
        try {
            // 查询表映射信息
            Map<Integer, String> dbPosMap = getDbPosMap(dbName, tableName);

            // 构造 BinlogRowData 对象
            BinlogRowDataDTO rowData = buildRowData(event.getData(), type, dbPosMap);
            rowData.setNextPosition(((EventHeaderV4) event.getHeader()).getNextPosition());
            rowData.setCurPosition(((EventHeaderV4) event.getHeader()).getPosition());

            log.info("收集完成: {}", rowData);

            // 将数据变动同步到备份表
            doBackup(rowData);


        } catch (Exception ex) {
            log.error("收集增量数据发送异常, 异常信息: ", ex);
        } finally {
            // 重置库名和表名
            this.dbName = null;
            this.tableName = null;
        }
    }

    /**
     * 将数据变动同步到备份表
     *
     * @param rowData 源数据
     * @author nza
     * @createTime 2020/12/23 14:33
     */
    private void doBackup(BinlogRowDataDTO rowData) {
        String table = "sys_config_copy";
        for (String sql : rowData.getSql(table)) {
            int res = jdbcTemplate.update(sql);
            log.info("同步完成, 影响行: {}", res);
        }

        // 更新配置表
        sysDictionaryService.updateByKey(SysDictionaryEnum.BIN_LOG_NEXT_POSITION, String.valueOf(rowData.getNextPosition()));
    }

    /**
     * 校验是否可以收集
     *
     * @param type 事件类型
     * @return boolean true 可以 false 不可以
     * @author nza
     * @createTime 2020/12/21 14:34
     */
    private boolean canCollection(EventType type) {
        // 如果不是更新、插入、删除事件, 直接忽略即可
        if (!ALLOW_COLLECTION_TYPES.contains(type)) {
            return false;
        }

        // 表名和库名是否已经完成填充
        if (StringUtils.isEmpty(dbName) || StringUtils.isEmpty(tableName)) {
            log.error("no meta data event");
            return false;
        }

        // 是否在数据库白名单中
        if (!ALLOW_COLLECTION_SCHEMAS.contains(dbName)) {
            return false;
        }

        // 是否在数据库表白名单中
        return ALLOW_COLLECTION_TABLES.contains(tableName);
    }

    /**
     * 构建行数据
     *
     * @param data 事件
     * @return {@link BinlogRowDataDTO} 行数据
     * @author nza
     * @createTime 2020/12/21 14:28
     */
    private BinlogRowDataDTO buildRowData(EventData data, EventType eventType, Map<Integer, String> dbPosMap) throws Exception {

        BinlogRowDataDTO binlogRowDataDTO = new BinlogRowDataDTO();
        List<String> primaryKeys = getPrimaryKeys(dbName, tableName);
        List<Map<String, String>> after = Lists.newArrayList();
        List<Map<String, String>> before = Lists.newArrayList();

        switch (eventType) {
            case EXT_WRITE_ROWS:
                processWriteRows((WriteRowsEventData) data, dbPosMap, after);
                break;
            case EXT_UPDATE_ROWS:
                processUpdateRows((UpdateRowsEventData) data, dbPosMap, after, before);
                break;
            case EXT_DELETE_ROWS:
                processDeleteRows((DeleteRowsEventData) data, dbPosMap, after);
                break;
            default:
                throw new Exception("非法的数据行类型: " + eventType.name());
        }

        binlogRowDataDTO.setPrimaryKeys(primaryKeys)
                .setSchemaName(dbName)
                .setTableName(tableName)
                .setAfter(after)
                .setBefore(before)
                .setEventType(eventType);

        return binlogRowDataDTO;
    }

    /**
     * 处理删除行操作
     *
     * @param data     binlog 源数据
     * @param dbPosMap 表映射
     * @param after    变更后的数据
     * @author nza
     * @createTime 2020/12/21 17:31
     */
    private void processDeleteRows(DeleteRowsEventData data, Map<Integer, String> dbPosMap, List<Map<String, String>> after) {
        BitSet columns = data.getIncludedColumns();
        List<Serializable[]> rows = data.getRows();
        addRowData(dbPosMap, after, columns, rows);
    }

    /**
     * 处理插入数据
     *
     * @param data     binlog 数据
     * @param dbPosMap 数据库映射
     * @param after    变更后的数据
     * @author nza
     * @createTime 2020/12/21 17:11
     */
    private void processWriteRows(WriteRowsEventData data, Map<Integer, String> dbPosMap, List<Map<String, String>> after) {
        BitSet columns = data.getIncludedColumns();
        List<Serializable[]> rows = data.getRows();
        addRowData(dbPosMap, after, columns, rows);
    }

    /**
     * 添加行数据
     *
     * @param dbPosMap 表映射
     * @param rowList  行数据列表
     * @param columns  行信息
     * @param rows     需要转换的binlog源数据
     * @author nza
     * @createTime 2020/12/21 17:20
     */
    private void addRowData(Map<Integer, String> dbPosMap, List<Map<String, String>> rowList, BitSet columns, List<Serializable[]> rows) {
        for (Serializable[] row : rows) {
            Map<String, String> afterRow = Maps.newHashMap();
            for (int i = 0; i < row.length; i++) {
                Object item = row[i];

                // todo：这里需要做数据类型转换
                afterRow.put(dbPosMap.get(columns.nextSetBit(i)), convert2SqlStr(item));
            }
            rowList.add(afterRow);
        }
    }

    /**
     * 转换Sql字符串
     *
     * @param item 参数
     * @return {@link java.lang.String}
     * @author nza
     * @createTime 2020/12/22 13:21
     */
    private String convert2SqlStr(Object item) {
        if (item == null) {
            return null;
        }
        if (NORMAL_TYPE.contains(item.getClass())) {
            return String.valueOf(item);
        }
        if (item instanceof Boolean) {
            return (boolean) item ? String.valueOf(1) : String.valueOf(0);
        }
        if (item instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item);
        }
        return null;
    }

    /**
     * 处理更新行
     *
     * @param data     binlog 数据
     * @param dbPosMap 数据库字段映射
     * @param after    变更前
     * @param before   变更后
     * @author nza
     * @createTime 2020/12/21 16:45
     */
    private void processUpdateRows(UpdateRowsEventData data, Map<Integer, String> dbPosMap, List<Map<String, String>> after, List<Map<String, String>> before) {
        BitSet columns = data.getIncludedColumns();
        List<Map.Entry<Serializable[], Serializable[]>> rows = data.getRows();
        for (Map.Entry<Serializable[], Serializable[]> entry : rows) {

            // 添加变动前的数据列表
            addRowData(dbPosMap, before, columns, Collections.singletonList(entry.getKey()));

            // 添加变动后的数据列表
            addRowData(dbPosMap, after, columns, Collections.singletonList(entry.getValue()));
        }
    }

    /**
     * 设置表信息
     *
     * @param event 事件
     * @param type  类型
     * @author nza
     * @createTime 2020/12/21 14:26
     */
    private void optionTableInfo(Event event, EventType type) {
        // 如果是 TABLE_MAP 事件，可以从中获取到操作的库名和表名
        if (type == EventType.TABLE_MAP) {
            TableMapEventData data = event.getData();
            tableName = data.getTable();
            dbName = data.getDatabase();
        }
    }

    @Override
    public Map<Integer, String> getDbPosMap(String schema, String tableName) {

        Map<Integer, String> posMap = Maps.newHashMap();

        jdbcTemplate.query(SQL_SCHEMA, new String[]{schema, tableName}, (rs, i) -> {
            int pos = rs.getInt("ORDINAL_POSITION");
            String colName = rs.getString("COLUMN_NAME");

            posMap.put(pos - 1, colName);
            return posMap;
        });

        return posMap;
    }

    @Override
    public List<String> getPrimaryKeys(String schema, String tableName) {

        List<String> primaryKeys = Lists.newArrayList();
        jdbcTemplate.query(SQL_PRIMARY_SCHEMA, new String[]{schema, tableName}, (rs, i) -> {
            String columnName = rs.getString("column_name");
            if (!StringUtils.isEmpty(columnName)) {
                primaryKeys.add(columnName);
            }
            return primaryKeys;
        });

        return primaryKeys;
    }
}
