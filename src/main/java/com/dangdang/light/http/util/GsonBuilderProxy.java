package com.dangdang.light.http.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 构造Gson
 * @author yuxuan
 *
 */
public class GsonBuilderProxy {

	private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public Gson create() {
		return new GsonBuilder()
		.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		.setDateFormat(DATETIME_FORMAT)
		.create();
	}

}
