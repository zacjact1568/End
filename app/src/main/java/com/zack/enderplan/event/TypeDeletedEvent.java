package com.zack.enderplan.event;

/** 事件：类型被删除 */
public class TypeDeletedEvent extends BaseTypeEvent {

    public TypeDeletedEvent(String typeCode, int position) {
        super(typeCode, position);
    }
}
