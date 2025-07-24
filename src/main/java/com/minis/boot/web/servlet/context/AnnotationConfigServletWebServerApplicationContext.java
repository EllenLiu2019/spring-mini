package com.minis.boot.web.servlet.context;

import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.context.annotation.AnnotatedBeanDefinitionReader;

/**
 * TODO: 核心context类 & 核心bean工厂类
 *                                                                 AbstractApplicationContext
 *                                                                             |
 *                                                                  GenericApplicationContext    --------------------- create & hold ----------> DefaultListableBeanFactory
 *                                                                             |
 *             SpringApplication -> BeanDefinitionLoader            GenericWebApplicationContext
 *                   |create                                                   |
 *   DefaultApplicationContextFactory                               ServletWebServerApplicationContext
 *                   |create                                                   |
 *   ServletWebServerApplicationContextFactory --- create --> AnnotationConfigServletWebServerApplicationContext   <---- hold ---||--- create & hold ----> AnnotatedBeanDefinitionReader
 *
 * TODO:
 *  ********************************************************************************************************************************
 *  *********************************** Spring boot 从程序启动开始 webApplicationContext 的创建步骤 ************************************
 *  ********************************************************************************************************************************
 *  STEP 1: Bootstrap
 *     ==> SpringApplication.run(String... args)
 *         NOTE: 创建context前的步骤：
 *               1. SpringApplication 的成员变量 applicationContextFactory 初始化为 new DefaultApplicationContextFactory
 *               2. 使用 SpringFactoriesLoader 加载并存储 配置文件 spring.factories ，配置文件中的类就是 Spring boot 自动配置的核心类
 *                  SpringFactoriesLoader loads the config from file:spring.factories into it's property:factories, the data structure is:
 *                    ------------------------------------------------------------------------------------------------------------------------------------------------
 *                    | Map<String, List<String>> [SpringFactoriesLoader#factories]
 *                    ------------------------------------------------------------------------------------------------------------------------------------------------
 *                    |     Key                                                             |    Value
 *                    ------------------------------------------------------------------------------------------------------------------------------------------------
 *                    | org.springframework.boot.ApplicationContextFactory                  | org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContextFactory
 *                    |                                                                     | org.springframework.boot.web.servlet.context.ServletWebServerApplicationContextFactory
 *                    ------------------------------------------------------------------------------------------------------------------------------------------------
 *                    |org.springframework.boot.autoconfigure.AutoConfigurationImportFilter | org.springframework.boot.autoconfigure.condition.OnBeanCondition
 *                    |                                                                     | org.springframework.boot.autoconfigure.condition.OnClassCondition
 *                    |                                                                     | org.springframework.boot.autoconfigure.condition.OnWebApplicationCondition
 *                    ------------------------------------------------------------------------------------------------------------------------------------------------
 *                注意：在 context 创建结束后，才继续下面的步骤：
 *                3. Prepare context : create BeanDefinitionLoader 注册 主类
 *                4. refresh context :
 *  ***********
 *  STEP 2: 使用 Default Application Context Factory 创建 context
 *       ==> SpringApplication.createApplicationContext()
 *       ==> org.springframework.boot.DefaultApplicationContextFactory.create(WebApplicationType) ==> #getFromSpringFactories
 *  ***********
 *  STEP 3: 读取 SpringFactoriesLoader 存储的配置文件，策略模式，会根据配置文件中的顺序尝试创建 “context factories”;
 *           ==> org.springframework.core.io.support.SpringFactoriesLoader.loadFactories(ApplicationContextFactory.class)
 *                  ------------------------------------------------------------------------------------------------------------------------------------------------
 *                  | Map<String, List<String>> [SpringFactoriesLoader#factories]
 *                  ------------------------------------------------------------------------------------------------------------------------------------------------
 *                  |     Key                                                             |    Value
 *                  ------------------------------------------------------------------------------------------------------------------------------------------------
 *                  | org.springframework.boot.ApplicationContextFactory                  | org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContextFactory
 *                  |                                                                     | org.springframework.boot.web.servlet.context.ServletWebServerApplicationContextFactory
 *                  ------------------------------------------------------------------------------------------------------------------------------------------------
 *  ***********
 *  STEP 4: 在使用 ServletWebServerApplicationContextFactory 成功后，返回成功创建的 上下文：AnnotationConfigServletWebServerApplicationContext
 *             ==> org.springframework.boot.web.servlet.context.ServletWebServerApplicationContextFactory.createContext()
 *               ==> new org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext()
 *                 ==> new org.springframework.context.annotation.AnnotatedBeanDefinitionReader()
 *  ***********
 *  STEP 5: 为 "注解配置后置处理器" 创建 bean definition，并注册于 bean definition registry
 *                   ==> org.springframework.context.annotation.AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry)
 *                     construct & register BeanDefinitions to registry for "Annotation Config Processors":
 *                     ------------------------------------------------------------------------------------------------------------------------------------------------
 *                     | BeanDefinition Registry
 *                     ------------------------------------------------------------------------------------------------------------------------------------------------
 *                     |  1. org.springframework.context.annotation.ConfigurationClassPostProcessor            [name: org.springframework.context.annotation.internalConfigurationAnnotationProcessor]
 *                     |  2. org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor [name: org.springframework.context.annotation.internalConfigurationBeanNameGenerator]
 *                     |  3. org.springframework.context.annotation.CommonAnnotationBeanPostProcessor          [name: org.springframework.context.annotation.internalCommonAnnotationProcessor]
 *                     ------------------------------------------------------------------------------------------------------------------------------------------------
 */
public class AnnotationConfigServletWebServerApplicationContext extends ServletWebServerApplicationContext {

    private final AnnotatedBeanDefinitionReader reader;

    public AnnotationConfigServletWebServerApplicationContext() {
        this.reader = new AnnotatedBeanDefinitionReader(this);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.postProcessBeanFactory(beanFactory);
    }
}
