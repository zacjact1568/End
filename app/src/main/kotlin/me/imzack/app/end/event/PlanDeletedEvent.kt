package me.imzack.app.end.event

import me.imzack.app.end.model.bean.Plan

class PlanDeletedEvent(
        eventSource: String,
        planCode: String,
        position: Int,
        // 已删除的计划，给接收此event的组件做后续处理使用。因为计划已删除，无法再通过position从DataManager中取得plan
        val deletedPlan: Plan
) : BasePlanEvent(eventSource, planCode, position)
