package com.reji;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 74545
 */
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
