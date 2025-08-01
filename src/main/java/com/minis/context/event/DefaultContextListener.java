package com.minis.context.event;

import com.minis.context.ApplicationContext;
import com.minis.context.event.ApplicationListener;
import com.minis.context.event.ContextRefreshedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultContextListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        StringBuilder sb = new StringBuilder();
        for (String beanDefinitionName : beanDefinitionNames) {
            sb.append(beanDefinitionName);
            sb.append(" | ");
        }
        log.debug("IoC WebApplicationContext created beans: [{}]", sb);
    }
}
