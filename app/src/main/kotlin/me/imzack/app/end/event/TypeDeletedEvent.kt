package me.imzack.app.end.event

import me.imzack.app.end.model.bean.Type

class TypeDeletedEvent(
        eventSource: String,
        typeCode: String,
        position: Int,
        // 已删除的类型，给接收此event的组件做后续处理使用。因为类型已删除，无法再通过position从DataManager中取得type
        val deletedType: Type
) : BaseTypeEvent(eventSource, typeCode, position)
