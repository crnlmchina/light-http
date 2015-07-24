package com.dangdang.light.http.netty;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Netty Channel Context
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ChannelContext {

	private static final String CLIENT_IP_KEY = "clientIp";

	private static ThreadLocal<Map<String, String>> context = new ThreadLocal<Map<String, String>>() {

		@Override
		protected Map<String, String> initialValue() {
			return Maps.newHashMap();
		}

	};

	public static String getClientIp() {
		return context.get().get(CLIENT_IP_KEY);
	}

	public static void setClientIp(String ip) {
		context.get().put(CLIENT_IP_KEY, ip);
	}

}
