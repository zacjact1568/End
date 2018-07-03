package net.zackzhang.app.end.event

abstract class BaseTypeEvent(
        eventSource: String,
        val typeCode: String,
        val position: Int
) : BaseEvent(eventSource)
