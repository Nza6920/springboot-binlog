package com.niu.springboot.binlog.service;

import com.github.shyiko.mysql.binlog.event.Event;

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
}
