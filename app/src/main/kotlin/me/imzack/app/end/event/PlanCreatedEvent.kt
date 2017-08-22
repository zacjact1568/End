package me.imzack.app.end.event

class PlanCreatedEvent(eventSource: String, planCode: String, position: Int) : BasePlanEvent(eventSource, planCode, position)
