package net.zackzhang.app.end.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.util.SystemUtil

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderTimeMap = DataManager.databaseHelper.queryReminderTimeWithEnabledReminder()
        for ((key, value) in reminderTimeMap) {
            SystemUtil.setReminder(key, value)
        }
    }
}
