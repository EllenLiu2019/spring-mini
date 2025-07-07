package com.minis.context;

public class ContextRefreshEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    public ContextRefreshEvent(Object source) {
        super(source);
    }
    public String toString() {
        return this.msg;
    }
}
