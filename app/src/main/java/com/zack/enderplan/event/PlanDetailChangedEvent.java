package com.zack.enderplan.event;

/** 有计划详细信息（除完成情况外）的更改事件 */
public class PlanDetailChangedEvent {

    /** 在AllPlansList中的位置 */
    public int position;

    public PlanDetailChangedEvent(int position) {
        this.position = position;
    }
}
