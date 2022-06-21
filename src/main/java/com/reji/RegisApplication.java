package com.reji;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 74545
 */
@EnableCaching //开启缓存注解功能
@EnableAspectJAutoProxy
@EnableTransactionManagement //开启事务管理
@ServletComponentScan
@MapperScan("com.reji.dao")
@Slf4j
@SpringBootApplication
public class RegisApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegisApplication.class,args);

    }
}
