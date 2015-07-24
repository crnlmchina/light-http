package com.dangdang.light.http.exception;

/**
 * 异常处理器注册
 * 
 * @author yuxuan
 *
 */
public class ExceptionLoggerContext {

	private static ExceptionLogger exceptionLogger;

	public static void register(ExceptionLogger exceptionLogger) {
		ExceptionLoggerContext.exceptionLogger = exceptionLogger;
	}

	public static ExceptionLogger getExceptionLogger() {
		return exceptionLogger;
	}

}
