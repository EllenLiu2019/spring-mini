package com.minis.test.listener;

import com.minis.context.ApplicationContext;
import com.minis.context.event.ApplicationListener;
import com.minis.context.event.ContextRefreshedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServletContextListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        StringBuilder sb = new StringBuilder();
        for (String beanDefinitionName : beanDefinitionNames) {
            sb.append(beanDefinitionName);
            sb.append(" | ");
        }
        log.debug("Servlet WebApplicationContext created beans: [{}]", sb);
    }
}
