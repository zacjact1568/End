package com.zack.enderplan.event;

/** 有类型详细信息更改的事件 */
public class TypeDetailChangedEvent extends BaseTypeEvent {

    public TypeDetailChangedEvent(String eventSource, String typeCode, int position) {
        super(eventSource, typeCode, position);
    }
}
