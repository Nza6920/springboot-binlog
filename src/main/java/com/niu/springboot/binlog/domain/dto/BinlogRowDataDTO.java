package com.niu.springboot.binlog.domain.dto;

import com.github.shyiko.mysql.binlog.event.EventType;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
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
     * 插入SQL模板
     */
    private static String INSTAR_SQL_TEMPLATE = "INSERT INTO {0} ({1}) VALUES ({2})";

    /**
     * 编辑SQL模板
     */
    private static String UPDATE_SQL_TEMPLATE = "UPDATE {0} SET {1} WHERE {2}";

    /**
     * 删除SQL模板
     */
    private static String DELETE_SQL_TEMPLATE = "DELETE FROM {0} WHERE {1}";

    /**
     * 逗号
     */
    private static String COMMA = ",";

    /**
     * 条件连接符
     */
    private static String AND = "AND";

    /**
     * 引号
     */
    private static String SINGLE_QUOTE_TEMPLATE = "''{0}''";

    /**
     * 反引号
     */
    private static String BACK_QUOTE_TEMPLATE = "`{0}`";

    /**
     * 相等模板
     */
    private static String EQUAL_TEMPLATE = "`{0}` = ''{1}''";

    /**
     * 数据库名
     */
    private String schemaName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 当前记录的位置
     */
    private Long curPosition;

    /**
     * 下一个记录的位置
     */
    private Long nextPosition;

    /**
     * 主键
     */
    private List<String> primaryKeys;

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

    /**
     * 获取SQL
     *
     * @return {@link java.lang.String}
     * @author nza
     * @createTime 2020/12/23 11:27
     */
    public List<String> getSql(String targetTable) {
        List<String> sqlList = Lists.newArrayList();
        switch (eventType) {
            case EXT_WRITE_ROWS:
                handleWriteRows(sqlList, targetTable);
                break;
            case EXT_UPDATE_ROWS:
                handleUpdateRows(sqlList, targetTable);
                break;
            case EXT_DELETE_ROWS:
                handleDeleteRows(sqlList, targetTable);
                break;
            default:
                return null;
        }

        return sqlList;
    }

    /**
     * 处理删除行
     *
     * @param sqlLists 列表
     * @author nza
     * @createTime 2020/12/23 11:53
     */
    private void handleDeleteRows(List<String> sqlLists, String targetTable) {
        for (Map<String, String> map : after) {

            List<String> wheres = Lists.newArrayList();
            map.forEach((column, value) -> {
                if (primaryKeys.contains(column)) {
                    wheres.add(MessageFormat.format(EQUAL_TEMPLATE, column, value));
                }
            });

            String where = String.join(AND, wheres);
            String table = MessageFormat.format(BACK_QUOTE_TEMPLATE, targetTable);
            String sql = MessageFormat.format(DELETE_SQL_TEMPLATE, table, where);
            sqlLists.add(sql);
        }
    }

    /**
     * 处理更新行
     *
     * @param sqlLists 列表
     * @author nza
     * @createTime 2020/12/23 11:53
     */
    private void handleUpdateRows(List<String> sqlLists, String targetTable) {
        for (int i = 0; i < after.size(); i++) {

            List<String> updates = Lists.newArrayList();
            List<String> wheres = Lists.newArrayList();

            for (Map.Entry<String, String> map : after.get(i).entrySet()) {
                String column = map.getKey();
                String val = map.getValue();

                // 判断是否是主键
                if (!primaryKeys.contains(column)) {
                    // 如果值没有变化, 则跳过
                    String beforeVal = this.before.get(i).get(column);
                    if (StringUtils.equals(beforeVal, val)) {
                        continue;
                    }
                    updates.add(MessageFormat.format(EQUAL_TEMPLATE, column, val));
                } else {
                    wheres.add(MessageFormat.format(EQUAL_TEMPLATE, column, val));
                }
            }

            String update = String.join(COMMA, updates);
            String where = String.join(AND, wheres);
            String table = MessageFormat.format(BACK_QUOTE_TEMPLATE, targetTable);
            String sql = MessageFormat.format(UPDATE_SQL_TEMPLATE, table, update, where);
            sqlLists.add(sql);
        }
    }

    /**
     * 处理插入行
     *
     * @param sqlLists 列表
     * @author nza
     * @createTime 2020/12/23 11:53
     */
    private void handleWriteRows(List<String> sqlLists, String targetTable) {
        for (Map<String, String> map : after) {
            List<String> columns = Lists.newArrayList();
            List<String> values = Lists.newArrayList();

            map.forEach((column, value) -> {
                columns.add(MessageFormat.format(BACK_QUOTE_TEMPLATE, column));
                values.add(MessageFormat.format(SINGLE_QUOTE_TEMPLATE, value));
            });

            String column = String.join(COMMA, columns);
            String value = String.join(COMMA, values);
            String table = MessageFormat.format(BACK_QUOTE_TEMPLATE, targetTable);
            String sql = MessageFormat.format(INSTAR_SQL_TEMPLATE, table, column, value);
            sqlLists.add(sql);
        }
    }
}
