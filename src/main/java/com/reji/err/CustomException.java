package com.reji.err;

/**
 *业务异常类
 */

public class CustomException extends RuntimeException{
    public CustomException(String massage){
        super(massage);
    }
}
