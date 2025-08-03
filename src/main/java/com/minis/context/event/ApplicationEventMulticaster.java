package com.minis.context.event;

public interface ApplicationEventMulticaster {

    void multicastEvent(ApplicationEvent event);
}
