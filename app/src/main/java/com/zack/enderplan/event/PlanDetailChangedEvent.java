package com.zack.enderplan.event;

/** 有计划详细信息的更改事件 */
public class PlanDetailChangedEvent extends BasePlanEvent {

    /** 类型是否发生改变 */
    private boolean isTypeOfPlanChanged;
    /** 完成情况是否发生改变 */
    private boolean isPlanStatusChanged;
    /** 在singleTypeUcPlanList中的位置 */
    private int posInStUcPlanList;

    public PlanDetailChangedEvent(String planCode, int position, boolean isTypeOfPlanChanged,
                                  boolean isPlanStatusChanged, int posInStUcPlanList) {
        super(planCode, position);
        this.isTypeOfPlanChanged = isTypeOfPlanChanged;
        this.isPlanStatusChanged = isPlanStatusChanged;
        this.posInStUcPlanList = posInStUcPlanList;
    }

    public boolean isTypeOfPlanChanged() {
        return isTypeOfPlanChanged;
    }

    public boolean isPlanStatusChanged() {
        return isPlanStatusChanged;
    }

    public int getPosInStUcPlanList() {
        return posInStUcPlanList;
    }
}
