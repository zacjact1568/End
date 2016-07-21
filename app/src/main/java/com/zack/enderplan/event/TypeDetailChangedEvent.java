package com.zack.enderplan.event;

/** 有类型详细信息更改的事件 */
public class TypeDetailChangedEvent extends BaseTypeEvent {

    public TypeDetailChangedEvent(String typeCode, int position) {
        super(typeCode, position);
    }
}
