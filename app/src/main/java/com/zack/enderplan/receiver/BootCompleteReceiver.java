package com.zack.enderplan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zack.enderplan.model.database.DatabaseDispatcher;
import com.zack.enderplan.manager.ReminderManager;

import java.util.Map;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootCompleteReceiver", "Boot Finished");
        ReminderManager manager = ReminderManager.getInstance();

        DatabaseDispatcher dispatcher = DatabaseDispatcher.getInstance();

        Map<String, Long> reminderTimeMap = dispatcher.queryReminderTimeWithEnabledReminder();

        for (Map.Entry<String, Long> entry : reminderTimeMap.entrySet()) {
            manager.setAlarm(entry.getKey(), entry.getValue());
        }
    }
}
