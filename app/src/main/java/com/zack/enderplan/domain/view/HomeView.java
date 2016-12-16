package com.zack.enderplan.domain.view;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public interface HomeView {

    void showInitialView(String ucPlanCount);

    void onUcPlanCountUpdated(String newUcPlanCount);

    void onCloseDrawer();

    void changeFabVisibility(boolean isVisible);

    void showFragment(String tag, @StringRes int titleResId, @DrawableRes int fabResId);

    void onPressBackKey();

    void enterActivity(String tag);

    void exitHome();

    void showToast(@StringRes int msgResId);
}
