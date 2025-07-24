package com.minis.web.context.support;

import com.minis.context.support.ClassPathXmlApplicationContext;
import com.minis.web.context.WebApplicationContext;
import jakarta.servlet.ServletContext;

//TODO: Ioc container, also called servlet's parent container
public class XmlWebApplicationContext extends ClassPathXmlApplicationContext implements WebApplicationContext {
    private ServletContext servletContext;
    public XmlWebApplicationContext(String fileName) {
        super(fileName);
    }
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
