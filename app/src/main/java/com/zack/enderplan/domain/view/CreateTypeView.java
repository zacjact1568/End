package com.zack.enderplan.domain.view;

import android.support.annotation.StringRes;

public interface CreateTypeView {

    void showInitialView(int typeMarkColorInt, String firstChar, String typeName, String typeMarkColorName);

    void onTypeMarkColorChanged(int colorInt, String colorName);

    void onTypeNameChanged(String typeName, String firstChar, boolean isValid);

    void showTypeMarkColorPickerDialog(String defaultColor);

    void playShakeAnimation(String tag);

    void showToast(@StringRes int msgResId);

    void exitCreateType();
}
