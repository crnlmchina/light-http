package com.dangdang.light.http.util;

import java.util.ArrayList;
import java.util.List;

import com.dangdang.light.http.annotation.RequestBean;
import com.dangdang.light.http.request.ParameterDefination;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * @author wangyuxuan
 *
 */
public final class RequestBeanUtil {

	private static final Splitter PARAMS_SPLITTER = Splitter.on('-');

	public static List<ParameterDefination> extractParameterDefination(
			RequestBean requestBeanAnnotation) {
		List<ParameterDefination> requiredParams = new ArrayList<>();
		String[] params = requestBeanAnnotation.requiredParams();
		for (String param : params) {
			List<String> paramParts = Lists.newArrayList(PARAMS_SPLITTER
					.split(param));
			int paramPartCount = paramParts.size();
			if (paramPartCount == 1) {
				requiredParams.add(new ParameterDefination(paramParts.get(0)));
			} else if (paramPartCount == 2) {
				requiredParams.add(new ParameterDefination(paramParts.get(0),
						Boolean.parseBoolean(paramParts.get(1))));
			} else if (paramPartCount == 3) {
				requiredParams.add(new ParameterDefination(paramParts.get(0),
						Boolean.parseBoolean(paramParts.get(1)), Integer
								.parseInt(paramParts.get(2))));
			}
		}
		return requiredParams;
	}

}
