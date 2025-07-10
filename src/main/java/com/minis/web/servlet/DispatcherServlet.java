package com.minis.web.servlet;

import com.minis.beans.BeansException;
import com.minis.web.context.WebApplicationContext;
import com.minis.web.context.support.AnnotationConfigWebApplicationContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DispatcherServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger(DispatcherServlet.class.getName());
    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";
    private WebApplicationContext webApplicationContext;
    private WebApplicationContext parentWebApplicationContext;// TODO: listener start up first, so it's parent
    private HandlerMapping handlerMapping;
    private HandlerAdapter handlerAdapter;
    public DispatcherServlet() {
        super();
        LOGGER.debug("Dispatcher Servlet constructor invoked");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        LOGGER.debug("Dispatcher Servlet initializing");

        super.init(config);

        LOGGER.debug("getting parent webApplication context from servlet context");
        parentWebApplicationContext = (WebApplicationContext) this.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        // TODO: build up controller context,
        //  controller context 持有对 listener 创建的 parent(ioc) XmlWebApplicationContext 的单向引用
        LOGGER.debug("building up controller context...");
        String sContextConfigLocation = config.getInitParameter("contextConfigLocation");
        webApplicationContext = new AnnotationConfigWebApplicationContext(sContextConfigLocation, parentWebApplicationContext);
        try {
            this.initHandler();
        } catch (ReflectiveOperationException | BeansException e) {
            throw new RuntimeException(e);
        }
    }

    private void initHandler() throws ReflectiveOperationException, BeansException {
        //TODO: 注册 url->bean->method
        handlerMapping = new RequestMappingHandlerMapping(webApplicationContext);
        handlerAdapter = new RequestMappingHandlerAdapter(webApplicationContext);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) {
        LOGGER.debug("DispatcherServlet service");
        req.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.webApplicationContext);
        try {
            doDispatch(req, res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse res) throws Exception {
        HandlerMethod handler = this.handlerMapping.getHandler(req);
        if (handler == null) {
            return;
        }
        this.handlerAdapter.handle(req, res, handler);
    }

}
