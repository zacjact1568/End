package com.zack.enderplan.receiver;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.zack.enderplan.R;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;
import com.zack.enderplan.util.SystemUtil;
import com.zack.enderplan.util.ViewUtil;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.database.DatabaseManager;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.view.widget.CircleColorView;

import org.greenrobot.eventbus.EventBus;

public class ReminderReceiver extends BaseReceiver {

    public static PendingIntent getPendingIntentForSend(Context context, String planCode) {
        return PendingIntent.getBroadcast(
                context,
                //requestCode参数也可区分不同的PendingIntent，但是planCode是String类型的，不是int类型
                0,
                new Intent(context, ReminderReceiver.class)
                        //相当于只有下面这条语句才能区分不同的PendingIntent，也就是planCode才能区分，如果code相同，那么extra也会被无视
                        .setAction(String.format(Constant.ACTION_REMINDER, planCode))
                        .setPackage(context.getPackageName())
                        .putExtra(Constant.PLAN_CODE, planCode),
                //FLAG_UPDATE_CURRENT真的很有用，修改过plan后再set，通知内容也会被替换成新的plan.content了，也就不需要cancel后再set了
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //直接从数据库获取plan（若通过DataManager获取，可能需要异步加载，而在receiver中不要做异步操作）
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Plan plan = databaseManager.queryPlan(intent.getStringExtra(Constant.PLAN_CODE));
        Type type = databaseManager.queryType(plan.getTypeCode());
        databaseManager.updateReminderTime(plan.getPlanCode(), Constant.UNDEFINED_TIME);

        DataManager dataManager = DataManager.getInstance();
        int position = -1;
        if (dataManager.isDataLoaded()) {
            //若DataManager中的数据已加载完成
            position = dataManager.getPlanLocationInPlanList(plan.getPlanCode());
            dataManager.getPlan(position).setReminderTime(Constant.UNDEFINED_TIME);
            //发出事件（NOTE：如果app已退出，但进程还没被杀，仍然会发出）
            EventBus.getDefault().post(new PlanDetailChangedEvent(
                    getReceiverName(),
                    plan.getPlanCode(),
                    position,
                    PlanDetailChangedEvent.FIELD_REMINDER_TIME
            ));
        }

        CircleColorView typeMarkIcon = new CircleColorView(context);
        typeMarkIcon.setFillColor(Color.parseColor(type.getTypeMarkColor()));
        typeMarkIcon.setInnerText(StringUtil.getFirstChar(type.getTypeName()));
        typeMarkIcon.setInnerIcon(ResourceUtil.getDrawable(type.getTypeMarkPattern()));

        SystemUtil.showNotification(
                plan.getPlanCode(),
                ResourceUtil.getString(R.string.title_notification_reminder),
                plan.getContent(),
                ViewUtil.convertViewToBitmap(typeMarkIcon, Constant.NOTIFICATION_LARGE_ICON_SIZE),
                ReminderNotificationActionReceiver.getPendingIntentForSend(context, plan.getPlanCode(), position, ReminderNotificationActionReceiver.NOTIFICATION_ACTION_CONTENT),
                SystemUtil.getNotificationAction(
                        R.drawable.ic_check_black_24dp,
                        R.string.button_complete,
                        ReminderNotificationActionReceiver.getPendingIntentForSend(context, plan.getPlanCode(), position, ReminderNotificationActionReceiver.NOTIFICATION_ACTION_COMPLETE)
                ),
                SystemUtil.getNotificationAction(
                        R.drawable.ic_snooze_black_24dp,
                        R.string.button_delay,
                        ReminderNotificationActionReceiver.getPendingIntentForSend(context, plan.getPlanCode(), position, ReminderNotificationActionReceiver.NOTIFICATION_ACTION_DELAY)
                )
        );
    }
}
