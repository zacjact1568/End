package com.zack.enderplan.domain.view;

import android.support.annotation.StringRes;

public interface ReminderView {

    void showInitialView(String contentStr, int titleBgColorInt);

    void showToast(@StringRes int msgResId);

    void exitReminder();
}
