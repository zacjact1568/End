package com.zack.enderplan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.manager.ReminderManager;

import java.util.Map;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootCompleteReceiver", "Boot Finished");
        ReminderManager manager = ReminderManager.getInstance();

        EnderPlanDB enderplanDB = EnderPlanDB.getInstance();

        Map<String, Long> reminderTimeMap = enderplanDB.queryReminderTimeWithEnabledReminder();

        for (Map.Entry<String, Long> entry : reminderTimeMap.entrySet()) {
            manager.setAlarm(entry.getKey(), entry.getValue());
        }
    }
}
