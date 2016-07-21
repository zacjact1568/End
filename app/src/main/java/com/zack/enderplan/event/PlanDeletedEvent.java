package com.zack.enderplan.event;

/** 有计划被删除的事件 */
public class PlanDeletedEvent extends BasePlanEvent {

    /** 是否是一个已完成的计划 */
    private boolean isCompleted;

    public PlanDeletedEvent(String planCode, int position, boolean isCompleted) {
        super(planCode, position);
        this.isCompleted = isCompleted;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}
