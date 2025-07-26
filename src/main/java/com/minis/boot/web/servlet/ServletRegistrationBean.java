package com.minis.boot.web.servlet;

import com.minis.utils.StringUtils;
import com.minis.web.servlet.DispatcherServlet;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class ServletRegistrationBean<T extends Servlet> extends DynamicRegistrationBean<ServletRegistration.Dynamic> {

    private static final String[] DEFAULT_MAPPINGS = {"/*"};

    private T servlet;

    private boolean alwaysMapUrl = true;

    private int loadOnStartup = -1;

    private MultipartConfigElement multipartConfig =
            new MultipartConfigElement("", 1024 * 1024, 1024 * 1024 * 10, 0);

    private Set<String> urlMappings = new LinkedHashSet<>();

    public ServletRegistrationBean() {
    }

    public void setServlet(DispatcherServlet servlet) {
        this.servlet = (T) servlet;
    }

    public T getServlet() {
        return this.servlet;
    }

    @Override
    protected String getDescription() {
        return "servlet " + getServletName();
    }

    public String getServletName() {
        return getOrDeduceName(this.servlet);
    }

    @Override
    protected ServletRegistration.Dynamic addRegistration(String description, ServletContext servletContext) {
        String name = getServletName();
        return servletContext.addServlet(name, this.servlet);
    }

    public void addUrlMappings(String... urlMappings) {
        this.urlMappings.addAll(Arrays.asList(urlMappings));
    }

    protected void configure(ServletRegistration.Dynamic registration) {
        super.configure(registration);
        String[] urlMapping = StringUtils.toStringArray(this.urlMappings);
        if (urlMapping.length == 0 && this.alwaysMapUrl) {
            urlMapping = DEFAULT_MAPPINGS;
        }
        if (!isEmpty(urlMapping)) {
            registration.addMapping(urlMapping);
        }
        registration.setLoadOnStartup(this.loadOnStartup);
        if (this.multipartConfig != null) {
            registration.setMultipartConfig(this.multipartConfig);
        }
    }

    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }

}
