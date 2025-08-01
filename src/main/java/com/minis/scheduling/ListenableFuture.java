package com.minis.scheduling;

import java.util.concurrent.Future;

public interface ListenableFuture<T> extends Future<T> {
    void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback);
}
