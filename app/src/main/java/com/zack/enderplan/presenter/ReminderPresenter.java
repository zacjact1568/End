package com.zack.enderplan.presenter;

import android.text.format.DateFormat;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.contract.ReminderViewContract;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.common.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

public class ReminderPresenter extends BasePresenter<ReminderViewContract> {

    private ReminderViewContract mReminderViewContract;
    private int mPosition;
    private Plan mPlan;
    private DataManager mDataManager;

    public ReminderPresenter(ReminderViewContract reminderViewContract, int position, String planCode) {
        attachView(reminderViewContract);
        mDataManager = DataManager.getInstance();

        if (position != -1) {
            //说明DataManager中的数据在ReminderReceiver的时候就已经加载完，直接取plan
            mPosition = position;
        } else if (mDataManager.isDataLoaded()) {
            //说明DataManager中的数据在ReminderReceiver的时候还未加载完，但是现在已经加载完了（显示通知到点击通知之间）
            mPosition = mDataManager.getPlanLocationInPlanList(planCode);
        } else {
            //说明DataManager中的数据到现在还未加载完，只有等通知
            Logger.e("ReminderPresenter", "ERROR POSITION");
            mPosition = -1;
            //TODO 添加subscriber
        }
        mPlan = mDataManager.getPlan(mPosition);
    }

    @Override
    public void attachView(ReminderViewContract viewContract) {
        mReminderViewContract = viewContract;
    }

    @Override
    public void detachView() {
        mReminderViewContract = null;
    }

    public void setInitialView() {
        mReminderViewContract.showInitialView(mPlan.getContent());
    }

    public void notifyDelayingReminder(String delay) {
        Calendar calendar = Calendar.getInstance();
        String delayDscpt;
        switch (delay) {
            case Constant.ONE_HOUR:
                calendar.add(Calendar.HOUR, 1);
                delayDscpt = Util.getString(R.string.toast_delay_1_hour);
                break;
            case Constant.TOMORROW:
                calendar.add(Calendar.DATE, 1);
                delayDscpt = Util.getString(R.string.toast_delay_tomorrow);
                break;
            default:
                throw new IllegalArgumentException("The argument delayTime cannot be " + delay);
        }
        updateReminderTime(calendar.getTimeInMillis(), String.format(Util.getString(R.string.toast_reminder_delayed_format), delayDscpt));
    }

    public void notifyUpdatingReminderTime(long reminderTime) {
        if (reminderTime == Constant.TIME_UNDEFINED) return;
        updateReminderTime(
                reminderTime,
                String.format(Util.getString(R.string.toast_reminder_delayed_format), String.format(Util.getString(R.string.toast_delay_more), DateFormat.format(Util.getString(R.string.date_time_format_short), reminderTime).toString()))
        );
    }

    private void updateReminderTime(long reminderTime, String toastMsg) {
        mDataManager.notifyReminderTimeChanged(mPosition, reminderTime);
        EventBus.getDefault().post(new PlanDetailChangedEvent(
                getPresenterName(),
                mPlan.getPlanCode(),
                mPosition,
                PlanDetailChangedEvent.FIELD_REMINDER_TIME
        ));
        mReminderViewContract.showToast(toastMsg);
        mReminderViewContract.exit();
    }

    public void notifyPlanCompleted() {
        //不需要检测是否有reminder，因为这里一定是没有reminder的
        mDataManager.notifyPlanStatusChanged(mPosition);
        mPosition = mPlan.isCompleted() ? mDataManager.getUcPlanCount() : 0;
        EventBus.getDefault().post(new PlanDetailChangedEvent(
                getPresenterName(),
                mPlan.getPlanCode(),
                mPosition,
                PlanDetailChangedEvent.FIELD_PLAN_STATUS
        ));
        mReminderViewContract.showToast(Util.getString(R.string.toast_plan_completed));
        mReminderViewContract.exit();
    }

    public void notifyEnteringPlanDetail() {
        mReminderViewContract.enterPlanDetail(mPosition);
        mReminderViewContract.exit();
    }
}
