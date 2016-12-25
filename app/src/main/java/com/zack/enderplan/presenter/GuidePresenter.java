package com.zack.enderplan.presenter;

import com.zack.enderplan.R;
import com.zack.enderplan.view.contract.GuideViewContract;
import com.zack.enderplan.event.GuideEndedEvent;
import com.zack.enderplan.model.preference.PreferenceHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class GuidePresenter extends BasePresenter {

    private GuideViewContract mGuideViewContract;
    private EventBus mEventBus;
    private long mLastBackKeyPressedTime;

    @Inject
    public GuidePresenter(GuideViewContract guideViewContract, EventBus eventBus) {
        mGuideViewContract = guideViewContract;
        mEventBus = eventBus;
    }

    @Override
    public void attach() {
        mEventBus.register(this);
        mGuideViewContract.showInitialView();
    }

    @Override
    public void detach() {
        mGuideViewContract = null;
        mEventBus.unregister(this);
    }

    public void notifyBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastBackKeyPressedTime < 1500) {
            mGuideViewContract.exit();
            //通知HomePresenter直接结束其对应的activity
            mEventBus.post(new GuideEndedEvent(getPresenterName(), false));
        } else {
            mLastBackKeyPressedTime = currentTime;
            mGuideViewContract.showToast(R.string.toast_double_click_exit);
        }
    }

    @Subscribe
    public void onGuideEnded(GuideEndedEvent event) {
        if (!event.getEventSource().equals(getPresenterName())) {
            if (event.isEndNormally()) {
                //正常结束，不再显示向导
                PreferenceHelper.getInstance().setPref(PreferenceHelper.KEY_PREF_NEED_GUIDE, false);
            }
            mGuideViewContract.exit();
        }
    }
}
