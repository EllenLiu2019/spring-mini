package com.minis.aop;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.FactoryBean;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/*
 ** TODO: 该类是Spring AOP 的一个入口，可以理解为是 AOP 实现动态代理的工具，
 **  是 Spring 对 动态代理实现 更高层次的封装，
 **  是 Spring 的 [Proxy's Factory],
 **  实现了动态代理过程：【 动态代理工厂 --> 动态代理  --> 动态代理对象 】
 **  该工具在 IoC getBean("proxyFactoryBean") 时，
 **  生成一个【代理对象】返回，即达到侵入式编程中的效果：
 **  > DynamicProxy proxy = new DynamicProxy(action);
 **  > IAction proxyIns = (IAction) proxy.getProxy();
 */
@Slf4j
public class ProxyFactoryBean implements FactoryBean<Object>, BeanFactoryAware {

    private Object target; // TODO: the realAction actually

    /*
     ** TODO：
     **  成员对象 与 用途 的对应关系：
     **     动态代理工厂          --> 动态代理  --> 动态代理对象
     **     this.aopProxyFactory --> AopProxy --> this.singletonInstance
     **  例如：
     **    step1: 创建动态代理工厂： new DefaultAopProxyFactory()
     **    step2: 创建动态代理：    动态代理工厂用 this.target new 一个 “JDK动态代理-AopProxy”，
     **    step2: 创建代理对象：    “JDK动态代理-AopProxy” 通过 getProxy() 生成 代理对象 this.singletonInstance
     **     getProxy() -> Proxy.newProxyInstance(JdkDynamicAopProxy.class.getClassLoader(), target.getClass().getInterfaces(), this)
     */
    private final AopProxyFactory aopProxyFactory;
    private Object singletonInstance; // TODO: 生成的代理对象

    private BeanFactory beanFactory;
    @Setter
    private String interceptorName; // TODO: beanName, used to invoke getBean("interceptorName")
    private PointcutAdvisor advisor;

    public ProxyFactoryBean() {
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    private synchronized void initializeAdvisor() {
        Object advice;
        //MethodInterceptor interceptor = null;
        try {
            advice = this.beanFactory.getBean(this.interceptorName);
            /*if (advice instanceof BeforeAdvice) {
                interceptor = new MethodBeforeAdviceInterceptor((MethodBeforeAdvice) advice);
            } else if (advice instanceof AfterAdvice) {
                interceptor = new AfterReturningAdviceInterceptor((AfterReturningAdvice) advice);
            } else if (advice instanceof MethodInterceptor) {
                interceptor = (MethodInterceptor) advice;
            }
            advisor.setMethodInterceptor(interceptor);*/
            this.advisor = (PointcutAdvisor) advice;
        } catch (BeansException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getObject() throws Exception {
        initializeAdvisor();
        return getSingletonInstance();
    }

    private Object getSingletonInstance() {
        if (this.singletonInstance != null) {
            log.debug("proxy instance={} exist, proxy Class={}", this.singletonInstance, this.singletonInstance.getClass());
            return this.singletonInstance;
        }
        log.debug("proxy not exist yet, getting proxy for target={}", target);
        AopProxy aopProxy = this.aopProxyFactory.createAopProxy(target, advisor);
        this.singletonInstance = aopProxy.getProxy();
        log.debug("got proxy instance for target={}, singletonInstance={}, beanClass={}",
                target, singletonInstance, singletonInstance.getClass());
        /*AopProxy aopProxy = createAopProxy();
        this.singletonInstance = getProxy(aopProxy);*/
        return singletonInstance;
    }

    /*protected AopProxy createAopProxy() {
        return this.aopProxyFactory.createAopProxy(target);
    }

    protected Object getProxy(AopProxy aopProxy) {
        return aopProxy.getProxy();
    }*/

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
