package com.minis.context.event;

import java.io.Serial;
import java.util.EventObject;

public class ApplicationEvent extends EventObject {
    @Serial
    private static final long serialVersionUID = 1L;
    protected String msg = null;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ApplicationEvent(Object source) {
        super(source);
        this.msg = source.toString();
    }
}
