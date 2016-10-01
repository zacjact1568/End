package com.zack.enderplan.event;

/** 有提醒到达后的事件 */
public class RemindedEvent extends BasePlanEvent {

    public RemindedEvent(String eventSource, String planCode, int position) {
        super(eventSource, planCode, position);
    }
}
