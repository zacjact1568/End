package com.zack.enderplan.event;

/** 有新建计划的事件 */
public class PlanCreatedEvent extends BasePlanEvent {

    public PlanCreatedEvent(String eventSource, String planCode, int position) {
        super(eventSource, planCode, position);
    }
}
