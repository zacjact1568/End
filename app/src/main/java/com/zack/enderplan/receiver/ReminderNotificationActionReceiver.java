package com.zack.enderplan.receiver;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.zack.enderplan.R;
import com.zack.enderplan.util.Constant;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.SystemUtil;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.view.activity.ReminderActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

public class ReminderNotificationActionReceiver extends BaseReceiver {

    public static final String NOTIFICATION_ACTION_CONTENT = "CONTENT";
    public static final String NOTIFICATION_ACTION_COMPLETE = "COMPLETE";
    public static final String NOTIFICATION_ACTION_DELAY = "DELAY";

    private DataManager mDataManager;
    private EventBus mEventBus;
    private int mPlanListPosition;
    private Plan mPlan;

    public ReminderNotificationActionReceiver() {
        mDataManager = DataManager.getInstance();
        mEventBus = EventBus.getDefault();
    }

    public static PendingIntent getPendingIntentForSend(Context context, String planCode, int planListPosition, String notificationAction) {
        return PendingIntent.getBroadcast(
                context,
                0,
                new Intent(context, ReminderNotificationActionReceiver.class)
                        .setAction(String.format(Constant.ACTION_REMINDER_NOTIFICATION_ACTION, planCode, notificationAction))
                        .setPackage(context.getPackageName())
                        //plan_code总是有效的，position可能无效（-1），但优先考虑position
                        .putExtra(Constant.PLAN_CODE, planCode)
                        .putExtra(Constant.PLAN_LIST_POSITION, planListPosition)
                        .putExtra(Constant.NOTIFICATION_ACTION, notificationAction),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!mDataManager.isDataLoaded()) {
            //说明DataManager中的数据到点击通知为止还未加载完
            SystemUtil.showToast(R.string.toast_data_loading_unfinished);
            return;
        }

        mPlanListPosition = intent.getIntExtra(Constant.PLAN_LIST_POSITION, -1);

        if (mPlanListPosition == -1) {
            //说明DataManager中的数据在ReminderReceiver的时候还未加载完，但是现在已经加载完了（显示通知到点击通知之间），更新mPlanListPosition
            mPlanListPosition = mDataManager.getPlanLocationInPlanList(intent.getStringExtra(Constant.PLAN_CODE));
        }

        mPlan = mDataManager.getPlan(mPlanListPosition);

        SystemUtil.cancelNotification(mPlan.getPlanCode());

        switch (intent.getStringExtra(Constant.NOTIFICATION_ACTION)) {
            case NOTIFICATION_ACTION_CONTENT:
                //点击通知内容
                ReminderActivity.start(context, mPlanListPosition);
                break;
            case NOTIFICATION_ACTION_COMPLETE:
                //点击完成按钮
                onComplete();
                break;
            case NOTIFICATION_ACTION_DELAY:
                //点击延迟按钮
                onDelay();
                break;
            default:
                throw new IllegalArgumentException("Notification action cannot be " + intent.getStringExtra(Constant.NOTIFICATION_ACTION));
        }
    }

    private void onComplete() {
        //不需要检测是否有reminder，因为这里一定是没有reminder的
        mDataManager.notifyPlanStatusChanged(mPlanListPosition);
        mPlanListPosition = mPlan.isCompleted() ? mDataManager.getUcPlanCount() : 0;
        mEventBus.post(new PlanDetailChangedEvent(
                getReceiverName(),
                mPlan.getPlanCode(),
                mPlanListPosition,
                PlanDetailChangedEvent.FIELD_PLAN_STATUS
        ));
        SystemUtil.showToast(R.string.toast_plan_completed);
    }

    private void onDelay() {
        Calendar calendar = Calendar.getInstance();
        //TODO 延迟的时间可自定义
        calendar.add(Calendar.MINUTE, 10);
        mDataManager.notifyReminderTimeChanged(mPlanListPosition, calendar.getTimeInMillis());
        mEventBus.post(new PlanDetailChangedEvent(
                getReceiverName(),
                mPlan.getPlanCode(),
                mPlanListPosition,
                PlanDetailChangedEvent.FIELD_REMINDER_TIME
        ));
        SystemUtil.showToast(String.format(ResourceUtil.getString(R.string.toast_reminder_delayed_format), ResourceUtil.getString(R.string.toast_delay_10_minutes)));
    }
}
