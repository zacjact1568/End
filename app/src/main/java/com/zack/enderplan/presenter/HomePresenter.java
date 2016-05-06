package com.zack.enderplan.presenter;

import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.manager.DataManager;
import com.zack.enderplan.view.HomeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HomePresenter implements Presenter<HomeView> {

    private HomeView homeView;
    private DataManager dataManager;
    private long lastBackKeyPressedTime;
    //private String nonePlan, onePlan, multiPlan;

    private static final String LOG_TAG = "HomePresenter";

    public HomePresenter(HomeView homeView) {
        attachView(homeView);
        dataManager = DataManager.getInstance();
        dataManager.initDataStruct();

        /*Context context = EnderPlanApp.getGlobalContext();
        nonePlan = context.getResources().getString(R.string.plan_uc_none);
        onePlan = context.getResources().getString(R.string.plan_uc_one);
        multiPlan = context.getResources().getString(R.string.plan_uc_multi);*/
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
        //dataManager.clearData();
    }

    public void initDrawerHeaderContent() {
        showUcPlanCount(dataManager.getUcPlanCount());
    }

    //显示未完成的计划
    private void showUcPlanCount(int ucPlanCount) {
        /*String ucPlanCountDscpt = "";
        if (ucPlanCount == 0) {
            ucPlanCountDscpt = nonePlan;
        } else if (ucPlanCount == 1) {
            ucPlanCountDscpt = onePlan;
        } else if (ucPlanCount > 1) {
            ucPlanCountDscpt = multiPlan;
        }*/
        homeView.updateDrawerHeaderContent(String.valueOf(ucPlanCount));
    }

    public void notifyPlanCreated() {
        //更新view
        showUcPlanCount(dataManager.getUcPlanCount());
        //通过EventBus通知刷新适配器
        EventBus.getDefault().post(new PlanCreatedEvent());
        //对view层回调
        homeView.onPlanCreated(dataManager.getPlan(0).getContent());
    }

    public void notifyPlanDetailChanged(int position, String planCode, boolean isTypeOfPlanChanged,
                                        boolean isPlanStatusChanged) {

        //通知AllPlans、AllTypes、TypeDetail更新
        EventBus.getDefault().post(new PlanDetailChangedEvent(position, planCode, isTypeOfPlanChanged, isPlanStatusChanged, -1));

        if (isPlanStatusChanged) {
            //如果计划完成情况改变，需要更新drawer上的header中的内容
            showUcPlanCount(dataManager.getUcPlanCount());
        }
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

    public void notifyBackPressed(boolean isDrawerOpen, boolean isOnRootFragment) {
        long currentTime = System.currentTimeMillis();
        if (isDrawerOpen) {
            homeView.onCloseDrawer();
        } else if (!isOnRootFragment || currentTime - lastBackKeyPressedTime < 1500) {
            //不是在根Fragment（可以直接退出的Fragment）上，或者连续点击间隔在1.5s以内，执行原back键操作
            homeView.onPressBackKey();
        } else {
            //否则更新上次点击back键的时间，并显示一个toast
            lastBackKeyPressedTime = currentTime;
            homeView.onShowDoubleClickToast();
        }
    }

    /*public void notifyReminderOff(String planCode) {

    }*/

    @Subscribe
    public void onUcPlanCountChanged(UcPlanCountChangedEvent event) {
        //当未完成计划数量改变的事件到来时，更新侧栏上显示的未完成计划数量
        showUcPlanCount(dataManager.getUcPlanCount());
    }
}
