package com.zack.enderplan.domain.view;

import android.support.annotation.StringRes;

public interface HomeView {

    void showInitialView(String ucPlanCount);

    void changeUcPlanCount(String ucPlanCount);

    void onCloseDrawer();

    void changeFabVisibility(boolean isVisible);

    void showFragment(String tag);

    void onPressBackKey();

    void enterActivity(String tag);

    void exitHome();

    void showToast(@StringRes int msgResId);
}
