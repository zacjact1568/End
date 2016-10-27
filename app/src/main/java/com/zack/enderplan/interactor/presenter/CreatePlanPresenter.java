package com.zack.enderplan.interactor.presenter;

import com.zack.enderplan.domain.fragment.CalendarDialogFragment;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
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

    public CreatePlanPresenter(CreatePlanView createPlanView) {
        attachView(createPlanView);
        mDataManager = DataManager.getInstance();
        mPlan = new Plan(Util.makeCode());
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

    public void notifyContentChanged(String newContent) {
        mPlan.setContent(newContent);
    }

    public void notifyTypeCodeChanged(int posInSpinner) {
        mPlan.setTypeCode(mDataManager.getType(posInSpinner).getTypeCode());
    }

    public void notifyDeadlineChanged(long newDeadline) {
        mPlan.setDeadline(newDeadline);
    }

    public void notifyReminderTimeChanged(long newReminderTime) {
        mPlan.setReminderTime(newReminderTime);
    }

    public void notifyStarStatusChanged() {
        //isStarred表示点击之前的星标状态
        boolean isStarred = mPlan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED;
        mPlan.setStarStatus(isStarred ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED);
        //星标状态变化了
        mCreatePlanView.onStarStatusChanged(!isStarred);
    }

    public void createDeadlineDialog() {
        CalendarDialogFragment deadlineDialog = CalendarDialogFragment.newInstance(mPlan.getDeadline());
        mCreatePlanView.onCreateDeadlineDialog(deadlineDialog);
    }

    public void createReminderDialog() {
        DateTimePickerDialogFragment reminderDialog = DateTimePickerDialogFragment.newInstance(mPlan.getReminderTime());
        mCreatePlanView.onCreateReminderDialog(reminderDialog);
    }

    public void notifyCreatingNewPlan() {
        mPlan.setCreationTime(System.currentTimeMillis());
        mDataManager.notifyPlanCreated(mPlan);
        EventBus.getDefault().post(new PlanCreatedEvent(getPresenterName(), mPlan.getPlanCode(), mDataManager.getRecentlyCreatedPlanLocation()));
    }
}
