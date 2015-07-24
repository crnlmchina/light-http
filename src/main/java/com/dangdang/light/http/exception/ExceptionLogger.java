package com.dangdang.light.http.exception;

/**
 * 异常处理
 * 
 * @author yuxuan
 *
 */
public interface ExceptionLogger {

	void log(String message, Throwable cause);

}
