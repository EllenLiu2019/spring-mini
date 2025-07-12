package com.minis.test.aop.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnotherRealAction implements IAction{
    @Override
    public void doAction() {
        log.info("really do another action");
    }

    @Override
    public void doSomething() {
        log.info("really do another thing");
    }
}
