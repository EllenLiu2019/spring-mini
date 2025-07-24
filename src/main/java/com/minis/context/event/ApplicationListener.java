package com.minis.context.event;

import com.minis.context.event.ApplicationEvent;

import java.util.EventListener;

public interface ApplicationListener <E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E event);
}
