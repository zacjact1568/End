package com.zack.enderplan.presenter;

import android.content.SharedPreferences;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.preference.PreferenceHelper;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.contract.HomeViewContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class HomePresenter extends BasePresenter implements SharedPreferences.OnSharedPreferenceChangeListener {

    private HomeViewContract mHomeViewContract;
    private DataManager mDataManager;
    private PreferenceHelper mPreferenceHelper;
    private EventBus mEventBus;
    private int[] mPlanCountTextSizes;
    private int mLastListScrollingVariation;
    private long mLastBackKeyPressedTime;

    @Inject
    HomePresenter(HomeViewContract homeViewContract, DataManager dataManager, PreferenceHelper preferenceHelper, EventBus eventBus) {
        mHomeViewContract = homeViewContract;
        mDataManager = dataManager;
        mPreferenceHelper = preferenceHelper;
        mEventBus = eventBus;

        //setTextSize传入的就是sp
        mPlanCountTextSizes = new int[]{52, 44, 32};
    }

    @Override
    public void attach() {
        mEventBus.register(this);
        mPreferenceHelper.registerOnChangeListener(this);
        String planCount = getPlanCount();
        mHomeViewContract.showInitialView(planCount, getPlanCountTextSize(planCount), getPlanCountDscpt());
    }

    @Override
    public void detach() {
        mHomeViewContract = null;
        mPreferenceHelper.unregisterOnChangeListener(this);
        mEventBus.unregister(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceHelper.KEY_PREF_DRAWER_HEADER_DISPLAY)) {
            String planCount = getPlanCount();
            mHomeViewContract.changeDrawerHeaderDisplay(planCount, getPlanCountTextSize(planCount), getPlanCountDscpt());
        }
    }

    public void notifyStartingUpCompleted() {
        if (mPreferenceHelper.getNeedGuideValue()) {
            mHomeViewContract.enterActivity(Constant.GUIDE);
        }
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

    private String getPlanCount() {
        int planCount;
        switch (mPreferenceHelper.getDrawerHeaderDisplayValue()) {
            case PreferenceHelper.VALUE_PREF_DHD_UPC:
                planCount = mDataManager.getUcPlanCount();
                break;
            case PreferenceHelper.VALUE_PREF_DHD_PC:
                planCount = mDataManager.getPlanCount();
                break;
            case PreferenceHelper.VALUE_PREF_DHD_TUPC:
                planCount = mDataManager.getTodayUcPlanCount();
                break;
            default:
                return null;
        }
        return planCount > 99 ? "99+" : String.valueOf(planCount);
    }

    private String getPlanCountDscpt() {
        int dscptResId;
        switch (mPreferenceHelper.getDrawerHeaderDisplayValue()) {
            case PreferenceHelper.VALUE_PREF_DHD_UPC:
                dscptResId = R.string.text_uc_plan_count;
                break;
            case PreferenceHelper.VALUE_PREF_DHD_PC:
                dscptResId = R.string.text_plan_count;
                break;
            case PreferenceHelper.VALUE_PREF_DHD_TUPC:
                dscptResId = R.string.text_today_uc_plan_count;
                break;
            default:
                return null;
        }
        return ResourceUtil.getString(dscptResId);
    }

    private int getPlanCountTextSize(String planCount) {
        int textSize;
        switch (planCount.length()) {
            case 1:
                textSize = mPlanCountTextSizes[0];
                break;
            case 2:
                textSize = mPlanCountTextSizes[1];
                break;
            default:
                textSize = mPlanCountTextSizes[2];
                break;
        }
        return textSize;
    }

    private void changePlanCount() {
        String planCount = getPlanCount();
        mHomeViewContract.changePlanCount(planCount, getPlanCountTextSize(planCount));
    }

    @Subscribe
    public void onDataLoaded(DataLoadedEvent event) {
        changePlanCount();
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        //由于判断是否需要更新的逻辑略麻烦，简单起见，就不进行判断，直接更新了
        changePlanCount();
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        changePlanCount();
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        switch (event.getChangedField()) {
            case PlanDetailChangedEvent.FIELD_PLAN_STATUS:
            case PlanDetailChangedEvent.FIELD_DEADLINE:
                changePlanCount();
                break;
        }
    }
}
