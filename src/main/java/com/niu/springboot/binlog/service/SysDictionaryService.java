package com.niu.springboot.binlog.service;

import com.niu.springboot.binlog.domain.enums.SysDictionaryEnum;

/**
 * 系统字典业务类
 *
 * @author [nza]
 * @version 1.0 [2020/12/23 14:52]
 * @createTime [2020/12/23 14:52]
 */
public interface SysDictionaryService {

    /**
     * 根据Key更新值
     *
     * @param key 键
     * @param val 值
     * @author nza
     * @createTime 2020/12/23 14:55
     */
    void updateByKey(SysDictionaryEnum key, String val);

    /**
     * 根据Key获取值
     *
     * @param key 键
     * @return {@link String} 获取键的值
     * @author nza
     * @createTime 2020/12/23 14:55
     */
    String getValByKey(SysDictionaryEnum key);
}
