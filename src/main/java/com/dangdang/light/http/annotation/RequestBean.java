package com.dangdang.light.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Request请求映射
 * 
 * @author wangyuxuan
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RequestBean {

	/**
	 * 请求路径
	 * 
	 * @return
	 */
	String path();

	/**
	 * 必须的参数 name-numeric-maxLen 参数名-是否为数字-最大长度
	 * 
	 * @return
	 */
	String[] requiredParams() default {};

	/**
	 * 请求类型
	 * 
	 * @return
	 */
	String httpMethod() default "GET";
}
