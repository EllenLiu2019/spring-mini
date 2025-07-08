package com.minis.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {
    private List<String> packageNames = new ArrayList<>();
    private Map<String, Object> controllerObjs = new HashMap<>();
    private List<String> controllerNames = new ArrayList<>();
    private Map<String, Class<?>> controllerClz = new HashMap<>();
    private Map<String, Object> mappingObjs = new HashMap<>(); // TODO: URL 名称与对象的映射关系
    private Map<String, Method> mappingMethods = new HashMap<>();// TODO: URL 名称与方法的映射关系
    private String sContextConfigLocation;
    private List<String> urlMappingNames = new ArrayList<>();

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
        this.packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);

        Refresh();
    }

    //TODO: 对所有的mappingValues中注册的类进行实例化，默认构造函数
    protected void Refresh() {
        initController();
        initMapping();
    }

    private void initController() {
        this.controllerNames = scanPackages(this.packageNames);
        for (String controllerName : controllerNames) {
            Class<?> clz = null;
            try {
                clz = Class.forName(controllerName);
                this.controllerClz.put(controllerName, clz);
                Object obj = clz.getConstructor().newInstance();
                this.controllerObjs.put(controllerName, obj);
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }

    private List<String> scanPackages(List<String> packageNames) {
        List<String> tempControllerNames = new ArrayList<>();
        for (String packageName : packageNames) {
            tempControllerNames.addAll(scanPackage(packageName));
        }
        return tempControllerNames;
    }

    private Collection<String> scanPackage(String packageName) {
        List<String> result = new ArrayList<>();
        URI uri;
        try {
            uri = this.getClass().getResource("/" + packageName.replaceAll("\\.", "/")).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        File dir = new File(uri);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                scanPackage(packageName + "." + file.getName());
            } else {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    result.add(className);
                }
            }
        }
        return result;
    }

    private void initMapping() {
        for(String controllerName: this.controllerNames) {
            Class<?> aClass = this.controllerClz.get(controllerName);
            Object obj = this.controllerObjs.get(controllerName);
            Method[] methods = aClass.getDeclaredMethods();
            for(Method method: methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    String urlMapping = method.getAnnotation(RequestMapping.class).value();
                    this.urlMappingNames.add(urlMapping);
                    this.mappingObjs.put(urlMapping, obj);
                    this.mappingMethods.put(urlMapping, method);
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sPath = request.getServletPath();
        if (!this.urlMappingNames.contains(sPath)) {
            return;
        }

        Method method = this.mappingMethods.get(sPath);
        Object obj = this.mappingObjs.get(sPath);
        Object objResult = null;
        try {
            objResult = method.invoke(obj);
        } catch (Exception ignored) {
        }

        //TODO: 将方法返回值写入response
        assert objResult != null;
        response.getWriter().append(objResult.toString());
    }

}
