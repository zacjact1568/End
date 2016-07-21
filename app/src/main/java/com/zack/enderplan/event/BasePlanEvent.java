package com.zack.enderplan.event;

/** 基本计划事件 */
public abstract class BasePlanEvent {

    protected String planCode;
    protected int position;

    protected BasePlanEvent(String planCode, int position) {
        this.planCode = planCode;
        this.position = position;
    }

    public String getPlanCode() {
        return planCode;
    }

    public int getPosition() {
        return position;
    }
}
