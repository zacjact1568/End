package com.zack.enderplan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zack.enderplan.common.Util;
import com.zack.enderplan.view.activity.ReminderActivity;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.database.DatabaseManager;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.common.Constant;

import org.greenrobot.eventbus.EventBus;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //直接从数据库获取plan（若通过DataManager获取，可能需要异步加载，而在receiver中不要做异步操作）
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Plan plan = databaseManager.queryPlan(intent.getStringExtra(Constant.PLAN_CODE));
        databaseManager.updateReminderTime(plan.getPlanCode(), Constant.TIME_UNDEFINED);

        DataManager dataManager = DataManager.getInstance();
        int position = -1;
        if (dataManager.isDataLoaded()) {
            //若DataManager中的数据已加载完成
            position = dataManager.getPlanLocationInPlanList(plan.getPlanCode());
            dataManager.getPlan(position).setReminderTime(Constant.TIME_UNDEFINED);
            //发出事件（NOTE：如果app已退出，但进程还没被杀，仍然会发出）
            EventBus.getDefault().post(new PlanDetailChangedEvent(
                    getClass().getSimpleName(),
                    plan.getPlanCode(),
                    position,
                    PlanDetailChangedEvent.FIELD_REMINDER_TIME
            ));
        }

        Util.showNotification(
                plan.getPlanCode(),
                plan.getContent(),
                ReminderActivity.getPendingIntentForStart(context, plan.getPlanCode(), position)
        );
    }
}
