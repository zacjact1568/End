package me.imzack.app.end.eventbus.event;

import me.imzack.app.end.model.bean.Type;

/** 有类型被删除的事件 */
public class TypeDeletedEvent extends BaseTypeEvent {

    /**
     * 已删除的类型<br>
     * 给接收此event的组件做后续处理使用<br>
     * 因为类型已删除，无法再通过position从DataManager中取得type
     */
    private Type deletedType;

    public TypeDeletedEvent(String eventSource, String typeCode, int position, Type deletedType) {
        super(eventSource, typeCode, position);
        this.deletedType = deletedType;
    }

    public Type getDeletedType() {
        return deletedType;
    }
}
