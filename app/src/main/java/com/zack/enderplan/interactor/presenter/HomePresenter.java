package com.zack.enderplan.interactor.presenter;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.GuideEndedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.preference.PreferenceHelper;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.domain.view.HomeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HomePresenter extends BasePresenter implements Presenter<HomeView> {

    private HomeView mHomeView;
    private DataManager mDataManager;
    private PreferenceHelper mPreferenceHelper;
    private EventBus mEventBus;
    private int mLastListScrollingVariation;
    private long lastBackKeyPressedTime;

    public HomePresenter(HomeView homeView) {
        mEventBus = EventBus.getDefault();
        attachView(homeView);
        mDataManager = DataManager.getInstance();
        mPreferenceHelper = PreferenceHelper.getInstance();
    }

    @Override
    public void attachView(HomeView view) {
        mHomeView = view;
        mEventBus.register(this);
    }

    @Override
    public void detachView() {
        mHomeView = null;
        mEventBus.unregister(this);
    }

    public void setInitialView() {
        mHomeView.showInitialView(getUcPlanCount());
    }

    public void notifyStartingUpCompleted() {
        if (mPreferenceHelper.getBooleanPref(PreferenceHelper.KEY_PREF_NEED_GUIDE)) {
            mHomeView.enterActivity(Constant.GUIDE);
        }
    }

    public void notifyListScrolled(int variation) {
        if (variation != 0 && mLastListScrollingVariation > 0 == variation < 0) {
            //滑动经过临界点
            mHomeView.changeFabVisibility(variation < 0);
            mLastListScrollingVariation = variation;
        }
    }

    public void notifyShowingFragment(String tag, boolean isShowing) {
        if (isShowing) return;
        int titleResId, fabResId;
        switch (tag) {
            case Constant.MY_PLANS:
                titleResId = R.string.title_fragment_my_plans;
                fabResId = R.drawable.ic_add_white_24dp;
                break;
            case Constant.ALL_TYPES:
                titleResId = R.string.title_fragment_all_types;
                fabResId = R.drawable.ic_playlist_add_white_24dp;
                break;
            default:
                throw new IllegalArgumentException("The argument tag cannot be " + tag);
        }
        mHomeView.showFragment(tag, titleResId, fabResId);
    }

    public void notifyBackPressed(boolean isDrawerOpen, boolean isOnRootFragment) {
        long currentTime = System.currentTimeMillis();
        if (isDrawerOpen) {
            mHomeView.onCloseDrawer();
        } else if (!isOnRootFragment) {
            //不是在根Fragment（可以直接退出的Fragment）上，回到根Fragment
            mHomeView.showFragment(Constant.MY_PLANS, R.string.title_fragment_my_plans, R.drawable.ic_add_white_24dp);
        } else if (currentTime - lastBackKeyPressedTime < 1500) {
            //连续点击间隔在1.5s以内，执行back键操作
            mHomeView.onPressBackKey();
        } else {
            //否则更新上次点击back键的时间，并显示一个toast
            lastBackKeyPressedTime = currentTime;
            mHomeView.showToast(R.string.toast_double_click_exit);
        }
    }

    private String getUcPlanCount() {
        return String.valueOf(mDataManager.getUcPlanCount());
    }

    @Subscribe
    public void onDataLoaded(DataLoadedEvent event) {
        mHomeView.onUcPlanCountUpdated(getUcPlanCount());
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        if (!mDataManager.getPlan(event.getPosition()).isCompleted()) {
            //若创建的是一个未完成的计划，需要更新侧边栏
            mHomeView.onUcPlanCountUpdated(getUcPlanCount());
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        if (!event.getDeletedPlan().isCompleted()) {
            //若删除的是一个未完成的计划，需要更新侧边栏
            mHomeView.onUcPlanCountUpdated(getUcPlanCount());
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        if (event.getChangedField() == PlanDetailChangedEvent.FIELD_PLAN_STATUS) {
            mHomeView.onUcPlanCountUpdated(getUcPlanCount());
        }
    }

    @Subscribe
    public void onGuideEnded(GuideEndedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        if (!event.isEndNormally()) {
            mHomeView.exitHome();
        }
    }
}
