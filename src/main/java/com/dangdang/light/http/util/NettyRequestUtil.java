package com.dangdang.light.http.util;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dangdang.light.http.request.LightHttpRequest;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;

/**
 * @author wangyuxuan
 *
 */
public final class NettyRequestUtil {

	private static final String EMPTY_STRING = "";
	private static final String URL_PATTERN = "https?:\\/\\/[^/]+";
	private static final String HTTP = "http";

	public static LightHttpRequest toLightRequest(HttpRequest request) {
		LightHttpRequest lightRequest = new LightHttpRequest();
		extractGetData(request.getUri(), lightRequest);
		if (request.getMethod() == HttpMethod.POST) {
			extractPostData(request, lightRequest);
		}
		return lightRequest;
	}

	private static void extractPostData(HttpRequest request, LightHttpRequest lightRequest) {
		HttpPostRequestDecoder httpPostRequestDecoder = new HttpPostRequestDecoder(request);
		List<InterfaceHttpData> bodyHttpDatas = httpPostRequestDecoder.getBodyHttpDatas();
		if (bodyHttpDatas != null) {
			for (InterfaceHttpData httpData : bodyHttpDatas) {
				if (httpData.getHttpDataType() == HttpDataType.Attribute) {
					Attribute attr = (Attribute) httpData;
					try {
						lightRequest.getPostParams().put(attr.getName(), attr.getString(Charsets.UTF_8));
					} catch (IOException e) {
						// DO NOTHING
					}
				}
			}
		}
	}

	/**
	 * 解析请求数据
	 * 
	 * @param data
	 * @param request
	 * @param isUri
	 *            true: uri/ false: body
	 */
	private static void extractGetData(String data, LightHttpRequest request) {
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(data, true);
		String path = queryStringDecoder.path();
		request.setPath(removeHost(path));

		Map<String, List<String>> params = queryStringDecoder.parameters();
		if (!params.isEmpty()) {
			for (Entry<String, List<String>> p : params.entrySet()) {
				String key = p.getKey();
				List<String> vals = p.getValue();
				for (String val : vals) {
					request.addGetParams(key, val);
				}
			}
		}
	}

	private static String removeHost(String path) {
		if (!Strings.isNullOrEmpty(path) && path.startsWith(HTTP)) {
			return path.replaceFirst(URL_PATTERN, EMPTY_STRING);
		}
		return path;
	}

}
