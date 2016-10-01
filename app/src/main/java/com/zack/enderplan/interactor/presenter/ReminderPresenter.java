package com.zack.enderplan.interactor.presenter;

import android.graphics.Color;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.database.DatabaseManager;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.utility.ReminderManager;
import com.zack.enderplan.domain.view.ReminderView;

import org.greenrobot.eventbus.EventBus;

public class ReminderPresenter extends BasePresenter implements Presenter<ReminderView> {

    private ReminderView mReminderView;
    private Plan mPlan;
    private DatabaseManager mDatabaseManager;
    private DataManager mDataManager;
    private ReminderManager mReminderManager;

    public ReminderPresenter(ReminderView reminderView, Plan plan) {
        attachView(reminderView);
        this.mPlan = plan;

        mDatabaseManager = DatabaseManager.getInstance();
        mDataManager = DataManager.getInstance();
        mReminderManager = ReminderManager.getInstance();
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
        mReminderView.showInitialView(
                mPlan.getContent(),
                Color.parseColor(mDatabaseManager.queryTypeMarkByTypeCode(mPlan.getTypeCode()))
        );
    }

    public void notifyReminderDelayed() {
        //设定为当前时刻的5分钟后提醒
        long newReminderTime = System.currentTimeMillis() + 300000;

        //设定提醒
        mReminderManager.setAlarm(mPlan.getPlanCode(), newReminderTime);

        //修改list（NOTE：在这个类里的plan上修改没有作用，因为它和list中的plan不是同一个对象）
        if (mDataManager.getDataStatus() == DataManager.DataStatus.STATUS_DATA_LOADED) {
            //说明数据已加载完成
            int position = mDataManager.getPlanLocationInPlanList(mPlan.getPlanCode());
            mDataManager.getPlan(position).setReminderTime(newReminderTime);

            EventBus.getDefault().post(new PlanDetailChangedEvent(
                    getPresenterName(),
                    mPlan.getPlanCode(),
                    position,
                    PlanDetailChangedEvent.FIELD_REMINDER_TIME
            ));
        }

        //数据库存储
        mDatabaseManager.editReminderTime(mPlan.getPlanCode(), newReminderTime);

        mReminderView.showToast(R.string.toast_reminder_delayed_5min);
        mReminderView.exitReminder();
    }

    public void notifyReminderCanceled() {
        mReminderView.showToast(R.string.toast_reminder_canceled);
        mReminderView.exitReminder();
    }

    public void notifyPlanCompleted() {

        long newCompletionTime = System.currentTimeMillis();

        //修改数据
        if (mDataManager.getDataStatus() == DataManager.DataStatus.STATUS_DATA_LOADED) {
            //说明数据已经加载完成

            //获取要修改的plan在list中的位置
            int posInPlanList = mDataManager.getPlanLocationInPlanList(mPlan.getPlanCode());

            //获取list中的plan（在这之后，所有的plan都用planInPlanList）
            Plan planInPlanList = mDataManager.getPlan(posInPlanList);

            //更新Maps
            mDataManager.updateUcPlanCountOfEachTypeMap(planInPlanList.getTypeCode(), -1);
            mDataManager.updateUcPlanCount(-1);

            //从list（未完成区域）中移除
            mDataManager.removeFromPlanList(posInPlanList);

            //设置新的creationTime与completionTime
            planInPlanList.setCreationTime(0);
            planInPlanList.setCompletionTime(newCompletionTime);

            //添加到list（已完成区域）
            int newPosInPlanList = mDataManager.getUcPlanCount();
            mDataManager.addToPlanList(newPosInPlanList, planInPlanList);

            //通知AllPlans、AllTypes、PlanDetail与TypeDetail（Presenters）更新其界面
            EventBus.getDefault().post(new PlanDetailChangedEvent(
                    getPresenterName(),
                    planInPlanList.getPlanCode(),
                    newPosInPlanList,
                    PlanDetailChangedEvent.FIELD_PLAN_STATUS
            ));
        }

        //数据库存储
        mDatabaseManager.editPlanStatus(mPlan.getPlanCode(), 0, newCompletionTime);

        mReminderView.showToast(R.string.toast_plan_completed);
        mReminderView.exitReminder();
    }
}
