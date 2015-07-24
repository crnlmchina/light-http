package com.dangdang.light.http.serial;

import com.dangdang.light.http.util.GsonBuilderProxy;
import com.google.gson.Gson;

/**
 * Gson 序列化实现
 * 
 * @author wangyuxuan
 * 
 */
public class GsonObjectSerializer implements IObjectSerializer {

	private static final String TEXT_JSON = "text/json;";
	private Gson gson;

	public GsonObjectSerializer() {
		this.gson = new GsonBuilderProxy().create();
	}

	@Override
	public String serial(Object obj) {
		return this.gson.toJson(obj);
	}

	@Override
	public String getMediaType() {
		return TEXT_JSON;
	}

}
