package com.zack.enderplan.interactor.presenter;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.view.GuideView;
import com.zack.enderplan.event.GuidePageTurnedEvent;
import com.zack.enderplan.model.preference.PreferenceHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class GuidePresenter implements Presenter<GuideView> {

    private GuideView mGuideView;
    private long lastBackKeyPressedTime;

    public GuidePresenter(GuideView guideView) {
        attachView(guideView);
    }

    @Override
    public void attachView(GuideView view) {
        mGuideView = view;
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView() {
        mGuideView = null;
        EventBus.getDefault().unregister(this);
    }

    public void setInitialView() {
        mGuideView.showInitialView();
    }

    public void notifyBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackKeyPressedTime < 1500) {
            mGuideView.navigateBack();
        } else {
            lastBackKeyPressedTime = currentTime;
            mGuideView.showToast(R.string.toast_double_click_exit);
        }
    }

    @Subscribe
    public void onGuidePageTurned(GuidePageTurnedEvent event) {
        switch (event.getPage()) {
            case GuidePageTurnedEvent.PAGE_WELCOME:
                break;
            case GuidePageTurnedEvent.PAGE_FIRST_PLAN:
                PreferenceHelper.getInstance().setPref(PreferenceHelper.KEY_PREF_NEED_WELCOME, false);
                mGuideView.endGuide();
                break;
        }
    }
}
