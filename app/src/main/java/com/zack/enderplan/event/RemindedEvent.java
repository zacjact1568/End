package com.zack.enderplan.event;

/** 有提醒到达后的事件 */
public class RemindedEvent {

    public int position;
    public String planCode;

    public RemindedEvent(int position, String planCode) {
        this.position = position;
        this.planCode = planCode;
    }
}
