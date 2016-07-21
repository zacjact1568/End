package com.zack.enderplan.event;

/** 基本计划事件 */
public abstract class BaseTypeEvent {

    protected String typeCode;
    protected int position;

    protected BaseTypeEvent(String typeCode, int position) {
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
