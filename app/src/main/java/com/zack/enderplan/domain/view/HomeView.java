package com.zack.enderplan.domain.view;

import android.support.annotation.StringRes;
import android.view.View;

public interface HomeView {

    void showInitialView(String ucPlanCount);

    void onUcPlanCountUpdated(String newUcPlanCount);

    void onCloseDrawer();

    void onPressBackKey();

    void showGuide();

    void exitHome();

    void showToast(@StringRes int msgResId);

    void showSnackbar(String msg);

    void showSnackbar(String msg, @StringRes int actionResId, View.OnClickListener actionListener);
}
