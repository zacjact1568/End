package me.imzack.app.end.presenter

import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.event.PlanDetailChangedEvent
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.TimeUtil
import me.imzack.app.end.view.contract.ReminderViewContract
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject

class ReminderPresenter @Inject constructor(
        private var mReminderViewContract: ReminderViewContract?,
        private var mPlanListPosition: Int,
        private val mEventBus: EventBus
) : BasePresenter() {

    private val mPlan = DataManager.getPlan(mPlanListPosition)
    private var mReminderCoordinateY = 0

    override fun attach() {
        mReminderViewContract!!.showInitialView(
                mPlan.content,
                mPlan.hasDeadline,
                if (mPlan.hasDeadline) TimeUtil.formatDateTime(mPlan.deadline) else null
        )
    }

    override fun detach() {
        mReminderViewContract = null
    }

    fun notifyPreDrawingReminder(reminderCoordinateY: Int) {
        mReminderCoordinateY = reminderCoordinateY
        mReminderViewContract!!.playEnterAnimation()
    }

    fun notifyTouchFinished(coordinateY: Float) {
        if (coordinateY < mReminderCoordinateY) {
            mReminderViewContract!!.exit()
        }
    }

    fun notifyDelayingReminder(delay: String) {
        val calendar = Calendar.getInstance()
        val delayDscpt = when (delay) {
            Constant.ONE_HOUR -> {
                calendar.add(Calendar.HOUR, 1)
                ResourceUtil.getString(R.string.toast_delay_1_hour)
            }
            Constant.TOMORROW -> {
                calendar.add(Calendar.DATE, 1)
                ResourceUtil.getString(R.string.toast_delay_tomorrow)
            }
            else -> throw IllegalArgumentException("The argument delayTime cannot be " + delay)
        }
        // 不能合并，否则timeInMillis取的是更改之前的值
        updateReminderTime(calendar.timeInMillis, String.format(ResourceUtil.getString(R.string.toast_reminder_delayed_format), delayDscpt))
    }

    fun notifyUpdatingReminderTime(reminderTime: Long) {
        if (reminderTime == 0L) return
        if (TimeUtil.isValidTime(reminderTime)) {
            updateReminderTime(
                    reminderTime,
                    String.format(ResourceUtil.getString(R.string.toast_reminder_delayed_format), String.format(ResourceUtil.getString(R.string.toast_delay_more), TimeUtil.formatDateTime(reminderTime)))
            )
        } else {
            mReminderViewContract!!.showToast(R.string.toast_past_reminder_time)
        }
    }

    private fun updateReminderTime(reminderTime: Long, toastMsg: String) {
        DataManager.notifyReminderTimeChanged(mPlanListPosition, reminderTime)
        mEventBus.post(PlanDetailChangedEvent(
                presenterName,
                mPlan.code,
                mPlanListPosition,
                PlanDetailChangedEvent.FIELD_REMINDER_TIME
        ))
        mReminderViewContract!!.showToast(toastMsg)
        mReminderViewContract!!.exit()
    }

    fun notifyPlanCompleted() {
        //不需要检测是否有reminder，因为这里一定是没有reminder的
        DataManager.notifyPlanStatusChanged(mPlanListPosition)
        mPlanListPosition = if (mPlan.isCompleted) DataManager.ucPlanCount else 0
        mEventBus.post(PlanDetailChangedEvent(
                presenterName,
                mPlan.code,
                mPlanListPosition,
                PlanDetailChangedEvent.FIELD_PLAN_STATUS
        ))
        mReminderViewContract!!.showToast(R.string.toast_plan_completed)
        mReminderViewContract!!.exit()
    }

    fun notifyEnteringPlanDetail() {
        mReminderViewContract!!.enterPlanDetail(mPlanListPosition)
        mReminderViewContract!!.exit()
    }
}
