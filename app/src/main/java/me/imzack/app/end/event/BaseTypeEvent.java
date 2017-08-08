package me.imzack.app.end.event;

/** 基本计划事件 */
public abstract class BaseTypeEvent extends BaseEvent {

    protected String typeCode;
    protected int position;

    protected BaseTypeEvent(String eventSource, String typeCode, int position) {
        super(eventSource);
        this.typeCode = typeCode;
        this.position = position;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public int getPosition() {
        return position;
    }
}
