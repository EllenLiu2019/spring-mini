package com.minis.scheduling.support;

import com.minis.scheduling.FailureCallback;
import com.minis.scheduling.SuccessCallback;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

@Slf4j
public class ListenableFutureCallbackRegistry<T> {
    private enum State {NEW, SUCCESS, FAILURE}

    private final Queue<SuccessCallback<? super T>> successCallbacks = new LinkedList<>();
    private final Queue<FailureCallback> failureCallbacks = new LinkedList<>();

    private State state = State.NEW;
    private Object result;

    private final Object mutex = new Object();

    public void addSuccessCallback(SuccessCallback<? super T> callback) {
        synchronized (this.mutex) {
            switch (this.state) {
                case NEW -> this.successCallbacks.add(callback);
                case SUCCESS -> notifySuccess(callback);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void notifySuccess(SuccessCallback<? super T> callback) {
        try {
            callback.onSuccess((T) this.result);
        }
        catch (Throwable ignored) {
        }
    }

    public void addFailureCallback(FailureCallback callback) {
        synchronized (this.mutex) {
            switch (this.state) {
                case NEW -> this.failureCallbacks.add(callback);
                case FAILURE -> notifyFailure(callback);
            }
        }
    }
    private void notifyFailure(FailureCallback callback) {
        try {
            callback.onFailure((Throwable) this.result);
        }
        catch (Throwable ignored) {
        }
    }


    public void success(T result) {
        synchronized (this.mutex) {
            this.state = State.SUCCESS;
            this.result = result;
            SuccessCallback<? super T> callback;
            while ((callback = this.successCallbacks.poll()) != null) {
                notifySuccess(callback);
            }
        }
    }

    public void failure(Throwable ex) {
        synchronized (this.mutex) {
            this.state = State.FAILURE;
            this.result = ex;
            FailureCallback callback;
            while((callback = this.failureCallbacks.poll()) != null) {
                notifyFailure(callback);
            }
        }
    }
}
