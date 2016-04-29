package com.zack.enderplan.presenter;

import android.content.Context;

import com.zack.enderplan.R;
import com.zack.enderplan.application.EnderPlanApp;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.PlanStatusChangedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.manager.DataManager;
import com.zack.enderplan.util.LogUtil;
import com.zack.enderplan.view.HomeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HomePresenter implements Presenter<HomeView> {

    private HomeView homeView;
    private DataManager dataManager;
    private String nonePlan;
    private String planExist;

    private static final String LOG_TAG = "HomePresenter";

    public HomePresenter(HomeView homeView) {
        attachView(homeView);
        dataManager = DataManager.getInstance();

        Context context = EnderPlanApp.getGlobalContext();
        nonePlan = context.getResources().getString(R.string.plan_uncompleted_none);
        planExist = context.getResources().getString(R.string.plan_uncompleted_exist);
    }

    @Override
    public void attachView(HomeView view) {
        homeView = view;
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView() {
        homeView = null;
        EventBus.getDefault().unregister(this);
    }

    public void initDrawerHeaderContent() {
        showUcPlanCount(dataManager.getUcPlanCount());
    }

    //显示未完成的计划
    private void showUcPlanCount(int ucPlanCount) {
        homeView.updateDrawerHeaderContent(String.valueOf(ucPlanCount), ucPlanCount == 0 ? nonePlan : planExist);
    }

    public void notifyPlanCreated() {
        //更新view
        showUcPlanCount(dataManager.getUcPlanCount());
        //通过EventBus通知刷新适配器
        EventBus.getDefault().post(new PlanCreatedEvent());
        //对view层回调
        homeView.onPlanCreated(dataManager.getPlan(0).getContent());
    }

    public void notifyPlanDetailAndStatusChanged(int position, String planCode) {
        //TODO 由于PlanStatus改变后AllPlans是全部刷新的，所以这里会产生重复
        notifyPlanDetailChanged(position);
        notifyPlanStatusChanged(position, planCode);
    }

    public void notifyPlanDetailChanged(int position) {
        //通知AllPlans、AllTypes、TypeDetail更新
        EventBus.getDefault().post(new PlanDetailChangedEvent(position));
    }

    public void notifyPlanStatusChanged(int position, String planCode) {
        EventBus.getDefault().post(new PlanStatusChangedEvent(position, planCode));
    }

    //通过PlanDetailActivity删除时（不能撤销）
    public void notifyPlanDeleted(int position, String planCode, String content, boolean isCompleted) {

        //通知AllPlans、AllTypes、TypeDetail更新
        EventBus.getDefault().post(new PlanDeletedEvent(position, planCode, isCompleted));

        if (!isCompleted) {
            //需要更新drawer上的未完成计划数量，因为刚刚删除了一个未完成的计划
            showUcPlanCount(dataManager.getUcPlanCount());
        }

        //show SnackBar
        homeView.onPlanDeleted(content);
    }

    /*public void notifyReminderOff(String planCode) {

    }*/

    @Subscribe
    public void onUcPlanCountChanged(UcPlanCountChangedEvent event) {
        //当未完成计划数量改变的事件到来时，更新侧栏上显示的未完成计划数量
        showUcPlanCount(dataManager.getUcPlanCount());
    }
}
