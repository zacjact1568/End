package com.zack.enderplan.event;

public abstract class BaseEvent {

    /** 发出这个事件的类名，防止重复更新（类自己通知自己） */
    public String classPostFrom;

    protected BaseEvent(String classPostFrom) {
        this.classPostFrom = classPostFrom;
    }
}
