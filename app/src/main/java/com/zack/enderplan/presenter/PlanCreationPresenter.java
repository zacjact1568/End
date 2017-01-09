package com.zack.enderplan.presenter;

import android.text.TextUtils;
import android.text.format.DateFormat;

import com.zack.enderplan.R;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.view.adapter.SimpleTypeAdapter;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.view.contract.PlanCreationViewContract;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class PlanCreationPresenter extends BasePresenter {

    private PlanCreationViewContract mPlanCreationViewContract;
    private DataManager mDataManager;
    private Plan mPlan;

    @Inject
    public PlanCreationPresenter(PlanCreationViewContract planCreationViewContract, Plan plan, DataManager dataManager) {
        mPlanCreationViewContract = planCreationViewContract;
        mPlan = plan;
        mDataManager = dataManager;
    }

    @Override
    public void attach() {
        mPlanCreationViewContract.showInitialView(new SimpleTypeAdapter(mDataManager.getTypeList(), SimpleTypeAdapter.STYLE_SPINNER));
    }

    @Override
    public void detach() {
        mPlanCreationViewContract = null;
    }

    public void notifyContentChanged(String content) {
        mPlan.setContent(content);
        mPlanCreationViewContract.onContentChanged(!TextUtils.isEmpty(content));
    }

    public void notifyTypeCodeChanged(int spinnerPos) {
        mPlan.setTypeCode(mDataManager.getType(spinnerPos).getTypeCode());
    }

    public void notifyDeadlineChanged(long deadline) {
        if (mPlan.getDeadline() == deadline) return;
        if (Util.isFutureTime(deadline)) {
            mPlan.setDeadline(deadline);
            mPlanCreationViewContract.onDeadlineChanged(mPlan.hasDeadline(), formatDateTime(deadline));
        } else {
            mPlanCreationViewContract.showToast(R.string.toast_past_deadline);
        }
    }

    public void notifyReminderTimeChanged(long reminderTime) {
        if (mPlan.getReminderTime() == reminderTime) return;
        if (Util.isFutureTime(reminderTime)) {
            mPlan.setReminderTime(reminderTime);
            mPlanCreationViewContract.onReminderTimeChanged(mPlan.hasReminder(), formatDateTime(reminderTime));
        } else {
            mPlanCreationViewContract.showToast(R.string.toast_past_reminder_time);
        }
    }

    public void notifyStarStatusChanged() {
        mPlan.invertStarStatus();
        mPlanCreationViewContract.onStarStatusChanged(mPlan.isStarred());
    }

    //TODO 以后都用这种形式，即notifySetting***，更换控件就不用改方法名了
    public void notifySettingDeadline() {
        mPlanCreationViewContract.showDeadlinePickerDialog(mPlan.getDeadline());
    }

    public void notifySettingReminder() {
        mPlanCreationViewContract.showReminderTimePickerDialog(mPlan.getReminderTime());
    }

    public void notifyCreatingPlan() {
        if (TextUtils.isEmpty(mPlan.getContent())) {
            mPlanCreationViewContract.showToast(R.string.toast_empty_content);
        } else {
            mPlan.setCreationTime(System.currentTimeMillis());
            mDataManager.notifyPlanCreated(mPlan);
            EventBus.getDefault().post(new PlanCreatedEvent(
                    getPresenterName(),
                    mPlan.getPlanCode(),
                    mDataManager.getRecentlyCreatedPlanLocation()
            ));
            mPlanCreationViewContract.exit();
        }
    }

    public void notifyPlanCreationCanceled() {
        //TODO 判断是否已编辑过
        mPlanCreationViewContract.exit();
    }

    private String formatDateTime(long timeInMillis) {
        if (timeInMillis == Constant.UNDEFINED_TIME) {
            return Util.getString(R.string.dscpt_click_to_set);
        } else {
            return DateFormat.format(Util.getString(R.string.date_time_format), timeInMillis).toString();
        }
    }
}
