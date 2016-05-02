package com.zack.enderplan.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.event.RemindedEvent;
import com.zack.enderplan.manager.DataManager;

import org.greenrobot.eventbus.EventBus;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String planCode = intent.getStringExtra("plan_code");

        EnderPlanDB enderplanDB = EnderPlanDB.getInstance();
        DataManager dataManager = DataManager.getInstance();

        Plan plan = enderplanDB.queryPlan(planCode);

        Intent reminderIntent = new Intent("com.zack.enderplan.ACTION_REMINDER");
        reminderIntent.putExtra("plan_detail", plan);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, reminderIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.enderplan_icon)
                .setTicker(context.getResources().getString(R.string.notification_ticker))
                .setContentTitle(context.getResources().getString(R.string.title_notification_content))
                .setContentText(plan.getContent())
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(planCode, 0, notification);

        //数据库存储
        enderplanDB.editReminderTime(planCode, 0);

        if (dataManager.getDataStatus() == DataManager.DataStatus.STATUS_DATA_LOADED) {
            //此时数据已加载完成，可以通过DataManager访问到数据
            int position = dataManager.getPlanLocationInPlanList(planCode);
            dataManager.getPlan(position).setReminderTime(0);
            //通知界面更新（NOTE：如果app已退出，但进程还没被杀，仍然会发出通知）
            EventBus.getDefault().post(new RemindedEvent(position, planCode));
        }

        /*Intent remindedIntent = new Intent("com.zack.enderplan.ACTION_REMINDED");
        remindedIntent.putExtra("plan_code", planCode);
        remindedIntent.setPackage(context.getPackageName());
        context.sendOrderedBroadcast(remindedIntent, null);*/
        /*LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(remindedIntent);*/
    }
}
