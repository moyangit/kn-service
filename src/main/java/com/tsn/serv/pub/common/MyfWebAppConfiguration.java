package com.tsn.serv.pub.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyfWebAppConfiguration extends WebMvcConfigurerAdapter {

    //拦截器，拦截文件流
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FileHeaderCheckInterceptor())
                .addPathPatterns("/**");
    }
}
