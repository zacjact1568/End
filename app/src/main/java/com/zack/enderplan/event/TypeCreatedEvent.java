package com.zack.enderplan.event;

/** 有新建类型的事件 */
public class TypeCreatedEvent extends BaseTypeEvent {

    public TypeCreatedEvent(String eventSource, String typeCode, int position) {
        super(eventSource, typeCode, position);
    }
}
