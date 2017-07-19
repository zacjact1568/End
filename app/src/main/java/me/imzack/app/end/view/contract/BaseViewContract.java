package me.imzack.app.end.view.contract;

import android.support.annotation.StringRes;

public interface BaseViewContract {

    void showToast(@StringRes int msgResId);

    void exit();
}
