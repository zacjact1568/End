package me.imzack.app.end.receiver

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.event.PlanDetailChangedEvent
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.SystemUtil
import me.imzack.app.end.view.activity.ReminderActivity
import java.util.*

class ReminderNotificationActionReceiver : BaseReceiver() {

    companion object {

        val NOTI_ACTION_CONTENT = 0
        val NOTI_ACTION_COMPLETE = 1
        val NOTI_ACTION_DELAY = 2

        fun getPendingIntentForSend(context: Context, planCode: String, planListPosition: Int, notificationAction: Int): PendingIntent {
            return PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(context, ReminderNotificationActionReceiver::class.java)
                            .setAction(String.format(Constant.ACTION_REMINDER_NOTIFICATION_ACTION, planCode, notificationAction))
                            .setPackage(context.packageName)
                            //code总是有效的，position可能无效（-1），但优先考虑position
                            .putExtra(Constant.CODE, planCode)
                            .putExtra(Constant.PLAN_LIST_POSITION, planListPosition)
                            .putExtra(Constant.NOTIFICATION_ACTION, notificationAction),
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {

        if (!DataManager.isDataLoaded) {
            //说明DataManager中的数据到点击通知为止还未加载完
            SystemUtil.showToast(R.string.toast_data_loading_unfinished)
            return
        }

        val eventBus = App.eventBus

        var planListPos = intent.getIntExtra(Constant.PLAN_LIST_POSITION, -1)

        if (planListPos == -1) {
            //说明DataManager中的数据在ReminderReceiver的时候还未加载完，但是现在已经加载完了（显示通知到点击通知之间），更新mPlanListPosition
            planListPos = DataManager.getPlanLocationInPlanList(intent.getStringExtra(Constant.CODE))
        }

        val plan = DataManager.getPlan(planListPos)

        SystemUtil.cancelNotification(plan.code)

        when (intent.getIntExtra(Constant.NOTIFICATION_ACTION, -1)) {
            //点击通知内容
            NOTI_ACTION_CONTENT -> ReminderActivity.start(context, planListPos)
            //点击完成按钮
            NOTI_ACTION_COMPLETE -> {
                //不需要检测是否有reminder，因为这里一定是没有reminder的
                DataManager.notifyPlanStatusChanged(planListPos)
                planListPos = if (plan.isCompleted) DataManager.ucPlanCount else 0
                eventBus.post(PlanDetailChangedEvent(
                        receiverName,
                        plan.code,
                        planListPos,
                        PlanDetailChangedEvent.FIELD_PLAN_STATUS
                ))
                SystemUtil.showToast(R.string.toast_plan_completed)
            }
            //点击延迟按钮
            NOTI_ACTION_DELAY -> {
                val calendar = Calendar.getInstance()
                //TODO 延迟的时间可自定义
                calendar.add(Calendar.MINUTE, 10)
                DataManager.notifyReminderTimeChanged(planListPos, calendar.timeInMillis)
                eventBus.post(PlanDetailChangedEvent(
                        receiverName,
                        plan.code,
                        planListPos,
                        PlanDetailChangedEvent.FIELD_REMINDER_TIME
                ))
                SystemUtil.showToast(String.format(ResourceUtil.getString(R.string.toast_reminder_delayed_format), ResourceUtil.getString(R.string.toast_delay_10_minutes)))
            }
        }
    }
}
