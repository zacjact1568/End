package com.zack.enderplan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zack.enderplan.util.SystemUtil;
import com.zack.enderplan.model.database.DatabaseManager;

import java.util.Map;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Map<String, Long> reminderTimeMap = DatabaseManager.getInstance().queryReminderTimeWithEnabledReminder();
        for (Map.Entry<String, Long> entry : reminderTimeMap.entrySet()) {
            SystemUtil.setReminder(entry.getKey(), entry.getValue());
        }
    }
}
