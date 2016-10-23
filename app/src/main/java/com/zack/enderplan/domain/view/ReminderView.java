package com.zack.enderplan.domain.view;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public interface ReminderView {

    void showInitialView(String content, @ColorInt int headerColorInt, @DrawableRes int typeMarkPtnResId);

    void showToast(@StringRes int msgResId);

    void exitReminder();
}
