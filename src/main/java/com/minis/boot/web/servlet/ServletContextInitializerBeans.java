package com.minis.boot.web.servlet;

import com.minis.beans.BeansException;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.ListableBeanFactory;
import jakarta.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 手动创建 ServletContextInitializerBeans
 */
@Slf4j
public class ServletContextInitializerBeans extends AbstractCollection<ServletContextInitializer> {

    private final Map<Class<?>, ServletContextInitializer> initializers;

    private final List<Class<? extends ServletContextInitializer>> initializerTypes;

    private final List<ServletContextInitializer> sortedList;

    public ServletContextInitializerBeans(ListableBeanFactory beanFactory) {
        this.initializers = new LinkedHashMap<>();
        this.initializerTypes = Collections.singletonList(ServletContextInitializer.class);
        addServletContextInitializerBeans(beanFactory);
        this.sortedList = this.initializers.values().stream().toList();
    }

    private void addServletContextInitializerBeans(ListableBeanFactory beanFactory) {
        for (Class<? extends ServletContextInitializer> initializerType : this.initializerTypes) {
            for (Map.Entry<String, ? extends ServletContextInitializer> initializerBean :
                    getOrderedBeansOfType(beanFactory, initializerType)) {
                addServletContextInitializerBean(initializerBean.getKey(), initializerBean.getValue(), beanFactory);
            }
        }
    }

    private <T> List<Map.Entry<String, T>> getOrderedBeansOfType(ListableBeanFactory beanFactory, Class<T> type) {
        String[] names = beanFactory.getBeanNamesForType(type);
        Map<String, T> map = new LinkedHashMap<>();
        for (String name : names) {
            T bean;
            try {
                bean = (T) beanFactory.getBean(name);
            } catch (BeansException | ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            map.put(name, bean);
        }
        return new ArrayList<>(map.entrySet());
    }

    private void addServletContextInitializerBean(String beanName, ServletContextInitializer initializer,
                                                  ListableBeanFactory beanFactory) {
        if (initializer instanceof ServletRegistrationBean<?> servletRegistrationBean) {
            Servlet source = servletRegistrationBean.getServlet();
            addServletContextInitializerBean(Servlet.class, beanName, servletRegistrationBean, beanFactory, source);
        } else {
            addServletContextInitializerBean(ServletContextInitializer.class, beanName, initializer, beanFactory, initializer);
        }
    }

    private void addServletContextInitializerBean(Class<?> type, String beanName, ServletContextInitializer initializer,
                                                  ListableBeanFactory beanFactory, Object source) {
        this.initializers.put(type, initializer);
        String beanClassName = getResourceDescription(beanName, beanFactory);
        log.trace("Added existing " + type.getSimpleName() + " initializer bean '" + beanName + "'; , beanClassName=" + beanClassName);
    }

    private String getResourceDescription(String beanName, ListableBeanFactory beanFactory) {
        if (beanFactory instanceof BeanDefinitionRegistry registry) {
            return registry.getBeanDefinition(beanName).getBeanClassName();
        }
        return "unknown";
    }

    @Override
    public Iterator<ServletContextInitializer> iterator() {
        return this.sortedList.iterator();
    }

    @Override
    public int size() {
        return this.sortedList.size();
    }


}
