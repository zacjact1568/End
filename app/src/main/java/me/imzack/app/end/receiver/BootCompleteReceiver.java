package me.imzack.app.end.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.imzack.app.end.App;
import me.imzack.app.end.util.SystemUtil;

import java.util.Map;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Map<String, Long> reminderTimeMap = App.getDataManager().getDatabaseHelper().queryReminderTimeWithEnabledReminder();
        for (Map.Entry<String, Long> entry : reminderTimeMap.entrySet()) {
            SystemUtil.setReminder(entry.getKey(), entry.getValue());
        }
    }
}
