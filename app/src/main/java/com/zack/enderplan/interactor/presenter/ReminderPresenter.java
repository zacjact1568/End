package com.zack.enderplan.interactor.presenter;

import android.content.Context;
import android.text.format.DateFormat;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.domain.view.ReminderView;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.common.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

public class ReminderPresenter extends BasePresenter implements Presenter<ReminderView> {

    private ReminderView mReminderView;
    private int mPosition;
    private Plan mPlan;
    private DataManager mDataManager;
    private String mReminderDelayedFormat, mDelay1HourStr, mDelayTomorrowStr, mDelayMoreStr, mShortDateTimeFormat, mPlanCompletedStr;

    public ReminderPresenter(ReminderView reminderView, int position, String planCode) {
        attachView(reminderView);
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

        Context context = App.getGlobalContext();
        mReminderDelayedFormat = context.getString(R.string.toast_reminder_delayed_format);
        mDelay1HourStr = context.getString(R.string.toast_delay_1_hour);
        mDelayTomorrowStr = context.getString(R.string.toast_delay_tomorrow);
        mDelayMoreStr = context.getString(R.string.toast_delay_more);
        mShortDateTimeFormat = context.getString(R.string.date_time_format_short);
        mPlanCompletedStr = context.getString(R.string.toast_plan_completed);
    }

    @Override
    public void attachView(ReminderView view) {
        mReminderView = view;
    }

    @Override
    public void detachView() {
        mReminderView = null;
    }

    public void setInitialView() {
        mReminderView.showInitialView(mPlan.getContent());
    }

    public void notifyDelayingReminder(String delay) {
        Calendar calendar = Calendar.getInstance();
        String delayDscpt;
        switch (delay) {
            case Constant.ONE_HOUR:
                calendar.add(Calendar.HOUR, 1);
                delayDscpt = mDelay1HourStr;
                break;
            case Constant.TOMORROW:
                calendar.add(Calendar.DATE, 1);
                delayDscpt = mDelayTomorrowStr;
                break;
            default:
                throw new IllegalArgumentException("The argument delayTime cannot be " + delay);
        }
        updateReminderTime(calendar.getTimeInMillis(), String.format(mReminderDelayedFormat, delayDscpt));
    }

    public void notifyUpdatingReminderTime(long reminderTime) {
        if (reminderTime == Constant.TIME_UNDEFINED) return;
        updateReminderTime(
                reminderTime,
                String.format(mReminderDelayedFormat, String.format(mDelayMoreStr, DateFormat.format(mShortDateTimeFormat, reminderTime).toString()))
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
        mReminderView.showToast(toastMsg);
        mReminderView.exitReminder();
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
        mReminderView.showToast(mPlanCompletedStr);
        mReminderView.exitReminder();
    }

    public void notifyEnteringPlanDetail() {
        mReminderView.enterPlanDetail(mPosition);
        mReminderView.exitReminder();
    }
}
