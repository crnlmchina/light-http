package com.dangdang.light.http.serial;

/**
 * Object to string 序列器
 * 
 * @author wangyuxuan
 * 
 */
public interface IObjectSerializer {

	/**
	 * 序列化对象
	 * 
	 * @param obj
	 * @return
	 */
	String serial(Object obj);

	/**
	 * 序列化的Media-Type
	 * 
	 * @return
	 */
	String getMediaType();

}
