package me.imzack.app.end.event;

/** 基本事件 */
public abstract class BaseEvent {

    /** 事件源（一般是类名） */
    protected String eventSource;

    protected BaseEvent(String eventSource) {
        this.eventSource = eventSource;
    }

    public String getEventSource() {
        return eventSource;
    }
}
