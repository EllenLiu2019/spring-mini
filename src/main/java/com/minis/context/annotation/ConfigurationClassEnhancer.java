package com.minis.context.annotation;

import com.minis.beans.BeansException;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.*;

import java.lang.reflect.Method;

@Slf4j
class ConfigurationClassEnhancer {

    static final Callback[] CALLBACKS = new Callback[]{
            new BeanMethodInterceptor(),
            NoOp.INSTANCE
    };

    private static final ConditionalCallbackFilter CALLBACK_FILTER = new ConditionalCallbackFilter(CALLBACKS);

    public Class<?> enhance(Class<?> configClass) {
        if (EnhancedConfiguration.class.isAssignableFrom(configClass)) {
            log.debug(String.format("Ignoring request to enhance %s as it has " +
                            "already been enhanced. This usually indicates that more than one " +
                            "ConfigurationClassPostProcessor has been registered (for example, via " +
                            "<context:annotation-config>). This is harmless, but you may " +
                            "want check your configuration and remove one CCPP if possible",
                    configClass.getName()));
            return configClass;
        }

        try {

            Enhancer enhancer = newEnhancer(configClass);
            Class<?> enhancedClass = createClass(enhancer);

            log.trace(String.format("Successfully enhanced %s; enhanced class name is: %s",
                    configClass.getName(), enhancedClass.getName()));

            return enhancedClass;
        } catch (Exception ex) {
            throw new BeansException("Could not enhance configuration class [" + configClass.getName() +
                    "]. Consider declaring @Configuration(proxyBeanMethods=false) without inter-bean references " +
                    "between @Bean methods on the configuration class, avoiding the need for CGLIB enhancement.");
        }
    }

    // Creates a new CGLIB Enhancer instance.
    private Enhancer newEnhancer(Class<?> configSuperClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(configSuperClass);
        enhancer.setInterfaces(new Class<?>[]{EnhancedConfiguration.class});
        enhancer.setUseFactory(false);
        enhancer.setCallbackFilter(CALLBACK_FILTER);
        enhancer.setCallbackTypes(CALLBACK_FILTER.getCallbackTypes());
        return enhancer;
    }

    private Class<?> createClass(Enhancer enhancer) {
        return enhancer.createClass();
    }

    public interface EnhancedConfiguration {
    }

    private interface ConditionalCallback extends Callback {
        boolean isMatch(Method candidateMethod);
    }

    private static class ConditionalCallbackFilter implements CallbackFilter {

        private final Callback[] callbacks;

        private final Class<?>[] callbackTypes;


        public ConditionalCallbackFilter(Callback[] callbacks) {
            this.callbacks = callbacks;
            this.callbackTypes = new Class<?>[callbacks.length];
            for (int i = 0; i < callbacks.length; i++) {
                callbackTypes[i] = callbacks[i].getClass();
            }
        }

        @Override
        public int accept(Method method) {
            for (int i = 0; i < this.callbacks.length; i++) {
                Callback callback = this.callbacks[i];
                if (!(callback instanceof ConditionalCallback conditional) || conditional.isMatch(method)) {
                    return i;
                }
            }
            throw new IllegalStateException("No callback available for method " + method.getName());
        }

        public Class<?>[] getCallbackTypes() {
            return this.callbackTypes;
        }
    }

    private static class BeanMethodInterceptor implements MethodInterceptor, ConditionalCallback {

        @Override
        public boolean isMatch(Method candidateMethod) {
            return (candidateMethod.getDeclaringClass() != Object.class &&
                    candidateMethod.isAnnotationPresent(Bean.class));
        }

        @Override
        public Object intercept(Object enhancedConfigInstance, Method beanMethod, Object[] beanMethodArgs,
                                MethodProxy cglibMethodProxy) throws Throwable {
            // The factory is calling the bean method in order to instantiate and register the bean
            // (i.e. via a getBean() call) -> invoke the super implementation of the method to actually
            // create the bean instance.
            return cglibMethodProxy.invokeSuper(enhancedConfigInstance, beanMethodArgs);
        }

    }
}
