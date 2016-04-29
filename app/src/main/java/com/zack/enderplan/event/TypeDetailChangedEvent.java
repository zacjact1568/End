package com.zack.enderplan.event;

/** 有类型详细信息更改的事件 */
public class TypeDetailChangedEvent {

    public String typeCode;
    /**
     * 在AllTypesList中的位置
     */
    public int position;

    public TypeDetailChangedEvent(String typeCode, int position) {
        this.typeCode = typeCode;
        this.position = position;
    }
}
