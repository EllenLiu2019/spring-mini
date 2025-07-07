package com.minis.context;

import java.util.EventListener;

public class ApplicationListener<E extends ApplicationEvent> implements EventListener {
    public void onApplicationEvent(E event) {
        System.out.println("ApplicationListener: " + event);
    }
}