package me.imzack.app.ender.view.contract;

import android.support.annotation.StringRes;

public interface BaseViewContract {

    void showToast(@StringRes int msgResId);

    void exit();
}
