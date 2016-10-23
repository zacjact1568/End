package com.zack.enderplan.domain.view;

import android.support.annotation.StringRes;

public interface EditTypeView {

    void showInitialView(int typeMarkColorInt, String firstChar, String typeName, String typeMarkColorName);

    void showTypeNameEditorDialog(String originalEditorText);

    void onTypeNameChanged(String typeName, String firstChar);

    void onTypeMarkColorChanged(int colorInt, String colorName);

    void showTypeMarkColorPickerDialog(String defaultColor);

    void showToast(@StringRes int msgResId);
}
