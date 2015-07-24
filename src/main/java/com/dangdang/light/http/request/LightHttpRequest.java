package com.dangdang.light.http.request;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求的简单封装
 * 
 * @author wangyuxuan
 * 
 */
public class LightHttpRequest {

	private String path;
	private Map<String, String> getParmas;
	private Map<String, String> postParams;
	
	public LightHttpRequest() {
		this.getParmas = new HashMap<String, String>();
		this.postParams = new HashMap<String, String>();
	}

	public void addGetParams(String key, String value) {
		this.getParmas.put(key, value);
	}

	public void addPostParams(String key, String value) {
		this.postParams.put(key, value);
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, String> getGetParmas() {
		return this.getParmas;
	}

	public Map<String, String> getPostParams() {
		return this.postParams;
	}

	@Override
	public String toString() {
		return "MyHttpRequest [path=" + this.path + ", getParmas=" + this.getParmas + ", postParams=" + this.postParams + "]";
	}

}
