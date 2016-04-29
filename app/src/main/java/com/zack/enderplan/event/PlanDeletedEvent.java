package com.zack.enderplan.event;

/** 有计划被删除的事件 */
public class PlanDeletedEvent {

    /** 在AllPlansList中的位置 */
    public int position;
    /** 被删除计划的编码 */
    public String planCode;
    /** 是否是一个已完成的计划 */
    public boolean isCompleted;

    public PlanDeletedEvent(int position, String planCode, boolean isCompleted) {
        this.position = position;
        this.planCode = planCode;
        this.isCompleted = isCompleted;
    }
}
