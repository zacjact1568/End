package me.imzack.app.end.model.bean

data class FormattedPlan(
        var content: String,
        var isStarred: Boolean,
        var spinnerPos: Int,
        var deadline: CharSequence,
        var reminderTime: CharSequence,
        var isCompleted: Boolean
)
