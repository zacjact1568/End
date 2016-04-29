package com.zack.enderplan.event;

/** 有提醒到达后的事件 */
public class RemindedEvent {

    public String planCode;

    public RemindedEvent(String planCode) {
        this.planCode = planCode;
    }
}
