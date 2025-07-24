package com.minis.context.event;

import com.minis.context.event.ApplicationContextEvent;

public class ContextRefreshedEvent extends ApplicationContextEvent {
    private static final long serialVersionUID = 1L;
    public ContextRefreshedEvent(Object source) {
        super(source);
    }
    public String toString() {
        return this.msg;
    }
}
