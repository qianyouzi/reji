package com.reji.config;

import com.reji.bean.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import sun.util.xml.PlatformXmlPropertiesProvider;

@Slf4j
@org.aspectj.lang.annotation.Aspect
@Component
public class Aspect {

    @Pointcut("execution(* com.reji.controller.OrderController.userPage(..))")
    public void pointcut() {
    }

    ;

    @Around("pointcut()")
    public Object run(ProceedingJoinPoint pjp) {
        try {
            Object[] args = pjp.getArgs();
          return pjp.proceed(args);
        } catch (Exception c) {
            c.getMessage();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }


}
