package com.dangdang.light.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.dangdang.light.http.annotation.RequestBean;
import com.dangdang.light.http.request.IRequestHandler;
import com.dangdang.light.http.request.LightHttpRequest;
import com.dangdang.light.http.serial.IObjectSerializer;

/**
 * 请求分发
 * 
 * @author yuxuan.wang
 * 
 */
public class SimpleRequestPathDispatcher<T> implements IRequestPathDispather {

	private static final String EMPTY_STRING = "";

	private static final String CONTENT_TYPE_CHARSET_PART = " charset=UTF-8";

	private Map<String, IRequestHandler<T>> pathMappings;

	@Autowired
	private ApplicationContext context;

	private IObjectSerializer serializer;

	public SimpleRequestPathDispatcher(IObjectSerializer serializer) {
		super();
		this.serializer = serializer;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRequestPathDispatcher.class);

	@PostConstruct
	private void init() {
		this.pathMappings = new HashMap<String, IRequestHandler<T>>();
		Map<String, Object> requestBeans = this.context.getBeansWithAnnotation(RequestBean.class);
		for (Entry<String, Object> entry : requestBeans.entrySet()) {
			Object requestBean = entry.getValue();
			if (!(requestBean instanceof IRequestHandler)) {
				LOGGER.warn("Request bean not instance of IRequestHandler, ignore: " + entry);
			} else {
				@SuppressWarnings("unchecked")
				IRequestHandler<T> requestHandlerBean = (IRequestHandler<T>) requestBean;
				Class<?> requestBeanClazz = AopUtils.getTargetClass(requestBean);
				RequestBean requestBeanAnnotation = requestBeanClazz.getAnnotation(RequestBean.class);
				String path = requestBeanAnnotation.path();
				this.pathMappings.put(path, requestHandlerBean);
				LOGGER.info("Mapping request [" + path + "] to request bean: " + entry);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dangdang.light.http.IRequestPathDispather#dispatch(com.dangdang.light
	 * .http.LightHttpRequest)
	 */
	@Override
	public String dispatch(LightHttpRequest request) {
		String path = request.getPath();
		IRequestHandler<T> handler = this.pathMappings.get(path);
		if (handler != null) {
			return this.serializer.serial(handler.handle(request));
		}
		return EMPTY_STRING;
	}

	@Override
	public String getContentType() {
		return this.serializer.getMediaType() + CONTENT_TYPE_CHARSET_PART;
	}

}
