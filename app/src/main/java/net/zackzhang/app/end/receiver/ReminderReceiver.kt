package net.zackzhang.app.end.receiver

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.event.PlanDetailChangedEvent
import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.model.bean.Plan
import net.zackzhang.app.end.model.bean.Type
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.StringUtil
import net.zackzhang.app.end.util.SystemUtil
import net.zackzhang.app.end.util.ViewUtil
import me.imzack.lib.circlecolorview.CircleColorView

class ReminderReceiver : BaseReceiver() {

    companion object {

        fun getPendingIntentForSend(context: Context, planCode: String): PendingIntent {
            return PendingIntent.getBroadcast(
                    context,
                    //requestCode参数也可区分不同的PendingIntent，但是planCode是String类型的，不是int类型
                    0,
                    Intent(context, ReminderReceiver::class.java)
                            //相当于只有下面这条语句才能区分不同的PendingIntent，也就是planCode才能区分，如果code相同，那么extra也会被无视
                            .setAction(String.format(Constant.ACTION_REMINDER, planCode))
                            .setPackage(context.packageName)
                            .putExtra(Constant.CODE, planCode),
                    //FLAG_UPDATE_CURRENT真的很有用，修改过plan后再set，通知内容也会被替换成新的plan.content了，也就不需要cancel后再set了
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {

        val planCode = intent.getStringExtra(Constant.CODE)

        val position: Int
        val plan: Plan
        val type: Type

        if (DataManager.isDataLoaded) {
            //若已加载数据，从DataManager获取plan和type
            position = DataManager.getPlanLocationInPlanList(planCode)
            plan = DataManager.getPlan(position)
            type = DataManager.getType(DataManager.getTypeLocationInTypeList(plan.typeCode))
            //通知清空提醒时间
            DataManager.clearPastReminderTime(position)
            //发出事件（NOTE：若在退出app时调用unloadData，则仅app在前台才发出，因为app退出前台后isDataLoaded为false）
            App.eventBus.post(PlanDetailChangedEvent(
                    receiverName,
                    plan.code,
                    position,
                    PlanDetailChangedEvent.FIELD_REMINDER_TIME
            ))
        } else {
            //若还未加载数据，直接从数据库获取plan和type（DataManager加载数据为异步加载，在receiver中不要做异步操作）
            position = -1
            plan = DataManager.getPlanFromDatabase(planCode)
            type = DataManager.getTypeFromDatabase(plan.typeCode)
            //通知清空提醒时间
            DataManager.clearPastReminderTimeInDatabase(planCode)
        }

        //以下为构建通知的操作

        val typeMarkIcon = CircleColorView(context)
        typeMarkIcon.setFillColor(Color.parseColor(type.markColor))
        typeMarkIcon.setInnerText(StringUtil.getFirstChar(type.name))
        typeMarkIcon.setInnerIcon(if (type.hasMarkPattern) ResourceUtil.getDrawable(type.markPattern!!) else null)

        SystemUtil.showNotification(
                Constant.NOTIFICATION_CHANNEL_ID_REMINDER,
                plan.code,
                ResourceUtil.getString(R.string.title_notification_reminder),
                plan.content,
                ViewUtil.convertViewToBitmap(typeMarkIcon, Constant.NOTIFICATION_LARGE_ICON_SIZE),
                ReminderNotificationActionReceiver.getPendingIntentForSend(context, plan.code, position, ReminderNotificationActionReceiver.NOTI_ACTION_CONTENT),
                SystemUtil.getNotificationAction(
                        R.drawable.ic_check_black_24dp,
                        R.string.button_complete,
                        ReminderNotificationActionReceiver.getPendingIntentForSend(context, plan.code, position, ReminderNotificationActionReceiver.NOTI_ACTION_COMPLETE)
                ),
                SystemUtil.getNotificationAction(
                        R.drawable.ic_snooze_black_24dp,
                        R.string.button_delay,
                        ReminderNotificationActionReceiver.getPendingIntentForSend(context, plan.code, position, ReminderNotificationActionReceiver.NOTI_ACTION_DELAY)
                )
        )
    }
}
