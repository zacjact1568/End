package com.zack.enderplan.interactor.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.utility.Util;
import com.zack.enderplan.domain.view.CreatePlanView;

import org.greenrobot.eventbus.EventBus;

public class CreatePlanPresenter extends BasePresenter implements Presenter<CreatePlanView> {

    private CreatePlanView mCreatePlanView;
    private DataManager mDataManager;
    private Plan mPlan;
    private String mDateTimeFormatStr, mClickToSetDscpt;

    public CreatePlanPresenter(CreatePlanView createPlanView) {
        attachView(createPlanView);
        mDataManager = DataManager.getInstance();
        mPlan = new Plan(Util.makeCode());

        Context context = App.getGlobalContext();
        mDateTimeFormatStr = context.getString(R.string.date_time_format);
        mClickToSetDscpt = context.getString(R.string.dscpt_click_to_set);
    }

    @Override
    public void attachView(CreatePlanView view) {
        mCreatePlanView = view;
    }

    @Override
    public void detachView() {
        mCreatePlanView = null;
    }

    public void setInitialView() {
        mCreatePlanView.showInitialView(new SimpleTypeAdapter(mDataManager.getTypeList(), SimpleTypeAdapter.STYLE_SPINNER));
    }

    public void notifyContentChanged(String content) {
        mPlan.setContent(content);
        mCreatePlanView.onContentChanged(!TextUtils.isEmpty(content));
    }

    public void notifyTypeCodeChanged(int spinnerPos) {
        mPlan.setTypeCode(mDataManager.getType(spinnerPos).getTypeCode());
    }

    public void notifyDeadlineChanged(long deadline) {
        if (mPlan.getDeadline() == deadline) return;
        mPlan.setDeadline(deadline);
        mCreatePlanView.onDeadlineChanged(deadline != 0, formatDateTime(deadline));
    }

    public void notifyReminderTimeChanged(long reminderTime) {
        if (mPlan.getReminderTime() == reminderTime) return;
        mPlan.setReminderTime(reminderTime);
        mCreatePlanView.onReminderTimeChanged(reminderTime != 0, formatDateTime(reminderTime));
    }

    public void notifyStarStatusChanged() {
        //isStarred表示点击之前的星标状态
        boolean isStarred = mPlan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED;
        mPlan.setStarStatus(isStarred ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED);
        //星标状态变化了
        mCreatePlanView.onStarStatusChanged(!isStarred);
    }

    //TODO 以后都用这种形式，即notifySetting***，更换控件就不用改方法名了
    public void notifySettingDeadline() {
        mCreatePlanView.showDeadlinePickerDialog(mPlan.getDeadline());
    }

    public void notifySettingReminder() {
        mCreatePlanView.showReminderTimePickerDialog(mPlan.getReminderTime());
    }

    public void notifyCreatingPlan() {
        if (TextUtils.isEmpty(mPlan.getContent())) {
            mCreatePlanView.onDetectedEmptyContent();
        } else {
            mPlan.setCreationTime(System.currentTimeMillis());
            mDataManager.notifyPlanCreated(mPlan);
            EventBus.getDefault().post(new PlanCreatedEvent(
                    getPresenterName(),
                    mPlan.getPlanCode(),
                    mDataManager.getRecentlyCreatedPlanLocation()
            ));
            mCreatePlanView.exitCreatePlan();
        }
    }

    public void notifyPlanCreationCanceled() {
        //TODO 判断是否已编辑过
        mCreatePlanView.exitCreatePlan();
    }

    private String formatDateTime(long timeInMillis) {
        if (timeInMillis == 0) {
            return mClickToSetDscpt;
        } else {
            return DateFormat.format(mDateTimeFormatStr, timeInMillis).toString();
        }
    }
}
