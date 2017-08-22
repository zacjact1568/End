package me.imzack.app.end.event

abstract class BaseTypeEvent(
        eventSource: String,
        val typeCode: String,
        val position: Int
) : BaseEvent(eventSource)
