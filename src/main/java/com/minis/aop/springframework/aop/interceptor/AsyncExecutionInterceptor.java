package com.minis.aop.springframework.aop.interceptor;

import com.minis.aop.aopalliance.intercept.MethodInterceptor;
import com.minis.aop.aopalliance.intercept.MethodInvocation;
import com.minis.scheduling.ListenableFuture;
import com.minis.scheduling.concurrent.ThreadPoolTaskExecutor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Slf4j
public class AsyncExecutionInterceptor implements MethodInterceptor {

    @Setter
    private ThreadPoolTaskExecutor executor;

    @Override
    // TODO: 只是实现了在此方法中的异步，该方法返回还是依赖于异步执行何时获得结果
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        log.info("interceptor submit task into listenable executor");
        Class<?> returnType = invocation.getMethod().getReturnType();
        if (ListenableFuture.class.isAssignableFrom(returnType)) {
            return executor.submitListenable(getTask(invocation));
        } else if (Future.class.isAssignableFrom(returnType)) {
            return executor.submit(getTask(invocation));
        } else {
            return invocation.proceed();
        }
    }

    private Callable<Object> getTask(MethodInvocation invocation) {
        return () -> {
            try {
                Object ret = invocation.proceed();
                if (ret instanceof Future<?> future) {
                    Object result = future.get();
                    log.info("thread {} wake up with result = {}", Thread.currentThread().getName(), result);
                    return result;
                }
            } catch (Throwable ex) {
                log.error("invocation proceed failed, caused by: {}", ex, ex);
                throw new RuntimeException(ex);
            }
            return null;
        };
    }
}
