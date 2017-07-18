package me.imzack.app.ender.event;

/** 基本计划事件 */
public abstract class BasePlanEvent extends BaseEvent {

    protected String planCode;
    protected int position;

    protected BasePlanEvent(String eventSource, String planCode, int position) {
        super(eventSource);
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
