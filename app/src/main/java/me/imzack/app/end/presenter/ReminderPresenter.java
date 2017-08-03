package me.imzack.app.end.presenter;

import me.imzack.app.end.R;
import me.imzack.app.end.util.ResourceUtil;
import me.imzack.app.end.util.TimeUtil;
import me.imzack.app.end.model.bean.Plan;
import me.imzack.app.end.eventbus.event.PlanDetailChangedEvent;
import me.imzack.app.end.model.DataManager;
import me.imzack.app.end.view.contract.ReminderViewContract;
import me.imzack.app.end.common.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import javax.inject.Inject;

public class ReminderPresenter extends BasePresenter {

    private ReminderViewContract mReminderViewContract;
    private int mPlanListPosition;
    private Plan mPlan;
    private DataManager mDataManager;
    private EventBus mEventBus;
    private int mReminderCoordinateY;

    @Inject
    public ReminderPresenter(ReminderViewContract reminderViewContract, int planListPosition, DataManager dataManager, EventBus eventBus) {
        mReminderViewContract = reminderViewContract;
        mPlanListPosition = planListPosition;
        mDataManager = dataManager;
        mEventBus = eventBus;

        mPlan = mDataManager.getPlan(mPlanListPosition);
    }

    @Override
    public void attach() {
        mReminderViewContract.showInitialView(
                mPlan.getContent(),
                mPlan.hasDeadline(),
                mPlan.hasDeadline() ? TimeUtil.formatDateTime(mPlan.getDeadline()) : null
        );
    }

    @Override
    public void detach() {
        mReminderViewContract = null;
    }

    public void notifyPreDrawingReminder(int reminderCoordinateY) {
        mReminderCoordinateY = reminderCoordinateY;
        mReminderViewContract.playEnterAnimation();
    }

    public void notifyTouchFinished(float coordinateY) {
        if (coordinateY < mReminderCoordinateY) {
            mReminderViewContract.exit();
        }
    }

    public void notifyDelayingReminder(String delay) {
        Calendar calendar = Calendar.getInstance();
        String delayDscpt;
        switch (delay) {
            case Constant.ONE_HOUR:
                calendar.add(Calendar.HOUR, 1);
                delayDscpt = ResourceUtil.getString(R.string.toast_delay_1_hour);
                break;
            case Constant.TOMORROW:
                calendar.add(Calendar.DATE, 1);
                delayDscpt = ResourceUtil.getString(R.string.toast_delay_tomorrow);
                break;
            default:
                throw new IllegalArgumentException("The argument delayTime cannot be " + delay);
        }
        updateReminderTime(calendar.getTimeInMillis(), String.format(ResourceUtil.getString(R.string.toast_reminder_delayed_format), delayDscpt));
    }

    public void notifyUpdatingReminderTime(long reminderTime) {
        if (reminderTime == Constant.UNDEFINED_TIME) return;
        if (TimeUtil.isValidTime(reminderTime)) {
            updateReminderTime(
                    reminderTime,
                    String.format(ResourceUtil.getString(R.string.toast_reminder_delayed_format), String.format(ResourceUtil.getString(R.string.toast_delay_more), TimeUtil.formatDateTime(reminderTime)))
            );
        } else {
            mReminderViewContract.showToast(R.string.toast_past_reminder_time);
        }
    }

    private void updateReminderTime(long reminderTime, String toastMsg) {
        mDataManager.notifyReminderTimeChanged(mPlanListPosition, reminderTime);
        mEventBus.post(new PlanDetailChangedEvent(
                getPresenterName(),
                mPlan.getPlanCode(),
                mPlanListPosition,
                PlanDetailChangedEvent.FIELD_REMINDER_TIME
        ));
        mReminderViewContract.showToast(toastMsg);
        mReminderViewContract.exit();
    }

    public void notifyPlanCompleted() {
        //不需要检测是否有reminder，因为这里一定是没有reminder的
        mDataManager.notifyPlanStatusChanged(mPlanListPosition);
        mPlanListPosition = mPlan.isCompleted() ? mDataManager.getUcPlanCount() : 0;
        mEventBus.post(new PlanDetailChangedEvent(
                getPresenterName(),
                mPlan.getPlanCode(),
                mPlanListPosition,
                PlanDetailChangedEvent.FIELD_PLAN_STATUS
        ));
        mReminderViewContract.showToast(R.string.toast_plan_completed);
        mReminderViewContract.exit();
    }

    public void notifyEnteringPlanDetail() {
        mReminderViewContract.enterPlanDetail(mPlanListPosition);
        mReminderViewContract.exit();
    }
}
