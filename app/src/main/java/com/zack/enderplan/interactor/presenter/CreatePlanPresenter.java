package com.zack.enderplan.interactor.presenter;

import com.zack.enderplan.domain.fragment.CalendarDialogFragment;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.database.DatabaseDispatcher;
import com.zack.enderplan.model.ram.DataManager;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.domain.view.CreatePlanView;
import com.zack.enderplan.interactor.adapter.TypeSpinnerAdapter;

public class CreatePlanPresenter implements Presenter<CreatePlanView> {

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
        mCreatePlanView.showInitialView(new TypeSpinnerAdapter(mDataManager.getTypeList(), mDataManager.getTypeMarkAndColorResMap()));
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

    public void createNewPlan() {
        mPlan.setCreationTime(System.currentTimeMillis());
        //处理并向model层传送数据，更新model
        mDataManager.addToPlanList(0, mPlan);
        //更新未完成计划的数量
        mDataManager.updateUcPlanCount(1);
        //设置提醒
        if (mPlan.getReminderTime() != 0) {
            //说明之前是设置了提醒的
            ReminderManager.getInstance().setAlarm(mPlan.getPlanCode(), mPlan.getReminderTime());
        }
        //更新每个类型具有的计划数量map
        mDataManager.updateUcPlanCountOfEachTypeMap(mPlan.getTypeCode(), 1);
        //存储至数据库
        DatabaseDispatcher.getInstance().savePlan(mPlan);
    }
}
