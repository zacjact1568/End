package com.zack.enderplan.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.zack.enderplan.R;
import com.zack.enderplan.database.EnderPlanDB;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String planCode = intent.getStringExtra("plan_code");

        EnderPlanDB enderplanDB = EnderPlanDB.getInstance();

        Intent contentIntent = new Intent("com.zack.enderplan.ACTION_REMINDER");
        contentIntent.putExtra("plan_code", planCode);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.enderplan_icon)
                .setTicker(context.getResources().getString(R.string.notification_ticker))
                .setContentTitle(context.getResources().getString(R.string.title_notification_content))
                .setContentText(enderplanDB.queryContentByPlanCode(planCode))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(planCode, 0, notification);

        ContentValues contentValues = new ContentValues();
        contentValues.put(EnderPlanDB.DB_STR_REMINDER_TIME, 0);
        enderplanDB.editPlan(planCode, contentValues);

        Intent remindedIntent = new Intent("com.zack.enderplan.ACTION_REMINDED");
        remindedIntent.putExtra("plan_code", planCode);
        remindedIntent.setPackage(context.getPackageName());
        context.sendOrderedBroadcast(remindedIntent, null);
        /*LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(remindedIntent);*/
    }
}
