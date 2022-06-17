package com.reji.err;

import com.reji.bean.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 * @author 74545
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final String SQLEXCEPTION = "Duplicate";

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R sqlException(SQLIntegrityConstraintViolationException sql) {
        log.error("全局异常处理器捕获异常:{}", sql.getMessage());
        if (sql.getMessage().contains(SQLEXCEPTION)) {
            String[] sp = sql.getMessage().split(" ");
            String msg = sp[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R runException(CustomException customException) {
        return R.error(customException.getMessage());
    }
}
