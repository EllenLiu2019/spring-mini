package com.minis.test.listener;

import com.minis.context.ApplicationContext;
import com.minis.context.ApplicationListener;
import com.minis.context.ContextRefreshedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
