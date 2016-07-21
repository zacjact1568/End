package com.zack.enderplan.interactor.presenter;

import android.content.Context;
import android.graphics.Color;

import com.zack.enderplan.R;
import com.zack.enderplan.application.App;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.database.DatabaseDispatcher;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.model.ram.DataManager;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.domain.view.ReminderView;

import org.greenrobot.eventbus.EventBus;

public class ReminderPresenter implements Presenter<ReminderView> {

    private ReminderView reminderView;
    private Plan plan;//TODO 把这个plan换掉，只传需要的进来
    private DatabaseDispatcher databaseDispatcher;
    private DataManager dataManager;
    private ReminderManager reminderManager;
    private String reminderDelayed5minStr;

    public ReminderPresenter(ReminderView reminderView, Plan plan) {
        attachView(reminderView);
        this.plan = plan;

        databaseDispatcher = DatabaseDispatcher.getInstance();
        dataManager = DataManager.getInstance();
        reminderManager = ReminderManager.getInstance();

        Context context = App.getGlobalContext();
        reminderDelayed5minStr = context.getResources().getString(R.string.toast_reminder_delayed_5min);
    }

    @Override
    public void attachView(ReminderView view) {
        reminderView = view;
    }

    @Override
    public void detachView() {
        reminderView = null;
    }

    public void setInitialView() {
        reminderView.showInitialView(
                plan.getContent(),
                Color.parseColor(databaseDispatcher.queryTypeMarkByTypeCode(plan.getTypeCode()))
        );
    }

    public void notifyReminderDelayed() {
        //设定为当前时刻的5分钟后提醒
        long newReminderTime = System.currentTimeMillis() + 300000;

        //设定提醒
        reminderManager.setAlarm(plan.getPlanCode(), newReminderTime);

        //修改list（NOTE：在这个类里的plan上修改没有作用，因为它和list中的plan不是同一个对象）
        if (dataManager.getDataStatus() == DataManager.DataStatus.STATUS_DATA_LOADED) {
            //说明数据已加载完成
            int position = dataManager.getPlanLocationInPlanList(plan.getPlanCode());
            dataManager.getPlan(position).setReminderTime(newReminderTime);

            EventBus.getDefault().post(new PlanDetailChangedEvent(plan.getPlanCode(), position, false, false, -1));
            //TODO 有4个订阅者，但只有两个需要刷新，虽然多刷新也无所谓，但后续也应添加判断
        }

        //数据库存储
        databaseDispatcher.editReminderTime(plan.getPlanCode(), newReminderTime);

        reminderView.onReminderDelayed(reminderDelayed5minStr);
    }

    public void notifyReminderCanceled() {
        reminderView.onReminderCanceled();
    }

    public void notifyPlanCompleted() {

        long newCompletionTime = System.currentTimeMillis();

        //修改数据
        if (dataManager.getDataStatus() == DataManager.DataStatus.STATUS_DATA_LOADED) {
            //说明数据已经加载完成

            //获取要修改的plan在list中的位置
            int posInPlanList = dataManager.getPlanLocationInPlanList(plan.getPlanCode());

            //获取list中的plan（在这之后，所有的plan都用planInPlanList）
            Plan planInPlanList = dataManager.getPlan(posInPlanList);

            //更新Maps
            dataManager.updateUcPlanCountOfEachTypeMap(planInPlanList.getTypeCode(), -1);
            dataManager.updateUcPlanCount(-1);

            //通知HomePresenter（更新侧栏header）
            EventBus.getDefault().post(new UcPlanCountChangedEvent());

            //从list（未完成区域）中移除
            dataManager.removeFromPlanList(posInPlanList);

            //设置新的creationTime与completionTime
            planInPlanList.setCreationTime(0);
            planInPlanList.setCompletionTime(newCompletionTime);

            //添加到list（已完成区域）
            dataManager.addToPlanList(dataManager.getUcPlanCount(), planInPlanList);

            //通知AllPlans、AllTypes、PlanDetail与TypeDetail（Presenters）更新其界面
            EventBus.getDefault().post(new PlanDetailChangedEvent(planInPlanList.getPlanCode(), posInPlanList, false, true, -1));
        }

        //数据库存储
        databaseDispatcher.editPlanStatus(plan.getPlanCode(), 0, newCompletionTime);

        reminderView.onPlanCompleted();
    }
}
