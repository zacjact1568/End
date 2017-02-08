package com.zack.enderplan.presenter;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.GuideEndedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.preference.PreferenceHelper;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.contract.HomeViewContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class HomePresenter extends BasePresenter {

    private HomeViewContract mHomeViewContract;
    private DataManager mDataManager;
    private PreferenceHelper mPreferenceHelper;
    private EventBus mEventBus;
    private int mLastListScrollingVariation;
    private long mLastBackKeyPressedTime;

    @Inject
    HomePresenter(HomeViewContract homeViewContract, DataManager dataManager, PreferenceHelper preferenceHelper, EventBus eventBus) {
        mHomeViewContract = homeViewContract;
        mDataManager = dataManager;
        mPreferenceHelper = preferenceHelper;
        mEventBus = eventBus;
    }

    @Override
    public void attach() {
        mEventBus.register(this);
        mHomeViewContract.showInitialView(getUcPlanCount());
    }

    @Override
    public void detach() {
        mHomeViewContract = null;
        mEventBus.unregister(this);
    }

    public void notifyStartingUpCompleted() {
        if (mPreferenceHelper.getNeedGuideValue()) {
            mHomeViewContract.enterActivity(Constant.GUIDE);
        }
    }

    public void notifyUcPlanCountTextClicked() {
        mHomeViewContract.closeDrawer();
        mHomeViewContract.showToast(getUcPlanCountStr());
    }

    public void notifyListScrolled(int variation) {
        if (variation != 0 && mLastListScrollingVariation > 0 == variation < 0) {
            //滑动经过临界点
            mHomeViewContract.changeFabVisibility(variation < 0);
            mLastListScrollingVariation = variation;
        }
    }

    public void notifyBackPressed(boolean isDrawerOpen, boolean isOnRootFragment) {
        long currentTime = System.currentTimeMillis();
        if (isDrawerOpen) {
            mHomeViewContract.closeDrawer();
        } else if (!isOnRootFragment) {
            //不是在根Fragment（可以直接退出的Fragment）上，回到根Fragment
            mHomeViewContract.showFragment(Constant.MY_PLANS);
        } else if (currentTime - mLastBackKeyPressedTime < 1500) {
            //连续点击间隔在1.5s以内，执行back键操作
            mHomeViewContract.onPressBackKey();
        } else {
            //否则更新上次点击back键的时间，并显示一个toast
            mLastBackKeyPressedTime = currentTime;
            mHomeViewContract.showToast(R.string.toast_double_click_exit);
        }
    }

    private String getUcPlanCount() {
        return String.valueOf(mDataManager.getUcPlanCount());
    }

    private String getUcPlanCountStr() {
        int count = mDataManager.getUcPlanCount();
        switch (count) {
            case 0:
                return ResourceUtil.getString(R.string.toast_uc_plan_count_none);
            case 1:
                return ResourceUtil.getString(R.string.toast_uc_plan_count_one);
            default:
                return String.format(ResourceUtil.getString(R.string.toast_uc_plan_count_multi_format), count);
        }
    }

    @Subscribe
    public void onDataLoaded(DataLoadedEvent event) {
        mHomeViewContract.changeUcPlanCount(getUcPlanCount());
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        if (!mDataManager.getPlan(event.getPosition()).isCompleted()) {
            //若创建的是一个未完成的计划，需要更新侧边栏
            mHomeViewContract.changeUcPlanCount(getUcPlanCount());
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        if (!event.getDeletedPlan().isCompleted()) {
            //若删除的是一个未完成的计划，需要更新侧边栏
            mHomeViewContract.changeUcPlanCount(getUcPlanCount());
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        if (event.getChangedField() == PlanDetailChangedEvent.FIELD_PLAN_STATUS) {
            mHomeViewContract.changeUcPlanCount(getUcPlanCount());
        }
    }
}
