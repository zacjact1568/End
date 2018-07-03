package net.zackzhang.app.end.event

class TypeDetailChangedEvent(
        eventSource: String,
        typeCode: String,
        position: Int,
        // 改变的字段
        val changedField: Int
) : BaseTypeEvent(eventSource, typeCode, position) {

    companion object {

        val FIELD_TYPE_NAME = 0
        val FIELD_TYPE_MARK_COLOR = 1
        val FIELD_TYPE_MARK_PATTERN = 2
    }
}
