package com.minis.test.aop.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RealAction implements IAction {
    @Override
    public void doAction() {
        log.info("really do action");
    }
}
