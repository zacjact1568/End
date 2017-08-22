package me.imzack.app.end.view.contract

interface ReminderViewContract : BaseViewContract {

    fun showInitialView(content: String, hasDeadline: Boolean, deadline: String?)

    fun playEnterAnimation()

    fun showToast(msg: String)

    fun enterPlanDetail(position: Int)
}
