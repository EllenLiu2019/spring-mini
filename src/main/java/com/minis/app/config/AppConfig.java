package com.minis.app.config;

import com.minis.aop.springframework.aop.Pointcut;
import com.minis.aop.springframework.aop.support.NameMatchMethodPointcut;
import com.minis.context.annotation.Bean;
import com.minis.context.annotation.Configuration;
import com.minis.test.mvc.DateInitializer;
import com.minis.web.bind.support.WebBindingInitializer;
import com.minis.web.servlet.DispatcherServlet;

@Configuration
public class AppConfig {

    @Bean
    public WebBindingInitializer dateInitializer() {
        return new DateInitializer();
    }

    @Bean
    public Pointcut nameMatchMethodPointcut() {
        return new NameMatchMethodPointcut("do*");
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

}
