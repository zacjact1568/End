package net.zackzhang.app.end.event

abstract class BasePlanEvent(
        eventSource: String,
        val planCode: String,
        val position: Int
) : BaseEvent(eventSource)
