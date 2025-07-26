package com.minis.app.service;

import com.minis.scheduling.ListenableFuture;
import com.minis.scheduling.annotation.Async;
import com.minis.scheduling.annotation.AsyncResult;
import com.minis.stereotype.Component;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class IServiceImpl implements IService {

    @Override
    @Async
    public ListenableFuture<Boolean> greeting4Score() {
        log.info("I'm a new Thread '{}',I'm greeting to 100 persons!", Thread.currentThread().getName());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ListenableFuture<Boolean> result = new AsyncResult<>(true);

        return result;
    }

    @Override
    @Async
    public ListenableFuture<Integer> greetingException() {
        int i = 1 / 0;
        return new AsyncResult<>(i);
    }

}
