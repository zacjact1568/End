package com.zack.enderplan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zack.enderplan.model.database.DatabaseManager;
import com.zack.enderplan.utility.ReminderManager;

import java.util.Map;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootCompleteReceiver", "Boot Finished");
        ReminderManager rManager = ReminderManager.getInstance();

        DatabaseManager dManager = DatabaseManager.getInstance();

        Map<String, Long> reminderTimeMap = dManager.queryReminderTimeWithEnabledReminder();

        for (Map.Entry<String, Long> entry : reminderTimeMap.entrySet()) {
            rManager.setAlarm(entry.getKey(), entry.getValue());
        }
    }
}
