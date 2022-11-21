package com.tsn.serv.auth.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.tsn.serv.auth.common.config.interceptor.ReqAuthInterceptor;

@Configuration
public class InterceptorConfigurer extends WebMvcConfigurerAdapter {

	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ReqAuthInterceptor())
		.addPathPatterns("/**")
		.excludePathPatterns("/error/**")
		.excludePathPatterns("/api/*")
		.excludePathPatterns("/admin/**")
		.excludePathPatterns("/auth/**")
		.excludePathPatterns("/getSysManageLoginCode")  // 验证码
		.excludePathPatterns("/checkimagecode");	// 验证验证码
	}
}