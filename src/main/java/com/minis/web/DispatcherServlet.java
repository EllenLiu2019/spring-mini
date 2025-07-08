package com.minis.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {
    private Map<String, MappingValue> mappingValues;
    private Map<String, Class<?>> mappingClz = new HashMap<>();
    private Map<String, Object> mappingObjs = new HashMap<>();
    private String sContextConfigLocation;
    public DispatcherServlet() {
        super();
    }
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        this.sContextConfigLocation = config.getInitParameter("contextConfigLocation");
        URL xmlPath = null;
        try {
            xmlPath = this.getServletContext().getResource(this.sContextConfigLocation);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Resource rs = new ClassPathXmlResource(xmlPath);
        XmlConfigReader reader = new XmlConfigReader();
        mappingValues = reader.loadConfig(rs);
        Refresh();
    }

    //TODO: 对所有的mappingValues中注册的类进行实例化，默认构造函数
    protected void Refresh() {
        for (Map.Entry<String, MappingValue> entry : mappingValues.entrySet()) {
            String id = entry.getKey();
            String className = entry.getValue().getClz();
            Object obj = null;
            Class<?> clz = null;
            try {
                clz = Class.forName(className);
                obj = clz.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mappingClz.put(id, clz);
            mappingObjs.put(id, obj);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sPath = request.getServletPath();
        if (this.mappingValues.get(sPath) == null) {
            return;
        }

        Class<?> clz = this.mappingClz.get(sPath);
        Object obj = this.mappingObjs.get(sPath);
        String methodName = this.mappingValues.get(sPath).getMethod();
        Object objResult = null;
        try {
            Method method = clz.getMethod(methodName);
            objResult = method.invoke(obj);
        } catch (Exception ignored) {
        }

        //TODO: 将方法返回值写入response
        assert objResult != null;
        response.getWriter().append(objResult.toString());
    }

    /*@Override
    public void destroy() {
        this.mappingValues.clear();
        this.mappingClz.clear();
        this.mappingObjs.clear();
        super.destroy();
    }*/
}
