package com.zack.enderplan.event;

/** 有计划详细信息的更改事件 */
public class PlanDetailChangedEvent {

    /** 在AllPlansList中的位置 */
    public int position;
    /** 状态改变的计划的编码 */
    public String planCode;
    /** 类型是否发生改变 */
    public boolean isTypeOfPlanChanged;
    /** 完成情况是否发生改变 */
    public boolean isPlanStatusChanged;
    /** 在singleTypeUcPlanList中的位置 */
    public int posInStUcPlanList;

    public PlanDetailChangedEvent(int position, String planCode, boolean isTypeOfPlanChanged,
                                  boolean isPlanStatusChanged, int posInStUcPlanList) {
        this.position = position;
        this.planCode = planCode;
        this.isTypeOfPlanChanged = isTypeOfPlanChanged;
        this.isPlanStatusChanged = isPlanStatusChanged;
        this.posInStUcPlanList = posInStUcPlanList;
    }
}
