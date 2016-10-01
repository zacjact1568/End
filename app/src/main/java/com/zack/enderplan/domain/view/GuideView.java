package com.zack.enderplan.domain.view;

import android.support.annotation.StringRes;

public interface GuideView {

    void showInitialView();

    void showToast(@StringRes int msgResId);

    void endGuide();
}
