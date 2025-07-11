package com.minis.context;

public class ContextRefreshedEvent extends ApplicationContextEvent {
    private static final long serialVersionUID = 1L;
    public ContextRefreshedEvent(Object source) {
        super(source);
    }
    public String toString() {
        return this.msg;
    }
}
