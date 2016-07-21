package com.zack.enderplan.event;

/** 有新建类型的事件 */
public class TypeCreatedEvent extends BaseTypeEvent {

    public TypeCreatedEvent(String typeCode, int position) {
        super(typeCode, position);
    }
}
