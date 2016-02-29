package com.zack.enderplan.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zack.enderplan.receiver.ReminderReceiver;

public class ReminderManager {

    private Context context;

    public ReminderManager(Context context) {
        this.context = context;
    }

    private PendingIntent getPendingIntent(String planCode) {
        //显式Intent
        Intent intent = new Intent(context, ReminderReceiver.class);
        //相当于只有下面这条语句才能区分不同的PendingIntent，也就是code才能区分
        //如果code相同，那么extra也会被无视
        intent.setAction("com.zack.enderplan.ACTION_REMINDER_PLAN_" + planCode);
        intent.setPackage(context.getPackageName());
        intent.putExtra("plan_code", planCode);
        //此处FLAG_UPDATE_CURRENT真的很有用，修改过plan后再set，通知内容也会被替换成新的plan.content了
        //也就不需要cancel后再set了
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void setAlarm(String planCode, long reminderTimeInMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, getPendingIntent(planCode));
        Log.d("ReminderManager", "Reminder已设置，Code为：" + planCode);
    }

    public void cancelAlarm(String planCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(planCode));
        Log.d("ReminderManager", "Reminder已取消");
    }
}
