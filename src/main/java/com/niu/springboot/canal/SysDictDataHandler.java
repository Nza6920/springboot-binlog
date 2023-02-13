package com.niu.springboot.canal;

import com.alibaba.fastjson.JSON;
import com.niu.springboot.canal.domain.SysDictData;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

/**
 * @author jiabin.xu
 * @descriptoion
 */
@CanalTable("sys_dict_data")
@Component
public class SysDictDataHandler implements EntryHandler<SysDictData> {

    @Override
    public void insert(SysDictData sysDictData) {
        System.out.println("insert="+ JSON.toJSONString(sysDictData));
    }


    @Override
    public void update(SysDictData before, SysDictData after) {
        System.out.println("before update="+ JSON.toJSONString(before));
        System.out.println("update="+ JSON.toJSONString(before));
    }


    @Override
    public void delete(SysDictData sysDictData) {
        System.out.println("insert="+ JSON.toJSONString(sysDictData));
    }
}
