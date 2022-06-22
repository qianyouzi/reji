package com.reji.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 缓存key生成配置类
 * @author 74545
 */
@Configuration
public class MyCacheConfig {

    @Bean("myKeyGenerator")
    public org.springframework.cache.interceptor.KeyGenerator keyGenerator(){
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                return method.getName()+"["+ Arrays.asList(params)+"]";
            }
        };
    }
}
