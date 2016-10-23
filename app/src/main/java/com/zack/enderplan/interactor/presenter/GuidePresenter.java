package com.zack.enderplan.interactor.presenter;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.view.GuideView;
import com.zack.enderplan.event.GuideEndedEvent;
import com.zack.enderplan.model.preference.PreferenceHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class GuidePresenter extends BasePresenter implements Presenter<GuideView> {

    private GuideView mGuideView;
    private EventBus mEventBus;
    private long lastBackKeyPressedTime;

    public GuidePresenter(GuideView guideView) {
        mEventBus = EventBus.getDefault();
        attachView(guideView);
    }

    @Override
    public void attachView(GuideView view) {
        mGuideView = view;
        mEventBus.register(this);
    }

    @Override
    public void detachView() {
        mGuideView = null;
        mEventBus.unregister(this);
    }

    public void setInitialView() {
        mGuideView.showInitialView();
    }

    public void notifyBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackKeyPressedTime < 1500) {
            mGuideView.endGuide();
            //通知HomePresenter直接结束其对应的activity
            mEventBus.post(new GuideEndedEvent(getPresenterName(), false));
        } else {
            lastBackKeyPressedTime = currentTime;
            mGuideView.showToast(R.string.toast_double_click_exit);
        }
    }

    @Subscribe
    public void onGuideEnded(GuideEndedEvent event) {
        if (!event.getEventSource().equals(getPresenterName())) {
            if (event.isEndNormally()) {
                //正常结束，不再显示向导
                PreferenceHelper.getInstance().setPref(PreferenceHelper.KEY_PREF_NEED_GUIDE, false);
            }
            mGuideView.endGuide();
        }
    }
}
