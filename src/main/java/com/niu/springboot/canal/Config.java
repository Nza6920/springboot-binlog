package com.niu.springboot.canal;

import com.alibaba.google.common.cache.Cache;
import com.alibaba.google.common.cache.CacheBuilder;
import com.alibaba.google.common.cache.CacheLoader;
import com.alibaba.google.common.cache.LoadingCache;
import com.niu.springboot.canal.domain.Item;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author jiabin.xu
 * @descriptoion
 */
@Configuration
public class Config {


    @Bean
    public Cache geetCache() {
        Cache cache = CacheBuilder.newBuilder()
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .maximumSize(5000).build();
        return cache;
    }

}
