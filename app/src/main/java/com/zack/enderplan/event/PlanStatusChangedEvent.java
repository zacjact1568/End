package com.zack.enderplan.event;

/** 计划完成情况更改的事件 */
public class PlanStatusChangedEvent {

    /** 在AllPlansList中的位置 */
    public int position;
    /** 状态改变的计划的编码 */
    public String planCode;
    /** 在singleTypeUcPlanList中的位置 */
    public int posInStUcPlanList;

    public PlanStatusChangedEvent(int position, int posInStUcPlanList) {
        this.position = position;
        this.posInStUcPlanList = posInStUcPlanList;
        planCode = null;
    }

    public PlanStatusChangedEvent(int position, String planCode) {
        this.position = position;
        this.planCode = planCode;
        posInStUcPlanList = -1;
    }
}
