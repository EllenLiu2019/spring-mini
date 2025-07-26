package com.minis.web.context.support;

import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.context.support.GenericApplicationContext;
import com.minis.web.context.WebApplicationContext;
import jakarta.servlet.ServletContext;

public class GenericWebApplicationContext extends GenericApplicationContext implements WebApplicationContext {
    private ServletContext servletContext;
    public GenericWebApplicationContext() {
    }
    public GenericWebApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    protected void onRefresh() {
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        if (this.servletContext != null) {
            //beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext));
        }
    }
}
