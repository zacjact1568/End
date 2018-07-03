package net.zackzhang.app.end.event

// 有计划详细信息的更改事件。注意：此类中的position是指事件被发送时的位置
class PlanDetailChangedEvent(
        eventSource: String,
        planCode: String,
        position: Int,
        // 改变的字段
        val changedField: Int
) : BasePlanEvent(eventSource, planCode, position) {

    companion object {

        val FIELD_CONTENT = 0
        val FIELD_TYPE_OF_PLAN = 1
        val FIELD_PLAN_STATUS = 2
        val FIELD_DEADLINE = 3
        val FIELD_STAR_STATUS = 4
        val FIELD_REMINDER_TIME = 5
    }
}
