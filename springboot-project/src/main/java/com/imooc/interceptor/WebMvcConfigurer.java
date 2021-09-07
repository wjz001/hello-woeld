package com.imooc.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> list = new ArrayList<>();
        list.add("/wx");
        list.add("/user/redirectGetUserInfo");
        list.add("/view/**");
//        list.add("/sell/wechat/**");
//        registry.addInterceptor(new UserInterceptor()).addPathPatterns("/**").excludePathPatterns(String.valueOf(list));


    }
}