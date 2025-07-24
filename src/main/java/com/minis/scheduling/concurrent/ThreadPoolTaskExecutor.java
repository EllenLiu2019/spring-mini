package com.minis.scheduling.concurrent;

import com.minis.scheduling.ListenableFuture;
import com.minis.scheduling.support.ListenableFutureTask;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ThreadPoolTaskExecutor {

    private final Object poolSizeMonitor = new Object();
    private int corePoolSize = 1;
    private int maxPoolSize = 8;
    private int keepAliveSeconds = 60;
    @Setter
    private int queueCapacity = Integer.MAX_VALUE;
    private RejectedExecutionHandler rejectedExecutionHandler = new ReentrantAbortPolicy();
    private ThreadPoolExecutor executor;

    public ThreadPoolTaskExecutor() {
        BlockingQueue<Runnable> queue = createQueue(queueCapacity);
        executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS,
                queue, Executors.defaultThreadFactory(), rejectedExecutionHandler);
    }

    private BlockingQueue<Runnable> createQueue(int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue<>(queueCapacity);
        } else {
            return new SynchronousQueue<>();
        }
    }

    public void setCorePoolSize(int corePoolSize) {
        synchronized (this.poolSizeMonitor) {
            this.corePoolSize = corePoolSize;
            if (this.executor != null) {
                this.executor.setCorePoolSize(corePoolSize);
            }
        }
    }

    public void setMaxPoolSize(int maxPoolSize) {
        synchronized (this.poolSizeMonitor) {
            this.maxPoolSize = maxPoolSize;
            if (this.executor != null) {
                this.executor.setMaximumPoolSize(maxPoolSize);
            }
        }
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        synchronized (this.poolSizeMonitor) {
            this.keepAliveSeconds = keepAliveSeconds;
            if (this.executor != null) {
                this.executor.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
            }
        }
    }

    public void execute(Runnable runnable) {
        this.executor.execute(runnable);
    }

    public Future<?> submit(Runnable task) {
        return this.executor.submit(task);
    }

    public <V> Future<V> submit(Callable<V> task) {
        return this.executor.submit(task);
    }

    public ListenableFuture<?> submitListenable(Runnable runnable) {
        ListenableFutureTask<?> future = new ListenableFutureTask<>(runnable, null);
        this.executor.execute(future);
        return future;
    }

    public <V> ListenableFuture<V> submitListenable(Callable<V> callable) {
        ListenableFutureTask<V> futureTask = new ListenableFutureTask<>(callable);
        this.executor.submit(futureTask);
        return futureTask;
    }

    public static class ReentrantAbortPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, final ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                try {
                    boolean execute = executor.getQueue().offer(r, 10, TimeUnit.SECONDS);
                    if (!execute) {
                        log.error("Task {} rejected from {} due to time elapses before queue space is available", r.toString(), executor);
                    }
                } catch (InterruptedException e) {
                    log.error("Task {} rejected from {} due to {}", r.toString(), executor, e.toString());
                }
            }
        }
    }
}
