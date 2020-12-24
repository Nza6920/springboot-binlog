package com.niu.springboot.autoconfig.service;

/**
 * binlog 信息业务类
 *
 * @author [nza]
 * @version 1.0 [2020/12/24 09:59]
 * @createTime [2020/12/24 09:59]
 */
public interface BinlogInfoService {

    /**
     * 获取 binlog 文件名
     *
     * @param param 参数
     * @return {@link java.lang.String} 文件名
     * @author nza
     * @createTime 2020/12/24 10:00
     */
    String getBinlogFileName(Object... param);

    /**
     * 获取 binlog 下一个读取位置
     *
     * @param param 参数
     * @return {@link java.lang.String} 文件名
     * @author nza
     * @createTime 2020/12/24 10:00
     */
    Long getBinlogNextPosition(Object... param);
}
