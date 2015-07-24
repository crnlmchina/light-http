package com.dangdang.light.http.exception;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基本的日志处理实现，可配置需要降级记录的异常类型
 * 
 * @author yuxuan
 *
 */
public class SimpleExceptionLogger implements ExceptionLogger {

	private Logger logger = LoggerFactory.getLogger(SimpleExceptionLogger.class);

	/**
	 * 需要在日志中降级为WARN记录的异常
	 */
	private Set<Class<Throwable>> exceptionsToDowngrade;

	public void setExceptionsToDowngrade(Set<Class<Throwable>> exceptionsToDowngrade) {
		this.exceptionsToDowngrade = exceptionsToDowngrade;
	}

	@Override
	public void log(String message, Throwable cause) {
		Class<? extends Throwable> clazz = cause.getClass();
		if (exceptionsToDowngrade != null && exceptionsToDowngrade.contains(clazz)) {
			logger.warn("[{}]{}", cause.getClass(), message);
		} else {
			logger.error(message, cause);
		}
	}

}
