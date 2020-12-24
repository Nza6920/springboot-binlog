package com.niu.springboot.binlog.service.impl;

import com.niu.springboot.binlog.domain.enums.SysDictionaryEnum;
import com.niu.springboot.binlog.service.SysDictionaryService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

/**
 * 系统字典业务实现类
 *
 * @author [nza]
 * @version 1.0 [2020/12/23 14:53]
 * @createTime [2020/12/23 14:53]
 */
@Service
@AllArgsConstructor
public class SysDictionaryServiceImpl implements SysDictionaryService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 更新SQL模板
     */
    private static final String UPDATE_SQL_TEMPLATE = "UPDATE `sys_dictionary` SET `value` = ''{0}'' WHERE `key` = ''{1}''";

    /**
     * 查询SQL模板
     */
    private static final String SELECT_SQL_TEMPLATE = "SELECT `value` FROM `sys_dictionary` WHERE `key` = ?";

    @Override
    public void updateByKey(SysDictionaryEnum key, String val) {
        String sql = MessageFormat.format(UPDATE_SQL_TEMPLATE, val, key.getKey());
        jdbcTemplate.update(sql);
    }

    @Override
    public String getValByKey(SysDictionaryEnum key) {
        String sql = MessageFormat.format(SELECT_SQL_TEMPLATE, key);
        List<String> res = jdbcTemplate.query(sql, new String[]{key.getKey()}, (rs, i) -> rs.getString("value"));
        return res.get(0);
    }
}
