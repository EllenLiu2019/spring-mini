package com.minis.web.servlet;

import com.minis.beans.BeansException;
import com.minis.context.ApplicationContext;
import com.minis.context.ApplicationContextAware;
import com.minis.web.context.WebApplicationContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.minis.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;


public class DispatcherServlet extends HttpServlet implements ApplicationContextAware {
    private static final Logger LOGGER = LogManager.getLogger(DispatcherServlet.class.getName());
    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";
    public static final String HANDLER_MAPPING_BEAN_NAME = "com.minis.web.servlet.RequestMappingHandlerMapping";
    private static final String HANDLER_ADAPTER_BEAN_NAME = "com.minis.web.servlet.RequestMappingHandlerAdapter";
    private WebApplicationContext webApplicationContext;

    //private WebApplicationContext parentWebApplicationContext;// TODO: listener start up first, so it's parent
    private HandlerMapping handlerMapping;
    private HandlerAdapter handlerAdapter;

    public DispatcherServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        LOGGER.info("Initializing Servlet '" + config.getServletName() + "'");

        super.init(config);

        this.webApplicationContext = (WebApplicationContext) config.getServletContext().getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        /*// build up servlet context,
        // servlet context 持有对 listener 创建的 parent(ioc) XmlWebApplicationContext 的单向引用
        LOGGER.debug("building up servlet context...");
        String sContextConfigLocation = config.getInitParameter("contextConfigLocation");
        webApplicationContext = new AnnotationConfigWebApplicationContext(sContextConfigLocation, parentWebApplicationContext);*/
        try {
            this.initHandler(this.webApplicationContext);
        } catch (ReflectiveOperationException | BeansException e) {
            throw new RuntimeException(e);
        }
    }

    private void initHandler(ApplicationContext context) throws ReflectiveOperationException, BeansException {
        //TODO: 注册 url->bean->method
        this.handlerMapping = (HandlerMapping) context.getBean(HANDLER_MAPPING_BEAN_NAME);
        LOGGER.trace("Detected " + this.handlerMapping);
        this.handlerAdapter = (HandlerAdapter) context.getBean(HANDLER_ADAPTER_BEAN_NAME);
        LOGGER.trace("Detected " + this.handlerAdapter);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) {
        LOGGER.debug("Dispatcher Servlet service");
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (this.webApplicationContext == null && applicationContext instanceof WebApplicationContext wac) {
            this.webApplicationContext = wac;
        }
    }

}
