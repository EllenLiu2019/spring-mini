package com.minis.app.service;

import com.minis.scheduling.ListenableFuture;

public interface IService {
    ListenableFuture<Boolean> greeting4Score();

    ListenableFuture<Integer> greetingException();
}
