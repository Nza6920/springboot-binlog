//package com.niu.springboot.canal;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.google.common.cache.Cache;
//import com.niu.springboot.canal.domain.Item;
//import org.springframework.stereotype.Component;
//import top.javatool.canal.client.annotation.CanalTable;
//import top.javatool.canal.client.handler.EntryHandler;
//
//import javax.annotation.Resource;
//
///**
// *
// * //监听的表
// * @author jiabin.xu
// * @descriptoion
// */
//@CanalTable("sys_config")
//@Component
//public class ItemHandler implements EntryHandler<Item> {
////    @Autowired
////    private RedisHandler redisHandler;
//    @Resource
//    private Cache cache;
//
//    @Override
//    public void insert(Item item) {
//        // 写数据到JVM进程缓存
//        cache.put(item.getId(), item);
//        System.out.println("insert="+ JSON.toJSONString(item));
//
//        // 写数据到redis
////        redisHandler.saveItem(item);
//    }
//
//    @Override
//    public void update(Item before, Item after) {
//        // 写数据到JVM进程缓存
//        cache.put(after.getId(), after);
//        System.out.println("update="+ JSON.toJSONString(after));
//        // 写数据到redis
////        redisHandler.saveItem(after);
//    }
//
//    @Override
//    public void delete(Item item) {
//        // 删除数据到JVM进程缓存
//        cache.invalidate(item.getId());
//        System.out.println("delete="+ JSON.toJSONString(item));
//        // 删除数据到redis
////        redisHandler.deleteItemById(item.getId());
//    }
//
//
//}
