package me.imzack.app.end.event;

/** 有计划详细信息的更改事件<br>注意：此类中的position是指事件被发送时的位置 */
public class PlanDetailChangedEvent extends BasePlanEvent {

    public static final int FIELD_CONTENT = 0;
    public static final int FIELD_TYPE_OF_PLAN = 1;
    public static final int FIELD_PLAN_STATUS = 2;
    public static final int FIELD_DEADLINE = 3;
    public static final int FIELD_STAR_STATUS = 4;
    public static final int FIELD_REMINDER_TIME = 5;

    /** 改变的字段 */
    private int mChangedField;

    public PlanDetailChangedEvent(String eventSource, String planCode, int position, int changedField) {
        super(eventSource, planCode, position);
        mChangedField = changedField;
    }

    public int getChangedField() {
        return mChangedField;
    }
}
