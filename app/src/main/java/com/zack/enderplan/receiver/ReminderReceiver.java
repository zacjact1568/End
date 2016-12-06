package com.zack.enderplan.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zack.enderplan.R;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.database.DatabaseManager;
import com.zack.enderplan.model.DataManager;

import org.greenrobot.eventbus.EventBus;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //直接从数据库获取plan（若通过DataManager获取，可能需要异步加载，而在receiver中不要做异步操作）
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Plan plan = databaseManager.queryPlan(intent.getStringExtra("plan_code"));
        databaseManager.updateReminderTime(plan.getPlanCode(), 0);

        DataManager dataManager = DataManager.getInstance();
        int position = -1;
        if (dataManager.isDataLoaded()) {
            //若DataManager中的数据已加载完成
            position = dataManager.getPlanLocationInPlanList(plan.getPlanCode());
            dataManager.getPlan(position).setReminderTime(0);
            //发出事件（NOTE：如果app已退出，但进程还没被杀，仍然会发出）
            EventBus.getDefault().post(new PlanDetailChangedEvent(
                    getClass().getSimpleName(),
                    plan.getPlanCode(),
                    position,
                    PlanDetailChangedEvent.FIELD_REMINDER_TIME
            ));
        }

        showNotification(context, plan.getPlanCode(), position, plan.getContent());
    }

    private void showNotification(Context context, String planCode, int position, String content) {
        Intent intent = new Intent("com.zack.enderplan.ACTION_REMINDER");
        //plan_code总是有效的，position可能无效（-1），但优先考虑position
        intent.putExtra("plan_code", planCode);
        intent.putExtra("position", position);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_check_box_white_24dp)
                .setContentTitle(context.getResources().getString(R.string.title_notification_content))
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(planCode, 0, notification);
    }
}
