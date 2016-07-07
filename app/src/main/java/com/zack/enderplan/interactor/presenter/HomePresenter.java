package com.zack.enderplan.interactor.presenter;

import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.model.ram.DataManager;
import com.zack.enderplan.domain.view.HomeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HomePresenter implements Presenter<HomeView> {

    private static final String LOG_TAG = "HomePresenter";

    private HomeView mHomeView;
    private DataManager mDataManager;
    private long lastBackKeyPressedTime;

    public HomePresenter(HomeView homeView) {
        attachView(homeView);
        mDataManager = DataManager.getInstance();
        mDataManager.initDataStruct();
    }

    @Override
    public void attachView(HomeView view) {
        mHomeView = view;
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView() {
        mHomeView = null;
        EventBus.getDefault().unregister(this);
    }

    public void setInitialView() {
        mHomeView.showInitialView(getUcPlanCount());
    }

    public void notifyPlanCreated() {
        //更新view
        mHomeView.onUcPlanCountUpdated(getUcPlanCount());
        //通过EventBus通知刷新适配器
        EventBus.getDefault().post(new PlanCreatedEvent());
        //对view层回调
        mHomeView.onPlanCreated(mDataManager.getPlan(0).getContent());
    }

    public void notifyPlanDetailChanged(int position, String planCode, boolean isTypeOfPlanChanged,
                                        boolean isPlanStatusChanged) {

        //通知AllPlans、AllTypes、TypeDetail更新
        EventBus.getDefault().post(new PlanDetailChangedEvent(position, planCode, isTypeOfPlanChanged, isPlanStatusChanged, -1));

        if (isPlanStatusChanged) {
            //如果计划完成情况改变，需要更新drawer上的header中的内容
            mHomeView.onUcPlanCountUpdated(getUcPlanCount());
        }
    }

    //通过PlanDetailActivity删除时（不能撤销）
    public void notifyPlanDeleted(int position, String planCode, String content, boolean isCompleted) {

        //通知AllPlans、AllTypes、TypeDetail更新
        EventBus.getDefault().post(new PlanDeletedEvent(position, planCode, isCompleted));

        if (!isCompleted) {
            //需要更新drawer上的未完成计划数量，因为刚刚删除了一个未完成的计划
            mHomeView.onUcPlanCountUpdated(getUcPlanCount());
        }

        //show SnackBar
        mHomeView.onPlanDeleted(content);
    }

    public void notifyBackPressed(boolean isDrawerOpen, boolean isOnRootFragment) {
        long currentTime = System.currentTimeMillis();
        if (isDrawerOpen) {
            mHomeView.onCloseDrawer();
        } else if (!isOnRootFragment || currentTime - lastBackKeyPressedTime < 1500) {
            //不是在根Fragment（可以直接退出的Fragment）上，或者连续点击间隔在1.5s以内，执行原back键操作
            mHomeView.onPressBackKey();
        } else {
            //否则更新上次点击back键的时间，并显示一个toast
            lastBackKeyPressedTime = currentTime;
            mHomeView.onShowDoubleClickToast();
        }
    }

    private String getUcPlanCount() {
        return String.valueOf(mDataManager.getUcPlanCount());
    }

    @Subscribe
    public void onUcPlanCountChanged(UcPlanCountChangedEvent event) {
        //当未完成计划数量改变的事件到来时，更新侧栏上显示的未完成计划数量
        mHomeView.onUcPlanCountUpdated(getUcPlanCount());
    }
}
