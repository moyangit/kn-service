package com.tsn.serv.auth.common.config.interceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 拦截请求合法校验
 * @author admin
 *
 */
public class ReqAuthInterceptor extends HandlerInterceptorAdapter {

	public static final Logger logger = LoggerFactory.getLogger(ReqAuthInterceptor.class);

	@Override
	public void afterCompletion(HttpServletRequest httpservletrequest,
			HttpServletResponse httpservletresponse, Object obj,
			Exception exception) throws Exception {
		super.afterCompletion(httpservletrequest, httpservletresponse, obj,
				exception);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String auth = httpRequest.getHeader("authInfo");
		return true;
	}

}
