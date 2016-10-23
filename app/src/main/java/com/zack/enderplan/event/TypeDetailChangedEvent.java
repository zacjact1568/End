package com.zack.enderplan.event;

/** 有类型详细信息更改的事件 */
public class TypeDetailChangedEvent extends BaseTypeEvent {

    public static final int FIELD_TYPE_NAME = 0;
    public static final int FIELD_TYPE_MARK_COLOR = 1;
    public static final int FIELD_TYPE_MARK_PATTERN = 2;

    /** 改变的字段 */
    private int changedField;

    public TypeDetailChangedEvent(String eventSource, String typeCode, int position, int changedField) {
        super(eventSource, typeCode, position);
        this.changedField = changedField;
    }

    public int getChangedField() {
        return changedField;
    }
}
