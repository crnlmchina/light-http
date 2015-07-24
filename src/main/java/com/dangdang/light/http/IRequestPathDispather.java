package com.dangdang.light.http;

import com.dangdang.light.http.request.LightHttpRequest;

/**
 * 请求分发器
 * 
 * @author wangyuxuan
 * 
 */
public interface IRequestPathDispather {

	/**
	 * 根据请求的path分发请求
	 * 
	 * @param request
	 * @return
	 */
	public String dispatch(LightHttpRequest request);

	/**
	 * 响应的Content-Type
	 * 
	 * @return
	 */
	public String getContentType();

}