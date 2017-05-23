package com.zack.enderplan.view.contract;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.zack.enderplan.model.bean.FormattedType;

public interface TypeCreationViewContract extends BaseViewContract {

    void showInitialView(FormattedType formattedType);

    void onTypeNameChanged(String typeName, String firstChar);

    void onTypeMarkColorChanged(int colorInt, String colorName);

    void onTypeMarkPatternChanged(boolean hasPattern, @DrawableRes int patternResId, String patternName);

    void showTypeNameEditorDialog(String defaultName);

    void showTypeMarkColorPickerDialog(String defaultColor);

    void showTypeMarkPatternPickerDialog(String defaultPattern);
}
