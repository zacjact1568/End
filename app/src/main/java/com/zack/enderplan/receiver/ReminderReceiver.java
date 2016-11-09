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

        String planCode = intent.getStringExtra("plan_code");

        DatabaseManager dManager = DatabaseManager.getInstance();
        DataManager dataManager = DataManager.getInstance();

        Plan plan = dManager.queryPlan(planCode);

        Intent reminderIntent = new Intent("com.zack.enderplan.ACTION_REMINDER");
        reminderIntent.putExtra("plan_detail", plan);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, reminderIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_check_box_white_24dp)
                .setContentTitle(context.getResources().getString(R.string.title_notification_content))
                .setContentText(plan.getContent())
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();
        nManager.notify(planCode, 0, notification);

        //数据库存储
        dManager.updateReminderTime(planCode, 0);

        if (dataManager.getDataStatus() == DataManager.STATUS_DATA_LOADED) {
            //此时数据已加载完成，可以通过DataManager访问到数据
            int position = dataManager.getPlanLocationInPlanList(planCode);
            dataManager.getPlan(position).setReminderTime(0);
            //通知界面更新（NOTE：如果app已退出，但进程还没被杀，仍然会发出通知）
            EventBus.getDefault().post(new PlanDetailChangedEvent(getClass().getSimpleName(), planCode, position, PlanDetailChangedEvent.FIELD_REMINDER_TIME));
        }
    }
}
