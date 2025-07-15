package com.minis.scheduling.support;

import com.minis.scheduling.FailureCallback;
import com.minis.scheduling.ListenableFuture;
import com.minis.scheduling.SuccessCallback;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Slf4j
public class ListenableFutureTask<T> extends FutureTask<T> implements ListenableFuture<T> {

    private final ListenableFutureCallbackRegistry<T> callbacks = new ListenableFutureCallbackRegistry<>();

    public ListenableFutureTask(Callable<T> callable) {
        super(callable);
    }

    public ListenableFutureTask(Runnable runnable, T result) {
        super(runnable, result);
    }

    @Override
    public void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback) {
        this.callbacks.addSuccessCallback(successCallback);
        this.callbacks.addFailureCallback(failureCallback);
    }


    /**
     * TODO: this will be invoked by:
     *  main-thread: ThreadPoolExecutor#Worker.thread#start() -> native ->
     *     thread-1: native -> thread#run -> ThreadPoolExecutor#worker#run() -> ThreadPoolExecutor#runWorker(Worker) ->
     *               Worker.firstTask#run()
     *     thread-2: native -> FutureTask#run() -> RunnableAdapter[callable=ListenableFutureTask].call() -> this.run()
     *                         -> this.run() -> super.run() -> FutureTask#run() -> set() -> finishCompletion() -> this.done() -> callback
     *
     */
    @Override
    public void run() {
        log.info("invoked by RunnableAdapter[callable=ListenableFutureTask]#call()");
        super.run();
        log.info("listener's callback is finished");
    }
    @Override
    /**
     * TODO: once all waiting threads are signalled and waken up,
     *  this method will be executed;
     *  for more info. see {@link FutureTask#finishCompletion()}
     */
    public void done() {
        log.info("task done. executing thread is waked up. listener continue working ");
        Throwable cause;
        try {
            T result = get();
            this.callbacks.success(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("thread interrupted, return directly");
        } catch (Throwable e) {
            cause = e.getCause();
            if (cause == null) {
                cause = e;
            }
            log.error("encounter throwable error, caused by:" + cause);
            this.callbacks.failure(cause);
        }
    }
}
