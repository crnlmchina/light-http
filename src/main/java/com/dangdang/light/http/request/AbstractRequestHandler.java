package com.dangdang.light.http.request;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.light.http.annotation.RequestBean;
import com.dangdang.light.http.response.ResponseInfo;
import com.dangdang.light.http.util.CollUtil;
import com.dangdang.light.http.util.RequestBeanUtil;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

/**
 * 抽象请求处理逻辑
 * 
 * @author wangyuxuan
 * 
 */
public abstract class AbstractRequestHandler implements IRequestHandler<ResponseInfo> {

	/**
	 * 编码错误
	 */
	protected static final ResponseInfo ERROR_INFO_ERROR_ENCODING = new ResponseInfo(110, "Error decoding with UTF-8.", null);
	/**
	 * 参数值的分隔符
	 */
	protected static final char PARAM_VALUE_SEPARATOR_CHAR = ',';

	private List<ParameterDefination> requiredParams;

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRequestHandler.class);

	@PostConstruct
	private void init() {
		Class<? extends AbstractRequestHandler> clazz = this.getClass();
		RequestBean requestBeanAnnotation = clazz.getAnnotation(RequestBean.class);
		if (requestBeanAnnotation != null) {
			this.requiredParams = RequestBeanUtil.extractParameterDefination(requestBeanAnnotation);
			LOGGER.info("Initial required parameters " + this.requiredParams + " for bean: " + clazz);
		}
	}

	@Override
	public final ResponseInfo handle(LightHttpRequest request) {
		Map<String, String> params = CollUtil.mergeMap(request.getGetParmas(), request.getPostParams());

		ResponseInfo errorInfo = null;
		errorInfo = checkRequiredParams(params);
		if (errorInfo != null) {
			return errorInfo;
		}

		return handleRequest(params);
	}

	/**
	 * 检查请求中的参数是否符合要求
	 * 
	 * @param params
	 * @return
	 */
	private ResponseInfo checkRequiredParams(Map<String, String> params) {
		for (ParameterDefination paramDefine : this.requiredParams) {
			String name = paramDefine.getName();
			String value = params.get(name);
			if (Strings.isNullOrEmpty(value)) {
				return new ResponseInfo(101, "Required parameter not provided or with blank value.", name);
			}
			if (paramDefine.isNumeric()) {
				if (!CharMatcher.DIGIT.matchesAllOf(value)) {
					return new ResponseInfo(102, "Required parameter is not number.", name);
				}
			}
			if (paramDefine.getMaxLen() != -1) {
				if (value.length() > paramDefine.getMaxLen()) {
					return new ResponseInfo(103, "The length of parameter must not longer than " + paramDefine.getMaxLen() + ".", name);
				}
			}

		}
		return null;
	}

	/**
	 * 处理请求
	 * 
	 * @param params
	 *            request参数
	 * @return
	 */
	protected abstract ResponseInfo handleRequest(Map<String, String> params);

}
